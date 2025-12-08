import React, { useEffect, useState, useMemo } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import {
  cotizarVenta,
  createVenta,
  getVentasHistorial,
} from "../../services/ventaService";
import {
  getAllClientes,
  createCliente,
  updateCliente,
} from "../../services/clienteService";
import {
  getRemates, 
} from "../../services/rematesService";
import {
  getAnimalById,
  getAnimalByCaravana,
} from "../../services/animalService";
import "./VentaPage.css";

export default function VentaPage() {
  
  const tabs = [
    { key: "vender", label: "Vender Ganado" },
    { key: "historialVentas", label: "Historial Ventas" },
    { key: "remates", label: "Remates" },
    {
      key: "clientes",
      label: "Clientes",
      dropdown: [
        { key: "registrarCliente", label: "Registrar" },
        { key: "listarClientes", label: "Listar" },
      ],
    },
  ];
  const [activeTab, setActiveTab] = useState("vender");

  // ---- Clientes
  const [clients, setClients] = useState([]);
  const [loadingClients, setLoadingClients] = useState(false);
  const [errClients, setErrClients] = useState("");

  useEffect(() => {
    const loadClients = async () => {
      try {
        setLoadingClients(true);
        setErrClients("");
        const { data } = await getAllClientes();
        const arr = Array.isArray(data) ? data : data?.items || [];
        const normalized = arr.map((c) => ({
          id: c.id ?? c.clienteId ?? c.cliente_id ?? c.clienteid,
          nombre: c.nombre,
          apellido: c.apellido,
          cuit: c.cuit,
          telefono: c.telefono,
          email: c.email,
          clienteId: c.clienteId ?? c.id,
        }));
        setClients(normalized);
      } catch {
        setErrClients("No se pudieron cargar los clientes");
      } finally {
        setLoadingClients(false);
      }
    };
    loadClients();
  }, []);


  const [saleForm, setSaleForm] = useState({
    animals: [{ animalId: "", caravana: "" }],
    clienteId: "",
    precio: "",
    fecha: new Date().toISOString().slice(0, 10),
  });
  const [msgSale, setMsgSale] = useState("");
  const [errSale, setErrSale] = useState("");
  const [cotizando, setCotizando] = useState(false);
  const [registrando, setRegistrando] = useState(false);

  const addAnimalLine = () => {
    setSaleForm((prev) => ({
      ...prev,
      animals: [...prev.animals, { animalId: "", caravana: "" }],
    }));
  };
  const removeAnimalLine = (i) => {
    setSaleForm((prev) => {
      const arr = prev.animals.slice();
      arr.splice(i, 1);
      return { ...prev, animals: arr };
    });
  };

  const resolveAnimalLine = async (i) => {
    const line = saleForm.animals[i];
    try {
      if (line.animalId && !line.caravana) {
        const { data } = await getAnimalById(parseInt(line.animalId, 10));
        setSaleForm((prev) => {
          const copy = [...prev.animals];
          copy[i].caravana = data.caravana;
          return { ...prev, animals: copy };
        });
      } else if (!line.animalId && line.caravana) {
        const { data } = await getAnimalByCaravana(line.caravana);
        setSaleForm((prev) => {
          const copy = [...prev.animals];
          copy[i].animalId = data.animalId;
          return { ...prev, animals: copy };
        });
      }
    } catch {
      setErrSale("No se pudo resolver el animal con los datos ingresados.");
    }
  };

  const handleAnimalChange = (i, field, value) => {
    setSaleForm((prev) => {
      const arr = prev.animals.slice();
      arr[i][field] = value;
      return { ...prev, animals: arr };
    });
  };

  const handleCotizar = async () => {
    setMsgSale("");
    setErrSale("");

    const animalIds = saleForm.animals
      .map((a) => (a.animalId ? parseInt(a.animalId, 10) : null))
      .filter(Boolean);

    const caravanas = saleForm.animals
      .map((a) => a.caravana?.trim())
      .filter(Boolean);

    if (!animalIds.length && !caravanas.length) {
      setErrSale("Cargá al menos un ID o una Caravana para cotizar.");
      return;
    }

    try {
      setCotizando(true);
      const { data } = await cotizarVenta({
        animalIds,
        caravanas,
        fecha: saleForm.fecha,
      });
      const total = data?.total ?? data?.precioTotal ?? null;
      if (total == null) {
        setErrSale("La cotización no devolvió un total reconocible.");
        return;
      }
      setSaleForm((prev) => ({ ...prev, precio: String(total) }));
      setMsgSale("Cotización actualizada.");
    } catch {
      setErrSale("Error al cotizar el lote.");
    } finally {
      setCotizando(false);
    }
  };


  const [sales, setSales] = useState([]);
  const [loadingSales, setLoadingSales] = useState(false);
  const [errSales, setErrSales] = useState("");


  const [clienteQuery, setClienteQuery] = useState("");
  const [clienteIdFilter, setClienteIdFilter] = useState(null);
  const [comboOpen, setComboOpen] = useState(false);
  const [comboIdx, setComboIdx] = useState(-1);

  const nombreCompleto = (c) => `${c.nombre} ${c.apellido}`.trim();
  const filteredClients = clients.filter((c) =>
    nombreCompleto(c).toLowerCase().includes(clienteQuery.toLowerCase())
  );

  const selectClient = (c) => {
    setClienteQuery(nombreCompleto(c));
    setClienteIdFilter(c.id);
    setComboOpen(false);
    setComboIdx(-1);
  };

  const clearClientFilter = () => {
    setClienteQuery("");
    setClienteIdFilter(null);
    setComboOpen(false);
    setComboIdx(-1);
  };

  useEffect(() => {
    if (activeTab !== "historialVentas") return;
    const loadSales = async () => {
      try {
        setLoadingSales(true);
        setErrSales("");
        const { data } = await getVentasHistorial(clienteIdFilter ?? undefined);
        setSales(Array.isArray(data) ? data : []);
      } catch {
        setSales([]);
        setErrSales("No se pudo cargar el historial de ventas.");
      } finally {
        setLoadingSales(false);
      }
    };
    loadSales();
  }, [activeTab, clienteIdFilter]);

  const handleSale = async (e) => {
    e.preventDefault();
    setMsgSale("");
    setErrSale("");

    if (saleForm.animals.some((a) => !a.animalId && !a.caravana)) {
      setErrSale("Cada animal debe tener ID o Caravana.");
      return;
    }
    if (!saleForm.clienteId || !saleForm.precio || !saleForm.fecha) {
      setErrSale("Falta cliente, precio o fecha.");
      return;
    }

    const payload = {
      animalIds: saleForm.animals
        .map((a) => (a.animalId ? parseInt(a.animalId, 10) : null))
        .filter(Boolean),
      caravanas: saleForm.animals
        .map((a) => a.caravana?.trim())
        .filter(Boolean),
      clienteId: parseInt(saleForm.clienteId, 10),
      fecha: saleForm.fecha,
      precioTotal: parseFloat(saleForm.precio),
      descripcion: undefined,
    };

    try {
      setRegistrando(true);
      const { data } = await createVenta(payload);

      setSales((prev) => [
        ...prev,
        {
          id: data?.ventaId ?? (prev.at(-1)?.id ?? 0) + 1,
          animals: saleForm.animals.map((a) => ({
            animalId: +a.animalId || 0,
            caravana: a.caravana,
          })),
          clienteId: data?.clienteId ?? payload.clienteId,
          clienteNombre: data?.clienteNombre ?? undefined,
          precio: parseFloat(saleForm.precio),
          fecha: saleForm.fecha,
          detalles: data?.detalles,
          total: data?.total,
          ventaId: data?.ventaId,
        },
      ]);

      setMsgSale("Venta registrada con éxito.");
      setSaleForm({
        animals: [{ animalId: "", caravana: "" }],
        clienteId: "",
        precio: "",
        fecha: new Date().toISOString().slice(0, 10),
      });
    } catch {
      setErrSale("No se pudo registrar la venta.");
    } finally {
      setRegistrando(false);
    }
  };


  const [newClient, setNewClient] = useState({
    nombre: "",
    apellido: "",
    cuit: "",
    telefono: "",
    email: "",
  });
  const [msgNew, setMsgNew] = useState("");
  const [errNew, setErrNew] = useState("");
  const [savingClient, setSavingClient] = useState(false);

  const handleNewClient = async (e) => {
    e.preventDefault();
    setMsgNew("");
    setErrNew("");

    const { nombre, apellido, cuit, telefono, email } = newClient;
    if (!nombre || !apellido || !cuit || !telefono || !email) {
      setErrNew("Completa todos los campos");
      return;
    }

    try {
      setSavingClient(true);
      const { data } = await createCliente(newClient);
      const created = {
        id: data.id ?? data.clienteId ?? data.cliente_id,
        nombre: data.nombre,
        apellido: data.apellido,
        cuit: data.cuit,
        telefono: data.telefono,
        email: data.email,
        clienteId: data.clienteId ?? data.id,
      };
      setClients((prev) => [...prev, created]);
      setMsgNew("Cliente registrado");
      setNewClient({
        nombre: "",
        apellido: "",
        cuit: "",
        telefono: "",
        email: "",
      });
    } catch {
      setErrNew("No se pudo crear el cliente.");
    } finally {
      setSavingClient(false);
    }
  };

  const [editingId, setEditingId] = useState(null);
  const [editData, setEditData] = useState({});
  const startEdit = (c) => {
    setEditingId(c.id);
    setEditData({ ...c });
  };
  const cancelEdit = () => {
    setEditingId(null);
    setEditData({});
  };

  const saveEdit = async () => {
    try {
      const { data } = await updateCliente(editingId, {
        nombre: editData.nombre,
        apellido: editData.apellido,
        cuit: editData.cuit,
        telefono: editData.telefono,
        email: editData.email,
      });
      const updated = {
        id: data.id ?? data.clienteId ?? data.cliente_id,
        nombre: data.nombre,
        apellido: data.apellido,
        cuit: data.cuit,
        telefono: data.telefono,
        email: data.email,
        clienteId: data.clienteId ?? data.id,
      };
      setClients((prev) => prev.map((c) => (c.id === editingId ? updated : c)));
    } catch {
      setClients((prev) =>
        prev.map((c) => (c.id === editingId ? { ...editData } : c))
      );
    } finally {
      setEditingId(null);
      setEditData({});
    }
  };

  // REMATES
  const [remates, setRemates] = useState([]);
  const [loadingRem, setLoadingRem] = useState(false);
  const [errRem, setErrRem] = useState("");

  // filtros
  const [qRem, setQRem] = useState("");
  const [provRem, setProvRem] = useState("");
  const [modoRem, setModoRem] = useState("");

  // paginado cliente
  const [pageRem, setPageRem] = useState(1);
  const pageSizeRem = 15;

  useEffect(() => {
    if (activeTab !== "remates") return;
    (async () => {
      try {
        setLoadingRem(true);
        setErrRem("");
        const data = await getRemates({
          q: qRem || undefined,
          provincia: provRem || undefined,
          modo: modoRem || undefined,
        });
        const arr = Array.isArray(data) ? data : data?.items || [];
        setRemates(arr);
        setPageRem(1);
      } catch {
        setErrRem(
          "No se pudo actualizar la cartelera. Si hay conexión, intentá nuevamente."
        );
        setRemates([]);
      } finally {
        setLoadingRem(false);
      }
    })();
  }, [activeTab, qRem, provRem, modoRem]);

  const pageCountRem = Math.max(1, Math.ceil(remates.length / pageSizeRem));
  const pageDataRem = useMemo(() => {
    const start = (pageRem - 1) * pageSizeRem;
    return remates.slice(start, start + pageSizeRem);
  }, [remates, pageRem]);

 const normalizarModo = (m) => {
  if (!m) return "–";
  const s = String(m).toLowerCase();
  if (s.includes("físico") || s.includes("fisico")) return "Físico";
  if (s.includes("internet") || s.includes("online") || s.includes("stream"))
    return "Internet / Streaming";
  return m;
};

const fmtFechaHora = (iso) => {
  if (!iso) return "-";
  const d = new Date(iso);
  return isNaN(d) ? String(iso) : d.toLocaleString("es-AR");
};
  

  return (
    <div className="venta-page">
      <TabBar tabs={tabs} activeKey={activeTab} onSelect={setActiveTab} />

      <div className="venta-content">

        {activeTab === "vender" && (
          <form className="form-venta" onSubmit={handleSale}>
            <h2>Vender Ganado</h2>

            {saleForm.animals.map((a, i) => (
              <div key={i} className="animal-line">
                <input
                  type="number"
                  placeholder="ID Animal"
                  value={a.animalId}
                  onChange={(e) =>
                    handleAnimalChange(i, "animalId", e.target.value)
                  }
                  onBlur={() => resolveAnimalLine(i)}
                  min="1"
                  step="1"
                />
                <input
                  placeholder="Caravana"
                  value={a.caravana}
                  onChange={(e) =>
                    handleAnimalChange(i, "caravana", e.target.value)
                  }
                  onBlur={() => resolveAnimalLine(i)}
                />
                {i > 0 && (
                  <button
                    type="button"
                    className="btn-remove"
                    onClick={() => removeAnimalLine(i)}
                  >
                    ✕
                  </button>
                )}
              </div>
            ))}

            <div className="row-actions">
              <button type="button" className="btn-add" onClick={addAnimalLine}>
                + Agregar Animal
              </button>
              <button
                type="button"
                className="btn-secondary"
                onClick={handleCotizar}
                disabled={cotizando}
              >
                {cotizando ? "Cotizando..." : "Cotizar"}
              </button>
            </div>

            <select
              value={saleForm.clienteId}
              onChange={(e) =>
                setSaleForm({ ...saleForm, clienteId: e.target.value })
              }
              disabled={loadingClients}
            >
              <option value="">
                {loadingClients ? "Cargando clientes..." : "Selecciona Cliente"}
              </option>
              {errClients && <option value="">{errClients}</option>}
              {clients.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.nombre} {c.apellido}
                </option>
              ))}
            </select>

            <input
              type="number"
              placeholder="Precio total ($)"
              value={saleForm.precio}
              onChange={(e) =>
                setSaleForm({ ...saleForm, precio: e.target.value })
              }
            />
            <input
              type="date"
              value={saleForm.fecha}
              onChange={(e) =>
                setSaleForm({ ...saleForm, fecha: e.target.value })
              }
            />

            <button type="submit" disabled={registrando}>
              {registrando ? "Registrando..." : "Registrar Venta"}
            </button>

            {msgSale && <p className="msg">{msgSale}</p>}
            {errSale && <p className="error">{errSale}</p>}
          </form>
        )}
        {activeTab === "historialVentas" && (
          <div className="table-container">
            <div className="table-header">
              <h2>Historial de Ventas</h2>

              <div className="filters">
                <div
                  className={`combo compact ${comboOpen ? "open" : ""}`}
                  onBlur={(e) => {
                    if (!e.currentTarget.contains(e.relatedTarget))
                      setComboOpen(false);
                  }}
                >
                  <span className="combo-icon" aria-hidden>
                    🔎
                  </span>

                  <input
                    className="combo-input"
                    placeholder="Filtrar por cliente…"
                    value={clienteQuery}
                    onFocus={() => setComboOpen(true)}
                    onChange={(e) => {
                      setClienteQuery(e.target.value);
                      setComboOpen(true);
                      setComboIdx(-1);
                    }}
                    onKeyDown={(e) => {
                      if (!comboOpen) return;
                      if (e.key === "ArrowDown") {
                        e.preventDefault();
                        setComboIdx((i) =>
                          Math.min(i + 1, filteredClients.length - 1)
                        );
                      } else if (e.key === "ArrowUp") {
                        e.preventDefault();
                        setComboIdx((i) => Math.max(i - 1, 0));
                      } else if (
                        e.key === "Enter" &&
                        filteredClients[comboIdx]
                      ) {
                        e.preventDefault();
                        selectClient(filteredClients[comboIdx]);
                      } else if (e.key === "Escape") {
                        setComboOpen(false);
                      }
                    }}
                  />

                  {clienteQuery && (
                    <button
                      type="button"
                      className="combo-clear"
                      onClick={clearClientFilter}
                      aria-label="Limpiar"
                    >
                      ✕
                    </button>
                  )}

                  {comboOpen && filteredClients.length > 0 && (
                    <ul className="combo-menu" role="listbox">
                      {filteredClients.slice(0, 8).map((c, idx) => (
                        <li
                          key={c.id}
                          role="option"
                          tabIndex={0}
                          className={`combo-item ${
                            idx === comboIdx ? "active" : ""
                          }`}
                          onMouseDown={(e) => e.preventDefault()}
                          onClick={() => selectClient(c)}
                          onMouseEnter={() => setComboIdx(idx)}
                        >
                          <span className="combo-item-name">
                            {nombreCompleto(c)}
                          </span>
                          <span className="combo-item-sub">ID {c.id}</span>
                        </li>
                      ))}
                    </ul>
                  )}
                </div>

                {clienteIdFilter && (
                  <button
                    type="button"
                    className="btn-secondary"
                    onClick={clearClientFilter}
                  >
                    Limpiar
                  </button>
                )}
              </div>
            </div>

            {loadingSales ? (
              <p style={{ padding: 12 }}>Cargando ventas…</p>
            ) : errSales ? (
              <p className="error">{errSales}</p>
            ) : (
              <table className="list-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Caravanas</th>
                    <th>Cliente</th>
                    <th>Precio</th>
                    <th>Fecha</th>
                  </tr>
                </thead>

                <tbody>
                  {sales.length === 0 ? (
                    <tr>
                      <td
                        colSpan={5}
                        style={{ textAlign: "center", padding: 16 }}
                      >
                        Sin ventas registradas
                      </td>
                    </tr>
                  ) : (
                    sales.map((s) => (
                      <tr key={s.ventaId}>
                        <td>{s.ventaId}</td>
                        <td>{s.caravanas || "–"}</td>
                        <td>{s.cliente || "Sin cliente"}</td>
                        <td>${Number(s.total || 0).toLocaleString("es-AR")}</td>
                        <td>{(s.fecha || "").toString().slice(0, 10)}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            )}
          </div>
        )}
      
        {activeTab === "remates" && (
          <div className="table-container">
            <div className="table-header">
              <h2>Próximos Remates</h2>

            </div>

            {loadingRem ? (
              <p style={{ padding: 12 }}>Cargando remates…</p>
            ) : errRem ? (
              <p className="error">{errRem}</p>
            ) : (
              <>
                <table className="list-table">
                  <thead>
                    <tr>
                      <th>Fecha/Hora</th>
                      <th>Socio</th>
                      <th>Ubicación</th>
                      <th>Cabezas</th>
                      <th>Modo</th>
                    </tr>
                  </thead>
                  <tbody>
                    {pageDataRem.length === 0 ? (
                      <tr>
                        <td
                          colSpan={6}
                          style={{ textAlign: "center", padding: 16 }}
                        >
                          Sin resultados
                        </td>
                      </tr>
                    ) : (
                      pageDataRem.map((r, i) => (
                        <tr key={i}>
                          <td>{fmtFechaHora(r.fechaHora)}</td>
                          <td>{r.socio || "–"}</td>

                          <td>
                            {[r.localidad, r.provincia]
                              .filter(Boolean)
                              .join(", ") || "–"}
                          </td>
                          <td style={{ textAlign: "right" }}>
                            {r.cabezas ?? "–"}
                          </td>
                          <td>{normalizarModo(r.modo)}</td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>

                <div className="pager">
                  <button
                    className="btn-secondary"
                    disabled={pageRem === 1}
                    onClick={() => setPageRem((p) => Math.max(1, p - 1))}
                  >
                    {"<"}
                  </button>
                  <span className="pager-info">
                    {pageRem} / {pageCountRem}
                  </span>
                  <button
                    className="btn-secondary"
                    disabled={pageRem === pageCountRem}
                    onClick={() =>
                      setPageRem((p) => Math.min(pageCountRem, p + 1))
                    }
                  >
                    {">"}
                  </button>
                </div>
              </>
            )}
          </div>
        )}
        {activeTab === "registrarCliente" && (
          <form className="form-cliente" onSubmit={handleNewClient}>
            <h2>Registrar Cliente</h2>
            <input
              placeholder="Nombre"
              value={newClient.nombre}
              onChange={(e) =>
                setNewClient({ ...newClient, nombre: e.target.value })
              }
            />
            <input
              placeholder="Apellido"
              value={newClient.apellido}
              onChange={(e) =>
                setNewClient({ ...newClient, apellido: e.target.value })
              }
            />
            <input
              placeholder="CUIT"
              value={newClient.cuit}
              onChange={(e) =>
                setNewClient({ ...newClient, cuit: e.target.value })
              }
            />
            <input
              placeholder="Teléfono"
              value={newClient.telefono}
              onChange={(e) =>
                setNewClient({ ...newClient, telefono: e.target.value })
              }
            />
            <input
              placeholder="Email"
              value={newClient.email}
              onChange={(e) =>
                setNewClient({ ...newClient, email: e.target.value })
              }
            />
            <button type="submit" disabled={savingClient}>
              {savingClient ? "Guardando..." : "Crear Cliente"}
            </button>
            {msgNew && <p className="msg">{msgNew}</p>}
            {errNew && <p className="error">{errNew}</p>}
          </form>
        )}
        {activeTab === "listarClientes" && (
          <div className="table-container">
            <h2>Listado de Clientes</h2>
            <table className="list-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Apellido</th>
                  <th>CUIT</th>
                  <th>Teléfono</th>
                  <th>Email</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {clients.map((c) => (
                  <tr key={c.id}>
                    {editingId === c.id ? (
                      <>
                        <td>{c.id}</td>
                        <td>
                          <input
                            value={editData.nombre}
                            onChange={(e) =>
                              setEditData({
                                ...editData,
                                nombre: e.target.value,
                              })
                            }
                          />
                        </td>
                        <td>
                          <input
                            value={editData.apellido}
                            onChange={(e) =>
                              setEditData({
                                ...editData,
                                apellido: e.target.value,
                              })
                            }
                          />
                        </td>
                        <td>
                          <input
                            value={editData.cuit}
                            onChange={(e) =>
                              setEditData({ ...editData, cuit: e.target.value })
                            }
                          />
                        </td>
                        <td>
                          <input
                            value={editData.telefono}
                            onChange={(e) =>
                              setEditData({
                                ...editData,
                                telefono: e.target.value,
                              })
                            }
                          />
                        </td>
                        <td>
                          <input
                            value={editData.email}
                            onChange={(e) =>
                              setEditData({
                                ...editData,
                                email: e.target.value,
                              })
                            }
                          />
                        </td>
                        <td>
                          <button className="btn-save" onClick={saveEdit}>
                            Guardar
                          </button>
                          <button className="btn-cancel" onClick={cancelEdit}>
                            Cancelar
                          </button>
                        </td>
                      </>
                    ) : (
                      <>
                        <td>{c.id}</td>
                        <td>{c.nombre}</td>
                        <td>{c.apellido}</td>
                        <td>{c.cuit}</td>
                        <td>{c.telefono}</td>
                        <td>{c.email}</td>
                        <td>
                          <button
                            className="btn-edit"
                            onClick={() => startEdit(c)}
                          >
                            Editar
                          </button>
                        </td>
                      </>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
            {errClients && <p className="error">{errClients}</p>}
          </div>
        )}
      </div>
    </div>
  );
}
