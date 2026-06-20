import { EmptyState } from "@/components/layout/empty-state";
import { PageHeader } from "@/components/layout/page-header";

export default function AdminUsersPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Users"
        description="Review registered accounts and activate or deactivate user access."
      />
      <EmptyState
        title="User management"
        description="Connect this page to GET /api/admin/users and PATCH /api/admin/users/{id}/status."
      />
    </div>
  );
}
