# アーキテクチャ図

## システム全体構成

```
┌─────────────────────────────────────────────────────────────────┐
│                         Frontend (Angular)                       │
│                      http://localhost:4200                       │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            │ HTTP/REST API
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                       BFF Service                                │
│                   (Backend For Frontend)                         │
│                   http://localhost:8080                          │
│                                                                  │
│  役割：                                                           │
│  - フロントエンド専用のAPIゲートウェイ                           │
│  - バックエンドサービスへのリクエストルーティング                 │
│  - 認証トークン（JWT）の自動転送                                 │
│  - レスポンスの集約（将来的に）                                  │
└────────┬──────────────────┬──────────────────┬─────────────────┘
         │                  │                  │
         │                  │                  │
    ┌────▼─────┐      ┌────▼─────┐      ┌────▼─────┐
    │  Auth    │      │   User   │      │Permission│
    │ Service  │      │ Service  │      │ Service  │
    │  :8081   │      │  :8082   │      │  :8083   │
    └────┬─────┘      └────┬─────┘      └────┬─────┘
         │                  │                  │
    ┌────▼─────┐      ┌────▼─────┐      ┌────▼─────┐
    │ auth_db  │      │ user_db  │      │permission│
    │          │      │          │      │   _db    │
    │PostgreSQL│      │PostgreSQL│      │PostgreSQL│
    └──────────┘      └──────────┘      └──────────┘
```

## API ルーティング

### フロントエンドからのリクエストフロー

```
Frontend                 BFF                  Backend Service
   │                      │                          │
   ├─ POST /api/auth/login ──→ BFF ──→ Auth Service :8081
   │                      │                          │
   ├─ GET /api/users/1 ────→ BFF ──→ User Service :8082
   │                      │                          │
   └─ GET /api/applications ─→ BFF ──→ Permission Service :8083
```

## サービス間通信

### BFF Service の役割

```
┌─────────────────────────────────────────────────────────┐
│                      BFF Service                         │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Controllers:                                            │
│  ├─ AuthController      → /api/auth/**                  │
│  ├─ UserController      → /api/users/**                 │
│  └─ PermissionController → /api/{applications|permissions}/** │
│                                                          │
│  ProxyService:                                           │
│  ├─ forwardToAuthService()                              │
│  ├─ forwardToUserService()                              │
│  └─ forwardToPermissionService()                        │
│                                                          │
│  機能:                                                   │
│  ├─ HTTPヘッダーの転送                                   │
│  ├─ Hop-by-Hopヘッダーのフィルタリング                   │
│  └─ レスポンスの転送                                     │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### 認証フロー

```
1. ログイン
Frontend → BFF → Auth Service → auth_db
                 ↓
           JWT Token生成
                 ↓
Frontend ← BFF ← Auth Service

2. 認証が必要なリクエスト
Frontend → BFF → Backend Service
(+JWT)     (JWT転送)  (JWT検証)
                 ↓
           リソースアクセス
                 ↓
Frontend ← BFF ← Backend Service
```

## データベーススキーマ

### auth_db
- `users` - ユーザー情報
- `refresh_tokens` - リフレッシュトークン
- `saga_state` - Saga状態管理

### user_db
- `user_profiles` - ユーザープロファイル
- `idempotency_keys` - べき等性キー

### permission_db
- `applications` - アプリケーション情報
- `permission_levels` - 権限レベル（READ, WRITE, ADMIN）
- `user_app_permissions` - ユーザー権限マッピング

## 技術スタック

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│                   Angular (Frontend)                     │
└─────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────┐
│                    BFF Layer                             │
│           Spring Boot + WebClient (Proxy)                │
└─────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────┐
│                 Business Logic Layer                     │
│              Spring Boot Microservices                   │
│        (Auth, User, Permission Services)                 │
└─────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────┐
│                    Data Layer                            │
│            PostgreSQL (3 separate databases)             │
└─────────────────────────────────────────────────────────┘
```

## セキュリティ

### JWT 認証フロー

```
1. キー生成（RSA 2048-bit）
   private_key.pem (Auth Serviceのみ使用)
   public_key.pem (全サービスで共有)

2. トークン生成（Auth Service）
   ユーザー認証 → JWT生成（RS256）
                  ├─ Access Token (1時間)
                  └─ Refresh Token (7日間)

3. トークン検証（各Backend Service）
   JWT受信 → public_keyで検証 → アクセス許可/拒否
```

## 拡張性

### 今後の拡張予定

```
BFF Service
├─ キャッシング機能
├─ レート制限
├─ リクエスト変換・集約
├─ エラーハンドリングの統一
├─ サーキットブレーカー
└─ メトリクス収集

Backend Services
├─ サービスディスカバリ（Eureka/Consul）
├─ 分散トレーシング（Zipkin/Jaeger）
├─ メッセージキュー（RabbitMQ/Kafka）
└─ イベント駆動アーキテクチャ
```

## デプロイメント

### DevContainer構成

```
┌─────────────────────────────────────────────────────────┐
│              Docker Compose Environment                  │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ws-demo-angular-app (Dev Container)                    │
│  ├─ Java 11                                             │
│  ├─ Maven                                               │
│  ├─ Node.js + Angular CLI                              │
│  └─ Source Code Volume                                  │
│                                                          │
│  wsdemoangulardb (PostgreSQL)                           │
│  ├─ PostgreSQL 14                                       │
│  ├─ 3 Databases (auth_db, user_db, permission_db)      │
│  └─ Data Volume (persistent)                            │
│                                                          │
└─────────────────────────────────────────────────────────┘
```
