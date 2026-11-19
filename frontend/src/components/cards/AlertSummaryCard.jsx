import React from 'react';
import Grid from '@mui/material/Grid';
export default function AlertSummaryCard({summary}) {
    const total = summary?.totalAlerts ?? 0;
    const severity = summary?.severitySummary ?? {};
    const eventTypes = Object.entries(
        summary?.eventTypeSummary ?? {}
    );
    const severityRows = [
        {
            label: 'HIGH',
            count: severity.HIGH ?? 0,
            color: 'var(--hostile)'
        },
        {
            label: 'MEDIUM',
            count: severity.MEDIUM ?? 0,
            color: 'var(--unknown)'
        },
        {
            label: 'LOW',
            count: severity.LOW ?? 0,
            color: 'var(--neutral)'
        }
    ];
    // Create coloured progress bar
    const bar = (count, maximum, color) => (
        <div className="stat-bar-track">
            <div
                className="stat-bar-fill"
                style={{
                    width: `${
                        maximum > 0
                            ? Math.min(100, count / maximum * 100)
                            : 0
                    }%`,
                    background: color
                }}
            />
        </div>
    );
    return (
        <section
            aria-label="Alert Summary"
            className="dashboard-panel"
        >
            <h2>Alert Summary</h2>
            <Grid container spacing={3}>
                {/* TOTAL ALERTS */}
                <Grid size={{xs: 12, sm: 4, lg: 3}}>
                    <div
                        className="stat-card"
                        style={{
                            height: '100%',
                            borderLeftColor: 'var(--accent)'
                        }}
                    >
                        <div className="stat-info">
                            <h3>Total Alerts</h3>
                            <p style={{
                                fontSize: 38,
                                color: '#60a5fa'
                            }}>
                                {total}
                            </p>
                        </div>
                    </div>
                </Grid>
                {/* SEVERITY BARS */}
                <Grid size={{xs: 12, sm: 8, lg: 4}}>
                    <h3 style={{fontSize: 15}}>
                        Severity Distribution
                    </h3>
                    <div className="stat-bars">
                        {severityRows.map(item => (
                            <div
                                className="stat-bar-item"
                                key={item.label}
                            >
                                <div className="stat-bar-label">
                                    <span>{item.label}</span>
                                    <strong style={{
                                        color: item.color
                                    }}>
                                        {item.count}
                                    </strong>
                                </div>
                                {bar(
                                    item.count,
                                    total,
                                    item.color
                                )}
                            </div>
                        ))}
                    </div>
                </Grid>
                {/* EVENT TYPE BARS */}
                <Grid size={{xs: 12, lg: 5}}>
                    <h3 style={{fontSize: 15}}>
                        Totals by Event Type
                    </h3>
                    {eventTypes.length === 0 ? (
                        <p>No alert event types recorded yet.</p>
                    ) : (
                        <div
                            className="dashboard-scroll"
                            style={{
                                maxHeight: 180,
                                overflowY: 'auto'
                            }}
                        >
                            <div className="stat-bars">
                                {eventTypes.map(([name, count]) => (
                                    <div
                                        className="stat-bar-item"
                                        key={name}
                                    >
                                        <div className="stat-bar-label">

                      <span style={{
                          overflowWrap: 'anywhere'
                      }}>

                        {name.replaceAll('_', ' ')}

                      </span>
                                            <strong>{count}</strong>
                                        </div>
                                        {bar(
                                            count,
                                            total,
                                            'var(--accent)'
                                        )}
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </Grid>
            </Grid>
        </section>
    );
}