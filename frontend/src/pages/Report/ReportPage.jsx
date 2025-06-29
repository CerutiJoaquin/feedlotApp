import React, { useState } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import "./ReportPage.css";

export default function ReportPage() {
  const tabs = [
    { key: "reportCompras", label: "Compras" },
    { key: "registroVentas", label: "Ventas" },
    { key: "reportGanancias", label: "Ganancias" },
    {
      key: "planillaComedero",
      label: "Planilla Comedero",
      dropdown: [
        { key: "registrarComedero", label: "Registrar Comedero" },
        { key: "verPlanillaComedero", label: "Ver Planilla" },
      ],
    },
    {
      key: "planillaRecorrido",
      label: "Planilla Recorrido",
      dropdown: [
        { key: "registrarRecorrido", label: "Registrar Recorrido" },
        { key: "verPlanillaRecorrido", label: "Ver Planilla" },
      ],
    },
  ];

  const [activeTab, setActiveTab] = useState("reportCompras");

  // --- Datos de ejemplo para cada sección ---
  const [compras] = useState([
    {
      id: 1,
      proveedor: "Proveedor A",
      tipo: "Alimento",
      cantidad: 120,
      total: 6000,
      fecha: "2025-06-10",
    },
    {
      id: 2,
      proveedor: "Proveedor B",
      tipo: "Medicamentos",
      cantidad: 80,
      total: 4000,
      fecha: "2025-06-12",
    },
  ]);
  const [ventas] = useState([
    { id: 1, cliente: "Juan Pérez", total: 7200, fecha: "2025-06-15" },
    { id: 2, cliente: "Empresa Agro S.A.", total: 9800, fecha: "2025-06-18" },
    { id: 3, cliente: "María Gómez", total: 12500, fecha: "2025-06-29" },
  ]);
  const [ganancias] = useState([
    { mes: "Enero", valor: 15000 },
    { mes: "Febrero", valor: 18000 },
    { mes: "Marzo", valor: 21000 },
    { mes: "Abril", valor: 20100 },
    { mes: "Mayo", valor: 20900 },
    { mes: "Junio", valor: 30000 },
  ]);

  // --- Planilla Comedero ---
  const [comederoForm, setComederoForm] = useState({
    planillaId: "",
    corralId: "",
    cantidadConsumida: "",
    observaciones: "",
    encargado: "",
  });
  const [comederoData, setComederoData] = useState([
    {
      planillaId: 1,
      corralId: 1,
      cantidadConsumida: 50,
      observaciones: "Correcto",
      encargado: "María",
    },
    {
      planillaId: 2,
      corralId: 2,
      cantidadConsumida: 60,
      observaciones: "Reponer",
      encargado: "José",
    },
  ]);
  const [msgCom, setMsgCom] = useState("");
  const [errCom, setErrCom] = useState("");

  const handleComedero = (e) => {
    e.preventDefault();
    setMsgCom("");
    setErrCom("");
    const { planillaId, corralId, cantidadConsumida, encargado } = comederoForm;
    if (!planillaId || !corralId || !cantidadConsumida || !encargado) {
      setErrCom("Completa los campos obligatorios");
      return;
    }
    setComederoData([
      ...comederoData,
      {
        planillaId: parseInt(planillaId, 10),
        corralId: parseInt(corralId, 10),
        cantidadConsumida: parseFloat(cantidadConsumida),
        observaciones: comederoForm.observaciones,
        encargado: comederoForm.encargado,
      },
    ]);
    setMsgCom("Registro agregado");
    setComederoForm({
      planillaId: "",
      corralId: "",
      cantidadConsumida: "",
      observaciones: "",
      encargado: "",
    });
  };

  // --- Planilla Recorrido ---
  const [recorridoForm, setRecorridoForm] = useState({
    corralId: "",
    observaciones: "",
    responsable: "",
    fecha: new Date().toISOString().slice(0, 10),
  });
  const [recorridoData, setRecorridoData] = useState([
    {
      id: 1,
      corralId: 1,
      observaciones: "Limpieza",
      responsable: "Ana",
      fecha: "2025-06-20",
    },
    {
      id: 2,
      corralId: 2,
      observaciones: "Arreglo",
      responsable: "Luis",
      fecha: "2025-06-21",
    },
  ]);
  const [msgRec, setMsgRec] = useState("");
  const [errRec, setErrRec] = useState("");

  const handleRecorrido = (e) => {
    e.preventDefault();
    setMsgRec("");
    setErrRec("");
    const { corralId, responsable, fecha } = recorridoForm;
    if (!corralId || !responsable || !fecha) {
      setErrRec("Completa los campos obligatorios");
      return;
    }
    setRecorridoData([
      ...recorridoData,
      {
        id: recorridoData.length
          ? recorridoData[recorridoData.length - 1].id + 1
          : 1,
        corralId: parseInt(corralId, 10),
        observaciones: recorridoForm.observaciones,
        responsable: recorridoForm.responsable,
        fecha,
      },
    ]);
    setMsgRec("Registro agregado");
    setRecorridoForm({
      corralId: "",
      observaciones: "",
      responsable: "",
      fecha: new Date().toISOString().slice(0, 10),
    });
  };

  return (
    <div className="report-page">
      <TabBar tabs={tabs} activeKey={activeTab} onSelect={setActiveTab} />

      <div className="report-content">
        {/* Compras */}
        {activeTab === "reportCompras" && (
          <div className="table-container">
            <h2>Informe de Compras</h2>
            <table className="list-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Proveedor</th>
                  <th>Cantidad</th>
                  <th>Total ($)</th>
                  <th>Fecha</th>
                </tr>
              </thead>
              <tbody>
                {compras.map((c) => (
                  <tr key={c.id}>
                    <td>{c.id}</td>
                    <td>{c.proveedor}</td>
                    <td>{c.cantidad}</td>
                    <td>${c.total}</td>
                    <td>{c.fecha}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Ventas */}
        {activeTab === "registroVentas" && (
          <div className="table-container">
            <h2>Registro de Ventas</h2>
            <table className="list-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Cliente</th>
                  <th>Total ($)</th>
                  <th>Fecha</th>
                </tr>
              </thead>
              <tbody>
                {ventas.map((v) => (
                  <tr key={v.id}>
                    <td>{v.id}</td>
                    <td>{v.cliente}</td>
                    <td>${v.total}</td>
                    <td>{v.fecha}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Ganancias */}
        {activeTab === "reportGanancias" && (
          <div className="table-container">
            <h2>Informe de Ganancias</h2>
            <table className="list-table">
              <thead>
                <tr>
                  <th>Mes</th>
                  <th>Ganancias ($)</th>
                </tr>
              </thead>
              <tbody>
                {ganancias.map((g, i) => (
                  <tr key={i}>
                    <td>{g.mes}</td>
                    <td>${g.valor}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Registrar Comedero */}
        {activeTab === "registrarComedero" && (
          <form className="form-report" onSubmit={handleComedero}>
            <h2>Registrar Comedero</h2>
            <input
              type="number"
              placeholder="Planilla ID"
              value={comederoForm.planillaId}
              onChange={(e) =>
                setComederoForm({ ...comederoForm, planillaId: e.target.value })
              }
            />
            <input
              type="number"
              placeholder="ID de Corral"
              value={comederoForm.corralId}
              onChange={(e) =>
                setComederoForm({ ...comederoForm, corralId: e.target.value })
              }
            />
            <input
              type="number"
              placeholder="Cantidad Consumida"
              value={comederoForm.cantidadConsumida}
              onChange={(e) =>
                setComederoForm({
                  ...comederoForm,
                  cantidadConsumida: e.target.value,
                })
              }
            />
            <input
              placeholder="Observaciones"
              value={comederoForm.observaciones}
              onChange={(e) =>
                setComederoForm({
                  ...comederoForm,
                  observaciones: e.target.value,
                })
              }
            />
            <input
              placeholder="Encargado"
              value={comederoForm.encargado}
              onChange={(e) =>
                setComederoForm({ ...comederoForm, encargado: e.target.value })
              }
            />
            <button type="submit">Guardar</button>
            {msgCom && <p className="msg">{msgCom}</p>}
            {errCom && <p className="error">{errCom}</p>}
          </form>
        )}

        {/* Ver Planilla Comedero */}
        {activeTab === "verPlanillaComedero" && (
          <div className="table-container">
            <h2>Planilla Comedero</h2>
            <table className="list-table">
              <thead>
                <tr>
                  <th>Planilla ID</th>
                  <th>Corral ID</th>
                  <th>Comida Consumida (Kg)</th>
                  <th>Observaciones</th>
                  <th>Encargado</th>
                </tr>
              </thead>
              <tbody>
                {comederoData.map((r) => (
                  <tr key={r.planillaId}>
                    <td>{r.planillaId}</td>
                    <td>{r.corralId}</td>
                    <td>{r.cantidadConsumida}</td>
                    <td>{r.observaciones}</td>
                    <td>{r.encargado}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Registrar Recorrido */}
        {activeTab === "registrarRecorrido" && (
          <form className="form-report" onSubmit={handleRecorrido}>
            <h2>Registrar Recorrido</h2>
            <input
              type="number"
              placeholder="ID de Corral"
              value={recorridoForm.corralId}
              onChange={(e) =>
                setRecorridoForm({ ...recorridoForm, corralId: e.target.value })
              }
            />
            <input
              placeholder="Observaciones"
              value={recorridoForm.observaciones}
              onChange={(e) =>
                setRecorridoForm({
                  ...recorridoForm,
                  observaciones: e.target.value,
                })
              }
            />
            <input
              placeholder="Responsable"
              value={recorridoForm.responsable}
              onChange={(e) =>
                setRecorridoForm({
                  ...recorridoForm,
                  responsable: e.target.value,
                })
              }
            />
            <input
              type="date"
              value={recorridoForm.fecha}
              onChange={(e) =>
                setRecorridoForm({ ...recorridoForm, fecha: e.target.value })
              }
            />
            <button type="submit">Guardar</button>
            {msgRec && <p className="msg">{msgRec}</p>}
            {errRec && <p className="error">{errRec}</p>}
          </form>
        )}

        {/* Ver Planilla Recorrido */}
        {activeTab === "verPlanillaRecorrido" && (
          <div className="table-container">
            <h2>Planilla Recorrido</h2>
            <table className="list-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Corral ID</th>
                  <th>Obsersvaciones</th>
                  <th>Responsable</th>
                  <th>Fecha</th>
                </tr>
              </thead>
              <tbody>
                {recorridoData.map((r) => (
                  <tr key={r.id}>
                    <td>{r.id}</td>
                    <td>{r.corralId}</td>
                    <td>{r.observaciones}</td>
                    <td>{r.responsable}</td>
                    <td>{r.fecha}</td>
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
