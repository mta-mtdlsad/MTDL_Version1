import React from 'react';
import Grid from '@mui/material/Grid';
export default function OverallAnalyticsCard({analytics}) {
    const tracks = Array.isArray(analytics?.highSpeedTracks)
        ? analytics.highSpeedTracks
        : [];
    const items = [
        {
            title: 'Total Processed',
            value: analytics?.totalProcessed ?? 0,
            color: 'var(--accent)'
        },
        {
            title: 'Accepted',
            value: analytics?.acceptedCount ?? 0,
            color: 'var(--friend)'
        },
        {
            title: 'Rejected',
            value: analytics?.rejectedCount ?? 0,
            color: 'var(--hostile)'
        },
        {
            title: 'Duplicate',
            value: analytics?.duplicateCount ?? 0,
            color: 'var(--unknown)'
        }
    ];
    return (
        <section
            className="dashboard-panel"
            aria-label="UDP Intake and High-Speed Records"
            style={{height: '100%'}}
        >
            <h2>UDP Intake & High-Speed Records</h2>
            {/* UDP STATISTICS */}
            <Grid container spacing={2}>
                {items.map(item => (
                    <Grid
                        key={item.title}
                        size={{xs: 6, md: 3}}
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
            {/* HIGH-SPEED TRACKS */}
            <h3 style={{
                fontSize: 15,
                marginTop: 22
            }}>
                High-Speed Tracks ({tracks.length})
            </h3>
            {tracks.length === 0 ? (
                <p>No high-speed tracks recorded.</p>
            ) : (
                <div
                    className="dashboard-scroll"
                    style={{
                        maxHeight: 300,
                        overflowY: 'auto'
                    }}
                >
                    <table>
                        <thead>
                        <tr>
                            <th>Track Number</th>
                            <th>Source ID</th>
                            <th>Speed</th>
                            <th>Identity</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        {tracks.map((track, index) => (
                            <tr key={track.trackId ?? index}>
                                <td>
                                    {track.trackNumber ?? '-'}
                                </td>
                                <td>
                                    {track.sourceId ?? '-'}
                                </td>
                                <td style={{
                                    color: 'var(--hostile)',
                                    fontWeight: 700
                                }}>
                                    {track.speed ?? 0}
                                </td>
                                <td>

                    <span
                        className={`identity-badge ${
                            String(
                                track.identityType ?? ''
                            ).toLowerCase()
                        }`}
                    >

                      {track.identityType ?? '-'}

                    </span>
                                </td>
                                <td>

                    <span
                        className={`track-status ${
                            String(
                                track.trackStatus ?? ''
                            ).toLowerCase()
                        }`}
                    >

                      {track.trackStatus ?? '-'}

                    </span>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </section>
    );
}