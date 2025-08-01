# scripts/setup-ssl.sh
#!/bin/bash
# Let's Encrypt SSL 인증서 설정 스크립트

set -e

# 사용법 확인
if [ "$#" -ne 2 ]; then
    echo "사용법: $0 <domain> <email>"
    echo "예: $0 api.edumanager.com admin@edumanager.com"
    exit 1
fi

DOMAIN=$1
EMAIL=$2

echo "=== SSL 인증서 설정 ==="
echo "도메인: $DOMAIN"
echo "이메일: $EMAIL"
echo ""

# 1. Certbot 설치 확인
if ! command -v certbot &> /dev/null; then
    echo "1. Certbot 설치 중..."
    sudo apt update
    sudo apt install -y certbot python3-certbot-nginx
else
    echo "1. Certbot이 이미 설치되어 있습니다."
fi

# 2. Nginx 설정 백업
echo "2. Nginx 설정 백업 중..."
sudo cp /etc/nginx/sites-available/edu-manager "/etc/nginx/sites-available/edu-manager.backup.$(date +%Y%m%d_%H%M%S)"

# 3. SSL 인증서 발급
echo "3. SSL 인증서 발급 중..."
sudo certbot --nginx \
    -d $DOMAIN \
    --non-interactive \
    --agree-tos \
    --email $EMAIL \
    --redirect

# 4. Nginx 설정 업데이트
echo "4. Nginx 설정 업데이트 중..."
sudo tee /etc/nginx/sites-available/edu-manager-ssl > /dev/null <<EOF
# HTTP to HTTPS 리다이렉트
server {
    listen 80;
    server_name $DOMAIN;
    return 301 https://\$server_name\$request_uri;
}

# HTTPS 서버 설정
server {
    listen 443 ssl http2;
    server_name $DOMAIN;
    
    # SSL 인증서 (Certbot이 자동으로 설정)
    ssl_certificate /etc/letsencrypt/live/$DOMAIN/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/$DOMAIN/privkey.pem;
    include /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;
    
    # 추가 SSL 보안 설정
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;
    
    # 보안 헤더
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    
    # 로그 설정
    access_log /var/log/nginx/edu-manager-ssl-access.log;
    error_log /var/log/nginx/edu-manager-ssl-error.log;
    
    # 파일 업로드 크기 제한
    client_max_body_size 50M;
    
    # 백엔드 프록시
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
        
        # WebSocket 지원 (필요한 경우)
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # 타임아웃 설정
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
    
    # 헬스체크
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
    
    # 정적 파일
    location /uploads {
        alias /home/ubuntu/edu-manager/uploads;
        expires 7d;
        add_header Cache-Control "public, immutable";
    }
}
EOF

# 5. Nginx 설정 적용
echo "5. Nginx 설정 적용 중..."
sudo ln -sf /etc/nginx/sites-available/edu-manager-ssl /etc/nginx/sites-enabled/edu-manager
sudo nginx -t
sudo systemctl reload nginx

# 6. 자동 갱신 설정
echo "6. 자동 갱신 설정 중..."
sudo systemctl enable certbot.timer
sudo systemctl start certbot.timer

# 7. 갱신 테스트
echo "7. 갱신 테스트 중..."
sudo certbot renew --dry-run

echo ""
echo "=== SSL 설정 완료 ==="
echo ""
echo "인증서 정보:"
sudo certbot certificates
echo ""
echo "자동 갱신 상태:"
sudo systemctl status certbot.timer
echo ""
echo "HTTPS로 접속 테스트: https://$DOMAIN/health"
