import '../../styles/App.css';
import {useEffect, useState} from 'react';
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
    const countStatus = (status) =>
        tracks.filter(
            track => track.trackStatus?.toUpperCase() === status
        ).length;
    const countIdentity = (identity) =>
        tracks.filter(
            track => track.identityType?.toUpperCase() === identity
        ).length;
    const activeTracks = countStatus('ACTIVE');
    const inactiveTracks = countStatus('INACTIVE');
    const friendTracks = countIdentity('FRIEND');
    const neutralTracks = countIdentity('NEUTRAL');
    const hostileTracks = countIdentity('HOSTILE');
    const unknownTracks = countIdentity('UNKNOWN');
    if (loading) {
        return <div className="stats-message">Loading statistics...</div>;
    }
    if (error) {
        return (
            <div className="stats-message stats-error">
                Unable to load statistics: {error}
            </div>
        );
    }
    return (
        <div className="stats-grid stats-overview">
            {/* TOTAL TRACKS */}
            <div className="stat-card stat-total">
                <div className="stat-info">
                    <h3>Total Tracks</h3>
                    <p>{totalTracks}</p>
                    <span className="stat-subtitle">

          Monitored Tracks

        </span>
                </div>
            </div>
            {/* IDENTITY DISTRIBUTION */}
            <div className="stat-card stat-unknown">
                <div className="stat-info">
                    <h3>Identity Distribution</h3>
                    <div className="stat-bars">
                        {[
                            {label: 'Friend', value: friendTracks, color: '#4ade80'},
                            {label: 'Neutral', value: neutralTracks, color: '#60a5fa'},
                            {label: 'Hostile', value: hostileTracks, color: '#f87171'},
                            {label: 'Unknown', value: unknownTracks, color: '#fbbf24'}
                        ].map((item) => (
                            <div className="stat-bar-item" key={item.label}>
                                <div className="stat-bar-label">
                                    <span>{item.label}</span>
                                    <strong style={{color: item.color}}>
                                        {item.value}
                                    </strong>
                                </div>
                                <div className="stat-bar-track">
                                    <div
                                        className="stat-bar-fill"
                                        style={{
                                            width: `${totalTracks > 0
                                                ? (item.value / totalTracks) * 100
                                                : 0}%`,
                                            background: item.color
                                        }}
                                    />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
            {/* TRACK STATUS */}
            <div className="stat-card stat-active">
                <div className="stat-info">
                    <h3>Track Status</h3>
                    <div className="stat-bars">
                        {[
                            {label: 'Active', value: activeTracks, color: '#4ade80'},
                            {label: 'Inactive', value: inactiveTracks, color: '#f87171'}
                        ].map((item) => (
                            <div className="stat-bar-item" key={item.label}>
                                <div className="stat-bar-label">
                                    <span>{item.label}</span>
                                    <strong style={{color: item.color}}>
                                        {item.value}
                                    </strong>
                                </div>
                                <div className="stat-bar-track">
                                    <div
                                        className="stat-bar-fill"
                                        style={{
                                            width: `${totalTracks > 0
                                                ? (item.value / totalTracks) * 100
                                                : 0}%`,
                                            background: item.color
                                        }}
                                    />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
}