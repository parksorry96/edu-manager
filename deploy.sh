#!/bin/bash

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Edu-Manager 배포 스크립트 ===${NC}"

# 환경 변수 파일 확인
if [ ! -f .env.production ]; then
    echo -e "${RED}오류: .env.production 파일이 없습니다.${NC}"
    echo -e "${YELLOW}.env.production.example을 참고하여 생성해주세요.${NC}"
    exit 1
fi

# 환경 변수 로드
export $(cat .env.production | xargs)

# Docker 상태 확인
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}오류: Docker가 실행되지 않았습니다.${NC}"
    exit 1
fi

# 현재 실행 중인 컨테이너 정지
echo -e "${YELLOW}기존 컨테이너 정지 중...${NC}"
docker-compose -f docker-compose.prod.yml down

# GitHub Container Registry 로그인
echo -e "${YELLOW}GitHub Container Registry 로그인 중...${NC}"
echo $GITHUB_TOKEN | docker login ghcr.io -u $GITHUB_REPOSITORY_OWNER --password-stdin

# 최신 이미지 pull
echo -e "${YELLOW}최신 이미지 다운로드 중...${NC}"
docker-compose -f docker-compose.prod.yml pull

# 서비스 시작
echo -e "${YELLOW}서비스 시작 중...${NC}"
docker-compose -f docker-compose.prod.yml up -d

# 헬스체크
echo -e "${YELLOW}서비스 상태 확인 중...${NC}"
sleep 10

# 컨테이너 상태 확인
if docker-compose -f docker-compose.prod.yml ps | grep -q "unhealthy\|Exit"; then
    echo -e "${RED}오류: 일부 서비스가 정상적으로 시작되지 않았습니다.${NC}"
    docker-compose -f docker-compose.prod.yml ps
    echo -e "${YELLOW}로그 확인: docker-compose -f docker-compose.prod.yml logs${NC}"
    exit 1
fi

echo -e "${GREEN}배포가 성공적으로 완료되었습니다!${NC}"
docker-compose -f docker-compose.prod.yml ps

# 오래된 이미지 정리
echo -e "${YELLOW}오래된 이미지 정리 중...${NC}"
docker image prune -f

echo -e "${GREEN}=== 배포 완료 ===${NC}"
