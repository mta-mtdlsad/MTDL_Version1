import React from "react";
import {useState, useEffect} from 'react';
import '../App.css'

function Correlation() {
    const [selectedMethod, setSelectedMethod] = useState("");
    const [groups, setGroups] = useState([]);

    useEffect(() => {
        fetch('/api/analytics/correlations')
            .then((res) => {
                if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);
                return res.json();
            })
            .then((data) => {
                setGroups(data);
                setLoading(false);
            })
            .catch((err) => {
                setError(err.message);
                setLoading(false);
            });
    }, []);

    return (
        <>
            {/* Render the matching UI, or a fallback prompt if nothing is selected */}
                <div className="table-header">
                    <h2>Select Correlation Group</h2>
                </div>
                <label htmlFor="group">Choose a correlation group: </label>
                <select
                    id="group"
                    value={selectedMethod}
                    onChange={(e) => setSelectedMethod(e.target.value)}
                >
                    <option value="" disabled>Select a group...</option>
                    {groups?.map((group) => (
                        <option key={group.groupId} value={group.groupId}>
                            Group {group.groupId}
                        </option>
                    ))}
                </select><br/><br/>
                <div className="table-header">
                    <h2>Correlation Group Information</h2>
                </div>
                <div>
                    <table>
                        <thead>
                        <tr>
                            <th>Group ID</th>
                            <th>Max Distance in Km</th>
                            <th>Max Time Difference in Sec</th>
                        </tr>
                        </thead>
                        <tbody>
                        {groups?.[selectedMethod - 1] && (
                            <tr>
                                <td>{groups[selectedMethod - 1]?.groupId}</td>
                                <td>{groups[selectedMethod - 1]?.maxDistanceKm}</td>
                                <td>{groups[selectedMethod - 1]?.maxTimeDiffSec}</td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
                <br/>

                <div className="table-header">
                    <h2>Correlated Tracks</h2>
                </div>
                <div>
                    <table>
                        <thead>
                        <tr>
                            <th>Track ID</th>
                            <th>Source ID</th>
                            <th>Sequence Number</th>
                            <th>Track Number</th>
                            <th>Latitude</th>
                            <th>Longitude</th>
                            <th>Speed</th>
                            <th>Identity</th>
                            <th>Status</th>
                            <th>Created Time</th>
                        </tr>
                        </thead>
                        <tbody>
                        {groups[selectedMethod - 1]?.tracks.map((track) => (
                            <tr key={track.trackId}>
                                <td>{track.trackId}</td>
                                <td>{track.sourceId}</td>
                                <td>{track.sequenceNumber}</td>
                                <td>{track.trackNumber}</td>
                                <td>{track.latitude}</td>
                                <td>{track.longitude}</td>
                                <td>{track.speed}</td>
                                <td>{track.identityType}</td>
                                <td>{track.trackStatus}</td>
                                <td>{track.createdTime}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
        </>
    );
}

export default Correlation;