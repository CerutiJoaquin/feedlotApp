// src/pages/Corral/CorralPage.js
import React, { useState } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import "./CorralPage.css";

export default function CorralPage() {
  const tabs = [
    { key: "crear", label: "Crear" },
    { key: "listar", label: "Listar" },
    { key: "detalle", label: "Detalle" },
  ];
  const [activeTab, setActiveTab] = useState("listar");

  // Datos de ejemplo
  const initialCorrals = [
    {
      id: 1,
      cabezas: 2,
      capacidadMin: 5,
      capacidadMax: 20,
      tipoSuperficie: "Tierra",
      estado: "Activo",
      assignedAnimals: [321, 322],
    },
    {
      id: 2,
      cabezas: 0,
      capacidadMin: 0,
      capacidadMax: 10,
      tipoSuperficie: "Concreto",
      estado: "Inactivo",
      assignedAnimals: [],
    },
    {
      id: 3,
      cabezas: 1,
      capacidadMin: 2,
      capacidadMax: 15,
      tipoSuperficie: "Grava",
      estado: "Activo",
      assignedAnimals: [323],
    },
  ];
  const [corrals, setCorrals] = useState(initialCorrals);

  // — Crear corral —
  const [newCorral, setNewCorral] = useState({
    capacidadMin: "",
    capacidadMax: "",
    tipoSuperficie: "",
    estado: "Activo",
  });
  const [msgCreate, setMsgCreate] = useState("");
  const [errorCreate, setErrorCreate] = useState("");

  const handleCreate = (e) => {
    e.preventDefault();
    setMsgCreate("");
    setErrorCreate("");
    const { capacidadMin, capacidadMax, tipoSuperficie, estado } = newCorral;
    if (!capacidadMin || !capacidadMax || !tipoSuperficie) {
      setErrorCreate("Completa todos los campos");
      return;
    }
    const id = corrals.length ? corrals[corrals.length - 1].id + 1 : 1;
    const corral = {
      id,
      cabezas: 0,
      capacidadMax: parseInt(capacidadMax, 10),
      tipoSuperficie,
      estado,
      assignedAnimals: [],
    };
    setCorrals([...corrals, corral]);
    setMsgCreate("Corral creado con éxito");
    setNewCorral({ capacidadMax: "", tipoSuperficie: "", estado: "Activo" });
  };

  // — Detalle de corral —
  const [searchId, setSearchId] = useState("");
  const [selectedCorral, setSelectedCorral] = useState(null);
  const [errorSearch, setErrorSearch] = useState("");

  const handleSearch = (e) => {
    e.preventDefault();
    setErrorSearch("");
    const id = parseInt(searchId, 10);
    if (!id) {
      setErrorSearch("Ingresa un ID válido");
      return;
    }
    const found = corrals.find((c) => c.id === id);
    if (!found) {
      setErrorSearch("Corral no encontrado");
      setSelectedCorral(null);
    } else {
      setSelectedCorral(found);
    }
  };

  return (
    <div className="corral-page">
      <TabBar tabs={tabs} activeKey={activeTab} onSelect={setActiveTab} />

      <div className="corral-content">
        {/* === CREAR === */}
        {activeTab === "crear" && (
          <form className="form-crear" onSubmit={handleCreate}>
            <h2>Crear Nuevo Corral</h2>
            <input
              type="number"
              placeholder="Capacidad Máxima"
              value={newCorral.capacidadMax}
              onChange={(e) =>
                setNewCorral({ ...newCorral, capacidadMax: e.target.value })
              }
            />
            <input
              placeholder="Tipo de Superficie"
              value={newCorral.tipoSuperficie}
              onChange={(e) =>
                setNewCorral({ ...newCorral, tipoSuperficie: e.target.value })
              }
            />
            <select
              value={newCorral.estado}
              onChange={(e) =>
                setNewCorral({ ...newCorral, estado: e.target.value })
              }
            >
              <option value="Activo">Activo</option>
              <option value="Inactivo">Inactivo</option>
            </select>
            <button type="submit">Crear</button>
            {msgCreate && <p className="msg">{msgCreate}</p>}
            {errorCreate && <p className="error">{errorCreate}</p>}
          </form>
        )}

        {/* === LISTAR === */}
        {activeTab === "listar" && (
          <div className="list-container">
            <h2>Listado de Corrales</h2>
            <table className="list-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Cabezas</th>
                  <th>Cap. Máx</th>
                  <th>Superficie</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                {corrals.map((c) => (
                  <tr key={c.id}>
                    <td>{c.id}</td>
                    <td>{c.cabezas}</td>
                    <td>{c.capacidadMax}</td>
                    <td>{c.tipoSuperficie}</td>
                    <td>{c.estado}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* === DETALLE === */}
        {activeTab === "detalle" && (
          <div className="detail-card">
            <h2>Detalle de Corral</h2>
            <form className="detail-form" onSubmit={handleSearch}>
              <input
                placeholder="ID de Corral"
                value={searchId}
                onChange={(e) => setSearchId(e.target.value)}
              />
              <button type="submit">🔍</button>
            </form>
            {errorSearch && <p className="error">{errorSearch}</p>}
            {selectedCorral && (
              <>
                <table className="detail-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Cabezas</th>
                      <th>Cap.Max</th>
                      <th>Superficie</th>
                      <th>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>{selectedCorral.id}</td>
                      <td>{selectedCorral.cabezas}</td>
                      <td>{selectedCorral.capacidadMax}</td>
                      <td>{selectedCorral.tipoSuperficie}</td>
                      <td>{selectedCorral.estado}</td>
                    </tr>
                  </tbody>
                </table>

                <h4>Animales Asignados</h4>
                {selectedCorral.assignedAnimals.length > 0 ? (
                  <div className="scroll-list">
                    {selectedCorral.assignedAnimals.map((aid) => (
                      <div key={aid} className="scroll-item">
                        Animal #{aid}
                      </div>
                    ))}
                  </div>
                ) : (
                  <p>No hay animales asignados</p>
                )}
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
