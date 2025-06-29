import React from 'react'

export default function TextInput({ label, name, type = 'text', register, error }) {
  return (
    <div className="w-full mb-4">
      {label && <label htmlFor={name} className="block text-sm font-medium mb-1">{label}</label>}
      <input
        id={name}
        type={type}
        {...register(name)}
        className={`w-full px-4 py-2 rounded-full border focus:outline-none focus:ring-2 focus:ring-indigo-400 
          ${error ? 'border-red-500' : 'border-gray-300'}`}
        placeholder={label}
      />
      {error && <p className="text-xs text-red-500 mt-1">{error.message}</p>}
    </div>
  )
}
