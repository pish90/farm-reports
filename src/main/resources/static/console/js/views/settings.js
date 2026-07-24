import { api } from '../api.js';
import { canSeeAllFarms, getSession } from '../auth.js';
import { escapeHtml, showToast } from '../util.js';

export async function render(container) {
  const session = getSession();
  const farms = canSeeAllFarms() ? await api.get('/admin/farms') : [];
  const farmId = canSeeAllFarms() ? farms[0]?.farmId : session.farmId;

  if (!farmId) {
    container.innerHTML = `<h2>Farm Settings</h2><div class="card"><div class="empty-state">Your role has no farm assigned.</div></div>`;
    return;
  }

  container.innerHTML = `
    <div class="section-title">
      <h2 style="margin:0">Farm Settings</h2>
      ${canSeeAllFarms() ? `
        <select id="s-farm">${farms.map((f) => `<option value="${f.farmId}">${escapeHtml(f.farmName)}</option>`).join('')}</select>` : ''}
    </div>
    <div class="card">
      <div class="section-title"><h3 style="margin:0">Departments</h3></div>
      <div id="dept-content"></div>
    </div>
    <div class="card">
      <h3>Livestock types</h3>
      <p class="text-dim">Read-only — adding a new category or type requires a database migration.</p>
      <div id="livestock-content"></div>
    </div>
  `;

  const farmSelect = container.querySelector('#s-farm');
  function currentFarmId() { return farmSelect ? Number(farmSelect.value) : farmId; }

  async function loadAll() {
    await Promise.all([loadDepartments(currentFarmId()), loadLivestockTypes(currentFarmId())]);
  }

  async function loadDepartments(fid) {
    const target = container.querySelector('#dept-content');
    target.innerHTML = `<div class="empty-state">Loading…</div>`;
    const departments = await api.get(`/farms/${fid}/departments`);
    target.innerHTML = `
      <div class="table-scroll">
        <table><thead><tr><th>Name</th><th></th></tr></thead>
        <tbody>${departments.map((d) => `
          <tr><td>${escapeHtml(d.name)}</td><td><button class="danger" data-del="${d.id}">Delete</button></td></tr>
        `).join('') || '<tr><td colspan="2" class="empty-state">No departments configured</td></tr>'}</tbody></table>
      </div>
      <form id="add-dept-form" class="toolbar" style="margin-top:1rem">
        <input name="name" placeholder="New department name" required>
        <button type="submit" class="secondary">Add</button>
      </form>
    `;
    target.querySelectorAll('[data-del]').forEach((btn) => {
      btn.addEventListener('click', async () => {
        if (!confirm('Delete this department?')) return;
        try {
          await api.delete(`/farms/${fid}/departments/${btn.dataset.del}`);
          showToast('Department deleted', 'success');
          loadDepartments(fid);
        } catch (err) { showToast(err.message, 'error'); }
      });
    });
    target.querySelector('#add-dept-form').addEventListener('submit', async (e) => {
      e.preventDefault();
      const name = new FormData(e.target).get('name');
      try {
        await api.post(`/farms/${fid}/departments`, { name });
        showToast('Department added', 'success');
        loadDepartments(fid);
      } catch (err) { showToast(err.message, 'error'); }
    });
  }

  async function loadLivestockTypes(fid) {
    const target = container.querySelector('#livestock-content');
    target.innerHTML = `<div class="empty-state">Loading…</div>`;
    const byCategory = await api.get(`/farms/${fid}/livestock-types`);
    const rows = Object.entries(byCategory).flatMap(([category, types]) =>
      types.map((t) => `<tr><td>${escapeHtml(category)}</td><td>${escapeHtml(t.type)}</td></tr>`)
    ).join('');
    target.innerHTML = `
      <div class="table-scroll">
        <table><thead><tr><th>Category</th><th>Type</th></tr></thead>
        <tbody>${rows || '<tr><td colspan="2" class="empty-state">No livestock types configured</td></tr>'}</tbody></table>
      </div>
    `;
  }

  farmSelect?.addEventListener('change', loadAll);
  await loadAll();
}
