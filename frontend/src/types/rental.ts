import type { RentalStatus } from "./enums";

export interface Rental {
  id: number;
  rentalDate: string;
  returnDate: string;
  status: RentalStatus;
  movieId: number;
  movieTitle: string;
  clientId: number;
  clientName: string;
  createdAt: string;
}

export interface CreateRentalRequest {
  movieId: number;
  rentalDate: string;
  returnDate: string;
}

export interface UpdateRentalStatusRequest {
  status: RentalStatus;
}

export interface RentalFilterParams {
  status?: RentalStatus;
  movieId?: number;
}
