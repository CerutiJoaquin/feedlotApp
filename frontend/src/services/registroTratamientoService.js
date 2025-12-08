import axios from "axios";

const API_URL = "/api/registrotratamiento";

export const getAll   = () => axios.get(API_URL);
export const getById   = (id) => axios.get(`${API_URL}/${id}`);
export const create    = (data) => axios.post(API_URL, data);
export const update    = (id, data) => axios.patch(`${API_URL}/${id}`, data);

export const getByAnimalId = (animalId) =>
  axios.get(`${API_URL}/animal/${animalId}`);

export const getByCaravana = (caravana) =>
  axios.get(`${API_URL}/caravana/${encodeURIComponent(caravana)}`);

export const getByFechaBetween = (desde, hasta) =>
  axios.get(`${API_URL}/fecha`, {
    params: { desde, hasta },
  });