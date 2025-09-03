import React from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import Button from "../../components/common/Button";
import { login as LoginApi } from "../../services/authService";
import "./LoginPage.css";

export default function LoginPage({ onLogin }) {
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    defaultValues: {
      email: "",
      contrasenia: "",
    },
  });

  const onSubmit = async ({ email, contrasenia }) => {
    try {
      const { data: user } = await LoginApi(email, contrasenia);

      localStorage.setItem("user", JSON.stringify(user));

      onLogin?.(user);
      navigate("/inicio", { replace: true });
    } catch (e) {
      console.error(e);
      setError("root", {
        type: "server",
        message: "Email o contraseña incorrectos",
      });
    }
  };
  return (
    <div className="login-container">
      <h1 className="login-title">Feedlot Gestor</h1>

      <form onSubmit={handleSubmit(onSubmit)} className="login-form" noValidate autoComplete="on">
        <h2>Iniciar Sesión</h2>

        <label htmlFor="email">Email</label>
        <input
          id="email"
          type="email"
          placeholder="ejemplo@correo.com"
          autoComplete="email"
          {...register("email", {})}
        />

        {errors.email && <span className="error">{errors.email.message}</span>}

        <label htmlFor="contrasenia">Contraseña</label>
        <input
          id="contrasenia"
          type="password"
          placeholder="Ingresa tu contraseña"
          autoComplete="current-password" 
          {...register("contrasenia", {
          })}
        />
        {errors.contrasenia && (
          <span className="error">{error.contrasenia.message}</span>
        )}

        {errors.root && <div className="error">{errors.root.message}</div>}

        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Ingresando…" : "Ingresar"}
        </Button>

        <div className="separator" />
        <a href="/register" className="register-button">
          Registrarme
        </a>
      </form>
    </div>
  );
}
