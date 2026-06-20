import { EmptyState } from "@/components/layout/empty-state";
import { PageHeader } from "@/components/layout/page-header";
import { Button } from "@/components/ui/button";

export default function LicensesPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Licenses"
        description="Create and monitor distribution agreements between distributors, movies, and clients."
        actions={<Button>Create license</Button>}
      />
      <EmptyState
        title="License management"
        description="Connect this page to GET /api/licenses and POST /api/licenses for agreement workflows."
      />
    </div>
  );
}
