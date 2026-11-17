import Card from '../components/Card.jsx'
import {useState} from "react";

export default function TrackFilter() {
    const [identity, setIdentity] = useState("");
    const [trackStatus, setTrackStatus] = useState("");
    const [minimumSpeed, setMinimumSpeed] = useState("");
    const [tracks, setTracks] = useState([]);
    const [searched, setSearched] = useState(false);
    const [loading, setLoading] = useState(false);


    async function getTracks(){

        setLoading(true);
        setSearched(false);
        setTracks([]);

        try{

            const params = new URLSearchParams({
                category: identity,
                status: trackStatus,
                minimumSpeed: minimumSpeed
            });
            const response = await fetch(
                `/api/tracks/filter?${params.toString()}`);

            if(!response.ok){
                throw new Error(`HTTP Error: ${response.status}`)
            }

            const data = await response.json();
            setTracks(data);
            setSearched(true);
            setIdentity("");
            setTrackStatus("");
            setMinimumSpeed("");

        } catch (error){
            console.log("Error:", error);
        } finally {
            setLoading(false);
        }
    }

    return (
        <Card title="Filter Tracks">
            <label>Identity Type:&ensp;
                <select
                    value={identity}
                    onChange={(event) => setIdentity(event.target.value)}>
                    <option value="">-- Select Identity --</option>
                    <option value="FRIEND">FRIEND</option>
                    <option value="NEUTRAL">NEUTRAL</option>
                    <option value="HOSTILE">HOSTILE</option>
                    <option value="UNKNOWN">UNKNOWN</option>
                </select>
            </label>
            <label>Track Status:&ensp;
                <select
                    value={trackStatus}
                    onChange={(event) => setTrackStatus(event.target.value)}>
                    <option value="">-- Select Status --</option>
                    <option value="ACTIVE">Active</option>
                    <option value="INACTIVE">Inactive</option>
                </select>
            </label>
            <label>Minimum Speed:&ensp;
                <input
                    type="number"
                    min="0"
                    step="any"
                    value={minimumSpeed}
                    onChange={(event) => setMinimumSpeed(event.target.value)}
                    placeholder="e.g.450.00"
                />
            </label>
            <button type="button" onClick={getTracks} disabled={loading}>Search</button>

            {searched && tracks.length === 0 && (
                <div>
                    <br/>
                    <p>No tracks found.</p>
                </div>
            )}
            {tracks.length > 0 && (
                <div>
                    <br/>
                    <h3>Tracks Results</h3>
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
                </div>
            )}
        </Card>
    )
}
