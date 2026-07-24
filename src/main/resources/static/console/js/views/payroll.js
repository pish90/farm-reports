import { api } from '../api.js';
import { canSeeAllFarms, getSession } from '../auth.js';
import { escapeHtml, formatMoney, monthName, showToast } from '../util.js';

export async function render(container) {
  const session = getSession();
  const farms = canSeeAllFarms() ? await api.get('/admin/farms') : [];
  const now = new Date();
  const defaultFarmId = canSeeAllFarms() ? farms[0]?.farmId : session.farmId;

  if (!defaultFarmId) {
    container.innerHTML = `<h2>Payroll</h2><div class="card"><div class="empty-state">Your role has no farm assigned.</div></div>`;
    return;
  }

  container.innerHTML = `
    <h2>Payroll</h2>
    <div class="card">
      <div class="toolbar">
        ${canSeeAllFarms() ? `
          <select id="p-farm">${farms.map((f) => `<option value="${f.farmId}">${escapeHtml(f.farmName)}</option>`).join('')}</select>` : ''}
        <select id="p-year">
          ${Array.from({ length: 6 }, (_, i) => now.getFullYear() - i)
            .map((y) => `<option value="${y}" ${y === now.getFullYear() ? 'selected' : ''}>${y}</option>`).join('')}
        </select>
        <select id="p-month">
          ${Array.from({ length: 12 }, (_, i) => i + 1)
            .map((m) => `<option value="${m}" ${m === now.getMonth() + 1 ? 'selected' : ''}>${monthName(m)}</option>`).join('')}
        </select>
        <button class="secondary" id="load-payroll">Load</button>
      </div>
      <div id="payroll-table"></div>
    </div>
    <div class="card">
      <h3>Annual summary</h3>
      <div id="payroll-summary"></div>
    </div>
  `;

  const farmSelect = container.querySelector('#p-farm');
  const yearSelect = container.querySelector('#p-year');
  const monthSelect = container.querySelector('#p-month');

  async function loadTable() {
    const farmId = farmSelect ? farmSelect.value : defaultFarmId;
    const year = yearSelect.value;
    const month = monthSelect.value;
    const target = container.querySelector('#payroll-table');
    target.innerHTML = `<div class="empty-state">Loading…</div>`;
    const entries = await api.get(`/reports/payroll?farmId=${farmId}&year=${year}&month=${month}`);
    drawTable(target, farmId, year, month, entries);
  }

  async function loadSummary() {
    const farmId = farmSelect ? farmSelect.value : defaultFarmId;
    const year = yearSelect.value;
    const target = container.querySelector('#payroll-summary');
    target.innerHTML = `<div class="empty-state">Loading…</div>`;
    const summary = await api.get(`/reports/payroll/summary?farmId=${farmId}&year=${year}`);
    target.innerHTML = `
      <div class="table-scroll">
        <table>
          <thead><tr><th>Month</th><th>Gross</th><th>Loans</th><th>Paid</th><th>Remaining</th></tr></thead>
          <tbody>${summary.map((s) => `
            <tr>
              <td>${monthName(s.month)}</td>
              <td>${formatMoney(s.totalGross)}</td>
              <td>${formatMoney(s.totalLoans)}</td>
              <td>${formatMoney(s.totalPaid)}</td>
              <td>${formatMoney(s.totalRemaining)}</td>
            </tr>
          `).join('') || '<tr><td colspan="5" class="empty-state">No payroll data for this year</td></tr>'}</tbody>
        </table>
      </div>
    `;
  }

  container.querySelector('#load-payroll').addEventListener('click', () => { loadTable(); loadSummary(); });
  farmSelect?.addEventListener('change', () => { loadTable(); loadSummary(); });

  await loadTable();
  await loadSummary();
}

function drawTable(target, farmId, year, month, entries) {
  const FIELDS = ['salaryRate', 'daysWorked', 'grossSalary', 'loans', 'amountPaid', 'amountRemaining'];
  target.innerHTML = `
    <div class="table-scroll">
      <table>
        <thead><tr><th>LS #</th><th>Employee</th><th>Salary rate</th><th>Days</th><th>Gross</th><th>Loans</th><th>Paid</th><th>Remaining</th><th>Notes</th></tr></thead>
        <tbody id="payroll-rows">
          ${entries.map((e, idx) => `
            <tr data-emp="${e.employeeId}">
              <td class="mono">${escapeHtml(e.lsNumber || '—')}</td>
              <td>${escapeHtml(e.employeeName)}</td>
              ${FIELDS.map((f) => `<td><input type="number" step="0.01" data-field="${f}" value="${e[f] ?? ''}"></td>`).join('')}
              <td><input data-field="notes" value="${escapeHtml(e.notes || '')}"></td>
            </tr>
          `).join('') || `<tr><td colspan="9" class="empty-state">No payroll entries for this period</td></tr>`}
        </tbody>
      </table>
    </div>
    ${entries.length ? '<div class="modal-actions" style="justify-content:flex-start;margin-top:1rem"><button id="save-payroll">Save payroll</button></div>' : ''}
  `;

  target.querySelector('#save-payroll')?.addEventListener('click', async () => {
    const rows = [...target.querySelectorAll('#payroll-rows tr[data-emp]')].map((tr) => {
      const get = (field) => tr.querySelector(`[data-field="${field}"]`)?.value;
      return {
        employeeId: Number(tr.dataset.emp),
        salaryRate: get('salaryRate') || null,
        daysWorked: get('daysWorked') ? Number(get('daysWorked')) : null,
        grossSalary: get('grossSalary') || null,
        loans: get('loans') || null,
        amountPaid: get('amountPaid') || null,
        amountRemaining: get('amountRemaining') || null,
        notes: get('notes') || null,
      };
    });
    try {
      await api.put(`/reports/payroll?farmId=${farmId}&year=${year}&month=${month}`, rows);
      showToast('Payroll saved', 'success');
    } catch (err) { showToast(err.message, 'error'); }
  });
}
