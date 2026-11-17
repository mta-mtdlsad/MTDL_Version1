import Card from '../components/Card.jsx'
import {useState } from 'react'

export default function TrackList() {

    const [tracks, setTracks] = useState([])
    const [loading, setLoading] = useState(false)
    const [loaded, setLoaded] = useState(false)
    const [error, setError] = useState(null)

    // method to fetch api request
    const handleAllTracks = async () => {

        setLoading(true)
        setError(null)

        try {
            const response = await fetch('/api/tracks/list')

            if (!response.ok) {
                throw new Error(`HTTP Error: ${response.status}`)
            }

            const data = await response.json()
            setTracks(data)
            setLoaded(true)

        } catch (error) {setError(error.message)

        }

        finally {
            setLoading(false)
        }
    }

    return (

        <Card title="Track List">

            {/*button function using method onclick + disappears once its done loading */}
            {!loaded && (
                <button onClick={handleAllTracks}
                        disabled={loading}
                >
                    {loading ? 'Loading...' : 'Get All Tracks'}
                </button> )}

            {/*If in case of error or no tracks found */}
            {error && <p className="error">{error}</p> }

            {loaded && !error && tracks.length === 0 && (<p>No tracks found</p>) }

            {tracks.length > 0 && (
                <table>
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
            )}
        </Card>
    )
}
