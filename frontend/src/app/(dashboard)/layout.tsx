"use client";

import { useEffect } from "react";
import { Clapperboard } from "lucide-react";

import { AppHeader } from "@/components/layout/app-header";
import { AppSidebar } from "@/components/layout/app-sidebar";
import { RouteGuard } from "@/components/layout/route-guard";
import { useAuth } from "@/providers/auth-provider";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const { role, isLoading, isAuthenticated } = useAuth();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      window.location.assign("/login");
    }
  }, [isAuthenticated, isLoading]);

  if (isLoading || !role) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center gap-4 bg-background">
        <div className="flex size-14 items-center justify-center rounded-2xl bg-primary/15 ring-1 ring-primary/25 animate-pulse">
          <Clapperboard className="size-6 text-primary" />
        </div>
        <p className="text-sm text-muted-foreground">Loading workspace...</p>
      </div>
    );
  }

  return (
    <RouteGuard>
      <div className="flex min-h-screen bg-background">
        <div className="bg-brand-radial pointer-events-none fixed inset-0 opacity-30" />
        <AppSidebar role={role} />
        <div className="relative flex min-w-0 flex-1 flex-col">
          <AppHeader />
          <main className="flex-1 p-4 md:p-6 lg:p-8">{children}</main>
        </div>
      </div>
    </RouteGuard>
  );
}
