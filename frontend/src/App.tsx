import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';

// 페이지 컴포넌트들
import LoginPage from './pages/LoginPage';
import MainPage from './pages/MainPage';
import MainLayout from './layouts/MainLayout';

// MUI 테마 설정
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2', // 파란색
    },
    secondary: {
      main: '#dc004e', // 빨간색
    },
  },
  typography: {
    fontFamily: [
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
      '"Apple Color Emoji"',
      '"Segoe UI Emoji"',
      '"Segoe UI Symbol"',
    ].join(','),
  },
});

// 인증 확인 컴포넌트
const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const token = localStorage.getItem('accessToken');
  return token ? <>{children}</> : <Navigate to="/login" />;
};

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <Routes>
          {/* 로그인 페이지 */}
          <Route path="/login" element={<LoginPage />} />

          {/* 인증이 필요한 페이지들 */}
          <Route
            path="/"
            element={
              <PrivateRoute>
                <MainLayout />
              </PrivateRoute>
            }
          >
            {/* 메인 대시보드 */}
            <Route index element={<MainPage />} />

            {/* 추후 추가할 페이지들 */}
            <Route path="classes" element={<div>수업 관리 페이지</div>} />
            <Route path="students" element={<div>학생 관리 페이지</div>} />
            <Route path="assignments" element={<div>과제 관리 페이지</div>} />
            <Route path="settings" element={<div>설정 페이지</div>} />
            <Route path="profile" element={<div>프로필 페이지</div>} />
          </Route>

          {/* 404 페이지 */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
