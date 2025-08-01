#!/bin/bash
# scripts/restore.sh
# 백업에서 복원하는 스크립트

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 사용법 확인
if [ "$#" -ne 1 ]; then
    echo "사용법: $0 <백업날짜>"
    echo "예: $0 20240115_120000"
    echo ""
    echo "사용 가능한 백업:"
    ls -la /home/ubuntu/edu-manager/backup/db/*.sql.gz 2>/dev/null | awk '{print $9}' | sed 's/.*edumanager_//' | sed 's/.sql.gz//' || echo "백업 없음"
    exit 1
fi

BACKUP_DATE=$1
BACKUP_DIR="/home/ubuntu/edu-manager/backup"

# 환경 변수 로드
source /home/ubuntu/edu-manager/.env.production

echo "=== EDU-MANAGER 복원 시작 ==="
echo "복원할 백업: $BACKUP_DATE"
echo ""

# 확인
echo -e "${YELLOW}경고: 이 작업은 현재 데이터를 모두 삭제하고 백업으로 대체합니다!${NC}"
read -p "정말 계속하시겠습니까? (yes/no): " confirm
if [ "$confirm" != "yes" ]; then
    echo "복원 취소됨"
    exit 0
fi

# 1. 서비스 중지
echo "1. 서비스 중지 중..."
docker-compose -f docker-compose.prod.yml stop backend
echo "   ✓ 백엔드 서비스 중지 완료"

# 2. 데이터베이스 복원
echo "2. 데이터베이스 복원 중..."
if [ -f "$BACKUP_DIR/db/edumanager_${BACKUP_DATE}.sql.gz" ]; then
    # 압축 해제
    gunzip -c "$BACKUP_DIR/db/edumanager_${BACKUP_DATE}.sql.gz" > "/tmp/restore_${BACKUP_DATE}.sql"
    
    # 기존 데이터베이스 초기화
    PGPASSWORD=$DB_PASSWORD psql \
        -h $RDS_ENDPOINT \
        -U $DB_USER \
        -d postgres \
        -c "DROP DATABASE IF EXISTS $DB_NAME;"
    
    PGPASSWORD=$DB_PASSWORD psql \
        -h $RDS_ENDPOINT \
        -U $DB_USER \
        -d postgres \
        -c "CREATE DATABASE $DB_NAME;"
    
    # 복원
    PGPASSWORD=$DB_PASSWORD psql \
        -h $RDS_ENDPOINT \
        -U $DB_USER \
        -d $DB_NAME \
        < "/tmp/restore_${BACKUP_DATE}.sql"
    
    # 임시 파일 삭제
    rm "/tmp/restore_${BACKUP_DATE}.sql"
    
    echo "   ✓ 데이터베이스 복원 완료"
else
    echo -e "   ${RED}✗ 데이터베이스 백업 파일을 찾을 수 없습니다${NC}"
    exit 1
fi

# 3. 업로드 파일 복원
echo "3. 업로드 파일 복원 중..."
if [ -f "$BACKUP_DIR/files/uploads_${BACKUP_DATE}.tar.gz" ]; then
    # 기존 업로드 디렉토리 백업
    if [ -d "/home/ubuntu/edu-manager/uploads" ]; then
        mv /home/ubuntu/edu-manager/uploads "/home/ubuntu/edu-manager/uploads.old.$(date +%Y%m%d_%H%M%S)"
    fi
    
    # 복원
    tar -xzf "$BACKUP_DIR/files/uploads_${BACKUP_DATE}.tar.gz" \
        -C /home/ubuntu/edu-manager/
    
    echo "   ✓ 업로드 파일 복원 완료"
else
    echo "   - 업로드 파일 백업이 없습니다"
fi

# 4. 설정 파일 복원 (선택사항)
read -p "설정 파일도 복원하시겠습니까? (yes/no): " restore_config
if [ "$restore_config" = "yes" ]; then
    echo "4. 설정 파일 복원 중..."
    if [ -f "$BACKUP_DIR/files/config_${BACKUP_DATE}.tar.gz" ]; then
        # 현재 설정 백업
        cp /home/ubuntu/edu-manager/.env.production "/home/ubuntu/edu-manager/.env.production.backup.$(date +%Y%m%d_%H%M%S)"
        
        # 복원
        tar -xzf "$BACKUP_DIR/files/config_${BACKUP_DATE}.tar.gz" \
            -C /home/ubuntu/edu-manager/
        
        echo "   ✓ 설정 파일 복원 완료"
    else
        echo "   - 설정 파일 백업이 없습니다"
    fi
fi

# 5. Redis 캐시 초기화
echo "5. Redis 캐시 초기화 중..."
docker-compose -f docker-compose.prod.yml exec -T backend redis-cli -h $REDIS_ENDPOINT -p 6379 -a $REDIS_PASSWORD FLUSHALL || true
echo "   ✓ Redis 캐시 초기화 완료"

# 6. 서비스 재시작
echo "6. 서비스 재시작 중..."
docker-compose -f docker-compose.prod.yml start backend
sleep 10
echo "   ✓ 백엔드 서비스 시작 완료"

# 7. 헬스체크
echo "7. 시스템 상태 확인 중..."
sleep 5
if curl -f -s http://localhost:8080/actuator/health > /dev/null; then
    echo -e "   ${GREEN}✓ 시스템 정상 작동${NC}"
else
    echo -e "   ${RED}✗ 시스템 응답 없음${NC}"
    echo "   로그를 확인하세요:"
    echo "   docker-compose -f docker-compose.prod.yml logs backend"
fi

echo ""
echo "=== 복원 완료 ==="
