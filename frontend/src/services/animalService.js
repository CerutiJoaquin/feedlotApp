import axios from "axios";

const API_BASE = import.meta.env.VITE_API_URL;

export const getAllAnimals = () => {
  return axios.get(API_BASE);
};

export const getAnimalById = (id) => {
  return axios.get(`${API_BASE}/${id}`);
};

export const createAnimal = (animalData) => {
  return axios.post(API_BASE, animalData);
};

export const updateAnimal = (id, animalData) => {
  return axios.put(`${API_BASE}/${id}`, animalData);
};

export const deleteAnimal = (id) => {
  return axios.delete(`${API_BASE}/${id}`);
};

export const searchByCaravana = (fragmento) => {
  return axios.get(`${API_BASE}/buscar`, {
    params: { fragmento },
  });
};

export const getAnimalByCaravana = (caravana) => {
  return axios.get(
    `${API_BASE}/caravana/${encodeURIComponent(caravana)}`
  );
};

export const getByRaza = (raza) => {
  return axios.get(API_BASE, { params: { raza } });
};

export const getBySexo = (sexo) => {
  return axios.get(API_BASE, { params: { sexo } });
};

export const getByEstadoSalud = (estadoSalud) => {
  return axios.get(API_BASE, { params: { estadoSalud } });
};

export const getByCorral = (corralId) => {
  return axios.get(API_BASE, { params: { corralId } });
};

export const addTreatment = (animalId, tratamientoDto) => {
  return axios.post(`${API_BASE}/${animalId}/tratamiento`, tratamientoDto);
};

export const trace = (caravana) => {
  return axios.get(
    `${API_BASE}/trace/${encodeURIComponent(caravana)}`
  );
};

export const getPesajesByAnimal = (animalId) => {
  return axios.get(`${API_BASE}/${animalId}/pesajes`);
};

export const getTratamientosByAnimal = (animalId) => {
  return axios.get(`${API_BASE}/${animalId}/tratamientos`);
};
