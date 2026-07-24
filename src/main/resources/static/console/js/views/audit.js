import { api } from '../api.js';
import { canSeeAllFarms } from '../auth.js';
import { escapeHtml, formatDateTime } from '../util.js';

const ACTIONS = [
  'LOGIN', 'LOGIN_FAILED', 'PASSWORD_CHANGED', 'PASSWORD_RESET',
  'REPORT_CREATED', 'REPORT_SUBMITTED', 'REPORT_REOPENED',
  'ATTENDANCE_UPDATED', 'LIVESTOCK_UPDATED', 'MILK_UPDATED', 'EXPENSES_UPDATED',
  'ATTENDANCE_NOTES_UPDATED', 'LIVESTOCK_NOTES_UPDATED', 'PAYROLL_UPDATED',
  'WORKER_ADDED', 'WORKER_UPDATED', 'WORKER_DEACTIVATED',
  'DEPARTMENT_ADDED', 'DEPARTMENT_DELETED',
  'EMPLOYEE_ADDED', 'EMPLOYEE_UPDATED', 'EMPLOYEE_DEACTIVATED',
  'EMPLOYEE_PAYMENT_RECORDED', 'EMPLOYEE_PAYMENT_DELETED',
  'CASUAL_LABOURER_ADDED', 'CASUAL_LABOURER_UPDATED', 'CASUAL_LABOURER_DEACTIVATED',
  'CASUAL_ATTENDANCE_UPDATED', 'CASUAL_PAYMENT_RECORDED', 'CASUAL_PAYMENT_DELETED',
  'EXCEL_EXPORTED', 'BACKUP_CREATED',
];

let page = 0;

export async function render(container) {
  const farms = canSeeAllFarms() ? await api.get('/admin/farms') : [];
  page = 0;

  container.innerHTML = `
    <h2>Audit Log</h2>
    <div class="card">
      <div class="toolbar">
        ${canSeeAllFarms() ? `
          <select id="a-farm"><option value="">All farms</option>
            ${farms.map((f) => `<option value="${f.farmId}">${escapeHtml(f.farmName)}</option>`).join('')}</select>` : ''}
        <select id="a-action"><option value="">Any action</option>
          ${ACTIONS.map((a) => `<option value="${a}">${a}</option>`).join('')}</select>
        <input type="date" id="a-start">
        <span class="text-dim">to</span>
        <input type="date" id="a-end">
        <button class="secondary" id="a-apply">Filter</button>
      </div>
      <div class="table-scroll">
        <table>
          <thead><tr><th>Time</th><th>User</th><th>Role</th><th>Farm</th><th>Action</th><th>Entity</th><th>Description</th></tr></thead>
          <tbody id="audit-rows"><tr><td colspan="7" class="empty-state">Loading…</td></tr></tbody>
        </table>
      </div>
      <div class="pagination" id="audit-pagination"></div>
    </div>
  `;

  async function load() {
    const tbody = container.querySelector('#audit-rows');
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state">Loading…</td></tr>`;
    const params = new URLSearchParams({ page: String(page), size: '50' });
    const farmId = container.querySelector('#a-farm')?.value;
    const action = container.querySelector('#a-action').value;
    const startDate = container.querySelector('#a-start').value;
    const endDate = container.querySelector('#a-end').value;
    if (farmId) params.set('farmId', farmId);
    if (action) params.set('action', action);
    if (startDate) params.set('startDate', startDate);
    if (endDate) params.set('endDate', endDate);

    const result = await api.get(`/admin/audit-logs?${params.toString()}`);
    tbody.innerHTML = result.content.map((log) => `
      <tr>
        <td>${formatDateTime(log.timestamp)}</td>
        <td>${escapeHtml(log.userName || '—')}</td>
        <td>${escapeHtml(log.userRole || '—')}</td>
        <td>${escapeHtml(log.farmName || '—')}</td>
        <td>${escapeHtml(log.action)}</td>
        <td>${escapeHtml(log.entityType || '')} ${escapeHtml(log.entityId || '')}</td>
        <td>${escapeHtml(log.description || '')}</td>
      </tr>
    `).join('') || `<tr><td colspan="7" class="empty-state">No matching audit entries</td></tr>`;

    const pagination = container.querySelector('#audit-pagination');
    pagination.innerHTML = `
      <button class="secondary" id="prev-page" ${page <= 0 ? 'disabled' : ''}>Previous</button>
      <span>Page ${result.page + 1} of ${Math.max(result.totalPages, 1)} · ${result.totalElements} entries</span>
      <button class="secondary" id="next-page" ${page + 1 >= result.totalPages ? 'disabled' : ''}>Next</button>
    `;
    pagination.querySelector('#prev-page').addEventListener('click', () => { page--; load(); });
    pagination.querySelector('#next-page').addEventListener('click', () => { page++; load(); });
  }

  container.querySelector('#a-apply').addEventListener('click', () => { page = 0; load(); });
  await load();
}
