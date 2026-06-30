"use client";

import { usePathname, useRouter } from "next/navigation";
import { useEffect } from "react";

import { mainNavItems } from "@/config/navigation";
import { useAuth } from "@/providers/auth-provider";

const adminRoutes = ["/admin", "/admin/users"];

export function RouteGuard({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const { role, isLoading } = useAuth();

  useEffect(() => {
    if (isLoading || !role) return;

    const isAdminRoute = adminRoutes.some(
      (route) => pathname === route || pathname.startsWith(`${route}/`),
    );

    if (isAdminRoute && role !== "ADMIN") {
      router.replace("/dashboard");
      return;
    }

    const navItem = mainNavItems.find((item) => item.href === pathname);
    if (navItem && !navItem.roles.includes(role)) {
      router.replace("/dashboard");
    }
  }, [isLoading, pathname, role, router]);

  return <>{children}</>;
}
