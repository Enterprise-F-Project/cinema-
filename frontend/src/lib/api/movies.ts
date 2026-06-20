import { apiClient, type QueryParams } from "@/lib/api/client";
import type {
  CreateMovieRequest,
  Movie,
  PageResponse,
  UpdateMovieRequest,
  UpdateMovieStatusRequest,
} from "@/types";

export const moviesApi = {
  list(params?: QueryParams) {
    return apiClient<PageResponse<Movie>>("/movies", { params });
  },

  getById(id: number) {
    return apiClient<Movie>(`/movies/${id}`);
  },

  create(data: CreateMovieRequest) {
    return apiClient<Movie>("/movies", { method: "POST", body: data });
  },

  update(id: number, data: UpdateMovieRequest) {
    return apiClient<Movie>(`/movies/${id}`, { method: "PUT", body: data });
  },

  updateStatus(id: number, data: UpdateMovieStatusRequest) {
    return apiClient<Movie>(`/movies/${id}/status`, {
      method: "PATCH",
      body: data,
    });
  },

  remove(id: number) {
    return apiClient<void>(`/movies/${id}`, { method: "DELETE" });
  },
};
