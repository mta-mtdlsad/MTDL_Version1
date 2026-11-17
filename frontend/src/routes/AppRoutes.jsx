import {Navigate, Route, Routes} from "react-router-dom";
import PortalLayout from "../layouts/PortalLayout.jsx";
import HomePage from "../pages/HomePage.jsx";
import NotFoundPage from "../pages/NotFountPage.jsx";
import TrackList from "../pages/TrackList.jsx";
import TrackSearch from "../pages/TrackSearch.jsx";
import TrackFilter from "../pages/TrackFilter.jsx";
import TrackCreate from "../pages/TrackCreate.jsx";
import TrackDelete from "../pages/TrackDelete.jsx";
import TrackHistory from "../pages/TrackHistory.jsx";

export default function AppRoutes(){
    return(
        <Routes>
            <Route path="/" element={<Navigate to="/api/tracks/" replace/>}/>
            <Route path="/api/tracks/" element={<PortalLayout />}>
                <Route index element={<HomePage />} />
                <Route path="/api/tracks/list" element={<TrackList />} />
                <Route path="/api/tracks/search" element={<TrackSearch />} />
                <Route path="/api/tracks/filter" element={<TrackFilter />} />
                <Route path="/api/tracks/create" element={<TrackCreate />} />
                <Route path="/api/tracks/delete" element={<TrackDelete />} />
                <Route path="/api/tracks/history" element={<TrackHistory />} />
                <Route path="*" element={<NotFoundPage />} />
            </Route>
        </Routes>

    )
}