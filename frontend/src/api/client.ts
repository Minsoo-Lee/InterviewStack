import axios from 'axios';
import type { UserSummary } from '../types';

// .env(.local)에서 VITE_API_BASE_URL을 오버라이드할 수 있음 (.env.example 참고)
const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

const TOKEN_STORAGE_KEY = 'interviewstack.accessToken';
const USER_STORAGE_KEY = 'interviewstack.user';

export function getAccessToken(): string | null {
  return localStorage.getItem(TOKEN_STORAGE_KEY);
}

export function setAccessToken(token: string): void {
  localStorage.setItem(TOKEN_STORAGE_KEY, token);
}

export function getStoredUser(): UserSummary | null {
  const raw = localStorage.getItem(USER_STORAGE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as UserSummary;
  } catch {
    return null;
  }
}

export function setStoredUser(user: UserSummary): void {
  localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user));
}

export function clearSession(): void {
  localStorage.removeItem(TOKEN_STORAGE_KEY);
  localStorage.removeItem(USER_STORAGE_KEY);
}

// 백엔드는 에러를 { message, code } 형태(ErrorResponse)로 내려준다. 없으면 기본 메시지로 대체.
export function extractErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    const message = error.response?.data?.message;
    if (typeof message === 'string' && message.trim()) {
      return message;
    }
  }
  return fallback;
}

apiClient.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 토큰이 없거나 만료돼서 401이 오면 세션을 지우고 로그인 화면으로 보낸다.
// (로그인/회원가입 자체 요청은 원래 401이 뜰 일이 없으므로 그대로 흘려보냄)
apiClient.interceptors.response.use(
  (res) => res,
  (error) => {
    const status = error?.response?.status;
    const url = error?.config?.url ?? '';
    const isAuthEndpoint = url.includes('/api/auth/');
    if (status === 401 && !isAuthEndpoint) {
      clearSession();
      if (window.location.pathname !== '/login') {
        window.location.assign('/login');
      }
    }
    return Promise.reject(error);
  },
);
