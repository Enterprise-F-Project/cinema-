import type { ReactNode } from "react";
import type { LucideIcon } from "lucide-react";

import { cn } from "@/lib/utils";

interface RoleHeroProps {
  icon: LucideIcon;
  badge: string;
  title: string;
  description: string;
  footer?: ReactNode;
  className?: string;
  variant?: "default" | "admin" | "distributor" | "client";
}

const variantStyles = {
  default: "from-primary/10 via-transparent to-chart-2/5",
  admin: "from-primary/12 via-chart-4/5 to-transparent",
  distributor: "from-chart-2/10 via-primary/8 to-transparent",
  client: "from-chart-3/10 via-primary/8 to-transparent",
};

export function RoleHero({
  icon: Icon,
  badge,
  title,
  description,
  footer,
  className,
  variant = "default",
}: RoleHeroProps) {
  return (
    <div
      className={cn(
        "glass-card animate-fade-up relative overflow-hidden p-6 md:p-8",
        className,
      )}
    >
      <div
        className={cn(
          "pointer-events-none absolute inset-0 bg-gradient-to-br opacity-80",
          variantStyles[variant],
        )}
      />
      <div className="bg-brand-radial pointer-events-none absolute inset-0 opacity-40" />
      <div className="relative flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div className="flex items-start gap-4">
          <div className="icon-box size-14 shrink-0 rounded-2xl">
            <Icon className="size-6" />
          </div>
          <div>
            <div className="mb-2 inline-flex items-center gap-1.5 rounded-full border border-primary/25 bg-primary/10 px-2.5 py-0.5 text-xs font-semibold text-primary">
              {badge}
            </div>
            <h2 className="text-xl font-bold tracking-tight md:text-2xl">
              {title}
            </h2>
            <p className="mt-1 max-w-xl text-sm leading-relaxed text-muted-foreground">
              {description}
            </p>
          </div>
        </div>
        {footer}
      </div>
    </div>
  );
}
