import Axios from "../apis.Axios";

const createRoom = async (roomDetails: IDetails) => {
    const response = await Axios.post("/rooms", roomDetails);
    return response.data;
};

export { createRoom };
