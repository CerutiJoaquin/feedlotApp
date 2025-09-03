
import axios from "axios";
const API = "/api/auth";

export const register = (payload) => axios.post(`${API}/register`, payload);
export const login = (email, contrasenia) => axios.post(`${API}/login`, { email, contrasenia });
