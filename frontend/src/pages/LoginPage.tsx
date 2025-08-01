// React와 필요한 Hook들을 임포트
import React, { useState } from 'react';

// React Router의 네비게이션 훅 임포트- 페이지 이동에 사용
import { useNavigate } from 'react-router-dom';

// Material-UI 컴포넌트 임포트
import {
  Container, // 레이아웃을 위한 컨테이너
  Paper, // 카드처럼 보이는 배경 컴포넌트
  TextField, // 입력 필드
  Button, // 버튼
  Typography, // 텍스트 스타일링
  Box, // 레이아웃 박스
  Alert, // 알림 메시지
  Link,
  CircularProgress,
  InputAdornment,
  IconButton,
} from '@mui/material';
import { Visibility, VisibilityOff, Email, Lock } from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';

// 직접만든 타입들 임포트
import { authService } from '../services/authService';
import type { LoginRequest } from '../types/auth.type';

// LoginPage 컴포넌트를 정의

// React.FC는 React Function Component의 약자
const LoginPage: React.FC = () => {
  // 비밀번호 표시/숨김 상태
  const [showPassword, setShowPassword] = useState(false);

  // 로딩 상태
  const [isLoading, setIsLoading] = useState(false);

  // 에러 메시지
  const [errorMessage, setErrorMessage] = useState('');

  // 라우터 네비게이션
  const navigate = useNavigate();

  // react-hook-form 설정
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginRequest>({
    defaultValues: {
      email: '',
      password: '',
    },
  });

  // 로그인 처리 함수
  const onSubmit = async (data: LoginRequest) => {
    try {
      setIsLoading(true);
      setErrorMessage('');

      // API 호출
      const response = await authService.login(data);

      // 성공 시 토큰과 사용자 정보 저장
      localStorage.setItem('accessToken', response.data.accessToken);
      localStorage.setItem('refreshToken', response.data.refreshToken);
      localStorage.setItem('user', JSON.stringify(response.data.user));

      // 메인 페이지로 이동
      navigate('/');
    } catch (error: unknown) {
      // 에러 처리
      const errorResponse = error as { response?: { status?: number } };
      if (errorResponse.response?.status === 401) {
        setErrorMessage('이메일 또는 비밀번호가 올바르지 않습니다.');
      } else if (errorResponse.response?.status === 403) {
        setErrorMessage('계정이 비활성화되었습니다. 관리자에게 문의하세요.');
      } else {
        setErrorMessage(
          '로그인 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
        );
      }
    } finally {
      setIsLoading(false);
    }
  };

  // 비밀번호 표시 토글
  const handleClickShowPassword = () => {
    setShowPassword(!showPassword);
  };

  return (
    <Container component="main" maxWidth="xs">
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Paper elevation={3} sx={{ padding: 4, width: '100%' }}>
          {/* 로고/제목 영역 */}
          <Box sx={{ textAlign: 'center', mb: 3 }}>
            <Typography
              component="h1"
              variant="h4"
              color="primary"
              fontWeight="bold"
            >
              EduManager
            </Typography>
            <Typography variant="subtitle1" color="textSecondary" mt={1}>
              학원 관리 시스템
            </Typography>
          </Box>

          {/* 에러 메시지 */}
          {errorMessage && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {errorMessage}
            </Alert>
          )}

          {/* 로그인 폼 */}
          <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
            {/* 이메일 입력 필드 */}
            <Controller
              name="email"
              control={control}
              rules={{
                required: '이메일을 입력해주세요.',
                pattern: {
                  value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                  message: '올바른 이메일 형식이 아닙니다.',
                },
              }}
              render={({ field }) => (
                <TextField
                  {...field}
                  margin="normal"
                  required
                  fullWidth
                  id="email"
                  label="이메일"
                  autoComplete="email"
                  autoFocus
                  error={!!errors.email}
                  helperText={errors.email?.message}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <Email color="action" />
                      </InputAdornment>
                    ),
                  }}
                />
              )}
            />

            {/* 비밀번호 입력 필드 */}
            <Controller
              name="password"
              control={control}
              rules={{
                required: '비밀번호를 입력해주세요.',
              }}
              render={({ field }) => (
                <TextField
                  {...field}
                  margin="normal"
                  required
                  fullWidth
                  label="비밀번호"
                  type={showPassword ? 'text' : 'password'}
                  id="password"
                  autoComplete="current-password"
                  error={!!errors.password}
                  helperText={errors.password?.message}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <Lock color="action" />
                      </InputAdornment>
                    ),
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          aria-label="toggle password visibility"
                          onClick={handleClickShowPassword}
                          edge="end"
                        >
                          {showPassword ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />
              )}
            />

            {/* 로그인 버튼 */}
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2, py: 1.5 }}
              disabled={isLoading}
            >
              {isLoading ? (
                <CircularProgress size={24} color="inherit" />
              ) : (
                '로그인'
              )}
            </Button>

            {/* 추가 링크들 */}
            <Box sx={{ mt: 2, textAlign: 'center' }}>
              <Link href="#" variant="body2" sx={{ mr: 2 }}>
                비밀번호 찾기
              </Link>
              <Link href="/signup" variant="body2">
                회원가입
              </Link>
            </Box>
          </Box>
        </Paper>

        {/* 하단 정보 */}
        <Typography
          variant="body2"
          color="textSecondary"
          align="center"
          sx={{ mt: 3 }}
        >
          © 2024 EduManager. All rights reserved.
        </Typography>
      </Box>
    </Container>
  );
};

export default LoginPage;
