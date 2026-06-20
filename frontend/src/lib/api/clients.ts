import { apiClient, type QueryParams } from "@/lib/api/client";
import type {
  Client,
  CreateClientRequest,
  PageResponse,
  UpdateClientRequest,
} from "@/types";

export const clientsApi = {
  list(params?: QueryParams) {
    return apiClient<PageResponse<Client>>("/clients", { params });
  },

  getById(id: number) {
    return apiClient<Client>(`/clients/${id}`);
  },

  create(data: CreateClientRequest) {
    return apiClient<Client>("/clients", { method: "POST", body: data });
  },

  update(id: number, data: UpdateClientRequest) {
    return apiClient<Client>(`/clients/${id}`, { method: "PUT", body: data });
  },
};
