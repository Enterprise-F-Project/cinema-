import { apiClient, type QueryParams } from "@/lib/api/client";
import type {
  DashboardStats,
  PageResponse,
  UpdateUserStatusRequest,
  User,
} from "@/types";

export const adminApi = {
  getDashboard() {
    return apiClient<DashboardStats>("/admin/dashboard");
  },

  listUsers(params?: QueryParams) {
    return apiClient<PageResponse<User>>("/admin/users", { params });
  },

  updateUserStatus(id: number, data: UpdateUserStatusRequest) {
    return apiClient<User>(`/admin/users/${id}/status`, {
      method: "PATCH",
      body: data,
    });
  },
};
