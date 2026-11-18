
import '../../styles/App.css';
import { useEffect, useState } from 'react';

export default function DashboardStats() {

    const [tracks, setTracks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {

        const fetchTracks = async () => {
            try {
                const response = await fetch('/api/tracks/list');

                if (!response.ok) {
                    throw new Error(`HTTP Error: ${response.status}`);
                }

                const data = await response.json();
                setTracks(data);

            } catch (error) {
                setError(error.message);

            } finally {
                setLoading(false);
            }
        };

        fetchTracks();

    }, []);

    // TRACK STATISTICS
    const totalTracks = tracks.length;

    const activeTracks = tracks.filter(
        track => track.trackStatus?.toUpperCase() === 'ACTIVE'
    ).length;

    const inactiveTracks = tracks.filter(
        track => track.trackStatus?.toUpperCase() === 'INACTIVE'
    ).length;

    const unknownTracks = tracks.filter(
        track => track.identityType?.toUpperCase() === 'UNKNOWN'
    ).length;

    // STATISTICS DATA
    const stats = [
        {
            id: 'total',
            title: 'Total Tracks',
            value: totalTracks,
            color: '#3b82f6'
        },
        {
            id: 'active',
            title: 'Active Tracks',
            value: activeTracks,
            color: '#22c55e'
        },
        {
            id: 'unknown',
            title: 'Unknown Identity',
            value: unknownTracks,
            color: '#a855f7'
        },
        {
            id: 'inactive',
            title: 'Inactive Tracks',
            value: inactiveTracks,
            color: '#ef4444'
        }
    ];

    if (loading) {
        return (
            <div className="stats-message">
                Loading statistics...
            </div>
        );
    }

    if (error) {
        return (
            <div className="stats-message stats-error">
                Unable to load statistics: {error}
            </div>
        );
    }

    return (
        <div className="stats-grid">

            {stats.map((stat) => (

                <div
                    className="stat-card"
                    key={stat.id}
                    style={{
                        '--stat-color': stat.color
                    }}
                >

                    <div className="stat-info">

                        <h3>{stat.title}</h3>

                        <p>{stat.value}</p>

                    </div>

                </div>

            ))}

        </div>
    );
}
