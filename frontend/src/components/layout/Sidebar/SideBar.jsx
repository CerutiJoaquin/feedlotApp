import React from "react";
import { FiHome } from "react-icons/fi";
import { GiCow } from "react-icons/gi";
import { LuFence } from "react-icons/lu";
import { FaBox } from "react-icons/fa";
import { FiTrendingUp } from "react-icons/fi";
import { MdOutlineSell } from "react-icons/md";
import { HiOutlineDocumentReport } from "react-icons/hi";
import "./Sidebar.css";

const items = [
  { key: "inicio", icon: <FiHome />, label: "Inicio" },
  { key: "ganado", icon: <GiCow />, label: "Ganado" },
  { key: "corral", icon: <LuFence />, label: "Corrales" },
  { key: "insumo", icon: <FaBox />, label: "Insumos" },
  { key: "prediction", icon: <FiTrendingUp />, label: "Predicción" },
  { key: "ventas", icon: <MdOutlineSell />, label: "Ventas" },
  { key: "reportes", icon: <HiOutlineDocumentReport />, label: "Reportes" },
];

export default function Sidebar({ activo, onSelect }) {
  return (
    <nav className="sidebar-nav">
      {items.map((i) => (
        <button
          key={i.key}
          className={`sidebar-nav__btn ${activo === i.key ? "active" : ""}`}
          onClick={() => onSelect(i.key)}
        >
          <span className="sidebar-nav__icon">{i.icon}</span>
          <span className="sidebar-nav__label">{i.label}</span>
        </button>
      ))}
    </nav>
  );
}
