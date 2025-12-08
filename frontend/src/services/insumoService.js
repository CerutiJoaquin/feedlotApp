import axios from "axios";

const API_URL = "/api/insumo";



export const getAllInsumos   = () => axios.get(API_URL);
export const getInsumoById   = (id) => axios.get(`${API_URL}/${id}`);
export const createInsumo    = (data) => axios.post(API_URL, data);
export const updateInsumo    = (id, data) => axios.patch(`${API_URL}/${id}`, data);
export const deleteInsumo    = (id) => axios.delete(`${API_URL}/${id}`);


export const getInsumoByCategoria = (categoria) =>
  axios.get(API_URL, { params: { categoria: String(categoria).toUpperCase() } });


export const listMedicamentos = ({ q = "", page = 0, size = 10, sort = "nombre,asc" } = {}) =>
  axios.get(`${API_URL}/medicamentos`, { params: { q, page, size, sort } });


export const aplicarTratamiento = (payload) =>
  axios.post(`${API_URL}/aplicar`, payload);


export const getLowStockInsumos = async () => {
  const res = await axios.get(`${API_URL}/alerts/low-stock`);
  return res.data;
};