import { Badge } from "@/components/ui/badge";
import type {
  LicenseStatus,
  MovieAvailabilityStatus,
  RentalStatus,
} from "@/types";

const movieStatusVariant: Record<
  MovieAvailabilityStatus,
  "default" | "secondary" | "outline"
> = {
  AVAILABLE: "default",
  RENTED: "secondary",
};

const licenseStatusVariant: Record<
  LicenseStatus,
  "default" | "secondary" | "destructive"
> = {
  ACTIVE: "default",
  EXPIRED: "destructive",
};

const rentalStatusVariant: Record<
  RentalStatus,
  "default" | "secondary" | "outline"
> = {
  REQUESTED: "outline",
  ACTIVE: "default",
  COMPLETED: "secondary",
};

function formatStatusLabel(status: string): string {
  return status.charAt(0) + status.slice(1).toLowerCase();
}

export function MovieStatusBadge({
  status,
}: {
  status: MovieAvailabilityStatus;
}) {
  return (
    <Badge variant={movieStatusVariant[status]}>
      {formatStatusLabel(status)}
    </Badge>
  );
}

export function LicenseStatusBadge({ status }: { status: LicenseStatus }) {
  return (
    <Badge variant={licenseStatusVariant[status]}>
      {formatStatusLabel(status)}
    </Badge>
  );
}

export function RentalStatusBadge({ status }: { status: RentalStatus }) {
  return (
    <Badge variant={rentalStatusVariant[status]}>
      {formatStatusLabel(status)}
    </Badge>
  );
}

export function ActiveBadge({ active }: { active: boolean }) {
  return (
    <Badge variant={active ? "default" : "destructive"}>
      {active ? "Active" : "Inactive"}
    </Badge>
  );
}
