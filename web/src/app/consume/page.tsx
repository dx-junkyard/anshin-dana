const suggestions = [
  { id: 1, name: "冷凍唐揚げ", reason: "冷凍庫スペース確保", quantity: 2, unit: "袋" },
  { id: 2, name: "トマトジュース", reason: "期限が近い", quantity: 1, unit: "本" }
];

export default function ConsumePage() {
  return (
    <main className="grid" style={{ gap: 12 }}>
      <h1 style={{ margin: 0 }}>消費（FEFOモック）</h1>
      <div className="grid">
        {suggestions.map((item) => (
          <div className="card" key={item.id}>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <div style={{ fontWeight: 700 }}>{item.name}</div>
              <span className="pill">{item.quantity}{item.unit} 提案</span>
            </div>
            <div style={{ fontSize: 12, color: "var(--muted)" }}>{item.reason}</div>
            <div className="stack" style={{ marginTop: 10 }}>
              <button>提案どおり消費</button>
              <button className="secondary">数量を変更</button>
            </div>
          </div>
        ))}
      </div>
    </main>
  );
}
