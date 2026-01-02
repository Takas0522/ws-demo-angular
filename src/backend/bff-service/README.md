# BFF Service (Backend For Frontend)

BFF（Backend For Frontend）サービス - フロントエンドアプリケーション用のAPIゲートウェイ

## 概要

BFF Service は、マイクロサービスアーキテクチャにおけるフロントエンド専用のAPIゲートウェイです。
フロントエンドアプリケーションからの全てのAPIリクエストを受け付け、適切なバックエンドサービスにルーティングします。

## アーキテクチャ

```
Frontend → BFF Service (Port 8080) → Backend Services
                                     ├── Auth Service (Port 8081)
                                     ├── User Service (Port 8082)
                                     └── Permission Service (Port 8083)
```

## 機能

- **APIルーティング**: フロントエンドからのリクエストを適切なバックエンドサービスにプロキシ
- **ヘッダー転送**: 認証トークン（JWT）などの重要なヘッダーを自動的に転送
- **統一エンドポイント**: フロントエンドは単一のエンドポイント（BFF）のみを意識

## API エンドポイント

### 認証関連 (Auth Service へプロキシ)

- `POST /api/auth/login` - ログイン
- `POST /api/auth/refresh` - トークンリフレッシュ
- `POST /api/auth/logout` - ログアウト
- `POST /api/auth/register` - ユーザー登録

### ユーザー管理関連 (User Service へプロキシ)

- `GET /api/users` - 全ユーザープロファイル取得
- `GET /api/users/{userId}` - ユーザープロファイル取得
- `POST /api/users` - ユーザープロファイル作成
- `PUT /api/users/{userId}` - ユーザープロファイル更新
- `DELETE /api/users/{userId}` - ユーザープロファイル削除

### 権限管理関連 (Permission Service へプロキシ)

- `GET /api/applications` - 全アプリケーション取得
- `GET /api/users/{userId}/permissions` - ユーザー権限取得
- `POST /api/permissions` - 権限作成
- `PUT /api/permissions/{permissionId}` - 権限更新
- `DELETE /api/permissions/{permissionId}` - 権限削除

## 設定

### バックエンドサービスURL

デフォルトではローカルホストを使用しますが、環境変数で変更可能です：

```yaml
backend:
  services:
    auth:
      url: ${AUTH_SERVICE_URL:http://localhost:8081}
    user:
      url: ${USER_SERVICE_URL:http://localhost:8082}
    permission:
      url: ${PERMISSION_SERVICE_URL:http://localhost:8083}
```

### 環境変数

- `AUTH_SERVICE_URL`: Auth Service のURL（デフォルト: http://localhost:8081）
- `USER_SERVICE_URL`: User Service のURL（デフォルト: http://localhost:8082）
- `PERMISSION_SERVICE_URL`: Permission Service のURL（デフォルト: http://localhost:8083）
- `JWT_PUBLIC_KEY_PATH`: JWT公開鍵のパス（デフォルト: /keys/public_key.pem）

## ビルド

```bash
cd src/backend/bff-service
mvn clean install
```

## 実行

```bash
mvn spring-boot:run
```

サービスは http://localhost:8080 で起動します。

## テスト

```bash
mvn test
```

## 依存関係

- Spring Boot 2.7.18
- Spring WebFlux (WebClient for proxying)
- Spring Web
- Shared Library (共通DTO、例外処理)
- Java 11

## 使用方法

### フロントエンドからの利用

フロントエンドアプリケーションは、全てのAPIリクエストをBFFサービス（http://localhost:8080）に送信します。

例：ログインリクエスト

```javascript
fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'admin',
    password: 'password123'
  })
})
```

BFFサービスは自動的にAuth Service（http://localhost:8081/api/auth/login）にリクエストを転送し、
レスポンスをフロントエンドに返します。

### 認証トークンの扱い

JWTトークンは自動的にAuthorizationヘッダーとして各バックエンドサービスに転送されます：

```javascript
fetch('http://localhost:8080/api/users/1', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer <jwt-token>'
  }
})
```

## ログ

BFFサービスは全てのリクエストをログに記録します：

```
2024-01-01 12:00:00.000  INFO - BFF: POST /api/auth/login
2024-01-01 12:00:00.100 DEBUG - Forwarding POST request to http://localhost:8081/api/auth/login
```

## 今後の拡張

- リクエスト/レスポンスのキャッシング
- レート制限
- リクエスト変換・集約
- エラーハンドリングの統一
- メトリクス収集
- サーキットブレーカー
- リトライ機能

## 関連サービス

- Auth Service (ポート 8081) - 認証サービス
- User Service (ポート 8082) - ユーザープロファイル管理サービス
- Permission Service (ポート 8083) - 権限管理サービス
