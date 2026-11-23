import React, {useEffect, useState, useCallback} from "react";
import "../../styles/App.css";
import {PlatformBadge} from "../TrackBadges.jsx";
function NearestTracks() {
    const [currentTracks, setCurrentTracks] = useState([]);
    const [nearestTracks, setNearestTracks] = useState([]);
    const [selectedTrack, setSelectedTrack] = useState("ALL");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    // Fetch current tracks for dropdown
    const fetchCurrentTracks = useCallback(async () => {
        const response = await fetch("/api/tracks/current");
        if (!response.ok) {
            throw new Error(`Failed to load current tracks: ${response.status}`);
        }
        const data = await response.json();
        setCurrentTracks(
            Array.isArray(data) ?
                data.sort((a, b) => a.trackNumber.localeCompare(b.trackNumber)) :
                []
        );
    }, []);
    // Fetch nearest tracks
    const fetchNearestTracks = useCallback(async () => {
        const url = selectedTrack === "ALL"
            ? "/api/tracks/nearest-unique"
            : `/api/tracks/nearest-unique?trackId=${selectedTrack}`;
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Failed to load nearest tracks: ${response.status}`);
        }
        const data = await response.json();
        setNearestTracks(Array.isArray(data) ? data : []);
    }, [selectedTrack]);
    // Load data
    useEffect(() => {
        let active = true;
        async function loadData() {
            setLoading(true);
            setError(null);
            try {
                await Promise.all([
                    fetchCurrentTracks(),
                    fetchNearestTracks()
                ]);
            } catch (err) {
                if (active) setError(err.message);
            } finally {
                if (active) setLoading(false);
            }
        }
        loadData();
        return () => {
            active = false;
        };
    }, [fetchCurrentTracks, fetchNearestTracks]);
    return (
        <div className="dashboard-panel">
            <div className="dashboard-scroll" style={{maxHeight: "300px"}}>
                <div className="table-header">
                    <h2>Nearest Current Tracks</h2>
                    {/* TRACK SELECTION */}
                    <div className="header-actions">
                        <label htmlFor="track-select">
                            Select Track:
                        </label>
                        <select
                            id="track-select"
                            value={selectedTrack}
                            onChange={e => setSelectedTrack(e.target.value)}
                        >
                            <option value="ALL">All Tracks</option>
                            {currentTracks.map(track => (
                                <option
                                    key={track.trackId}
                                    value={track.trackId}
                                >
                                    {track.trackNumber} ({track.sourceId})
                                </option>
                            ))}
                        </select>
                    </div>
                </div>
                {loading ? (
                    <p>Loading nearest tracks...</p>
                ) : error ? (
                    <p className="error-message">{error}</p>
                ) : nearestTracks.length === 0 ? (
                    <p>No nearest tracks available.</p>
                ) : (
                        <table>
                            <thead>
                            <tr>
                                <th>Track Number</th>
                                <th>Track Type</th>
                                <th>Nearest Track</th>
                                <th>Nearest Type</th>
                                <th>Distance</th>
                            </tr>
                            </thead>
                            <tbody>
                            {nearestTracks.map((track, index) => (
                                <tr key={`${track.trackId}-${track.nearestTrackId}-${index}`}>
                                    <td>{track.trackNumber}</td>
                                    <td><PlatformBadge value={track.trackType ?? track.platformType} /></td>
                                    <td>{track.nearestTrackNumber}</td>
                                    <td><PlatformBadge value={track.nearestTrackType ?? track.platformType} /></td>
                                    <td>{track.distanceKm}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                )}
            </div>
        </div>
    );
}
export default NearestTracks;