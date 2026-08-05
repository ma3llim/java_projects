import { useEffect, useRef, useState } from "react";
import { MdAttachFile, MdSend } from "react-icons/md";
import useChatContext from "../context/ChatContext";
import { useNavigate } from "react-router";
import SockJS from "sockjs-client";
import { BASE_URL } from "../apis.Axios";
import { CompatClient, Stomp } from "@stomp/stompjs";
import { toast } from "react-toastify";
import { getMessages } from "../services/RoomServices";

interface Message {
    content: string;
    sender: string;
    timeStamp?: string;
    avatar?: string;
    status?: "sent" | "delivered" | "read";
}
const ChatPage = () => {
    const { roomId, connected, currentUser } = useChatContext();
    const [messages, setMessages] = useState<Message[]>([]);
    const [input, setInput] = useState<string>("");
    const stompClient = useRef<CompatClient | null>(null);
    const inputRef = useRef(null);
    const chatBoxRef = useRef<HTMLElement | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (!connected) {
            navigate("/");
        }
    }, [connected]);

    const connectWebSocket = () => {
        if (!roomId) return;

        // SockJS
        const sock = new SockJS(`${BASE_URL}/chat`);
        const client = Stomp.over(sock);

        stompClient.current = client;

        client.connect({}, () => {
            toast.success("Connected to chat!");

            client.subscribe(`/topic/room/${roomId}`, (message: any) => {
                const newMessage = JSON.parse(message.body);
                setMessages((prev) => [...prev, newMessage]);
            });
        });
    };

    useEffect(() => {
        connectWebSocket();
        if (!roomId) return;

        chatMessages(roomId);
        return () => {
            stompClient.current?.disconnect(() => {
                console.info("Disconnected");
            });
        };
    }, [roomId]);

    // Sending Message
    const sendMessage = async () => {
        if (!stompClient.current || !connected || !input.trim()) return;

        const message = {
            sender: currentUser,
            content: input,
        };

        stompClient.current?.send(`/app/sendMessage/${roomId}`, {}, JSON.stringify(message));

        setInput("");
    };

    // loading message
    const chatMessages = async (rooId: string) => {
        try {
            const chatMessgesResult = await getMessages(rooId);
            setMessages((prev) => [...prev, ...chatMessgesResult]);
        } catch (error: any) {
            console.log(error.data);
        }
    };

    // scroll Down
    useEffect(() => {
        if (chatBoxRef.current) {
            chatBoxRef.current.scroll({
                top: chatBoxRef.current.scrollHeight,
                behavior: "smooth",
            });
        }
    }, [messages]);

    return (
        <div className="h-screen flex flex-col dark:bg-gray-800">
            {/* Header - Responsive */}
            <header className="dark:border-gray-700 fixed w-full dark:bg-gray-900 py-3 sm:py-4 md:py-5 shadow flex flex-wrap justify-between sm:justify-around items-center px-3 sm:px-6 md:px-8 z-10">
                {/* Room name container */}
                <div className="flex-1 sm:flex-none">
                    <h1 className="text-sm sm:text-base md:text-xl font-semibold truncate max-w-[120px] sm:max-w-none">
                        Room :<span>{roomId}</span>
                    </h1>
                </div>

                {/* Username container - hidden on mobile */}
                <div className="hidden sm:block flex-1 sm:flex-none text-center">
                    <h1 className="text-sm sm:text-base md:text-xl font-semibold">
                        User : <span>{currentUser}</span>
                    </h1>
                </div>

                {/* Leave room button */}
                <div className="flex-none">
                    <button
                        // onClick={handleLogout}
                        className="dark:bg-red-500 dark:hover:bg-red-700 px-2 sm:px-3 py-1.5 sm:py-2 rounded-full text-xs sm:text-sm transition-colors duration-200"
                    >
                        Leave Room
                    </button>
                </div>
            </header>

            {/* Main chat area */}
            <main
                ref={chatBoxRef}
                className="flex-1 pt-16 sm:pt-20 md:pt-24 pb-24 sm:pb-28 px-2 sm:px-4 md:px-8 lg:px-16 w-full max-w-4xl mx-auto overflow-y-auto scrollbar-thin scrollbar-thumb-gray-600 scrollbar-track-transparent"
            >
                {messages.map((message, index) => (
                    <div key={index} className={`flex ${message.sender === currentUser ? "justify-end" : "justify-start"} animate-fadeIn mb-2 sm:mb-3`}>
                        <div className={`my-1 sm:my-2 ${message.sender === currentUser ? "bg-green-800" : "bg-gray-700"} p-2 sm:p-3 max-w-[85%] sm:max-w-[70%] md:max-w-[60%] rounded-lg shadow`}>
                            <div className="flex flex-row gap-2 sm:gap-3 items-start">
                                <div className="flex flex-col gap-1 min-w-0 flex-1">
                                    <p className="text-xs sm:text-sm font-bold break-words">{message.sender}</p>
                                    <p className="text-sm sm:text-base break-words">{message.content}</p>
                                    {/* <p className="text-[10px] sm:text-xs text-gray-400">{timeAgo(message.timeStamp)}</p> */}
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </main>

            {/* Input message container - Responsive */}
            <div className="fixed bottom-2 sm:bottom-3 md:bottom-4 w-full px-2 sm:px-4 md:px-6">
                <div className="h-12 sm:h-14 md:h-16 gap-2 sm:gap-3 md:gap-4 flex items-center justify-between rounded-full w-full sm:w-[85%] md:w-[70%] lg:w-1/2 mx-auto dark:bg-gray-900 px-2 sm:px-3 md:px-4 shadow-lg border dark:border-gray-700">
                    <input
                        value={input}
                        onChange={(e) => {
                            setInput(e.target.value);
                        }}
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                sendMessage();
                            }
                        }}
                        type="text"
                        placeholder="Type your message..."
                        className="flex-1 dark:bg-transparent px-2 sm:px-3 py-1 sm:py-2 rounded-full h-full focus:outline-none text-sm sm:text-base placeholder:text-gray-400 dark:placeholder:text-gray-500"
                    />

                    <div className="flex gap-1 sm:gap-2 flex-shrink-0">
                        <button className="dark:bg-purple-600 hover:dark:bg-purple-700 h-8 w-8 sm:h-9 sm:w-9 md:h-10 md:w-10 flex justify-center items-center rounded-full transition-colors duration-200">
                            <MdAttachFile size={16} className="sm:text-lg md:text-xl" />
                        </button>
                        <button
                            onClick={sendMessage}
                            className="dark:bg-green-600 hover:dark:bg-green-700 h-8 w-8 sm:h-9 sm:w-9 md:h-10 md:w-10 flex justify-center items-center rounded-full transition-colors duration-200"
                        >
                            <MdSend size={16} className="sm:text-lg md:text-xl" />
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ChatPage;
