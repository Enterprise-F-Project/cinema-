"use client";

import Link from "next/link";
import { FileKey, Film, Shield, Users, Video } from "lucide-react";
import { useEffect, useState } from "react";
import { toast } from "sonner";

import { PageHeader } from "@/components/layout/page-header";
import { LoadingState } from "@/components/shared/loading-state";
import { RoleHero } from "@/components/shared/role-hero";
import { StatCard } from "@/components/shared/stat-card";
import { buttonVariants } from "@/components/ui/button-variants";
import { adminApi, ApiError } from "@/lib/api";
import { cn } from "@/lib/utils";
import type { DashboardStats } from "@/types";

const statConfig: {
  key: keyof DashboardStats;
  label: string;
  icon: typeof Users;
}[] = [
  { key: "totalUsers", label: "Registered users", icon: Users },
  { key: "totalMovies", label: "Movies in catalog", icon: Film },
  { key: "totalClients", label: "Client organizations", icon: Users },
  { key: "activeLicenses", label: "Active licenses", icon: FileKey },
  { key: "activeRentals", label: "Active rentals", icon: Video },
];

export function AdminDashboardContent() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadStats() {
      try {
        const data = await adminApi.getDashboard();
        setStats(data);
      } catch (error) {
        const message =
          error instanceof ApiError
            ? error.message
            : "Failed to load admin dashboard";
        toast.error(message);
      } finally {
        setIsLoading(false);
      }
    }

    void loadStats();
  }, []);

  return (
    <div className="space-y-8">
      <PageHeader
        title="Administration"
        description="Platform-wide metrics, user management, and operational oversight."
        actions={
          <Link
            href="/admin/users"
            className={cn(buttonVariants({ variant: "outline" }), "hover-glow")}
          >
            Manage users
          </Link>
        }
      />

      <RoleHero
        variant="admin"
        icon={Shield}
        badge="Admin control center"
        title="Platform oversight"
        description="Full visibility into users, catalog health, licenses, and rental activity."
      />

      {isLoading ? (
        <LoadingState message="Loading admin metrics..." />
      ) : stats ? (
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
          {statConfig.map(({ key, label, icon }, index) => (
            <StatCard
              key={key}
              label={label}
              value={stats[key]}
              icon={icon}
              delay={index * 80}
            />
          ))}
        </div>
      ) : null}
    </div>
  );
}
