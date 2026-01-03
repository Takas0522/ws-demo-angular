# Frontend

このプロジェクトは [Angular CLI](https://github.com/angular/angular-cli) version 16.2.16 で生成されました。

## 概要

このAngularアプリケーションは、BFF (Backend For Frontend) パターンを採用したマイクロサービスアーキテクチャと統合されています。

## 開発サーバー

開発サーバーを起動するには `ng serve` を実行してください。`http://localhost:4200/` にアクセスしてください。ソースファイルを変更すると、アプリケーションは自動的にリロードされます。

```bash
npm start
# または
ng serve
```

## 認証機能

このアプリケーションには、JWT ベースの認証機能が実装されています。

### ログイン画面

アプリケーションを起動すると、ログイン画面が表示されます（`/login`）。

### テストユーザー

以下のテストユーザーでログインできます：

| ユーザー名 | パスワード | 説明 |
|-----------|----------|------|
| admin | password123 | 管理者ユーザー |
| user1 | password123 | 通常ユーザー1 |
| user2 | password123 | 通常ユーザー2 |
| user3 | password123 | 通常ユーザー3 |

### 認証機能の特徴

- **ReactiveForm による入力バリデーション**: ユーザー名は3文字以上、パスワードは6文字以上
- **JWT トークン管理**: アクセストークンとリフレッシュトークンの自動管理
- **自動トークン付与**: HTTP Interceptor により、API リクエストに自動的に JWT トークンを付与
- **トークン自動リフレッシュ**: 401 エラー時に自動的にトークンをリフレッシュ
- **ルートガード**: 認証が必要なページへの未認証アクセスを防止

## プロジェクト構成

```
src/app/
├── core/                    # コア機能
│   ├── guards/             # ルートガード（AuthGuard）
│   ├── interceptors/       # HTTP インターセプター（AuthInterceptor）
│   ├── models/             # データモデル
│   └── services/           # サービス（AuthService）
└── features/               # 機能モジュール
    └── auth/               # 認証機能
        └── components/
            └── login/      # ログインコンポーネント
```

## ビルド

プロジェクトをビルドするには `ng build` を実行してください。ビルド成果物は `dist/` ディレクトリに格納されます。

```bash
npm run build
# または
ng build
```

## 単体テスト

[Karma](https://karma-runner.github.io) を使用して単体テストを実行するには `ng test` を実行してください。

```bash
npm test
# または
ng test
```

## End-to-End テスト

プラットフォームに応じた End-to-End テストを実行するには `ng e2e` を実行してください。このコマンドを使用するには、まず End-to-End テスト機能を実装するパッケージを追加する必要があります。

## API 接続設定

アプリケーションは BFF Service（`http://localhost:8080`）に接続します。環境設定は `src/environments/` で管理されています。

- `environment.ts` - 開発環境設定
- `environment.prod.ts` - 本番環境設定

## 詳細情報

Angular CLI の詳細については、`ng help` を実行するか、[Angular CLI Overview and Command Reference](https://angular.io/cli) ページを確認してください。
