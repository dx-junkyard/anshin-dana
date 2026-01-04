const mockTasks = {
  expiringSoon: [
    { name: "パスタソース", expiresOn: "2024-08-03" },
    { name: "ツナ缶", expiresOn: "2024-08-10" }
  ],
  expired: [{ name: "トマトジュース", expiredOn: "2024-07-01" }],
  lowStock: [{ category: "主食", suggestion: "お米を2kg買い足し" }],
  suggestedConsume: [
    { name: "冷凍唐揚げ", reason: "冷凍庫スペース確保" },
    { name: "パスタ", reason: "賞味期限が近い" }
  ]
};

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

      <TaskSection
        title="期限が近い"
        items={mockTasks.expiringSoon}
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
        items={mockTasks.expired}
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
        items={mockTasks.lowStock}
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
        items={mockTasks.suggestedConsume}
        renderItem={(item) => (
          <div className="card" key={item.name} style={{ padding: 12 }}>
            <div style={{ fontWeight: 600 }}>{item.name}</div>
            <div style={{ fontSize: 12, color: "var(--muted)" }}>{item.reason}</div>
            <button style={{ marginTop: 8 }}>消費する</button>
          </div>
        )}
      />
    </main>
  );
}
