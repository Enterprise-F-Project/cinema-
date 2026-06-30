"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { toast } from "sonner";

import { PageHeader } from "@/components/layout/page-header";
import { LoadingState } from "@/components/shared/loading-state";
import { buttonVariants } from "@/components/ui/button-variants";
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { adminApi, ApiError } from "@/lib/api";
import { cn } from "@/lib/utils";
import type { DashboardStats } from "@/types";

const statLabels: { key: keyof DashboardStats; label: string }[] = [
  { key: "totalUsers", label: "Registered users" },
  { key: "totalMovies", label: "Movies in catalog" },
  { key: "totalClients", label: "Client organizations" },
  { key: "activeLicenses", label: "Active licenses" },
  { key: "activeRentals", label: "Active rentals" },
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
    <div className="space-y-6">
      <PageHeader
        title="Administration"
        description="Platform-wide metrics, user management, and operational oversight."
        actions={
          <Link
            href="/admin/users"
            className={cn(buttonVariants({ variant: "outline" }))}
          >
            Manage users
          </Link>
        }
      />

      {isLoading ? (
        <LoadingState message="Loading admin metrics..." />
      ) : stats ? (
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
          {statLabels.map(({ key, label }) => (
            <Card key={key}>
              <CardHeader>
                <CardDescription>{label}</CardDescription>
                <CardTitle className="text-3xl">{stats[key]}</CardTitle>
              </CardHeader>
            </Card>
          ))}
        </div>
      ) : null}
    </div>
  );
}
