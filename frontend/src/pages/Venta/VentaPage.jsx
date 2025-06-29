import React, { useState } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import "./VentaPage.css";

export default function VentaPage() {
  const tabs = [
    { key: "vender",          label: "Vender Ganado" },
    { key: "historialVentas", label: "Historial Ventas" },
    { key: "remates",         label: "Remates" },
    {
      key: "clientes",
      label: "Clientes",
      dropdown: [
        { key: "registrarCliente", label: "Registrar" },
        { key: "listarClientes",   label: "Listar"   },
      ],
    },
  ];
  const [activeTab, setActiveTab] = useState("vender");

  // — Datos de ejemplo —
  const initialSales = [
    {
      id: 1,
      animals: [
        { animalId: 322, caravana: "#A103" },
        { animalId: 323, caravana: "#A108" },
      ],
      clienteId: 2,
      precio: 9800,
      fecha: "2025-06-28",
    },
    {
      id: 2,
      animals: [{ animalId: 325, caravana: "#A115" }],
      clienteId: 1,
      precio: 4800,
      fecha: "2025-06-20",
    },
  ];
  const initialClients = [
    {
      id: 1,
      nombre: "Juan",
      apellido: "Gutierrez",
      cuit: "20-12345678-9",
      telefono: "358-1234567",
      email: "juan@gmail.com",
    },
    {
      id: 2,
      nombre: "Empresa",
      apellido: "Agro S.A.",
      cuit: "30-87654321-0",
      telefono: "351-7654321",
      email: "ventas@agro.com",
    },
    {
      id: 3,
      nombre: "María",
      apellido: "Gómez",
      cuit: "27-11223344-5",
      telefono: "351-9988776",
      email: "maria@gmail.com",
    },
  ];
  const initialAuctions = [
    {
      id: 1,
      nombre: "Remate Ganadero Julio",
      fecha: "2025-07-10",
      ubicacion: "Establecimiento El Valle",
    },
    {
      id: 2,
      nombre: "Remate de Otoño",
      fecha: "2025-08-05",
      ubicacion: "Campo Los Robles",
    },
  ];

  const [sales,   setSales]   = useState(initialSales);
  const [clients, setClients] = useState(initialClients);
  const [auctions, setAuctions] = useState(initialAuctions);

  // — Form Venta (múltiples animales) —
  const [saleForm, setSaleForm] = useState({
    animals: [{ animalId: "", caravana: "" }],
    clienteId: "",
    precio: "",
    fecha: new Date().toISOString().slice(0, 10),
  });
  const [msgSale, setMsgSale] = useState("");
  const [errSale, setErrSale] = useState("");

  const addAnimalLine = () => {
    setSaleForm({
      ...saleForm,
      animals: [...saleForm.animals, { animalId: "", caravana: "" }],
    });
  };
  const removeAnimalLine = (i) => {
    const arr = saleForm.animals.slice();
    arr.splice(i, 1);
    setSaleForm({ ...saleForm, animals: arr });
  };
  const handleAnimalChange = (i, field, value) => {
    const arr = saleForm.animals.slice();
    arr[i][field] = value;
    setSaleForm({ ...saleForm, animals: arr });
  };

  const handleSale = (e) => {
    e.preventDefault();
    setMsgSale("");
    setErrSale("");
    const { animals, clienteId, precio, fecha } = saleForm;
    if (
      animals.some((a) => !a.animalId || !a.caravana) ||
      !clienteId ||
      !precio ||
      !fecha
    ) {
      setErrSale("Completa todos los campos (todos los animales)");
      return;
    }
    const id = sales.length ? sales[sales.length - 1].id + 1 : 1;
    setSales([
      ...sales,
      {
        id,
        animals: animals.map((a) => ({
          animalId: parseInt(a.animalId, 10),
          caravana: a.caravana,
        })),
        clienteId: parseInt(clienteId, 10),
        precio: parseFloat(precio),
        fecha,
      },
    ]);
    setMsgSale("Venta registrada");
    setSaleForm({
      animals: [{ animalId: "", caravana: "" }],
      clienteId: "",
      precio: "",
      fecha: new Date().toISOString().slice(0, 10),
    });
  };

  // — Registrar Cliente —
  const [newClient, setNewClient] = useState({
    nombre: "",
    apellido: "",
    cuit: "",
    telefono: "",
    email: "",
  });
  const [msgNew, setMsgNew] = useState("");
  const [errNew, setErrNew] = useState("");

  const handleNewClient = (e) => {
    e.preventDefault();
    setMsgNew("");
    setErrNew("");
    const { nombre, apellido, cuit, telefono, email } = newClient;
    if (!nombre || !apellido || !cuit || !telefono || !email) {
      setErrNew("Completa todos los campos");
      return;
    }
    const id = clients.length ? clients[clients.length - 1].id + 1 : 1;
    setClients([...clients, { id, nombre, apellido, cuit, telefono, email }]);
    setMsgNew("Cliente registrado");
    setNewClient({ nombre: "", apellido: "", cuit: "", telefono: "", email: "" });
  };

  // — Editar Cliente inline —
  const [editingId, setEditingId] = useState(null);
  const [editData, setEditData]   = useState({});

  const startEdit = (c) => {
    setEditingId(c.id);
    setEditData({ ...c });
  };
  const cancelEdit = () => {
    setEditingId(null);
    setEditData({});
  };
  const saveEdit = () => {
    setClients(
      clients.map((c) => (c.id === editingId ? { ...editData } : c))
    );
    setEditingId(null);
    setEditData({});
  };

  return (
    <div className="venta-page">
      <TabBar tabs={tabs} activeKey={activeTab} onSelect={setActiveTab} />

      <div className="venta-content">
        {/* — Vender Ganado — */}
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
                />
                <input
                  placeholder="Caravana"
                  value={a.caravana}
                  onChange={(e) =>
                    handleAnimalChange(i, "caravana", e.target.value)
                  }
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
            <button type="button" className="btn-add" onClick={addAnimalLine}>
              + Agregar Animal
            </button>

            <select
              value={saleForm.clienteId}
              onChange={(e) =>
                setSaleForm({ ...saleForm, clienteId: e.target.value })
              }
            >
              <option value="">Selecciona Cliente</option>
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
            <button type="submit">Registrar Venta</button>
            {msgSale && <p className="msg">{msgSale}</p>}
            {errSale && <p className="error">{errSale}</p>}
          </form>
        )}

        {/* — Historial Ventas — */}
        {activeTab === "historialVentas" && (
          <div className="table-container">
            <h2>Historial de Ventas</h2>
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
                {sales.map((s) => {
                  const cli = clients.find((c) => c.id === s.clienteId);
                  return (
                    <tr key={s.id}>
                      <td>{s.id}</td>
                      <td>{s.animals.map((a) => a.caravana).join(", ")}</td>
                      <td>{cli ? `${cli.nombre} ${cli.apellido}` : "–"}</td>
                      <td>${s.precio}</td>
                      <td>{s.fecha}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        {/* — Remates — */}
        {activeTab === "remates" && (
          <div className="table-container">
            <h2>Próximos Remates</h2>
            <table className="list-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Ubicación</th>
                  <th>Fecha</th>
                </tr>
              </thead>
              <tbody>
                {auctions.map((r) => (
                  <tr key={r.id}>
                    <td>{r.id}</td>
                    <td>{r.nombre}</td>
                    <td>{r.ubicacion}</td>
                    <td>{r.fecha}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* — Registrar Cliente — */}
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
            <button type="submit">Crear Cliente</button>
            {msgNew && <p className="msg">{msgNew}</p>}
            {errNew && <p className="error">{errNew}</p>}
          </form>
        )}

        {/* — Listar y Editar Clientes Inline — */}
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
                              setEditData({ ...editData, nombre: e.target.value })
                            }
                          />
                        </td>
                        <td>
                          <input
                            value={editData.apellido}
                            onChange={(e) =>
                              setEditData({ ...editData, apellido: e.target.value })
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
                              setEditData({ ...editData, telefono: e.target.value })
                            }
                          />
                        </td>
                        <td>
                          <input
                            value={editData.email}
                            onChange={(e) =>
                              setEditData({ ...editData, email: e.target.value })
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
          </div>
        )}
      </div>
    </div>
  );
}
