import React, { useState } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import LoginPage from "./pages/Login/LoginPage";
import MainMenuPage from "./pages/MainMenu/MainMenuPage";
import DashboardPage from "./pages/Dashboard/DashboardPage";
import AnimalPage from "./pages/Animal/AnimalPage";
import CorralPage from "./pages/Corral/CorralPage";
import InsumoPage from "./pages/Insumo/InsumoPage";
import VentaPage from "./pages/Venta/VentaPage";
import PredictionPage from "./pages/Prediction/PredictionPage";
import ReportPage from "./pages/Report/ReportPage";

export default function App() {
  const [usuario, setUsuario] = useState(() => {
    try { return JSON.parse(localStorage.getItem("user")) || null; } catch { return null; }
  });

  const handleLogin = (user) => {
    localStorage.setItem("user", JSON.stringify(user));
    setUsuario(user);
  };

  const handleLogout = () => {
  localStorage.removeItem('user');
  setUsuario(null);
};

  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/login"
          element={usuario ? <Navigate to="/inicio" replace /> : <LoginPage onLogin={handleLogin} />}
        />

        <Route
          path="/*"
          element={usuario ? <MainMenuPage usuario={usuario} onLogout={handleLogout} /> : <Navigate to="/login" replace />}
        >
          <Route index element={<Navigate to="inicio" replace />} />
          <Route path="inicio"     element={<DashboardPage />} />
          <Route path="dashboard"  element={<DashboardPage />} />
          <Route path="ganado"     element={<AnimalPage />} />
          <Route path="corral"     element={<CorralPage />} />
          <Route path="insumo"     element={<InsumoPage />} />
          <Route path="ventas"     element={<VentaPage />} />
          <Route path="prediction" element={<PredictionPage />} />
          <Route path="reportes"   element={<ReportPage />} />
          <Route path="*"          element={<Navigate to="inicio" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
