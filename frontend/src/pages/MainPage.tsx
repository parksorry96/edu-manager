// React와 필요한 Hook들을 가져옵니다
import React, { useEffect, useState } from 'react';
// Material-UI 컴포넌트들을 가져옵니다
import {
  Box,
  Card, // 카드 컴포넌트
  CardContent, // 카드 내용
  Typography,
  Paper,
  List, // 목록
  ListItem, // 목록 항목
  ListItemText, // 목록 텍
  Divider,
  // 구분선
} from '@mui/material';
// Grid2를 사용합니다 (MUI v5의 새로운 그리드 시스템)
import Grid2 from '@mui/material/Grid';
// Material-UI 아이콘들
import {
  School as SchoolIcon, // 학교 아이콘
  Person as PersonIcon, // 사람 아이콘
  Assignment as AssignmentIcon, // 과제 아이콘
  Event as EventIcon, // 이벤트 아이콘
} from '@mui/icons-material';
// 우리가 정의한 타입 가져오기
import type { UserInfo } from '../types/auth.type';

// 대시보드 데이터의 타입을 정의합니다
// TypeScript에서는 데이터의 형태를 미리 정의해야 합니다
interface DashboardData {
  totalClasses: number; // 전체 수업 수
  totalStudents: number; // 전체 학생 수
  pendingAssignments: number; // 미완료 과제 수
  todayClasses: number; // 오늘의 수업 수
}

// 최근 활동 데이터의 타입
interface RecentActivity {
  id: number;
  type: string; // 활동 유형
  description: string; // 활동 설명
  time: string; // 시간
}

// MainPage 컴포넌트 정의
const MainPage: React.FC = () => {
  // 상태 관리
  const [dashboardData, setDashboardData] = useState<DashboardData>({
    totalClasses: 0,
    totalStudents: 0,
    pendingAssignments: 0,
    todayClasses: 0,
  });

  const [recentActivities, setRecentActivities] = useState<RecentActivity[]>(
    [],
  );
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState<UserInfo | null>(null);

  useEffect(() => {
    // localStorage에서 사용자 정보 가져오기
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        const userData = JSON.parse(userStr) as UserInfo;
        setUser(userData);
      } catch (error) {
        console.error('사용자 정보 파싱 실패:', error);
      }
    }

    // 데이터를 가져오는 함수
    const fetchDashboardData = async () => {
      try {
        setTimeout(() => {
          setDashboardData({
            totalClasses: 12,
            totalStudents: 156,
            pendingAssignments: 8,
            todayClasses: 3,
          });

          setRecentActivities([
            {
              id: 1,
              type: '과제',
              description: '수학 과제가 제출되었습니다',
              time: '10분 전',
            },
            {
              id: 2,
              type: '수업',
              description: '영어 수업이 시작되었습니다',
              time: '30분 전',
            },
            {
              id: 3,
              type: '학생',
              description: '새로운 학생이 등록되었습니다',
              time: '1시간 전',
            },
          ]);

          setLoading(false);
        }, 1000);
      } catch (error) {
        console.error('데이터 로딩 실패:', error);
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  // 통계 카드를 만드는 함수
  const StatCard = ({
    title,
    value,
    icon,
    color,
  }: {
    title: string;
    value: number;
    icon: React.ReactNode;
    color: string;
  }) => (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Box display="flex" alignItems="center" justifyContent="space-between">
          <Box>
            <Typography color="textSecondary" gutterBottom>
              {title}
            </Typography>
            <Typography variant="h4">{value}</Typography>
          </Box>
          <Box sx={{ color: color }}>{icon}</Box>
        </Box>
      </CardContent>
    </Card>
  );

  if (loading) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        minHeight="400px"
      >
        <Typography>로딩 중...</Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ flexGrow: 1 }}>
      <Typography variant="h4" gutterBottom>
        대시보드
      </Typography>

      <Typography variant="subtitle1" color="textSecondary" gutterBottom>
        {user ? `${user.name}님, ` : ''}교육 관리 시스템에 오신 것을 환영합니다
      </Typography>

      {user && (
        <Typography variant="body2" color="primary" gutterBottom>
          역할:{' '}
          {user.role === 'ADMIN'
            ? '관리자'
            : user.role === 'TEACHER'
              ? '선생님'
              : user.role === 'STUDENT'
                ? '학생'
                : '부모님'}
        </Typography>
      )}

      {/* Grid2를 사용한 통계 카드 */}
      <Grid2 container spacing={3} sx={{ mt: 2 }}>
        <Grid2 size={{ xs: 12, sm: 6, md: 3 }}>
          <StatCard
            title="전체 수업"
            value={dashboardData.totalClasses}
            icon={<SchoolIcon sx={{ fontSize: 40 }} />}
            color="#1976d2"
          />
        </Grid2>

        <Grid2 size={{ xs: 12, sm: 6, md: 3 }}>
          <StatCard
            title="전체 학생"
            value={dashboardData.totalStudents}
            icon={<PersonIcon sx={{ fontSize: 40 }} />}
            color="#388e3c"
          />
        </Grid2>

        <Grid2 size={{ xs: 12, sm: 6, md: 3 }}>
          <StatCard
            title="미완료 과제"
            value={dashboardData.pendingAssignments}
            icon={<AssignmentIcon sx={{ fontSize: 40 }} />}
            color="#f57c00"
          />
        </Grid2>

        <Grid2 size={{ xs: 12, sm: 6, md: 3 }}>
          <StatCard
            title="오늘의 수업"
            value={dashboardData.todayClasses}
            icon={<EventIcon sx={{ fontSize: 40 }} />}
            color="#d32f2f"
          />
        </Grid2>
      </Grid2>

      <Paper sx={{ mt: 4, p: 3 }}>
        <Typography variant="h6" gutterBottom>
          최근 활동
        </Typography>

        <List>
          {recentActivities.map((activity, index) => (
            <React.Fragment key={activity.id}>
              <ListItem>
                <ListItemText
                  primary={activity.description}
                  secondary={`${activity.type} • ${activity.time}`}
                />
              </ListItem>
              {index < recentActivities.length - 1 && <Divider />}
            </React.Fragment>
          ))}
        </List>
      </Paper>
    </Box>
  );
};

export default MainPage;
