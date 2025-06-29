import React, { useState } from "react";
import StatsCard   from "../../components/common/StatsCard";
import Chart       from "../../components/common/Chart";
import { FiUsers, FiBox }     from "react-icons/fi";
import { GiCow, GiHealthNormal } from "react-icons/gi";
import "./DashboardPage.css";

export default function DashboardPage() {
  const estadisticas = [
    { titulo: "Cantidad de Animales", valor: 50,    icono: <GiCow /> },
    { titulo: "Corrales Activos",     valor: 3,      icono: <FiUsers /> },
    { titulo: "Stock de Alimento",    valor: "150 bolsas", icono: <FiBox /> },
  ];

  const datosConsumo = [
    { mes: "Ene", consumo: 4500 },
    { mes: "Feb", consumo: 4800 },
    { mes: "Mar", consumo: 5000 },
    { mes: "Abr", consumo: 5300 },
    { mes: "May", consumo: 5200 },
    { mes: "Jun", consumo: 5600 },
  ];

  return (
    <div className="dashboard-page">
      <div className="dashboard-body-container">
        <div className="dashboard-main">
          <div className="stats-grid">
            {estadisticas.map((s) => (
              <StatsCard
                key={s.titulo}
                titulo={s.titulo}
                valor={s.valor}
                icono={s.icono}
              />
            ))}
          </div>

          <div className="dashboard-body">
            <div className="chart-card">
              <h2>Consumo de alimento mensual (kg)</h2>
              <Chart
                tipo="line"
                data={datosConsumo}
                dataKey="consumo"
                label="Consumo (kg)"
              />
            </div>

            <div className="treatments-card">
              <h2>Próximos Tratamientos</h2>
              <ul>
                <li>Vaca 101 – 15/07/2025</li>
                <li>Vaca 57 – 18/07/2025</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
