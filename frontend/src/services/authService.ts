import api from './api';
import type {
  LoginRequest,
  LoginResponse,
  ApiResponse,
} from '../types/auth.type.ts';

export const authService = {
  login: async (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    const response = await api.post<ApiResponse<LoginResponse>>(
      '/auth/login',
      data,
    );
    return response.data;
  },
  logout: async (): Promise<void> => {
    try {
      await api.post('/auth/logout');
    } finally {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
    }
  },

  getCurrentUser: async (): Promise<ApiResponse<string>> => {
    const response = await api.get<ApiResponse<string>>('/auth/me');
    return response.data;
  },
};
