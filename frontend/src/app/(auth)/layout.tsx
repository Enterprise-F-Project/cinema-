import { Clapperboard } from "lucide-react";

import { siteConfig } from "@/config/site";

export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="grid min-h-screen lg:grid-cols-2">
      <div className="relative hidden flex-col justify-between bg-primary p-10 text-primary-foreground lg:flex">
        <div className="flex items-center gap-2">
          <Clapperboard className="size-5" />
          <span className="text-lg font-semibold">{siteConfig.name}</span>
        </div>
        <div className="space-y-4">
          <h2 className="text-3xl font-semibold tracking-tight">
            Streamline movie distribution
          </h2>
          <p className="max-w-md text-sm text-primary-foreground/80">
            Manage licenses, track rentals, and control catalog availability for
            distributors, cinemas, and administrators.
          </p>
        </div>
        <p className="text-xs text-primary-foreground/70">
          Enterprise Application Development · Capstone Project
        </p>
      </div>

      <div className="flex items-center justify-center p-6 md:p-10">
        {children}
      </div>
    </div>
  );
}
