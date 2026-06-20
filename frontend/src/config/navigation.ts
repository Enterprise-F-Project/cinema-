import type { LucideIcon } from "lucide-react";
import {
  Clapperboard,
  FileKey,
  Film,
  LayoutDashboard,
  Shield,
  Users,
  Video,
} from "lucide-react";

import type { Role } from "@/types";

export interface NavItem {
  title: string;
  href: string;
  icon: LucideIcon;
  roles: Role[];
}

export const mainNavItems: NavItem[] = [
  {
    title: "Dashboard",
    href: "/dashboard",
    icon: LayoutDashboard,
    roles: ["ADMIN", "DISTRIBUTOR", "CLIENT"],
  },
  {
    title: "Movies",
    href: "/movies",
    icon: Film,
    roles: ["ADMIN", "DISTRIBUTOR", "CLIENT"],
  },
  {
    title: "Licenses",
    href: "/licenses",
    icon: FileKey,
    roles: ["ADMIN", "DISTRIBUTOR"],
  },
  {
    title: "Rentals",
    href: "/rentals",
    icon: Video,
    roles: ["ADMIN", "CLIENT"],
  },
  {
    title: "Clients",
    href: "/clients",
    icon: Users,
    roles: ["ADMIN", "DISTRIBUTOR"],
  },
  {
    title: "Admin",
    href: "/admin",
    icon: Shield,
    roles: ["ADMIN"],
  },
];

export function getNavItemsForRole(role: Role | null): NavItem[] {
  if (!role) return [];
  return mainNavItems.filter((item) => item.roles.includes(role));
}

export const brandIcon = Clapperboard;
