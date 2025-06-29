import React, { useState } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import Chart from "../../components/common/Chart";
import "./PredictionPage.css";

export default function PredictionPage() {
  const tabs = [
    { key: "predictPesoAnimal", label: "Peso de Animal" },
    { key: "predictRendimiento", label: "Rendimiento Animal" },
    { key: "predictAlimento", label: "Consumo de Alimento" },
  ];
  const [activeTab, setActiveTab] = useState(tabs[0].key);

  // Estado común de búsqueda
  const [query, setQuery] = useState("");
  const [errorQ, setErrorQ] = useState("");
  const [loading, setLoading] = useState(false);

  // Mock de resultados
  const mockPeso = [
    { mes: "Jul", peso: 330 },
    { mes: "Ago", peso: 345 },
    { mes: "Sep", peso: 360 },
    { mes: "Oct", peso: 376 },
    { mes: "Nov", peso: 390 },
    { mes: "Dic", peso: 410 },
  ];
  const mockRend = [
    { mes: "Jul", valor: 1200 },
    { mes: "Ago", valor: 1300 },
    { mes: "Sep", valor: 1400 },
    { mes: "Oct", valor: 1500 },
    { mes: "Nov", valor: 1600 },
    { mes: "Dic", valor: 1700 },
  ];
  const consumoData = [
    { mes: "Jul", consumo: 3700 },
    { mes: "Ago", consumo: 3780 },
    { mes: "Sep", consumo: 3900 },
    { mes: "Oct", consumo: 3850 },
    { mes: "Nov", consumo: 3820 },
    { mes: "Dic", consumo: 3950 },
  ];

  // Estados de datos tras buscar
  const [pesoData, setPesoData] = useState([]);
  const [rendData, setRendData] = useState([]);

  const handleSearch = (e) => {
    e.preventDefault();
    setErrorQ("");
    if (!query.trim()) {
      setErrorQ("Ingresa un ID o N° Caravana");
      return;
    }
    setLoading(true);
    // Simular fetch
    setTimeout(() => {
      if (activeTab === "predictPesoAnimal") {
        setPesoData(mockPeso);
      } else if (activeTab === "predictRendimiento") {
        setRendData(mockRend);
      }
      setLoading(false);
    }, 500);
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
          setPesoData([]);
          setRendData([]);
        }}
      />

      <div className="prediction-content">
        {(activeTab === "predictPesoAnimal" ||
          activeTab === "predictRendimiento") && (
          <form className="pred-form" onSubmit={handleSearch}>
            <input
              type="text"
              placeholder="ID o N° Caravana"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
            <button type="submit" disabled={loading}>
              {loading ? "Buscando…" : "🔍"}
            </button>
          </form>
        )}

        {errorQ && <p className="error">{errorQ}</p>}

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

        {activeTab === "predictRendimiento" && rendData.length > 0 && (
          <div className="prediction-card">
            <h2>Predicción de Rendimiento ($) para animal {query}</h2>
            <Chart
              tipo="bar"
              data={rendData}
              dataKey="valor"
              label="Valor ($)"
            />
          </div>
        )}

        {activeTab === "predictAlimento" && (
          <div className="prediction-card">
            <h2>Predicción de Consumo de Alimento (kg)</h2>
            <Chart
              tipo="line"
              data={consumoData}
              dataKey="consumo"
              label="Consumo (kg)"
            />
          </div>
        )}
      </div>
    </div>
  );
}
