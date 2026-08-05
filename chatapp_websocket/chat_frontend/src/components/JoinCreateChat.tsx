import React, { useState } from "react";
import { toast } from "react-toastify";
import { createRoom } from "../services/RoomServices";
import useChatContext from "../context/ChatContext";
import { useNavigate } from "react-router";

const JoinCreateChat = () => {
    const [detail, setDetails] = useState<IDetails>({ roomId: "", userName: "" });
    const { roomId, currentUser, setRoomId, setCurrentUser, connected, setConnected } = useChatContext();
    const navigate = useNavigate();

    const handleFormInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setDetails({
            ...detail,
            [e.target.name]: e.target.value,
        });
    };
    const validateForm = () => {
        if (detail.roomId === "" || detail.userName === "") {
            toast.error("Invalid Input !!");
        }
        return true;
    };

    const createRoomHandler = async () => {
        if (validateForm()) {
            try {
                const result = await createRoom(detail);
                console.log(result);

                toast.success("Room created successfully!");
                setCurrentUser(detail.userName);
                setRoomId(result.roomId);
                setConnected(true);

                navigate("/chat");
            } catch (error: any) {
                console.log(error.response);
                toast.error(error.response.data);
            }
        }
    };

    const joinChat = () => {
        if (validateForm()) {
            console.log(detail);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center px-3 sm:px-4 md:px-6 py-4 sm:py-8">
            <div className="p-4 sm:p-6 md:p-8 lg:p-10 dark:border-gray-700 border w-full flex flex-col gap-4 sm:gap-5 max-w-sm sm:max-w-md md:max-w-lg mx-auto rounded dark:bg-gray-900 shadow-lg">
                <h1 className="text-lg sm:text-xl md:text-2xl font-semibold text-center">Join Room / Create Room</h1>

                {/* Name div */}
                <div>
                    <label htmlFor="name" className="block font-medium mb-1.5 sm:mb-2 text-sm sm:text-base">
                        Your name
                    </label>
                    <input
                        value={detail.userName}
                        onChange={handleFormInputChange}
                        type="text"
                        id="name"
                        name="userName"
                        placeholder="Enter your name"
                        className="w-full dark:bg-gray-600 px-3 sm:px-4 py-2 sm:py-2.5 border dark:border-gray-600 rounded-full focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm sm:text-base"
                    />
                </div>

                {/* Room ID div */}
                <div>
                    <label htmlFor="roomId" className="block font-medium mb-1.5 sm:mb-2 text-sm sm:text-base">
                        Room ID / New Room ID
                    </label>
                    <input
                        name="roomId"
                        onChange={handleFormInputChange}
                        value={detail.roomId}
                        type="text"
                        id="roomId"
                        placeholder="Enter room ID or leave blank"
                        className="w-full dark:bg-gray-600 px-3 sm:px-4 py-2 sm:py-2.5 border dark:border-gray-600 rounded-full focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm sm:text-base"
                    />
                </div>

                {/* Buttons */}
                <div className="flex flex-col sm:flex-row justify-center gap-2 sm:gap-3 mt-2 sm:mt-4">
                    <button
                        onClick={joinChat}
                        className="px-4 sm:px-5 py-2 sm:py-2.5 dark:bg-blue-500 hover:dark:bg-blue-600 active:dark:bg-blue-700 rounded-full cursor-pointer transition-colors duration-200 text-sm sm:text-base w-full sm:w-auto"
                    >
                        Join Room
                    </button>
                    <button
                        onClick={createRoomHandler}
                        className="px-4 sm:px-5 py-2 sm:py-2.5 dark:bg-orange-500 hover:dark:bg-orange-600 active:dark:bg-orange-700 rounded-full cursor-pointer transition-colors duration-200 text-sm sm:text-base w-full sm:w-auto"
                    >
                        Create Room
                    </button>
                </div>
            </div>
        </div>
    );
};

export default JoinCreateChat;
