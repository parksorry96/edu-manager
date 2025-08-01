#!/bin/bash
# deploy-backend.sh - EC2 백엔드 배포 스크립트

set -e  # 에러 발생시 즉시 중단

echo "=== EDU-MANAGER 백엔드 배포 시작 ==="

# 환경 변수 로드
if [ -f .env.production ]; then
    export $(cat .env.production | grep -v '^#' | xargs)
else
    echo "❌ .env.production 파일이 없습니다!"
    exit 1
fi

# 1. Git 최신 코드 가져오기
echo "1. 최신 코드 가져오기..."
git pull origin main

# 2. 백엔드 빌드
echo "2. 백엔드 애플리케이션 빌드..."
cd backend
chmod +x gradlew
./gradlew clean build -x test
cd ..

# 3. 기존 컨테이너 정지 및 제거
echo "3. 기존 컨테이너 정리..."
docker-compose -f docker-compose.prod.yml down

# 4. Docker 이미지 빌드
echo "4. Docker 이미지 빌드..."
docker-compose -f docker-compose.prod.yml build --no-cache

# 5. 새 컨테이너 시작
echo "5. 새 컨테이너 시작..."
docker-compose -f docker-compose.prod.yml up -d

# 6. 헬스체크
echo "6. 헬스체크 대기..."
sleep 30
if curl -f http://localhost:8080/actuator/health; then
    echo "✅ 백엔드 배포 성공!"
else
    echo "❌ 백엔드 헬스체크 실패!"
    docker-compose -f docker-compose.prod.yml logs
    exit 1
fi

# 7. 로그 확인
echo "7. 최근 로그 확인..."
docker-compose -f docker-compose.prod.yml logs --tail 50

echo "=== 배포 완료 ==="
