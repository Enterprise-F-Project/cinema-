import { EmptyState } from "@/components/layout/empty-state";
import { PageHeader } from "@/components/layout/page-header";
import { Button } from "@/components/ui/button";

export default function MoviesPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Movies"
        description="Browse the catalog, filter by genre or availability, and manage distributor titles."
        actions={<Button>Add movie</Button>}
      />
      <EmptyState
        title="Movie catalog"
        description="Connect this page to GET /api/movies to display paginated results with search and filter controls."
      />
    </div>
  );
}
