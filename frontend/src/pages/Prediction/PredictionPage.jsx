import React, { useState } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import Chart from "../../components/common/Chart";
import {
  getPesoPrediccion,
  getConsumoPrediccion,
} from "../../services/prediccionService";
import "./PredictionPage.css";

export default function PredictionPage() {
  const tabs = [
    { key: "predictPesoAnimal", label: "Peso de Animal" },
    { key: "predictAlimento", label: "Consumo de Alimento" },
  ];
  const [activeTab, setActiveTab] = useState(tabs[0].key);

  const [query, setQuery] = useState("");
  const [errorQ, setErrorQ] = useState("");
  const [loading, setLoading] = useState(false);
  const [infoMsg, setInfoMsg] = useState("");

  const [pesoData, setPesoData] = useState([]);
  const [consumoData, setConsumoData] = useState([]);

  const formatMonthFromIso = (isoDate) => {
    const d = new Date(isoDate);
    if (Number.isNaN(d.getTime())) return isoDate;
    return d.toLocaleDateString("es-AR", { month: "short" });
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setErrorQ("");
    setInfoMsg("");
    setPesoData([]);
    setConsumoData([]);

    if (!query.trim()) {
      setErrorQ(
        activeTab === "predictAlimento"
          ? "Ingresa un ID de corral"
          : "Ingresa un ID de animal"
      );
      return;
    }

    try {
      setLoading(true);

      if (activeTab === "predictPesoAnimal") {
        const res = await getPesoPrediccion(query.trim());
        const data = res.data.map((p) => ({
          mes: formatMonthFromIso(p.fecha),
          peso: Number(p.pesoKg),
          predicho: p.predicho,
        }));
        setPesoData(data);
        if (data.length === 0)
          setInfoMsg("No se encontraron pesajes para este animal.");
      } else if (activeTab === "predictAlimento") {
        const res = await getConsumoPrediccion(query.trim());
        const data = res.data.map((p) => ({
          mes: formatMonthFromIso(p.fecha),
          consumo: Number(p.consumoTotalKg),
          predicho: p.predicho,
        }));
        setConsumoData(data);
        if (data.length === 0)
          setInfoMsg(
            "No se encontraron registros de comedero para este corral."
          );
      } else if (activeTab === "predictRendimiento") {
        setInfoMsg(
          "La predicción de rendimiento animal aún está en desarrollo."
        );
      }
    } catch (err) {
      console.error(err);
      if (err.response && err.response.status === 404) {
        setErrorQ("Animal no encontrado o no se encuentra ACTIVO.");
      } else {
        setErrorQ("Ocurrió un error al obtener la predicción.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="prediction-page">
      <TabBar
        tabs={tabs}
        activeKey={activeTab}
        onSelect={(key) => {
          setActiveTab(key);
          setQuery("");
          setErrorQ("");
          setInfoMsg("");
          setPesoData([]);
          setConsumoData([]);
        }}
      />

      <div className="prediction-content">
        {(activeTab === "predictPesoAnimal" ||
          activeTab === "predictAlimento") && (
          <form className="pred-form" onSubmit={handleSearch}>
            <input
              type={activeTab === "predictPesoAnimal" ? "text" : "number"}
              placeholder={
                activeTab === "predictPesoAnimal"
                  ? "ID o N° Caravana"
                  : "ID de Corral"
              }
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
            <button type="submit" disabled={loading}>
              {loading ? "Buscando…" : "🔍"}
            </button>
          </form>
        )}

        {errorQ && <p className="error">{errorQ}</p>}
        {infoMsg && <p className="info">{infoMsg}</p>}

        {activeTab === "predictPesoAnimal" && pesoData.length > 0 && (
          <div className="prediction-card">
            <h2>Predicción de Peso (kg) para animal ID: {query}</h2>
            <Chart
              tipo="line"
              data={pesoData}
              dataKey="peso"
              label="Peso (kg)"
            />
          </div>
        )}

        {activeTab === "predictRendimiento" && (
          <div className="prediction-card">
            <h2>Predicción de Rendimiento Animal</h2>
            <p>Esta funcionalidad se añadirá más adelante.</p>
          </div>
        )}

        {activeTab === "predictAlimento" && consumoData.length > 0 && (
          <div className="prediction-card">
            <h2>
              Predicción de Consumo de Alimento (kg) para corral ID: {query}
            </h2>
            <Chart
              tipo="line"
              data={consumoData}
              dataKey="consumo"
              label="Consumo total (kg)"
            />
          </div>
        )}
      </div>
    </div>
  );
}
