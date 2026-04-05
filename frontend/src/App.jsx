import { Navigate, NavLink, Route, Routes } from 'react-router-dom';
import { HomePage } from './pages/HomePage';
import { UsersPage } from './pages/UsersPage';
import { UserDashboardPage } from './pages/UserDashboardPage';
import { ProductsPage } from './pages/ProductsPage';
import { MealsPage } from './pages/MealsPage';
import { MealDetailsPage } from './pages/MealDetailsPage';

const navigationItems = [
  { to: '/', label: 'Главная', end: true },
  { to: '/users', label: 'Пользователи' },
  { to: '/products', label: 'Продукты' },
  { to: '/meals', label: 'Приемы пищи' },
];

function AppShell({ children }) {
  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="topbar-inner">
          <div className="brand">
            <h1 className="brand-title">Food Diary</h1>
          </div>
          <nav className="nav-list nav-list-top" aria-label="Primary">
            {navigationItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) => `nav-link nav-link-tab${isActive ? ' nav-link-active' : ''}`}
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
        </div>
      </header>
      <main className="main-content">
        <div className="page-frame">{children}</div>
      </main>
    </div>
  );
}

export default function App() {
  return (
    <AppShell>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/users" element={<UsersPage />} />
        <Route path="/users/:userId" element={<UserDashboardPage />} />
        <Route path="/products" element={<ProductsPage />} />
        <Route path="/meals" element={<MealsPage />} />
        <Route path="/meals/:mealId" element={<MealDetailsPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AppShell>
  );
}
