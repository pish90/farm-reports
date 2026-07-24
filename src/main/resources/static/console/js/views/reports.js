import { api } from '../api.js';
import { canSeeAllFarms, getSession } from '../auth.js';
import { escapeHtml, monthName, openModal, showToast } from '../util.js';
import { navigate } from '../router.js';

function statusBadge(status) {
  const cls = status === 'SUBMITTED' ? 'submitted' : 'draft';
  return `<span class="badge ${cls}">${status}</span>`;
}

export async function render(container) {
  const farms = canSeeAllFarms() ? await api.get('/admin/farms') : [];
  const now = new Date();

  container.innerHTML = `
    <div class="section-title">
      <h2 style="margin:0">Reports</h2>
      <button id="new-report">New report</button>
    </div>
    <div class="card">
      <div class="toolbar">
        ${canSeeAllFarms() ? `
          <select id="f-farm">
            <option value="">All farms</option>
            ${farms.map((f) => `<option value="${f.farmId}">${escapeHtml(f.farmName)}</option>`).join('')}
          </select>` : ''}
        <select id="f-year">
          <option value="">All years</option>
          ${Array.from({ length: 6 }, (_, i) => now.getFullYear() - i)
            .map((y) => `<option value="${y}" ${y === now.getFullYear() ? 'selected' : ''}>${y}</option>`).join('')}
        </select>
        <select id="f-month">
          <option value="">All months</option>
          ${Array.from({ length: 12 }, (_, i) => i + 1)
            .map((m) => `<option value="${m}">${monthName(m)}</option>`).join('')}
        </select>
        <select id="f-status">
          <option value="">Any status</option>
          <option value="DRAFT">Draft</option>
          <option value="SUBMITTED">Submitted</option>
        </select>
        <button class="secondary" id="apply-filters">Filter</button>
      </div>
      <div class="table-scroll">
        <table>
          <thead><tr><th>Farm</th><th>Period</th><th>Status</th><th>Submitted</th><th></th></tr></thead>
          <tbody id="report-rows"><tr><td colspan="5" class="empty-state">Loading…</td></tr></tbody>
        </table>
      </div>
    </div>
  `;

  async function loadReports() {
    const tbody = container.querySelector('#report-rows');
    tbody.innerHTML = `<tr><td colspan="5" class="empty-state">Loading…</td></tr>`;
    const params = new URLSearchParams();
    const farmId = container.querySelector('#f-farm')?.value;
    const year = container.querySelector('#f-year').value;
    const month = container.querySelector('#f-month').value;
    const status = container.querySelector('#f-status').value;
    if (farmId) params.set('farmId', farmId);
    if (year) params.set('year', year);
    if (month) params.set('month', month);
    if (status) params.set('status', status);

    const reports = await api.get(`/admin/reports?${params.toString()}`);
    tbody.innerHTML = reports.map((r) => `
      <tr>
        <td>${escapeHtml(farms.find((f) => f.farmId === r.farmId)?.farmName || `Farm #${r.farmId}`)}</td>
        <td>${monthName(r.month)} ${r.year}</td>
        <td>${statusBadge(r.status)}</td>
        <td>${r.submittedAt ? r.submittedAt.replace('T', ' ').slice(0, 16) : '—'}</td>
        <td><button class="secondary" data-open="${r.id}" data-farm="${r.farmId}">Open</button></td>
      </tr>
    `).join('') || `<tr><td colspan="5" class="empty-state">No reports match these filters</td></tr>`;

    tbody.querySelectorAll('[data-open]').forEach((btn) => {
      btn.addEventListener('click', () => navigate(`/reports/detail?id=${btn.dataset.open}&farmId=${btn.dataset.farm}`));
    });
  }

  container.querySelector('#apply-filters').addEventListener('click', loadReports);
  container.querySelector('#new-report').addEventListener('click', () => openNewReportModal(farms, loadReports));

  await loadReports();
}

function openNewReportModal(farms, onCreated) {
  const now = new Date();
  const { modal, close } = openModal(`
    <h3>New report</h3>
    <form id="new-report-form">
      <div class="form-grid">
        ${farms.length ? `
          <div class="span-2"><label>Farm</label>
            <select name="farmId" required>${farms.map((f) => `<option value="${f.farmId}">${escapeHtml(f.farmName)}</option>`).join('')}</select>
          </div>` : ''}
        <div><label>Year</label><input type="number" name="year" value="${now.getFullYear()}" required></div>
        <div><label>Month</label><input type="number" name="month" min="1" max="12" value="${now.getMonth() + 1}" required></div>
      </div>
      <div class="modal-actions">
        <button type="button" class="secondary" id="cancel-btn">Cancel</button>
        <button type="submit">Create</button>
      </div>
    </form>
  `);
  modal.querySelector('#cancel-btn').addEventListener('click', close);
  modal.querySelector('#new-report-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const farmId = fd.get('farmId') || getSession().farmId;
    const year = fd.get('year');
    const month = fd.get('month');
    try {
      const report = await api.post(`/admin/farms/${farmId}/report?year=${year}&month=${month}`);
      showToast('Report created', 'success');
      close();
      navigate(`/reports/detail?id=${report.id}&farmId=${farmId}`);
      onCreated();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });
}
