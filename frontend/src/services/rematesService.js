import axios from "axios";
export async function getRemates(params = {}) {
  const { data } = await axios.get("/api/remates", { params });
  return data;
}
