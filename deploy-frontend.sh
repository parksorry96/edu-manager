#!/bin/bash
# deploy-frontend.sh - S3 프론트엔드 배포 스크립트

set -e  # 에러 발생시 즉시 중단

echo "=== EDU-MANAGER 프론트엔드 S3 배포 시작 ==="

# 환경 변수 설정
S3_BUCKET_NAME="edu-manager-frontend"
CLOUDFRONT_DISTRIBUTION_ID="YOUR_DISTRIBUTION_ID"
AWS_REGION="ap-northeast-2"

# 1. 프론트엔드 빌드
echo "1. 프론트엔드 빌드..."
cd frontend

# 프로덕션 환경 변수 설정
cat > .env.production << EOF
VITE_API_URL=https://api.yourdomain.com
VITE_APP_ENV=production
EOF

# 의존성 설치 및 빌드
pnpm install
pnpm build

# 2. S3 버킷으로 업로드
echo "2. S3 버킷으로 업로드..."
aws s3 sync dist/ s3://${S3_BUCKET_NAME}/ \
    --delete \
    --cache-control "public, max-age=31536000" \
    --exclude "index.html" \
    --exclude "*.json"

# index.html은 캐시하지 않음
aws s3 cp dist/index.html s3://${S3_BUCKET_NAME}/index.html \
    --cache-control "no-cache, no-store, must-revalidate" \
    --content-type "text/html"

# 3. CloudFront 캐시 무효화
echo "3. CloudFront 캐시 무효화..."
aws cloudfront create-invalidation \
    --distribution-id ${CLOUDFRONT_DISTRIBUTION_ID} \
    --paths "/*"

echo "✅ 프론트엔드 배포 완료!"
echo "CloudFront URL: https://your-distribution.cloudfront.net"

cd ..
