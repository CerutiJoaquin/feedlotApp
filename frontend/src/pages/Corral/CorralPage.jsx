import React, { useState, useEffect, useMemo } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import {
  getAllCorral,
  getCorralById,
  createCorral,
  updateCorral,
  deleteCorral,
} from "../../services/corralService";

import {
  getAllRecorridos,
  createRecorrido,
} from "../../services/registroRecorridoService.js";

import {
  getAllComederos,
  createComedero,
} from "../../services/registroComederoService.js";

import { getAllInsumos } from "../../services/insumoService";
import "./CorralPage.css";

export default function CorralPage() {
  const tabs = useMemo(
    () => [
      { key: "crear", label: "Crear" },
      { key: "listar", label: "Listar" },
      { key: "detalle", label: "Detalle" },
      { key: "recorrido", label: "Recorrido" },
      { key: "alimentar", label: "Alimentar" },
    ],
    []
  );
  const [activeTab, setActiveTab] = useState("listar");

  // data bases
  const [corrals, setCorrals] = useState([]);
  const [alimentos, setAlimentos] = useState([]); // insumos tipo ALIMENTO

  // loading flags
  const [loadingCorrales, setLoadingCorrales] = useState(false);
  const [loadingRecorridos, setLoadingRecorridos] = useState(false);
  const [loadingComederos, setLoadingComederos] = useState(false);

  // editar/listar corrales
  const [editingId, setEditingId] = useState(null);
  const [editData, setEditData] = useState({});

  // detalle
  const [searchId, setSearchId] = useState("");
  const [selectedCorral, setSelectedCorral] = useState(null);
  const [errorSearch, setErrorSearch] = useState("");

  // crear corral
  const [newCorral, setNewCorral] = useState({
    capacidadMax: "",
    tipoSuperficie: "",
    estado: "Activo",
  });
  const [msgCreate, setMsgCreate] = useState("");
  const [errorCreate, setErrorCreate] = useState("");

  // Recorrido
  const [recorridoSubTab, setRecorridoSubTab] = useState("registrar");
  const [recorridoForm, setRecorridoForm] = useState({
    corralId: "",
    observaciones: "",
  });
  const [recorridos, setRecorridos] = useState([]);
  const [msgRecorrido, setMsgRecorrido] = useState("");
  const [errRecorrido, setErrRecorrido] = useState("");

  // Alimentar
  const [alimentarSubTab, setAlimentarSubTab] = useState("registrar");
  const [alimentarForm, setAlimentarForm] = useState({
    corralId: "",
    insumoId: "",
    cantidad: "",
  });
  const [comederos, setComederos] = useState([]);
  const [msgAlimentar, setMsgAlimentar] = useState("");
  const [errAlimentar, setErrAlimentar] = useState("");

  // init
  useEffect(() => {
    fetchCorrals();
    fetchAlimentos();
  }, []);

  useEffect(() => {
    if (activeTab === "recorrido" && recorridoSubTab === "listar") {
      loadRecorridos();
    }
  }, [activeTab, recorridoSubTab]);

  useEffect(() => {
    if (activeTab === "alimentar" && alimentarSubTab === "listar") {
      loadComederos();
    }
  }, [activeTab, alimentarSubTab]);

  // fetchers
  const fetchCorrals = async () => {
    try {
      setLoadingCorrales(true);
      const res = await getAllCorral();
      const sorted = (res?.data ?? [])
        .slice()
        .sort((a, b) => a.corralId - b.corralId);
      setCorrals(sorted);
    } catch (e) {
      console.error("Error al cargar corrales", e);
      setCorrals([]);
    } finally {
      setLoadingCorrales(false);
    }
  };

  const fetchAlimentos = async () => {
    try {
      const res = await getAllInsumos();
      const todos = res?.data ?? [];
      const soloAlimentos = todos.filter(
        (i) => (i?.tipo || "").toUpperCase() === "ALIMENTO"
      );
      setAlimentos(soloAlimentos);
    } catch (e) {
      console.error("Error al cargar insumos", e);
      setAlimentos([]);
    }
  };

  const loadRecorridos = async () => {
    try {
      setLoadingRecorridos(true);
      const res = await getAllRecorridos();
      setRecorridos(res?.data ?? []);
    } catch (e) {
      console.error(e);
      setRecorridos([]);
    } finally {
      setLoadingRecorridos(false);
    }
  };

  const loadComederos = async () => {
    try {
      setLoadingComederos(true);
      const res = await getAllComederos();

      setComederos(res?.data ?? []);
    } catch (e) {
      console.error(e);
      setComederos([]);
    } finally {
      setLoadingComederos(false);
    }
  };

  // crear corral
  const handleCreate = async (e) => {
    e.preventDefault();
    setMsgCreate("");
    setErrorCreate("");

    const cap = Number.parseInt(newCorral.capacidadMax, 10);
    if (
      !Number.isFinite(cap) ||
      cap <= 0 ||
      !newCorral.tipoSuperficie ||
      !newCorral.estado
    ) {
      setErrorCreate("Completa todos los campos (capacidad > 0)");
      return;
    }

    try {
      const body = {
        capacidadMax: cap,
        tipoSuperficie: newCorral.tipoSuperficie.trim(),
        estado: newCorral.estado,
      };
      await createCorral(body);
      setMsgCreate("Corral creado con éxito");
      setNewCorral({ capacidadMax: "", tipoSuperficie: "", estado: "Activo" });
      await fetchCorrals();
      setActiveTab("listar");
    } catch (error) {
      console.error(error);
      setErrorCreate("Error al intentar crear el corral");
    }
  };

  // editar inline
  const startEdit = (row) => {
    setEditingId(row.corralId);
    setEditData({
      corralId: row.corralId,
      capacidadMax: row.capacidadMax,
      tipoSuperficie: row.tipoSuperficie ?? "",
      estado: row.estado ?? "Activo",
    });
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditData({});
  };

  const saveEdit = async () => {
    const body = {
      capacidadMax: Number.parseInt(editData.capacidadMax, 10),
      tipoSuperficie: (editData.tipoSuperficie || "").trim(),
      estado: editData.estado,
    };

    try {
      await updateCorral(editingId, body);
      await fetchCorrals();
    } catch (err) {
      console.error("Error al actualizar corral", err);
    } finally {
      setEditingId(null);
      setEditData({});
    }
  };

  // eliminar
  const handleDelete = async (id) => {
    if (!window.confirm("¿Seguro que deseas eliminar este corral?")) return;
    try {
      await deleteCorral(id);
      await fetchCorrals();
    } catch (error) {
      console.error("Error al eliminar el corral", error);
    }
  };

  // detalle
  const handleSearch = async (e) => {
    e.preventDefault();
    setErrorSearch("");
    const id = parseInt(searchId, 10);
    if (!id) {
      setErrorSearch("Ingresa un ID válido");
      return;
    }
    try {
      const res = await getCorralById(id);
      setSelectedCorral(res?.data ?? null);
      if (!res?.data) setErrorSearch("Corral no encontrado");
    } catch {
      setSelectedCorral(null);
      setErrorSearch("Corral no encontrado");
    }
  };

  // registrar recorrido
  const submitRecorrido = async (e) => {
    e.preventDefault();
    setMsgRecorrido("");
    setErrRecorrido("");

    const corralId = parseInt(recorridoForm.corralId, 10);
    if (!Number.isFinite(corralId) || !recorridoForm.descripcion.trim()) {
      setErrRecorrido("Selecciona un corral y escribe una descripción");
      return;
    }

    try {
      await createRecorrido({
        corralId,
        observaciones: recorridoForm.observaciones.trim(),
      });
      setMsgRecorrido("Recorrido registrado");
      setRecorridoForm({ corralId: "", observaciones: "" });
      if (recorridoSubTab === "listar") await loadRecorridos();
    } catch (err) {
      console.error(err);
      setErrRecorrido("No se pudo registrar el recorrido");
    }
  };

  // registrar alimentación
  const submitAlimentar = async (e) => {
    e.preventDefault();
    setMsgAlimentar("");
    setErrAlimentar("");

    const corralId = parseInt(alimentarForm.corralId, 10);
    const insumoId = parseInt(alimentarForm.insumoId, 10);
    const cantidad = Number.parseFloat(alimentarForm.cantidad);

    if (
      !Number.isFinite(corralId) ||
      !Number.isFinite(insumoId) ||
      !(cantidad > 0)
    ) {
      setErrAlimentar("Selecciona corral, insumo y una cantidad > 0");
      return;
    }

    try {
      await createComedero({ corralId, insumoId, cantidad });
      setMsgAlimentar("Alimentación registrada y stock descontado");
      setAlimentarForm({ corralId: "", insumoId: "", cantidad: "" });
      if (alimentarSubTab === "listar") await loadComederos();
    } catch (err) {
      console.error(err);
      setErrAlimentar(
        err?.response?.data || "No se pudo registrar la alimentación"
      );
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

        {/* LISTAR CORRALES */}
        {activeTab === "listar" && (
          <div className="list-container">
            <h2>Listado de Corrales</h2>

            {loadingCorrales ? (
              <p style={{ padding: 8 }}>Cargando…</p>
            ) : (
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
                  {(Array.isArray(corrals) ? corrals : []).map((row) => {
                    const isEditing = editingId === row.corralId;
                    const cabezas = row.cabezas ?? row.animales?.length ?? 0;

                    return (
                      <tr key={row.corralId}>
                        <td>{row.corralId}</td>
                        <td>{cabezas}</td>

                        <td>
                          {isEditing ? (
                            <input
                              type="number"
                              value={editData.capacidadMax}
                              onChange={(e) =>
                                setEditData({
                                  ...editData,
                                  capacidadMax: e.target.value,
                                })
                              }
                              className="inline-input"
                            />
                          ) : (
                            row.capacidadMax
                          )}
                        </td>

                        {/* Superficie */}
                        <td>
                          {isEditing ? (
                            <input
                              value={editData.tipoSuperficie}
                              onChange={(e) =>
                                setEditData({
                                  ...editData,
                                  tipoSuperficie: e.target.value,
                                })
                              }
                              className="inline-input"
                            />
                          ) : (
                            row.tipoSuperficie
                          )}
                        </td>

                        {/* Estado */}
                        <td>
                          {isEditing ? (
                            <select
                              value={editData.estado}
                              onChange={(e) =>
                                setEditData({
                                  ...editData,
                                  estado: e.target.value,
                                })
                              }
                              className="inline-select"
                            >
                              <option value="Activo">Activo</option>
                              <option value="Inactivo">Inactivo</option>
                            </select>
                          ) : (
                            row.estado
                          )}
                        </td>

                        {/* Acciones */}
                        <td>
                          {isEditing ? (
                            <div className="actions">
                              <button
                                type="button"
                                className="btn btn--save"
                                onClick={saveEdit}
                              >
                                ✅ Guardar
                              </button>
                              <button
                                type="button"
                                className="btn btn--danger"
                                onClick={cancelEdit}
                              >
                                Cancelar
                              </button>
                            </div>
                          ) : (
                            <div className="actions">
                              <button
                                type="button"
                                className="btn btn--edit"
                                onClick={() => startEdit(row)}
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
                          )}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
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

        {/* RECORRIDO */}
        {activeTab === "recorrido" && (
          <div className="subtabs">
            <div className="subtabs__bar">
              <button
                className={`subtab-btn ${
                  recorridoSubTab === "registrar" ? "is-active" : ""
                }`}
                onClick={() => setRecorridoSubTab("registrar")}
                type="button"
              >
                Registrar
              </button>
              <button
                className={`subtab-btn ${
                  recorridoSubTab === "listar" ? "is-active" : ""
                }`}
                onClick={() => setRecorridoSubTab("listar")}
                type="button"
              >
                Listar
              </button>
            </div>

            {recorridoSubTab === "registrar" ? (
              <form className="form-crear" onSubmit={submitRecorrido}>
                <h2>Registrar Recorrido</h2>

                <select
                  value={recorridoForm.corralId}
                  onChange={(e) =>
                    setRecorridoForm({
                      ...recorridoForm,
                      corralId: e.target.value,
                    })
                  }
                >
                  <option value="">Selecciona un corral…</option>
                  {corrals.map((c) => (
                    <option key={c.corralId} value={c.corralId}>
                      #{c.corralId} — {c.tipoSuperficie} — {c.estado}
                    </option>
                  ))}
                </select>

                <textarea
                  placeholder="Observaciones de la recorrida"
                  value={recorridoForm.observaciones}
                  onChange={(e) =>
                    setRecorridoForm({
                      ...recorridoForm,
                      observaciones: e.target.value,
                    })
                  }
                  rows={4}
                />

                <button type="submit">Guardar Recorrido</button>
                {msgRecorrido && <p className="msg">{msgRecorrido}</p>}
                {errRecorrido && <p className="error">{errRecorrido}</p>}
              </form>
            ) : (
              <div className="list-container">
                <div className="list-header">
                  <h2>Recorridos</h2>
                  <button
                    className="btn"
                    onClick={loadRecorridos}
                    type="button"
                  >
                    ↻ Refrescar
                  </button>
                </div>
                {loadingRecorridos ? (
                  <p style={{ padding: 8 }}>Cargando…</p>
                ) : (
                  <table className="list-table">
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Corral</th>
                        <th>Observaciones</th>
                        <th>Fecha</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(recorridos ?? []).map((r) => (
                        <tr key={r.registroRecorridoId}>
                          <td>{r.registroRecorridoId}</td>
                          <td>{r.corralId}</td>
                          <td style={{ textAlign: "left" }}>
                            {r.observaciones}
                          </td>
                          <td>{r.fecha}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </div>
            )}
          </div>
        )}

        {/* ALIMENTAR */}
        {activeTab === "alimentar" && (
          <div className="subtabs">
            <div className="subtabs__bar">
              <button
                className={`subtab-btn ${
                  alimentarSubTab === "registrar" ? "is-active" : ""
                }`}
                onClick={() => setAlimentarSubTab("registrar")}
                type="button"
              >
                Registrar
              </button>
              <button
                className={`subtab-btn ${
                  alimentarSubTab === "listar" ? "is-active" : ""
                }`}
                onClick={() => setAlimentarSubTab("listar")}
                type="button"
              >
                Listar
              </button>
            </div>

            {alimentarSubTab === "registrar" ? (
              <form className="form-crear" onSubmit={submitAlimentar}>
                <h2>Alimentar Corral</h2>

                <select
                  value={alimentarForm.corralId}
                  onChange={(e) =>
                    setAlimentarForm({
                      ...alimentarForm,
                      corralId: e.target.value,
                    })
                  }
                >
                  <option value="">Selecciona un corral…</option>
                  {corrals.map((c) => (
                    <option key={c.corralId} value={c.corralId}>
                      #{c.corralId} — {c.tipoSuperficie} — {c.estado}
                    </option>
                  ))}
                </select>

                <select
                  value={alimentarForm.insumoId}
                  onChange={(e) =>
                    setAlimentarForm({
                      ...alimentarForm,
                      insumoId: e.target.value,
                    })
                  }
                >
                  <option value="">Selecciona el alimento…</option>
                  {alimentos.map((i) => (
                    <option key={i.insumoId} value={i.insumoId}>
                      {i.nombre} — {i.unidadMedida ?? "kg"} (stock: {i.cantidad}
                      )
                    </option>
                  ))}
                </select>

                <input
                  type="number"
                  step="0.001"
                  placeholder="Cantidad a servir"
                  value={alimentarForm.cantidad}
                  onChange={(e) =>
                    setAlimentarForm({
                      ...alimentarForm,
                      cantidad: e.target.value,
                    })
                  }
                />

                <button type="submit">Guardar Alimentación</button>
                {msgAlimentar && <p className="msg">{msgAlimentar}</p>}
                {errAlimentar && <p className="error">{errAlimentar}</p>}
              </form>
            ) : (
              <div className="list-container">
                <div className="list-header">
                  <h2>Alimentaciones</h2>
                  <button className="btn" onClick={loadComederos} type="button">
                    ↻ Refrescar
                  </button>
                </div>
                {loadingComederos ? (
                  <p style={{ padding: 8 }}>Cargando…</p>
                ) : (
                  <table className="list-table">
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Corral</th>
                        <th>Insumo</th>
                        <th>Cantidad</th>
                        <th>Fecha</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(comederos ?? []).map((c) => {
                        const corralShown =
                          c.corralId ?? c.corral ?? c.corral?.corralId ?? "";
                        const insumoShown = 
                          c.insumoId ?? c.insumo ?? c.insumo?.insumoId ?? "";
                        const cantShown =
                          typeof c.cantidad === "number"
                            ? c.cantidad.toFixed(3)
                            : Number.parseFloat(c.cantidad ?? "0").toFixed(3);
                            

                        return (
                          <tr key={c.registroComederoId}>
                            <td>{c.registroComederoId}</td>
                            <td>{corralShown}</td>
                            <td>{insumoShown}</td>
                            <td>{cantShown}</td>
                            <td>{c.fecha}</td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                )}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
