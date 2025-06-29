import axios from "axios";

const API_URL = "/api/insumos";

export const getAllInsumos = () => {
  return axios.get(API_URL);
};

export const getInsumoById = (id) => {
  return axios.get(`${API_URL}/${id}`);
};

export const createInsumo = (payload) => axios.post(API_URL, payload);
export const updateInsumo = (id, payload) =>
  axios.put(`${API_URL}/${id}`, payload);
export const deleteInsumo = (id) => axios.delete(`${API_URL}/${id}`);
