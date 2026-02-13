import React, { useState, useEffect, useMemo } from "react";
import TabBar from "../../components/layout/TabBar/TabBar";
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";
import "./ReportPage.css";

import { getAllAnimals } from "../../services/animalService";
import { getAllCorral } from "../../services/corralService";
import { getAllComederos } from "../../services/registroComederoService";
import { getAll as getAllTratamientos } from "../../services/registroTratamientoService";
import { getVentasHistorial } from "../../services/ventaService";
import { getAllInsumos } from "../../services/insumoService";

export default function ReportPage() {
  const tabs = [
    { key: "resumen", label: "Resumen general" },
    { key: "alimentacion", label: "Alimentación" },
    { key: "ventasGanancias", label: "Ventas y ganancias" },
  ];

  const [activeTab, setActiveTab] = useState("resumen");

  const [animales, setAnimales] = useState([]);
  const [corrales, setCorrales] = useState([]);
  const [registrosComedero, setRegistrosComedero] = useState([]);
  const [ventas, setVentas] = useState([]);
  const [tratamientos, setTratamientos] = useState([]);
  const [insumos, setInsumos] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const parseNumber = (value) => {
    const n = Number(value);
    return Number.isNaN(n) ? 0 : n;
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError("");

        const [
          animalesRes,
          corralesRes,
          comederosRes,
          ventasRes,
          tratRes,
          insumosRes,
        ] = await Promise.all([
          getAllAnimals(),
          getAllCorral(),
          getAllComederos(),
          getVentasHistorial(),
          getAllTratamientos(),
          getAllInsumos(),
        ]);

        setAnimales(animalesRes.data ?? []);
        setCorrales(corralesRes.data ?? []);
        setRegistrosComedero(comederosRes.data ?? []);
        setVentas(ventasRes.data ?? []);
        setTratamientos(tratRes.data ?? []);
        setInsumos(insumosRes.data ?? []);
      } catch (e) {
        console.error(e);
        setError("No se pudieron cargar los datos de los reportes.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const insumoNombrePorId = useMemo(() => {
    const map = new Map();
    insumos.forEach((i) => {
      const id = i.id ?? i.insumoId;
      if (id == null) return;
      const nombre =
        i.nombre ??
        i.nombreInsumo ??
        i.descripcion ??
        i.detalle ??
        `Insumo ${id}`;
      map.set(id, nombre);
    });
    return map;
  }, [insumos]);

  const obtenerNombreInsumo = (insumoId) =>
    insumoNombrePorId.get(insumoId) ?? `#${insumoId}`;

  const totalAnimales = animales.length;
  const totalCorrales = corrales.length;

  const corralesOcupadosSet = new Set(
    animales
      .map((a) => a.corralId ?? a.corral?.corralId ?? a.corral ?? null)
      .filter((id) => id !== null && id !== undefined),
  );
  const corralesConAnimales = corralesOcupadosSet.size;

  const ocupacionPromedio =
    totalCorrales > 0
      ? Math.round((corralesConAnimales / totalCorrales) * 100)
      : 0;

  const consumoTotal = registrosComedero.reduce(
    (acc, r) => acc + parseNumber(r.cantidad),
    0,
  );
  const registrosComederoCount = registrosComedero.length;
  const consumoPromedioPorRegistro =
    registrosComederoCount > 0 ? consumoTotal / registrosComederoCount : 0;

  const totalVentas = ventas.reduce(
    (acc, v) => acc + parseNumber(v.total ?? v.montoTotal),
    0,
  );
  const margenEstimado = 0.3;
  const gananciaEstimada = Math.round(totalVentas * margenEstimado);

  const totalTratamientos = tratamientos.length;
  const animalesTratadosSet = new Set(
    tratamientos
      .map((t) => t.animalId ?? t.animal?.animalId ?? t.animal ?? null)
      .filter((id) => id !== null && id !== undefined),
  );
  const animalesTratados = animalesTratadosSet.size;

  const handleExportPDF = () => {
    if (activeTab === "resumen") exportResumenPDF();
    if (activeTab === "alimentacion") exportAlimentacionPDF();
    if (activeTab === "ventasGanancias") exportVentasPDF();
  };

  const exportResumenPDF = () => {
    const doc = new jsPDF();

    doc.text("Resumen general del feedlot", 14, 15);

    autoTable(doc, {
      startY: 25,
      head: [["Métrica", "Valor"]],
      body: [
        ["Animales totales", totalAnimales],
        [
          "Corrales ocupados",
          `${corralesConAnimales} / ${totalCorrales} (${ocupacionPromedio}%)`,
        ],
        ["Consumo total de alimento (kg)", consumoTotal.toFixed(2)],
        ["Ingresos por ventas ($)", totalVentas.toLocaleString("es-AR")],
        ["Registros de tratamiento", totalTratamientos],
        ["Animales tratados", animalesTratados],
      ],
    });

    doc.save("resumen-general.pdf");
  };

  const exportAlimentacionPDF = () => {
    const doc = new jsPDF();

    doc.text("Reporte de alimentación", 14, 15);

    autoTable(doc, {
      startY: 25,
      head: [["Métrica", "Valor"]],
      body: [
        ["Consumo total (kg)", consumoTotal.toFixed(2)],
        [
          "Consumo promedio por registro (kg)",
          consumoPromedioPorRegistro.toFixed(2),
        ],
        ["Registros de comedero", registrosComederoCount],
      ],
    });

    if (registrosComedero.length > 0) {
      const y =
        doc.lastAutoTable && doc.lastAutoTable.finalY
          ? doc.lastAutoTable.finalY + 10
          : 40;

      doc.text("Detalle de registros de comedero", 14, y);

      autoTable(doc, {
        startY: y + 5,
        head: [["ID", "Insumo", "Corral", "Consumo (kg)", "Fecha"]],
        body: registrosComedero.map((r) => [
          r.registroComederoId,
          obtenerNombreInsumo(r.insumo),
          r.corral,
          parseNumber(r.cantidad),
          r.fecha,
        ]),
      });
    }

    doc.save("reporte-alimentacion.pdf");
  };

  const exportVentasPDF = () => {
    const doc = new jsPDF();

    doc.text("Reporte de ventas y ganancias", 14, 15);

    autoTable(doc, {
      startY: 25,
      head: [["Métrica", "Valor"]],
      body: [
        ["Ingresos por ventas ($)", totalVentas.toLocaleString("es-AR")],
        ["Ganancia estimada ($)", gananciaEstimada.toLocaleString("es-AR")],
        ["Cantidad de ventas", ventas.length],
      ],
    });

    if (ventas.length > 0) {
      const y =
        doc.lastAutoTable && doc.lastAutoTable.finalY
          ? doc.lastAutoTable.finalY + 10
          : 40;

      doc.text("Detalle de ventas", 14, y);

      autoTable(doc, {
        startY: y + 5,
        head: [["ID", "Cliente", "Total ($)", "Fecha"]],
        body: ventas.map((v) => [
          v.id ?? v.ventaId ?? "",
          v.cliente?.nombre ??
            v.clienteNombre ??
            v.cliente ??
            v.nombreCliente ??
            "",
          parseNumber(v.total ?? v.montoTotal).toLocaleString("es-AR"),
          v.fecha ?? v.fechaVenta ?? "",
        ]),
      });
    }

    doc.save("reporte-ventas-ganancias.pdf");
  };

  if (loading) {
    return (
      <div className="report-page">
        <TabBar tabs={tabs} activeKey={activeTab} onSelect={setActiveTab} />
        <div className="report-content">
          <p>Cargando reportes...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="report-page">
        <TabBar tabs={tabs} activeKey={activeTab} onSelect={setActiveTab} />
        <div className="report-content">
          <p className="error">{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="report-page">
      <TabBar tabs={tabs} activeKey={activeTab} onSelect={setActiveTab} />

      <div className="report-content">
        <div className="report-header">
          <h2 className="section-title">
            {activeTab === "resumen" && "Resumen general del feedlot"}
            {activeTab === "alimentacion" && "Reporte de alimentación"}
            {activeTab === "ventasGanancias" && "Reporte de ventas y ganancias"}
          </h2>
          <button className="export-btn" onClick={handleExportPDF}>
            <span className="export-icon">📄</span>
            <span>Exportar PDF</span>
          </button>
        </div>
        {activeTab === "resumen" && (
          <>
            <div className="report-summary-grid">
              <div className="report-summary-card">
                <h3>Animales totales</h3>
                <p>{totalAnimales}</p>
                <span>Cabezas actualmente en el feedlot</span>
              </div>

              <div className="report-summary-card">
                <h3>Corrales ocupados</h3>
                <p>
                  {corralesConAnimales} / {totalCorrales}
                </p>
                <span>Ocupación promedio: {ocupacionPromedio}%</span>
              </div>

              <div className="report-summary-card">
                <h3>Consumo total de alimento</h3>
                <p>
                  {consumoTotal.toLocaleString("es-AR", {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  })}{" "}
                  kg
                </p>
                <span>Según registros de comedero</span>
              </div>

              <div className="report-summary-card">
                <h3>Ingresos por ventas</h3>
                <p>${totalVentas.toLocaleString("es-AR")}</p>
                <span>Suma de todas las ventas</span>
              </div>

              <div className="report-summary-card">
                <h3>Registros de tratamiento</h3>
                <p>{totalTratamientos}</p>
                <span>Tratamientos aplicados</span>
              </div>

              <div className="report-summary-card">
                <h3>Animales tratados</h3>
                <p>{animalesTratados}</p>
                <span>Animales con al menos un tratamiento</span>
              </div>
            </div>

            <div className="table-container">
              <h3>Últimos registros de alimentación</h3>
              <table className="list-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Insumo</th>
                    <th>Corral</th>
                    <th>Consumo (kg)</th>
                    <th>Fecha</th>
                  </tr>
                </thead>
                <tbody>
                  {registrosComedero.map((r) => (
                    <tr key={r.registroComederoId}>
                      <td>{r.registroComederoId}</td>
                      <td>{obtenerNombreInsumo(r.insumo)}</td>
                      <td>{r.corral}</td>
                      <td>{parseNumber(r.cantidad).toFixed(2)}</td>
                      <td>{r.fecha}</td>
                    </tr>
                  ))}
                  {registrosComedero.length === 0 && (
                    <tr>
                      <td colSpan={5}>No hay registros de comedero.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>

            <div className="table-container">
              <h3>Últimas ventas</h3>
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
                    <tr key={v.id ?? v.ventaId}>
                      <td>{v.id ?? v.ventaId}</td>
                      <td>
                        {v.cliente?.nombre ??
                          v.clienteNombre ??
                          v.cliente ??
                          v.nombreCliente ??
                          ""}
                      </td>
                      <td>
                        {parseNumber(v.total ?? v.montoTotal).toLocaleString(
                          "es-AR",
                        )}
                      </td>
                      <td>{v.fecha ?? v.fechaVenta ?? ""}</td>
                    </tr>
                  ))}
                  {ventas.length === 0 && (
                    <tr>
                      <td colSpan={4}>No hay ventas registradas.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </>
        )}
        {activeTab === "alimentacion" && (
          <>
            <div className="report-summary-grid">
              <div className="report-summary-card">
                <h3>Consumo total</h3>
                <p>
                  {consumoTotal.toLocaleString("es-AR", {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  })}{" "}
                  kg
                </p>
                <span>Suma de registros de comedero</span>
              </div>
              <div className="report-summary-card">
                <h3>Consumo promedio</h3>
                <p>{consumoPromedioPorRegistro.toFixed(1)} kg</p>
                <span>Por registro</span>
              </div>
              <div className="report-summary-card">
                <h3>Registros de comedero</h3>
                <p>{registrosComederoCount}</p>
                <span>Cantidad total</span>
              </div>
            </div>

            <div className="table-container">
              <h3>Detalle de registros de comedero</h3>
              <table className="list-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Insumo</th>
                    <th>Corral</th>
                    <th>Consumo (kg)</th>
                    <th>Fecha</th>
                  </tr>
                </thead>
                <tbody>
                  {registrosComedero.map((r) => (
                    <tr key={r.registroComederoId}>
                      <td>{r.registroComederoId}</td>
                      <td>{obtenerNombreInsumo(r.insumo)}</td>
                      <td>{r.corral}</td>
                      <td>{parseNumber(r.cantidad).toFixed(2)}</td>
                      <td>{r.fecha}</td>
                    </tr>
                  ))}

                  {registrosComedero.length === 0 && (
                    <tr>
                      <td colSpan={5}>No hay registros de alimentación.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </>
        )}

        {activeTab === "ventasGanancias" && (
          <>
            <div className="report-summary-grid">
              <div className="report-summary-card">
                <h3>Ingresos por ventas</h3>
                <p>${totalVentas.toLocaleString("es-AR")}</p>
                <span>Total del período</span>
              </div>
              <div className="report-summary-card">
                <h3>Ganancia estimada</h3>
                <p>${gananciaEstimada.toLocaleString("es-AR")}</p>
                <span>Ejemplo con margen del 30%</span>
              </div>
              <div className="report-summary-card">
                <h3>Cantidad de ventas</h3>
                <p>{ventas.length}</p>
                <span>Operaciones registradas</span>
              </div>
            </div>

            <div className="table-container">
              <h3>Detalle de ventas</h3>
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
                    <tr key={v.id ?? v.ventaId}>
                      <td>{v.id ?? v.ventaId}</td>
                      <td>
                        {v.cliente?.nombre ??
                          v.clienteNombre ??
                          v.cliente ??
                          v.nombreCliente ??
                          ""}
                      </td>
                      <td>
                        {parseNumber(v.total ?? v.montoTotal).toLocaleString(
                          "es-AR",
                        )}
                      </td>
                      <td>{v.fecha ?? v.fechaVenta ?? ""}</td>
                    </tr>
                  ))}
                  {ventas.length === 0 && (
                    <tr>
                      <td colSpan={4}>No hay ventas registradas.</td>
                    </tr>
                  )}
                  {ventas.length > 0 && (
                    <tr className="list-table-footer">
                      <td colSpan={2}>TOTAL</td>
                      <td colSpan={2}>
                        ${totalVentas.toLocaleString("es-AR")}
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
