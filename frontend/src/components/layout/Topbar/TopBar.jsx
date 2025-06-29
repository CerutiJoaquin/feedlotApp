import React from 'react'
import { FiBell } from 'react-icons/fi'
import './Topbar.css'

export default function Topbar({ usuario }) {
  
  return (
    <header className="topbar">
      <div className="topbar__logo">FeedlotGestor</div>
      <input
        type="search"
        placeholder="🔍 Buscar..."
        className="topbar__search"
      />
      <div className="topbar__actions">
        <FiBell className="topbar__bell"/>
        <span className="topbar__user">{usuario}</span>
        <div className="topbar__avatar">{usuario.charAt(0).toUpperCase()}</div>
      </div>
    </header>
  )
}
