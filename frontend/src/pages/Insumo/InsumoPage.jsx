import React, { useEffect, useState } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import {
  getAllInsumos,
  createInsumo,
  updateInsumo,
  deleteInsumo,
  getInsumoByCategoria,
} from "../../services/insumoService";
import "./InsumoPage.css";

export default function InsumoPage() {
  const tabs = [
    { key: "crear", label: "Ingresar" },
    { key: "listar", label: "Listar" },
    { key: "reponer", label: "Reponer" },
  ];

  const listSubtabs = [
    { key: "listarAlimentos", label: "Alimentos" },
    { key: "listarMedicamentos", label: "Medicamentos" },
  ];

  const [activeTab, setActiveTab] = useState("listar");
  const [subTab, setSubTab] = useState("listarAlimentos");

  const [insumos, setInsumos] = useState([]);
  const [msgCreate, setMsgCreate] = useState("");
  const [errorCreate, setErrorCreate] = useState("");
  const [editingInsumo, setEditingInsumo] = useState(null);


  const [repo, setRepo] = useState({
    insumoId: "",
    cantidad: "",
    observacion: "",
  });
  const [msgRepo, setMsgRepo] = useState("");
  const [errorRepo, setErrorRepo] = useState("");

  useEffect(() => {
    loadByTab();
  }, [activeTab, subTab]);

  const loadByTab = async () => {
    try {
      if (activeTab === "listar") {
        if (subTab === "listarMedicamentos") {
          const { data } = await getInsumoByCategoria("MEDICAMENTO");
          setInsumos(data);
          return;
        }
        if (subTab === "listarAlimentos") {
          const { data } = await getInsumoByCategoria("ALIMENTO");
          setInsumos(data);
          return;
        }
        const { data } = await getAllInsumos();
        setInsumos(data);
        return;
      }
  
      if (activeTab === "reponer") {
        const { data } = await getAllInsumos();
        setInsumos(data);
        return;
      }
      setInsumos([]);
    } catch (e) {
      console.error("Error al cargar insumos", e);
      setInsumos([]);
    }
  };

  const [newInsumo, setNewInsumo] = useState({
    nombre: "",
    categoria: "",
    tipo: "",
    cantidad: "",
    cantidadMinima: "",
    unidadMedida: "",
  });

  const handleCreate = async (e) => {
    e.preventDefault();
    setMsgCreate("");
    setErrorCreate("");

    const nombre = newInsumo.nombre?.trim();
    const categoria = newInsumo.categoria?.toUpperCase();
    const tipo = newInsumo.tipo.trim();
    const cantidad = Number(newInsumo.cantidad);
    const cantidadMinima = Number(newInsumo.cantidadMinima);
    const unidadMedida = newInsumo.unidadMedida?.trim();

    if (
      !nombre ||
      !categoria ||
      !tipo ||
      Number.isNaN(cantidad) ||
      Number.isNaN(cantidadMinima) ||
      !unidadMedida
    ) {
      setErrorCreate("Completa todos los campos");
      return;
    }

    const body = {
      nombre,
      categoria,
      tipo,
      cantidad,
      cantidadMinima,
      unidadMedida,
    };

    try {
      await createInsumo(body);
      setMsgCreate("Insumo ingresado con éxito");
      setNewInsumo({
        nombre: "",
        categoria: "",
        tipo: "",
        cantidad: "",
        cantidadMinima: "",
        unidadMedida: "",
      });

      setActiveTab("listar");
      setSubTab(
        body.categoria === "MEDICAMENTO"
          ? "listarMedicamentos"
          : "listarAlimentos"
      );
    } catch (e) {
      console.error("Error al crear el insumo", e);
      if (cantidad < 0 || cantidadMinima < 0) {
        setErrorCreate(
          "La cantidad y la cantidad mínima no pueden ser menores a 0"
        );
      } else {
        setErrorCreate("Error al intentar crear el insumo");
      }
    }
  };

  const handleEditClick = (row) => setEditingInsumo(row);

  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      const body = {
        ...editingInsumo,
        cantidad: Number(editingInsumo.cantidad),
        cantidadMinima: Number(editingInsumo.cantidadMinima),
        unidadMedida: editingInsumo.unidadMedida,
      };
      await updateInsumo(editingInsumo.insumoId, body);
      setEditingInsumo(null);
      await loadByTab();
    } catch (error) {
      console.log("Error al editar el insumo", error);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("¿Seguro que deseas eliminar este insumo?")) return;
    try {
      await deleteInsumo(id);
      await loadByTab();
    } catch (error) {
      console.error("Error al eliminar el insumo", error);
    }
  };

  const handleReponer = async (e) => {
    e.preventDefault();
    setMsgRepo("");
    setErrorRepo("");

    const insumoId = Number(repo.insumoId);
    const delta = Number(repo.cantidad);

    if (!insumoId || Number.isNaN(delta) || delta <= 0) {
      setErrorRepo("Selecciona un insumo y una cantidad mayor a 0.");
      return;
    }

    const selected = insumos.find((i) => i.insumoId === insumoId);
    if (!selected) {
      setErrorRepo("No se encontró el insumo seleccionado.");
      return;
    }

    const newCantidad = Number(selected.cantidad || 0) + delta;

    const body = {
      ...selected,
      cantidad: newCantidad,
    };

    try {
      await updateInsumo(insumoId, body);
      setMsgRepo(
        `Reposición aplicada. Nuevo stock: ${newCantidad} ${
          selected.unidadMedida || ""
        }`
      );
      setRepo({ insumoId: "", cantidad: "", observacion: "" });
      await loadByTab();
    } catch (err) {
      console.error("Error al reponer insumo", err);
      setErrorRepo("No se pudo aplicar la reposición.");
    }
  };

  const selectedRepo = insumos.find(
    (i) => i.insumoId === Number(repo.insumoId)
  );
  const previewCantidad =
    Number(selectedRepo?.cantidad || 0) + Number(repo.cantidad || 0) ||
    Number(selectedRepo?.cantidad || 0);

  return (
    <div className="insumo-page">
      <TabBar
        tabs={tabs}
        activeKey={activeTab}
        onSelect={(k) => {
          setActiveTab(k);
          if (k !== "listar") setEditingInsumo(null);
          if (k !== "reponer") {
            setRepo({ insumoId: "", cantidad: "", observacion: "" });
            setMsgRepo("");
            setErrorRepo("");
          }
        }}
      />

      {activeTab === "listar" && (
        <div>
          <TabBar
            tabs={listSubtabs}
            activeKey={subTab}
            onSelect={(k) => {
              setSubTab(k);
              setEditingInsumo(null);
            }}
          />
        </div>
      )}

      <div className="insumo-content">
        {activeTab === "crear" && (
          <form className="form-ingresar" onSubmit={handleCreate}>
            <h2>Ingresar Insumo</h2>

            <h6 className="h6">Nombre</h6>
            <input
              placeholder="Nombre"
              value={newInsumo.nombre}
              onChange={(e) =>
                setNewInsumo({ ...newInsumo, nombre: e.target.value })
              }
            />

            <h6 className="h6">Categoria</h6>
            <select
              value={newInsumo.categoria}
              onChange={(e) =>
                setNewInsumo({ ...newInsumo, categoria: e.target.value })
              }
            >
              <option value="">Seleccione categoria...</option>
              <option value="ALIMENTO">Alimento</option>
              <option value="MEDICAMENTO">Medicamento</option>
            </select>

            <h6 className="h6">Tipo</h6>
            <input
              placeholder="Tipo"
              value={newInsumo.tipo}
              onChange={(e) =>
                setNewInsumo({ ...newInsumo, tipo: e.target.value })
              }
            />

            <h6 className="h6">Cantidad</h6>
            <input
              type="number"
              placeholder="Cantidad"
              value={newInsumo.cantidad}
              onChange={(e) =>
                setNewInsumo({ ...newInsumo, cantidad: e.target.value })
              }
            />

            <h6 className="h6">Cantidad Mínima</h6>
            <input
              type="number"
              placeholder="Cantidad Mínima"
              value={newInsumo.cantidadMinima}
              onChange={(e) =>
                setNewInsumo({ ...newInsumo, cantidadMinima: e.target.value })
              }
            />

            <h6 className="h6">Unidad de Medida</h6>
            <input
              placeholder="Unidad de Medida"
              value={newInsumo.unidadMedida}
              onChange={(e) =>
                setNewInsumo({ ...newInsumo, unidadMedida: e.target.value })
              }
            />
            <button type="submit">Ingresar</button>
            {msgCreate && <p className="msg">{msgCreate}</p>}
            {errorCreate && <p className="error">{errorCreate}</p>}
          </form>
        )}

        {activeTab === "listar" && subTab === "listarAlimentos" && (
          <div className="list-container">
            <h2>Alimentos</h2>
            <table className="list-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Tipo</th>
                  <th>Cantidad</th>
                  <th>Cant. Mínima</th>
                  <th>Ud. de Medida</th>
                  <th>Fecha Ingreso</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {insumos.map((row) => (
                  <tr key={row.insumoId}>
                    <td>{row.insumoId}</td>
                    <td>{row.nombre}</td>
                    <td>{row.tipo}</td>
                    <td>{row.cantidad}</td>
                    <td>{row.cantidadMinima}</td>
                    <td>{row.unidadMedida}</td>
                    <td>{row.fechaIngreso}</td>
                    <td>
                      <div className="actions">
                        <button
                          type="button"
                          className="btn btn--edit"
                          onClick={() => handleEditClick(row)}
                          title="Editar"
                        >
                          <span className="btn__icon">✏️</span>
                          <span className="btn__label">Configurar</span>
                        </button>
                        <button
                          type="button"
                          className="btn btn--danger"
                          onClick={() => handleDelete(row.insumoId)}
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

            {editingInsumo && (
              <form className="form-editar" onSubmit={handleUpdate}>
                <h3>Configurar Insumo #{editingInsumo?.insumoId}</h3>
                <h5>Cantidad</h5>
                <input
                  type="number"
                  value={editingInsumo.cantidad}
                  onChange={(e) =>
                    setEditingInsumo({
                      ...editingInsumo,
                      cantidad: e.target.value,
                    })
                  }
                />
                <h5>Cantidad Mínima</h5>
                <input
                  type="number"
                  value={editingInsumo.cantidadMinima}
                  onChange={(e) =>
                    setEditingInsumo({
                      ...editingInsumo,
                      cantidadMinima: e.target.value,
                    })
                  }
                />
                <h5>Unidad de Medida</h5>
                <input
                  type="text"
                  value={editingInsumo.unidadMedida}
                  onChange={(e) =>
                    setEditingInsumo({
                      ...editingInsumo,
                      unidadMedida: e.target.value,
                    })
                  }
                />
                <button type="submit" className="btn--accept">
                  Guardar Cambios
                </button>
              </form>
            )}
          </div>
        )}

        {activeTab === "listar" && subTab === "listarMedicamentos" && (
          <div className="list-container">
            <h2>Medicamentos</h2>
            <table className="list-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Tipo</th>
                  <th>Cantidad</th>
                  <th>Cant. Mínima</th>
                  <th>Ud. de Medida</th>
                  <th>Fecha Ingreso</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {insumos.map((row) => (
                  <tr key={row.insumoId}>
                    <td>{row.insumoId}</td>
                    <td>{row.nombre}</td>
                    <td>{row.tipo}</td>
                    <td>{row.cantidad}</td>
                    <td>{row.cantidadMinima}</td>
                    <td>{row.unidadMedida}</td>
                    <td>{row.fechaIngreso}</td>
                    <td>
                      <div className="actions">
                        <button
                          type="button"
                          className="btn btn--edit"
                          onClick={() => handleEditClick(row)}
                          title="Editar"
                        >
                          <span className="btn__icon">✏️</span>
                          <span className="btn__label">Configurar</span>
                        </button>
                        <button
                          type="button"
                          className="btn btn--danger"
                          onClick={() => handleDelete(row.insumoId)}
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

            {editingInsumo && (
              <form className="form-editar" onSubmit={handleUpdate}>
                <h3>Configurar Insumo #{editingInsumo?.insumoId}</h3>
                <h5>Cantidad</h5>
                <input
                  type="number"
                  value={editingInsumo.cantidad}
                  onChange={(e) =>
                    setEditingInsumo({
                      ...editingInsumo,
                      cantidad: e.target.value,
                    })
                  }
                />
                <h5>Cantidad Mínima</h5>
                <input
                  type="number"
                  value={editingInsumo.cantidadMinima}
                  onChange={(e) =>
                    setEditingInsumo({
                      ...editingInsumo,
                      cantidadMinima: e.target.value,
                    })
                  }
                />
                <h5>Unidad de Medida</h5>
                <input
                  type="text"
                  value={editingInsumo.unidadMedida}
                  onChange={(e) =>
                    setEditingInsumo({
                      ...editingInsumo,
                      unidadMedida: e.target.value,
                    })
                  }
                />
                <button type="submit" className="btn--accept">
                  Guardar Cambios
                </button>
              </form>
            )}
          </div>
        )}

    
        {activeTab === "reponer" && (
          <form className="form-reponer" onSubmit={handleReponer}>
            <h2>Reponer Insumo</h2>

            <h6 className="h6">Insumo</h6>
            <select
              value={repo.insumoId}
              onChange={(e) => setRepo({ ...repo, insumoId: e.target.value })}
            >
              <option value="">Seleccione un insumo...</option>
              {insumos.map((i) => (
                <option key={i.insumoId} value={i.insumoId}>
                  {i.nombre} {i.unidadMedida ? `(${i.unidadMedida})` : ""}
                </option>
              ))}
            </select>

            {selectedRepo && (
              <>
                <div className="repo-stats">
                  Stock actual: <b>{selectedRepo.cantidad}</b> · Mínimo:{" "}
                  <b>{selectedRepo.cantidadMinima}</b>{" "}
                  {selectedRepo.unidadMedida || ""}
                </div>

                <h6 className="h6">Cantidad a ingresar</h6>
                <input
                  type="number"
                  min="0"
                  step="any"
                  placeholder="Ej: 50"
                  value={repo.cantidad}
                  onChange={(e) =>
                    setRepo({ ...repo, cantidad: e.target.value })
                  }
                />

                <div className="repo-preview">
                  Quedará en: <b>{previewCantidad}</b>{" "}
                  {selectedRepo.unidadMedida || ""}
                </div>
              </>
            )}

            <button type="submit" className="btn--accept">
              Reponer
            </button>

            {msgRepo && <p className="msg">{msgRepo}</p>}
            {errorRepo && <p className="error">{errorRepo}</p>}
          </form>
        )}
      </div>
    </div>
  );
}
