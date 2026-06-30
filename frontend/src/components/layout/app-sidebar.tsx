"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

import { brandIcon, getNavItemsForRole } from "@/config/navigation";
import { siteConfig } from "@/config/site";
import { cn } from "@/lib/utils";
import type { Role } from "@/types";

const BrandIcon = brandIcon;

interface AppSidebarProps {
  role: Role;
}

export function AppSidebar({ role }: AppSidebarProps) {
  const pathname = usePathname();
  const navItems = getNavItemsForRole(role);

  return (
    <aside className="hidden w-64 shrink-0 flex-col border-r border-sidebar-border bg-sidebar md:flex">
      <div className="flex h-16 items-center gap-3 border-b border-sidebar-border px-5">
        <div className="flex size-9 items-center justify-center rounded-lg bg-primary/15 ring-1 ring-primary/25">
          <BrandIcon className="size-4 text-primary" />
        </div>
        <div>
          <p className="font-heading text-sm font-semibold text-sidebar-foreground">
            {siteConfig.name}
          </p>
          <p className="text-[11px] text-muted-foreground">Distribution platform</p>
        </div>
      </div>

      <nav className="flex flex-1 flex-col gap-1 p-3">
        {navItems.map((item) => {
          const isActive =
            pathname === item.href || pathname.startsWith(`${item.href}/`);
          const Icon = item.icon;

          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                "group flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-all duration-200",
                isActive
                  ? "nav-item-active"
                  : "text-sidebar-foreground hover:bg-sidebar-accent hover:translate-x-0.5",
              )}
            >
              <Icon
                className={cn(
                  "size-4 transition-colors duration-200",
                  isActive
                    ? "text-primary"
                    : "text-muted-foreground group-hover:text-primary",
                )}
              />
              {item.title}
            </Link>
          );
        })}
      </nav>

      <div className="border-t border-sidebar-border p-4">
        <div className="rounded-xl bg-primary/8 p-3 ring-1 ring-primary/15">
          <p className="text-xs font-medium text-primary">Cinema Platform</p>
          <p className="mt-0.5 text-[11px] leading-relaxed text-muted-foreground">
            Manage your catalog, licenses, and rentals from one place.
          </p>
        </div>
      </div>
    </aside>
  );
}
