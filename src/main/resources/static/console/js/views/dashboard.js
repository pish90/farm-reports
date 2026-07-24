import { api } from '../api.js';
import { isAdmin, getSession } from '../auth.js';
import { formatMoney, formatDateTime, monthName, escapeHtml } from '../util.js';
import { navigate } from '../router.js';

function statusBadge(status) {
  const cls = status === 'SUBMITTED' ? 'submitted' : status === 'DRAFT' ? 'draft' : 'not-started';
  const label = status === 'NOT_STARTED' ? 'Not started' : status;
  return `<span class="badge ${cls}">${label}</span>`;
}

export async function render(container) {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth() + 1;

  container.innerHTML = `
    <h2>Dashboard</h2>
    <div class="card"><div class="empty-state">Loading farms…</div></div>
  `;

  const session = getSession();
  const employeesRequest = isAdmin()
    ? api.get('/admin/employees')
    : session.farmId ? api.get(`/farms/${session.farmId}/employees`) : Promise.resolve([]);

  const [summaries, liveStatus, employees] = await Promise.all([
    api.get('/admin/farms'),
    api.get(`/admin/live-status?year=${year}&month=${month}`),
    employeesRequest,
  ]);

  const employeesByFarm = new Map();
  for (const e of employees) {
    const bucket = employeesByFarm.get(e.farmId) || { total: 0, active: 0 };
    bucket.total++;
    if (e.status === 'ACTIVE') bucket.active++;
    employeesByFarm.set(e.farmId, bucket);
  }
  const totalActive = employees.filter((e) => e.status === 'ACTIVE').length;

  const employeeWidget = `
    <div class="stat-tile">
      <div class="label">Employees</div>
      <div class="value">${totalActive}</div>
      <div class="text-dim">active of ${employees.length} total</div>
    </div>
  `;

  const tiles = summaries.map((s) => {
    const empCount = employeesByFarm.get(s.farmId);
    return `
    <div class="stat-tile">
      <div class="label">${escapeHtml(s.farmName)}</div>
      <div class="value">${s.reportsThisYear}</div>
      <div class="text-dim">reports in ${year}</div>
      ${empCount ? `<div class="text-dim" style="margin-top:.5rem">Employees: <strong>${empCount.active}</strong> active of ${empCount.total}</div>` : ''}
      <div class="text-dim">Milk this month: <strong>${formatMoney(s.totalMilkThisMonth)}</strong> L</div>
      <div class="text-dim">Expenses this month: <strong>KES ${formatMoney(s.totalExpensesThisMonth)}</strong></div>
      <div class="text-dim">Last submitted: ${formatDateTime(s.lastSubmittedAt)}</div>
    </div>
  `;
  }).join('');

  const rows = liveStatus.map((f) => `
    <tr>
      <td>${escapeHtml(f.farmName)}</td>
      <td>${statusBadge(f.reportStatus)}</td>
      <td>${f.activeWorkers}</td>
      <td>${f.attendanceDaysRecorded}</td>
      <td>${f.livestockEntered ? 'Yes' : 'No'}</td>
      <td>${formatMoney(f.milkTotalLitres)} L</td>
      <td>${f.expenseCount}</td>
      <td>KES ${formatMoney(f.expenseTotal)}</td>
      <td>${f.reportId ? `<button class="secondary" data-open="${f.reportId}" data-farm="${f.farmId}">Open</button>` : '—'}</td>
    </tr>
  `).join('');

  container.innerHTML = `
    <h2>Dashboard</h2>
    <div class="stat-grid">${employeeWidget}${tiles}</div>
    <div class="card">
      <div class="section-title">
        <h3 style="margin:0">Live status — ${monthName(month)} ${year}</h3>
      </div>
      <div class="table-scroll">
        <table>
          <thead>
            <tr>
              <th>Farm</th><th>Report</th><th>Active workers</th><th>Attendance days</th>
              <th>Livestock</th><th>Milk</th><th>Expense rows</th><th>Expense total</th><th></th>
            </tr>
          </thead>
          <tbody>${rows || `<tr><td colspan="9" class="empty-state">No farms found</td></tr>`}</tbody>
        </table>
      </div>
    </div>
  `;

  container.querySelectorAll('[data-open]').forEach((btn) => {
    btn.addEventListener('click', () => {
      navigate(`/reports/detail?id=${btn.dataset.open}&farmId=${btn.dataset.farm}`);
    });
  });
}
