import { rmSync } from "node:fs";

try {
  rmSync(".next", { recursive: true, force: true });
} catch {
  // Ignore missing or locked folders; Next.js will recreate .next on start.
}
