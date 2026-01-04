const mockScanResult = {
  barcode: "4901234567890",
  name: "Mock Beans",
  brand: "Sample Brand",
  category: "canned",
  expiryTemplates: ["前回と同じ（2025-01-05）", "半年後", "1年後"]
};

export default function ScanPage() {
  return (
    <main className="grid" style={{ gap: 12 }}>
      <h1 style={{ margin: 0 }}>スキャン → 登録</h1>
      <div className="card">
        <div style={{ fontWeight: 700, marginBottom: 8 }}>バーコードスキャン</div>
        <div style={{ fontSize: 12, color: "var(--muted)", marginBottom: 8 }}>
          カメラは後で `zxing-js/browser` に差し替え予定。今はモック値でUIを確認。
        </div>
        <div className="stack">
          <input style={{ padding: 10, borderRadius: 10, border: "1px solid rgba(255,255,255,0.1)", background: "rgba(255,255,255,0.06)", color: "var(--text)" }} placeholder="バーコードを入力 (モック)" defaultValue={mockScanResult.barcode} />
          <button>検索</button>
        </div>
      </div>

      <div className="card grid">
        <div>
          <div style={{ fontWeight: 700 }}>候補</div>
          <div style={{ fontSize: 12, color: "var(--muted)" }}>DBヒット時のサンプル。</div>
        </div>
        <div className="pill">{mockScanResult.brand}</div>
        <div style={{ fontSize: 18, fontWeight: 700 }}>{mockScanResult.name}</div>
        <div className="stack">
          <span className="pill">{mockScanResult.category}</span>
          <span className="pill">バーコード {mockScanResult.barcode}</span>
        </div>
      </div>

      <div className="card grid">
        <div style={{ fontWeight: 700 }}>期限テンプレート</div>
        <div className="stack">
          {mockScanResult.expiryTemplates.map((tpl) => (
            <button key={tpl} className="secondary">{tpl}</button>
          ))}
        </div>
        <div style={{ display: "grid", gap: 8 }}>
          <label style={{ fontSize: 12, color: "var(--muted)" }}>数量</label>
          <input style={{ padding: 10, borderRadius: 10, border: "1px solid rgba(255,255,255,0.1)", background: "rgba(255,255,255,0.06)", color: "var(--text)" }} defaultValue={2} />
          <label style={{ fontSize: 12, color: "var(--muted)" }}>期限</label>
          <input style={{ padding: 10, borderRadius: 10, border: "1px solid rgba(255,255,255,0.1)", background: "rgba(255,255,255,0.06)", color: "var(--text)" }} defaultValue="2025-01-05" />
          <button>登録（モック）</button>
        </div>
      </div>
    </main>
  );
}
