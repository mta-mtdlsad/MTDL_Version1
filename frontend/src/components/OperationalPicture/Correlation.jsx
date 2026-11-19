import React, { useState, useEffect } from "react";
import "../../styles/App.css";
import { identityColors } from "./CurrentMap.jsx";

function Correlation() {
    // 1. Track the selected dropdown key (string) separately from the array of tracks
    const [selectedGroup, setSelectedGroup] = useState("");
    // 2. Initialize groups as an empty object {} to match your Java Map<Track, List<Track>>
    const [groups, setGroups] = useState({});
    // 3. Declare loading and error states so fetch() doesn't throw a ReferenceError
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetch("/api/analytics/correlations")
            .then((res) => {
                if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);
                return res.json();
            })
            .then((data) => {
                setGroups(data || {});
                setLoading(false);
            })
            .catch((err) => {
                setError(err.message);
                setLoading(false);
            });
    }, []);

    if (loading) {
        return (
            <section className="dashboard-panel">
                <p>Loading correlation groups...</p>
            </section>
        );
    }

    if (error) {
        return (
            <section className="dashboard-panel">
                <p>Error loading correlations: {error}</p>
            </section>
        );
    }

    // 4. Check Object.keys(groups).length instead of groups.length
    const groupKeys = Object.keys(groups);
    if (groupKeys.length === 0) {
        return (
            <section className="dashboard-panel">
                <div className="table-header">
                    <h2>Correlation Group Information</h2>
                </div>
                <p>There are currently no tracks.</p>
            </section>
        );
    }

    // 5. Look up the List<Track> array for the currently selected key
    const displayedTracks = groups[selectedGroup] || [];

    const getTrackId = (trackKey) => {
        // Matches trackId=123, trackId="123", or id=123 inside the serialized key string
        const match = trackKey.match(/(?:trackId|id)=['"]?([^,'")\s}]+)['"]?/i);
        return match ? match[1] : trackKey;
    };

    return (
        <section className="dashboard-panel">
            <div className="dashboard-scroll">
                <div className="table-header">
                    <h2>Select Track Observation for Correlation</h2>
                </div>
                <label htmlFor="group">Choose an Observation: </label>
                <select
                    id="group"
                    value={selectedGroup}
                    onChange={(e) => setSelectedGroup(e.target.value)}
                >
                    <option value="" disabled>Select an observation...</option>
                    {groupKeys.map((trackKey) => (
                        <option key={trackKey} value={trackKey}>
                            Observation {getTrackId(trackKey)}
                        </option>
                    ))}
                </select>
                <br /><br />

                <div className="table-header">
                    <h2>Correlated Track Observations</h2>
                </div>
                <table>
                    <thead>
                    <tr>
                        <th>Track ID</th>
                        <th>Source ID</th>
                        <th>Sequence</th>
                        <th>Track Number</th>
                        <th>Track Type</th>
                        <th>Latitude</th>
                        <th>Longitude</th>
                        <th>Speed</th>
                        <th>Identity Type</th>
                        <th>Status</th>
                        <th>Created Time</th>
                    </tr>
                    </thead>

                    <tbody>
                    {displayedTracks.length === 0 ? (
                        <tr>
                            <td colSpan={11} style={{ textAlign: "center", padding: "1rem" }}>
                                Select an observation group above to view tracks.
                            </td>
                        </tr>
                    ) : (
                        displayedTracks.map((track) => (
                            <tr key={track.trackId}>
                                <td>{track.trackId}</td>
                                <td>{track.sourceId}</td>
                                <td>{track.sequenceNumber}</td>
                                <td>{track.trackNumber}</td>
                                <td>{track.trackType ?? "N/A"}</td>
                                <td>{track.latitude}</td>
                                <td>{track.longitude}</td>
                                <td>{track.speed}</td>
                                {/* IDENTITY */}
                                <td>
                                <span
                                    className="identity-badge"
                                    style={{
                                        color:
                                            identityColors[track.identityType],
                                        backgroundColor:
                                            identityColors[track.identityType] +
                                            '22'
                                    }}
                                >
                                    {track.identityType}
                                </span>
                            </td>
                            {/* STATUS */}
                            <td>
                                <span
                                className={
                                    'track-status ' +
                                    (track.trackStatus === 'ACTIVE'
                                    ? 'active'
                                    : 'inactive')
                                }
                                >
                                    {track.trackStatus}
                                </span>
                            </td>
                        <td>{track.createdTime}</td>
                    </tr>
                )))}
            </tbody>
        </table>
    </div>
</section>
);
}

export default Correlation;
