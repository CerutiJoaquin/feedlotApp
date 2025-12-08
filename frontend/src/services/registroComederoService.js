import axios from "axios";
const API_URL = "/api/registrocomedero";

export const getAllComederos = () => {
  return axios.get(API_URL);
};
export const createComedero = (data) => {
  return axios.post(API_URL, data);
};
