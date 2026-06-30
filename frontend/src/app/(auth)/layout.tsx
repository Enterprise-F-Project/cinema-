import { Clapperboard, Film, Sparkles, Star } from "lucide-react";

import { siteConfig } from "@/config/site";

const perks = [
  "Secure JWT authentication",
  "Role-based dashboards",
  "Real-time catalog sync",
];

export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="grid min-h-screen lg:grid-cols-2">
      <div className="relative hidden flex-col justify-between overflow-hidden bg-brand-gradient p-10 text-white lg:flex">
        <div className="bg-brand-radial film-grain pointer-events-none absolute inset-0" />
        <div className="pointer-events-none absolute -right-20 top-1/3 size-72 rounded-full bg-primary/15 blur-3xl" />

        <div className="relative z-10 flex items-center gap-3">
          <div className="flex size-10 items-center justify-center rounded-xl bg-primary/20 ring-1 ring-primary/30">
            <Clapperboard className="size-5 text-primary" />
          </div>
          <span className="font-heading text-xl font-semibold">
            {siteConfig.name}
          </span>
        </div>

        <div className="relative z-10 space-y-6">
          <div className="inline-flex items-center gap-2 rounded-full border border-primary/30 bg-primary/10 px-3 py-1 text-xs font-medium text-primary">
            <Sparkles className="size-3.5" />
            Premium cinema platform
          </div>
          <h2 className="font-heading text-4xl font-semibold leading-tight tracking-tight">
            Streamline movie
            <br />
            <span className="text-gradient-brand">distribution</span>
          </h2>
          <p className="max-w-md text-sm leading-relaxed text-white/60">
            Manage licenses, track rentals, and control catalog availability for
            distributors, cinemas, and administrators — all in one elegant
            workspace.
          </p>
          <ul className="space-y-3">
            {perks.map((perk) => (
              <li key={perk} className="flex items-center gap-2.5 text-sm text-white/70">
                <Star className="size-3.5 shrink-0 fill-primary text-primary" />
                {perk}
              </li>
            ))}
          </ul>
        </div>

        <div className="relative z-10 flex items-center justify-between">
          <p className="text-xs text-white/40">
            Enterprise Application Development · Capstone Project
          </p>
          <Film className="size-5 text-primary/40" />
        </div>
      </div>

      <div className="relative flex items-center justify-center bg-background p-6 md:p-10">
        <div className="bg-brand-radial pointer-events-none absolute inset-0 opacity-40" />
        <div className="relative w-full max-w-md">{children}</div>
      </div>
    </div>
  );
}
