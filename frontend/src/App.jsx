import { BrowserRouter, Routes, Route } from "react-router-dom";
import OperationalPicture from "./pages/OperationalPicture";
import TrackOperations from "./pages/TrackOperations";
import AlertsAndSourceHealth from "./pages/AlertsAndSourceHealth";
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
                    <Routes>
                        <Route path="/TrackOperations" element={<TrackOperations />} />
                    </Routes>
                    <Routes>
                        <Route path="/AlertsAndSourceHealth" element={<AlertsAndSourceHealth />} />
                    </Routes>
                </main>
            </div>
        </BrowserRouter>
    );
}

export default App;