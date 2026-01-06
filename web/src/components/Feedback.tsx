'use client';

import React from "react";

export function InlineError({ message }: { message?: string | null }) {
  if (!message) return null;
  return (
    <div style={{ background: "rgba(248,113,113,0.15)", color: "#fecdd3", padding: 10, borderRadius: 12, fontSize: 12 }}>
      {message}
    </div>
  );
}

export function InlineSuccess({ message }: { message?: string | null }) {
  if (!message) return null;
  return (
    <div style={{ background: "rgba(52,211,153,0.12)", color: "#bbf7d0", padding: 10, borderRadius: 12, fontSize: 12 }}>
      {message}
    </div>
  );
}
