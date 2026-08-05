import { createContext, useContext, useState, type ReactNode } from "react";

type ChatContextType = {
    roomId: string;
    currentUser: string;
    connected: boolean;
    setRoomId: React.Dispatch<React.SetStateAction<string>>;
    setCurrentUser: React.Dispatch<React.SetStateAction<string>>;
    setConnected: React.Dispatch<React.SetStateAction<boolean>>;
};
type ChatProviderProps = {
    children: ReactNode;
};

const ChatContext = createContext<ChatContextType | undefined>(undefined);

export const ChatProvider = ({ children }: ChatProviderProps) => {
    const [roomId, setRoomId] = useState<string>("");
    const [currentUser, setCurrentUser] = useState<string>("");
    const [connected, setConnected] = useState<boolean>(false);
    return (
        <ChatContext.Provider
            value={{
                roomId,
                currentUser,
                connected,
                setRoomId,
                setCurrentUser,
                setConnected,
            }}
        >
            {children}
        </ChatContext.Provider>
    );
};

const useChatContext = (): ChatContextType => {
    const context = useContext(ChatContext);

    if (!context) {
        throw new Error("useChatContext must be used within a ChatProvider");
    }
    return context;
};

export default useChatContext;
