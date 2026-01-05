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

#### クイックスタート（全サービス一括起動）

全てのバックエンドサービスを一括で起動：

```bash
cd src/backend
./start-all-services.sh
```

全てのバックエンドサービスを一括で停止：

```bash
cd src/backend
./stop-all-services.sh
```

起動スクリプトは以下のサービスを順番に起動します：
1. Auth Service (Port 8081)
2. User Service (Port 8082)
3. Permission Service (Port 8083)
4. BFF Service (Port 8080)

各サービスのログは `/tmp/` ディレクトリに出力されます。

#### 個別起動

各サービスは個別に起動することもできます：

##### BFF Service
```bash
cd src/backend/bff-service
mvn spring-boot:run
```

##### Auth Service
```bash
cd src/backend/auth-service
mvn spring-boot:run
```

##### User Service
```bash
cd src/backend/user-service
mvn spring-boot:run
```

##### Permission Service
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

### 初期ログイン情報

アプリケーションには以下のテストアカウントでログインできます：

**推奨アカウント:**
- **ユーザー名:** `admin`
- **パスワード:** `password123`

その他の利用可能なテストユーザー：
- `user1` / `password123` - 通常ユーザー1
- `user2` / `password123` - 通常ユーザー2
- `user3` / `password123` - 通常ユーザー3

> **注意:** これらは開発・テスト用のアカウントです。本番環境では適切なパスワードポリシーを適用してください。

### API仕様

各サービスのAPI仕様は以下のREADMEで確認できます：
- [BFF Service API](src/backend/bff-service/README.md)
- [Auth Service API](src/backend/auth-service/README.md)
- [User Service API](src/backend/user-service/README.md)
- [Permission Service API](src/backend/permission-service/README.md)

**主要なエンドポイント:**

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

## トラブルシューティング

### サービスが起動しない

**問題:** サービスが起動に失敗する

**解決方法:**

1. **ポートが既に使用されている場合**
   ```bash
   # 使用中のポートを確認
   netstat -tln | grep -E ':(8080|8081|8082|8083)'
   
   # または
   ss -tln | grep -E ':(8080|8081|8082|8083)'
   
   # 既存のサービスを停止
   cd src/backend
   ./stop-all-services.sh
   ```

2. **データベース接続エラー**
   ```bash
   # PostgreSQLが起動しているか確認
   docker ps | grep postgres
   
   # DevContainerを再起動
   # VS Code: コマンドパレット → "Dev Containers: Rebuild Container"
   ```

3. **JWT キーペアが見つからない**
   ```bash
   # キーペアを再生成
   bash .devcontainer/generate-keys.sh
   
   # 全サービスを再起動
   cd src/backend
   ./stop-all-services.sh
   ./start-all-services.sh
   ```

### ログイン時に認証エラーが発生する

**問題:** 「認証に失敗しました」エラー

**解決方法:**

1. **正しいログイン情報を使用しているか確認**
   - ユーザー名: `admin`
   - パスワード: `password123`
   - 大文字小文字を区別します

2. **Auth Service が起動しているか確認**
   ```bash
   # ログを確認
   tail -f /tmp/auth-service.log
   
   # Auth Service にアクセスできるか確認
   curl http://localhost:8081/actuator/health
   ```

3. **データベースにテストユーザーが存在するか確認**
   ```bash
   # PostgreSQLに接続
   psql -h localhost -U postgres -d auth_db
   
   # ユーザー確認
   SELECT username, email, enabled FROM users;
   ```

### フロントエンドがバックエンドに接続できない

**問題:** API呼び出しが失敗する（CORS エラーなど）

**解決方法:**

1. **BFF Service が起動しているか確認**
   ```bash
   # BFF Service のステータス確認
   curl http://localhost:8080/actuator/health
   
   # ログを確認
   tail -f /tmp/bff-service.log
   ```

2. **全サービスが起動しているか確認**
   ```bash
   # すべてのサービスポートを確認
   netstat -tln | grep -E ':(8080|8081|8082|8083).*LISTEN'
   ```

3. **フロントエンドの環境設定を確認**
   ```bash
   # src/frontend/src/environments/environment.ts
   # apiUrl が http://localhost:8080 を指していることを確認
   ```

### ビルドエラー

**問題:** Maven ビルドが失敗する

**解決方法:**

1. **Shared Library を最初にビルド**
   ```bash
   cd src/backend/shared-lib
   mvn clean install
   ```

2. **依存関係をクリア**
   ```bash
   cd src/backend/{service-name}
   mvn clean
   mvn install
   ```

3. **キャッシュをクリア**
   ```bash
   rm -rf ~/.m2/repository/com/example/shared-lib
   cd src/backend/shared-lib
   mvn clean install
   ```

### Frontend のビルド・起動エラー

**問題:** `npm start` や `npm run build` が失敗する

**解決方法:**

1. **node_modules を再インストール**
   ```bash
   cd src/frontend
   rm -rf node_modules package-lock.json
   npm install
   ```

2. **Node.js と npm のバージョンを確認**
   ```bash
   node --version  # v14以上推奨
   npm --version   # v6以上推奨
   ```

3. **Angular CLI を再インストール**
   ```bash
   npm uninstall -g @angular/cli
   npm install -g @angular/cli@16
   ```

### データベースの初期化

**問題:** データベースのデータが破損またはリセットが必要

**解決方法:**

1. **DevContainer でデータベースを再初期化**
   ```bash
   # VS Code: コマンドパレット → "Dev Containers: Rebuild Container"
   ```

2. **手動でデータベースを再作成**
   ```bash
   # PostgreSQL に接続
   psql -h localhost -U postgres
   
   # データベースを削除して再作成
   DROP DATABASE auth_db;
   DROP DATABASE user_db;
   DROP DATABASE permission_db;
   
   CREATE DATABASE auth_db;
   CREATE DATABASE user_db;
   CREATE DATABASE permission_db;
   
   # 各サービスを再起動（スキーマとデータが自動作成されます）
   cd src/backend
   ./stop-all-services.sh
   ./start-all-services.sh
   ```

### ログの確認方法

各サービスのログファイル：
- **Auth Service:** `/tmp/auth-service.log`
- **User Service:** `/tmp/user-service.log`
- **Permission Service:** `/tmp/permission-service.log`
- **BFF Service:** `/tmp/bff-service.log`

ログをリアルタイムで確認：
```bash
tail -f /tmp/auth-service.log
tail -f /tmp/bff-service.log
```

全ログを同時に確認：
```bash
tail -f /tmp/*-service.log
```

### それでも解決しない場合

1. **Issue を作成**: [GitHub Issues](https://github.com/Takas0522/ws-demo-angular/issues)
2. **以下の情報を含めてください:**
   - エラーメッセージ
   - 関連するログファイルの内容
   - 実行した手順
   - 環境情報（OS、Docker Desktop バージョンなど）

## ライセンス

MIT License

## 貢献

Issue や Pull Request を歓迎します！