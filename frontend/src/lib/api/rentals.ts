import { apiClient, type QueryParams } from "@/lib/api/client";
import type {
  CreateRentalRequest,
  PageResponse,
  Rental,
  UpdateRentalStatusRequest,
} from "@/types";

export const rentalsApi = {
  list(params?: QueryParams) {
    return apiClient<PageResponse<Rental>>("/rentals", { params });
  },

  getById(id: number) {
    return apiClient<Rental>(`/rentals/${id}`);
  },

  create(data: CreateRentalRequest) {
    return apiClient<Rental>("/rentals", { method: "POST", body: data });
  },

  updateStatus(id: number, data: UpdateRentalStatusRequest) {
    return apiClient<Rental>(`/rentals/${id}/status`, {
      method: "PATCH",
      body: data,
    });
  },
};
