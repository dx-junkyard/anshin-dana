"use client";

import { useEffect, useState } from "react";
import { api } from "../../lib/api";
import { InlineError, InlineSuccess } from "../../components/Feedback";
import { StockItemView } from "../../lib/types";
import { useAuth } from "../../components/AuthProvider";

export default function ConsumePage() {
  const { loading: authLoading } = useAuth();
  const [stocks, setStocks] = useState<StockItemView[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [selected, setSelected] = useState<StockItemView | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [reason, setReason] = useState("consume");
  const [consuming, setConsuming] = useState(false);

  useEffect(() => {
    if (authLoading) return;
    void fetchStocks();
  }, [authLoading]);

  const fetchStocks = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.listStocks("expiresSoon");
      setStocks(res);
    } catch (e) {
      setError(e instanceof Error ? e.message : "在庫取得に失敗しました");
    } finally {
      setLoading(false);
    }
  };

  const submitConsume = async () => {
    if (!selected) return;
    if (!quantity || quantity < 1) {
      setError("1以上の数量を指定してください");
      return;
    }
    setConsuming(true);
    setError(null);
    setSuccess(null);
    try {
      await api.consume({ stockItemId: selected.id, quantity, reason });
      setSuccess("消費しました");
      setSelected(null);
      await fetchStocks();
    } catch (e) {
      setError(e instanceof Error ? e.message : "消費に失敗しました");
    } finally {
      setConsuming(false);
    }
  };

  return (
    <main className="grid" style={{ gap: 12 }}>
      <h1 style={{ margin: 0 }}>消費</h1>
      <InlineError message={error} />
      <InlineSuccess message={success} />
      {loading && <div>読み込み中...</div>}

      <div className="grid">
        {stocks.map((item) => (
          <div className="card" key={item.id}>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <div style={{ fontWeight: 700 }}>{item.product.name}</div>
              <span className="pill">残 {item.totalQuantity}{item.unit}</span>
            </div>
            <div style={{ fontSize: 12, color: "var(--muted)" }}>
              次の期限 {item.nextLot?.expiresOn || "未設定"} ({item.nextLot?.quantity ?? 0}{item.unit})
            </div>
            <div className="stack" style={{ marginTop: 10 }}>
              <button onClick={() => { setSelected(item); setQuantity(1); setReason("consume"); }}>消費する</button>
              <button className="secondary" onClick={() => { setSelected(item); setQuantity(1); setReason("dispose"); }}>廃棄</button>
            </div>
          </div>
        ))}
      </div>

      {selected && (
        <div className="card" style={{ position: "fixed", bottom: 24, right: 24, maxWidth: 320, width: "90%" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <div style={{ fontWeight: 700 }}>{selected.product.name}</div>
            <button className="secondary" onClick={() => setSelected(null)}>閉じる</button>
          </div>
          <div style={{ fontSize: 12, color: "var(--muted)", marginTop: 4 }}>残り {selected.totalQuantity}{selected.unit}</div>
          <div style={{ display: "grid", gap: 8, marginTop: 8 }}>
            <label style={{ fontSize: 12, color: "var(--muted)" }}>数量</label>
            <input
              type="number"
              min={1}
              style={{ padding: 10, borderRadius: 10, border: "1px solid rgba(255,255,255,0.1)", background: "rgba(255,255,255,0.06)", color: "var(--text)" }}
              value={quantity}
              onChange={(e) => setQuantity(Number(e.target.value))}
            />
            <label style={{ fontSize: 12, color: "var(--muted)" }}>理由</label>
            <select value={reason} onChange={(e) => setReason(e.target.value)} style={{ padding: 10, borderRadius: 10 }}>
              <option value="consume">consume</option>
              <option value="dispose">dispose</option>
              <option value="adjust">adjust</option>
            </select>
            <button onClick={submitConsume} disabled={consuming}>{consuming ? "処理中..." : "送信"}</button>
          </div>
        </div>
      )}
    </main>
  );
}
