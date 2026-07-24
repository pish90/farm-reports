import { getSession, clearSession } from './auth.js';

export const API_BASE = '/api';

class ApiError extends Error {
  constructor(message, status) {
    super(message);
    this.status = status;
  }
}

async function request(path, options = {}) {
  const session = getSession();
  const headers = { ...(options.headers || {}) };
  if (!(options.body instanceof FormData) && options.body && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json';
  }
  if (session?.token) headers['Authorization'] = `Bearer ${session.token}`;

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (res.status === 401) {
    clearSession();
    location.hash = '#/login';
    throw new ApiError('Session expired — please sign in again', 401);
  }

  const contentType = res.headers.get('content-type') || '';
  if (!contentType.includes('application/json')) {
    if (!res.ok) throw new ApiError(`Request failed (${res.status})`, res.status);
    return res;
  }

  const body = await res.json().catch(() => null);
  if (!res.ok || (body && body.success === false)) {
    throw new ApiError(body?.message || `Request failed (${res.status})`, res.status);
  }
  return body ? body.data : null;
}

export const api = {
  get: (path) => request(path, { method: 'GET' }),
  post: (path, data) => request(path, { method: 'POST', body: data !== undefined ? JSON.stringify(data) : undefined }),
  put: (path, data) => request(path, { method: 'PUT', body: data !== undefined ? JSON.stringify(data) : undefined }),
  delete: (path) => request(path, { method: 'DELETE' }),
};

export async function apiDownload(path, filename, options = {}) {
  const res = await request(path, { method: 'GET', ...options });
  const blob = res instanceof Response ? await res.blob() : res;
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

export { ApiError };
