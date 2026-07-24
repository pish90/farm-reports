const TOKEN_KEY = 'fr_console_token';

function base64UrlDecode(str) {
  str = str.replace(/-/g, '+').replace(/_/g, '/');
  while (str.length % 4) str += '=';
  const binary = atob(str);
  const bytes = Uint8Array.from(binary, (c) => c.charCodeAt(0));
  return new TextDecoder('utf-8').decode(bytes);
}

export function decodeToken(token) {
  try {
    const payload = token.split('.')[1];
    return JSON.parse(base64UrlDecode(payload));
  } catch {
    return null;
  }
}

let session = null;

export function loadSession() {
  const token = localStorage.getItem(TOKEN_KEY);
  if (!token) return null;
  const claims = decodeToken(token);
  if (!claims) return null;
  if (claims.exp && Date.now() >= claims.exp * 1000) {
    clearSession();
    return null;
  }
  session = {
    token,
    userId: claims.userId,
    userName: claims.userName,
    role: claims.role,
    farmId: claims.farmId ?? null,
    farmName: claims.farmName ?? null,
    mustChangePassword: !!claims.mustChangePassword,
  };
  return session;
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
  return loadSession();
}

export function getSession() {
  return session;
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  session = null;
}

export function isAdmin() {
  return session?.role === 'ADMIN';
}

export function isOpsManager() {
  return session?.role === 'OPERATIONS_MANAGER';
}

export function canSeeAllFarms() {
  return isAdmin() || isOpsManager();
}
