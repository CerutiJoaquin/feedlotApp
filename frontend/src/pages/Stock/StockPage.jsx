import React, { useState } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import "./StockPage.css";

export default function StockPage() {
  const tabs = [
    { key: "crear",              label: "Ingresar" },
    { key: "listarAlimentos",    label: "Alimentos" },
    { key: "listarMedicamentos", label: "Medicamentos" },
    { key: "configurar",         label: "Configurar Limites" },
  ];
  const [activeTab, setActiveTab] = useState("crear");

  // Datos de ejemplo
  const initialInsumos = [
    { id: 1, nombre: "Maíz",       categoria: "Alimento",    tipo: "Grano",          cantidadActual: 1000, cantidadMinima: 200, unidad: "kg" },
    { id: 2, nombre: "Heno",       categoria: "Alimento",    tipo: "Forraje",        cantidadActual:  500, cantidadMinima: 100, unidad: "kg" },
    { id: 3, nombre: "Silo",       categoria: "Alimento",    tipo: "Fermentado",     cantidadActual:  300, cantidadMinima:  50, unidad: "kg" },
    { id: 4, nombre: "Ivermectina",categoria: "Medicamento", tipo: "Antiparasitario",cantidadActual:   50, cantidadMinima:  10, unidad: "ml" },
    { id: 5, nombre: "Penicilina", categoria: "Medicamento", tipo: "Antibiótico",    cantidadActual:  100, cantidadMinima:  20, unidad: "ml" },
  ];
  const [insumos, setInsumos] = useState(initialInsumos);

  // Estado para formulario de ingreso
  const [newInsumo, setNewInsumo] = useState({
    nombre: "",
    categoria: "Alimento",
    tipo: "",
    cantidadActual: "",
    unidad: "",
  });
  const [msgCreate, setMsgCreate] = useState("");
  const [errorCreate, setErrorCreate] = useState("");

  const handleCreate = (e) => {
    e.preventDefault();
    setMsgCreate(""); setErrorCreate("");
    const { nombre, categoria, tipo, cantidadActual, unidad } = newInsumo;
    if (!nombre || !tipo || !cantidadActual || !unidad) {
      setErrorCreate("Completa todos los campos");
      return;
    }
    const id = insumos.length
      ? insumos[insumos.length - 1].id + 1
      : 1;
    const insumo = {
      id,
      nombre,
      categoria,
      tipo,
      cantidadActual: parseFloat(cantidadActual),
      cantidadMinima: 0,
      unidad,
    };
    setInsumos([...insumos, insumo]);
    setMsgCreate("Insumo creado con éxito");
    setNewInsumo({ nombre: "", categoria: "Alimento", tipo: "", cantidadActual: "", unidad: "" });
  };

  // Ajustar cantidad mínima
  const handleMinChange = (id, value) => {
    setInsumos(insumos.map(i =>
      i.id === id ? { ...i, cantidadMinima: parseFloat(value) } : i
    ));
  };

  return (
    <div className="stock-page">
      <TabBar tabs={tabs} activeKey={activeTab} onSelect={setActiveTab} />

      <div className="stock-content">
        {/* === INGRESAR === */}
        {activeTab === "crear" && (
          <form className="form-ingresar" onSubmit={handleCreate}>
            <h2>Ingresar Insumo</h2>
            <input
              placeholder="Nombre"
              value={newInsumo.nombre}
              onChange={e => setNewInsumo({ ...newInsumo, nombre: e.target.value })}
            />
            <select
              value={newInsumo.categoria}
              onChange={e => setNewInsumo({ ...newInsumo, categoria: e.target.value })}
            >
              <option value="Alimento">Alimento</option>
              <option value="Medicamento">Medicamento</option>
            </select>
            <input
              placeholder="Tipo"
              value={newInsumo.tipo}
              onChange={e => setNewInsumo({ ...newInsumo, tipo: e.target.value })}
            />
            <input
              type="number"
              placeholder="Cantidad"
              value={newInsumo.cantidadActual}
              onChange={e => setNewInsumo({ ...newInsumo, cantidadActual: e.target.value })}
            />
            <input
              placeholder="Unidad de Medida"
              value={newInsumo.unidad}
              onChange={e => setNewInsumo({ ...newInsumo, unidad: e.target.value })}
            />
            <button type="submit">Crear</button>
            {msgCreate   && <p className="msg">{msgCreate}</p>}
            {errorCreate && <p className="error">{errorCreate}</p>}
          </form>
        )}

        {/* === ALIMENTOS === */}
        {activeTab === "listarAlimentos" && (
          <div className="list-container">
            <h2>Alimentos</h2>
            <table className="list-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Tipo</th>
                  <th>Cant. Actual</th>
                  <th>Cant. Mínima</th>
                  <th>Unidad</th>
                </tr>
              </thead>
              <tbody>
                {insumos
                  .filter(i => i.categoria === "Alimento")
                  .map(i => (
                    <tr key={i.id}>
                      <td>{i.id}</td>
                      <td>{i.nombre}</td>
                      <td>{i.tipo}</td>
                      <td>{i.cantidadActual}</td>
                      <td>{i.cantidadMinima}</td>
                      <td>{i.unidad}</td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        )}

        {/* === MEDICAMENTOS === */}
        {activeTab === "listarMedicamentos" && (
          <div className="list-container">
            <h2>Medicamentos</h2>
            <table className="list-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Tipo</th>
                  <th>Cant. Actual</th>
                  <th>Cant. Mínima</th>
                  <th>Unidad</th>
                </tr>
              </thead>
              <tbody>
                {insumos
                  .filter(i => i.categoria === "Medicamento")
                  .map(i => (
                    <tr key={i.id}>
                      <td>{i.id}</td>
                      <td>{i.nombre}</td>
                      <td>{i.tipo}</td>
                      <td>{i.cantidadActual}</td>
                      <td>{i.cantidadMinima}</td>
                      <td>{i.unidad}</td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        )}

        {/* === CONFIGURAR LÍMITES === */}
        {activeTab === "configurar" && (
          <div className="config-container">
            <h2>Configurar Límites</h2>
            <table className="limits-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Categoria</th>
                  <th>Cant. Mínima</th>
                </tr>
              </thead>
              <tbody>
                {insumos.map(i => (
                  <tr key={i.id}>
                    <td>{i.id}</td>
                    <td>{i.nombre}</td>
                    <td>{i.categoria}</td>
                    <td>
                      <input
                        type="number"
                        value={i.cantidadMinima}
                        onChange={e => handleMinChange(i.id, e.target.value)}
                      />
                    </td>
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
