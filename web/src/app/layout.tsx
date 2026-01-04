import "../styles/globals.css";
import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Anshin Dana",
  description: "Household stock assistant for LINE LIFF"
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ja">
      <body>
        <div className="layout">
          <header style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
            <div>
              <div style={{ fontSize: 12, color: "var(--muted)" }}>Anshin Dana</div>
              <div style={{ fontWeight: 700, fontSize: 20 }}>在庫まるっと安心ダナ</div>
            </div>
            <a href="/" style={{ fontSize: 12, color: "var(--muted)" }}>HOME</a>
          </header>
          {children}
        </div>
      </body>
    </html>
  );
}
