#!/bin/bash

# スクリプトの実行ディレクトリを取得
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=========================================="
echo "全バックエンドサービスを起動します"
echo "=========================================="
echo ""

# 既存のSpring Bootプロセスを停止
echo "既存のサービスを停止中..."
pkill -f 'spring-boot:run' 2>/dev/null
sleep 3

echo ""
echo "サービスを起動中..."
echo ""

# Auth Service
echo "[1/4] Auth Service を起動中 (Port 8081)..."
cd "${SCRIPT_DIR}/auth-service"
mvn spring-boot:run > /tmp/auth-service.log 2>&1 &
AUTH_PID=$!
echo "  ├─ PID: ${AUTH_PID}"
echo "  └─ ログ: /tmp/auth-service.log"
sleep 10

# User Service
echo ""
echo "[2/4] User Service を起動中 (Port 8082)..."
cd "${SCRIPT_DIR}/user-service"
mvn spring-boot:run > /tmp/user-service.log 2>&1 &
USER_PID=$!
echo "  ├─ PID: ${USER_PID}"
echo "  └─ ログ: /tmp/user-service.log"
sleep 10

# Permission Service
echo ""
echo "[3/4] Permission Service を起動中 (Port 8083)..."
cd "${SCRIPT_DIR}/permission-service"
mvn spring-boot:run > /tmp/permission-service.log 2>&1 &
PERMISSION_PID=$!
echo "  ├─ PID: ${PERMISSION_PID}"
echo "  └─ ログ: /tmp/permission-service.log"
sleep 10

# BFF Service
echo ""
echo "[4/4] BFF Service を起動中 (Port 8080)..."
cd "${SCRIPT_DIR}/bff-service"
mvn spring-boot:run > /tmp/bff-service.log 2>&1 &
BFF_PID=$!
echo "  ├─ PID: ${BFF_PID}"
echo "  └─ ログ: /tmp/bff-service.log"

echo ""
echo "サービスの起動を待機中..."
sleep 30

echo ""
echo "=========================================="
echo "サービス状態を確認中..."
echo "=========================================="

# ポートの状態を確認
if command -v netstat &> /dev/null; then
    netstat -tln | grep -E ':(8080|8081|8082|8083).*LISTEN' || echo "注意: いくつかのサービスがまだ起動していない可能性があります"
elif command -v ss &> /dev/null; then
    ss -tln | grep -E ':(8080|8081|8082|8083)' || echo "注意: いくつかのサービスがまだ起動していない可能性があります"
else
    echo "注意: netstatまたはssコマンドが見つかりません。ポート状態を確認できません。"
fi

echo ""
echo "=========================================="
echo "全サービスが起動しました！"
echo "=========================================="
echo ""
echo "サービスURL:"
echo "  - BFF Service:        http://localhost:8080"
echo "  - Auth Service:       http://localhost:8081"
echo "  - User Service:       http://localhost:8082"
echo "  - Permission Service: http://localhost:8083"
echo ""
echo "ログファイル:"
echo "  - Auth Service:       /tmp/auth-service.log"
echo "  - User Service:       /tmp/user-service.log"
echo "  - Permission Service: /tmp/permission-service.log"
echo "  - BFF Service:        /tmp/bff-service.log"
echo ""
echo "サービスを停止するには: ./stop-all-services.sh"
echo "=========================================="
