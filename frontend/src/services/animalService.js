import axios from 'axios';

const API = import.meta.env.VITE_API_URL;

export function getAllAnimals() {
    return axios.get(API);
}

export function getAnimalById(id) {
    return axios.get(`${API}/${id}`);
}

export function getAnimalByCaravana(caravana) {
    return axios.get(`${API}/caravana/${caravana}`);
}

export function searchByCaravana(fragmento) {
    return axios.get(`${API}/buscar`, { params: { fragmento } });
}

export function createAnimal(animal) {
    return axios.post(API, animal);
}

export function updateAnimal(id, animal) {
    return axios.put(`${API}/${id}`, animal);
}

export function deleteAnimal(id) {
    return axios.delete(`${API}/${id}`);
}
