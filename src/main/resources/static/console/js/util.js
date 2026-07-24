export function formatMoney(value) {
  if (value === null || value === undefined || value === '') return '—';
  const n = Number(value);
  if (Number.isNaN(n)) return '—';
  return n.toLocaleString('en-KE', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

export function formatDate(value) {
  if (!value) return '—';
  return String(value).slice(0, 10);
}

export function formatDateTime(value) {
  if (!value) return '—';
  return String(value).replace('T', ' ').slice(0, 16);
}

export function debounce(fn, delayMs = 250) {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => fn(...args), delayMs);
  };
}

export function el(html) {
  const template = document.createElement('template');
  template.innerHTML = html.trim();
  return template.content.firstElementChild;
}

export function escapeHtml(str) {
  if (str === null || str === undefined) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

let toastTimer;
export function showToast(message, type = 'info') {
  const toast = document.getElementById('toast');
  if (!toast) return;
  toast.textContent = message;
  toast.className = `toast ${type}`;
  toast.hidden = false;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => { toast.hidden = true; }, 3500);
}

export function openModal(innerHtml) {
  const backdrop = el(`<div class="modal-backdrop"><div class="modal">${innerHtml}</div></div>`);
  document.body.appendChild(backdrop);
  const close = () => backdrop.remove();
  backdrop.addEventListener('click', (e) => { if (e.target === backdrop) close(); });
  return { backdrop, modal: backdrop.querySelector('.modal'), close };
}

export function monthName(month) {
  const names = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
  return names[(Number(month) || 1) - 1] || month;
}

export function daysInMonth(year, month) {
  return new Date(Number(year), Number(month), 0).getDate();
}
