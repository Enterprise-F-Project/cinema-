import { getApiUrl } from "@/config/site";
import { getAccessToken } from "@/lib/auth/session";
import type { ErrorResponse } from "@/types";

export class ApiError extends Error {
  status: number;
  body: ErrorResponse | null;

  constructor(message: string, status: number, body: ErrorResponse | null = null) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.body = body;
  }
}

export type QueryParams = Record<
  string,
  string | number | boolean | undefined | null
>;

type RequestOptions = Omit<RequestInit, "body"> & {
  body?: unknown;
  params?: QueryParams;
  auth?: boolean;
};

function buildUrl(path: string, params?: RequestOptions["params"]): string {
  const url = new URL(getApiUrl(path));

  if (params) {
    for (const [key, value] of Object.entries(params)) {
      if (value !== undefined && value !== null && value !== "") {
        url.searchParams.set(key, String(value));
      }
    }
  }

  return url.toString();
}

async function parseError(response: Response): Promise<ApiError> {
  let body: ErrorResponse | null = null;

  try {
    body = (await response.json()) as ErrorResponse;
  } catch {
    body = null;
  }

  const message = body?.message ?? response.statusText ?? "Request failed";
  return new ApiError(message, response.status, body);
}

export async function apiClient<T>(
  path: string,
  { body, params, auth = true, headers, ...options }: RequestOptions = {},
): Promise<T> {
  const requestHeaders = new Headers(headers);

  if (body !== undefined) {
    requestHeaders.set("Content-Type", "application/json");
  }

  if (auth) {
    const token = getAccessToken();
    if (token) {
      requestHeaders.set("Authorization", `Bearer ${token}`);
    }
  }

  const response = await fetch(buildUrl(path, params), {
    ...options,
    headers: requestHeaders,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (!response.ok) {
    throw await parseError(response);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}
