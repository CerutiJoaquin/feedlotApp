import React, { useState } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import PredictionChart from "../../components/common/PredictionChart";
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
  const [dias, setDias] = useState(7);
  const [meses, setMeses] = useState(6);

  const [loading, setLoading] = useState(false);
  const [errorQ, setErrorQ] = useState("");
  const [infoMsg, setInfoMsg] = useState("");

  const [pesoData, setPesoData] = useState([]);
  const [consumoData, setConsumoData] = useState([]);

  const handleSearch = async (e) => {
    e.preventDefault();
    setErrorQ("");
    setInfoMsg("");
    setPesoData([]);
    setConsumoData([]);

    if (!query.trim()) {
      setErrorQ("Debes ingresar un ID válido");
      return;
    }

    try {
      setLoading(true);

      if (activeTab === "predictPesoAnimal") {
        const res = await getPesoPrediccion(Number(query), meses);

        setPesoData(
          res.data.map((p) => ({
            fecha: p.fecha,
            valor: Number(p.pesoKg),
            predicho: p.predicho,
          }))
        );
      }

      if (activeTab === "predictAlimento") {
        const res = await getConsumoPrediccion({
          corral_id: Number(query),
          dias: dias,
        });

        setConsumoData(
          res.data.map((p) => ({
            fecha: p.fecha,
            valor: Number(p.consumoKg),
            predicho: p.predicho,
          }))
        );
      }
    } catch (err) {
      setErrorQ("Error obteniendo la predicción");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="prediction-page">
      <TabBar tabs={tabs} activeKey={activeTab} onSelect={setActiveTab} />

      {activeTab === "predictPesoAnimal" && (
        <form className="predict-form" onSubmit={handleSearch}>
          <div className="predict-box">
            <div className="predict-title">Predicción de peso del animal</div>

            <div className="predict-subtitle">
              Ingresá el ID del animal y los meses a predecir
            </div>

            <div className="input-row">
              <div className="input-field">
                <label>ID del animal</label>
                <input
                  type="number"
                  placeholder="Ej: 12"
                  value={query}
                  onChange={(e) => setQuery(e.target.value)}
                />
              </div>

              <div className="input-field">
                <label>Meses futuros</label>
                <input
                  type="number"
                  min="1"
                  max="24"
                  value={meses}
                  onChange={(e) => setMeses(Number(e.target.value))}
                />
              </div>

              <button type="submit" disabled={loading}>
                Predecir
              </button>
            </div>
          </div>
        </form>
      )}

      {activeTab === "predictAlimento" && (
        <form className="predict-form" onSubmit={handleSearch}>
          <div className="predict-box">
            <div className="predict-title">
              Predicción de consumo de alimento
            </div>

            <div className="predict-subtitle">
              Ingresá el ID del corral y la cantidad de días a predecir
            </div>

            <div className="input-row">
              <div className="input-field">
                <label>ID de corral</label>
                <input
                  type="number"
                  placeholder="Ej: 3"
                  value={query}
                  onChange={(e) => setQuery(e.target.value)}
                />
              </div>

              <div className="input-field">
                <label>Días futuros</label>
                <input
                  type="number"
                  min="1"
                  value={dias}
                  onChange={(e) => setDias(Number(e.target.value))}
                />
              </div>

              <button type="submit" disabled={loading}>
                Predecir
              </button>
            </div>
          </div>
        </form>
      )}

      {pesoData.length > 0 && activeTab === "predictPesoAnimal" && (
        <PredictionChart data={pesoData} yLabel="Kg" />
      )}

      {consumoData.length > 0 && activeTab === "predictAlimento" && (
        <PredictionChart data={consumoData} yLabel="Kg / día" />
      )}

      {errorQ && <div className="error-msg">{errorQ}</div>}
      {infoMsg && <div className="info-msg">{infoMsg}</div>}
    </div>
  );
}
