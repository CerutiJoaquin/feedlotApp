import React from 'react'

export default function Button({ children, disabled, onClick, type = 'button' }) {
  return (
    <button
      type={type}
      disabled={disabled}
      onClick={onClick}
      className={`w-full py-2 rounded-full text-white font-semibold 
        ${disabled ? 'bg-gray-400 cursor-not-allowed' : 'bg-indigo-600 hover:bg-indigo-700'}`}
    >
      {children}
    </button>
  )
}
