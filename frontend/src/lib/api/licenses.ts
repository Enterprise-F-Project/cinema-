import { apiClient, type QueryParams } from "@/lib/api/client";
import type {
  CreateLicenseRequest,
  License,
  PageResponse,
  UpdateLicenseStatusRequest,
} from "@/types";

export const licensesApi = {
  list(params?: QueryParams) {
    return apiClient<PageResponse<License>>("/licenses", { params });
  },

  getById(id: number) {
    return apiClient<License>(`/licenses/${id}`);
  },

  create(data: CreateLicenseRequest) {
    return apiClient<License>("/licenses", { method: "POST", body: data });
  },

  updateStatus(id: number, data: UpdateLicenseStatusRequest) {
    return apiClient<License>(`/licenses/${id}/status`, {
      method: "PATCH",
      body: data,
    });
  },
};
