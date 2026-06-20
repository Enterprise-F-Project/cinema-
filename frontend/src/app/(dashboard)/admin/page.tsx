import Link from "next/link";

import { EmptyState } from "@/components/layout/empty-state";
import { PageHeader } from "@/components/layout/page-header";
import { Button } from "@/components/ui/button";

export default function AdminPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Administration"
        description="Platform-wide metrics, user management, and operational oversight."
        actions={
          <Button variant="outline" render={<Link href="/admin/users" />}>
            Manage users
          </Button>
        }
      />
      <EmptyState
        title="Admin dashboard"
        description="Connect this page to GET /api/admin/dashboard for totals on users, movies, clients, licenses, and rentals."
      />
    </div>
  );
}
