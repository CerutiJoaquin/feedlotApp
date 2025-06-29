import axios from 'axios'

const API = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api'
})

export const login = async (credentials) => {
  const { data } = await API.post('/auth/login', credentials)
  localStorage.setItem('token', data.token)
  return data
}
