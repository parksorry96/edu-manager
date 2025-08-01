#!/bin/bash

# EC2 초기 설정 스크립트

echo "=== EC2 서버 초기 설정 시작 ==="

# 1. 프로젝트 클론
echo "1. 프로젝트 클론..."
cd /home/ubuntu
git clone https://github.com/YOUR_GITHUB_USERNAME/edu-manager.git
cd edu-manager

# 2. 환경 변수 파일 생성
echo "2. 환경 변수 파일 생성..."
cp .env.production.example .env.production
echo "⚠️  .env.production 파일을 편집하여 실제 값을 입력하세요:"
echo "   nano .env.production"

# 3. 필요한 디렉토리 생성
echo "3. 필요한 디렉토리 생성..."
mkdir -p logs uploads

# 4. 실행 권한 부여
chmod +x deploy.sh

# 5. Nginx 설정
echo "4. Nginx 설정..."
sudo cp nginx.conf.prod /etc/nginx/sites-available/edu-manager
sudo ln -s /etc/nginx/sites-available/edu-manager /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx

echo "=== 초기 설정 완료 ==="
echo ""
echo "다음 단계:"
echo "1. .env.production 파일 편집"
echo "2. GitHub Secrets 설정:"
echo "   - EC2_HOST: EC2 퍼블릭 IP"
echo "   - EC2_SSH_KEY: EC2 프라이빗 키"
echo "3. ./deploy.sh 실행하여 배포"
