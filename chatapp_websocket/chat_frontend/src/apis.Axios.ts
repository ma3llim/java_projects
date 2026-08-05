import axios from "axios";
export const BASE_URL = "http://localhost:8080/api/v1";

const Axios = axios.create({
    baseURL: BASE_URL,
    timeout: 1000,
    headers: {
        "Content-Type": "application/json",
    },
});

export default Axios;
