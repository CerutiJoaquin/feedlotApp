import React from "react";
import ReactECharts from "echarts-for-react";
import "./PredictionChart.css";

export default function PredictionChart({ data, yLabel = "Kg" }) {
  if (!data || data.length === 0) return null;


  const labels = data.map((d) =>
    new Date(d.fecha).toLocaleDateString("es-AR", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    })
  );


  const reales = data.map((d) => (d.predicho ? null : d.valor));


  const ultimoRealIndex = data
    .map((d) => !d.predicho)
    .lastIndexOf(true);


  const predichos = data.map((d, i) => {
    if (!d.predicho && i !== ultimoRealIndex) return null;
    return d.valor;
  });

  const option = {
    tooltip: {
      trigger: "axis",
    },

    legend: {
      data: ["Valor real", "Valor predicho"],
      bottom: 0,
    },

    grid: {
      left: "6%",
      right: "4%",
      bottom: "20%",
      containLabel: true,
    },

    dataZoom: [
      {
        type: "inside",
        start: 70,
        end: 100,
      },
      {
        start: 70,
        end: 100,
      },
    ],

    xAxis: {
      type: "category",
      data: labels,
      boundaryGap: false,
    },

    yAxis: {
      type: "value",
      name: yLabel,
      nameLocation: "middle",
      nameGap: 45,
    },

    series: [
      {
        name: "Valor real",
        type: "line",
        data: reales,
        smooth: true,
        symbolSize: 6,
        lineStyle: {
          width: 2,
        },
      },
      {
        name: "Valor predicho",
        type: "line",
        data: predichos,
        smooth: true,
        symbolSize: 6,
        lineStyle: {
          type: "dashed",
          width: 3,
        },
        itemStyle: {
          color: "#b6ff2a",
        },
        markArea: {
          itemStyle: {
            color: "rgba(79, 140, 255, 0.12)",
          },
          data: [
            [
              { xAxis: labels[ultimoRealIndex] },
              { xAxis: labels[labels.length - 1] },
            ],
          ],
        },
      },
    ],
  };

  return (
    <div className="chart-card">
      <ReactECharts option={option} style={{ height: 420, width: "100%" }} />
    </div>
  );
}
