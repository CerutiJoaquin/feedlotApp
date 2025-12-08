import axios from "axios";

const API_URL = "/api/corral";

export const getAllCorral = () => axios.get(API_URL);
export const getCorralById = (id) => axios.get(`${API_URL}/${id}`);
export const createCorral = (corralData) => axios.post(API_URL, corralData);
export const updateCorral = (id, corralData) => axios.patch(`${API_URL}/${id}`, corralData);
export const deleteCorral = (id) => axios.delete(`${API_URL}/${id}`);
