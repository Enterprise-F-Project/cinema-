import type { MovieAvailabilityStatus } from "./enums";

export interface Movie {
  id: number;
  title: string;
  genre: string;
  releaseDate: string;
  duration: number;
  description: string | null;
  availabilityStatus: MovieAvailabilityStatus;
  distributorId: number;
  distributorName: string;
  createdAt: string;
}

export interface CreateMovieRequest {
  title: string;
  genre: string;
  releaseDate: string;
  duration: number;
  description?: string;
}

export type UpdateMovieRequest = CreateMovieRequest;

export interface UpdateMovieStatusRequest {
  availabilityStatus: MovieAvailabilityStatus;
}

export interface MovieFilterParams {
  title?: string;
  genre?: string;
  availabilityStatus?: MovieAvailabilityStatus;
}
