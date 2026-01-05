'use client';

import liff from "@line/liff";
import React, { createContext, useContext, useEffect, useRef, useState } from "react";
import { api } from "../lib/api";
import { AuthResponse } from "../lib/types";
import { clearToken, getStoredToken, storeToken } from "../lib/auth-store";

interface AuthContextValue {
  user: AuthResponse["user"] | null;
  token: string | null;
  loading: boolean;
  error: string | null;
  refresh: () => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const LIFF_ID = process.env.NEXT_PUBLIC_LIFF_ID;
const DEV_TOKEN = process.env.NEXT_PUBLIC_DEV_APP_TOKEN;
const DEV_ID_TOKEN = process.env.NEXT_PUBLIC_DEV_ID_TOKEN || "dev-id-token";

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthResponse["user"] | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const isInitializing = useRef(false);

  useEffect(() => {
    const existing = getStoredToken();
    if (existing) {
      setToken(existing);
    }
  }, []);

  useEffect(() => {
    if (isInitializing.current) return;
    isInitializing.current = true;
    void initialize();
  }, []);

  const initialize = async () => {
    setLoading(true);
    setError(null);

    try {
      if (DEV_TOKEN) {
        setToken(DEV_TOKEN);
        storeToken(DEV_TOKEN);
        setLoading(false);
        return;
      }

      if (!LIFF_ID) {
        throw new Error("LIFF ID not configured");
      }

      await liff.init({ liffId: LIFF_ID });

      if (!liff.isLoggedIn()) {
        liff.login();
        return;
      }

      const idToken = liff.getIDToken() || DEV_ID_TOKEN;
      if (!idToken) {
        throw new Error("Failed to acquire ID token");
      }

      const auth = await api.authWithLine(idToken);
      setUser(auth.user);
      setToken(auth.token);
      storeToken(auth.token);
    } catch (err) {
      console.error(err);
      setError(err instanceof Error ? err.message : "認証に失敗しました");
    } finally {
      setLoading(false);
    }
  };

  const refresh = async () => {
    await initialize();
  };

  const logout = () => {
    clearToken();
    setToken(null);
    setUser(null);
    if (liff.isLoggedIn()) {
      liff.logout();
    }
  };

  return <AuthContext.Provider value={{ user, token, loading, error, refresh, logout }}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
