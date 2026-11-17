import Card from '../components/Card.jsx'
import {useState } from 'react'

export default function TrackHistory() {

    const [endTime, setEndTime] = useState("")
    const [startTime, setStartTime] = useState("")
    const [tracks, setTracks] = useState([])
    const [loading, setLoading] = useState(false)
    const [loaded, setLoaded] = useState(false)
    const [error, setError] = useState(null)

    // method to fetch api request
    const handleGetAllHistory = async () => {

        setLoading(true)
        setError(null)
        setTracks([])

        try {

            const params = new URLSearchParams({start: startTime, end: endTime})
            const response = await fetch(`/api/tracks/history?${params.toString()}`)

            // const response = await fetch('/api/tracks/history')

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

        <Card title="Track History">
            {/*user inputs - Start Time*/}
            <label>Enter Start Time:&ensp;
                <input
                    type="datetime-local"
                    value={startTime}
                    onChange={event => setStartTime(event.target.value)}
                />
            </label>
            {/*user inputs - End Time*/}
            <label>Enter End Time:&ensp;
                <input
                    type="datetime-local"
                    value={endTime}
                    onChange={event => setEndTime(event.target.value)}
                />
            </label>
            {/*button function using method onclick + disappears once its done loading */}

            <button onClick={handleGetAllHistory} disabled={loading || !startTime || !endTime}>
                {loading ? 'Loading...' : 'Get Track History'}
            </button>

            {/*If in case of error or no tracks found */}
            {error && <p className="error">{error}</p>}

            {loaded && !error && tracks.length === 0 && (<p>No tracks found</p>)}

            {tracks.length > 0 && (
                <div>
                    <br/>
                    <h3>Tracks Results</h3>
                    <table>
                        <thead>
                        <tr>
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
                        {tracks.map(track => (
                            <tr key={track.trackId}>
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
            )}
        </Card>
    )
}