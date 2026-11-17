import { BrowserRouter, Routes, Route } from "react-router-dom";
import OperationalPicture from "./pages/OperationalPicture";
import Sidebar from "./components/Sidebar";
import "./styles/App.css";

function App() {
    return (
        <BrowserRouter>
            <div className="app-layout">
                <Sidebar />
                <main className="main-content">
                    <Routes>
                        <Route path="/" element={<OperationalPicture />} />
                    </Routes>
                </main>
            </div>
        </BrowserRouter>
    );
}

export default App;