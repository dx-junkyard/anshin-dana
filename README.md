## Anshin Dana

LINE LIFF を入口に、家庭内の在庫を「スキャン → 登録 → 消費」までシンプルに回せるようにした支援サービスです。期限切れのムダを減らし、買い過ぎ・買い忘れを防ぐことをコンセプトにしています。

### どう実現するか

- **登録の容易さ**: バーコードをスキャンすると同じ商品を過去に登録した場合は候補情報と「最近の期限入力（テンプレート）」を返し、最小入力で登録できます。
- **在庫の分離モデル**: 「商品マスタ」と「ユーザーの在庫（lot 単位）」を分けて保持し、同じ商品を複数ユーザーが使っても整理しやすい構造にしています。
- **FEFO 消費**: 賞味期限が早い lot から減らすことで、期限切れを防ぎます。
- **通知・提案**: 期限が近いものや低在庫を毎日まとめて知らせる（worker プロファイルでスケジュール実行）。

### 利用フロー（アプリ側）

1. **LINE ログイン**: LIFF から取得した `idToken` を `POST /api/auth/line` に送信し、バックエンドで検証・ユーザー作成し、アプリ用 JWT を受け取ります。
2. **バーコードスキャン**: `POST /api/scan { barcode }` で商品候補と期限テンプレートを取得。
3. **在庫登録**: `POST /api/stocks` で数量・単位・期限を登録。商品がなければ自動作成し、同一ユーザー×商品で在庫を upsert。
4. **在庫一覧**: `GET /api/stocks?sort=expiresSoon` で最短期限を含む一覧を取得。
5. **消費/廃棄/調整**: `POST /api/consume` で理由付き消費ログを残しつつ、FEFO で lot を減算。
6. **タスク確認**: `GET /api/tasks/today` で期限接近・切れ・低在庫のサマリを確認（現状はモックレスポンス）。

### システム構成

- **web/**: Next.js (App Router) の LIFF クライアント。バーコードスキャン・在庫一覧・消費画面を提供。
- **api/**: Spring Boot REST API。LINE トークン検証、JWT 発行、在庫 CRUD、消費ログ管理を担当。プロファイル `worker` ではスケジュールジョブを実行。
- **infra/**: 開発用の nginx リバースプロキシと Docker Compose 設定。
- **docs/**: アーキテクチャや API の補足ドキュメント。
- **DB**: PostgreSQL。Flyway でスキーマを管理。
- **リバースプロキシ**: `/api` をバックエンドへ、それ以外を web へルーティング。

詳細な図や背景は `docs/architecture.md` を参照してください。

### ローカル環境のセットアップ手順

1. 依存ツールを準備: Docker / Docker Compose。
2. 環境変数ファイルを用意: `.env.example` を `.env` にコピーし、LINE チャネル情報・JWT シークレットなどを入力。デフォルトで PostgreSQL の接続情報も含まれています。
3. ビルドして起動:

```bash
docker compose up --build
```

起動後のアクセス先:

- Web: `http://localhost:8080`
- API: `http://localhost:8080/api`
- DB: `DATABASE_URL` に指定された PostgreSQL（Compose 内の `db` サービス）

### システム設定のポイント

- `app` サービス（API）は起動時に Flyway で `api/src/main/resources/db/migration` 以下の DDL を自動適用します。Compose の `DATABASE_*` 環境変数で接続先を指定してください。
- JWT 関連設定（`APP_JWT_SECRET`, `APP_JWT_ISSUER`, `APP_JWT_EXPIRES_MINUTES`）は `.env` で指定し、必ず十分長いランダム値を使います。
- LINE チャネル設定（`LINE_CHANNEL_ID`, `LINE_CHANNEL_SECRET`, `LINE_MESSAGING_CHANNEL_ACCESS_TOKEN`）を正しく設定しないと認証が失敗します。
- タイムゾーンは `TZ=Asia/Tokyo` を前提にしています。

### マイグレーションの実行方法

- 通常は API（または worker）起動時に自動で Flyway が適用されます。
- 手動で実行する場合は `api/` ディレクトリで以下を実行してください:

```bash
./gradlew flywayMigrate \
  -Dspring.datasource.url=$DATABASE_URL \
  -Dspring.datasource.username=$DATABASE_USER \
  -Dspring.datasource.password=$DATABASE_PASSWORD
```

### 認証・API の基本

- `POST /api/auth/line` で LINE ID トークンを検証し、アプリ用 JWT を発行します。
- それ以外の `/api/**` では `Authorization: Bearer <JWT>` が必須です。不正・欠如している場合は `401` が返ります。

### プロダクトの目標

- スキャンから登録までを最小入力で終わらせ、日常的に使いやすいこと。
- 期限を考慮した FEFO 消費と期限通知でムダを減らすこと。
- 家族や複数端末でも一貫した在庫を共有できること。
