#!/bin/bash
# terraform/user_data.sh
# EC2 초기 설정 스크립트 (Terraform에서 사용)

# 로그 파일 설정
exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1

# 시스템 업데이트
apt update && apt upgrade -y

# 필요한 패키지 설치
apt install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common \
    git \
    htop \
    vim \
    ufw

# Docker 설치
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
apt update
apt install -y docker-ce docker-ce-cli containerd.io

# Docker Compose 설치
curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# ubuntu 사용자를 docker 그룹에 추가
usermod -aG docker ubuntu

# Swap 메모리 설정
fallocate -l 2G /swapfile
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo '/swapfile none swap sw 0 0' | tee -a /etc/fstab

# 애플리케이션 디렉토리 생성
mkdir -p /home/ubuntu/edu-manager/{logs,uploads,backup}
chown -R ubuntu:ubuntu /home/ubuntu/edu-manager

# CloudWatch Agent 설치 (선택사항)
wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb
dpkg -i amazon-cloudwatch-agent.deb
rm amazon-cloudwatch-agent.deb

echo "EC2 초기 설정 완료"
