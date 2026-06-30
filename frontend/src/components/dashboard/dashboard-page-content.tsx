"use client";

import Link from "next/link";
import { FileKey, Film, Users, Video } from "lucide-react";
import { useEffect, useState } from "react";
import { toast } from "sonner";

import { PageHeader } from "@/components/layout/page-header";
import { LoadingState } from "@/components/shared/loading-state";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { adminApi, ApiError } from "@/lib/api";
import { getNavItemsForRole } from "@/config/navigation";
import { useAuth } from "@/providers/auth-provider";
import type { DashboardStats } from "@/types";

const roleSummaries = {
  ADMIN: {
    title: "Administrator overview",
    description:
      "Monitor users, movies, clients, licenses, and active rentals across the platform.",
  },
  DISTRIBUTOR: {
    title: "Distributor overview",
    description:
      "Manage your movie catalog, licensing agreements, and client relationships.",
  },
  CLIENT: {
    title: "Client overview",
    description:
      "Browse available movies and track your rental requests and active rentals.",
  },
};

const statLabels: { key: keyof DashboardStats; label: string }[] = [
  { key: "totalUsers", label: "Users" },
  { key: "totalMovies", label: "Movies" },
  { key: "totalClients", label: "Clients" },
  { key: "activeLicenses", label: "Active licenses" },
  { key: "activeRentals", label: "Active rentals" },
];

export function DashboardPageContent() {
  const { role, formatRole } = useAuth();
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [isLoading, setIsLoading] = useState(role === "ADMIN");

  useEffect(() => {
    if (role !== "ADMIN") return;

    async function loadStats() {
      try {
        const data = await adminApi.getDashboard();
        setStats(data);
      } catch (error) {
        const message =
          error instanceof ApiError ? error.message : "Failed to load dashboard";
        toast.error(message);
      } finally {
        setIsLoading(false);
      }
    }

    void loadStats();
  }, [role]);

  if (!role) return null;

  const summary = roleSummaries[role];
  const quickLinks = getNavItemsForRole(role).filter(
    (item) => item.href !== "/dashboard",
  );

  return (
    <div className="space-y-8">
      <PageHeader title={summary.title} description={summary.description} />

      {role === "ADMIN" && (
        <>
          {isLoading ? (
            <LoadingState message="Loading platform metrics..." />
          ) : stats ? (
            <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-5">
              {statLabels.map(({ key, label }) => (
                <Card key={key}>
                  <CardHeader className="pb-2">
                    <CardDescription>{label}</CardDescription>
                    <CardTitle className="text-3xl">{stats[key]}</CardTitle>
                  </CardHeader>
                </Card>
              ))}
            </div>
          ) : null}
        </>
      )}

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {quickLinks.map((link) => {
          const Icon =
            link.icon === Film
              ? Film
              : link.icon === FileKey
                ? FileKey
                : link.icon === Video
                  ? Video
                  : Users;

          return (
            <Card key={link.href}>
              <CardHeader>
                <div className="mb-2 flex size-10 items-center justify-center rounded-lg bg-muted">
                  <Icon className="size-5" />
                </div>
                <CardTitle className="text-base">{link.title}</CardTitle>
                <CardDescription>
                  Open the {link.title.toLowerCase()} workspace.
                </CardDescription>
              </CardHeader>
              <CardContent>
                <Link
                  href={link.href}
                  className="text-sm font-medium text-primary hover:underline"
                >
                  Go to {link.title.toLowerCase()}
                </Link>
              </CardContent>
            </Card>
          );
        })}
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Welcome, {formatRole(role)}</CardTitle>
          <CardDescription>
            Use the navigation sidebar to manage movies, licenses, rentals, and
            clients based on your role permissions.
          </CardDescription>
        </CardHeader>
      </Card>
    </div>
  );
}
