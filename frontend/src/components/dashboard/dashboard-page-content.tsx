"use client";

import Link from "next/link";
import {
  ArrowRight,
  Building2,
  Clapperboard,
  FileKey,
  Film,
  Package,
  Shield,
  Sparkles,
  Star,
  TrendingUp,
  Users,
  Video,
} from "lucide-react";
import { useEffect, useState } from "react";
import { toast } from "sonner";

import { PageHeader } from "@/components/layout/page-header";
import { LoadingState } from "@/components/shared/loading-state";
import { QuickLinkCard } from "@/components/shared/quick-link-card";
import { RoleHero } from "@/components/shared/role-hero";
import { StatCard } from "@/components/shared/stat-card";
import { buttonVariants } from "@/components/ui/button-variants";
import { adminApi, ApiError } from "@/lib/api";
import { getNavItemsForRole } from "@/config/navigation";
import { useAuth } from "@/providers/auth-provider";
import { cn } from "@/lib/utils";
import type { DashboardStats, Role } from "@/types";

const roleSummaries: Record<
  Role,
  { title: string; description: string; variant: "admin" | "distributor" | "client" }
> = {
  ADMIN: {
    title: "Administrator overview",
    description:
      "Monitor users, movies, clients, licenses, and active rentals across the platform.",
    variant: "admin",
  },
  DISTRIBUTOR: {
    title: "Distributor overview",
    description:
      "Manage your movie catalog, licensing agreements, and client relationships.",
    variant: "distributor",
  },
  CLIENT: {
    title: "Client overview",
    description:
      "Browse available movies and track your rental requests and active rentals.",
    variant: "client",
  },
};

const statConfig: {
  key: keyof DashboardStats;
  label: string;
  icon: typeof Users;
}[] = [
  { key: "totalUsers", label: "Users", icon: Users },
  { key: "totalMovies", label: "Movies", icon: Film },
  { key: "totalClients", label: "Clients", icon: Users },
  { key: "activeLicenses", label: "Active licenses", icon: FileKey },
  { key: "activeRentals", label: "Active rentals", icon: Video },
];

const quickLinkDescriptions: Record<string, string> = {
  Movies: "Browse and manage the movie catalog and availability.",
  Licenses: "Create and track distribution license agreements.",
  Rentals: "Monitor rental requests and fulfillment status.",
  Clients: "View and manage client organizations.",
  Admin: "Platform metrics and user administration.",
};

const distributorHighlights = [
  {
    icon: Film,
    title: "Catalog control",
    description: "Publish titles, set availability, and keep metadata current.",
  },
  {
    icon: FileKey,
    title: "License deals",
    description: "Issue agreements and monitor contract status with clients.",
  },
  {
    icon: Building2,
    title: "Client network",
    description: "Manage cinema and platform relationships in one place.",
  },
];

const clientWorkflow = [
  { step: "01", title: "Browse catalog", description: "Explore available movies" },
  { step: "02", title: "Request rental", description: "Submit a rental for approval" },
  { step: "03", title: "Track status", description: "Follow progress to completion" },
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

  const heroIcon =
    role === "ADMIN" ? Shield : role === "DISTRIBUTOR" ? Package : Clapperboard;

  return (
    <div className="space-y-8">
      <PageHeader title={summary.title} description={summary.description} />

      <RoleHero
        variant={summary.variant}
        icon={heroIcon}
        badge={`${formatRole(role)} workspace`}
        title="Welcome back"
        description="Your personalized command center for movies, licenses, rentals, and clients."
        footer={
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <TrendingUp className="size-4 text-primary" />
            All systems operational
          </div>
        }
      />

      {role === "ADMIN" && (
        <>
          {isLoading ? (
            <LoadingState message="Loading platform metrics..." />
          ) : stats ? (
            <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-5">
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
        </>
      )}

      {role === "DISTRIBUTOR" && (
        <div className="grid gap-4 md:grid-cols-3">
          {distributorHighlights.map((item, index) => {
            const Icon = item.icon;
            return (
              <div
                key={item.title}
                className="glass-card hover-lift group p-5"
                style={{ animationDelay: `${index * 100}ms` }}
              >
                <div className="icon-box mb-4 size-10">
                  <Icon className="size-5" />
                </div>
                <h3 className="font-semibold">{item.title}</h3>
                <p className="mt-1.5 text-sm text-muted-foreground">
                  {item.description}
                </p>
              </div>
            );
          })}
        </div>
      )}

      {role === "CLIENT" && (
        <div className="space-y-6">
          <div className="glass-card p-6">
            <h3 className="mb-4 flex items-center gap-2 font-semibold">
              <Sparkles className="size-4 text-primary" />
              Rental workflow
            </h3>
            <div className="grid gap-4 md:grid-cols-3">
              {clientWorkflow.map((step) => (
                <div
                  key={step.step}
                  className="rounded-xl border border-primary/15 bg-primary/5 p-4 transition-colors hover:border-primary/30 hover:bg-primary/8"
                >
                  <span className="text-xs font-bold text-primary">
                    {step.step}
                  </span>
                  <p className="mt-2 font-semibold">{step.title}</p>
                  <p className="mt-1 text-sm text-muted-foreground">
                    {step.description}
                  </p>
                </div>
              ))}
            </div>
          </div>
          <Link
            href="/movies"
            className={cn(
              buttonVariants({ size: "lg" }),
              "hover-glow inline-flex w-full justify-center sm:w-auto",
            )}
          >
            Browse movie catalog
            <ArrowRight />
          </Link>
        </div>
      )}

      <div>
        <h3 className="mb-4 text-lg font-bold">Quick access</h3>
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {quickLinks.map((link) => (
            <QuickLinkCard
              key={link.href}
              href={link.href}
              title={link.title}
              description={
                quickLinkDescriptions[link.title] ??
                `Open the ${link.title.toLowerCase()} workspace.`
              }
              icon={link.icon}
            />
          ))}
        </div>
      </div>
    </div>
  );
}
