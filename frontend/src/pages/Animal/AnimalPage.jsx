import React, { useState, useEffect } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import {
  getAllAnimals,
  getAnimalByCaravana,
  getAnimalById,
  createAnimal,
  updateAnimal,
  updateFechaTrat,
  getPesajesByAnimal,
  getTratamientosByAnimal,
} from "../../services/animalService";
import {
  getAllPesajes,
  getPesajeById,
  createPesaje,
  updatePesaje,
} from "../../services/pesajeService";
import {
  getInsumoByCategoria,
  aplicarTratamiento,
} from "../../services/insumoService";
import {
  getAll,
  create,
  getByFechaBetween,
  getByAnimalId,
  getByCaravana,
} from "../../services/registroTratamientoService";
import "./AnimalPage.css";

export default function AnimalPage() {
  const fmtYMD = (s) => (s ? s.split("-").reverse().join("/") : "");

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

  const fetchAnimals = async () => {
    try {
      const res = await getAllAnimals();
      const sorted = (res?.data ?? [])
        .slice()
        .sort((a, b) => a.animalId - b.animalId);
      setAnimals(sorted);
    } catch (e) {
      console.error("Error al cargar los animales", e);
      setAnimals([]);
    }
  };

  const [animals, setAnimals] = useState([]);

  // ───────────────── INGRESO
  const [newAnimal, setNewAnimal] = useState({
    caravana: "",
    raza: "",
    sexo: true,
    pesoActual: "",
    estadoSalud: "",
    corralId: "",
    fechaNacimiento: "",
    proxTratamiento: "",
  });
  const [msgIngreso, setMsgIngreso] = useState("");
  const [errorIngreso, setErrorIngreso] = useState("");

  const handleIngreso = async (e) => {
    e.preventDefault();
    setMsgIngreso("");
    setErrorIngreso("");

    const body = {
      caravana: newAnimal.caravana.trim(),
      raza: newAnimal.raza.trim(),
      sexo: newAnimal.sexo === true || newAnimal.sexo === "true",
      pesoActual: Number.parseFloat(newAnimal.pesoActual),
      estadoSalud: newAnimal.estadoSalud.trim(),
      corralId: Number.parseInt(newAnimal.corralId, 10),
      fechaNacimiento: newAnimal.fechaNacimiento,
      proxTratamiento: newAnimal.proxTratamiento || null,
    };

    try {
      if (
        !body.caravana ||
        !body.raza ||
        !body.estadoSalud ||
        !body.fechaNacimiento
      ) {
        setErrorIngreso(
          "Completa caravana, raza, estado de salud y fecha de nacimiento"
        );
        return;
      }
      if (!Number.isFinite(body.pesoActual) || body.pesoActual <= 0) {
        setErrorIngreso("Peso debe ser un número mayor a 0");
        return;
      }
      if (!Number.isInteger(body.corralId) || body.corralId <= 0) {
        setErrorIngreso("ID de corral inválido");
        return;
      }

      const res = await createAnimal(body);
      const creado = res?.data || {};
      let animalIdCreado = creado.animalId;

      if (!animalIdCreado) {
        const lookup = await getAnimalByCaravana(body.caravana);
        animalIdCreado = lookup?.data?.animalId;
      }

      try {
        if (animalIdCreado) {
          await createPesaje({
            peso: body.pesoActual,
            animalId: animalIdCreado,
          });
        } else {
          console.warn(
            "No se pudo obtener animalId para crear el pesaje inicial"
          );
        }
      } catch (errPesaje) {
        console.error("Error creando pesaje inicial:", errPesaje);

        setMsgIngreso(
          "Animal creado. ⚠️ No se pudo registrar el pesaje inicial."
        );
      }

      setMsgIngreso((m) => m || "Animal ingresado con éxito");
      setNewAnimal({
        caravana: "",
        raza: "",
        sexo: true,
        edad: "",
        pesoActual: "",
        estadoSalud: "",
        corralId: "",
        fechaNacimiento: "",
        proxTratamiento: "",
      });

      fetchAnimals();
    } catch (e) {
      console.log(e);
      setErrorIngreso("Error al ingresar el animal");
    }
  };

  // LISTAR
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

  //  TRAZABILIDAD
  const [query, setQuery] = useState("");
  const [traceResult, setTraceResult] = useState(null);
  const [loadingTrace, setLoadingTrace] = useState(false);
  const [errorTrace, setErrorTrace] = useState("");

  const [showEditDatos, setShowEditDatos] = useState(false);
  const [showEditTrat, setShowEditTrat] = useState(false);
  const [formDatos, setFormDatos] = useState({
    estadoSalud: "",
    corralId: "",
    proxTratamiento: "",
  });
  const [formFecha, setFormFecha] = useState({ proxTratamiento: "" });

  const fetchTrace = async (q) => {
    const queryValue = (q ?? query).trim();
    if (!queryValue) return;

    setLoadingTrace(true);
    setErrorTrace("");
    try {
      let animalRes;
      if (!isNaN(queryValue)) {
        animalRes = await getAnimalById(queryValue);
      } else {
        animalRes = await getAnimalByCaravana(queryValue);
      }

      const animal = animalRes.data;
      if (!animal || !animal.animalId) {
        throw new Error("Animal no encontrado");
      }

      const [pesajesRes, tratamientosRes] = await Promise.all([
        getPesajesByAnimal(animal.animalId),
        getTratamientosByAnimal(animal.animalId),
      ]);

      const data = {
        ...animal,
        pesajes: pesajesRes.data || [],
        tratamientos: tratamientosRes.data || [],
      };

      setTraceResult(data);
    } catch (err) {
      console.error("Error al obtener trazabilidad:", err);
      setErrorTrace("Animal no encontrado o error al cargar datos");
      setTraceResult(null);
    } finally {
      setLoadingTrace(false);
    }
  };

  const handleTrace = async (e) => {
    e.preventDefault();
    await fetchTrace(query);
  };

  const openEditDatos = () => {
    setFormDatos({
      estadoSalud: traceResult.estadoSalud,
      corralId: traceResult.corralId,
      proxTratamiento: traceResult.proxTratamiento || "",
    });
    setShowEditDatos(true);
  };

  const openEditTrat = () => {
    setFormFecha({ proxTratamiento: traceResult.proxTratamiento || "" });
    setShowEditTrat(true);
  };

  const handleUpdateDatos = async () => {
    try {
      await updateAnimal(traceResult.animalId, formDatos);
      alert("Datos actualizados correctamente");
      setShowEditDatos(false);
      await fetchTrace();
    } catch {
      alert("Error al actualizar datos");
    }
  };

  const handleUpdateTrat = async () => {
    try {
      await updateFechaTrat(traceResult.animalId, formFecha);
      alert("Próximo tratamiento actualizado");
      setShowEditTrat(false);
      await fetchTrace();
    } catch {
      alert("Error al actualizar tratamiento");
    }
  };

  // PESAJE
  const [newPeso, setNewPeso] = useState({
    peso: "",
    animalIdPesaje: "",
    caravana: "",
  });
  const [msgPesaje, setMsgPesaje] = useState("");
  const [errorPesaje, setErrorPesaje] = useState("");

  const handlePesaje = async (e) => {
    e.preventDefault();
    setMsgPesaje("");
    setErrorPesaje("");

    const body = {
      peso: Number.parseFloat(newPeso.peso),
      animalId: newPeso.animalIdPesaje
        ? Number.parseInt(newPeso.animalIdPesaje, 10)
        : null,
      caravana: newPeso.caravana || null,
    };

    try {
      if (!Number.isFinite(body.peso) || body.peso <= 0) {
        setErrorPesaje("Por favor ingrese un pesaje válido");
        return;
      }
      await createPesaje(body);
      setMsgPesaje("Pesaje realizado!");
      setNewPeso({
        peso: "",
        animalIdPesaje: "",
        caravana: "",
      });
    } catch (e) {
      console.error(e);
      setErrorPesaje(
        "Error al pesar el animal. Por favor, revise si el número del ID o la caravana son correctos"
      );
    }
  };


  const [medicamentos, setMedicamentos] = useState([]);
  useEffect(() => {
    if (activeTab === "tratar") {
      (async () => {
        try {
          const { data } = await getInsumoByCategoria("MEDICAMENTO");
          setMedicamentos(data);
        } catch (e) {
          console.error("No se pudieron cargar los medicamentos", e);
          setMedicamentos([]);
        }
      })();
    }
  }, [activeTab]);


  const [treatments, setTreatments] = useState([]);
  const [treatmentForm, setTreatmentForm] = useState({
    animalId: "",
    caravana: "",
    medicamentoId: "",
    dosis: "",
    responsable: "",
    descripcion: "",
  });
  const [msgTrat, setMsgTrat] = useState("");
  const [errorTrat, setErrorTrat] = useState("");

  const handleRegisterTreatment = async (e) => {
    e.preventDefault();
    setMsgTrat("");
    setErrorTrat("");

    const animalIdNum = treatmentForm.animalId
      ? Number(treatmentForm.animalId)
      : null;
    const caravanaStr = treatmentForm.caravana?.trim() || null;
    const medicamentoIdNum = treatmentForm.medicamentoId
      ? Number(treatmentForm.medicamentoId)
      : null;
    const dosisNum = treatmentForm.dosis ? Number(treatmentForm.dosis) : null;

    if (!animalIdNum && !caravanaStr) {
      setErrorTrat("Ingresá ID o Caravana del animal.");
      return;
    }
    if (!medicamentoIdNum) {
      setErrorTrat("Seleccioná un medicamento.");
      return;
    }
    if (!dosisNum || Number.isNaN(dosisNum) || dosisNum <= 0) {
      setErrorTrat("Ingresá una dosis válida (> 0).");
      return;
    }

    const payload = {
      animalId: animalIdNum,
      caravana: caravanaStr,
      medicamentoId: medicamentoIdNum,
      dosis: dosisNum,
      responsable: treatmentForm.responsable || undefined,
      descripcion: treatmentForm.descripcion || undefined,
    };

    try {
      await create(payload);

      if (animalIdNum) {
        const res = await getTratamientosByAnimal(animalIdNum);
        setTreatments(res.data);
      }

      setMsgTrat("Tratamiento registrado y stock actualizado.");
      setTreatmentForm({
        animalId: "",
        caravana: "",
        medicamentoId: "",
        dosis: "",
        responsable: "",
        descripcion: "",
      });
    } catch (err) {
      console.error(err);
      setErrorTrat(
        err?.response?.data?.message || "Error al registrar tratamiento"
      );
    }
  };

  // ───────────────── LISTAR TRATAMIENTOS
  const [listTrat, setListTrat] = useState([]);
  const [loadingListTrat, setLoadingListTrat] = useState(false);
  const [errorListTrat, setErrorListTrat] = useState("");

  const [animalId, setAnimalId] = useState("");
  const [caravana, setCaravana] = useState("");
  const [fechaDesde, setFechaDesde] = useState("");
  const [fechaHasta, setFechaHasta] = useState("");

  useEffect(() => {
    if (activeTab === "tratamientos") {
      setLoadingListTrat(true);
      getAll()
        .then(({ data }) => {
          setListTrat(data);
          setErrorListTrat("");
        })
        .catch(() => {
          setErrorListTrat("Error al cargar los tratamientos");
        })
        .finally(() => {
          setLoadingListTrat(false);
        });
    }
  }, [activeTab]);

  const handleBuscar = async () => {
    setLoadingListTrat(true);
    setErrorListTrat("");

    try {
      let response;

      if (animalId) {
        response = await getByAnimalId(animalId);
      } else if (caravana) {
        response = await getByCaravana(caravana);
      } else if (fechaDesde && fechaHasta) {
        response = await getByFechaBetween(fechaDesde, fechaHasta);
      } else {
        response = await getAll();
      }

      setListTrat(response.data);
    } catch (error) {
      setErrorListTrat("Error al buscar tratamientos");
    } finally {
      setLoadingListTrat(false);
    }
  };

  useEffect(() => {
    if (activeTab === "tratamientos") {
      handleBuscar();
    }
  }, [activeTab, animalId, caravana, fechaDesde, fechaHasta]);

  return (
    <div className="animal-page">
      <TabBar tabs={tabs} activeKey={activeTab} onSelect={setActiveTab} />

      <div className="animal-content">
        {activeTab === "ingreso" && (
          <form className="form-ingreso" onSubmit={handleIngreso}>
            <h2>Ingresar Animal</h2>
            <h6 className="h6">Caravana</h6>
            <input
              placeholder="Caravana"
              value={newAnimal.caravana}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, caravana: e.target.value })
              }
            />
            <h6 className="h6">Raza</h6>
            <input
              placeholder="Raza"
              value={newAnimal.raza}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, raza: e.target.value })
              }
            />
            <h6 className="h6">Sexo</h6>
            <select
              value={String(newAnimal.sexo)}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, sexo: e.target.value === "true" })
              }
            >
              <option value="true">Macho</option>
              <option value="false">Hembra</option>
            </select>

            <h6 className="h6">Peso</h6>
            <input
              type="number"
              placeholder="Peso Actual"
              value={newAnimal.pesoActual}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, pesoActual: e.target.value })
              }
            />

            <h6 className="h6">Estado Salud</h6>
            <input
              placeholder="Estado Salud"
              value={newAnimal.estadoSalud}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, estadoSalud: e.target.value })
              }
            />

            <h6 className="h6">Número de Corral</h6>
            <input
              type="number"
              placeholder="ID Corral"
              value={newAnimal.corralId}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, corralId: e.target.value })
              }
            />

            <h6 className="h6">Fecha de Nacimiento</h6>
            <input
              type="Date"
              placeholder="Fecha de Nacimiento"
              value={newAnimal.fechaNacimiento}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, fechaNacimiento: e.target.value })
              }
            />

            <h6 className="h6">Próximo Tratamiento</h6>
            <input
              type="Date"
              placeholder="Próx. Tratamiento"
              value={newAnimal.proxTratamiento}
              onChange={(e) =>
                setNewAnimal({ ...newAnimal, proxTratamiento: e.target.value })
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
                    <th>Edad </th>
                    <th>Peso</th>
                    <th>Sexo</th>
                    <th>Salud</th>
                    <th>Estado</th>
                    <th>Corral</th>
                    <th>Fecha Ingreso</th>
                  </tr>
                </thead>
                <tbody>
                  {list.map((a) => (
                    <tr key={a.animalId}>
                      <td>{a.animalId}</td>
                      <td>{a.caravana}</td>
                      <td>{a.raza}</td>
                      <td>{a.edadMeses + " meses"}</td>
                      <td>{a.pesoActual}</td>
                      <td>{a.sexo ? "Macho" : "Hembra"}</td>
                      <td>{a.estadoSalud}</td>
                      <td>{a.estado}</td>
                      <td>{a.corralId}</td>
                      <td>{a.fechaIngreso}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}
      
        {activeTab === "trazabilidad" && (
          <>
            <div className="trace-card">
              <h2>Trazabilidad del Ganado</h2>

              {traceResult && (
                <div className="trace-actions">
                  <button className="btn-edit" onClick={openEditDatos}>
                    ✏️ Actualizar Datos
                  </button>
                  <button className="btn-prox" onClick={openEditTrat}>
                    🩺 Establecer Próx. Tratamiento
                  </button>
                </div>
              )}

              <form className="trace-form" onSubmit={handleTrace}>
                <input
                  type="text"
                  className="search-input"
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
                <div className="trace-detail">
                  <h3>Animal Nº Caravana: {traceResult.caravana}</h3>


                  <table className="detail-table">
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Raza</th>
                        <th>Edad </th>
                        <th>Peso Inicial</th>
                        <th>Peso Actual</th>
                        <th>Sexo</th>
                        <th>Salud</th>
                        <th>Estado</th>
                        <th>Corral</th>
                        <th>Ingreso</th>
                        <th>Prox. Tratamiento</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td>{traceResult.animalId}</td>
                        <td>{traceResult.raza}</td>
                        <td>{traceResult.edadMeses}{" meses"}</td>
                        <td>{traceResult.pesoInicial}{" kg"}</td>
                        <td>{traceResult.pesoActual}{" kg"}</td>
                        <td>{traceResult.sexo ? "Macho" : "Hembra"}</td>
                        <td>{traceResult.estadoSalud}</td>
                        <td>{traceResult.estado}</td>
                        <td>{traceResult.corralId}</td>
                        <td>{fmtYMD(traceResult.fechaIngreso)}</td>
                        <td>{fmtYMD(traceResult.proxTratamiento)}</td>
                      </tr>
                    </tbody>
                  </table>

                  <div className="history-section">
                    <h4>Historial de Pesajes</h4>
                    <div className="scroll-x">
                      <table className="history-table condensed">
                        <thead>
                          <tr>
                            <th>Fecha</th>
                            {traceResult.pesajes?.map((p) => (
                              <th key={p.pesajeId}>{fmtYMD(p.fecha)}</th>
                            ))}
                          </tr>
                        </thead>
                        <tbody>
                          <tr>
                            <td>Peso (kg)</td>
                            {traceResult.pesajes?.map((p) => (
                              <td key={p.pesajeId}>{p.peso}</td>
                            ))}
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>
                  <div className="history-section">
                    <h4>Historial de Tratamientos</h4>
                    <div className="scroll-x">
                      <table className="history-table condensed">
                        <thead>
                          <tr>
                            <th>Fecha</th>
                            {traceResult.tratamientos?.map((t) => (
                              <th key={t.registroTratamientoId}>
                                {fmtYMD(t.fecha)}
                              </th>
                            ))}
                          </tr>
                        </thead>
                        <tbody>
                          <tr>
                            <td>Medicamento</td>
                            {traceResult.tratamientos?.map((t) => (
                              <td key={`dos-${t.registroTratamientoId}`}>
                                {t.nombreInsumo}
                              </td>
                            ))}
                          </tr>
                          <tr>
                            <td>Cantidad</td>
                            {traceResult.tratamientos?.map((t) => (
                              <td key={`dos-${t.registroTratamientoId}`}>
                                {t.dosis}
                              </td>
                            ))}
                          </tr>
                          <tr>
                            <td>Responsable</td>
                            {traceResult.tratamientos?.map((t) => (
                              <td key={`dos-${t.registroTratamientoId}`}>
                                {t.responsable}
                              </td>
                            ))}
                          </tr>
                           <tr>
                            <td>Motivo</td>
                            {traceResult.tratamientos?.map((t) => (
                              <td key={`dos-${t.registroTratamientoId}`}>
                                {t.descripcion}
                              </td>
                            ))}
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              )}
            </div>

            
            {showEditDatos && (
              <div className="modal">
                <div className="modal-content">
                  <h3>Actualizar Datos del Animal</h3>
                  <label>Estado de Salud:</label>
                  <input
                    type="text"
                    value={formDatos.estadoSalud}
                    onChange={(e) =>
                      setFormDatos({
                        ...formDatos,
                        estadoSalud: e.target.value,
                      })
                    }
                  />
                  <label>ID de Corral:</label>
                  <input
                    type="number"
                    value={formDatos.corralId}
                    onChange={(e) =>
                      setFormDatos({ ...formDatos, corralId: e.target.value })
                    }
                  />
                  <label>Próximo Tratamiento:</label>
                  <input
                    type="date"
                    value={formDatos.proxTratamiento || ""}
                    onChange={(e) =>
                      setFormDatos({
                        ...formDatos,
                        proxTratamiento: e.target.value,
                      })
                    }
                  />
                  <div className="modal-buttons">
                    <button onClick={handleUpdateDatos}>Guardar</button>
                    <button onClick={() => setShowEditDatos(false)}>
                      Cancelar
                    </button>
                  </div>
                </div>
              </div>
            )}

            {showEditTrat && (
              <div className="modal">
                <div className="modal-content">
                  <h3>Establecer Próximo Tratamiento</h3>
                  <label>Fecha:</label>
                  <input
                    type="date"
                    value={formFecha.proxTratamiento || ""}
                    onChange={(e) =>
                      setFormFecha({ proxTratamiento: e.target.value })
                    }
                  />
                  <div className="modal-buttons">
                    <button onClick={handleUpdateTrat}>Guardar</button>
                    <button onClick={() => setShowEditTrat(false)}>
                      Cancelar
                    </button>
                  </div>
                </div>
              </div>
            )}
          </>
        )}

        
        {activeTab === "pesaje" && (
          <form className="form-pesaje" onSubmit={handlePesaje}>
            <h2>Pesar Animal</h2>
            <h5 className="h5">
              Por favor, ingrese el ID o la Caravana del animal
            </h5>

            <h6 className="h6">ID</h6>
            <input
              placeholder="ID"
              type="number"
              value={newPeso.animalIdPesaje}
              onChange={(e) =>
                setNewPeso({ ...newPeso, animalIdPesaje: e.target.value })
              }
            />

            <h6 className="h6">Caravana</h6>
            <input
              placeholder="Caravana"
              type="text"
              value={newPeso.caravana}
              onChange={(e) =>
                setNewPeso({ ...newPeso, caravana: e.target.value })
              }
            />

            <h6 className="h6">Nuevo peso</h6>
            <input
              type="number"
              placeholder="Nuevo Peso"
              value={newPeso.peso}
              onChange={(e) => setNewPeso({ ...newPeso, peso: e.target.value })}
            />
            <button type="submit">Registrar Pesaje</button>
            {msgPesaje && <p className="msg">{msgPesaje}</p>}
            {errorPesaje && <p className="error">{errorPesaje}</p>}
          </form>
        )}
        {activeTab === "tratar" && (
          <form className="form-tratamiento" onSubmit={handleRegisterTreatment}>
            <h2>Tratar Animal</h2>
            <h5 className="h5">Ingresá el ID o la Caravana del animal</h5>

            <div className="grid-2">
              <div>
                <h6 className="h6">ID</h6>
                <input
                  type="number"
                  placeholder="ID"
                  value={treatmentForm.animalId}
                  onChange={(e) =>
                    setTreatmentForm({
                      ...treatmentForm,
                      animalId: e.target.value,
                    })
                  }
                />
              </div>

              <div>
                <h6 className="h6">Caravana</h6>
                <input
                  type="text"
                  placeholder="Caravana"
                  value={treatmentForm.caravana}
                  onChange={(e) =>
                    setTreatmentForm({
                      ...treatmentForm,
                      caravana: e.target.value,
                    })
                  }
                />
              </div>
            </div>

            <h6 className="h6">Medicamento</h6>
            <select
              value={treatmentForm.medicamentoId}
              onChange={(e) =>
                setTreatmentForm({
                  ...treatmentForm,
                  medicamentoId: e.target.value,
                })
              }
            >
              <option value="">Seleccionar</option>
              {medicamentos.map((m) => (
                <option key={m.insumoId} value={m.insumoId}>
                  {m.nombre}
                  {m.unidadMedida ? ` — ${m.cantidad} ${m.unidadMedida}` : ""}
                </option>
              ))}
            </select>

            <h6 className="h6">Dosis</h6>
            <input
              type="number"
              min="0"
              step="0.01"
              placeholder="Dosis"
              value={treatmentForm.dosis}
              onChange={(e) =>
                setTreatmentForm({ ...treatmentForm, dosis: e.target.value })
              }
            />

            <h6 className="h6">Responsable</h6>
            <input
              placeholder="Responsable"
              value={treatmentForm.responsable}
              onChange={(e) =>
                setTreatmentForm({
                  ...treatmentForm,
                  responsable: e.target.value,
                })
              }
            />

            <h6 className="h6">Descripción</h6>
            <input
              type="text"
              placeholder="Descripción"
              value={treatmentForm.descripcion}
              onChange={(e) =>
                setTreatmentForm({
                  ...treatmentForm,
                  descripcion: e.target.value,
                })
              }
            />

            <button type="submit">Registrar Tratamiento</button>
            {msgTrat && <p className="msg">{msgTrat}</p>}
            {errorTrat && <p className="error">{errorTrat}</p>}
          </form>
        )}

      
        {activeTab === "tratamientos" && (
          <div className="treatments-container">
            <h2>Listado de Tratamientos</h2>
            <div className="treatments-actions">
              <div className="input-group">
                <h6>Buscar animal por ID</h6>
                <input
                  type="text"
                  placeholder="🔍 Buscar"
                  value={animalId}
                  onChange={(e) => setAnimalId(e.target.value)}
                />
              </div>
              <div className="input-group">
                <h6>Buscar animal por caravana</h6>
                <input
                  type="text"
                  placeholder="🔍 Buscar"
                  value={caravana}
                  onChange={(e) => setCaravana(e.target.value)}
                />
              </div>
              <div className="input-group">
                <h6>Desde</h6>
                <input
                  type="date"
                  value={fechaDesde}
                  onChange={(e) => setFechaDesde(e.target.value)}
                />
              </div>
              <div className="input-group">
                <h6>Hasta</h6>
                <input
                  type="date"
                  value={fechaHasta}
                  onChange={(e) => setFechaHasta(e.target.value)}
                />
              </div>
              <button
                onClick={() => {
                  setAnimalId("");
                  setCaravana("");
                  setFechaDesde("");
                  setFechaHasta("");
                  handleBuscar();
                }}
              >
                Limpiar filtros
              </button>
            </div>

            {loadingListTrat ? (
              <p>Cargando…</p>
            ) : errorListTrat ? (
              <p className="error">{errorListTrat}</p>
            ) : (
              <table className="treatments-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Fecha</th>
                    <th>Dosis</th>
                    <th>Insumo</th>
                    <th>Animal ID</th>
                    <th>Caravana</th>
                    <th>Responsable</th>
                    <th>Descripción</th>
                  </tr>
                </thead>
                <tbody>
                  {listTrat.map((t) => (
                    <tr key={t.registroTratamientoId}>
                      <td>{t.registroTratamientoId}</td>
                      <td>{t.fecha}</td>
                      <td>{t.dosis}</td>
                      <td>{t.nombreInsumo}</td>
                      <td>{t.animalId}</td>
                      <td>{t.caravana}</td>
                      <td>{t.responsable}</td>
                      <td>{t.descripcion}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
