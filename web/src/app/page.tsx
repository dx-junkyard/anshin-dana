"use client";

import React, { useEffect, useState } from "react";
import { api } from "../lib/api";
import { InlineError } from "../components/Feedback";
import { TodayTasks } from "../lib/types";
import { useAuth } from "../components/AuthProvider";

const TaskSection = ({ title, items, renderItem }: { title: string; items: any[]; renderItem: (item: any, index: number) => React.ReactNode }) => (
  <section className="card">
    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 8 }}>
      <div style={{ fontWeight: 700 }}>{title}</div>
      <span className="pill">{items.length} 件</span>
    </div>
    <div className="grid">
      {items.map(renderItem)}
    </div>
  </section>
);

export default function HomePage() {
  const { loading: authLoading, error: authError } = useAuth();
  const [tasks, setTasks] = useState<TodayTasks | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (authLoading) return;
    void fetchTasks();
  }, [authLoading]);

  const fetchTasks = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.getTodayTasks();
      setTasks(res);
    } catch (e) {
      setError(e instanceof Error ? e.message : "タスク取得に失敗しました");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="grid" style={{ gap: 16 }}>
      <nav className="card">
        <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
          <a className="pill" href="/scan">スキャン登録</a>
          <a className="pill" href="/stocks">在庫一覧</a>
          <a className="pill" href="/consume">消費</a>
        </div>
        <div style={{ fontSize: 12, color: "var(--muted)", marginTop: 8 }}>LIFF起動後に idToken を取得し、バックエンドに交換する想定です。</div>
      </nav>

      {authError && <InlineError message={authError} />}
      {error && <InlineError message={error} />}

      {loading && <div>読み込み中...</div>}
      {!loading && tasks && (
        <>
          <TaskSection
            title="期限が近い"
            items={tasks.expiringSoon}
            renderItem={(item) => (
              <div className="card" key={item.name} style={{ padding: 12 }}>
                <div style={{ fontWeight: 600 }}>{item.name}</div>
                <div style={{ fontSize: 12, color: "var(--muted)" }}>期限 {item.expiresOn}</div>
                <div className="stack" style={{ marginTop: 8 }}>
                  <button>消費する</button>
                  <button className="secondary">メモ</button>
                </div>
              </div>
            )}
          />

          <TaskSection
            title="期限切れ"
            items={tasks.expired}
            renderItem={(item) => (
              <div className="card" key={item.name} style={{ padding: 12 }}>
                <div style={{ fontWeight: 600 }}>{item.name}</div>
                <div style={{ fontSize: 12, color: "var(--muted)" }}>期限 {item.expiredOn}</div>
                <button style={{ marginTop: 8 }}>点検/処分</button>
              </div>
            )}
          />

          <TaskSection
            title="不足"
            items={tasks.lowStock}
            renderItem={(item, idx) => (
              <div className="card" key={idx} style={{ padding: 12 }}>
                <div style={{ fontWeight: 600 }}>{item.category}</div>
                <div style={{ fontSize: 12, color: "var(--muted)" }}>{item.suggestion}</div>
                <button style={{ marginTop: 8 }}>買い足しメモ</button>
              </div>
            )}
          />

          <TaskSection
            title="今日食べる候補"
            items={tasks.suggestedConsume}
            renderItem={(item) => (
              <div className="card" key={item.name} style={{ padding: 12 }}>
                <div style={{ fontWeight: 600 }}>{item.name}</div>
                <div style={{ fontSize: 12, color: "var(--muted)" }}>{item.reason}</div>
                <button style={{ marginTop: 8 }}>消費する</button>
              </div>
            )}
          />
        </>
      )}
    </main>
  );
}
