"use client";

import Link from "next/link";
import { LogOut, Menu } from "lucide-react";
import { usePathname } from "next/navigation";
import { toast } from "sonner";

import { brandIcon, getNavItemsForRole } from "@/config/navigation";
import { siteConfig } from "@/config/site";
import { useAuth } from "@/providers/auth-provider";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { cn } from "@/lib/utils";

const BrandIcon = brandIcon;

const roleBadgeStyles: Record<string, string> = {
  ADMIN: "bg-primary/15 text-primary border-primary/25",
  DISTRIBUTOR: "bg-chart-2/15 text-chart-2 border-chart-2/25",
  CLIENT: "bg-chart-3/15 text-chart-3 border-chart-3/25",
};

export function AppHeader() {
  const pathname = usePathname();
  const { role, logout, formatRole } = useAuth();

  if (!role) return null;

  const navItems = getNavItemsForRole(role);
  const initials = siteConfig.name
    .split(" ")
    .map((word) => word[0])
    .join("")
    .slice(0, 2);

  async function handleLogout() {
    await logout();
    toast.success("Signed out successfully");
    window.location.assign("/login");
  }

  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-border/60 bg-background/80 px-4 backdrop-blur-xl md:px-6">
      <div className="flex items-center gap-3 md:hidden">
        <Sheet>
          <SheetTrigger
            render={
              <Button variant="outline" size="icon" aria-label="Open navigation">
                <Menu />
              </Button>
            }
          />
          <SheetContent side="left" className="w-72 border-sidebar-border bg-sidebar p-0">
            <SheetHeader className="border-b border-sidebar-border p-5 text-left">
              <SheetTitle className="flex items-center gap-2.5">
                <div className="flex size-8 items-center justify-center rounded-lg bg-primary/15">
                  <BrandIcon className="size-4 text-primary" />
                </div>
                <span className="font-heading">{siteConfig.name}</span>
              </SheetTitle>
            </SheetHeader>
            <nav className="flex flex-col gap-1 p-3">
              {navItems.map((item) => {
                const Icon = item.icon;
                const isActive =
                  pathname === item.href ||
                  pathname.startsWith(`${item.href}/`);
                return (
                  <Link
                    key={item.href}
                    href={item.href}
                    className={cn(
                      "flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-colors",
                      isActive
                        ? "nav-item-active"
                        : "hover:bg-sidebar-accent",
                    )}
                  >
                    <Icon className="size-4" />
                    {item.title}
                  </Link>
                );
              })}
            </nav>
          </SheetContent>
        </Sheet>
        <div className="flex items-center gap-2">
          <BrandIcon className="size-5 text-primary" />
          <span className="font-heading font-semibold">{siteConfig.name}</span>
        </div>
      </div>

      <div className="hidden md:block">
        <p className="text-sm text-muted-foreground">
          Signed in as{" "}
          <span className="font-medium text-foreground">{formatRole(role)}</span>
        </p>
      </div>

      <div className="flex items-center gap-3">
        <Badge
          variant="outline"
          className={cn("hidden sm:inline-flex border", roleBadgeStyles[role])}
        >
          {formatRole(role)}
        </Badge>

        <DropdownMenu>
          <DropdownMenuTrigger
            render={
              <Button
                variant="ghost"
                size="icon"
                aria-label="Open account menu"
                className="rounded-full ring-1 ring-border/60 transition-all hover:ring-primary/30"
              />
            }
          >
            <Avatar className="size-8">
              <AvatarFallback className="bg-primary/15 text-xs font-semibold text-primary">
                {initials}
              </AvatarFallback>
            </Avatar>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-48">
            <DropdownMenuGroup>
              <DropdownMenuLabel>Account</DropdownMenuLabel>
              <DropdownMenuItem onClick={() => void handleLogout()}>
                <LogOut />
                Log out
              </DropdownMenuItem>
            </DropdownMenuGroup>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}
