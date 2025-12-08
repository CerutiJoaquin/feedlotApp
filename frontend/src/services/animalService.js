import axios from "axios";

const API_URL = "/api/animal";

export const getAllAnimals = () => {
  return axios.get(API_URL);
};

export const getAnimalById = (id) => {
  return axios.get(`${API_URL}/${id}`);
};

export const createAnimal = (animalData) => {
  return axios.post(API_URL, animalData);
};

export const updateAnimal = (id, animalData) => {
  return axios.put(`${API_URL}/${id}`, animalData);
};

export const updateFechaTrat = (id, animalData) => {
  return axios.patch(`${API_URL}/${id}`, animalData);
}

export const deleteAnimal = (id) => {
  return axios.delete(`${API_URL}/${id}`);
};

export const searchByCaravana = (fragmento) => {
  return axios.get(`${API_URL}/buscar`, {
    params: { fragmento },
  });
};

export const getAnimalByCaravana = (caravana) => {
  return axios.get(
    `${API_URL}/caravana/${encodeURIComponent(caravana)}`
  );
};

export const getByRaza = (raza) => {
  return axios.get(API_URL, { params: { raza } });
};

export const getBySexo = (sexo) => {
  return axios.get(API_URL, { params: { sexo } });
};

export const getByEstadoSalud = (estadoSalud) => {
  return axios.get(API_URL, { params: { estadoSalud } });
};

export const getByCorral = (corralId) => {
  return axios.get(API_URL, { params: { corralId } });
};

export const addTreatment = (animalId, tratamientoDto) => {
  return axios.post(`${API_URL}/${animalId}/tratamiento`, tratamientoDto);
};

export const trace = (caravana) => {
  return axios.get(
    `${API_URL}/trace/${encodeURIComponent(caravana)}`
  );
};

export const getPesajesByAnimal = (animalId) => {
  return axios.get(`${API_URL}/${animalId}/pesajes`);
};

export const getTratamientosByAnimal = (animalId) => {
  return axios.get(`${API_URL}/${animalId}/tratamientos`);
};
