import Link from "next/link";
import type { LucideIcon } from "lucide-react";
import { ArrowUpRight } from "lucide-react";

import { cn } from "@/lib/utils";

interface QuickLinkCardProps {
  href: string;
  title: string;
  description: string;
  icon: LucideIcon;
  className?: string;
}

export function QuickLinkCard({
  href,
  title,
  description,
  icon: Icon,
  className,
}: QuickLinkCardProps) {
  return (
    <Link
      href={href}
      className={cn(
        "group glass-card hover-lift block p-5 no-underline",
        className,
      )}
    >
      <div className="flex items-start justify-between gap-4">
        <div className="icon-box">
          <Icon className="size-5" />
        </div>
        <ArrowUpRight className="size-4 text-muted-foreground transition-all duration-300 group-hover:-translate-y-0.5 group-hover:translate-x-0.5 group-hover:text-primary" />
      </div>
      <h3 className="font-heading mt-4 text-base font-semibold text-foreground">
        {title}
      </h3>
      <p className="mt-1.5 text-sm leading-relaxed text-muted-foreground">
        {description}
      </p>
      <span className="mt-4 inline-flex items-center gap-1 text-sm font-medium text-primary opacity-0 transition-all duration-300 group-hover:opacity-100">
        Open workspace
        <ArrowUpRight className="size-3.5" />
      </span>
    </Link>
  );
}
