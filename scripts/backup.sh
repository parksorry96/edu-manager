#!/bin/bash
# scripts/backup.sh
# 데이터베이스 및 파일 백업 스크립트

set -e

# 설정
BACKUP_DIR="/home/ubuntu/edu-manager/backup"
DATE=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=7

# 환경 변수 로드
source /home/ubuntu/edu-manager/.env.production

echo "=== EDU-MANAGER 백업 시작 ==="
echo "백업 날짜: $DATE"

# 백업 디렉토리 생성
mkdir -p "$BACKUP_DIR/db" "$BACKUP_DIR/files"

# 1. PostgreSQL 백업
echo "1. 데이터베이스 백업 중..."
PGPASSWORD=$DB_PASSWORD pg_dump \
    -h $RDS_ENDPOINT \
    -U $DB_USER \
    -d $DB_NAME \
    --no-owner \
    --no-acl \
    -f "$BACKUP_DIR/db/edumanager_${DATE}.sql"

# 압축
gzip "$BACKUP_DIR/db/edumanager_${DATE}.sql"
echo "   ✓ 데이터베이스 백업 완료: edumanager_${DATE}.sql.gz"

# 2. 업로드 파일 백업
echo "2. 업로드 파일 백업 중..."
if [ -d "/home/ubuntu/edu-manager/uploads" ]; then
    tar -czf "$BACKUP_DIR/files/uploads_${DATE}.tar.gz" \
        -C /home/ubuntu/edu-manager uploads
    echo "   ✓ 파일 백업 완료: uploads_${DATE}.tar.gz"
else
    echo "   - 업로드 디렉토리가 없습니다."
fi

# 3. 설정 파일 백업
echo "3. 설정 파일 백업 중..."
tar -czf "$BACKUP_DIR/files/config_${DATE}.tar.gz" \
    -C /home/ubuntu/edu-manager \
    --exclude=node_modules \
    --exclude=.git \
    --exclude=build \
    --exclude=dist \
    --exclude=logs \
    --exclude=backup \
    .env.production \
    docker-compose.prod.yml \
    nginx/

echo "   ✓ 설정 파일 백업 완료: config_${DATE}.tar.gz"

# 4. S3로 백업 업로드 (선택사항)
if [ ! -z "$BACKUP_S3_BUCKET" ]; then
    echo "4. S3로 백업 업로드 중..."
    aws s3 cp "$BACKUP_DIR/db/edumanager_${DATE}.sql.gz" \
        "s3://$BACKUP_S3_BUCKET/db/" --storage-class GLACIER
    
    aws s3 cp "$BACKUP_DIR/files/uploads_${DATE}.tar.gz" \
        "s3://$BACKUP_S3_BUCKET/files/" --storage-class GLACIER
    
    aws s3 cp "$BACKUP_DIR/files/config_${DATE}.tar.gz" \
        "s3://$BACKUP_S3_BUCKET/config/"
    
    echo "   ✓ S3 업로드 완료"
fi

# 5. 오래된 백업 삭제
echo "5. 오래된 백업 삭제 중..."
find "$BACKUP_DIR" -type f -mtime +$RETENTION_DAYS -delete
echo "   ✓ ${RETENTION_DAYS}일 이상 된 백업 삭제 완료"

# 6. 백업 크기 정보
echo ""
echo "백업 완료 정보:"
echo "----------------"
du -sh "$BACKUP_DIR/db/edumanager_${DATE}.sql.gz" 2>/dev/null || true
du -sh "$BACKUP_DIR/files/uploads_${DATE}.tar.gz" 2>/dev/null || true
du -sh "$BACKUP_DIR/files/config_${DATE}.tar.gz" 2>/dev/null || true
echo "----------------"
echo "전체 백업 크기: $(du -sh $BACKUP_DIR | cut -f1)"

echo ""
echo "=== 백업 완료 ==="
