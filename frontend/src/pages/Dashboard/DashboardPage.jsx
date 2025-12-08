import React, { useState, useEffect } from "react";
import StatsCard from "../../components/common/StatsCard";
import Chart from "../../components/common/Chart";
import { FiUsers, FiBox } from "react-icons/fi";
import { GiCow, GiHealthNormal } from "react-icons/gi";
import { getAllAnimals } from "../../services/animalService";
import { getAllCorral } from "../../services/corralService";
import { getAllInsumos } from "../../services/insumoService";
import { getConsumoMensualDashboard } from "../../services/prediccionService";
import "./DashboardPage.css";

export default function DashboardPage() {
  const [animalCount, setAnimalCount] = useState(0);
  const [corralCount, setCorralCount] = useState(0);
  const [insumoCount, setInsumoCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);


  const [consumoMensualData, setConsumoMensualData] = useState([]);
  const [consumoUltimoMes, setConsumoUltimoMes] = useState(0);

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

  const fetchCorrals = async () => {
    try {
      const { data } = await getAllCorral();
      if (Array.isArray(data)) {
        setCorralCount(data.length);
      } else {
        const total =
          typeof data?.totalElements === "number"
            ? data.totalElements
            : Array.isArray(data?.content)
            ? data.content.length
            : 0;
        setCorralCount(total);
      }
    } catch (e) {
      throw new Error(e?.message || "Error al cargar corrales");
    }
  };

  const fetchInsumos = async () => {
    try {
      const { data } = await getAllInsumos();
      if (Array.isArray(data)) {
        setInsumoCount(data.length);
      } else {
        const total =
          typeof data?.totalElements === "number"
            ? data.totalElements
            : Array.isArray(data?.content)
            ? data.content.length
            : 0;
        setInsumoCount(total);
      }
    } catch (e) {
      throw new Error(e?.message || "Error al cargar insumos");
    }
  };


  const [animals, setAnimals] = useState([]);
  const [upcomingTreatments, setUpcomingTreatments] = useState([]);

  const fmt = (d) =>
    new Date(d).toLocaleDateString("es-AR", { timeZone: "UTC" });

  const fetchAnimals = async () => {
    try {
      const { data } = await getAllAnimals();

      const list = Array.isArray(data)
        ? data
        : Array.isArray(data?.content)
        ? data.content
        : [];

      setAnimals(list);
      setAnimalCount(list.length);
    } catch (e) {
      throw new Error(e?.message || "Error al cargar animales");
    }
  };


  const fetchConsumoMensual = async () => {
    try {
      const { data } = await getConsumoMensualDashboard(6);
      const list = Array.isArray(data) ? data : [];

      const formatted = list.map((item) => {
        const d = new Date(item.mes);
        const mesLabel = d.toLocaleDateString("es-AR", { month: "short" });
        return {
          mes: mesLabel,
          consumo: Number(item.consumoKg),
        };
      });

      setConsumoMensualData(formatted);

      if (formatted.length > 0) {
        setConsumoUltimoMes(formatted[formatted.length - 1].consumo || 0);
      } else {
        setConsumoUltimoMes(0);
      }
    } catch (e) {
      throw new Error(e?.message || "Error al cargar consumo de alimento");
    }
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
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    })();
  }, []);


  useEffect(() => {
    const today = new Date();
    const upcomings = animals
      .filter((a) => a.proxTratamiento)
      .map((a) => ({ ...a, _proxDate: new Date(a.proxTratamiento) }))
      .filter((a) => a._proxDate > today)
      .sort((a, b) => a._proxDate - b._proxDate)
      .slice(0, 15);

    setUpcomingTreatments(upcomings);
  }, [animals]);

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
                {consumoMensualData.length > 0 ? (
                  <>
                    <Chart
                      tipo="line"
                      data={consumoMensualData}
                      dataKey="consumo"
                      label="Consumo (kg)"
                    />
                    <p className="chart-summary">
                      Consumo del último mes:{" "}
                      {consumoUltimoMes.toLocaleString("es-AR")} kg
                    </p>
                  </>
                ) : (
                  <p className="chart-summary chart-summary-empty">
                    No hay registros de consumo de alimento en los últimos
                    meses.
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
            <div className="chart-summary chart-summary-empty">
              {error}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
