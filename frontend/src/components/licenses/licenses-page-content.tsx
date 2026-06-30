"use client";

import { useCallback, useEffect, useState } from "react";
import { toast } from "sonner";

import { PageHeader } from "@/components/layout/page-header";
import { DataTable, type Column } from "@/components/shared/data-table";
import { LoadingState } from "@/components/shared/loading-state";
import { Pagination } from "@/components/shared/pagination";
import { LicenseStatusBadge } from "@/components/shared/status-badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { Textarea } from "@/components/ui/textarea";
import { ApiError, clientsApi, licensesApi, moviesApi } from "@/lib/api";
import { formatDate } from "@/lib/format";
import { useAuth } from "@/providers/auth-provider";
import type { Client, License, LicenseStatus, Movie } from "@/types";

const emptyForm = {
  startDate: "",
  endDate: "",
  terms: "",
  movieId: "",
  clientId: "",
};

export function LicensesPageContent() {
  const { role } = useAuth();
  const canCreate = role === "ADMIN" || role === "DISTRIBUTOR";
  const canUpdateStatus = role === "ADMIN";

  const [licenses, setLicenses] = useState<License[]>([]);
  const [movies, setMovies] = useState<Movie[]>([]);
  const [clients, setClients] = useState<Client[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);

  const [statusFilter, setStatusFilter] = useState<LicenseStatus | "">("");
  const [sheetOpen, setSheetOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const loadLicenses = useCallback(async () => {
    setIsLoading(true);
    try {
      const response = await licensesApi.list({
        page,
        size: 10,
        sort: "createdAt,desc",
        status: statusFilter || undefined,
      });
      setLicenses(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Failed to load licenses";
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  }, [page, statusFilter]);

  useEffect(() => {
    void loadLicenses();
  }, [loadLicenses]);

  useEffect(() => {
    if (!canCreate) return;

    async function loadOptions() {
      try {
        const [moviesResponse, clientsResponse] = await Promise.all([
          moviesApi.list({ page: 0, size: 100, sort: "title,asc" }),
          clientsApi.list({ page: 0, size: 100, sort: "organizationName,asc" }),
        ]);
        setMovies(moviesResponse.content);
        setClients(clientsResponse.content);
      } catch {
        // Options load silently; form will show empty selects.
      }
    }

    void loadOptions();
  }, [canCreate]);

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setIsSubmitting(true);

    try {
      await licensesApi.create({
        startDate: form.startDate,
        endDate: form.endDate,
        terms: form.terms || undefined,
        movieId: Number(form.movieId),
        clientId: Number(form.clientId),
      });
      toast.success("License created");
      setSheetOpen(false);
      setForm(emptyForm);
      await loadLicenses();
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Failed to create license";
      toast.error(message);
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleStatusToggle(license: License) {
    const nextStatus: LicenseStatus =
      license.status === "ACTIVE" ? "EXPIRED" : "ACTIVE";

    try {
      await licensesApi.updateStatus(license.id, { status: nextStatus });
      toast.success("License status updated");
      await loadLicenses();
    } catch (error) {
      const message =
        error instanceof ApiError
          ? error.message
          : "Failed to update license status";
      toast.error(message);
    }
  }

  const columns: Column<License>[] = [
    {
      key: "movie",
      header: "Movie",
      render: (license) => license.movieTitle,
    },
    {
      key: "client",
      header: "Client",
      render: (license) => license.clientName,
    },
    {
      key: "distributor",
      header: "Distributor",
      render: (license) => license.distributorName,
    },
    {
      key: "period",
      header: "Period",
      render: (license) =>
        `${formatDate(license.startDate)} – ${formatDate(license.endDate)}`,
    },
    {
      key: "status",
      header: "Status",
      render: (license) => <LicenseStatusBadge status={license.status} />,
    },
  ];

  if (canUpdateStatus) {
    columns.push({
      key: "actions",
      header: "Actions",
      render: (license) => (
        <Button
          variant="outline"
          size="sm"
          onClick={() => void handleStatusToggle(license)}
        >
          Mark {license.status === "ACTIVE" ? "expired" : "active"}
        </Button>
      ),
    });
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Licenses"
        description="View distribution agreements and manage licensing contracts."
        actions={
          canCreate ? (
            <Button onClick={() => setSheetOpen(true)}>Create license</Button>
          ) : undefined
        }
      />

      <div className="grid gap-4 rounded-xl border p-4 md:grid-cols-3">
        <div className="space-y-2">
          <Label htmlFor="license-status">Status</Label>
          <Select
            id="license-status"
            value={statusFilter}
            onChange={(e) => {
              setPage(0);
              setStatusFilter(e.target.value as LicenseStatus | "");
            }}
          >
            <option value="">All statuses</option>
            <option value="ACTIVE">Active</option>
            <option value="EXPIRED">Expired</option>
          </Select>
        </div>
        <div className="flex items-end md:col-span-2">
          <Button
            variant="outline"
            onClick={() => {
              setStatusFilter("");
              setPage(0);
            }}
          >
            Clear filters
          </Button>
        </div>
      </div>

      {isLoading ? (
        <LoadingState message="Loading licenses..." />
      ) : (
        <>
          <DataTable
            columns={columns}
            data={licenses}
            keyExtractor={(license) => license.id}
            emptyMessage="No licenses found."
          />
          <Pagination
            page={page}
            totalPages={totalPages}
            totalElements={totalElements}
            onPageChange={setPage}
          />
        </>
      )}

      <Sheet open={sheetOpen} onOpenChange={setSheetOpen}>
        <SheetContent side="right" className="overflow-y-auto sm:max-w-md">
          <SheetHeader>
            <SheetTitle>Create license</SheetTitle>
            <SheetDescription>
              Issue a distribution agreement linking a movie to a client.
            </SheetDescription>
          </SheetHeader>
          <form onSubmit={handleSubmit} className="flex flex-col gap-4 px-4">
            <div className="space-y-2">
              <Label htmlFor="license-movie">Movie</Label>
              <Select
                id="license-movie"
                required
                value={form.movieId}
                onChange={(e) => setForm({ ...form, movieId: e.target.value })}
              >
                <option value="">Select movie</option>
                {movies.map((movie) => (
                  <option key={movie.id} value={movie.id}>
                    {movie.title}
                  </option>
                ))}
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="license-client">Client</Label>
              <Select
                id="license-client"
                required
                value={form.clientId}
                onChange={(e) =>
                  setForm({ ...form, clientId: e.target.value })
                }
              >
                <option value="">Select client</option>
                {clients.map((client) => (
                  <option key={client.id} value={client.id}>
                    {client.organizationName}
                  </option>
                ))}
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="license-start">Start date</Label>
              <Input
                id="license-start"
                type="date"
                required
                value={form.startDate}
                onChange={(e) =>
                  setForm({ ...form, startDate: e.target.value })
                }
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="license-end">End date</Label>
              <Input
                id="license-end"
                type="date"
                required
                value={form.endDate}
                onChange={(e) => setForm({ ...form, endDate: e.target.value })}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="license-terms">Terms</Label>
              <Textarea
                id="license-terms"
                value={form.terms}
                onChange={(e) => setForm({ ...form, terms: e.target.value })}
              />
            </div>
            <SheetFooter>
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Creating..." : "Create license"}
              </Button>
            </SheetFooter>
          </form>
        </SheetContent>
      </Sheet>
    </div>
  );
}
