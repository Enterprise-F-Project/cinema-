"use client";

import { Pencil } from "lucide-react";
import { useCallback, useEffect, useState } from "react";
import { toast } from "sonner";

import { PageHeader } from "@/components/layout/page-header";
import { DataTable, type Column } from "@/components/shared/data-table";
import { LoadingState } from "@/components/shared/loading-state";
import { Pagination } from "@/components/shared/pagination";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { Textarea } from "@/components/ui/textarea";
import { ApiError, clientsApi } from "@/lib/api";
import { formatDate } from "@/lib/format";
import { useAuth } from "@/providers/auth-provider";
import type { Client } from "@/types";

const emptyForm = {
  organizationName: "",
  email: "",
  phone: "",
  address: "",
};

export function ClientsPageContent() {
  const { role } = useAuth();
  const canCreate = role === "ADMIN";
  const canEdit = role === "ADMIN";

  const [clients, setClients] = useState<Client[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);

  const [sheetOpen, setSheetOpen] = useState(false);
  const [editingClient, setEditingClient] = useState<Client | null>(null);
  const [form, setForm] = useState(emptyForm);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const loadClients = useCallback(async () => {
    setIsLoading(true);
    try {
      const response = await clientsApi.list({
        page,
        size: 10,
        sort: "createdAt,desc",
      });
      setClients(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Failed to load clients";
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  }, [page]);

  useEffect(() => {
    void loadClients();
  }, [loadClients]);

  function openCreate() {
    setEditingClient(null);
    setForm(emptyForm);
    setSheetOpen(true);
  }

  function openEdit(client: Client) {
    setEditingClient(client);
    setForm({
      organizationName: client.organizationName,
      email: client.email,
      phone: client.phone ?? "",
      address: client.address ?? "",
    });
    setSheetOpen(true);
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setIsSubmitting(true);

    const payload = {
      organizationName: form.organizationName,
      email: form.email,
      phone: form.phone || undefined,
      address: form.address || undefined,
    };

    try {
      if (editingClient) {
        await clientsApi.update(editingClient.id, payload);
        toast.success("Client updated");
      } else {
        await clientsApi.create(payload);
        toast.success("Client created");
      }
      setSheetOpen(false);
      await loadClients();
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Failed to save client";
      toast.error(message);
    } finally {
      setIsSubmitting(false);
    }
  }

  const columns: Column<Client>[] = [
    {
      key: "organization",
      header: "Organization",
      render: (client) => (
        <div>
          <p className="font-medium">{client.organizationName}</p>
          <p className="text-xs text-muted-foreground">{client.email}</p>
        </div>
      ),
    },
    {
      key: "phone",
      header: "Phone",
      render: (client) => client.phone ?? "—",
    },
    {
      key: "address",
      header: "Address",
      render: (client) => client.address ?? "—",
    },
    {
      key: "createdAt",
      header: "Created",
      render: (client) => formatDate(client.createdAt),
    },
  ];

  if (canEdit) {
    columns.push({
      key: "actions",
      header: "Actions",
      render: (client) => (
        <Button variant="outline" size="sm" onClick={() => openEdit(client)}>
          <Pencil />
          Edit
        </Button>
      ),
    });
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Clients"
        description="Manage cinema and platform organizations."
        actions={
          canCreate ? (
            <Button onClick={openCreate}>Add client</Button>
          ) : undefined
        }
      />

      {isLoading ? (
        <LoadingState message="Loading clients..." />
      ) : (
        <>
          <DataTable
            columns={columns}
            data={clients}
            keyExtractor={(client) => client.id}
            emptyMessage="No clients found."
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
              {editingClient ? "Edit client" : "Add client"}
            </SheetTitle>
            <SheetDescription>
              {editingClient
                ? "Update client organization details."
                : "Register a new cinema or platform organization."}
            </SheetDescription>
          </SheetHeader>
          <form onSubmit={handleSubmit} className="flex flex-col gap-4 px-4">
            <div className="space-y-2">
              <Label htmlFor="client-org">Organization name</Label>
              <Input
                id="client-org"
                required
                value={form.organizationName}
                onChange={(e) =>
                  setForm({ ...form, organizationName: e.target.value })
                }
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="client-email">Email</Label>
              <Input
                id="client-email"
                type="email"
                required
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="client-phone">Phone</Label>
              <Input
                id="client-phone"
                value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="client-address">Address</Label>
              <Textarea
                id="client-address"
                value={form.address}
                onChange={(e) => setForm({ ...form, address: e.target.value })}
              />
            </div>
            <SheetFooter>
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting
                  ? "Saving..."
                  : editingClient
                    ? "Save changes"
                    : "Create client"}
              </Button>
            </SheetFooter>
          </form>
        </SheetContent>
      </Sheet>
    </div>
  );
}
