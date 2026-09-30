import type { ReactNode } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './AuthContext';

// 로그인 안 된 상태로 보호된 페이지에 들어오면 /login으로 보내고,
// 로그인 후 원래 가려던 페이지로 돌아갈 수 있게 원래 경로를 state로 함께 넘긴다.
export function RequireAuth({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  return <>{children}</>;
}
