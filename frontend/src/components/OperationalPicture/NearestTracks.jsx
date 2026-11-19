import React from "react";
import {useState, useEffect} from 'react';
import '../../styles/App.css'

function NearestTracks() {
    const [tracks, setTracks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetch('/api/tracks/nearest')
            .then((res) => {
                if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);
                return res.json();
            })
            .then((data) => {
                setTracks(data);
                setLoading(false);
            })
            .catch((err) => {
                setError(err.message);
                setLoading(false);
            });
    }, []);

    if (loading) return <p>Loading data...</p>;
    if (error) return <p>Error: {error}</p>;
    if (tracks.length < 1)
        return (
            <div className="dashboard-panel">
                <div className="table-header">
                    <h2>Current Track List</h2>
                </div>
                <p>There is currently no tracks.</p>
            </div>
        );

    return (
        <div className="dashboard-panel">
            <div className="dashboard-scroll" style={{maxHeight: "350px"}}>
                <div className="table-header">
                    <h2>Nearest Sources</h2>
                </div>
                <table>
                    <thead>
                    <tr>
                        <th>Source ID</th>
                        <th>Nearest Source ID</th>
                        <th>Distance in Km</th>
                    </tr>
                    </thead>
                    <tbody>
                    {tracks.map((track) => (
                        <tr key={track.sourceId}>
                            <td>{track.sourceId}</td>
                            <td>{track.nearestSourceId}</td>
                            <td>{track.distanceKm}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default NearestTracks;