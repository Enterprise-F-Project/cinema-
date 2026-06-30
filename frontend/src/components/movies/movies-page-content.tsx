"use client";

import { Pencil, Trash2 } from "lucide-react";
import { useCallback, useEffect, useState } from "react";
import { toast } from "sonner";

import { PageHeader } from "@/components/layout/page-header";
import { DataTable, type Column } from "@/components/shared/data-table";
import { LoadingState } from "@/components/shared/loading-state";
import { Pagination } from "@/components/shared/pagination";
import { MovieStatusBadge } from "@/components/shared/status-badge";
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
import { ApiError, moviesApi } from "@/lib/api";
import { formatDate } from "@/lib/format";
import { useAuth } from "@/providers/auth-provider";
import type { Movie, MovieAvailabilityStatus } from "@/types";

const emptyForm = {
  title: "",
  genre: "",
  releaseDate: "",
  duration: "",
  description: "",
};

export function MoviesPageContent() {
  const { role } = useAuth();
  const canManage = role === "ADMIN" || role === "DISTRIBUTOR";
  const canDelete = role === "ADMIN";

  const [movies, setMovies] = useState<Movie[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);

  const [titleFilter, setTitleFilter] = useState("");
  const [genreFilter, setGenreFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState<MovieAvailabilityStatus | "">(
    "",
  );

  const [sheetOpen, setSheetOpen] = useState(false);
  const [editingMovie, setEditingMovie] = useState<Movie | null>(null);
  const [form, setForm] = useState(emptyForm);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const loadMovies = useCallback(async () => {
    setIsLoading(true);
    try {
      const response = await moviesApi.list({
        page,
        size: 10,
        sort: "createdAt,desc",
        title: titleFilter || undefined,
        genre: genreFilter || undefined,
        availabilityStatus: statusFilter || undefined,
      });
      setMovies(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Failed to load movies";
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  }, [page, titleFilter, genreFilter, statusFilter]);

  useEffect(() => {
    void loadMovies();
  }, [loadMovies]);

  function openCreate() {
    setEditingMovie(null);
    setForm(emptyForm);
    setSheetOpen(true);
  }

  function openEdit(movie: Movie) {
    setEditingMovie(movie);
    setForm({
      title: movie.title,
      genre: movie.genre,
      releaseDate: movie.releaseDate,
      duration: String(movie.duration),
      description: movie.description ?? "",
    });
    setSheetOpen(true);
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setIsSubmitting(true);

    const payload = {
      title: form.title,
      genre: form.genre,
      releaseDate: form.releaseDate,
      duration: Number(form.duration),
      description: form.description || undefined,
    };

    try {
      if (editingMovie) {
        await moviesApi.update(editingMovie.id, payload);
        toast.success("Movie updated");
      } else {
        await moviesApi.create(payload);
        toast.success("Movie created");
      }
      setSheetOpen(false);
      await loadMovies();
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Failed to save movie";
      toast.error(message);
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleStatusToggle(movie: Movie) {
    const nextStatus: MovieAvailabilityStatus =
      movie.availabilityStatus === "AVAILABLE" ? "RENTED" : "AVAILABLE";

    try {
      await moviesApi.updateStatus(movie.id, { availabilityStatus: nextStatus });
      toast.success("Status updated");
      await loadMovies();
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Failed to update status";
      toast.error(message);
    }
  }

  async function handleDelete(movie: Movie) {
    if (!confirm(`Delete "${movie.title}"?`)) return;

    try {
      await moviesApi.remove(movie.id);
      toast.success("Movie deleted");
      await loadMovies();
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Failed to delete movie";
      toast.error(message);
    }
  }

  const columns: Column<Movie>[] = [
    {
      key: "title",
      header: "Title",
      render: (movie) => (
        <div>
          <p className="font-medium">{movie.title}</p>
          <p className="text-xs text-muted-foreground">{movie.genre}</p>
        </div>
      ),
    },
    {
      key: "releaseDate",
      header: "Release",
      render: (movie) => formatDate(movie.releaseDate),
    },
    {
      key: "duration",
      header: "Duration",
      render: (movie) => `${movie.duration} min`,
    },
    {
      key: "status",
      header: "Status",
      render: (movie) => (
        <MovieStatusBadge status={movie.availabilityStatus} />
      ),
    },
    {
      key: "distributor",
      header: "Distributor",
      render: (movie) => movie.distributorName,
    },
  ];

  if (canManage) {
    columns.push({
      key: "actions",
      header: "Actions",
      className: "w-48",
      render: (movie) => (
        <div className="flex flex-wrap gap-2">
          <Button variant="outline" size="sm" onClick={() => openEdit(movie)}>
            <Pencil />
            Edit
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => void handleStatusToggle(movie)}
          >
            Toggle status
          </Button>
          {canDelete && (
            <Button
              variant="destructive"
              size="sm"
              onClick={() => void handleDelete(movie)}
            >
              <Trash2 />
            </Button>
          )}
        </div>
      ),
    });
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Movies"
        description="Browse the catalog, filter by genre or availability, and manage distributor titles."
        actions={
          canManage ? (
            <Button onClick={openCreate}>Add movie</Button>
          ) : undefined
        }
      />

      <div className="grid gap-4 rounded-xl border p-4 md:grid-cols-4">
        <div className="space-y-2">
          <Label htmlFor="title-filter">Title</Label>
          <Input
            id="title-filter"
            placeholder="Search title..."
            value={titleFilter}
            onChange={(e) => {
              setPage(0);
              setTitleFilter(e.target.value);
            }}
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="genre-filter">Genre</Label>
          <Input
            id="genre-filter"
            placeholder="Filter genre..."
            value={genreFilter}
            onChange={(e) => {
              setPage(0);
              setGenreFilter(e.target.value);
            }}
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="status-filter">Availability</Label>
          <Select
            id="status-filter"
            value={statusFilter}
            onChange={(e) => {
              setPage(0);
              setStatusFilter(e.target.value as MovieAvailabilityStatus | "");
            }}
          >
            <option value="">All statuses</option>
            <option value="AVAILABLE">Available</option>
            <option value="RENTED">Rented</option>
          </Select>
        </div>
        <div className="flex items-end">
          <Button
            variant="outline"
            className="w-full"
            onClick={() => {
              setTitleFilter("");
              setGenreFilter("");
              setStatusFilter("");
              setPage(0);
            }}
          >
            Clear filters
          </Button>
        </div>
      </div>

      {isLoading ? (
        <LoadingState message="Loading movies..." />
      ) : (
        <>
          <DataTable
            columns={columns}
            data={movies}
            keyExtractor={(movie) => movie.id}
            emptyMessage="No movies match your filters."
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
            <SheetTitle>
              {editingMovie ? "Edit movie" : "Add movie"}
            </SheetTitle>
            <SheetDescription>
              {editingMovie
                ? "Update movie details in the catalog."
                : "Add a new title to the distribution catalog."}
            </SheetDescription>
          </SheetHeader>
          <form onSubmit={handleSubmit} className="flex flex-col gap-4 px-4">
            <div className="space-y-2">
              <Label htmlFor="movie-title">Title</Label>
              <Input
                id="movie-title"
                required
                value={form.title}
                onChange={(e) => setForm({ ...form, title: e.target.value })}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="movie-genre">Genre</Label>
              <Input
                id="movie-genre"
                required
                value={form.genre}
                onChange={(e) => setForm({ ...form, genre: e.target.value })}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="movie-release">Release date</Label>
              <Input
                id="movie-release"
                type="date"
                required
                value={form.releaseDate}
                onChange={(e) =>
                  setForm({ ...form, releaseDate: e.target.value })
                }
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="movie-duration">Duration (minutes)</Label>
              <Input
                id="movie-duration"
                type="number"
                min={1}
                required
                value={form.duration}
                onChange={(e) => setForm({ ...form, duration: e.target.value })}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="movie-description">Description</Label>
              <Textarea
                id="movie-description"
                value={form.description}
                onChange={(e) =>
                  setForm({ ...form, description: e.target.value })
                }
              />
            </div>
            <SheetFooter>
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting
                  ? "Saving..."
                  : editingMovie
                    ? "Save changes"
                    : "Create movie"}
              </Button>
            </SheetFooter>
          </form>
        </SheetContent>
      </Sheet>
    </div>
  );
}
