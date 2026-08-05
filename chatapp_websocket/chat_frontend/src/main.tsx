import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { BrowserRouter, Route, Routes } from "react-router";
import { ToastContainer } from "react-toastify";
import JoinCreateChat from "./components/JoinCreateChat";
import ChatPage from "./components/ChatPage";

createRoot(document.getElementById("root")!).render(
    <StrictMode>
        <BrowserRouter>
            <Routes>
                <Route index path="/" element={<JoinCreateChat />} />
                <Route path="/chat" element={<ChatPage />} />
            </Routes>
        </BrowserRouter>
        <ToastContainer />
    </StrictMode>,
);
