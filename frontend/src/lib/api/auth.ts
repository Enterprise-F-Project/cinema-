import { apiClient } from "@/lib/api/client";
import type {
  LoginRequest,
  MessageResponse,
  RegisterRequest,
  TokenResponse,
  User,
} from "@/types";

export const authApi = {
  register(data: RegisterRequest) {
    return apiClient<User>("/auth/register", {
      method: "POST",
      body: data,
      auth: false,
    });
  },

  login(data: LoginRequest) {
    return apiClient<TokenResponse>("/auth/login", {
      method: "POST",
      body: data,
      auth: false,
    });
  },

  logout(token: string) {
    return apiClient<MessageResponse>("/auth/logout", {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
      auth: false,
    });
  },
};
