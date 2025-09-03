import React, { useState, useEffect } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import {
  getAllCorral,
  getCorralById,
  createCorral,
  updateCorral,
  deleteCorral,
} from "../../services/corralService";
import "./CorralPage.css";

export default function CorralPage() {
  const tabs = [
    { key: "crear", label: "Crear" },
    { key: "listar", label: "Listar" },
    { key: "detalle", label: "Detalle" },
  ];
  const [activeTab, setActiveTab] = useState("listar");

  const [corrals, setCorrals] = useState([]);
  const [newCorral, setNewCorral] = useState({
    capacidadMax: "",
    tipoSuperficie: "",
    estado: "Activo",
  });
  const [msgCreate, setMsgCreate] = useState("");
  const [errorCreate, setErrorCreate] = useState("");

  const [editingCorral, setEditingCorral] = useState(null);

  const [searchId, setSearchId] = useState("");
  const [selectedCorral, setSelectedCorral] = useState(null);
  const [errorSearch, setErrorSearch] = useState("");

  useEffect(() => {
    fetchCorrals();
  }, []);

  const fetchCorrals = async () => {
    try {
      const res = await getAllCorral();
      const sorted = (res?.data ?? [])
        .slice()
        .sort((a, b) => a.corralId - b.corralId);
      setCorrals(sorted);
    } catch (e) {
      console.error("Error cargando corrales", e);
      setCorrals([]);
    }
  };

  // Crear
  const handleCreate = async (e) => {
    e.preventDefault();
    setMsgCreate("");
    setErrorCreate("");

    const { capacidadMax, tipoSuperficie, estado } = newCorral;
    if (!capacidadMax || !tipoSuperficie || !estado) {
      setErrorCreate("Completa todos los campos");
      return;
    }

    try {
      const body = {
        capacidadMax: parseInt(capacidadMax, 10),
        tipoSuperficie,
        estado,
      };
      await createCorral(body);
      setMsgCreate("Corral creado con éxito");
      setNewCorral({ capacidadMax: "", tipoSuperficie: "", estado: "Activo" });
      await fetchCorrals();
      setActiveTab("listar");
    } catch (error) {
      console.error(error);
      if (capacidadMax <= 0) {
        setErrorCreate("La capacidad máxima debe ser mayor a 0");
      } else {
        setErrorCreate("Error al intentar crear el corral");
      }
    }
  };

  // Editar
  const handleEditClick = (row) => setEditingCorral(row);

  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      const body = {
        ...editingCorral,
        capacidadMax: parseInt(editingCorral.capacidadMax, 10),
      };
      await updateCorral(editingCorral.corralId, body);
      setEditingCorral(null);
      await fetchCorrals();
    } catch (error) {
      console.log("Error al actualizar corral", error);
    }
  };

  // Eliminar
  const handleDelete = async (id) => {
    if (!window.confirm("¿Seguro que deseas eliminar este corral?")) return;
    try {
      await deleteCorral(id);
      await fetchCorrals();
    } catch (error) {
      console.error("Error al eliminar el corral", error);
    }
  };

  // Detalle
  const handleSearch = (e) => {
    e.preventDefault();
    setErrorSearch("");
    const id = parseInt(searchId, 10);
    if (!id) {
      setErrorSearch("Ingresa un ID válido");
      return;
    }
    const found = corrals.find((x) => x.corralId === id);
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
        {/* CREAR */}
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

        {/* LISTAR */}
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
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {(Array.isArray(corrals) ? corrals : []).map((row) => (
                  <tr key={row.corralId}>
                    <td>{row.corralId}</td>
                    <td>{row.cabezas ?? row.animales?.length ?? 0}</td>
                    <td>{row.capacidadMax}</td>
                    <td>{row.tipoSuperficie}</td>
                    <td>{row.estado}</td>
                    <td>
                      <div className="actions">
                        <button
                          type="button"
                          className="btn btn--edit"
                          onClick={() => handleEditClick(row)}
                          title="Editar"
                        >
                          <span className="btn__icon">✏️</span>
                          <span className="btn__label">Editar</span>
                        </button>
                        <button
                          type="button"
                          className="btn btn--danger"
                          onClick={() => handleDelete(row.corralId)}
                          title="Eliminar"
                        >
                          <span className="btn__icon">🗑️</span>
                          <span className="btn__label">Eliminar</span>
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {editingCorral && (
              <form className="form-editar" onSubmit={handleUpdate}>
                <h3>Editar Corral #{editingCorral?.corralId}</h3>
                <input
                  type="number"
                  value={editingCorral.capacidadMax}
                  onChange={(e) =>
                    setEditingCorral({
                      ...editingCorral,
                      capacidadMax: e.target.value,
                    })
                  }
                />
                <input
                  value={editingCorral.tipoSuperficie}
                  onChange={(e) =>
                    setEditingCorral({
                      ...editingCorral,
                      tipoSuperficie: e.target.value,
                    })
                  }
                />
                <select
                  value={editingCorral.estado}
                  onChange={(e) =>
                    setEditingCorral({
                      ...editingCorral,
                      estado: e.target.value,
                    })
                  }
                >
                  <option value="Activo">Activo</option>
                  <option value="Inactivo">Inactivo</option>
                </select>
                <button type="submit">Guardar Cambios</button>
              </form>
            )}
          </div>
        )}

        {/* DETALLE */}
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
                      <th>Cap. Máx</th>
                      <th>Superficie</th>
                      <th>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>{selectedCorral.corralId}</td>
                      <td>
                        {selectedCorral.cabezas ??
                          selectedCorral.animales?.length ??
                          0}
                      </td>
                      <td>{selectedCorral.capacidadMax}</td>
                      <td>{selectedCorral.tipoSuperficie}</td>
                      <td>{selectedCorral.estado}</td>
                    </tr>
                  </tbody>
                </table>

                {Array.isArray(selectedCorral.animales) &&
                selectedCorral.animales.length > 0 ? (
                  <>
                    <h4>Animales Asignados</h4>
                    <div className="scroll-list">
                      {selectedCorral.animales.map((a) => (
                        <div key={a.animalId} className="scroll-item">
                          Animal #{a.animalId}
                          {a.caravana ? ` — ${a.caravana}` : ""}
                        </div>
                      ))}
                    </div>
                  </>
                ) : null}
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
