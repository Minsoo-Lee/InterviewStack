import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

// TODO: 백엔드에 프로필/통계 조회 API(GET /api/users/me 등)가 아직 없어서,
// 로그인 시 받아온 사용자 정보만 보여주는 최소 버전. API가 추가되면 활동 통계 등으로 확장.
export function MyPage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login', { replace: true });
  }

  return (
    <div className="page">
      <h1>마이페이지</h1>

      {user && (
        <section className="profile-card">
          <p className="profile-card__name">{user.name}</p>
          <p className="profile-card__email">{user.email}</p>
        </section>
      )}

      <button type="button" onClick={handleLogout}>
        로그아웃
      </button>
    </div>
  );
}
