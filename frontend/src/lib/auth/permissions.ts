import type { Role } from "@/types";

export function canAccessRoute(role: Role | null, allowedRoles: Role[]): boolean {
  if (!role) return false;
  return allowedRoles.includes(role);
}

export function formatRole(role: Role): string {
  return role.charAt(0) + role.slice(1).toLowerCase();
}
