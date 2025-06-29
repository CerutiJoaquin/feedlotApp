// src/pages/Login/LoginPage.js
import React from 'react'
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'
import TextInput from '../../components/common/TextInput'
import Button    from '../../components/common/Button'
import './LoginPage.css'

export default function LoginPage({onLogin}) {
 const navigate = useNavigate()
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting }
  } = useForm()

  const onSubmit = async (values) => {
    try {
      // aquí harías tu llamada al back…
      // const res = await api.login(values)
      // const { token, usuario } = await res.json()
      // localStorage.setItem('token', token)

      
      onLogin(values.username)  

      
      navigate('/inicio', { replace: true })
    } catch (e) {
      console.error(e)
    }
  }
  return (
    <div className="login-container">
      <h1 className="login-title">Feedlot Gestor</h1>
      <form onSubmit={handleSubmit(onSubmit)} className="login-form">
        <h2>Iniciar Sesión</h2>

        <label htmlFor="username">Usuario</label>
        <input
          id="username"
          type="text"
          placeholder="Ingresa tu usuario"
          {...register('username', { required: true })}
        />
        {errors.username && <span>El usuario es obligatorio</span>}

        <label htmlFor="password">Contraseña</label>
        <input
          id="password"
          type="password"
          placeholder="Ingresa tu contraseña"
          {...register('password', { required: true })}
        />
        {errors.password && <span>La contraseña es obligatoria</span>}

        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Ingresando…' : 'Ingresar'}
        </Button>

        <div className="separator" />
        <a href="/register" className="register-button">
          Registrarme
        </a>
      </form>
    </div>
  )
}
