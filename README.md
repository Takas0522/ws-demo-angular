# WS Demo Angular - Microservices Architecture

マイクロサービスアーキテクチャを採用したWebアプリケーションのデモプロジェクト

## アーキテクチャ概要

このプロジェクトは、BFF（Backend For Frontend）パターンを採用したマイクロサービスアーキテクチャで構成されています。

```
Frontend (Angular)
    ↓
BFF Service (Port 8080)
    ↓
Backend Services
├── Auth Service (Port 8081) - 認証・認可
├── User Service (Port 8082) - ユーザープロファイル管理
└── Permission Service (Port 8083) - 権限管理
```

## サービス一覧

### BFF Service (Port 8080)
フロントエンド専用のAPIゲートウェイ。全てのフロントエンドリクエストを受け付け、適切なバックエンドサービスにルーティングします。

**主な機能:**
- APIルーティング
- リクエストプロキシ
- ヘッダー転送（JWT認証トークンなど）

詳細: [BFF Service README](src/backend/bff-service/README.md)

### Auth Service (Port 8081)
JWT ベースの認証とリフレッシュトークン管理を提供します。

**主な機能:**
- ユーザー認証（ログイン）
- JWT トークン生成
- リフレッシュトークン管理
- Saga パターンによる分散トランザクション管理

詳細: [Auth Service README](src/backend/auth-service/README.md)

### User Service (Port 8082)
ユーザープロファイル管理とべき等性キー管理を提供します。

**主な機能:**
- ユーザープロファイル管理
- べき等性キーによる重複リクエスト防止
- DataSource 再試行設定による高可用性

詳細: [User Service README](src/backend/user-service/README.md)

### Permission Service (Port 8083)
アプリケーション権限とユーザーアクセスレベルを管理します。

**主な機能:**
- アプリケーション管理
- 権限レベル管理（READ, WRITE, ADMIN）
- ユーザー-アプリケーション権限マッピング

詳細: [Permission Service README](src/backend/permission-service/README.md)

### Shared Library
各サービス間で共有される共通ライブラリです。

**主な機能:**
- JWT トークン生成・検証
- 共通 DTO
- 共通例外処理

詳細: [Shared Library README](src/backend/shared-lib/README.md)

### Frontend (Angular)
Angular 16 で構築されたシングルページアプリケーション（SPA）。

**主な機能:**
- JWT ベースの認証システム
- BFF Service との統合
- レスポンシブ UI（Tailwind CSS）
- ReactiveForm による入力バリデーション

詳細: [Frontend README](src/frontend/README.md)

**テストユーザー:**

アプリケーションにログインするには、以下のテストユーザーを使用できます：

| ユーザー名 | パスワード | 説明 |
|-----------|----------|------|
| admin | password123 | 管理者ユーザー |
| user1 | password123 | 通常ユーザー1 |
| user2 | password123 | 通常ユーザー2 |
| user3 | password123 | 通常ユーザー3 |

## 開発環境セットアップ

### 前提条件
- Docker Desktop
- Visual Studio Code with Dev Containers extension

### DevContainer での起動

1. リポジトリをクローン
```bash
git clone https://github.com/Takas0522/ws-demo-angular.git
cd ws-demo-angular
```

2. VS Code で開く
```bash
code .
```

3. Dev Container で再オープン
   - コマンドパレット（Ctrl+Shift+P / Cmd+Shift+P）を開く
   - "Dev Containers: Reopen in Container" を選択

4. コンテナが起動すると、自動的に以下が実行されます：
   - PostgreSQL データベースの初期化
   - JWT キーペアの生成（初回のみ）
   - キーディレクトリへのシンボリックリンク作成
   - 各サービスのビルド（オプション）

### JWT キーペアについて

このプロジェクトは JWT (RS256) 認証を使用しており、RSA 公開鍵/秘密鍵のペアが必要です。

**自動生成:**
DevContainer の初回起動時に自動的に生成されます（`.devcontainer/keys/` に配置）。

**手動生成:**
キーペアを再生成する場合は、以下のコマンドを実行してください：

```bash
bash .devcontainer/generate-keys.sh
```

生成されたキーファイル：
- `keys/private_key.pem` - 秘密鍵（Auth Service がトークン生成に使用）
- `keys/public_key.pem` - 公開鍵（各サービスがトークン検証に使用）

**注意事項:**
- 秘密鍵は厳重に管理してください（本番環境では環境変数や Key Vault を使用）
- キーペアを変更した場合は、全サービスを再起動してください
- DevContainer 内では `/keys/` からもアクセス可能（シンボリックリンク経由）

### サービスの起動

各サービスは個別に起動できます：

#### BFF Service
```bash
cd src/backend/bff-service
mvn spring-boot:run
```

#### Auth Service
```bash
cd src/backend/auth-service
mvn spring-boot:run
```

#### User Service
```bash
cd src/backend/user-service
mvn spring-boot:run
```

#### Permission Service
```bash
cd src/backend/permission-service
mvn spring-boot:run
```

#### Frontend (Angular)
```bash
cd src/frontend
npm install  # 初回のみ
npm start
```

フロントエンドは `http://localhost:4200` で起動します。

## API エンドポイント

フロントエンドからは全てのリクエストを BFF Service (http://localhost:8080) に送信します。

### 認証 API
- `POST /api/auth/login` - ログイン
- `POST /api/auth/refresh` - トークンリフレッシュ
- `POST /api/auth/logout` - ログアウト
- `POST /api/auth/register` - ユーザー登録

### ユーザー API
- `GET /api/users` - 全ユーザープロファイル取得
- `GET /api/users/{userId}` - ユーザープロファイル取得
- `POST /api/users` - ユーザープロファイル作成
- `PUT /api/users/{userId}` - ユーザープロファイル更新
- `DELETE /api/users/{userId}` - ユーザープロファイル削除

### 権限 API
- `GET /api/applications` - 全アプリケーション取得
- `GET /api/users/{userId}/permissions` - ユーザー権限取得
- `POST /api/permissions` - 権限作成
- `PUT /api/permissions/{permissionId}` - 権限更新
- `DELETE /api/permissions/{permissionId}` - 権限削除

## テストデータ

デフォルトで以下のテストユーザーが利用可能です：

| ユーザー名 | パスワード | 説明 |
|-----------|----------|------|
| admin | password123 | 管理者ユーザー |
| user1 | password123 | 通常ユーザー1 |
| user2 | password123 | 通常ユーザー2 |
| user3 | password123 | 通常ユーザー3 |

## ビルド

### 全サービスのビルド

```bash
# Shared Library のビルド（最初に実行）
cd src/backend/shared-lib
mvn clean install

# 各サービスのビルド
cd ../bff-service && mvn clean install
cd ../auth-service && mvn clean install
cd ../user-service && mvn clean install
cd ../permission-service && mvn clean install
```

## テスト

各サービスのテストを実行：

```bash
cd src/backend/{service-name}
mvn test
```

## 技術スタック

### Frontend
- Angular 16
- TypeScript 5.1
- RxJS 7.8
- Tailwind CSS 3.4
- Reactive Forms

### Backend
- Java 11
- Spring Boot 2.7.18
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (RS256)
- Maven

### DevOps
- Docker
- Docker Compose
- VS Code Dev Containers

## アーキテクチャの特徴

### BFF パターン
- フロントエンド専用の API ゲートウェイ
- バックエンドサービスの抽象化
- 認証トークンの自動転送

### マイクロサービス
- サービスごとに独立したデータベース
- 疎結合なアーキテクチャ
- スケーラブルな設計

### セキュリティ
- JWT (RS256) による認証
- リフレッシュトークンによるトークン更新
- BCrypt によるパスワードハッシュ化

### 信頼性
- べき等性キーによる重複リクエスト防止
- Saga パターンによる分散トランザクション管理
- DataSource 再試行設定

## ライセンス

MIT License

## 貢献

Issue や Pull Request を歓迎します！