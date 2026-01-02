# User Service

ユーザープロファイル管理サービス - ユーザープロファイルとべき等性キー管理を提供

## 概要

User Service は、マイクロサービスアーキテクチャにおけるユーザープロファイル管理を担当するサービスです。

## 機能

- ユーザープロファイル管理
- べき等性キーによる重複リクエスト防止
- DataSource 再試行設定による高可用性

## データベーススキーマ

### user_profiles テーブル
ユーザープロファイル情報を格納します。

- `id`: プロファイルID（主キー、自動採番）
- `user_id`: ユーザーID（ユニーク、auth_db の users.id に対応）
- `first_name`: 名
- `last_name`: 姓
- `display_name`: 表示名
- `bio`: 自己紹介
- `avatar_url`: アバター画像URL
- `phone_number`: 電話番号
- `date_of_birth`: 生年月日
- `address`: 住所
- `city`: 市区町村
- `state`: 都道府県
- `country`: 国
- `postal_code`: 郵便番号
- `created_at`: 作成日時
- `updated_at`: 更新日時

### idempotency_keys テーブル
重複リクエストを防止するためのべき等性キーを格納します。

- `id`: キーID（主キー、自動採番）
- `idempotency_key`: べき等性キー（ユニーク）
- `request_path`: リクエストパス
- `request_method`: HTTPメソッド
- `response_status_code`: レスポンスステータスコード
- `response_body`: レスポンスボディ
- `created_at`: 作成日時
- `expires_at`: 有効期限

## シードデータ

10 人のテストユーザープロファイルが初期データとして登録されます：

1. `user_id: 1` - Admin User (管理者)
2. `user_id: 2-7` - Regular Users (通常ユーザー)
3. `user_id: 8` - Disabled User (無効化ユーザー)
4. `user_id: 9` - Locked User (ロックユーザー)
5. `user_id: 10` - Expired Credentials User (認証情報期限切れユーザー)

## 設定

### データベース接続

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/user_db
    username: postgres
    password: postgres
```

### DataSource 再試行設定

DataSource は以下の設定で自動的に再試行されます：
- 最大試行回数: 5回
- 再試行間隔: 2秒

## ビルド

```bash
cd src/backend/user-service
mvn clean install
```

## 実行

```bash
mvn spring-boot:run
```

サービスは http://localhost:8082 で起動します。

## テスト

```bash
mvn test
```

## 依存関係

- Spring Boot 2.7.18
- Spring Data JPA
- PostgreSQL
- Shared Library (JWT, DTOs)
- Java 11

## 環境変数

- `POSTGRES_HOSTNAME`: PostgreSQL ホスト名（デフォルト: localhost）
- `POSTGRES_USER`: PostgreSQL ユーザー名（デフォルト: postgres）
- `POSTGRES_PASSWORD`: PostgreSQL パスワード（デフォルト: postgres）

## 関連サービス

- Auth Service (ポート 8081) - 認証サービス
- Permission Service (ポート 8083) - 権限管理サービス
