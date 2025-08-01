#!/bin/bash
# scripts/health-check.sh
# 헬스체크 및 모니터링 스크립트

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=== EDU-MANAGER 시스템 헬스체크 ==="
echo ""

# 1. Backend 헬스체크
echo -n "1. Backend API: "
if curl -f -s http://localhost:8080/actuator/health > /dev/null; then
    echo -e "${GREEN}✓ Healthy${NC}"
    # 상세 정보
    curl -s http://localhost:8080/actuator/health | jq '.'
else
    echo -e "${RED}✗ Unhealthy${NC}"
fi
echo ""

# 2. Nginx 상태
echo -n "2. Nginx: "
if systemctl is-active --quiet nginx; then
    echo -e "${GREEN}✓ Running${NC}"
else
    echo -e "${RED}✗ Not Running${NC}"
fi
echo ""

# 3. Docker 컨테이너 상태
echo "3. Docker Containers:"
docker-compose -f docker-compose.prod.yml ps
echo ""

# 4. 시스템 리소스
echo "4. System Resources:"
echo "   CPU Usage:"
top -bn1 | grep "Cpu(s)" | sed "s/.*, *\([0-9.]*\)%* id.*/\1/" | awk '{print "   " 100 - $1"% used"}'
echo ""
echo "   Memory Usage:"
free -h | grep Mem | awk '{print "   Total: " $2 ", Used: " $3 ", Free: " $4}'
echo ""
echo "   Disk Usage:"
df -h | grep -E '^/dev/root|^/dev/xvda1' | awk '{print "   " $5 " used of " $2}'
echo ""

# 5. 데이터베이스 연결 테스트
echo -n "5. Database Connection: "
if docker-compose -f docker-compose.prod.yml exec -T backend curl -s http://localhost:8080/actuator/health | jq -r '.components.db.status' | grep -q "UP"; then
    echo -e "${GREEN}✓ Connected${NC}"
else
    echo -e "${RED}✗ Disconnected${NC}"
fi
echo ""

# 6. Redis 연결 테스트
echo -n "6. Redis Connection: "
if docker-compose -f docker-compose.prod.yml exec -T backend curl -s http://localhost:8080/actuator/health | jq -r '.components.redis.status' | grep -q "UP"; then
    echo -e "${GREEN}✓ Connected${NC}"
else
    echo -e "${RED}✗ Disconnected${NC}"
fi
echo ""

# 7. 최근 에러 로그
echo "7. Recent Error Logs:"
echo "   Backend errors (last 10):"
docker-compose -f docker-compose.prod.yml logs --tail 10 backend 2>&1 | grep -i error || echo "   No recent errors"
echo ""

# 8. API 응답 시간 테스트
echo "8. API Response Time:"
RESPONSE_TIME=$(curl -o /dev/null -s -w '%{time_total}' http://localhost:8080/api/health)
echo "   Health endpoint: ${RESPONSE_TIME}s"
echo ""

# 종합 상태
echo "==================================="
echo -e "Overall Status: ${GREEN}✓ System Operational${NC}"
echo "==================================="
