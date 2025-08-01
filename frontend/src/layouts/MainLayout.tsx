// React와 필요한 Hook들을 가져옵니다
import React, { useState, useEffect } from 'react';
// React Router 컴포넌트들
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
// Material-UI 컴포넌트들
import {
  Box,
  Drawer, // 사이드바
  AppBar, // 상단 바
  Toolbar, // 툴바
  Typography,
  IconButton, // 아이콘 버튼
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Divider,
  Menu, // 드롭다운 메뉴
  MenuItem, // 메뉴 항목
} from '@mui/material';
// Material-UI 아이콘들
import {
  Menu as MenuIcon,
  Dashboard as DashboardIcon,
  School as SchoolIcon,
  Person as PersonIcon,
  Assignment as AssignmentIcon,
  Settings as SettingsIcon,
  Logout as LogoutIcon,
  AccountCircle as AccountCircleIcon,
} from '@mui/icons-material';
// 우리가 만든 서비스와 타입들
import { authService } from '../services/authService';
import type { UserInfo } from '../types/auth.type';

// 사이드바 너비 상수 정의
const drawerWidth = 240;

// 메뉴 아이템 타입 정의
interface MenuItem {
  text: string; // 메뉴 이름
  icon: React.ReactNode; // 아이콘 컴포넌트
  path: string; // 이동할 경로
}

const MainLayout: React.FC = () => {
  // 상태 관리
  const [mobileOpen, setMobileOpen] = useState(false); // 모바일 메뉴 열림 상태
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null); // 프로필 메뉴 앵커
  const [user, setUser] = useState<UserInfo | null>(null); // 사용자 정보

  // React Router hooks
  const navigate = useNavigate();
  const location = useLocation(); // 현재 경로 정보

  // 컴포넌트가 마운트될 때 사용자 정보 가져오기
  useEffect(() => {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        const userData = JSON.parse(userStr) as UserInfo;
        setUser(userData);
      } catch (error) {
        console.error('사용자 정보 파싱 실패:', error);
      }
    }
  }, []);

  // 메뉴 항목 배열
  const menuItems: MenuItem[] = [
    { text: '대시보드', icon: <DashboardIcon />, path: '/' },
    { text: '수업 관리', icon: <SchoolIcon />, path: '/classes' },
    { text: '학생 관리', icon: <PersonIcon />, path: '/students' },
    { text: '과제 관리', icon: <AssignmentIcon />, path: '/assignments' },
  ];

  // 모바일 메뉴 토글 함수
  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  // 프로필 메뉴 열기
  const handleProfileMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  // 프로필 메뉴 닫기
  const handleProfileMenuClose = () => {
    setAnchorEl(null);
  };

  // 로그아웃 처리
  const handleLogout = async () => {
    try {
      // authService의 logout 메서드 사용
      await authService.logout();
      // 로그인 페이지로 이동 (authService가 이미 처리하지만 명시적으로)
      navigate('/login');
    } catch (error) {
      console.error('로그아웃 실패:', error);
      // 에러가 발생해도 로그인 페이지로 이동
      navigate('/login');
    }
  };

  // 사이드바 내용
  const drawer = (
    <Box>
      {/* 로고 영역 */}
      <Toolbar>
        <Typography variant="h6" noWrap component="div">
          교육 관리 시스템
        </Typography>
      </Toolbar>

      <Divider />

      {/* 메뉴 목록 */}
      <List>
        {menuItems.map((item) => (
          <ListItem key={item.text} disablePadding>
            <ListItemButton
              selected={location.pathname === item.path} // 현재 경로와 일치하면 선택됨
              onClick={() => {
                navigate(item.path);
                setMobileOpen(false); // 모바일에서는 메뉴 닫기
              }}
            >
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>

      <Divider />

      {/* 설정 메뉴 */}
      <List>
        <ListItem disablePadding>
          <ListItemButton onClick={() => navigate('/settings')}>
            <ListItemIcon>
              <SettingsIcon />
            </ListItemIcon>
            <ListItemText primary="설정" />
          </ListItemButton>
        </ListItem>
      </List>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      {/* 상단 바 */}
      <AppBar
        position="fixed"
        sx={{
          width: { sm: `calc(100% - ${drawerWidth}px)` }, // 데스크탑에서는 사이드바 너비만큼 빼기
          ml: { sm: `${drawerWidth}px` }, // 왼쪽 마진
        }}
      >
        <Toolbar>
          {/* 모바일 메뉴 버튼 */}
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { sm: 'none' } }} // 데스크탑에서는 숨김
          >
            <MenuIcon />
          </IconButton>

          {/* 페이지 제목 (자동으로 늘어남) */}
          <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
            {/* 현재 경로에 따라 제목 표시 */}
            {menuItems.find((item) => item.path === location.pathname)?.text ||
              '교육 관리 시스템'}
          </Typography>

          {/* 사용자 이름 표시 */}
          {user && (
            <Typography variant="body2" sx={{ mr: 2 }}>
              {user.name}님
            </Typography>
          )}

          {/* 프로필 버튼 */}
          <IconButton
            size="large"
            aria-label="account of current user"
            aria-controls="menu-appbar"
            aria-haspopup="true"
            onClick={handleProfileMenuOpen}
            color="inherit"
          >
            <AccountCircleIcon />
          </IconButton>

          {/* 프로필 메뉴 */}
          <Menu
            id="menu-appbar"
            anchorEl={anchorEl}
            anchorOrigin={{
              vertical: 'bottom',
              horizontal: 'right',
            }}
            keepMounted
            transformOrigin={{
              vertical: 'top',
              horizontal: 'right',
            }}
            open={Boolean(anchorEl)}
            onClose={handleProfileMenuClose}
          >
            <MenuItem
              onClick={() => {
                handleProfileMenuClose();
                navigate('/profile');
              }}
            >
              <ListItemIcon>
                <PersonIcon fontSize="small" />
              </ListItemIcon>
              프로필
            </MenuItem>
            <MenuItem onClick={handleLogout}>
              <ListItemIcon>
                <LogoutIcon fontSize="small" />
              </ListItemIcon>
              로그아웃
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>

      {/* 사이드바 */}
      <Box
        component="nav"
        sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
      >
        {/* 모바일용 사이드바 (임시) */}
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{
            keepMounted: true, // 모바일 성능 향상
          }}
          sx={{
            display: { xs: 'block', sm: 'none' },
            '& .MuiDrawer-paper': {
              boxSizing: 'border-box',
              width: drawerWidth,
            },
          }}
        >
          {drawer}
        </Drawer>

        {/* 데스크탑용 사이드바 (고정) */}
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', sm: 'block' },
            '& .MuiDrawer-paper': {
              boxSizing: 'border-box',
              width: drawerWidth,
            },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>

      {/* 메인 컨텐츠 영역 */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3, // padding: 3
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          mt: 8, // margin-top: 8 (Toolbar 높이만큼)
        }}
      >
        {/* Outlet: React Router에서 자식 라우트가 렌더링되는 곳 */}
        <Outlet />
      </Box>
    </Box>
  );
};

export default MainLayout;
