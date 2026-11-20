import React, { useEffect, useState } from "react";
import "../../styles/App.css";

function NearestTracks() {
    const [mode, setMode] = useState("SOURCES");
    const [tracks, setTracks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const controller = new AbortController();
        const url = mode === "SOURCES"
            ? "/api/tracks/nearest"
            : `/api/tracks/nearest-tracks?type=${mode}`;

        async function loadNearest() {
            setLoading(true);
            setError(null);
            try {
                const response = await fetch(url, {
                    signal: controller.signal,
                    headers: { Accept: "application/json" }
                });
                if (!response.ok) throw new Error(`HTTP ${response.status}`);
                const data = await response.json();
                if (!Array.isArray(data)) throw new Error("Invalid nearest-track response");
                if (!controller.signal.aborted) setTracks(data);
            } catch (err) {
                if (err.name !== "AbortError") setError(err.message);
            } finally {
                if (!controller.signal.aborted) setLoading(false);
            }
        }
        loadNearest();
        return () => controller.abort();
    }, [mode]);

    const isSources = mode === "SOURCES";
    const title = isSources ? "Nearest Sources" : mode === "AIR" ? "Nearest Aircraft" : "Nearest Tracks";

    return (
        <div className="dashboard-panel">
            <div className="table-header">
                <h2>{title}</h2>
                <label htmlFor="nearest-mode">Compare: </label>
                <select id="nearest-mode" value={mode} onChange={e => setMode(e.target.value)}>
                    <option value="SOURCES">Nearest Sources</option>
                    <option value="ALL">Nearest Tracks (All Types)</option>
                    <option value="AIR">Nearest Aircraft (AIR Only)</option>
                </select>
            </div>
            {loading ? <p>Loading data...</p> : error ? <p>Error: {error}</p> : tracks.length === 0
                ? <p>No nearest {isSources ? "sources" : "tracks"} available.</p>
                : <div className="dashboard-scroll" style={{ maxHeight: "350px" }}>
                    <table>
                        <thead>
                        {isSources ? (
                            <tr><th>Source ID</th><th>Nearest Source ID</th><th>Distance in Km</th></tr>
                        ) : (
                            <tr>
                                <th>Source ID</th><th>Track Number</th><th>Track Type</th>
                                <th>Nearest Source ID</th><th>Nearest Track Number</th>
                                <th>Nearest Track Type</th><th>Distance in Km</th>
                            </tr>
                        )}
                        </thead>
                        <tbody>
                        {tracks.map((track, index) => (
                            <tr key={`${track.sourceId}-${track.trackNumber ?? "source"}-${index}`}>
                                <td>{track.sourceId}</td>
                                {!isSources && <><td>{track.trackNumber}</td><td>{track.trackType ?? "N/A"}</td></>}
                                <td>{track.nearestSourceId}</td>
                                {!isSources && <><td>{track.nearestTrackNumber}</td><td>{track.nearestTrackType ?? "N/A"}</td></>}
                                <td>{track.distanceKm}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>}
        </div>
    );
}

export default NearestTracks;