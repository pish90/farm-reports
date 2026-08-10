import { api } from '../api.js';
import { getSession, isAdmin, canSeeAllFarms } from '../auth.js';
import { formatMoney, formatDate, escapeHtml, debounce, openModal, showToast, monthName } from '../util.js';

let allEmployees = [];
let farmFilter = '';
let typeFilter = '';
let searchTerm = '';

export async function render(container) {
  const session = getSession();
  container.innerHTML = `<h2>Employees</h2><div class="card"><div class="empty-state">Loading…</div></div>`;

  if (isAdmin()) {
    allEmployees = await api.get('/admin/employees');
  } else if (session.farmId) {
    allEmployees = await api.get(`/farms/${session.farmId}/employees`);
  } else {
    container.innerHTML = `<h2>Employees</h2><div class="card"><div class="empty-state">Your role has no farm assigned — nothing to show.</div></div>`;
    return;
  }

  const farmOptions = canSeeAllFarms()
    ? [...new Map(allEmployees.map((e) => [e.farmId, e.farmName])).entries()]
        .map(([id, name]) => `<option value="${id}">${escapeHtml(name)}</option>`).join('')
    : '';

  container.innerHTML = `
    <div class="section-title">
      <h2 style="margin:0">Employees</h2>
      <div style="display:flex;gap:0.5rem">
        ${isAdmin() ? '<button id="import-csv" class="secondary">Import CSV</button>' : ''}
        <button id="add-employee">Add employee</button>
      </div>
    </div>
    <div class="card">
      <div class="toolbar">
        ${canSeeAllFarms() ? `<select id="farm-filter"><option value="">All farms</option>${farmOptions}</select>` : ''}
        <select id="type-filter">
          <option value="">All types</option>
          <option value="SALARIED">Salaried</option>
          <option value="CASUAL">Casual</option>
        </select>
        <input id="search" type="search" placeholder="Search name, LS number, phone…" style="min-width:220px">
        <span class="text-dim" id="result-count"></span>
      </div>
      <div class="table-scroll">
        <table id="employee-table">
          <thead>
            <tr>
              <th>LS #</th><th>Name</th>${canSeeAllFarms() ? '<th>Farm</th>' : ''}<th>Type</th>
              <th>Job title</th><th>Phone</th><th>Status</th><th></th>
            </tr>
          </thead>
          <tbody></tbody>
        </table>
      </div>
    </div>
  `;

  const tbody = container.querySelector('tbody');
  const resultCount = container.querySelector('#result-count');

  function draw() {
    const filtered = allEmployees.filter((e) => {
      if (farmFilter && String(e.farmId) !== farmFilter) return false;
      if (typeFilter && e.employmentType !== typeFilter) return false;
      if (searchTerm) {
        const hay = `${e.fullName} ${e.lsNumber} ${e.phone}`.toLowerCase();
        if (!hay.includes(searchTerm.toLowerCase())) return false;
      }
      return true;
    });
    resultCount.textContent = `${filtered.length} employee${filtered.length === 1 ? '' : 's'}`;
    tbody.innerHTML = filtered.map((e) => `
      <tr>
        <td class="mono">${escapeHtml(e.lsNumber || '—')}</td>
        <td>${escapeHtml(e.fullName)}</td>
        ${canSeeAllFarms() ? `<td>${escapeHtml(e.farmName)}</td>` : ''}
        <td>${escapeHtml(e.employmentType)}</td>
        <td>${escapeHtml(e.jobTitle || '—')}</td>
        <td>${escapeHtml(e.phone || '—')}</td>
        <td>${escapeHtml(e.status || '—')}</td>
        <td class="row-actions">
          <button class="secondary" data-view="${e.id}" data-farm="${e.farmId}">View</button>
        </td>
      </tr>
    `).join('') || `<tr><td colspan="8" class="empty-state">No employees match</td></tr>`;

    tbody.querySelectorAll('[data-view]').forEach((btn) => {
      btn.addEventListener('click', () => openEmployeeModal(
        filtered.find((e) => String(e.id) === btn.dataset.view && String(e.farmId) === btn.dataset.farm)
      ));
    });
  }

  container.querySelector('#farm-filter')?.addEventListener('change', (e) => { farmFilter = e.target.value; draw(); });
  container.querySelector('#type-filter').addEventListener('change', (e) => { typeFilter = e.target.value; draw(); });
  container.querySelector('#search').addEventListener('input', debounce((e) => { searchTerm = e.target.value; draw(); }, 200));
  container.querySelector('#add-employee').addEventListener('click', () => {
    const farmId = canSeeAllFarms() ? null : session.farmId;
    openEmployeeModal(null, farmId);
  });
  container.querySelector('#import-csv')?.addEventListener('click', openImportCsvModal);

  draw();
}

function downloadCsvTemplate() {
  const header = 'farmName,firstName,lastName,phone,employmentType,jobTitle,startDate,defaultDailyRate';
  const example = 'Matunda,Jane,Doe,0712345678,SALARIED,Herdsman,2024-01-15,';
  const csv = '﻿' + header + '\n' + example + '\n';
  const blob = new Blob([csv], { type: 'text/csv' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'employee_import_template.csv';
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

function openImportCsvModal() {
  const { modal, close } = openModal(`
    <h3>Import employees from CSV</h3>
    <p class="text-dim">
      Columns: <strong>farmName</strong>, <strong>firstName</strong>, lastName, phone,
      <strong>employmentType</strong> (SALARIED or CASUAL), jobTitle, startDate (yyyy-mm-dd), defaultDailyRate.
      Bold columns are required.
    </p>
    <p><button type="button" class="secondary" id="download-template">Download CSV template</button></p>
    <form id="csv-import-form">
      <div><label>CSV file</label><input type="file" name="csvFile" accept=".csv,text/csv" required></div>
      <div class="modal-actions">
        <button type="button" class="secondary" id="cancel-btn">Cancel</button>
        <button type="submit">Import</button>
      </div>
    </form>
    <div id="import-errors" hidden></div>
  `);

  modal.querySelector('#download-template').addEventListener('click', downloadCsvTemplate);
  modal.querySelector('#cancel-btn').addEventListener('click', close);
  modal.querySelector('#csv-import-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const file = modal.querySelector('input[name="csvFile"]').files[0];
    if (!file) { showToast('Choose a CSV file', 'error'); return; }

    const fd = new FormData();
    fd.append('file', file);
    const errorBox = modal.querySelector('#import-errors');

    try {
      const result = await api.postForm('/admin/employees/import', fd);
      if (result.success) {
        showToast(`Imported ${result.importedCount} employee(s)`, 'success');
        close();
        render(document.getElementById('view-container'));
      } else {
        errorBox.hidden = false;
        errorBox.innerHTML = `
          <p style="color:var(--danger);margin-top:1rem">
            ${result.errors.length} row(s) have errors — fix and re-upload. Nothing was imported.
          </p>
          <div class="table-scroll" style="max-height:260px">
            <table>
              <thead><tr><th>Row</th><th>Who</th><th>Problem</th></tr></thead>
              <tbody>${result.errors.map((err) => `
                <tr>
                  <td class="mono">${err.row}</td>
                  <td>${escapeHtml(err.rowSummary)}</td>
                  <td>${escapeHtml(err.message)}</td>
                </tr>
              `).join('')}</tbody>
            </table>
          </div>
        `;
        showToast(`${result.errors.length} row error(s) — see details below`, 'error');
      }
    } catch (err) {
      showToast(err.message, 'error');
    }
  });
}

async function openEmployeeModal(employee, forcedFarmId) {
  const isNew = !employee;
  const farmId = employee?.farmId ?? forcedFarmId;
  if (!farmId) { showToast('Pick a farm filter first', 'error'); return; }

  const departments = await api.get(`/farms/${farmId}/departments`).catch(() => []);
  const deptOptions = departments.map((d) =>
    `<option value="${d.id}" ${employee?.departmentId === d.id ? 'selected' : ''}>${escapeHtml(d.name)}</option>`
  ).join('');

  let summary = null;
  let ledger = null;
  const ledgerYear = new Date().getFullYear();
  if (!isNew) {
    summary = await api.get(`/farms/${farmId}/employees/${employee.id}/summary`).catch(() => null);
    if (employee.employmentType === 'SALARIED') {
      ledger = await api.get(`/farms/${farmId}/employees/${employee.id}/ledger?year=${ledgerYear}`).catch(() => null);
    }
  }

  const { modal, close } = openModal(`
    <h3>${isNew ? 'Add employee' : escapeHtml(employee.fullName)}</h3>
    ${employee?.lsNumber ? `<p class="text-dim mono" style="margin-top:-0.5rem">${employee.lsNumber}</p>` : ''}
    <form id="employee-form">
      <div class="form-grid">
        <div><label>First name</label><input name="firstName" value="${escapeHtml(employee?.firstName || '')}" required></div>
        <div><label>Last name</label><input name="lastName" value="${escapeHtml(employee?.lastName || '')}"></div>
        <div><label>Phone</label><input name="phone" value="${escapeHtml(employee?.phone || '')}"></div>
        <div><label>Employment type</label>
          <select name="employmentType" required>
            <option value="SALARIED" ${employee?.employmentType === 'SALARIED' ? 'selected' : ''}>Salaried</option>
            <option value="CASUAL" ${employee?.employmentType === 'CASUAL' ? 'selected' : ''}>Casual</option>
          </select>
        </div>
        <div><label>Job title</label><input name="jobTitle" value="${escapeHtml(employee?.jobTitle || '')}"></div>
        <div><label>Department</label><select name="departmentId"><option value="">—</option>${deptOptions}</select></div>
        <div><label>Start date</label><input type="date" name="startDate" value="${employee?.startDate || ''}"></div>
        <div><label>Date of birth</label><input type="date" name="dateOfBirth" value="${employee?.dateOfBirth || ''}"></div>
        <div><label>National ID</label><input name="nationalId" value="${escapeHtml(employee?.nationalId || '')}"></div>
        <div><label>Gender</label>
          <select name="gender">
            <option value="">—</option>
            <option value="Male" ${employee?.gender === 'Male' ? 'selected' : ''}>Male</option>
            <option value="Female" ${employee?.gender === 'Female' ? 'selected' : ''}>Female</option>
          </select>
        </div>
        <div><label>Default daily rate</label><input type="number" step="0.01" name="defaultDailyRate" value="${employee?.defaultDailyRate ?? ''}"></div>
        <div><label>Status</label>
          <select name="status">
            <option value="ACTIVE" ${(!employee || employee.status === 'ACTIVE') ? 'selected' : ''}>Active</option>
            <option value="INACTIVE" ${employee?.status === 'INACTIVE' ? 'selected' : ''}>Inactive</option>
          </select>
        </div>
      </div>
      <div class="modal-actions">
        <button type="button" class="secondary" id="cancel-btn">Cancel</button>
        <button type="submit">${isNew ? 'Create' : 'Save changes'}</button>
      </div>
    </form>
    ${ledger ? renderLedgerSection(ledger) : ''}
    ${summary ? renderSummarySection(summary) : ''}
  `);

  modal.querySelector('#cancel-btn').addEventListener('click', close);
  modal.querySelector('#employee-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const payload = {
      firstName: fd.get('firstName'),
      lastName: fd.get('lastName') || null,
      phone: fd.get('phone') || null,
      employmentType: fd.get('employmentType'),
      jobTitle: fd.get('jobTitle') || null,
      departmentId: fd.get('departmentId') ? Number(fd.get('departmentId')) : null,
      startDate: fd.get('startDate') || null,
      dateOfBirth: fd.get('dateOfBirth') || null,
      nationalId: fd.get('nationalId') || null,
      gender: fd.get('gender') || null,
      defaultDailyRate: fd.get('defaultDailyRate') || null,
      status: fd.get('status'),
    };
    try {
      if (isNew) {
        await api.post(`/farms/${farmId}/employees`, payload);
        showToast('Employee added', 'success');
      } else {
        await api.put(`/farms/${farmId}/employees/${employee.id}`, payload);
        showToast('Employee updated', 'success');
      }
      close();
      render(document.getElementById('view-container'));
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  if (summary) wireSummarySection(modal, farmId, employee.id, summary);
  if (ledger) wireLedgerSection(modal, farmId, employee.id);
}

function renderLedgerSection(ledger) {
  const now = new Date();
  const yearOptions = Array.from({ length: 6 }, (_, i) => now.getFullYear() - i)
    .map((y) => `<option value="${y}" ${y === ledger.year ? 'selected' : ''}>${y}</option>`).join('');
  return `
    <div class="section-title" style="margin-top:1.5rem">
      <h4 style="margin:0">Annual Ledger</h4>
      <select id="ledger-year">${yearOptions}</select>
    </div>
    <div id="ledger-content">${renderLedgerContent(ledger)}</div>
  `;
}

function renderLedgerContent(ledger) {
  return `
    <div class="stat-grid">
      <div class="stat-tile"><div class="label">Opening balance</div><div class="value">${formatMoney(ledger.openingBalance)}</div></div>
      <div class="stat-tile"><div class="label">Earned this year</div><div class="value">${formatMoney(ledger.totalEarned)}</div></div>
      <div class="stat-tile"><div class="label">Paid this year</div><div class="value">${formatMoney(ledger.totalPaid)}</div></div>
      <div class="stat-tile"><div class="label">Closing balance</div><div class="value">${formatMoney(ledger.closingBalance)}</div></div>
    </div>
    <div class="table-scroll">
      <table>
        <thead><tr><th>Month</th><th>Earned</th><th>Paid</th><th>Balance</th></tr></thead>
        <tbody>${ledger.months.map((m) => `
          <tr>
            <td>${monthName(m.month)}</td>
            <td>${formatMoney(m.earned)}</td>
            <td>${formatMoney(m.paid)}</td>
            <td>${formatMoney(m.balance)}</td>
          </tr>
        `).join('')}</tbody>
      </table>
    </div>
  `;
}

function wireLedgerSection(modal, farmId, employeeId) {
  modal.querySelector('#ledger-year').addEventListener('change', async (e) => {
    const year = Number(e.target.value);
    const content = modal.querySelector('#ledger-content');
    content.innerHTML = `<div class="empty-state">Loading…</div>`;
    const newLedger = await api.get(`/farms/${farmId}/employees/${employeeId}/ledger?year=${year}`).catch(() => null);
    content.innerHTML = newLedger ? renderLedgerContent(newLedger) : '<div class="empty-state">Failed to load ledger</div>';
  });
}

function renderSummarySection(summary) {
  return `
    <div class="section-title" style="margin-top:1.5rem"><h4 style="margin:0">Payments</h4></div>
    <div class="stat-grid">
      <div class="stat-tile"><div class="label">All-time earned</div><div class="value">${formatMoney(summary.allTimeEarned)}</div></div>
      <div class="stat-tile"><div class="label">All-time paid</div><div class="value">${formatMoney(summary.allTimePaid)}</div></div>
      <div class="stat-tile"><div class="label">Outstanding</div><div class="value">${formatMoney(summary.outstanding)}</div></div>
    </div>
    <div class="table-scroll">
      <table>
        <thead><tr><th>Date</th><th>Amount</th><th>Note</th><th>Paid by</th><th></th></tr></thead>
        <tbody id="payment-rows">
          ${summary.payments.map((p) => `
            <tr>
              <td>${formatDate(p.paymentDate)}</td>
              <td>${formatMoney(p.amount)}</td>
              <td>${escapeHtml(p.note || '')}</td>
              <td>${escapeHtml(p.paidBy || '')}</td>
              <td><button class="danger" data-del-payment="${p.id}">Delete</button></td>
            </tr>
          `).join('') || '<tr><td colspan="5" class="empty-state">No payments recorded</td></tr>'}
        </tbody>
      </table>
    </div>
    <form id="add-payment-form" class="form-grid" style="margin-top:0.75rem">
      <div><label>Date</label><input type="date" name="paymentDate" required></div>
      <div><label>Amount</label><input type="number" step="0.01" name="amount" required></div>
      <div class="span-2"><label>Note</label><input name="note"></div>
      <div class="span-2"><button type="submit" class="secondary">Record payment</button></div>
    </form>
  `;
}

function wireSummarySection(modal, farmId, employeeId, summary) {
  modal.querySelectorAll('[data-del-payment]').forEach((btn) => {
    btn.addEventListener('click', async () => {
      if (!confirm('Delete this payment?')) return;
      try {
        await api.delete(`/farms/${farmId}/employees/${employeeId}/payments/${btn.dataset.delPayment}`);
        showToast('Payment deleted', 'success');
        render(document.getElementById('view-container'));
        modal.closest('.modal-backdrop').remove();
      } catch (err) {
        showToast(err.message, 'error');
      }
    });
  });

  modal.querySelector('#add-payment-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    try {
      await api.post(`/farms/${farmId}/employees/${employeeId}/payments`, {
        paymentDate: fd.get('paymentDate'),
        amount: Number(fd.get('amount')),
        note: fd.get('note') || null,
      });
      showToast('Payment recorded', 'success');
      modal.closest('.modal-backdrop').remove();
      render(document.getElementById('view-container'));
    } catch (err) {
      showToast(err.message, 'error');
    }
  });
}
