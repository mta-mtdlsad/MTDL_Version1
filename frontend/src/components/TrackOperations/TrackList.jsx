import Card from './Card.jsx'
import React, { useEffect, useState } from 'react'
import {identityColors} from "../OperationalPicture/CurrentMap.jsx";
import {PlatformBadge} from "../TrackBadges.jsx";

export default function TrackList() {

    const [tracks, setTracks] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    // Automatically fetch all tracks when component loads
    useEffect(() => {

        const fetchTracks = async () => {

            try {
                const response = await fetch('/api/tracks/list')

                if (!response.ok) {
                    throw new Error(`HTTP Error: ${response.status}`)
                }

                const data = await response.json()
                setTracks(data)

            } catch (error) {
                setError(error.message)

            } finally {
                setLoading(false)
            }
        }

        fetchTracks()

    }, [])

    return (

        <Card title="All Tracks">

            {/* loading */}
            {loading && (
                <p>Loading tracks...</p>
            )}

            {/* error */}
            {error && (
                <p className="error">
                    Unable to load tracks: {error}
                </p>
            )}

            {/* empty list*/}
            {!loading && !error && tracks.length === 0 && (
                <p>No tracks found.</p>
            )}

            {/* display table */}
            {!loading && !error && tracks.length > 0 && (

                <div className="table-scroll" style={{maxHeight: "305px"}}>

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
                        {tracks.map(track => (
                            <tr key={track.trackId}>
                                <td>{track.trackId}</td>
                                <td>{track.sourceId}</td>
                                <td>{track.sequenceNumber}</td>
                                <td>{track.trackNumber}</td>
                                <td><PlatformBadge value={track.trackType ?? track.platformType} /></td>
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
                        ))}
                        </tbody>
                    </table>

                </div>
            )}

        </Card>
    )
}