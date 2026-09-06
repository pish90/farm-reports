import { api, apiDownload } from '../api.js';
import { isAdmin } from '../auth.js';
import { monthName, showToast } from '../util.js';

export async function render(container) {
  if (!isAdmin()) {
    container.innerHTML = `<h2>Tools</h2><div class="card"><div class="empty-state">Admin access required.</div></div>`;
    return;
  }

  const now = new Date();
  container.innerHTML = `
    <h2>Tools</h2>
    <div class="card">
      <h3>Export all farms to Excel</h3>
      <p class="text-dim">Generates a combined workbook for every farm's report in the selected period.</p>
      <div class="toolbar">
        <select id="x-year">${Array.from({ length: 6 }, (_, i) => now.getFullYear() - i)
          .map((y) => `<option value="${y}" ${y === now.getFullYear() ? 'selected' : ''}>${y}</option>`).join('')}</select>
        <select id="x-month">${Array.from({ length: 12 }, (_, i) => i + 1)
          .map((m) => `<option value="${m}" ${m === now.getMonth() + 1 ? 'selected' : ''}>${monthName(m)}</option>`).join('')}</select>
        <button id="export-all">Export</button>
      </div>
    </div>
    <div class="card">
      <h3>Reset a user's password</h3>
      <form id="reset-form" class="form-grid">
        <div><label>Email</label><input type="email" name="email" required></div>
        <div><label>New password</label><input type="text" name="newPassword" minlength="6" required></div>
        <div class="span-2"><button type="submit" class="secondary">Reset password</button></div>
      </form>
    </div>
  `;

  container.querySelector('#export-all').addEventListener('click', () => {
    const year = container.querySelector('#x-year').value;
    const month = container.querySelector('#x-month').value;
    apiDownload(`/admin/export/excel?year=${year}&month=${month}`, `farm-reports-${year}-${String(month).padStart(2, '0')}.xlsx`)
      .catch((err) => showToast(err.message, 'error'));
  });

  container.querySelector('#reset-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    try {
      await api.put('/admin/users/reset-password', { email: fd.get('email'), newPassword: fd.get('newPassword') });
      showToast('Password reset', 'success');
      e.target.reset();
    } catch (err) { showToast(err.message, 'error'); }
  });
}
