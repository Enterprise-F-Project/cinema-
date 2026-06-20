"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";

import { authApi } from "@/lib/api";
import { formatRole } from "@/lib/auth/permissions";
import {
  clearSession,
  getSession,
  getUserRole,
  setSession,
} from "@/lib/auth/session";
import type { LoginRequest, RegisterRequest, Role } from "@/types";

interface AuthContextValue {
  role: Role | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  formatRole: (role: Role) => string;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [role, setRole] = useState<Role | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    setRole(getUserRole());
    setIsLoading(false);
  }, []);

  const login = useCallback(async (data: LoginRequest) => {
    const response = await authApi.login(data);
    const session = {
      token: response.accessToken,
      role: response.role,
      expiresAt: Date.now() + response.expiresIn,
    };

    setSession(session);
    setRole(response.role);
  }, []);

  const register = useCallback(async (data: RegisterRequest) => {
    await authApi.register(data);
    await login({ email: data.email, password: data.password });
  }, [login]);

  const logout = useCallback(async () => {
    const session = getSession();

    if (session?.token) {
      try {
        await authApi.logout(session.token);
      } catch {
        // Clear local session even if the backend call fails.
      }
    }

    clearSession();
    setRole(null);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      role,
      isAuthenticated: role !== null,
      isLoading,
      login,
      register,
      logout,
      formatRole,
    }),
    [role, isLoading, login, register, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }

  return context;
}
