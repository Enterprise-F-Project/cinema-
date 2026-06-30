"use client";

import { useCallback, useEffect, useState } from "react";
import { toast } from "sonner";

import { PageHeader } from "@/components/layout/page-header";
import { DataTable, type Column } from "@/components/shared/data-table";
import { LoadingState } from "@/components/shared/loading-state";
import { Pagination } from "@/components/shared/pagination";
import { ActiveBadge } from "@/components/shared/status-badge";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import { adminApi, ApiError } from "@/lib/api";
import { formatRole } from "@/lib/auth/permissions";
import { formatDate } from "@/lib/format";
import type { Role, User } from "@/types";

export function AdminUsersContent() {
  const [users, setUsers] = useState<User[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [roleFilter, setRoleFilter] = useState<Role | "">("");
  const [activeFilter, setActiveFilter] = useState<"" | "true" | "false">("");

  const loadUsers = useCallback(async () => {
    setIsLoading(true);
    try {
      const response = await adminApi.listUsers({
        page,
        size: 10,
        sort: "createdAt,desc",
        role: roleFilter || undefined,
        active: activeFilter === "" ? undefined : activeFilter === "true",
      });
      setUsers(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Failed to load users";
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  }, [page, roleFilter, activeFilter]);

  useEffect(() => {
    void loadUsers();
  }, [loadUsers]);

  async function handleStatusToggle(user: User) {
    try {
      await adminApi.updateUserStatus(user.id, { active: !user.active });
      toast.success(user.active ? "User deactivated" : "User activated");
      await loadUsers();
    } catch (error) {
      const message =
        error instanceof ApiError
          ? error.message
          : "Failed to update user status";
      toast.error(message);
    }
  }

  const columns: Column<User>[] = [
    {
      key: "name",
      header: "User",
      render: (user) => (
        <div>
          <p className="font-medium">{user.fullName}</p>
          <p className="text-xs text-muted-foreground">{user.email}</p>
        </div>
      ),
    },
    {
      key: "role",
      header: "Role",
      render: (user) => <Badge variant="outline">{formatRole(user.role)}</Badge>,
    },
    {
      key: "status",
      header: "Status",
      render: (user) => <ActiveBadge active={user.active} />,
    },
    {
      key: "createdAt",
      header: "Joined",
      render: (user) => formatDate(user.createdAt),
    },
    {
      key: "actions",
      header: "Actions",
      render: (user) => (
        <Button
          variant="outline"
          size="sm"
          onClick={() => void handleStatusToggle(user)}
        >
          {user.active ? "Deactivate" : "Activate"}
        </Button>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <PageHeader
        title="Users"
        description="Review registered accounts and activate or deactivate user access."
      />

      <div className="grid gap-4 rounded-xl border p-4 md:grid-cols-3">
        <div className="space-y-2">
          <Label htmlFor="user-role">Role</Label>
          <Select
            id="user-role"
            value={roleFilter}
            onChange={(e) => {
              setPage(0);
              setRoleFilter(e.target.value as Role | "");
            }}
          >
            <option value="">All roles</option>
            <option value="ADMIN">Admin</option>
            <option value="DISTRIBUTOR">Distributor</option>
            <option value="CLIENT">Client</option>
          </Select>
        </div>
        <div className="space-y-2">
          <Label htmlFor="user-active">Account status</Label>
          <Select
            id="user-active"
            value={activeFilter}
            onChange={(e) => {
              setPage(0);
              setActiveFilter(e.target.value as "" | "true" | "false");
            }}
          >
            <option value="">All accounts</option>
            <option value="true">Active</option>
            <option value="false">Inactive</option>
          </Select>
        </div>
        <div className="flex items-end">
          <Button
            variant="outline"
            onClick={() => {
              setRoleFilter("");
              setActiveFilter("");
              setPage(0);
            }}
          >
            Clear filters
          </Button>
        </div>
      </div>

      {isLoading ? (
        <LoadingState message="Loading users..." />
      ) : (
        <>
          <DataTable
            columns={columns}
            data={users}
            keyExtractor={(user) => user.id}
            emptyMessage="No users found."
          />
          <Pagination
            page={page}
            totalPages={totalPages}
            totalElements={totalElements}
            onPageChange={setPage}
          />
        </>
      )}
    </div>
  );
}
