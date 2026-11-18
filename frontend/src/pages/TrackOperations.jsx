
import React, {useCallback, useEffect} from 'react'
import '../styles/App.css'
import {Grid} from "@mui/material";
import {useState} from "react";
import TrackSearch from '../components/TrackOperations/TrackSearch.jsx'
import TrackFilter from '../components/TrackOperations/TrackFilter'
import TrackCreate from '../components/TrackOperations/TrackCreate.jsx'
import TrackDelete from '../components/TrackOperations/TrackDelete.jsx'
import TrackHistory from '../components/TrackOperations/TrackHistory.jsx'
import DashboardStats from '../components/TrackOperations/DashboardStats.jsx'
import TrackList from '../components/TrackOperations/TrackList.jsx'
import TrackReplay from "../components/TrackOperations/TrackReplay.jsx";
import CurrentMap from "../components/OperationalPicture/CurrentMap.jsx";

function TrackDashboard() {


    const [activeOperation, setActiveOperation] = useState(null);
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
                            Track Monitoring System
                        </h1>
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

                <Grid container spacing={3}>
                    {/* Row 1 */}
                    <Grid size={12}>
                        <DashboardStats />
                    </Grid>

                    {/* Row 2 */}
                    <Grid size={12}>
                        <Grid container spacing={3}>
                            <Grid size={{xs: 12, md: 6}}>
                                <section className="dashboard-panel">
                                    <TrackReplay />
                                </section>
                            </Grid>
                            <Grid size={{xs: 12, md: 6}}>
                                <Grid container spacing={3}>
                                    <Grid size={{sm: 6, xs: 12, md: 6, lg: 12}}>
                                        <section className="table-section">
                                            <TrackList />
                                        </section>
                                    </Grid>
                                    <Grid size={{sm: 6, xs: 12, md: 6, lg: 12}}>
                                        <section className="dashboard-panel">

                                            <h2>Track Operations</h2>

                                            <Grid container spacing={2}    sx={{
                                                display: "flex",
                                                justifyContent: "center",
                                                alignItems: "center",
                                            }}>

                                                <Grid size={{ xs: 12, sm: 6, md: 2.2 }}>
                                                    <button
                                                        className="operation-button"
                                                        style={{fontSize: "12px"}}
                                                        onClick={() => setActiveOperation('search')}
                                                    >
                                                        Search Tracks
                                                    </button>
                                                </Grid>

                                                <Grid size={{ xs: 12, sm: 6, md: 2.2 }}>
                                                    <button
                                                        className="operation-button"
                                                        style={{fontSize: "12px"}}
                                                        onClick={() => setActiveOperation('filter')}
                                                    >
                                                        Filter Tracks
                                                    </button>
                                                </Grid>

                                                <Grid size={{ xs: 12, sm: 6, md: 2.2 }}>
                                                    <button
                                                        className="operation-button"
                                                        style={{fontSize: "12px"}}
                                                        onClick={() => setActiveOperation('create')}
                                                    >
                                                        Create Track
                                                    </button>
                                                </Grid>

                                                <Grid size={{ xs: 12, sm: 6, md: 2.2 }}>
                                                    <button
                                                        className="operation-button"
                                                        style={{fontSize: "12px"}}
                                                        onClick={() => setActiveOperation('delete')}
                                                    >
                                                        Delete Track
                                                    </button>
                                                </Grid>

                                                <Grid size={{ xs: 12, sm: 6, md: 2.2}}>
                                                    <button
                                                        className="operation-button"
                                                        style={{fontSize: "12px"}}
                                                        onClick={() => setActiveOperation('history')}
                                                    >
                                                        Track History
                                                    </button>
                                                </Grid>

                                            </Grid>

                                            {/* SELECTED OPERATION */}
                                            {activeOperation && (
                                                <div className="operation-content"  >

                                                    <button
                                                        className="back-button"
                                                        onClick={() => setActiveOperation(null)}
                                                    >
                                                        ← Back to Operations
                                                    </button>

                                                    {activeOperation === 'search' && <TrackSearch />}
                                                    {activeOperation === 'filter' && <TrackFilter />}
                                                    {activeOperation === 'create' && <TrackCreate />}
                                                    {activeOperation === 'delete' && <TrackDelete />}
                                                    {activeOperation === 'history' && <TrackHistory />}

                                                </div>
                                            )}

                                        </section>
                                    </Grid>
                                </Grid>
                            </Grid>
                        </Grid>
                    </Grid>


                </Grid>


{/*
                 DASHBOARD STATISTICS
                <Grid container spacing={3} >
                    <Grid size={12}>
                        <DashboardStats />
                    </Grid>

                     TRACK OPERATIONS
                    <Grid size={12} >
                        <section className="dashboard-panel">

                            <h2>Track Operations</h2>

                            <Grid container spacing={2}    sx={{
                                display: "flex",
                                justifyContent: "center",
                                alignItems: "center",
                            }}>

                                <Grid size={{ xs: 12, sm: 6, md: 2.2 }}>
                                    <button
                                        className="operation-button"
                                        onClick={() => setActiveOperation('search')}
                                    >
                                        Search Tracks
                                    </button>
                                </Grid>

                                <Grid size={{ xs: 12, sm: 6, md: 2.2 }}>
                                    <button
                                        className="operation-button"
                                        onClick={() => setActiveOperation('filter')}
                                    >
                                        Filter Tracks
                                    </button>
                                </Grid>

                                <Grid size={{ xs: 12, sm: 6, md: 2.2 }}>
                                    <button
                                        className="operation-button"
                                        onClick={() => setActiveOperation('create')}
                                    >
                                        Create Track
                                    </button>
                                </Grid>

                                <Grid size={{ xs: 12, sm: 6, md: 2.2 }}>
                                    <button
                                        className="operation-button"
                                        onClick={() => setActiveOperation('delete')}
                                    >
                                        Delete Track
                                    </button>
                                </Grid>

                                <Grid size={{ xs: 12, sm: 6, md: 2.2}}>
                                    <button
                                        className="operation-button"
                                        onClick={() => setActiveOperation('history')}
                                    >
                                        Track History
                                    </button>
                                </Grid>

                            </Grid>

                             SELECTED OPERATION
                            {activeOperation && (
                                <div className="operation-content"  >

                                    <button
                                        className="back-button"
                                        onClick={() => setActiveOperation(null)}
                                    >
                                        ← Back to Operations
                                    </button>

                                    {activeOperation === 'search' && <TrackSearch />}
                                    {activeOperation === 'filter' && <TrackFilter />}
                                    {activeOperation === 'create' && <TrackCreate />}
                                    {activeOperation === 'delete' && <TrackDelete />}
                                    {activeOperation === 'history' && <TrackHistory />}

                                </div>
                            )}

                        </section>
                    </Grid>

                     TRACK REPLAY
                    <Grid size={12}>
                        <section className="dashboard-panel">
                            <TrackReplay />
                        </section>
                    </Grid>

                     ALL TRACKS
                    <Grid size={12}>
                        <section className="table-section">
                            <TrackList />
                        </section>
                    </Grid>

                </Grid>*/}
            </main>
        </div>
    );
}

export default TrackDashboard;
