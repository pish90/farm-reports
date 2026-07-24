import { api, apiDownload } from '../api.js';
import { canSeeAllFarms, getSession } from '../auth.js';
import { escapeHtml, formatMoney, formatDate, monthName, openModal, showToast } from '../util.js';

let subTab = 'labourers';

export async function render(container) {
  const session = getSession();
  const farms = canSeeAllFarms() ? await api.get('/admin/farms') : [];
  const farmId = canSeeAllFarms() ? (farms[0]?.farmId) : session.farmId;

  if (!farmId) {
    container.innerHTML = `<h2>Casual Labour</h2><div class="card"><div class="empty-state">Your role has no farm assigned.</div></div>`;
    return;
  }

  container.innerHTML = `
    <div class="section-title">
      <h2 style="margin:0">Casual Labour</h2>
      ${canSeeAllFarms() ? `
        <select id="c-farm">${farms.map((f) => `<option value="${f.farmId}">${escapeHtml(f.farmName)}</option>`).join('')}</select>` : ''}
    </div>
    <div class="tabs">
      <div class="tab" data-tab="labourers">Labourers</div>
      <div class="tab" data-tab="sessions">Work sessions</div>
      <div class="tab" data-tab="monthly">Monthly payroll</div>
    </div>
    <div id="casual-content" class="card"></div>
  `;

  const content = container.querySelector('#casual-content');
  const farmSelect = container.querySelector('#c-farm');

  function currentFarmId() {
    return farmSelect ? Number(farmSelect.value) : farmId;
  }

  function selectTab(key) {
    subTab = key;
    container.querySelectorAll('.tab').forEach((t) => t.classList.toggle('active', t.dataset.tab === key));
    if (key === 'labourers') renderLabourers(content, currentFarmId());
    else if (key === 'sessions') renderSessions(content, currentFarmId());
    else renderMonthly(content, currentFarmId());
  }

  container.querySelectorAll('.tab').forEach((t) => t.addEventListener('click', () => selectTab(t.dataset.tab)));
  farmSelect?.addEventListener('change', () => selectTab(subTab));
  selectTab(subTab);
}

// ── Labourers ────────────────────────────────────────────────────────────

async function renderLabourers(container, farmId) {
  container.innerHTML = `<div class="empty-state">Loading…</div>`;
  const labourers = await api.get(`/farms/${farmId}/casual-labourers`);

  container.innerHTML = `
    <div class="section-title"><h3 style="margin:0">Active casual labourers</h3><button id="add-labourer">Add labourer</button></div>
    <div class="table-scroll">
      <table>
        <thead><tr><th>LS #</th><th>Name</th><th>Phone</th><th>Job title</th><th>Department</th><th></th></tr></thead>
        <tbody>${labourers.map((l) => `
          <tr>
            <td class="mono">${escapeHtml(l.lsNumber || '—')}</td>
            <td>${escapeHtml(l.name)}</td>
            <td>${escapeHtml(l.phone || '—')}</td>
            <td>${escapeHtml(l.jobTitle || '—')}</td>
            <td>${escapeHtml(l.departmentName || '—')}</td>
            <td class="row-actions">
              <button class="secondary" data-view="${l.id}">View</button>
              <button class="danger" data-deactivate="${l.id}">Deactivate</button>
            </td>
          </tr>
        `).join('') || `<tr><td colspan="6" class="empty-state">No active casual labourers</td></tr>`}</tbody>
      </table>
    </div>
  `;

  container.querySelector('#add-labourer').addEventListener('click', () => openLabourerModal(farmId, null, () => renderLabourers(container, farmId)));
  container.querySelectorAll('[data-view]').forEach((btn) => {
    const labourer = labourers.find((l) => String(l.id) === btn.dataset.view);
    btn.addEventListener('click', () => openLabourerModal(farmId, labourer, () => renderLabourers(container, farmId)));
  });
  container.querySelectorAll('[data-deactivate]').forEach((btn) => {
    btn.addEventListener('click', async () => {
      if (!confirm('Deactivate this labourer?')) return;
      try {
        await api.delete(`/farms/${farmId}/casual-labourers/${btn.dataset.deactivate}`);
        showToast('Labourer deactivated', 'success');
        renderLabourers(container, farmId);
      } catch (err) { showToast(err.message, 'error'); }
    });
  });
}

async function openLabourerModal(farmId, labourer, onSaved) {
  const isNew = !labourer;
  const departments = await api.get(`/farms/${farmId}/departments`).catch(() => []);
  const deptOptions = departments.map((d) => `<option value="${d.id}">${escapeHtml(d.name)}</option>`).join('');
  const summary = !isNew ? await api.get(`/farms/${farmId}/casual-labourers/${labourer.id}/summary`).catch(() => null) : null;

  const { modal, close } = openModal(`
    <h3>${isNew ? 'Add labourer' : escapeHtml(labourer.name)}</h3>
    ${labourer?.lsNumber ? `<p class="text-dim mono" style="margin-top:-0.5rem">${labourer.lsNumber}</p>` : ''}
    <form id="labourer-form">
      <div class="form-grid">
        <div><label>First name</label><input name="firstName" value="${escapeHtml(labourer?.firstName || '')}" required></div>
        <div><label>Last name</label><input name="lastName" value="${escapeHtml(labourer?.lastName || '')}"></div>
        <div><label>Phone</label><input name="phone" value="${escapeHtml(labourer?.phone || '')}"></div>
        <div><label>Job title</label><input name="jobTitle" value="${escapeHtml(labourer?.jobTitle || '')}"></div>
        <div class="span-2"><label>Department</label><select name="departmentId"><option value="">—</option>${deptOptions}</select></div>
      </div>
      <div class="modal-actions">
        <button type="button" class="secondary" id="cancel-btn">Cancel</button>
        <button type="submit">${isNew ? 'Create' : 'Save changes'}</button>
      </div>
    </form>
    ${summary ? `
      <div class="section-title" style="margin-top:1.5rem"><h4 style="margin:0">Payments</h4></div>
      <div class="stat-grid">
        <div class="stat-tile"><div class="label">All-time earned</div><div class="value">${formatMoney(summary.allTimeEarned)}</div></div>
        <div class="stat-tile"><div class="label">All-time paid</div><div class="value">${formatMoney(summary.allTimePaid)}</div></div>
        <div class="stat-tile"><div class="label">Outstanding</div><div class="value">${formatMoney(summary.outstanding)}</div></div>
      </div>
      <div class="table-scroll">
        <table><thead><tr><th>Date</th><th>Amount</th><th>Note</th><th></th></tr></thead>
        <tbody id="payment-rows">${summary.payments.map((p) => `
          <tr><td>${formatDate(p.paymentDate)}</td><td>${formatMoney(p.amount)}</td><td>${escapeHtml(p.note || '')}</td>
          <td><button class="danger" data-del-payment="${p.id}">Delete</button></td></tr>
        `).join('') || '<tr><td colspan="4" class="empty-state">No payments recorded</td></tr>'}</tbody></table>
      </div>
      <form id="add-payment-form" class="form-grid" style="margin-top:0.75rem">
        <div><label>Date</label><input type="date" name="paymentDate" required></div>
        <div><label>Amount</label><input type="number" step="0.01" name="amount" required></div>
        <div class="span-2"><label>Note</label><input name="note"></div>
        <div class="span-2"><button type="submit" class="secondary">Record payment</button></div>
      </form>
    ` : ''}
  `);

  modal.querySelector('#cancel-btn').addEventListener('click', close);
  modal.querySelector('#labourer-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const payload = {
      firstName: fd.get('firstName'),
      lastName: fd.get('lastName') || null,
      phone: fd.get('phone') || null,
      jobTitle: fd.get('jobTitle') || null,
      departmentId: fd.get('departmentId') ? Number(fd.get('departmentId')) : null,
    };
    try {
      if (isNew) await api.post(`/farms/${farmId}/casual-labourers`, payload);
      else await api.put(`/farms/${farmId}/casual-labourers/${labourer.id}`, payload);
      showToast('Saved', 'success');
      close();
      onSaved();
    } catch (err) { showToast(err.message, 'error'); }
  });

  modal.querySelectorAll('[data-del-payment]').forEach((btn) => {
    btn.addEventListener('click', async () => {
      if (!confirm('Delete this payment?')) return;
      await api.delete(`/farms/${farmId}/casual-labourers/${labourer.id}/payments/${btn.dataset.delPayment}`);
      showToast('Payment deleted', 'success');
      close();
      onSaved();
    });
  });
  modal.querySelector('#add-payment-form')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    try {
      await api.post(`/farms/${farmId}/casual-labourers/${labourer.id}/payments`, {
        paymentDate: fd.get('paymentDate'), amount: Number(fd.get('amount')), note: fd.get('note') || null,
      });
      showToast('Payment recorded', 'success');
      close();
      onSaved();
    } catch (err) { showToast(err.message, 'error'); }
  });
}

// ── Work sessions ────────────────────────────────────────────────────────

async function renderSessions(container, farmId) {
  container.innerHTML = `<div class="empty-state">Loading…</div>`;
  const [sessions, labourers] = await Promise.all([
    api.get(`/farms/${farmId}/casual-labourers/work-sessions`),
    api.get(`/farms/${farmId}/casual-labourers`),
  ]);

  container.innerHTML = `
    <div class="section-title"><h3 style="margin:0">Work sessions</h3><button id="add-session">New session</button></div>
    <div class="table-scroll">
      <table>
        <thead><tr><th>Date</th><th>Activity</th><th>Default rate</th><th>Labourers</th><th></th></tr></thead>
        <tbody>${sessions.map((s) => `
          <tr>
            <td>${formatDate(s.sessionDate)}</td>
            <td>${escapeHtml(s.activity)}</td>
            <td>${formatMoney(s.defaultDailyRate)}</td>
            <td>${s.entries.length}</td>
            <td class="row-actions">
              <button class="secondary" data-edit="${s.id}">Edit</button>
              <button class="danger" data-del="${s.id}">Delete</button>
            </td>
          </tr>
        `).join('') || `<tr><td colspan="5" class="empty-state">No work sessions recorded</td></tr>`}</tbody>
      </table>
    </div>
  `;

  container.querySelector('#add-session').addEventListener('click', () => openSessionModal(farmId, null, labourers, () => renderSessions(container, farmId)));
  container.querySelectorAll('[data-edit]').forEach((btn) => {
    const session = sessions.find((s) => String(s.id) === btn.dataset.edit);
    btn.addEventListener('click', () => openSessionModal(farmId, session, labourers, () => renderSessions(container, farmId)));
  });
  container.querySelectorAll('[data-del]').forEach((btn) => {
    btn.addEventListener('click', async () => {
      if (!confirm('Delete this work session?')) return;
      try {
        await api.delete(`/farms/${farmId}/casual-labourers/work-sessions/${btn.dataset.del}`);
        showToast('Session deleted', 'success');
        renderSessions(container, farmId);
      } catch (err) { showToast(err.message, 'error'); }
    });
  });
}

function openSessionModal(farmId, session, labourers, onSaved) {
  const isNew = !session;
  const selectedIds = new Set((session?.entries || []).map((e) => e.casualLabourerId));

  const { modal, close } = openModal(`
    <h3>${isNew ? 'New work session' : 'Edit work session'}</h3>
    <form id="session-form">
      <div class="form-grid">
        <div><label>Date</label><input type="date" name="sessionDate" value="${session?.sessionDate || ''}" required></div>
        <div><label>Default daily rate</label><input type="number" step="0.01" name="defaultDailyRate" value="${session?.defaultDailyRate ?? ''}" required></div>
        <div class="span-2"><label>Activity</label><input name="activity" value="${escapeHtml(session?.activity || '')}" required></div>
      </div>
      <div class="span-2" style="margin-top:1rem">
        <label>Labourers present</label>
        <div class="table-scroll" style="max-height:240px;overflow-y:auto">
          <table><tbody>
            ${labourers.map((l) => `
              <tr>
                <td><input type="checkbox" data-labourer="${l.id}" ${selectedIds.has(l.id) ? 'checked' : ''}></td>
                <td>${escapeHtml(l.name)}</td>
                <td><input type="number" step="0.01" placeholder="rate override" data-rate="${l.id}"
                  value="${(session?.entries || []).find((e) => e.casualLabourerId === l.id)?.rateOverride ?? ''}"></td>
              </tr>
            `).join('')}
          </tbody></table>
        </div>
      </div>
      <div class="modal-actions">
        <button type="button" class="secondary" id="cancel-btn">Cancel</button>
        <button type="submit">${isNew ? 'Create' : 'Save changes'}</button>
      </div>
    </form>
  `);

  modal.querySelector('#cancel-btn').addEventListener('click', close);
  modal.querySelector('#session-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const entries = [...modal.querySelectorAll('[data-labourer]')]
      .filter((cb) => cb.checked)
      .map((cb) => {
        const rateInput = modal.querySelector(`[data-rate="${cb.dataset.labourer}"]`);
        return { casualLabourerId: Number(cb.dataset.labourer), rateOverride: rateInput.value || null };
      });
    if (!entries.length) { showToast('Select at least one labourer', 'error'); return; }
    const payload = {
      sessionDate: fd.get('sessionDate'),
      activity: fd.get('activity'),
      defaultDailyRate: Number(fd.get('defaultDailyRate')),
      entries,
    };
    try {
      if (isNew) await api.post(`/farms/${farmId}/casual-labourers/work-sessions`, payload);
      else await api.put(`/farms/${farmId}/casual-labourers/work-sessions/${session.id}`, payload);
      showToast('Session saved', 'success');
      close();
      onSaved();
    } catch (err) { showToast(err.message, 'error'); }
  });
}

// ── Monthly payroll ─────────────────────────────────────────────────────

async function renderMonthly(container, farmId) {
  const now = new Date();
  container.innerHTML = `
    <div class="toolbar">
      <select id="m-year">${Array.from({ length: 6 }, (_, i) => now.getFullYear() - i)
        .map((y) => `<option value="${y}" ${y === now.getFullYear() ? 'selected' : ''}>${y}</option>`).join('')}</select>
      <select id="m-month">${Array.from({ length: 12 }, (_, i) => i + 1)
        .map((m) => `<option value="${m}" ${m === now.getMonth() + 1 ? 'selected' : ''}>${monthName(m)}</option>`).join('')}</select>
      <button class="secondary" id="m-load">Load</button>
      <button class="secondary" id="m-export">Export Excel</button>
    </div>
    <div id="monthly-table"><div class="empty-state">Pick a period and load</div></div>
  `;

  async function load() {
    const year = container.querySelector('#m-year').value;
    const month = container.querySelector('#m-month').value;
    const target = container.querySelector('#monthly-table');
    target.innerHTML = `<div class="empty-state">Loading…</div>`;
    const entries = await api.get(`/farms/${farmId}/casual-labourers/payroll?year=${year}&month=${month}`);
    target.innerHTML = `
      <div class="table-scroll">
        <table>
          <thead><tr><th>Name</th><th>Days present</th><th>Month earnings</th><th>All-time paid</th><th>Outstanding</th></tr></thead>
          <tbody>${entries.map((e) => `
            <tr><td>${escapeHtml(e.name)}</td><td>${e.daysPresent}</td><td>${formatMoney(e.monthEarnings)}</td>
            <td>${formatMoney(e.allTimePaid)}</td><td>${formatMoney(e.outstanding)}</td></tr>
          `).join('') || '<tr><td colspan="5" class="empty-state">No casual payroll data for this period</td></tr>'}</tbody>
        </table>
      </div>
    `;
  }

  container.querySelector('#m-load').addEventListener('click', load);
  container.querySelector('#m-export').addEventListener('click', () => {
    const year = container.querySelector('#m-year').value;
    const month = container.querySelector('#m-month').value;
    apiDownload(`/farms/${farmId}/casual-labourers/export?year=${year}&month=${month}`,
      `casual-labour-${year}-${String(month).padStart(2, '0')}.xlsx`).catch((err) => showToast(err.message, 'error'));
  });

  await load();
}
