"use client";

import { useEffect, useState } from "react";
import { api } from "../../lib/api";
import { InlineError, InlineSuccess } from "../../components/Feedback";
import { ProductSummary, ScanResponse } from "../../lib/types";
import { useAuth } from "../../components/AuthProvider";

interface RegistrationFormState {
  barcode: string;
  name: string;
  brand: string;
  category: string;
  quantity: number;
  unit: string;
  expiresOn: string;
  purchasedOn: string;
}

export default function ScanPage() {
  const { loading: authLoading } = useAuth();
  const [barcodeInput, setBarcodeInput] = useState("");
  const [scanResult, setScanResult] = useState<ScanResponse | null>(null);
  const [productOverride, setProductOverride] = useState<ProductSummary | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [registering, setRegistering] = useState(false);
  const [form, setForm] = useState<RegistrationFormState>({
    barcode: "",
    name: "",
    brand: "",
    category: "",
    quantity: 1,
    unit: "個",
    expiresOn: "",
    purchasedOn: ""
  });

  useEffect(() => {
    if (scanResult?.productCandidate) {
      setProductOverride(scanResult.productCandidate);
      setForm((prev) => ({
        ...prev,
        barcode: scanResult.productCandidate?.barcode || "",
        name: scanResult.productCandidate?.name || "",
        brand: scanResult.productCandidate?.brand || "",
        category: scanResult.productCandidate?.category || ""
      }));
    }
  }, [scanResult]);

  const handleSearch = async () => {
    if (!barcodeInput) {
      setError("バーコードを入力してください");
      return;
    }
    setLoading(true);
    setError(null);
    setSuccess(null);
    try {
      const res = await api.scanBarcode(barcodeInput);
      setScanResult(res);
      setForm((prev) => ({
        ...prev,
        barcode: barcodeInput,
        name: res.productCandidate?.name || "",
        brand: res.productCandidate?.brand || "",
        category: res.productCandidate?.category || ""
      }));
    } catch (e) {
      setError(e instanceof Error ? e.message : "検索に失敗しました");
    } finally {
      setLoading(false);
    }
  };

  const handleTemplateClick = (tpl: string) => {
    setForm((prev) => ({ ...prev, expiresOn: tpl }));
  };

  const handleChange = (key: keyof RegistrationFormState, value: string | number) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const handleRegister = async () => {
    if (!form.barcode || !form.name || !form.quantity || !form.unit || !form.expiresOn) {
      setError("バーコード、商品名、数量、単位、期限は必須です");
      return;
    }
    setRegistering(true);
    setError(null);
    setSuccess(null);
    try {
      const payload = {
        barcode: form.barcode,
        name: form.name,
        brand: form.brand || undefined,
        category: form.category || undefined,
        quantity: form.quantity,
        unit: form.unit,
        expiresOn: form.expiresOn,
        purchasedOn: form.purchasedOn || undefined
      };
      await api.registerStock(payload);
      setSuccess("登録しました");
    } catch (e) {
      setError(e instanceof Error ? e.message : "登録に失敗しました");
    } finally {
      setRegistering(false);
    }
  };

  return (
    <main className="grid" style={{ gap: 12 }}>
      <h1 style={{ margin: 0 }}>スキャン → 登録</h1>
      <div className="card">
        <div style={{ fontWeight: 700, marginBottom: 8 }}>バーコードスキャン</div>
        <div style={{ fontSize: 12, color: "var(--muted)", marginBottom: 8 }}>
          カメラは後で `zxing-js/browser` に差し替え予定。バーコード入力で検索します。
        </div>
        <div className="stack">
          <input
            style={{ padding: 10, borderRadius: 10, border: "1px solid rgba(255,255,255,0.1)", background: "rgba(255,255,255,0.06)", color: "var(--text)" }}
            placeholder="バーコードを入力"
            value={barcodeInput}
            onChange={(e) => setBarcodeInput(e.target.value)}
          />
          <button onClick={handleSearch} disabled={loading || authLoading}>{loading ? "検索中..." : "検索"}</button>
        </div>
        <InlineError message={error} />
        <InlineSuccess message={success} />
      </div>

      {scanResult && (
        <>
          <div className="card grid">
            <div>
              <div style={{ fontWeight: 700 }}>候補</div>
              <div style={{ fontSize: 12, color: "var(--muted)" }}>DBヒット時のサンプル。</div>
            </div>
            {productOverride ? (
              <>
                <div className="pill">{productOverride.brand || "ブランド未設定"}</div>
                <div style={{ fontSize: 18, fontWeight: 700 }}>{productOverride.name}</div>
                <div className="stack">
                  <span className="pill">{productOverride.category || "カテゴリ未設定"}</span>
                  <span className="pill">バーコード {productOverride.barcode}</span>
                </div>
              </>
            ) : (
              <div style={{ color: "var(--muted)", fontSize: 14 }}>候補が見つかりませんでした。手入力してください。</div>
            )}
          </div>

          <div className="card grid">
            <div style={{ fontWeight: 700 }}>期限テンプレート</div>
            <div className="stack">
              {(scanResult.lastUsedExpiryTemplates || []).map((tpl) => (
                <button key={tpl} className="secondary" onClick={() => handleTemplateClick(tpl)}>{tpl}</button>
              ))}
              {scanResult.lastUsedExpiryTemplates?.length === 0 && (
                <span style={{ color: "var(--muted)", fontSize: 12 }}>テンプレートがありません</span>
              )}
            </div>

            <div style={{ display: "grid", gap: 8 }}>
              <label style={{ fontSize: 12, color: "var(--muted)" }}>商品名*</label>
              <input
                style={{ padding: 10, borderRadius: 10, border: "1px solid rgba(255,255,255,0.1)", background: "rgba(255,255,255,0.06)", color: "var(--text)" }}
                value={form.name}
                onChange={(e) => handleChange("name", e.target.value)}
              />

              <label style={{ fontSize: 12, color: "var(--muted)" }}>ブランド</label>
              <input
                style={{ padding: 10, borderRadius: 10, border: "1px solid rgba(255,255,255,0.1)", background: "rgba(255,255,255,0.06)", color: "var(--text)" }}
                value={form.brand}
                onChange={(e) => handleChange("brand", e.target.value)}
              />

              <label style={{ fontSize: 12, color: "var(--muted)" }}>カテゴリ</label>
              <input
                style={{ padding: 10, borderRadius: 10, border: "1px solid rgba(255,255,255,0.1)", background: "rgba(255,255,255,0.06)", color: "var(--text)" }}
                value={form.category}
                onChange={(e) => handleChange("category", e.target.value)}
              />

              <label style={{ fontSize: 12, color: "var(--muted)" }}>数量*</label>
              <input
                type="number"
                style={{ padding: 10, borderRadius: 10, border: "1px solid rgba(255,255,255,0.1)", background: "rgba(255,255,255,0.06)", color: "var(--text)" }}
                value={form.quantity}
                onChange={(e) => handleChange("quantity", Number(e.target.value))}
                min={1}
              />

              <label style={{ fontSize: 12, color: "var(--muted)" }}>単位*</label>
              <input
                style={{ padding: 10, borderRadius: 10, border: "1px solid rgba(255,255,255,0.1)", background: "rgba(255,255,255,0.06)", color: "var(--text)" }}
                value={form.unit}
                onChange={(e) => handleChange("unit", e.target.value)}
              />

              <label style={{ fontSize: 12, color: "var(--muted)" }}>期限*</label>
              <input
                type="date"
                style={{ padding: 10, borderRadius: 10, border: "1px solid rgba(255,255,255,0.1)", background: "rgba(255,255,255,0.06)", color: "var(--text)" }}
                value={form.expiresOn}
                onChange={(e) => handleChange("expiresOn", e.target.value)}
              />

              <label style={{ fontSize: 12, color: "var(--muted)" }}>購入日</label>
              <input
                type="date"
                style={{ padding: 10, borderRadius: 10, border: "1px solid rgba(255,255,255,0.1)", background: "rgba(255,255,255,0.06)", color: "var(--text)" }}
                value={form.purchasedOn}
                onChange={(e) => handleChange("purchasedOn", e.target.value)}
              />

              <button onClick={handleRegister} disabled={registering || authLoading}>{registering ? "登録中..." : "登録する"}</button>
            </div>
          </div>
        </>
      )}
    </main>
  );
}
