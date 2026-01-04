const mockStocks = [
  {
    id: 1,
    name: "パスタソース",
    totalQuantity: 5,
    unit: "袋",
    nextLot: { expiresOn: "2024-08-03", quantity: 2 }
  },
  {
    id: 2,
    name: "ツナ缶",
    totalQuantity: 8,
    unit: "缶",
    nextLot: { expiresOn: "2024-08-10", quantity: 3 }
  }
];

export default function StocksPage() {
  return (
    <main className="grid" style={{ gap: 12 }}>
      <h1 style={{ margin: 0 }}>在庫一覧（モック）</h1>
      <div className="grid">
        {mockStocks.map((item) => (
          <div className="card" key={item.id}>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <div style={{ fontWeight: 700 }}>{item.name}</div>
              <span className="pill">合計 {item.totalQuantity}{item.unit}</span>
            </div>
            <div style={{ marginTop: 8, fontSize: 12, color: "var(--muted)" }}>
              最短期限: {item.nextLot.expiresOn} ({item.nextLot.quantity}{item.unit})
            </div>
            <div className="stack" style={{ marginTop: 10 }}>
              <button>消費</button>
              <button className="secondary">調整</button>
            </div>
          </div>
        ))}
      </div>
    </main>
  );
}
