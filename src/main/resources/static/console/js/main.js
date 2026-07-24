import { api } from './api.js';
import { loadSession, setToken, clearSession, getSession } from './auth.js';
import { registerRoute, startRouter, navigate, currentRoute } from './router.js';
import { showToast } from './util.js';

const loginScreen = document.getElementById('login-screen');
const changePasswordScreen = document.getElementById('change-password-screen');
const appShell = document.getElementById('app-shell');
const navLinks = document.getElementById('nav-links');
const sessionUserEl = document.getElementById('session-user');
const viewContainer = document.getElementById('view-container');

const NAV_ITEMS = [
  { path: '/dashboard', label: 'Dashboard' },
  { path: '/employees', label: 'Employees' },
  { path: '/reports', label: 'Reports' },
  { path: '/payroll', label: 'Payroll' },
  { path: '/casual', label: 'Casual Labour' },
  { path: '/settings', label: 'Farm Settings' },
  { path: '/audit', label: 'Audit Log' },
  { path: '/tools', label: 'Tools', adminOnly: true },
];

registerRoute('/dashboard', () => import('./views/dashboard.js'));
registerRoute('/employees', () => import('./views/employees.js'));
registerRoute('/reports', () => import('./views/reports.js'));
registerRoute('/reports/detail', () => import('./views/report-detail.js'));
registerRoute('/payroll', () => import('./views/payroll.js'));
registerRoute('/casual', () => import('./views/casual.js'));
registerRoute('/settings', () => import('./views/settings.js'));
registerRoute('/audit', () => import('./views/audit.js'));
registerRoute('/tools', () => import('./views/tools.js'));

function showScreen(name) {
  loginScreen.hidden = name !== 'login';
  changePasswordScreen.hidden = name !== 'change-password';
  appShell.hidden = name !== 'app';
}

function renderNav() {
  const session = getSession();
  navLinks.innerHTML = '';
  for (const item of NAV_ITEMS) {
    if (item.adminOnly && session.role !== 'ADMIN') continue;
    const a = document.createElement('a');
    a.className = 'nav-link';
    a.textContent = item.label;
    a.href = `#${item.path}`;
    navLinks.appendChild(a);
  }
  sessionUserEl.innerHTML = `${session.userName}<br><span class="role-badge">${session.role}${session.farmName ? ' · ' + session.farmName : ''}</span>`;
}

function highlightActiveNav(path) {
  const topLevel = '/' + path.split('/')[1];
  document.querySelectorAll('.nav-link').forEach((a) => {
    a.classList.toggle('active', a.getAttribute('href') === `#${topLevel}`);
  });
}

function enterApp() {
  showScreen('app');
  renderNav();
  startRouter(viewContainer, highlightActiveNav);
}

async function handleLoginSuccess(auth) {
  setToken(auth.token);
  const session = getSession();
  if (session.mustChangePassword) {
    showScreen('change-password');
    return;
  }
  enterApp();
  if (!location.hash) navigate('/dashboard');
}

document.getElementById('login-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const errorEl = document.getElementById('login-error');
  errorEl.hidden = true;
  const email = document.getElementById('login-email').value.trim();
  const password = document.getElementById('login-password').value;
  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    const auth = await res.json();
    if (!res.ok) throw new Error(auth.message || 'Invalid credentials');
    await handleLoginSuccess(auth);
  } catch (err) {
    errorEl.textContent = err.message || 'Sign in failed';
    errorEl.hidden = false;
  }
});

document.getElementById('change-password-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const errorEl = document.getElementById('change-password-error');
  errorEl.hidden = true;
  const currentPassword = document.getElementById('cp-current').value;
  const newPassword = document.getElementById('cp-new').value;
  try {
    const auth = await api.put('/auth/password', { currentPassword, newPassword });
    setToken(auth.token);
    showToast('Password updated', 'success');
    enterApp();
    navigate('/dashboard');
  } catch (err) {
    errorEl.textContent = err.message || 'Could not update password';
    errorEl.hidden = false;
  }
});

document.getElementById('logout-btn').addEventListener('click', () => {
  clearSession();
  location.hash = '';
  showScreen('login');
});

(function init() {
  const session = loadSession();
  if (!session) {
    showScreen('login');
    return;
  }
  if (session.mustChangePassword) {
    showScreen('change-password');
    return;
  }
  enterApp();
})();
