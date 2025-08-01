# EDU-MANAGER AWS 마이그레이션 요약

## 📁 생성된 파일 구조

```
edu-manager/
├── .env.production              # 프로덕션 환경 변수
├── docker-compose.prod.yml      # 프로덕션 Docker 설정
├── deploy-backend.sh            # EC2 백엔드 배포 스크립트
├── deploy-frontend.sh           # S3 프론트엔드 배포 스크립트
├── AWS_DEPLOYMENT_GUIDE.md      # 상세 배포 가이드
├── backend/
│   ├── Dockerfile              # 백엔드 Docker 이미지
│   └── src/main/resources/
│       └── application-prod.yml # Spring Boot 프로덕션 설정
├── terraform/                   # AWS 인프라 코드
│   ├── main.tf                 # Terraform 메인 설정
│   ├── terraform.tfvars.example # Terraform 변수 예제
│   └── user_data.sh            # EC2 초기화 스크립트
├── nginx/
│   └── nginx-backend.conf      # Nginx 설정
├── scripts/                    # 유틸리티 스크립트
│   ├── health-check.sh         # 시스템 헬스체크
│   ├── backup.sh               # 백업 스크립트
│   ├── restore.sh              # 복원 스크립트
│   ├── setup-monitoring.sh     # CloudWatch 설정
│   └── setup-ssl.sh            # SSL 인증서 설정
└── .github/workflows/
    └── deploy.yml              # GitHub Actions 자동 배포
```

## 🏗️ 권장 아키텍처

### 1. **관리형 서비스 활용 (권장)**
- **프론트엔드**: S3 + CloudFront
- **백엔드**: EC2 (Docker)
- **데이터베이스**: RDS PostgreSQL
- **캐시**: ElastiCache Redis
- **로드밸런서**: ALB

**장점**:
- AWS가 관리하는 서비스로 운영 부담 감소
- 자동 백업, 모니터링, 패치 관리
- 고가용성 보장
- 월 약 $45-60 비용

### 2. **EC2 단일 서버 (비용 절감)**
- 모든 서비스를 EC2에서 Docker로 실행
- PostgreSQL, Redis도 Docker 컨테이너로 운영

**장점**:
- 비용 절감 (월 약 $15-20)
- 간단한 구성

**단점**:
- 직접 관리 필요
- 단일 장애점
- 백업/복구 직접 구현

## 🚀 빠른 시작 가이드

### 1단계: AWS 계정 준비
```bash
# AWS CLI 설치 및 설정
aws configure
```

### 2단계: 인프라 구성 (Terraform)
```bash
cd terraform
terraform init
terraform apply
```

### 3단계: EC2 백엔드 배포
```bash
# EC2 접속
ssh -i your-key.pem ubuntu@<EC2_IP>

# 프로젝트 클론 및 배포
git clone <your-repo>
cd edu-manager
./deploy-backend.sh
```

### 4단계: S3 프론트엔드 배포
```bash
# 로컬에서 실행
./deploy-frontend.sh
```

## 🔐 보안 설정

1. **환경 변수 관리**
   - `.env.production`은 절대 Git에 커밋하지 않기
   - AWS Secrets Manager 사용 권장

2. **네트워크 보안**
   - RDS/Redis는 프라이빗 서브넷에 배치
   - Security Group으로 필요한 포트만 개방
   - SSH는 특정 IP만 허용

3. **SSL/TLS**
   - Let's Encrypt로 무료 SSL 인증서
   - CloudFront에서 자동 HTTPS

## 📈 모니터링

- **CloudWatch**: 시스템 메트릭, 로그
- **Health Check**: `/scripts/health-check.sh`
- **백업**: 일일 자동 백업 (cron)

## 💡 추가 권장사항

1. **도메인 설정**
   - Route53으로 DNS 관리
   - api.yourdomain.com (백엔드)
   - app.yourdomain.com (프론트엔드)

2. **CI/CD**
   - GitHub Actions 자동 배포 설정 완료
   - 환경별 브랜치 전략 (main, production)

3. **백업 전략**
   - RDS 자동 백업 (7일 보관)
   - S3 백업 (Glacier 장기 보관)
   - 일일 백업 스크립트

4. **성능 최적화**
   - CloudFront 캐싱
   - Redis 캐싱
   - Docker 이미지 최적화

## 🎯 다음 단계

1. Terraform 변수 설정 후 인프라 생성
2. 환경 변수 실제 값으로 수정
3. 도메인 연결
4. SSL 인증서 설정
5. 모니터링 및 알람 설정

모든 스크립트는 실행 가능하도록 준비되어 있습니다. 
문제가 발생하면 로그를 확인하고 AWS 콘솔에서 리소스 상태를 점검하세요.
