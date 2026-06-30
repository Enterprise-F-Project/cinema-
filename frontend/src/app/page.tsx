import Link from "next/link";
import {
  ArrowRight,
  Clapperboard,
  FileKey,
  Film,
  Shield,
  Sparkles,
  Star,
  Video,
} from "lucide-react";

import { siteConfig } from "@/config/site";
import { buttonVariants } from "@/components/ui/button-variants";
import { cn } from "@/lib/utils";

const features = [
  {
    title: "Movie catalog",
    description:
      "Distributors manage titles, genres, availability, and release details with a refined workspace.",
    icon: Film,
  },
  {
    title: "License management",
    description:
      "Create distribution agreements with clients and track active contracts in real time.",
    icon: FileKey,
  },
  {
    title: "Rental workflow",
    description:
      "Clients rent movies with elegant status tracking from request to completion.",
    icon: Video,
  },
  {
    title: "Role-based access",
    description:
      "Tailored experiences for administrators, distributors, and cinema clients.",
    icon: Shield,
  },
];

const highlights = [
  "Enterprise-grade security",
  "Real-time analytics",
  "Multi-role workflows",
];

export default function HomePage() {
  return (
    <div className="relative min-h-screen overflow-hidden bg-brand-gradient text-white">
      <div className="bg-brand-radial film-grain pointer-events-none absolute inset-0" />
      <div className="pointer-events-none absolute -left-32 top-20 size-96 rounded-full bg-primary/10 blur-3xl" />
      <div className="pointer-events-none absolute -right-32 bottom-20 size-96 rounded-full bg-primary/8 blur-3xl" />

      <header className="relative z-10 border-b border-white/8 bg-black/20 backdrop-blur-xl">
        <div className="mx-auto flex h-16 max-w-6xl items-center justify-between px-4 md:px-6">
          <div className="flex items-center gap-3">
            <div className="flex size-9 items-center justify-center rounded-lg bg-primary/20 ring-1 ring-primary/30">
              <Clapperboard className="size-5 text-primary" />
            </div>
            <span className="font-heading text-lg font-semibold tracking-tight">
              {siteConfig.name}
            </span>
          </div>
          <div className="flex items-center gap-2">
            <Link
              href="/login"
              className={cn(
                buttonVariants({ variant: "ghost" }),
                "text-white/80 hover:bg-white/10 hover:text-white",
              )}
            >
              Sign in
            </Link>
            <Link
              href="/register"
              className={cn(buttonVariants(), "hover-glow gap-2")}
            >
              Get started
              <ArrowRight className="size-4 transition-transform group-hover/button:translate-x-0.5" />
            </Link>
          </div>
        </div>
      </header>

      <main className="relative z-10 mx-auto max-w-6xl px-4 pb-24 pt-20 md:px-6 md:pt-28">
        <section className="mx-auto max-w-3xl text-center">
          <div className="animate-fade-up mb-6 inline-flex items-center gap-2 rounded-full border border-primary/30 bg-primary/10 px-4 py-1.5 text-xs font-medium text-primary backdrop-blur-sm">
            <Sparkles className="size-3.5" />
            Movie Distribution & Rental Management
          </div>
          <h1 className="animate-fade-up-delay-1 font-heading text-5xl font-semibold leading-[1.1] tracking-tight md:text-7xl">
            <span className="text-gradient-brand">{siteConfig.name}</span>
          </h1>
          <p className="animate-fade-up-delay-2 mx-auto mt-6 max-w-2xl text-lg leading-relaxed text-white/65 md:text-xl">
            {siteConfig.tagline}
          </p>
          <div className="animate-fade-up-delay-3 mt-10 flex flex-wrap items-center justify-center gap-3">
            <Link
              href="/register"
              className={cn(buttonVariants({ size: "lg" }), "hover-glow h-11 px-6")}
            >
              Create account
              <ArrowRight />
            </Link>
            <Link
              href="/login"
              className={cn(
                buttonVariants({ size: "lg", variant: "outline" }),
                "h-11 border-white/20 bg-white/5 px-6 text-white backdrop-blur-sm hover:bg-white/10 hover:text-white",
              )}
            >
              Sign in
            </Link>
          </div>
          <div className="animate-fade-up-delay-3 mt-10 flex flex-wrap items-center justify-center gap-6 text-sm text-white/50">
            {highlights.map((item) => (
              <span key={item} className="flex items-center gap-2">
                <Star className="size-3.5 fill-primary/80 text-primary" />
                {item}
              </span>
            ))}
          </div>
        </section>

        <section className="mt-24 grid gap-5 md:grid-cols-2">
          {features.map((feature, index) => {
            const Icon = feature.icon;
            return (
              <div
                key={feature.title}
                className={cn(
                  "group glass-panel hover-lift rounded-2xl p-6 md:p-8",
                  index % 2 === 0 ? "animate-fade-up" : "animate-fade-up-delay-1",
                )}
              >
                <div className="icon-box mb-5">
                  <Icon className="size-5" />
                </div>
                <h3 className="font-heading text-xl font-semibold text-white">
                  {feature.title}
                </h3>
                <p className="mt-2 text-sm leading-relaxed text-white/60">
                  {feature.description}
                </p>
              </div>
            );
          })}
        </section>

        <section className="mt-24 overflow-hidden rounded-3xl border border-primary/20 bg-primary/10 p-8 text-center backdrop-blur-sm md:p-12">
          <h2 className="font-heading text-2xl font-semibold text-white md:text-3xl">
            Ready to manage your cinema empire?
          </h2>
          <p className="mx-auto mt-3 max-w-lg text-sm text-white/60 md:text-base">
            Join distributors and cinema clients already using the platform to
            streamline licensing and rentals.
          </p>
          <Link
            href="/register"
            className={cn(buttonVariants({ size: "lg" }), "hover-glow mt-8")}
          >
            Start for free
            <ArrowRight />
          </Link>
        </section>
      </main>

      <footer className="relative z-10 border-t border-white/8 py-6 text-center text-xs text-white/40">
        Enterprise Application Development · Capstone Project
      </footer>
    </div>
  );
}
