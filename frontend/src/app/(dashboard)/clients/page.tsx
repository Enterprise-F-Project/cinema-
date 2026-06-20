import { EmptyState } from "@/components/layout/empty-state";
import { PageHeader } from "@/components/layout/page-header";
import { Button } from "@/components/ui/button";

export default function ClientsPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Clients"
        description="Manage cinema and platform organizations that receive licenses and place rentals."
        actions={<Button>Add client</Button>}
      />
      <EmptyState
        title="Client directory"
        description="Connect this page to GET /api/clients and POST /api/clients for organization management."
      />
    </div>
  );
}
