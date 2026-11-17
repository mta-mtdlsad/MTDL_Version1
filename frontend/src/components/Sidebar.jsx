import {NavLink} from "react-router-dom";
import "../styles/App.css";
import "../styles/Sidebar.css";

export default function Sidebar() {
    return (
        <aside className="sidebar">
            <h2 className="sidebar-title">MTDL-SAD</h2>
        <nav className="sidebar-nav">
            <NavLink to="/" end
                     className={({isActive}) => `sidebar-link ${isActive ? "active" : ""}`}>
                Operational Picture
            </NavLink>
            <NavLink to="/dashboard2"
                     className={({isActive}) => `sidebar-link ${isActive ? "active" : ""}`}>
                Track Operations</NavLink>
            <NavLink to="/dashboard3"
                     className={({isActive}) => `sidebar-link ${isActive ? "active" : ""}`}>
                Alerts and Source Health
            </NavLink>
        </nav>
    </aside>
    );
}
