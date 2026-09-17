import { NavLink } from 'react-router-dom';
import './Header.css';

const navItems = [
  { to: '/', label: '대시보드', end: true },
  { to: '/questions', label: '질문은행' },
  { to: '/interviews', label: 'AI 모의면접' },
  { to: '/me', label: '마이페이지' },
];

export function Header() {
  return (
    <header className="app-header">
      <div className="app-header__brand">
        <span className="app-header__logo-mark" />
        <span className="app-header__logo-text">InterviewStack</span>
      </div>
      <nav className="app-header__nav">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) =>
              isActive ? 'app-header__nav-link app-header__nav-link--active' : 'app-header__nav-link'
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </header>
  );
}
