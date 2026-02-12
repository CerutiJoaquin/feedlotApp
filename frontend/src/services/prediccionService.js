import axios from "axios";


const API_SPRING = "http://localhost:8080/api/predicciones";

const API_PYTHON = "http://localhost:8000";


export const getConsumoMensualFeedlot = (meses = 6) =>
  axios.get(`${API_SPRING}/consumo-mensual`, {
    params: { meses }
  });


export const getPesoPrediccion = (animalId, meses) =>
  axios.post(`${API_PYTHON}/predict/peso`, {
    animal_id: animalId,
    meses: meses,
  });

export const getConsumoPrediccion = ({ corral_id, dias }) =>
  axios.post(`${API_PYTHON}/predict/consumo`, {
    corral_id,
    dias,
  });

