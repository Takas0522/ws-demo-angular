# Auth Service

認証サービス - JWT ベースの認証とリフレッシュトークン管理を提供

## 概要

Auth Service は、マイクロサービスアーキテクチャにおける認証と認可を担当するサービスです。

## 機能

- ユーザー認証（ログイン）
- JWT トークン生成
- リフレッシュトークン管理
- Saga パターンによる分散トランザクション管理

## データベーススキーマ

### users テーブル
ユーザー情報を格納します。

- `id`: ユーザーID（主キー、自動採番）
- `username`: ユーザー名（ユニーク）
- `password`: BCrypt ハッシュ化されたパスワード
- `email`: メールアドレス（ユニーク）
- `enabled`: アカウントが有効かどうか
- `account_non_expired`: アカウントが期限切れでないか
- `account_non_locked`: アカウントがロックされていないか
- `credentials_non_expired`: 認証情報が期限切れでないか
- `created_at`: 作成日時
- `updated_at`: 更新日時

### refresh_tokens テーブル
リフレッシュトークンを格納します。

- `id`: トークンID（主キー、自動採番）
- `user_id`: ユーザーID（外部キー）
- `token`: リフレッシュトークン（ユニーク）
- `expires_at`: 有効期限
- `created_at`: 作成日時

### saga_state テーブル
分散トランザクションの状態を管理します。

- `id`: Saga ID（主キー、自動採番）
- `saga_id`: Saga の一意識別子（ユニーク）
- `saga_type`: Saga のタイプ
- `current_step`: 現在のステップ
- `status`: ステータス（PENDING, IN_PROGRESS, COMPLETED, FAILED, COMPENSATING, COMPENSATED）
- `payload`: ペイロードデータ（JSON）
- `error_message`: エラーメッセージ
- `created_at`: 作成日時
- `updated_at`: 更新日時

## シードデータ

10 人のテストユーザーが初期データとして登録されます：

1. `admin` - 管理者ユーザー
2. `user1` - `user6` - 通常ユーザー
3. `disabled_user` - 無効化されたユーザー
4. `locked_user` - ロックされたユーザー
5. `expired_creds_user` - 認証情報が期限切れのユーザー

すべてのユーザーのデフォルトパスワード: `password123`

## 設定

### データベース接続

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_db
    username: postgres
    password: postgres
```

### JWT 設定

```yaml
jwt:
  private-key-path: /keys/private_key.pem
  public-key-path: /keys/public_key.pem
  expiration: 3600000  # 1 hour
  refresh-expiration: 604800000  # 7 days
```

## ビルド

```bash
cd backend/auth-service
mvn clean install
```

## 実行

```bash
mvn spring-boot:run
```

サービスは http://localhost:8081 で起動します。

## テスト

```bash
mvn test
```

## 依存関係

- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security
- PostgreSQL
- Shared Library (JWT, DTOs)

## 環境変数

- `POSTGRES_HOSTNAME`: PostgreSQL ホスト名（デフォルト: localhost）
- `POSTGRES_USER`: PostgreSQL ユーザー名（デフォルト: postgres）
- `POSTGRES_PASSWORD`: PostgreSQL パスワード（デフォルト: postgres）
- `JWT_PRIVATE_KEY_PATH`: JWT 秘密鍵のパス（デフォルト: /keys/private_key.pem）
- `JWT_PUBLIC_KEY_PATH`: JWT 公開鍵のパス（デフォルト: /keys/public_key.pem）
