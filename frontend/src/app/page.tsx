import Link from "next/link";
import { ArrowRight, Clapperboard, FileKey, Film, Shield } from "lucide-react";

import { siteConfig } from "@/config/site";
import { buttonVariants } from "@/components/ui/button-variants";
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { cn } from "@/lib/utils";

const features = [
  {
    title: "Movie catalog",
    description:
      "Distributors manage titles, genres, availability, and release details.",
    icon: Film,
  },
  {
    title: "License management",
    description:
      "Create distribution agreements with clients and track active contracts.",
    icon: FileKey,
  },
  {
    title: "Rental workflow",
    description:
      "Clients rent movies with status tracking from request to completion.",
    icon: Clapperboard,
  },
  {
    title: "Role-based access",
    description:
      "Separate experiences for administrators, distributors, and clients.",
    icon: Shield,
  },
];

export default function HomePage() {
  return (
    <div className="min-h-screen bg-background">
      <header className="border-b">
        <div className="mx-auto flex h-16 max-w-6xl items-center justify-between px-4 md:px-6">
          <div className="flex items-center gap-2">
            <Clapperboard className="size-5" />
            <span className="font-semibold">{siteConfig.name}</span>
          </div>
          <div className="flex items-center gap-2">
            <Link href="/login" className={cn(buttonVariants({ variant: "ghost" }))}>
              Sign in
            </Link>
            <Link href="/register" className={cn(buttonVariants())}>
              Get started
              <ArrowRight />
            </Link>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-4 py-16 md:px-6">
        <section className="mx-auto max-w-3xl text-center">
          <p className="mb-3 text-sm font-medium text-muted-foreground">
            Movie Distribution & Rental Management
          </p>
          <h1 className="text-4xl font-semibold tracking-tight md:text-5xl">
            {siteConfig.name}
          </h1>
          <p className="mt-4 text-lg text-muted-foreground">
            {siteConfig.tagline}
          </p>
          <div className="mt-8 flex flex-wrap items-center justify-center gap-3">
            <Link href="/register" className={cn(buttonVariants({ size: "lg" }))}>
              Create account
            </Link>
            <Link
              href="/login"
              className={cn(buttonVariants({ size: "lg", variant: "outline" }))}
            >
              Sign in
            </Link>
          </div>
        </section>

        <section className="mt-16 grid gap-4 md:grid-cols-2">
          {features.map((feature) => {
            const Icon = feature.icon;
            return (
              <Card key={feature.title}>
                <CardHeader>
                  <div className="mb-2 flex size-10 items-center justify-center rounded-lg bg-muted">
                    <Icon className="size-5" />
                  </div>
                  <CardTitle>{feature.title}</CardTitle>
                  <CardDescription>{feature.description}</CardDescription>
                </CardHeader>
              </Card>
            );
          })}
        </section>
      </main>
    </div>
  );
}
