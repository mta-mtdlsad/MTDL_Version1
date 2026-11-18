
import React from 'react'
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

function TrackDashboard() {


    const [activeOperation, setActiveOperation] = useState(null);


    return (
        <div className="app-container">
            <main className="app-content">

                {/* HEADER */}
                <header className="app-content-header">
                    <div style={{ textAlign: "center", width: "100%" }}>
                        <h1 className="app-content-headerText">
                            <br/>
                            Track Monitoring System
                            <br/><br/>
                        </h1>


                    </div>
                </header>

                {/* DASHBOARD STATISTICS */}
                <Grid container spacing={3} >
                    <Grid size={12}>
                        <DashboardStats />
                    </Grid>

                    {/* TRACK OPERATIONS */}
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

                    {/* TRACK REPLAY */}
                    <Grid size={12}>
                        <section className="dashboard-panel">
                            <TrackReplay />
                        </section>
                    </Grid>

                    {/* ALL TRACKS */}
                    <Grid size={12}>
                        <section className="table-section">
                            <TrackList />
                        </section>
                    </Grid>

                </Grid>
            </main>
        </div>
    );
}

export default TrackDashboard;
