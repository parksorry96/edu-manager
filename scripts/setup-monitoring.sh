# scripts/setup-monitoring.sh
#!/bin/bash
# CloudWatch 및 모니터링 설정 스크립트

set -e

echo "=== CloudWatch 모니터링 설정 ==="

# 1. CloudWatch Agent 설정 파일 생성
sudo tee /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json > /dev/null <<EOF
{
  "agent": {
    "metrics_collection_interval": 60,
    "run_as_user": "cwagent"
  },
  "logs": {
    "logs_collected": {
      "files": {
        "collect_list": [
          {
            "file_path": "/home/ubuntu/edu-manager/logs/edu-manager.log",
            "log_group_name": "/aws/ec2/edu-manager/backend",
            "log_stream_name": "{instance_id}/spring-boot",
            "timezone": "UTC"
          },
          {
            "file_path": "/var/log/nginx/edu-manager-access.log",
            "log_group_name": "/aws/ec2/edu-manager/nginx",
            "log_stream_name": "{instance_id}/access",
            "timezone": "UTC"
          },
          {
            "file_path": "/var/log/nginx/edu-manager-error.log",
            "log_group_name": "/aws/ec2/edu-manager/nginx",
            "log_stream_name": "{instance_id}/error",
            "timezone": "UTC"
          }
        ]
      }
    }
  },
  "metrics": {
    "namespace": "EduManager",
    "metrics_collected": {
      "cpu": {
        "measurement": [
          {
            "name": "cpu_usage_idle",
            "rename": "CPU_USAGE_IDLE",
            "unit": "Percent"
          },
          {
            "name": "cpu_usage_iowait",
            "rename": "CPU_USAGE_IOWAIT",
            "unit": "Percent"
          },
          "cpu_time_guest"
        ],
        "totalcpu": false,
        "metrics_collection_interval": 60
      },
      "disk": {
        "measurement": [
          {
            "name": "used_percent",
            "rename": "DISK_USED_PERCENT",
            "unit": "Percent"
          }
        ],
        "metrics_collection_interval": 60,
        "resources": [
          "*"
        ]
      },
      "mem": {
        "measurement": [
          {
            "name": "mem_used_percent",
            "rename": "MEM_USED_PERCENT",
            "unit": "Percent"
          }
        ],
        "metrics_collection_interval": 60
      },
      "netstat": {
        "measurement": [
          "tcp_established",
          "tcp_time_wait"
        ],
        "metrics_collection_interval": 60
      }
    }
  }
}
EOF

# 2. CloudWatch Agent 시작
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
    -a fetch-config \
    -m ec2 \
    -s \
    -c file:/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json

# 3. CloudWatch 대시보드 생성 스크립트
cat > /home/ubuntu/edu-manager/scripts/create-dashboard.sh << 'EOF'
#!/bin/bash
# CloudWatch 대시보드 생성

INSTANCE_ID=$(ec2-metadata --instance-id | cut -d " " -f 2)
REGION="ap-northeast-2"

aws cloudwatch put-dashboard \
    --dashboard-name EduManagerDashboard \
    --dashboard-body '{
  "widgets": [
    {
      "type": "metric",
      "properties": {
        "metrics": [
          [ "EduManager", "CPU_USAGE_IDLE", { "stat": "Average" } ],
          [ ".", "MEM_USED_PERCENT", { "stat": "Average" } ],
          [ ".", "DISK_USED_PERCENT", { "stat": "Average" } ]
        ],
        "period": 300,
        "stat": "Average",
        "region": "'$REGION'",
        "title": "System Metrics"
      }
    },
    {
      "type": "log",
      "properties": {
        "query": "SOURCE '\''/aws/ec2/edu-manager/backend'\'' | fields @timestamp, @message | filter @message like /ERROR/ | sort @timestamp desc | limit 20",
        "region": "'$REGION'",
        "title": "Recent Errors"
      }
    }
  ]
}'
EOF

chmod +x /home/ubuntu/edu-manager/scripts/create-dashboard.sh

# 4. 알람 설정
cat > /home/ubuntu/edu-manager/scripts/create-alarms.sh << 'EOF'
#!/bin/bash
# CloudWatch 알람 생성

INSTANCE_ID=$(ec2-metadata --instance-id | cut -d " " -f 2)
SNS_TOPIC_ARN="arn:aws:sns:ap-northeast-2:YOUR_ACCOUNT_ID:edu-manager-alerts"

# CPU 사용률 알람
aws cloudwatch put-metric-alarm \
    --alarm-name "edu-manager-high-cpu" \
    --alarm-description "Alert when CPU exceeds 80%" \
    --metric-name CPUUtilization \
    --namespace AWS/EC2 \
    --statistic Average \
    --period 300 \
    --threshold 80 \
    --comparison-operator GreaterThanThreshold \
    --dimensions Name=InstanceId,Value=$INSTANCE_ID \
    --evaluation-periods 2 \
    --alarm-actions $SNS_TOPIC_ARN

# 메모리 사용률 알람
aws cloudwatch put-metric-alarm \
    --alarm-name "edu-manager-high-memory" \
    --alarm-description "Alert when memory exceeds 85%" \
    --metric-name MEM_USED_PERCENT \
    --namespace EduManager \
    --statistic Average \
    --period 300 \
    --threshold 85 \
    --comparison-operator GreaterThanThreshold \
    --evaluation-periods 2 \
    --alarm-actions $SNS_TOPIC_ARN

# 디스크 사용률 알람
aws cloudwatch put-metric-alarm \
    --alarm-name "edu-manager-high-disk" \
    --alarm-description "Alert when disk usage exceeds 90%" \
    --metric-name DISK_USED_PERCENT \
    --namespace EduManager \
    --statistic Average \
    --period 300 \
    --threshold 90 \
    --comparison-operator GreaterThanThreshold \
    --evaluation-periods 1 \
    --alarm-actions $SNS_TOPIC_ARN

# API 헬스체크 알람
aws cloudwatch put-metric-alarm \
    --alarm-name "edu-manager-api-down" \
    --alarm-description "Alert when API is down" \
    --metric-name HealthCheckStatus \
    --namespace AWS/Route53 \
    --statistic Minimum \
    --period 60 \
    --threshold 1 \
    --comparison-operator LessThanThreshold \
    --evaluation-periods 2 \
    --alarm-actions $SNS_TOPIC_ARN
EOF

chmod +x /home/ubuntu/edu-manager/scripts/create-alarms.sh

echo "=== CloudWatch 설정 완료 ==="
echo ""
echo "다음 단계:"
echo "1. SNS 토픽 생성: aws sns create-topic --name edu-manager-alerts"
echo "2. 이메일 구독: aws sns subscribe --topic-arn <SNS_TOPIC_ARN> --protocol email --notification-endpoint your-email@example.com"
echo "3. 대시보드 생성: ./scripts/create-dashboard.sh"
echo "4. 알람 생성: ./scripts/create-alarms.sh (SNS ARN 수정 필요)"
