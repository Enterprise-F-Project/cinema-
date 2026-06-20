import { EmptyState } from "@/components/layout/empty-state";
import { PageHeader } from "@/components/layout/page-header";
import { Button } from "@/components/ui/button";

export default function RentalsPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Rentals"
        description="Track rental requests from REQUESTED through ACTIVE to COMPLETED."
        actions={<Button>Request rental</Button>}
      />
      <EmptyState
        title="Rental workflow"
        description="Connect this page to GET /api/rentals and POST /api/rentals for client rental operations."
      />
    </div>
  );
}
