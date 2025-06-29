
import React, { useState, useRef, useEffect } from "react";
import "./TabBar.css";
import { FiChevronDown } from "react-icons/fi";

export default function TabBar({ tabs = [], activeKey, onSelect }) {
  
  const [openDropdownKey, setOpenDropdownKey] = useState(null);
  const navRef = useRef(null);

  
  useEffect(() => {
    function handleClickOutside(e) {
      if (navRef.current && !navRef.current.contains(e.target)) {
        setOpenDropdownKey(null);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <nav className="tabbar" ref={navRef}>
      {tabs.map((tab) => {
        const isActive = tab.key === activeKey;
        const isOpen = openDropdownKey === tab.key;

        
        if (tab.dropdown) {
          return (
            <div
              key={tab.key}
              className={`tabbar__item ${
                isActive || isOpen ? "tabbar__item--active" : ""
              }`}
            >
              <button
                className="tabbar__btn"
                onClick={() => {
                  onSelect(tab.key);
                  
                  setOpenDropdownKey(isOpen ? null : tab.key);
                }}
              >
                {tab.label} <FiChevronDown />
              </button>
              {isOpen && (
                <div className="tabbar__dropdown">
                  {tab.dropdown.map((sub) => (
                    <button
                      key={sub.key}
                      className="tabbar__dropdown-btn"
                      onClick={() => {
                        onSelect(sub.key);
                        setOpenDropdownKey(null);
                      }}
                    >
                      {sub.label}
                    </button>
                  ))}
                </div>
              )}
            </div>
          );
        }

        
        return (
          <button
            key={tab.key}
            className={`tabbar__item tabbar__btn ${
              isActive ? "tabbar__item--active" : ""
            }`}
            onClick={() => {
              onSelect(tab.key);
              setOpenDropdownKey(null);
            }}
          >
            {tab.label}
          </button>
        );
      })}
    </nav>
  );
}
