import axios from "axios";
const API_URL = "/api/registrorecorrido";

export const getAllRecorridos = () => {
  return axios.get(API_URL);
};
export const createRecorrido = (data) => {
  return axios.post(API_URL, data);
};
