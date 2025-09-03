import React from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import Sidebar from '../../components/layout/Sidebar/Sidebar';
import Topbar  from '../../components/layout/Topbar/Topbar';
import './MainMenuPage.css';

export default function MainMenuPage({ usuario, onLogout }) {
  const navigate = useNavigate();
  const { pathname } = useLocation();

  let activo = pathname.split('/')[1] || 'inicio';
  if (activo === 'dashboard') activo = 'inicio';

  return (
    <div className="app-layout">
      <Topbar usuario={usuario} onLogout={onLogout} />

      <div className="dashboard-body-container">
        <Sidebar activo={activo} onSelect={(key) => navigate(`/${key}`)} />
        <main className="dashboard-main">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

