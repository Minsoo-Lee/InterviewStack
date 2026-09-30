import { createContext, useContext, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import {
  clearSession,
  getAccessToken,
  getStoredUser,
  setAccessToken,
  setStoredUser,
} from '../api/client';
import type { UserSummary } from '../types';

interface AuthContextValue {
  user: UserSummary | null;
  isAuthenticated: boolean;
  login: (accessToken: string, user: UserSummary) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  // 새로고침해도 로그인 상태가 유지되도록 localStorage에서 초기값을 읽어온다.
  // (토큰과 사용자 정보는 login()에서 항상 같이 저장하므로, 둘 중 하나라도 없으면 비로그인 상태로 취급)
  const [user, setUser] = useState<UserSummary | null>(() => {
    if (!getAccessToken()) return null;
    return getStoredUser();
  });

  function login(accessToken: string, nextUser: UserSummary) {
    setAccessToken(accessToken);
    setStoredUser(nextUser);
    setUser(nextUser);
  }

  function logout() {
    clearSession();
    setUser(null);
  }

  const value = useMemo(
    () => ({ user, isAuthenticated: user !== null, login, logout }),
    [user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth는 AuthProvider 안에서만 사용할 수 있습니다.');
  }
  return ctx;
}
