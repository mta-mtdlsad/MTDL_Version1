import React from 'react';
import '../../styles/App.css'
import {identityColors} from "./CurrentMap.jsx";


function CurrentTracks({filteredTracks = []}) {
    {/* TRACK TABLE */
    }
    if (filteredTracks.length < 1)
        return (
            <section className="dashboard-panel">
                <div className="table-header">
                    <h2>Current Track List</h2>
                </div>
                <p>There is currently no tracks.</p>
            </section>
        );
    return (
        <section className="dashboard-panel">
            <div className="dashboard-scroll">
                <div className="table-header">
                    <h2>Current Track List</h2>
                </div>
                <table>
                    <thead>
                    <tr>
                        <th>Track ID</th>
                        <th>Source ID</th>
                        <th>Sequence</th>
                        <th>Track Number</th>
                        <th>Latitude</th>
                        <th>Longitude</th>
                        <th>Speed</th>
                        <th>Identity Type</th>
                        <th>Status</th>
                        <th>Created Time</th>
                        <th>Recieved Time</th>
                    </tr>
                    </thead>
                    <tbody>
                    {filteredTracks.map(track => (
                        <tr key={track.trackId}>
                            <td>{track.trackId}</td>
                            <td>{track.sourceId}</td>
                            <td>{track.sequenceNumber}</td>
                            <td>{track.trackNumber}</td>
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
                            <td>{track.receiveTime}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </section>
    );
}

export default CurrentTracks;