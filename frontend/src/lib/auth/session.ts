import type { AuthSession } from "@/types";

const TOKEN_KEY = "cinema_token";
const ROLE_KEY = "cinema_role";
const EXPIRES_KEY = "cinema_expires_at";

function isBrowser(): boolean {
  return typeof window !== "undefined";
}

export function getSession(): AuthSession | null {
  if (!isBrowser()) return null;

  const token = localStorage.getItem(TOKEN_KEY);
  const role = localStorage.getItem(ROLE_KEY) as AuthSession["role"] | null;
  const expiresAt = Number(localStorage.getItem(EXPIRES_KEY) ?? 0);

  if (!token || !role || !expiresAt) return null;
  if (Date.now() >= expiresAt) {
    clearSession();
    return null;
  }

  return { token, role, expiresAt };
}

export function setSession(session: AuthSession): void {
  if (!isBrowser()) return;

  localStorage.setItem(TOKEN_KEY, session.token);
  localStorage.setItem(ROLE_KEY, session.role);
  localStorage.setItem(EXPIRES_KEY, String(session.expiresAt));

  document.cookie = `cinema_token=${session.token}; path=/; SameSite=Lax`;
  document.cookie = `cinema_role=${session.role}; path=/; SameSite=Lax`;
}

export function clearSession(): void {
  if (!isBrowser()) return;

  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(ROLE_KEY);
  localStorage.removeItem(EXPIRES_KEY);

  document.cookie = "cinema_token=; path=/; max-age=0";
  document.cookie = "cinema_role=; path=/; max-age=0";
}

export function getAccessToken(): string | null {
  return getSession()?.token ?? null;
}

export function getUserRole(): AuthSession["role"] | null {
  return getSession()?.role ?? null;
}

export function isAuthenticated(): boolean {
  return getSession() !== null;
}
