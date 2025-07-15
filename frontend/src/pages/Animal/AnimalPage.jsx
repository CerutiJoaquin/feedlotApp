import React, { useState, useEffect } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import {
  getAllAnimals,
  getAnimalByCaravana,
  getAnimalById,
  createAnimal,
  updateAnimal,
  trace,
  getPesajesByAnimal,
  getTratamientosByAnimal,
  addTreatment,
} from "../../services/animalService";
import "./AnimalPage.css";

export default function AnimalPage() {
  const tabs = [
    { key: "ingreso", label: "Ingresar" },
    { key: "listar", label: "Listar" },
    { key: "trazabilidad", label: "Trazabilidad" },
    { key: "pesaje", label: "Pesar" },
    {
      key: "tratamiento",
      label: "Tratamiento",
      dropdown: [
        { key: "tratar", label: "Tratar" },
        { key: "tratamientos", label: "Ver Tratamientos" },
      ],
    },
  ];

  const [activeTab, setActiveTab] = useState("listar");

  // ─── LISTAR ──────────────────────────────────────────────────────────
  const [list, setList] = useState([]);
  const [loadingList, setLoadingList] = useState(false);
  const [errorList, setErrorList] = useState("");

  useEffect(() => {
    if (activeTab === "listar") {
      setLoadingList(true);
      getAllAnimals()
        .then(({ data }) => {
          setList(data);
          setErrorList("");
        })
        .catch(() => {
          setErrorList("Error al cargar animales");
        })
        .finally(() => {
          setLoadingList(false);
        });
    }
  }, [activeTab]);

  // ─── INGRESO ─────────────────────────────────────────────────────────
  const [newAnimal, setNewAnimal] = useState({
    caravana: "",
    raza: "",
    edad: "",
    pesoActual: "",
    sexo: true,
    estadoSalud: "",
    corral: "",
  });
  const [msgIngreso, setMsgIngreso] = useState("");
  const [errorIngreso, setErrorIngreso] = useState("");

  const handleIngreso = async (e) => {
    e.preventDefault();
    setMsgIngreso("");
    setErrorIngreso("");
    try {
      await createAnimal({
        caravana: newAnimal.caravana,
        raza: newAnimal.raza,
        edad: parseInt(newAnimal.edad, 10),
        pesoActual: parseFloat(newAnimal.pesoActual),
        sexo: newAnimal.sexo,
        estadoSalud: newAnimal.estadoSalud,
        corral: { corralId: parseInt(newAnimal.corral, 10) },
      });
      setMsgIngreso("Animal creado con éxito");
      setNewAnimal({
        caravana: "",
        raza: "",
        edad: "",
        pesoActual: "",
        sexo: true,
        estadoSalud: "",
        corral: "",
      });
    } catch {
      setErrorIngreso("Error al crear animal");
    }
  };

  // ─── TRAZABILIDAD ────────────────────────────────────────────────────
  const [query, setQuery] = useState("");
  const [traceResult, setTraceResult] = useState(null);
  const [loadingTrace, setLoadingTrace] = useState(false);
  const [errorTrace, setErrorTrace] = useState("");

  const handleTrace = async (e) => {
    e.preventDefault();
    if (!query.trim()) return;
    setLoadingTrace(true);
    setTraceResult(null);
    setErrorTrace("");
    try {
      const res = await trace(query.trim());
      setTraceResult(res.data);
    } catch {
      setErrorTrace("Animal no encontrado");
    } finally {
      setLoadingTrace(false);
    }
  };

  // ─── PESAJE ──────────────────────────────────────────────────────────
  const [pesoCaravana, setPesoCaravana] = useState("");
  const [newPeso, setNewPeso] = useState("");
  const [msgPesaje, setMsgPesaje] = useState("");
  const [errorPesaje, setErrorPesaje] = useState("");

  const handlePesaje = async (e) => {
    e.preventDefault();
    setMsgPesaje("");
    setErrorPesaje("");
    if (!pesoCaravana.trim() || !newPeso.trim()) return;
    try {
      const { data: animal } =
        (await getAnimalByCaravana(pesoCaravana.trim())) ||
        (await getAnimalById(pesoCaravana.trim()));
      await updateAnimal(animal.animalId, {
        ...animal,
        pesoActual: parseFloat(newPeso),
      });
      setMsgPesaje("Peso actualizado");
      setPesoCaravana("");
      setNewPeso("");
    } catch {
      setErrorPesaje("Error al actualizar peso");
    }
  };

  // ─── TRATAMIENTO ──────────────────────────────────────────────────────
  const [treatments, setTreatments] = useState([]);
  const [treatmentForm, setTreatmentForm] = useState({
    animalId: "",
    caravana: "",
    tipo: "",
    dosis: "",
    aplicadoPor: "",
    observaciones: "",
  });
  const [msgTrat, setMsgTrat] = useState("");
  const [errorTrat, setErrorTrat] = useState("");

  const handleRegisterTreatment = async (e) => {
    e.preventDefault();
    setMsgTrat("");
    setErrorTrat("");
    try {
      await addTreatment(parseInt(treatmentForm.animalId, 10), {
        caravana: treatmentForm.caravana,
        tipo: treatmentForm.tipo,
        dosis: treatmentForm.dosis,
        aplicadoPor: treatmentForm.aplicadoPor,
        observaciones: treatmentForm.observaciones,
      });
      const res = await getTratamientosByAnimal(
        parseInt(treatmentForm.animalId, 10)
      );
      setTreatments(res.data);
      setMsgTrat("Tratamiento registrado");
      setTreatmentForm({
        animalId: "",
        caravana: "",
        tipo: "",
        dosis: "",
        aplicadoPor: "",
        observaciones: "",
      });
    } catch {
      setErrorTrat("Error al registrar tratamiento");
    }
  };

  return (
    <div className="animal-page">
      <TabBar tabs={tabs} activeKey={activeTab} onSelect={setActiveTab} />

      <div className="animal-content">
        {/* INGRESO */}
        {activeTab === "ingreso" && (
          <form className="form-ingreso" onSubmit={handleIngreso}>
            <h2>Ingreso de Nuevo Animal</h2>
            <input
              placeholder="Caravana"
              value={newAnimal.caravana}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, caravana: e.target.value })
              }
            />
            <input
              placeholder="Raza"
              value={newAnimal.raza}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, raza: e.target.value })
              }
            />
            <input
              placeholder="Edad (Meses)"
              value={newAnimal.edad}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, edad: e.target.value })
              }
            />
            <input
              type="number"
              placeholder="Peso Actual"
              value={newAnimal.pesoActual}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, pesoActual: e.target.value })
              }
            />
            <select
              value={newAnimal.sexo}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, sexo: e.target.value === "true" })
              }
            >
              <option value="true">Macho</option>
              <option value="false">Hembra</option>
            </select>
            <input
              placeholder="Estado Salud"
              value={newAnimal.estadoSalud}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, estadoSalud: e.target.value })
              }
            />
            <input
              placeholder="ID Corral"
              value={newAnimal.corral}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, corral: e.target.value })
              }
            />
            <button type="submit">Crear</button>
            {msgIngreso && <p className="msg">{msgIngreso}</p>}
            {errorIngreso && <p className="error">{errorIngreso}</p>}
          </form>
        )}

        {/* LISTAR */}
        {activeTab === "listar" && (
          <div className="list-container">
            <h2>Listado de Animales</h2>
            {loadingList ? (
              <p>Cargando…</p>
            ) : errorList ? (
              <p className="error">{errorList}</p>
            ) : (
              <table className="list-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Caravana</th>
                    <th>Raza</th>
                    <th>Edad (Meses)</th>
                    <th>Peso</th>
                    <th>Sexo</th>
                    <th>Salud</th>
                  </tr>
                </thead>
                <tbody>
                  {list.map((a) => (
                    <tr key={a.animalId}>
                      <td>{a.animalId}</td>
                      <td>{a.caravana}</td>
                      <td>{a.raza}</td>
                      <td>{a.edad}</td>
                      <td>{a.pesoActual}</td>
                      <td>{a.sexo ? "Macho" : "Hembra"}</td>
                      <td>{a.estadoSalud}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}

        {/* TRAZABILIDAD */}
        {activeTab === "trazabilidad" && (
          <>
            <div className="trace-card">
              <h2>Trazabilidad del Ganado</h2>
              <form className="trace-form" onSubmit={handleTrace}>
                <input
                  type="text"
                  placeholder="ID o N° Caravana"
                  value={query}
                  onChange={(e) => setQuery(e.target.value)}
                />
                <button type="submit" disabled={loadingTrace}>
                  {loadingTrace ? "Buscando…" : "🔍"}
                </button>
              </form>
              {errorTrace && <p className="trace-error">{errorTrace}</p>}
              {traceResult && (
                <p className="trace-result">
                  • <strong>Caravana:</strong> {traceResult.caravana}
                </p>
              )}
            </div>
            {traceResult && (
              <div className="trace-detail">
                <h3>Animal Nº Caravana: {traceResult.caravana}</h3>
                {/* Datos generales */}
                <table className="detail-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Raza</th>
                      <th>Edad (meses)</th>
                      <th>Peso Actual</th>
                      <th>Sexo</th>
                      <th>Estado Salud</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>{traceResult.animalId}</td>
                      <td>{traceResult.raza}</td>
                      <td>{traceResult.edad}</td>
                      <td>{traceResult.pesoActual}</td>
                      <td>{traceResult.sexo ? "Macho" : "Hembra"}</td>
                      <td>{traceResult.estadoSalud}</td>
                    </tr>
                  </tbody>
                </table>
                {/* Historial de Pesajes */}
                <div className="history-section">
                  <h4>Historial de Pesajes</h4>
                  <table className="history-table">
                    <thead>
                      <tr>
                        {traceResult.pesajes.map((p) => (
                          <th key={p.pesajeId}>
                            {new Date(p.fecha).toLocaleDateString()}
                          </th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        {traceResult.pesajes.map((p) => (
                          <td key={p.pesajeId}>{p.peso}</td>
                        ))}
                      </tr>
                    </tbody>
                  </table>
                </div>
                {/* Historial de Tratamientos */}
                <div className="history-section">
                  <h4>Historial de Tratamientos</h4>
                  <table className="history-table">
                    <thead>
                      <tr>
                        {traceResult.tratamientos.map((t) => (
                          <th key={t.registroTratamientoId}>
                            {new Date(t.fecha).toLocaleDateString()}
                          </th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        {traceResult.tratamientos.map((t) => (
                          <td key={t.registroTratamientoId}>{t.medicamento}</td>
                        ))}
                      </tr>
                      <tr>
                        {traceResult.tratamientos.map((t) => (
                          <td key={t.registroTratamientoId}>{t.dosis}</td>
                        ))}
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            )}
          </>
        )}

        {/* PESAJE */}
        {activeTab === "pesaje" && (
          <form className="form-pesaje" onSubmit={handlePesaje}>
            <h2>Registrar Pesaje</h2>
            <input
              placeholder="ID o Caravana"
              value={pesoCaravana}
              onChange={(e) => setPesoCaravana(e.target.value)}
            />
            <input
              type="number"
              placeholder="Nuevo Peso"
              value={newPeso}
              onChange={(e) => setNewPeso(e.target.value)}
            />
            <button type="submit">Actualizar Peso</button>
            {msgPesaje && <p className="msg">{msgPesaje}</p>}
            {errorPesaje && <p className="error">{errorPesaje}</p>}
          </form>
        )}

        {/* TRATAR */}
        {activeTab === "tratar" && (
          <form className="form-tratamiento" onSubmit={handleRegisterTreatment}>
            <h2>Registrar Tratamiento</h2>
            <input
              placeholder="Animal ID"
              value={treatmentForm.animalId}
              onChange={(e) =>
                setTreatmentForm({ ...treatmentForm, animalId: e.target.value })
              }
            />
            <input
              placeholder="N° Caravana"
              value={treatmentForm.caravana}
              onChange={(e) =>
                setTreatmentForm({ ...treatmentForm, caravana: e.target.value })
              }
            />
            <input
              placeholder="Tipo de Vacuna/Medicamento"
              value={treatmentForm.tipo}
              onChange={(e) =>
                setTreatmentForm({ ...treatmentForm, tipo: e.target.value })
              }
            />
            <input
              placeholder="Dosis"
              value={treatmentForm.dosis}
              onChange={(e) =>
                setTreatmentForm({ ...treatmentForm, dosis: e.target.value })
              }
            />
            <input
              placeholder="Aplicado por"
              value={treatmentForm.aplicadoPor}
              onChange={(e) =>
                setTreatmentForm({
                  ...treatmentForm,
                  aplicadoPor: e.target.value,
                })
              }
            />
            <input
              placeholder="Observaciones"
              rows={3}
              value={treatmentForm.observaciones}
              onChange={(e) =>
                setTreatmentForm({
                  ...treatmentForm,
                  observaciones: e.target.value,
                })
              }
            />
            <button type="submit">Registrar</button>
            {msgTrat && <p className="msg">{msgTrat}</p>}
            {errorTrat && <p className="error">{errorTrat}</p>}
          </form>
        )}

        {/* VER TRATAMIENTOS */}
        {activeTab === "tratamientos" && (
          <div className="treatments-container">
            <h2>Planilla de Tratamientos</h2>
            <div className="treatments-actions">
              <input type="text" placeholder="🔍 Buscar" />
              <input type="date" />
            </div>
            <table className="treatments-table">
              <thead>
                <tr>
                  <th></th>
                  <th>Fecha</th>
                  <th>Animal ID</th>
                  <th>Caravana</th>
                  <th>Tipo</th>
                  <th>Dosis</th>
                  <th>Aplicado por</th>
                  <th>Observaciones</th>
                </tr>
              </thead>
              <tbody>
                {treatments.map((t) => (
                  <tr key={t.registroTratamientoId || t.id}>
                    <td>
                      <input type="checkbox" />
                    </td>
                    <td>{t.fecha}</td>
                    <td>{t.animalId}</td>
                    <td>{t.caravana}</td>
                    <td>{t.tipo || t.medicamento}</td>
                    <td>{t.dosis}</td>
                    <td>{t.aplicadoPor}</td>
                    <td>{t.observaciones}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="treatments-footer">
              <button>✏️ Editar</button>
              <button>💾 Guardar</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
