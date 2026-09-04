import { api, apiDownload } from '../api.js';
import { isAdmin } from '../auth.js';
import { escapeHtml, formatMoney, monthName, daysInMonth, showToast } from '../util.js';
import { navigate } from '../router.js';

const ATTENDANCE_STATUSES = ['', 'P', 'A', 'WA', 'H', 'SL', 'AL'];
const PRESENT_STATUSES = new Set(['P', 'WA']);

const TABS = [
  { key: 'attendance', label: 'Attendance' },
  { key: 'livestock', label: 'Livestock' },
  { key: 'milk', label: 'Milk' },
  { key: 'expenses', label: 'Expenses' },
  { key: 'casual', label: 'Casual (legacy)' },
];

export async function render(container, params) {
  const reportId = params.id;
  const farmId = params.farmId;
  container.innerHTML = `<div class="empty-state">Loading report…</div>`;

  const report = await api.get(`/admin/reports/${reportId}`);
  const state = { farmId: Number(farmId), report, activeTab: 'attendance' };

  container.innerHTML = `
    <div class="section-title">
      <div>
        <button class="secondary" id="back-btn">← Back to reports</button>
        <h2 style="display:inline-block;margin-left:0.75rem">${monthName(report.month)} ${report.year}</h2>
        <span class="badge ${report.status === 'SUBMITTED' ? 'submitted' : 'draft'}" style="margin-left:0.5rem">${report.status}</span>
      </div>
      <div class="row-actions">
        <button class="secondary" id="export-btn">Export Excel</button>
        ${report.status === 'DRAFT' ? '<button id="submit-btn">Submit report</button>' : ''}
        ${report.status === 'SUBMITTED' && isAdmin() ? '<button class="secondary" id="reopen-btn">Reopen</button>' : ''}
      </div>
    </div>
    <div class="tabs">${TABS.map((t) => `<div class="tab" data-tab="${t.key}">${t.label}</div>`).join('')}</div>
    <div id="tab-content" class="card"></div>
  `;

  container.querySelector('#back-btn').addEventListener('click', () => navigate('/reports'));
  container.querySelector('#export-btn').addEventListener('click', () =>
    apiDownload(`/reports/${reportId}/export`, `farm-report-${report.year}-${String(report.month).padStart(2, '0')}.xlsx`)
      .catch((err) => showToast(err.message, 'error')));
  container.querySelector('#submit-btn')?.addEventListener('click', async () => {
    if (!confirm('Submit this report? It will become read-only.')) return;
    try {
      await api.post(`/admin/reports/${reportId}/submit?farmId=${state.farmId}`);
      showToast('Report submitted', 'success');
      render(container, params);
    } catch (err) { showToast(err.message, 'error'); }
  });
  container.querySelector('#reopen-btn')?.addEventListener('click', async () => {
    if (!confirm('Reopen this report for editing?')) return;
    try {
      await api.post(`/admin/reports/${reportId}/reopen`);
      showToast('Report reopened', 'success');
      render(container, params);
    } catch (err) { showToast(err.message, 'error'); }
  });

  const tabContent = container.querySelector('#tab-content');
  function selectTab(key) {
    state.activeTab = key;
    container.querySelectorAll('.tab').forEach((t) => t.classList.toggle('active', t.dataset.tab === key));
    renderTab(tabContent, state, () => render(container, params));
  }
  container.querySelectorAll('.tab').forEach((t) => t.addEventListener('click', () => selectTab(t.dataset.tab)));
  selectTab('attendance');
}

async function renderTab(container, state, reload) {
  container.innerHTML = `<div class="empty-state">Loading…</div>`;
  const locked = state.report.status === 'SUBMITTED';
  try {
    switch (state.activeTab) {
      case 'attendance': return await renderAttendance(container, state, reload, locked);
      case 'livestock': return await renderLivestock(container, state, reload, locked);
      case 'milk': return renderMilk(container, state, reload, locked);
      case 'expenses': return await renderExpenses(container, state, reload, locked);
      case 'casual': return renderCasual(container, state);
    }
  } catch (err) {
    container.innerHTML = `<div class="empty-state">${err.message}</div>`;
  }
}

// ── Attendance ──────────────────────────────────────────────────────────────

async function renderAttendance(container, state, reload, locked) {
  const workers = await api.get(`/farms/${state.farmId}/employees?isSalaried=true`);
  const days = daysInMonth(state.report.year, state.report.month);
  const byWorkerDay = new Map();
  for (const a of state.report.attendance || []) byWorkerDay.set(`${a.workerId}-${a.dayOfMonth}`, a.status || (a.present ? 'P' : 'A'));

  const dayHeaders = Array.from({ length: days }, (_, i) => `<th>${i + 1}</th>`).join('');
  const rows = workers.map((w) => {
    const cells = Array.from({ length: days }, (_, i) => {
      const day = i + 1;
      const current = byWorkerDay.get(`${w.id}-${day}`) || '';
      return `<td><select data-worker="${w.id}" data-day="${day}" ${locked ? 'disabled' : ''}>
        ${ATTENDANCE_STATUSES.map((s) => `<option value="${s}" ${s === current ? 'selected' : ''}>${s || '·'}</option>`).join('')}
      </select></td>`;
    }).join('');
    return `<tr><td>${escapeHtml(w.fullName)}</td>${cells}</tr>`;
  }).join('');

  container.innerHTML = `
    <p class="text-dim">P = present · A = absent · WA = work assignment · H = holiday · SL = sick leave · AL = annual leave</p>
    <div class="table-scroll">
      <table><thead><tr><th>Worker</th>${dayHeaders}</tr></thead><tbody>${rows || `<tr><td colspan="${days + 1}" class="empty-state">No active salaried employees on this farm</td></tr>`}</tbody></table>
    </div>
    ${!locked ? '<div class="modal-actions" style="justify-content:flex-start;margin-top:1rem"><button id="save-attendance">Save attendance</button></div>' : ''}
  `;

  container.querySelector('#save-attendance')?.addEventListener('click', async () => {
    const entries = [];
    container.querySelectorAll('select[data-worker]').forEach((sel) => {
      if (!sel.value) return;
      entries.push({
        workerId: Number(sel.dataset.worker),
        dayOfMonth: Number(sel.dataset.day),
        present: PRESENT_STATUSES.has(sel.value),
        status: sel.value,
        notes: null,
      });
    });
    try {
      await api.put(`/admin/reports/${state.report.id}/attendance?farmId=${state.farmId}`, entries);
      showToast('Attendance saved', 'success');
      reload();
    } catch (err) { showToast(err.message, 'error'); }
  });
}

// ── Livestock ─────────────────────────────────────────────────────────────

async function renderLivestock(container, state, reload, locked) {
  const byCategory = await api.get(`/farms/${state.farmId}/livestock-types`);
  const existing = new Map((state.report.livestock || []).map((l) => [l.livestockTypeId, l.count]));

  const rows = Object.entries(byCategory).flatMap(([category, types]) =>
    types.map((t) => `
      <tr>
        <td>${escapeHtml(category)}</td>
        <td>${escapeHtml(t.type)}</td>
        <td><input type="number" min="0" data-type-id="${t.id}" value="${existing.get(t.id) ?? 0}" ${locked ? 'disabled' : ''}></td>
      </tr>
    `)
  ).join('');

  container.innerHTML = `
    <div class="table-scroll">
      <table><thead><tr><th>Category</th><th>Type</th><th>Count</th></tr></thead>
      <tbody>${rows || `<tr><td colspan="3" class="empty-state">No livestock types configured for this farm</td></tr>`}</tbody></table>
    </div>
    ${!locked ? '<div class="modal-actions" style="justify-content:flex-start;margin-top:1rem"><button id="save-livestock">Save livestock</button></div>' : ''}
  `;

  container.querySelector('#save-livestock')?.addEventListener('click', async () => {
    const entries = [];
    container.querySelectorAll('input[data-type-id]').forEach((input) => {
      entries.push({ livestockTypeId: Number(input.dataset.typeId), count: Number(input.value) || 0 });
    });
    try {
      await api.put(`/admin/reports/${state.report.id}/livestock?farmId=${state.farmId}`, entries);
      showToast('Livestock saved', 'success');
      reload();
    } catch (err) { showToast(err.message, 'error'); }
  });
}

// ── Milk ────────────────────────────────────────────────────────────────────

function renderMilk(container, state, reload, locked) {
  const days = daysInMonth(state.report.year, state.report.month);
  const existing = new Map((state.report.milk || []).map((m) => [m.dayOfMonth, m.litres]));

  const rows = Array.from({ length: days }, (_, i) => {
    const day = i + 1;
    return `<tr><td>${day}</td><td><input type="number" step="0.01" min="0" data-day="${day}" value="${existing.get(day) ?? ''}" ${locked ? 'disabled' : ''}></td></tr>`;
  }).join('');

  const total = [...existing.values()].reduce((sum, v) => sum + Number(v || 0), 0);

  container.innerHTML = `
    <p class="text-dim">Total recorded this month: <strong>${formatMoney(total)} L</strong></p>
    <div class="table-scroll" style="max-width:300px">
      <table><thead><tr><th>Day</th><th>Litres</th></tr></thead><tbody>${rows}</tbody></table>
    </div>
    ${!locked ? '<div class="modal-actions" style="justify-content:flex-start;margin-top:1rem"><button id="save-milk">Save milk</button></div>' : ''}
  `;

  container.querySelector('#save-milk')?.addEventListener('click', async () => {
    const entries = [];
    container.querySelectorAll('input[data-day]').forEach((input) => {
      if (input.value === '') return;
      entries.push({ dayOfMonth: Number(input.dataset.day), litres: Number(input.value) });
    });
    try {
      await api.put(`/admin/reports/${state.report.id}/milk?farmId=${state.farmId}`, entries);
      showToast('Milk production saved', 'success');
      reload();
    } catch (err) { showToast(err.message, 'error'); }
  });
}

// ── Expenses ────────────────────────────────────────────────────────────────

async function renderExpenses(container, state, reload, locked) {
  const [categories, businessUnits] = await Promise.all([
    api.get('/lookup/expense-categories'),
    api.get('/lookup/business-units'),
  ]);
  const rows = state.report.expenses || [];

  function rowHtml(e, idx) {
    const apportionmentNote = e.apportionments?.length
      ? `<span class="text-dim" title="Apportionments are preserved but not editable here">split (${e.apportionments.length})</span>` : '';
    return `
      <tr data-row="${idx}" data-apportionments='${JSON.stringify(e.apportionments || [])}'>
        <td><input type="number" data-field="entryNo" value="${e.entryNo ?? idx + 1}" style="width:60px" ${locked ? 'disabled' : ''}></td>
        <td><input type="date" data-field="date" value="${e.date || ''}" ${locked ? 'disabled' : ''}></td>
        <td><input data-field="supplierContractor" value="${escapeHtml(e.supplierContractor || '')}" ${locked ? 'disabled' : ''}></td>
        <td><input data-field="receiptNo" value="${escapeHtml(e.receiptNo || '')}" ${locked ? 'disabled' : ''}></td>
        <td><input type="number" step="0.01" data-field="cost" value="${e.cost ?? ''}" ${locked ? 'disabled' : ''}></td>
        <td><input data-field="description" value="${escapeHtml(e.description || '')}" ${locked ? 'disabled' : ''}></td>
        <td><select data-field="categoryId" ${locked ? 'disabled' : ''}>
          <option value="">—</option>
          ${categories.map((c) => `<option value="${c.id}" ${e.categoryId === c.id ? 'selected' : ''}>${escapeHtml(c.accountCode)} ${escapeHtml(c.accountName)}</option>`).join('')}
        </select></td>
        <td><select data-field="businessUnitId" ${locked ? 'disabled' : ''}>
          <option value="">—</option>
          ${businessUnits.map((b) => `<option value="${b.id}" ${e.businessUnitId === b.id ? 'selected' : ''}>${escapeHtml(b.code)}</option>`).join('')}
        </select> ${apportionmentNote}</td>
        <td>${!locked ? `<button class="danger" data-remove="${idx}">×</button>` : ''}</td>
      </tr>
    `;
  }

  function draw() {
    container.innerHTML = `
      <div class="table-scroll">
        <table>
          <thead><tr><th>#</th><th>Date</th><th>Supplier</th><th>Receipt</th><th>Cost</th><th>Description</th><th>Category</th><th>Business unit</th><th></th></tr></thead>
          <tbody id="expense-rows">${rows.map(rowHtml).join('') || `<tr><td colspan="9" class="empty-state">No expenses recorded</td></tr>`}</tbody>
        </table>
      </div>
      ${!locked ? `
        <div class="modal-actions" style="justify-content:flex-start;margin-top:1rem">
          <button class="secondary" id="add-expense-row">Add row</button>
          <button id="save-expenses">Save expenses</button>
        </div>` : ''}
    `;

    container.querySelectorAll('[data-remove]').forEach((btn) => {
      btn.addEventListener('click', () => { rows.splice(Number(btn.dataset.remove), 1); draw(); });
    });
    container.querySelector('#add-expense-row')?.addEventListener('click', () => {
      rows.push({ entryNo: rows.length + 1, date: state.report.year + '-' + String(state.report.month).padStart(2, '0') + '-01', cost: 0 });
      draw();
    });
    container.querySelector('#save-expenses')?.addEventListener('click', async () => {
      const entries = [...container.querySelectorAll('#expense-rows tr[data-row]')].map((tr) => {
        const get = (field) => tr.querySelector(`[data-field="${field}"]`)?.value || null;
        return {
          entryNo: Number(get('entryNo')) || 1,
          date: get('date'),
          supplierContractor: get('supplierContractor'),
          receiptNo: get('receiptNo'),
          cost: Number(get('cost')) || 0,
          description: get('description'),
          categoryId: get('categoryId') ? Number(get('categoryId')) : null,
          businessUnitId: get('businessUnitId') ? Number(get('businessUnitId')) : null,
          apportionments: JSON.parse(tr.dataset.apportionments || '[]').map((a) => ({
            businessUnitId: a.businessUnitId, percentage: a.percentage, amount: a.amount,
          })),
        };
      });
      try {
        await api.put(`/admin/reports/${state.report.id}/expenses?farmId=${state.farmId}`, entries);
        showToast('Expenses saved', 'success');
        reload();
      } catch (err) { showToast(err.message, 'error'); }
    });
  }

  draw();
}

// ── Casual attendance (legacy, read-only) ───────────────────────────────────

function renderCasual(container, state) {
  const rows = (state.report.casualAttendance || []).map((c) => `
    <tr>
      <td>${escapeHtml(c.casualLabourerName)}</td>
      <td>${c.dayOfMonth}</td>
      <td>${c.status || (c.present ? 'P' : 'A')}</td>
      <td>${escapeHtml(c.taskDescription || '')}</td>
      <td>${formatMoney(c.effectiveRate)}</td>
    </tr>
  `).join('');

  container.innerHTML = `
    <p class="text-dim">Historical data from the legacy casual-attendance table, kept for record purposes. Current casual labour is tracked via Work Sessions on the Casual Labour page.</p>
    <div class="table-scroll">
      <table><thead><tr><th>Labourer</th><th>Day</th><th>Status</th><th>Task</th><th>Rate</th></tr></thead>
      <tbody>${rows || '<tr><td colspan="5" class="empty-state">No legacy casual attendance for this report</td></tr>'}</tbody></table>
    </div>
  `;
}
