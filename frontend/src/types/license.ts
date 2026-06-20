import type { LicenseStatus } from "./enums";

export interface License {
  id: number;
  startDate: string;
  endDate: string;
  terms: string | null;
  status: LicenseStatus;
  movieId: number;
  movieTitle: string;
  distributorId: number;
  distributorName: string;
  clientId: number;
  clientName: string;
  createdAt: string;
}

export interface CreateLicenseRequest {
  startDate: string;
  endDate: string;
  terms?: string;
  movieId: number;
  clientId: number;
}

export interface UpdateLicenseStatusRequest {
  status: LicenseStatus;
}

export interface LicenseFilterParams {
  status?: LicenseStatus;
  movieId?: number;
  clientId?: number;
  startDateFrom?: string;
  startDateTo?: string;
}
