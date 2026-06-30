"use client";

import { useCallback, useEffect, useState } from "react";
import { toast } from "sonner";

import { PageHeader } from "@/components/layout/page-header";
import { DataTable, type Column } from "@/components/shared/data-table";
import { LoadingState } from "@/components/shared/loading-state";
import { Pagination } from "@/components/shared/pagination";
import { RentalStatusBadge } from "@/components/shared/status-badge";
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
import { ApiError, moviesApi, rentalsApi } from "@/lib/api";
import { formatDate } from "@/lib/format";
import { useAuth } from "@/providers/auth-provider";
import type { Movie, Rental, RentalStatus } from "@/types";

const emptyForm = {
  movieId: "",
  rentalDate: "",
  returnDate: "",
};

const statusTransitions: Record<RentalStatus, RentalStatus[]> = {
  REQUESTED: ["ACTIVE", "COMPLETED"],
  ACTIVE: ["COMPLETED"],
  COMPLETED: [],
};

export function RentalsPageContent() {
  const { role } = useAuth();
  const canCreate = role === "CLIENT";
  const canUpdateStatus = role === "CLIENT" || role === "ADMIN";

  const [rentals, setRentals] = useState<Rental[]>([]);
  const [movies, setMovies] = useState<Movie[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);

  const [statusFilter, setStatusFilter] = useState<RentalStatus | "">("");
  const [sheetOpen, setSheetOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const loadRentals = useCallback(async () => {
    setIsLoading(true);
    try {
      const response = await rentalsApi.list({
        page,
        size: 10,
        sort: "createdAt,desc",
        status: statusFilter || undefined,
      });
      setRentals(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Failed to load rentals";
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  }, [page, statusFilter]);

  useEffect(() => {
    void loadRentals();
  }, [loadRentals]);

  useEffect(() => {
    if (!canCreate) return;

    async function loadMovies() {
      try {
        const response = await moviesApi.list({
          page: 0,
          size: 100,
          sort: "title,asc",
          availabilityStatus: "AVAILABLE",
        });
        setMovies(response.content);
      } catch {
        // Silent fail for movie options.
      }
    }

    void loadMovies();
  }, [canCreate]);

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setIsSubmitting(true);

    try {
      await rentalsApi.create({
        movieId: Number(form.movieId),
        rentalDate: form.rentalDate,
        returnDate: form.returnDate,
      });
      toast.success("Rental requested");
      setSheetOpen(false);
      setForm(emptyForm);
      await loadRentals();
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Failed to create rental";
      toast.error(message);
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleStatusUpdate(rental: Rental, status: RentalStatus) {
    try {
      await rentalsApi.updateStatus(rental.id, { status });
      toast.success("Rental status updated");
      await loadRentals();
    } catch (error) {
      const message =
        error instanceof ApiError
          ? error.message
          : "Failed to update rental status";
      toast.error(message);
    }
  }

  const columns: Column<Rental>[] = [
    {
      key: "movie",
      header: "Movie",
      render: (rental) => rental.movieTitle,
    },
    {
      key: "client",
      header: "Client",
      render: (rental) => rental.clientName,
    },
    {
      key: "period",
      header: "Rental period",
      render: (rental) =>
        `${formatDate(rental.rentalDate)} – ${formatDate(rental.returnDate)}`,
    },
    {
      key: "status",
      header: "Status",
      render: (rental) => <RentalStatusBadge status={rental.status} />,
    },
  ];

  if (canUpdateStatus) {
    columns.push({
      key: "actions",
      header: "Actions",
      render: (rental) => {
        const nextStatuses = statusTransitions[rental.status];
        if (nextStatuses.length === 0) return "—";

        return (
          <div className="flex flex-wrap gap-2">
            {nextStatuses.map((status) => (
              <Button
                key={status}
                variant="outline"
                size="sm"
                onClick={() => void handleStatusUpdate(rental, status)}
              >
                Mark {status.toLowerCase()}
              </Button>
            ))}
          </div>
        );
      },
    });
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Rentals"
        description="Track rental requests and manage rental workflow status."
        actions={
          canCreate ? (
            <Button onClick={() => setSheetOpen(true)}>Request rental</Button>
          ) : undefined
        }
      />

      <div className="grid gap-4 rounded-xl border p-4 md:grid-cols-3">
        <div className="space-y-2">
          <Label htmlFor="rental-status">Status</Label>
          <Select
            id="rental-status"
            value={statusFilter}
            onChange={(e) => {
              setPage(0);
              setStatusFilter(e.target.value as RentalStatus | "");
            }}
          >
            <option value="">All statuses</option>
            <option value="REQUESTED">Requested</option>
            <option value="ACTIVE">Active</option>
            <option value="COMPLETED">Completed</option>
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
        <LoadingState message="Loading rentals..." />
      ) : (
        <>
          <DataTable
            columns={columns}
            data={rentals}
            keyExtractor={(rental) => rental.id}
            emptyMessage="No rentals found."
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
            <SheetTitle>Request rental</SheetTitle>
            <SheetDescription>
              Request a movie rental. An active license is required.
            </SheetDescription>
          </SheetHeader>
          <form onSubmit={handleSubmit} className="flex flex-col gap-4 px-4">
            <div className="space-y-2">
              <Label htmlFor="rental-movie">Movie</Label>
              <Select
                id="rental-movie"
                required
                value={form.movieId}
                onChange={(e) => setForm({ ...form, movieId: e.target.value })}
              >
                <option value="">Select available movie</option>
                {movies.map((movie) => (
                  <option key={movie.id} value={movie.id}>
                    {movie.title}
                  </option>
                ))}
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="rental-start">Rental date</Label>
              <Input
                id="rental-start"
                type="date"
                required
                value={form.rentalDate}
                onChange={(e) =>
                  setForm({ ...form, rentalDate: e.target.value })
                }
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="rental-end">Return date</Label>
              <Input
                id="rental-end"
                type="date"
                required
                value={form.returnDate}
                onChange={(e) =>
                  setForm({ ...form, returnDate: e.target.value })
                }
              />
            </div>
            <SheetFooter>
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Submitting..." : "Submit request"}
              </Button>
            </SheetFooter>
          </form>
        </SheetContent>
      </Sheet>
    </div>
  );
}
