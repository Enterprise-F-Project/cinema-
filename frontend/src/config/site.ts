export const siteConfig = {
  name: "The Cinema",
  description:
    "Movie distribution and rental management for distributors, cinemas, and administrators.",
  tagline: "Manage licenses, rentals, and your movie catalog in one place.",
} as const;

export const apiConfig = {
  baseUrl: process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080",
  apiPrefix: "/api",
} as const;

export function getApiUrl(path: string): string {
  const normalizedPath = path.startsWith("/") ? path : `/${path}`;
  return `${apiConfig.baseUrl}${apiConfig.apiPrefix}${normalizedPath}`;
}
