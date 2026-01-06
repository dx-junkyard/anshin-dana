"use client";

import { useCallback, useEffect, useState } from "react";
import { api } from "../../lib/api";
import { InlineError, InlineSuccess } from "../../components/Feedback";
import { StockItemView } from "../../lib/types";
import { useAuth } from "../../components/AuthProvider";

export default function StocksPage() {
  const { loading: authLoading } = useAuth();
  const [stocks, setStocks] = useState<StockItemView[]>([]);
  const [sort, setSort] = useState<string>("expiresSoon");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [consumeTarget, setConsumeTarget] = useState<StockItemView | null>(null);
  const [consumeQty, setConsumeQty] = useState(1);
  const [consuming, setConsuming] = useState(false);

  const fetchStocks = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.listStocks(sort);
      setStocks(res);
    } catch (e) {
      setError(e instanceof Error ? e.message : "在庫取得に失敗しました");
    } finally {
      setLoading(false);
    }
  }, [sort]);

  useEffect(() => {
    if (authLoading) return;
    void fetchStocks();
  }, [authLoading, fetchStocks]);

  const startConsume = (item: StockItemView) => {
    setConsumeTarget(item);
    setConsumeQty(1);
    setSuccess(null);
    setError(null);
  };

  const submitConsume = async () => {
    if (!consumeTarget) return;
    if (!consumeQty || consumeQty < 1) {
      setError("1以上の数量を指定してください");
      return;
    }
    setConsuming(true);
    setError(null);
    setSuccess(null);
    try {
      await api.consume({ stockItemId: consumeTarget.id, quantity: consumeQty });
      setSuccess("消費を記録しました");
      setConsumeTarget(null);
      await fetchStocks();
    } catch (e) {
      setError(e instanceof Error ? e.message : "消費に失敗しました");
    } finally {
      setConsuming(false);
    }
  };

  return (
    <main className="grid" style={{ gap: 12 }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h1 style={{ margin: 0 }}>在庫一覧</h1>
        <div className="stack" style={{ alignItems: "center" }}>
          <span style={{ fontSize: 12, color: "var(--muted)" }}>並び替え</span>
          <select value={sort} onChange={(e) => setSort(e.target.value)} style={{ padding: 8, borderRadius: 10 }}>
            <option value="expiresSoon">期限が近い順</option>
            <option value="name">名前順</option>
          </select>
        </div>
      </div>

      <InlineError message={error} />
      <InlineSuccess message={success} />
      {loading && <div>読み込み中...</div>}

      <div className="grid">
        {stocks.map((item) => (
          <div className="card" key={item.id}>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <div style={{ fontWeight: 700 }}>{item.product.name}</div>
              <span className="pill">合計 {item.totalQuantity}{item.unit}</span>
            </div>
            <div style={{ marginTop: 8, fontSize: 12, color: "var(--muted)" }}>
              最短期限: {item.nextLot?.expiresOn || "未設定"} ({item.nextLot?.quantity ?? 0}{item.unit})
            </div>
            <div className="stack" style={{ marginTop: 10 }}>
              <button onClick={() => startConsume(item)}>消費</button>
              <button className="secondary">調整</button>
            </div>
          </div>
        ))}
      </div>

      {consumeTarget && (
        <div className="card" style={{ position: "fixed", bottom: 24, right: 24, maxWidth: 320, width: "90%" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <div style={{ fontWeight: 700 }}>{consumeTarget.product.name}</div>
            <button className="secondary" onClick={() => setConsumeTarget(null)}>閉じる</button>
          </div>
          <div style={{ marginTop: 8, fontSize: 12, color: "var(--muted)" }}>残り {consumeTarget.totalQuantity}{consumeTarget.unit}</div>
          <div style={{ display: "grid", gap: 8, marginTop: 8 }}>
            <label style={{ fontSize: 12, color: "var(--muted)" }}>消費数量</label>
            <input
              type="number"
              min={1}
              style={{ padding: 10, borderRadius: 10, border: "1px solid rgba(255,255,255,0.1)", background: "rgba(255,255,255,0.06)", color: "var(--text)" }}
              value={consumeQty}
              onChange={(e) => setConsumeQty(Number(e.target.value))}
            />
            <button onClick={submitConsume} disabled={consuming}>{consuming ? "処理中..." : "消費する"}</button>
          </div>
        </div>
      )}
    </main>
  );
}
