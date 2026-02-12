import React, { useState, useEffect } from "react";
import StatsCard from "../../components/common/StatsCard";
import Chart from "../../components/common/Chart";
import { FiUsers, FiBox } from "react-icons/fi";
import { GiCow } from "react-icons/gi";

import { getAllAnimals } from "../../services/animalService";
import { getAllCorral } from "../../services/corralService";
import { getAllInsumos } from "../../services/insumoService";
import { getConsumoMensualFeedlot } from "../../services/prediccionService";

import "./DashboardPage.css";

export default function DashboardPage() {

  const [animalCount, setAnimalCount] = useState(0);
  const [corralCount, setCorralCount] = useState(0);
  const [insumoCount, setInsumoCount] = useState(0);

  const [animals, setAnimals] = useState([]);
  const [upcomingTreatments, setUpcomingTreatments] = useState([]);

  const [consumoMensual, setConsumoMensual] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  
  const fetchAnimals = async () => {
    const { data } = await getAllAnimals();
    const list = Array.isArray(data)
      ? data
      : Array.isArray(data?.content)
      ? data.content
      : [];

    setAnimals(list);
    setAnimalCount(list.length);
  };

  const fetchCorrals = async () => {
    const { data } = await getAllCorral();
    const total = Array.isArray(data)
      ? data.length
      : data?.totalElements ?? data?.content?.length ?? 0;

    setCorralCount(total);
  };

  const fetchInsumos = async () => {
    const { data } = await getAllInsumos();
    const total = Array.isArray(data)
      ? data.length
      : data?.totalElements ?? data?.content?.length ?? 0;

    setInsumoCount(total);
  };

  const fetchConsumoMensual = async () => {
    const res = await getConsumoMensualFeedlot(6);
  

    const adaptado = res.data.map((d) => ({
      mes: new Date(d.mes).toLocaleString("es-AR", {
        month: "short",
        year: "numeric",
      }),
      consumo: Number(d.consumoKg),
    }));

    setConsumoMensual(adaptado);
  };


  useEffect(() => {
    (async () => {
      try {
        await Promise.all([
          fetchAnimals(),
          fetchCorrals(),
          fetchInsumos(),
          fetchConsumoMensual(),
        ]);
        setError(null);
      } catch (e) {
        setError(e.message || "Error cargando dashboard");
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  
  useEffect(() => {
    const today = new Date();

    const upcomings = animals
      .filter((a) => a.proxTratamiento)
      .map((a) => ({ ...a, fecha: new Date(a.proxTratamiento) }))
      .filter((a) => a.fecha > today)
      .sort((a, b) => a.fecha - b.fecha)
      .slice(0, 15);

    setUpcomingTreatments(upcomings);
  }, [animals]);

  
  const consumoUltimoMes =
    consumoMensual.length > 0
      ? consumoMensual[consumoMensual.length - 1].consumo
      : 0;

  const fmt = (d) =>
    new Date(d).toLocaleDateString("es-AR", { timeZone: "UTC" });

  const estadisticas = [
    {
      titulo: "Animales en el Feedlot",
      valor: loading ? "—" : animalCount.toLocaleString("es-AR"),
      icono: <GiCow />,
    },
    {
      titulo: "Corrales Activos",
      valor: loading ? "—" : corralCount.toLocaleString("es-AR"),
      icono: <FiUsers />,
    },
    {
      titulo: "Cantidad de Insumos",
      valor: loading ? "—" : insumoCount.toLocaleString("es-AR"),
      icono: <FiBox />,
    },
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

          <div className="dashboard-grid">

          
            <div className="chart-card">
              <h2>Consumo de alimento mensual (kg)</h2>

              <div className="card-scroll chart-scroll">
                {consumoMensual.length > 0 ? (
                  <>
                  
                    <Chart
                      tipo="line"
                      data={consumoMensual}
                      dataKey="consumo"
                      label="Consumo mensual de alimento (kg)"
                    />

                    <p className="chart-summary">
                      Consumo del último mes:{" "}
                      {consumoUltimoMes.toLocaleString("es-AR")} kg
                    </p>
                  </>
                ) : (
                  <p className="chart-summary chart-summary-empty">
                    No hay registros de consumo de alimento.
                  </p>
                )}
              </div>
            </div>

          
            <div className="treatments-card">
              <h2>Próximos Tratamientos</h2>

              <div className="card-scroll">
                {upcomingTreatments.length === 0 ? (
                  <p className="chart-summary chart-summary-empty">
                    No hay tratamientos programados.
                  </p>
                ) : (
                  <ul>
                    {upcomingTreatments.map((a) => (
                      <li key={a.animalId}>
                        {a.caravana
                          ? `Caravana ${a.caravana}`
                          : `Animal ${a.animalId}`}{" "}
                        – {fmt(a.proxTratamiento)}
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </div>

          </div>

          {error && (
            <div className="chart-summary chart-summary-empty">{error}</div>
          )}
        </div>
      </div>
    </div>
  );
}
