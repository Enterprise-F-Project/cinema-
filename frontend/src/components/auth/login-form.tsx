"use client";

import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { useState } from "react";
import { LogIn } from "lucide-react";
import { toast } from "sonner";

import { siteConfig } from "@/config/site";
import { ApiError } from "@/lib/api";
import { useAuth } from "@/providers/auth-provider";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { cn } from "@/lib/utils";

export function LoginForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setIsSubmitting(true);

    try {
      await login({ email, password });
      toast.success("Welcome back");
      const from = searchParams.get("from");
      router.push(from && from !== "/login" ? from : "/dashboard");
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Unable to sign in";
      toast.error(message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="glass-card animate-fade-up overflow-hidden p-0">
      <div className="border-b border-border/60 bg-primary/5 px-6 py-8 dark:bg-primary/8">
        <div className="mb-4 flex size-12 items-center justify-center rounded-xl bg-primary/15 ring-1 ring-primary/25">
          <LogIn className="size-5 text-primary" />
        </div>
        <h1 className="font-heading text-2xl font-semibold tracking-tight">
          Welcome back
        </h1>
        <p className="mt-1.5 text-sm text-muted-foreground">
          Sign in to {siteConfig.name} to access your workspace.
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-5 p-6">
        <div className="space-y-2">
          <Label htmlFor="email">Email address</Label>
          <Input
            id="email"
            type="email"
            autoComplete="email"
            required
            placeholder="you@cinema.com"
            className={cn("h-10 bg-background/50 input-glow transition-all duration-300")}
            value={email}
            onChange={(event) => setEmail(event.target.value)}
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="password">Password</Label>
          <Input
            id="password"
            type="password"
            autoComplete="current-password"
            required
            placeholder="••••••••"
            className={cn("h-10 bg-background/50 input-glow transition-all duration-300")}
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
        </div>

        <Button
          type="submit"
          className="hover-glow h-10 w-full"
          disabled={isSubmitting}
        >
          {isSubmitting ? "Signing in..." : "Sign in"}
        </Button>

        <p className="text-center text-sm text-muted-foreground">
          No account?{" "}
          <Link
            href="/register"
            className="font-medium text-primary transition-colors hover:text-primary/80 hover:underline"
          >
            Create one
          </Link>
        </p>
      </form>
    </div>
  );
}
