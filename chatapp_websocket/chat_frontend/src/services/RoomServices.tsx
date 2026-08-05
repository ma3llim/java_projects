import Axios from "../apis.Axios";

const createRoom = async (roomDetails: IDetails) => {
    const response = await Axios.post("/rooms", roomDetails);
    return response.data;
};

const joinChat = async (roomId: string) => {
    const response = await Axios.get(`/rooms/${roomId}`);
    return response.data;
};

const getMessages = async (rooId: string) => {
    const response = await Axios.get(`/rooms/${rooId}/messages`);
    return response.data;
};

export { createRoom, joinChat, getMessages };
