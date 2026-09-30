import { useState } from 'react';
import type { FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { apiClient, extractErrorMessage } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import type { UserSummary } from '../types';

interface AuthResponse {
  accessToken: string;
  user: UserSummary;
}

export function SignupPage() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const res = await apiClient.post<AuthResponse>('/api/auth/signup', {
        email,
        password,
        name,
      });
      login(res.data.accessToken, res.data.user);
      navigate('/', { replace: true });
    } catch (err) {
      setError(extractErrorMessage(err, '회원가입에 실패했습니다. 다시 시도해주세요.'));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="page page--narrow">
      <h1>회원가입</h1>

      <form className="auth-form" onSubmit={handleSubmit}>
        <label className="auth-form__field">
          <span>이름</span>
          <input
            type="text"
            required
            autoComplete="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </label>

        <label className="auth-form__field">
          <span>이메일</span>
          <input
            type="email"
            required
            autoComplete="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </label>

        <label className="auth-form__field">
          <span>비밀번호</span>
          <input
            type="password"
            required
            minLength={8}
            autoComplete="new-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <span className="auth-form__field-hint">최소 8자 이상</span>
        </label>

        {error && <p className="page__error">{error}</p>}

        <button type="submit" disabled={submitting}>
          {submitting ? '가입 중...' : '회원가입'}
        </button>
      </form>

      <p className="page__hint">
        이미 계정이 있으신가요? <Link to="/login">로그인</Link>
      </p>
    </div>
  );
}
