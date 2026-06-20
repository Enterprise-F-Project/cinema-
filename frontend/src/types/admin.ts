import type { Role } from "./enums";
import type { User } from "./auth";

export interface DashboardStats {
  totalUsers: number;
  totalMovies: number;
  totalClients: number;
  activeLicenses: number;
  activeRentals: number;
}

export interface UpdateUserStatusRequest {
  active: boolean;
}

export interface UserFilterParams {
  role?: Role;
  active?: boolean;
}

export type { User };
