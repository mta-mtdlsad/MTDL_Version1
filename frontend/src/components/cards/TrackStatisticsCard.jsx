import React from 'react';
import Grid from '@mui/material/Grid';
export default function TrackStatisticsCard({stats}) {
    const total = stats?.totalTracks ?? 0;
    const identities = [
        {
            label: 'Friend',
            value: stats?.friendCount ?? 0,
            color: 'var(--friend)'
        },
        {
            label: 'Hostile',
            value: stats?.hostileCount ?? 0,
            color: 'var(--hostile)'
        },
        {
            label: 'Neutral',
            value: stats?.neutralCount ?? 0,
            color: 'var(--neutral)'
        },
        {
            label: 'Unknown',
            value: stats?.unknownCount ?? 0,
            color: 'var(--unknown)'
        }
    ];
    const statuses = [
        {
            label: 'Active',
            value: stats?.activeCount ?? 0,
            color: 'var(--friend)'
        },
        {
            label: 'Inactive',
            value: stats?.inactiveCount ?? 0,
            color: 'var(--hostile)'
        }
    ];
    // Display identity and status bars
    const renderBars = rows => rows.map(row => (
        <div className="stat-bar-item" key={row.label}>
            <div className="stat-bar-label">
                <span>{row.label}</span>
                <strong style={{color: row.color}}>
                    {row.value}
                </strong>
            </div>
            <div className="stat-bar-track">
                <div
                    className="stat-bar-fill"
                    style={{
                        width: `${
                            total > 0
                                ? Math.min(
                                    100,
                                    row.value / total * 100
                                )
                                : 0
                        }%`,
                        background: row.color
                    }}
                />
            </div>
        </div>
    ));
    return (
        <section
            className="dashboard-panel"
            aria-label="Aggregate Track Counts"
            style={{height: '100%'}}
        >
            <h2>Aggregate Track Counts</h2>
            {/* TOTAL TRACKS */}
            <div
                className="total-tracks-card"
                style={{
                    padding: 14,
                    marginBottom: 18
                }}
            >
                <h2 style={{margin: 0}}>
                    Total Tracks
                </h2>
                <div className="total-tracks-value">
                    {total}
                </div>
            </div>
            <Grid container spacing={3}>
                {/* IDENTITY DISTRIBUTION */}
                <Grid size={{xs: 12, sm: 6, lg: 12}}>
                    <h3 style={{fontSize: 15}}>
                        Identity Distribution
                    </h3>
                    <div className="stat-bars">
                        {renderBars(identities)}
                    </div>
                </Grid>
                {/* TRACK STATUS */}
                <Grid size={{xs: 12, sm: 6, lg: 12}}>
                    <h3 style={{fontSize: 15}}>
                        Track Status
                    </h3>
                    <div className="stat-bars">
                        {renderBars(statuses)}
                    </div>
                </Grid>
            </Grid>
        </section>
    );
}