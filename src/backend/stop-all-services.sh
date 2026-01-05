#!/bin/bash

echo "=========================================="
echo "全バックエンドサービスを停止します"
echo "=========================================="
echo ""

# Spring Bootプロセスを停止
echo "Spring Boot プロセスを停止中..."
pkill -f 'spring-boot:run'

# プロセスの終了を待機
sleep 3

# 必要に応じて強制終了
if pgrep -f 'spring-boot:run' > /dev/null; then
    echo "残っているプロセスを強制停止中..."
    pkill -9 -f 'spring-boot:run'
    sleep 2
fi

echo ""
echo "=========================================="
echo "サービス状態を確認中..."
echo "=========================================="

# プロセスが停止したか確認
if pgrep -f 'spring-boot:run' > /dev/null; then
    echo "警告: 一部のプロセスがまだ実行中です"
    echo ""
    echo "実行中のプロセス:"
    ps aux | grep 'spring-boot:run' | grep -v grep
    echo ""
    echo "手動で停止するには:"
    echo "  kill -9 <PID>"
else
    echo "✓ 全サービスが正常に停止しました"
fi

echo ""

# ポートの状態を確認
echo "ポート状態を確認中..."
if command -v netstat &> /dev/null; then
    netstat -tln | grep -E ':(8080|8081|8082|8083).*LISTEN' && echo "注意: 一部のポートがまだ使用中です" || echo "✓ 全サービスポートが解放されました"
elif command -v ss &> /dev/null; then
    ss -tln | grep -E ':(8080|8081|8082|8083)' && echo "注意: 一部のポートがまだ使用中です" || echo "✓ 全サービスポートが解放されました"
else
    echo "注意: netstatまたはssコマンドが見つかりません。ポート状態を確認できません。"
fi

echo ""
echo "=========================================="
echo "完了"
echo "=========================================="
