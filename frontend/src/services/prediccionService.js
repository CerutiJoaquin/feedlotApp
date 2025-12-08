import axios from "axios";

const API_URL = "/api/predicciones";

export const getPesoPrediccion = (query) =>
  axios.get(`${API_URL}/peso`, { params: { q: query } });

export const getConsumoPrediccion = (corralId) =>
  axios.get(`${API_URL}/consumo/${corralId}`);

export const getConsumoMensualDashboard = (meses = 6) =>
  axios.get(`${API_URL}/dashboard/consumo-mensual`, {
    params: { meses },
  });