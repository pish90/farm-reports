const routes = new Map();

export function registerRoute(path, loader) {
  routes.set(path, loader);
}

export function navigate(path) {
  location.hash = `#${path}`;
}

export function currentRoute() {
  const hash = location.hash.replace(/^#/, '') || '/dashboard';
  const [path, queryString] = hash.split('?');
  const params = Object.fromEntries(new URLSearchParams(queryString || ''));
  return { path, params };
}

export async function renderRoute(container, onRouteChange) {
  const { path, params } = currentRoute();
  const loader = routes.get(path);
  if (!loader) {
    container.innerHTML = '<div class="empty-state">Page not found.</div>';
    return;
  }
  container.innerHTML = '<div class="empty-state">Loading…</div>';
  try {
    const view = await loader();
    await view.render(container, params);
  } catch (err) {
    console.error(err);
    container.innerHTML = `<div class="empty-state">Failed to load: ${err.message || err}</div>`;
  }
  if (onRouteChange) onRouteChange(path);
}

export function startRouter(container, onRouteChange) {
  window.addEventListener('hashchange', () => renderRoute(container, onRouteChange));
  renderRoute(container, onRouteChange);
}
