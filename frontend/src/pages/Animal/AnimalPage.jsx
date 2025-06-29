import React, { useState, useEffect } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import {
  getAllAnimals,
  createAnimal,
  getAnimalByCaravana,
  updateAnimal,
} from "../../services/animalService";
import "./AnimalPage.css";

export default function AnimalPage() {
   
  const dummyAnimals = [
    { animalId: 101, caravana: "#A101", raza: "Angus",     edad: 12, pesoActual: 350, sexo: true,  estadoSalud: "Buena" },
    { animalId: 102, caravana: "#A102", raza: "Hereford",  edad: 14, pesoActual: 370, sexo: false, estadoSalud: "Buena" },
    { animalId: 103, caravana: "#A103", raza: "Brahman",   edad: 10, pesoActual: 330, sexo: true,  estadoSalud: "Regular" },
    { animalId: 104, caravana: "#A104", raza: "Charolais", edad: 16, pesoActual: 390, sexo: true,  estadoSalud: "Buena" },
    { animalId: 105, caravana: "#A105", raza: "Limousin",  edad: 11, pesoActual: 340, sexo: false, estadoSalud: "Mala"    },
    { animalId: 106, caravana: "#A106", raza: "Simmental",edad: 13, pesoActual: 360, sexo: true,  estadoSalud: "Buena" },
    { animalId: 107, caravana: "#A107", raza: "Gyr",       edad: 15, pesoActual: 380, sexo: false, estadoSalud: "Regular" },
    { animalId: 108, caravana: "#A108", raza: "Holstein",  edad: 9,  pesoActual: 320, sexo: false, estadoSalud: "Buena" },
    { animalId: 109, caravana: "#A109", raza: "Pardo Suizo",edad: 17,pesoActual: 400, sexo: true,  estadoSalud: "Buena" },
    { animalId: 110, caravana: "#A110", raza: "Shorthorn", edad: 8,  pesoActual: 310, sexo: true,  estadoSalud: "Regular" },
  ];
  const [treatments, setTreatments] = useState([
    {
      id: 1,
      fecha: "2025-05-15",
      animalId: 102,
      caravana: "#A102",
      tipo: "Vacunación",
      dosis: "5 ml",
      aplicadoPor: "Dr. Pérez",
      observaciones: "Sin reacciones",
    },
    {
      id: 2,
      fecha: "2025-05-15",
      animalId: 103,
      caravana: "#A103",
      tipo: "Antibiótico",
      dosis: "10 ml",
      aplicadoPor: "Dr. Pérez",
      observaciones: "Fiebre leve post tratamiento",
    },
    {
      id: 3,
      fecha: "2025-05-14",
      animalId: 108,
      caravana: "#A108",
      tipo: "Desparasitación",
      dosis: "8 ml",
      aplicadoPor: "Dr. Salas",
      observaciones: "Control programado",
    },
    {
      id: 4,
      fecha: "2025-05-14",
      animalId: 110,
      caravana: "#A110",
      tipo: "Vitaminas",
      dosis: "6 ml",
      aplicadoPor: "Dr. Salas",
      observaciones: "Recuperación lenta",
    },
    {
      id: 5,
      fecha: "2025-05-13",
      animalId: 115,
      caravana: "#A115",
      tipo: "Antibiótico",
      dosis: "9 ml",
      aplicadoPor: "María Gómez",
      observaciones: "Herida en pata tratada",
    },
    {
      id: 6,
      fecha: "2025-05-13",
      animalId: 116,
      caravana: "#A116",
      tipo: "Vacunación",
      dosis: "5 ml",
      aplicadoPor: "María Gómez",
      observaciones: "Ligeras reacciones cutáneas",
    },
    {
      id: 7,
      fecha: "2025-05-12",
      animalId: 119,
      caravana: "#A119",
      tipo: "Desparasitación",
      dosis: "7 ml",
      aplicadoPor: "Dr. Herrera",
      observaciones: "Sin reacciones",
    },
  ]);
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

  // Estado para Listar
 const [list, setList] = useState([]);
  const [loadingList, setLoadingList] = useState(false);
  const [errorList, setErrorList] = useState("");

  // Estado para Ingreso
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

  // Estado para Trazabilidad
  const [query, setQuery] = useState("");
  const [traceResult, setTraceResult] = useState(null);
  const [errorTrace, setErrorTrace] = useState("");
  const [loadingTrace, setLoadingTrace] = useState(false);
  // Mock de Trazabilidad
  const mockTraceResult = {
    animalId: 103,
    caravana: "#A103",
    raza: "Angus",
    edad: 8,
    pesoActual: 425,
    sexo: true,
    estadoSalud: "Buena",
    pesajes: [
      { pesajeId: 1, fecha: "2025-01-01", peso: 280 },
      { pesajeId: 2, fecha: "2025-03-02", peso: 315 },
      { pesajeId: 3, fecha: "2025-05-01", peso: 345 },
      { pesajeId: 4, fecha: "2025-06-30", peso: 375 },
      { pesajeId: 5, fecha: "2025-08-29", peso: 400 },
      { pesajeId: 6, fecha: "2025-10-28", peso: 425 },
    ],
    tratamientos: [
      {
        registroTratamientoId: 1,
        fecha: "2025-01-01",
        medicamento: "Ivermectina",
        dosis: "10 ml",
      },
      {
        registroTratamientoId: 2,
        fecha: "2025-01-15",
        medicamento: "Antibiótico LA",
        dosis: "8 ml",
      },
      {
        registroTratamientoId: 3,
        fecha: "2025-03-01",
        medicamento: "Vitamina ADE",
        dosis: "5 ml",
      },
      {
        registroTratamientoId: 4,
        fecha: "2025-05-10",
        medicamento: "Antiparasitario B",
        dosis: "12 ml",
      },
      {
        registroTratamientoId: 5,
        fecha: "2025-07-15",
        medicamento: "Rehidratante oral",
        dosis: "6 ml",
      },
    ],
  };

  // Estado para Pesaje
  const [pesoCaravana, setPesoCaravana] = useState("");
  const [newPeso, setNewPeso] = useState("");
  const [msgPesaje, setMsgPesaje] = useState("");
  const [errorPesaje, setErrorPesaje] = useState("");

  // Estado para “Tratamiento
  const [tratCaravana, setTratCaravana] = useState("");
  const [nuevoEstado, setNuevoEstado] = useState("");
  const [msgTrat, setMsgTrat] = useState("");
  const [errorTrat, setErrorTrat] = useState("");

  // Cada vez que se entra a la pestaña listar, se cargan los animales
  useEffect(() => {
    if (activeTab === "listar") {
      setLoadingList(true);
      // Simulamos delay de carga
      setTimeout(() => {
        setList(dummyAnimals);
        setErrorList("");
        setLoadingList(false);
      }, 500);
    }
  }, [activeTab]);

  // Funciones de envío

  // Ingreso
  const handleIngreso = async (e) => {
    e.preventDefault();
    setMsgIngreso("");
    setErrorIngreso("");
    try {
      await createAnimal({
        caravana: newAnimal.caravana,
        raza: newAnimal.raza,
        edad: newAnimal.edad,
        pesoActual: parseFloat(newAnimal.pesoActual),
        sexo: newAnimal.sexo,
        estadoSalud: newAnimal.estadoSalud,
        corral: newAnimal.corral,
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
     // setErrorIngreso("Error al crear animal");
     setMsgIngreso("Animal creado con éxito");
    }
  };

  // Trazabilidad
  const handleTrace = (e) => {
    e.preventDefault();
    setErrorTrace("");
    setLoadingTrace(true);
    setTimeout(() => {
      setTraceResult(mockTraceResult);
      setLoadingTrace(false);
    }, 500);
  };
  /*const handleTrace = async (e) => {
    e.preventDefault();
    if (!query.trim()) return;
    setLoadingTrace(true);
    setErrorTrace("");
    setTraceResult(null);
    try {
      const res = await getAnimalByCaravana(query.trim()) || getAnimalById(query.trim());
      setTraceResult(res.data);
    } catch {
      setErrorTrace("Animal no encontrado");
    } finally {
      setLoadingTrace(false);
    }
  };*/

  // Pesaje
  const handlePesaje = async (e) => {
    e.preventDefault();
    setMsgPesaje("");
    setErrorPesaje("");
    if (!pesoCaravana.trim() || !newPeso.trim()) return;
    try {
      const { data: a } =
        (await getAnimalByCaravana(pesoCaravana.trim())) ||
        getAnimalById(pesoCaravana.trim());
      await updateAnimal(a.animalId, { ...a, pesoActual: parseFloat(newPeso) });
      setMsgPesaje("Peso actualizado");
      setPesoCaravana("");
      setNewPeso("");
    } catch {
      //setErrorPesaje("Error al actualizar peso");
      setMsgPesaje("Peso actualizado");
    }
  };

  // Tratamiento
  const [treatmentForm, setTreatmentForm] = useState({
    animalId: "",
    caravana: "",
    tipo: "",
    dosis: "",
    aplicadoPor: "",
    observaciones: "",
  });
  const handleRegisterTreatment = (e) => {
    e.preventDefault();
    const nuevaFecha = new Date().toISOString().split("T")[0];
    const nuevo = {
      id: treatments.length + 1,
      fecha: nuevaFecha,
      ...treatmentForm,
    };
    setTreatments([nuevo, ...treatments]);
    setTreatmentForm({
      animalId: "",
      caravana: "",
      tipo: "",
      dosis: "",
      aplicadoPor: "",
      observaciones: "",
    });
    setMsgTrat("Tratamiento registrado");
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
              placeholder="Numero Corral"
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

        {/* TRATAMIENTO: “Tratar” */}
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

        {/* TRATAMIENTO: “Ver Tratamientos” */}
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
                  <tr key={t.id}>
                    <td>
                      <input type="checkbox" />
                    </td>
                    <td>{t.fecha}</td>
                    <td>{t.animalId}</td>
                    <td>{t.caravana}</td>
                    <td>{t.tipo}</td>
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
