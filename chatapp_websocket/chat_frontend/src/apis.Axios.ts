import axios from "axios";

const Axios = axios.create({
    baseURL: "http://localhost:8080/api/v1",
    timeout: 1000,
    headers: {
        "Content-Type": "application/json",
    },
});

export default Axios;
