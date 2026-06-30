import type { LucideIcon } from "lucide-react";

import { cn } from "@/lib/utils";

interface StatCardProps {
  label: string;
  value: number | string;
  icon?: LucideIcon;
  trend?: string;
  className?: string;
  delay?: number;
}

export function StatCard({
  label,
  value,
  icon: Icon,
  trend,
  className,
  delay = 0,
}: StatCardProps) {
  return (
    <div
      className={cn(
        "group glass-card hover-lift relative overflow-hidden p-5",
        className,
      )}
      style={{ animationDelay: `${delay}ms` }}
    >
      <div className="absolute -right-4 -top-4 size-24 rounded-full bg-primary/5 transition-transform duration-500 group-hover:scale-150" />
      <div className="relative flex items-start justify-between gap-3">
        <div className="space-y-2">
          <p className="text-xs font-medium uppercase tracking-wider text-muted-foreground">
            {label}
          </p>
          <p className="font-heading text-3xl font-semibold tracking-tight text-foreground">
            {value}
          </p>
          {trend ? (
            <p className="text-xs text-muted-foreground">{trend}</p>
          ) : null}
        </div>
        {Icon ? (
          <div className="icon-box size-10 shrink-0 rounded-lg">
            <Icon className="size-5" />
          </div>
        ) : null}
      </div>
    </div>
  );
}
