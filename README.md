# WS Demo Angular - Microservices Architecture

マイクロサービス風味のアーキテクチャを採用したWebアプリケーションのデモプロジェクト

## アーキテクチャ概要

このプロジェクトは、BFF（Backend For Frontend）パターンを採用したマイクロサービスアーキテクチャで構成されています。

## 開発環境セットアップ

### 前提条件(動かす場合)

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
