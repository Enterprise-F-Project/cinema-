"use client";

import { FileKey, Film, Users, Video } from "lucide-react";

import { EmptyState } from "@/components/layout/empty-state";
import { PageHeader } from "@/components/layout/page-header";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { useAuth } from "@/providers/auth-provider";

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

const quickLinks = [
  {
    title: "Movies",
    description: "Browse and manage the movie catalog.",
    href: "/movies",
    icon: Film,
  },
  {
    title: "Licenses",
    description: "View distribution agreements and contract status.",
    href: "/licenses",
    icon: FileKey,
  },
  {
    title: "Rentals",
    description: "Track rental requests and completion status.",
    href: "/rentals",
    icon: Video,
  },
  {
    title: "Clients",
    description: "Manage cinema and platform organizations.",
    href: "/clients",
    icon: Users,
  },
];

export default function DashboardPage() {
  const { role, formatRole } = useAuth();

  if (!role) return null;

  const summary = roleSummaries[role];

  return (
    <div className="space-y-8">
      <PageHeader
        title={summary.title}
        description={summary.description}
      />

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {quickLinks.map((link) => {
          const Icon = link.icon;
          return (
            <Card key={link.title}>
              <CardHeader>
                <div className="mb-2 flex size-10 items-center justify-center rounded-lg bg-muted">
                  <Icon className="size-5" />
                </div>
                <CardTitle className="text-base">{link.title}</CardTitle>
                <CardDescription>{link.description}</CardDescription>
              </CardHeader>
              <CardContent>
                <a
                  href={link.href}
                  className="text-sm font-medium text-primary hover:underline"
                >
                  Open {link.title.toLowerCase()}
                </a>
              </CardContent>
            </Card>
          );
        })}
      </div>

      <EmptyState
        title={`Welcome, ${formatRole(role)}`}
        description="This dashboard is ready for live data. Connect the pages below to the Spring Boot API to load movies, licenses, rentals, and admin metrics."
      />
    </div>
  );
}
