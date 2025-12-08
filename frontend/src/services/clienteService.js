import axios from "axios";

const API_URL = "/api/cliente";

export const getAllClientes = () => {
  return axios.get(API_URL); 
};

export const getClienteById = (id) => {
  return axios.get(`${API_URL}/${id}`); 
};

export const createCliente = (clienteCreateDto) => {
  return axios.post(API_URL, clienteCreateDto);
};

export const updateCliente = (id, clienteUpdateDto) => {
  return axios.patch(`${API_URL}/${id}`, clienteUpdateDto);
};

export const deleteCliente = (id) => {
  return axios.delete(`${API_URL}/${id}`); 
};
