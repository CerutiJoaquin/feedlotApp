import axios from "axios";

const API_URL = "/api/pesaje";


export const getAllPesajes = () => {
  return axios.get(API_URL);
};

export const getPesajeById = (id) => {
  return axios.get(`${API_URL}/${id}`);
};

export const createPesaje = (pesajeData) => {
  return axios.post(API_URL, pesajeData);
};

export const updatePesaje = (id, pesajeData) => {
  return axios.put(`${API_URL}/${id}`, pesajeData);
};
