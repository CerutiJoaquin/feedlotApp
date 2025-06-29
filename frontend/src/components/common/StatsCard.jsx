import React from 'react'
import './StatsCard.css'

export default function StatsCard({ titulo, valor, icono }) {
  return (
    <div className="stat-card">
      <div className="stat-card__icon">{icono}</div>
      <div>
        <p className="stat-card__title">{titulo}</p>
        <p className="stat-card__value">{valor}</p>
      </div>
    </div>
  )
}
