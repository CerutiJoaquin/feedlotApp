import axios from "axios";

const API_URL = "/api/corral";

export const getAllCorral = () => {
    return axios.get(API_URL);
};

export const getCorralById = (id) => {
    return axios.get(`${API_URL}/${id}`);
};

export const createCorral = (corralData) =>{
    return axios.post(API_URL, corralData);
};

export const updateCorral = (id, corralData) => {
    return axios.put(`${API_URL}/${id}`, corralData);
};

export const deleteCorral = (id) => {
    return axios.delete(`${API_URL}/${id}`);
};