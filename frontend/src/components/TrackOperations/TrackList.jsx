import Card from './Card.jsx'
import { useEffect, useState } from 'react'

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

                <div className="table-section">
                    <div className="table-scroll">

                        <table className="track-table">

                            <thead>
                            <tr>
                                <th>Source ID</th>
                                <th>Sequence Number</th>
                                <th>Track Number</th>
                                <th>Latitude</th>
                                <th>Longitude</th>
                                <th>Speed</th>
                                <th>Identity</th>
                                <th>Status</th>
                                <th>Created Time</th>
                                <th>Received Time</th>
                                <th>Path ID</th>
                            </tr>
                            </thead>

                            <tbody>
                            {tracks.map(track => (
                                <tr key={track.trackId}>
                                    <td> {track.sourceId}</td>
                                    <td> {track.sequenceNumber}</td>
                                    <td> {track.trackNumber}</td>
                                    <td> {track.latitude}</td>
                                    <td> {track.longitude}</td>
                                    <td> {track.speed}</td>
                                    <td>{track.identityType}</td>
                                    <td>{track.trackStatus}</td>
                                    <td>{track.createdTime}</td>
                                    <td>{track.receiveTime}</td>
                                    <td>{track.pathId}</td>
                                </tr>

                            ))}

                            </tbody>

                        </table>

                    </div>
                </div>
            )}

        </Card>
    )
}