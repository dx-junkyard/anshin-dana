'use client';

import { AuthResponse, ConsumeRequest, ConsumptionResult, RegisterStockRequest, ScanResponse, StockItemView, TodayTasks } from "./types";
import { getStoredToken } from "./auth-store";

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE || "/api";

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = getStoredToken();
  const headers = new Headers(options.headers);

  if (!headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers
  });

  if (!res.ok) {
    const message = await safeErrorMessage(res);
    throw new Error(message || `Request failed: ${res.status}`);
  }

  if (res.status === 204) {
    return undefined as unknown as T;
  }

  return res.json();
}

async function safeErrorMessage(res: Response) {
  try {
    const data = await res.json();
    return data?.message || data?.error || undefined;
  } catch (e) {
    return undefined;
  }
}

export const api = {
  authWithLine: (idToken: string, displayName?: string, pictureUrl?: string) =>
    request<AuthResponse>("/auth/line", {
      method: "POST",
      body: JSON.stringify({ idToken, displayName, pictureUrl })
    }),

  getTodayTasks: () => request<TodayTasks>("/tasks/today"),

  scanBarcode: (barcode: string) =>
    request<ScanResponse>("/scan", {
      method: "POST",
      body: JSON.stringify({ barcode })
    }),

  registerStock: (payload: RegisterStockRequest) =>
    request<StockItemView>("/stocks", {
      method: "POST",
      body: JSON.stringify(payload)
    }),

  listStocks: (sort?: string) => {
    const query = sort ? `?sort=${encodeURIComponent(sort)}` : "";
    return request<StockItemView[]>(`/stocks${query}`);
  },

  consume: (payload: ConsumeRequest) =>
    request<ConsumptionResult>("/consume", {
      method: "POST",
      body: JSON.stringify(payload)
    })
};
