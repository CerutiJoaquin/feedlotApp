import React, { useMemo, useRef, useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { FiBell } from 'react-icons/fi';
import { getLowStockInsumos } from "../../../services/insumoService";
import './Topbar.css';

export default function Topbar({ usuario, onLogout }) {
  
  const [openUser, setOpenUser] = useState(false);
  const [menuPos, setMenuPos]   = useState({ top: 0, right: 16 });

  
  const [bellOpen, setBellOpen] = useState(false);
  const [bellPos, setBellPos]   = useState({ top: 0, right: 16 });
  const [alerts, setAlerts]     = useState([]);
  const [loadingAlerts, setLoadingAlerts] = useState(false);

  const actionsRef  = useRef(null);
  const anchorRef   = useRef(null); 
  const menuRef     = useRef(null); 

  const bellRef     = useRef(null);   
  const bellMenuRef = useRef(null);   

  
  const label = useMemo(() => {
    const u = typeof usuario === 'string' ? { email: usuario } : (usuario || {});
    const full = [u?.nombre, u?.apellido].filter(Boolean).join(' ').trim();
    return full || u?.email || 'Usuario';
  }, [usuario]);
  const initial = (label?.[0] || 'U').toUpperCase();

  
  useEffect(() => {
    const onDocClick = (e) => {
      const insideUserAnchor = anchorRef.current?.contains(e.target);
      const insideUserMenu   = menuRef.current?.contains(e.target);
      const insideBellAnchor = bellRef.current?.contains(e.target);
      const insideBellMenu   = bellMenuRef.current?.contains(e.target);

      if (!insideUserAnchor && !insideUserMenu) setOpenUser(false);
      if (!insideBellAnchor && !insideBellMenu) setBellOpen(false);
    };
    document.addEventListener('click', onDocClick);
    return () => document.removeEventListener('click', onDocClick);
  }, []);

  
  const toggleUserMenu = () => {
    if (anchorRef.current) {
      const r = anchorRef.current.getBoundingClientRect();
      setMenuPos({
        top: r.bottom + 8,
        right: Math.max(16, window.innerWidth - r.right),
      });
    }
    setOpenUser(v => !v);
    setBellOpen(false); 
  };

  
  const toggleBellMenu = () => {
    if (bellRef.current) {
      const r = bellRef.current.getBoundingClientRect();
      setBellPos({
        top: r.bottom + 8,
        right: Math.max(16, window.innerWidth - r.right),
      });
    }
    setBellOpen(v => !v);
    setOpenUser(false);
  };


  const handleLogout = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setOpenUser(false);
    onLogout?.();
  };

  
  useEffect(() => {
    let mounted = true;
    const fetchAlerts = async () => {
      try {
        setLoadingAlerts(true);
        const data = await getLowStockInsumos(); 
        if (mounted) setAlerts(Array.isArray(data) ? data : []);
      } catch (err) {
        
      } finally {
        if (mounted) setLoadingAlerts(false);
      }
    };
    fetchAlerts();

    const id = setInterval(fetchAlerts, 30000); // 30s
    return () => { mounted = false; clearInterval(id); };
  }, []);

  const alertCount = alerts.length;

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
        
        <button
          type="button"
          className="topbar__bell-btn"
          onClick={toggleBellMenu}
          aria-haspopup="menu"
          aria-expanded={bellOpen}
          title="Alertas de stock"
          ref={bellRef}
        >
          <FiBell className="topbar__bell" />
          {alertCount > 0 && (
            <span className="topbar__badge" aria-label={`${alertCount} alertas`}>
              {alertCount}
            </span>
          )}
        </button>

        
        <span className="topbar__user">{label}</span>

        <button
          type="button"
          className="topbar__avatar-btn"
          onClick={toggleUserMenu}
          aria-haspopup="menu"
          aria-expanded={openUser}
          title="Cuenta"
          ref={anchorRef}
        >
          <div className="topbar__avatar">{initial}</div>
        </button>
      </div>

      
      {openUser && createPortal(
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

      
      {bellOpen && createPortal(
        <div
          className="topbar__panel topbar__menu--fixed"
          role="menu"
          ref={bellMenuRef}
          style={{ top: bellPos.top, right: bellPos.right }}
          onClick={(e) => e.stopPropagation()}
        >
          <div className="topbar__panel-header">
            <strong>Alertas de stock</strong>
            {loadingAlerts ? (
              <span className="topbar__panel-sub">Cargando…</span>
            ) : (
              <span className="topbar__panel-sub">
                {alertCount > 0 ? `${alertCount} en alerta` : 'Sin alertas'}
              </span>
            )}
          </div>

          <div className="topbar__panel-body">
            {alertCount === 0 ? (
              <div className="topbar__panel-empty">Todos los insumos están OK.</div>
            ) : (
              <ul className="topbar__panel-list">
                {alerts.map(a => (
                  <li key={a.insumoId} className="topbar__panel-item">
                    <div className="topbar__panel-item-title">{a.nombre}</div>
                    <div className="topbar__panel-item-meta">
                      Cant.: <b>{a.cantidad}</b> / Mín.: <b>{a.cantidadMinima}</b> · Faltante: <b>{a.faltante}</b>
                    </div>
                   
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>,
        document.body
      )}
    </header>
  );
}
