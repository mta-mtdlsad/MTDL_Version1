import React from "react";
import { useState, useEffect } from 'react';
import '../App.css'

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

      return (
        <>
            <div className="table-header">
                <h2>Nearest Sources</h2>
            </div>
            <div>
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
        </>
      )
}
export default NearestTracks;