import React from 'react';
import Grid from '@mui/material/Grid';
import TrackReplay from "../TrackOperations/TrackReplay.jsx";
export default function UDPAnalyticsCard({summary, analytics}) {
    const items = [
        {
            title: 'Total UDP Processed',
            value: analytics?.totalProcessed ?? 0,
            color: 'var(--accent)'
        },
        {
            title: 'Accepted',
            value: analytics?.acceptedCount ?? 0,
            color: 'var(--friend)'
        },
        {
            title: 'Total Alerts',
            value: summary?.totalAlerts ?? 0,
            color: 'var(--hostile)'
        }
    ];
    return (
        <section
            aria-label="UDP Intake"
            style={{height: '100%'}}
        >
            {/* UDP STATISTICS */}
            <Grid container spacing={2}>
                {items.map(item => (
                    <Grid
                        key={item.title}
                        size={{xs: 6, md: 4}}
                    >
                        <div
                            className="stat-card"
                            style={{
                                minHeight: 100,
                                padding: 14,
                                borderLeftColor: item.color
                            }}
                        >
                            <div className="stat-info">
                                <h3 style={{fontSize: 12}}>
                                    {item.title}
                                </h3>
                                <p style={{
                                    color: item.color,
                                    fontSize: 27
                                }}>
                                    {item.value}
                                </p>
                            </div>
                        </div>
                    </Grid>
                ))}
            </Grid>
        </section>
    );
}