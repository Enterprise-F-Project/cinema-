"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { UserPlus } from "lucide-react";
import { toast } from "sonner";

import { siteConfig } from "@/config/site";
import { ApiError } from "@/lib/api";
import { useAuth } from "@/providers/auth-provider";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { cn } from "@/lib/utils";
import type { Role } from "@/types";

const roleOptions: { value: Role; label: string; description: string }[] = [
  {
    value: "CLIENT",
    label: "Client",
    description: "Cinema / streaming platform",
  },
  {
    value: "DISTRIBUTOR",
    label: "Distributor",
    description: "Movie rights holder",
  },
  {
    value: "ADMIN",
    label: "Administrator",
    description: "Platform oversight",
  },
];

export function RegisterForm() {
  const router = useRouter();
  const { register } = useAuth();
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState<Role>("CLIENT");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setIsSubmitting(true);

    try {
      await register({ fullName, email, password, role });
      toast.success("Account created successfully");
      router.push("/dashboard");
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Unable to create account";
      toast.error(message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="glass-card animate-fade-up overflow-hidden p-0">
      <div className="border-b border-border/60 bg-primary/5 px-6 py-8 dark:bg-primary/8">
        <div className="mb-4 flex size-12 items-center justify-center rounded-xl bg-primary/15 ring-1 ring-primary/25">
          <UserPlus className="size-5 text-primary" />
        </div>
        <h1 className="font-heading text-2xl font-semibold tracking-tight">
          Create your account
        </h1>
        <p className="mt-1.5 text-sm text-muted-foreground">
          Join {siteConfig.name} to manage distribution and rentals.
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-5 p-6">
        <div className="space-y-2">
          <Label htmlFor="fullName">Full name</Label>
          <Input
            id="fullName"
            required
            placeholder="Jane Cooper"
            className={cn("h-10 bg-background/50 input-glow transition-all duration-300")}
            value={fullName}
            onChange={(event) => setFullName(event.target.value)}
          />
        </div>
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
            autoComplete="new-password"
            minLength={8}
            required
            placeholder="Min. 8 characters"
            className={cn("h-10 bg-background/50 input-glow transition-all duration-300")}
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
        </div>

        <div className="space-y-2">
          <Label>Select your role</Label>
          <div className="grid gap-2">
            {roleOptions.map((option) => (
              <label
                key={option.value}
                className={cn(
                  "flex cursor-pointer items-center gap-3 rounded-xl border p-3 transition-all duration-200",
                  role === option.value
                    ? "border-primary/50 bg-primary/10 shadow-[0_0_0_1px_oklch(0.72_0.13_72_/_30%)]"
                    : "border-border/60 bg-background/30 hover:border-primary/30 hover:bg-primary/5",
                )}
              >
                <input
                  type="radio"
                  name="role"
                  value={option.value}
                  checked={role === option.value}
                  onChange={() => setRole(option.value)}
                  className="accent-primary"
                />
                <div>
                  <p className="text-sm font-medium">{option.label}</p>
                  <p className="text-xs text-muted-foreground">
                    {option.description}
                  </p>
                </div>
              </label>
            ))}
          </div>
        </div>

        <Button
          type="submit"
          className="hover-glow h-10 w-full"
          disabled={isSubmitting}
        >
          {isSubmitting ? "Creating account..." : "Create account"}
        </Button>

        <p className="text-center text-sm text-muted-foreground">
          Already have an account?{" "}
          <Link
            href="/login"
            className="font-medium text-primary transition-colors hover:text-primary/80 hover:underline"
          >
            Sign in
          </Link>
        </p>
      </form>
    </div>
  );
}
