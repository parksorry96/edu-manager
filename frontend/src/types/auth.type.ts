export type UserRole = "ADMIN" | "TEACHER" | "STUDENT" | "PARENT";

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserInfo;
  message: string;
}

export interface UserInfo {
  id: number;
  email: string;
  name: string;
  role: UserRole;
}

export interface ApiResponse<T> {
  status: string;
  data: T;
  message: string;
  timestamp: string;
}
