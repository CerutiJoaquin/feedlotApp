import axios from "axios";

const API_URL = "/api/venta";

export const cotizarVenta = (cotizarData) => {
    return axios.post(`${API_URL}/cotizar`, cotizarData);
};

export const createVenta = (ventaData) => {
    return axios.post(API_URL, ventaData);
};

export const getVentaById = (id) => {
    return axios.get(`${API_URL}/${id}`);
};

export const getVentasHistorial = (clienteId) => {
  const params = {};
  if (clienteId != null && clienteId !== "") params.clienteId = clienteId;
  return axios.get(`${API_URL}/historial`, { params });
};




