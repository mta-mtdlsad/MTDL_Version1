import Card from './Card.jsx'
import {useEffect, useState} from "react";

export default function TrackSearch() {
    const [trackNumber, setTrackNumber] = useState("");
    const [tracks, setTracks] = useState([]);
    const [searched, setSearched] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    async function getTracks(){
        if(!trackNumber.trim()){
            setError("Please enter a track number");
            return;
        }
        setLoading(true);
        setError("");
        setSearched(false);
        setTracks([]);

        try{
            const response = await fetch(`/api/tracks/search?trackNumber=${trackNumber}`);

            if(!response.ok){
                throw new Error(`HTTP Error: ${response.status}`)
            }

            const data = await response.json();
            setTracks(data);
            setSearched(true);

            setTrackNumber("");
        } catch (error){
            console.log("Error:", error);
            setError("Unable to fetch tracks.")
        } finally {
            setLoading(false);
        }
    }

    return (
        <Card title="Search Tracks">

            <label>Track Number: &ensp; <br/>
                <input type="text"
                       placeholder={"e.g.TRK10001"}
                       value={trackNumber}
                       onChange={(e) => setTrackNumber(e.target.value)}
                />
            </label>
            {error && <p className="error">{error}</p>}
            <button type="button" onClick={getTracks} disabled={loading}>Search</button>

            {searched && tracks.length === 0 &&  !error && (
                <div>
                    <br/>
                    <p>No tracks found.</p>
                </div>
            )}
            {tracks.length > 0 && (
                <div>
                    <br/>
                    <h3>Tracks Results</h3>
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
