import React, { useMemo, useRef, useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { FiBell } from 'react-icons/fi';
import './Topbar.css';

export default function Topbar({ usuario, onLogout }) {
  const [open, setOpen] = useState(false);
  const [menuPos, setMenuPos] = useState({ top: 0, right: 16 });

  const actionsRef = useRef(null); 
  const anchorRef  = useRef(null); 
  const menuRef    = useRef(null); 

  const label = useMemo(() => {
    const u = typeof usuario === 'string' ? { email: usuario } : (usuario || {});
    const full = [u?.nombre, u?.apellido].filter(Boolean).join(' ').trim();
    return full || u?.email || 'Usuario';
  }, [usuario]);

  const initial = (label?.[0] || 'U').toUpperCase();


  useEffect(() => {
    const onDocClick = (e) => {
      const insideAnchor = anchorRef.current?.contains(e.target);
      const insideMenu   = menuRef.current?.contains(e.target);
      if (!insideAnchor && !insideMenu) setOpen(false);
    };
    document.addEventListener('click', onDocClick);
    return () => document.removeEventListener('click', onDocClick);
  }, []);

  const toggleMenu = () => {
    if (anchorRef.current) {
      const r = anchorRef.current.getBoundingClientRect();
      setMenuPos({
        top: r.bottom + 8,
        right: Math.max(16, window.innerWidth - r.right),
      });
    }
    setOpen(v => !v);
  };

  const handleLogout = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setOpen(false);
    onLogout?.(); 
  };

  return (
    <header className="topbar">
      <div className="topbar__logo">FeedlotGestor</div>

      <input
        type="search"
        placeholder="🔍 Buscar..."
        className="topbar__search"
        autoComplete="off"
      />

      <div className="topbar__actions" ref={actionsRef}>
        <FiBell className="topbar__bell" />
        <span className="topbar__user">{label}</span>

        <button
          type="button"
          className="topbar__avatar-btn"
          onClick={toggleMenu}
          aria-haspopup="menu"
          aria-expanded={open}
          title="Cuenta"
          ref={anchorRef}
        >
          <div className="topbar__avatar">{initial}</div>
        </button>
      </div>

    
      {open && createPortal(
        <div
          className="topbar__menu topbar__menu--fixed"
          role="menu"
          ref={menuRef}
          style={{ top: menuPos.top, right: menuPos.right }}
          onClick={(e) => e.stopPropagation()} 
        >
          <button type="button" className="topbar__menu-item" role="menuitem">
            Mi perfil
          </button>
          <div className="topbar__menu-sep" />
          <button type="button" className="topbar__menu-item" role="menuitem">
            Preferencias
          </button>
          <div className="topbar__menu-sep" />
          <button
            type="button"
            className="topbar__menu-item topbar__menu-item--danger"
            role="menuitem"
            onClick={handleLogout}
          >
            Cerrar sesión
          </button>
        </div>,
        document.body
      )}
    </header>
  );
}
