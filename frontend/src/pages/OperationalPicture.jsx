import React, {useCallback, useEffect, useState} from 'react';
import Grid from '@mui/material/Grid';
import CurrentMap from '../components/OperationalPicture/CurrentMap.jsx';
import CurrentTracks from '../components/OperationalPicture/CurrentTracks.jsx';
import Correlation from '../components/OperationalPicture/Correlation.jsx';
import NearestTracks from '../components/OperationalPicture/NearestTracks.jsx';
import IdentityStatistics from '../components/OperationalPicture/IdentityStatistics.jsx';
import TrackStatus from '../components/OperationalPicture/TrackStatus.jsx';
import SimulationStatus from '../components/OperationalPicture/SimulationStatus.jsx';
import '../styles/App.css';

function OperationalPicture() {
    const [tracks, setTracks] = useState([]);
    const [error, setError] = useState('');
    const [refreshing, setRefreshing] = useState(false);
    const [lastUpdated, setLastUpdated] = useState(null);

    // Fetch latest tracks from Spring Boot
    const fetchTracks = useCallback(async (signal) => {
        const response = await fetch('/api/tracks/latest', {
            cache: 'no-store',
            signal
        });
        if (!response.ok) {
            throw new Error(
                `Failed to load tracks: HTTP ${response.status}`
            );
        }
        const data = await response.json();
        if (!Array.isArray(data)) {
            throw new Error('Invalid tracks response');
        }
        return data;
    }, []);

    // Manual refresh
    const handleRefresh = useCallback(async () => {
        setRefreshing(true);
        window.location.reload();
        setRefreshing(false);
    }, []);

    // Automatically refresh tracks every 5 seconds
    useEffect(() => {
        const controller = new AbortController();
        let active = true;

        async function loadTracks() {
            try {
                const data = await fetchTracks(controller.signal);
                if (active) {
                    setTracks(data);
                    setLastUpdated(new Date());
                    setError('');
                }
            } catch (err) {
                if (active && err.name !== 'AbortError') {
                    setError(err.message);
                }
            }
        }

        loadTracks();
        const interval = setInterval(loadTracks, 5000);
        return () => {
            active = false;
            controller.abort();
            clearInterval(interval);
        };
    }, [fetchTracks]);

    // Refresh dashboard after simulation changes
    const handleSimulationChange = () => {
        handleRefresh();
    };

    return (
        <div className="app-container">
            <main className="app-content">
                {/* HEADER */}
                <header className="app-content-header">
                    <div>
                        <h1 className="app-content-header-text">
                            Operational Picture
                        </h1>
                        <p className="page-subtitle">
                            Real-time track monitoring dashboard
                        </p>
                    </div>
                    <div className="header-actions">
                        <span className="last-updated">
                            {lastUpdated
                                ? `Updated: ${lastUpdated.toLocaleTimeString()}`
                                : 'Waiting for data'}
                        </span>
                        <button
                            className="app-content-headerButton"
                            onClick={handleRefresh}
                            disabled={refreshing}
                        >
                            {refreshing ? 'Refreshing...' : 'Refresh'}
                        </button>
                    </div>
                </header>

                {error && (<p className="error-message" role="alert">{error}</p>)}

                <Grid container spacing={3}>

                    {/* Row 1 */}
                    <Grid size={12}>
                        <Grid container spacing={3}>

                            {/* Column 1 */}
                            <Grid size={{xs: 12, md: 7}}>
                                <CurrentMap tracks={tracks}/>
                            </Grid>

                            {/* Column 2 */}
                            <Grid size={{xs: 12, md: 2}}>
                                <Grid container spacing={3}>
                                    <Grid size={{sm: 6, xs: 12, md: 6, lg: 12}}>
                                        {/* TOTAL TRACKS */}
                                        <section className="total-tracks-card">
                                            <div>
                                                <h2>Total Current Tracks</h2>
                                            </div>
                                            <div className="total-tracks-value">
                                                {tracks.length}
                                            </div>
                                        </section>
                                    </Grid>
                                    <Grid size={{sm: 6, xs: 12, md: 6, lg: 12}}>
                                        <IdentityStatistics tracks={tracks}/>
                                    </Grid>
                                    <Grid size={{sm: 6, xs: 12, md: 6, lg: 12}}>
                                        <TrackStatus tracks={tracks}/>
                                    </Grid>
                                </Grid>
                            </Grid>

                            {/* Column 3 */}
                            <Grid size={{xs: 12, md: 3}}>
                                <Grid container spacing={3}>
                                    <Grid size={{sm: 6, xs: 12, md: 6, lg: 12}}>
                                        <SimulationStatus
                                            onSimulationChange={handleSimulationChange}
                                        />
                                    </Grid>
                                    <Grid size={{sm: 6, xs: 12, md: 6, lg: 12}}>
                                        <NearestTracks/>
                                    </Grid>
                                </Grid>
                            </Grid>
                        </Grid>
                    </Grid>

                    {/* Row 2 */}
                    <Grid size={12}>
                        <Grid container spacing={3}>

                            {/* Column 1 */}
                            <Grid size={{xs: 12, md: 7}}>
                                <CurrentTracks filteredTracks={tracks}/>
                            </Grid>

                            {/* Column 2 */}
                            <Grid size={{xs: 12, md: 5}}>
                                <Correlation/>
                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>
            </main>
        </div>
    );
}

export default OperationalPicture;