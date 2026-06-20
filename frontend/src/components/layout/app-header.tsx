"use client";

import Link from "next/link";
import { LogOut, Menu } from "lucide-react";
import { useRouter } from "next/navigation";
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
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";

const BrandIcon = brandIcon;

export function AppHeader() {
  const router = useRouter();
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
    router.push("/login");
  }

  return (
    <header className="flex h-16 items-center justify-between border-b bg-background px-4 md:px-6">
      <div className="flex items-center gap-3 md:hidden">
        <Sheet>
          <SheetTrigger
            render={
              <Button variant="outline" size="icon" aria-label="Open navigation">
                <Menu />
              </Button>
            }
          />
          <SheetContent side="left" className="w-72 p-0">
            <SheetHeader className="border-b p-6 text-left">
              <SheetTitle className="flex items-center gap-2">
                <BrandIcon className="size-5" />
                {siteConfig.name}
              </SheetTitle>
            </SheetHeader>
            <nav className="flex flex-col gap-1 p-4">
              {navItems.map((item) => {
                const Icon = item.icon;
                return (
                  <Link
                    key={item.href}
                    href={item.href}
                    className="flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium hover:bg-muted"
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
          <BrandIcon className="size-5" />
          <span className="font-semibold">{siteConfig.name}</span>
        </div>
      </div>

      <div className="hidden md:block">
        <p className="text-sm text-muted-foreground">
          Signed in as{" "}
          <span className="font-medium text-foreground">{formatRole(role)}</span>
        </p>
      </div>

      <div className="flex items-center gap-3">
        <Badge variant="secondary" className="hidden sm:inline-flex">
          {formatRole(role)}
        </Badge>

        <DropdownMenu>
          <DropdownMenuTrigger
            render={
              <Button variant="ghost" size="icon" className="rounded-full">
                <Avatar>
                  <AvatarFallback>{initials}</AvatarFallback>
                </Avatar>
              </Button>
            }
          />
          <DropdownMenuContent align="end" className="w-56">
            <DropdownMenuLabel>Account</DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={handleLogout}>
              <LogOut />
              Sign out
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}
