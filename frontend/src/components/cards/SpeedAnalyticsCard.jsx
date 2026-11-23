import React from 'react';
import Grid from '@mui/material/Grid';
export default function SpeedAnalyticsCard({analytics}) {
    const tracks = Array.isArray(analytics?.highSpeedTracks)
        ? analytics.highSpeedTracks
        : [];

    return (
        <section
            className="dashboard-panel"
            aria-label="High-Speed Records"
            style={{height: '100%'}}
        >
            {/* HIGH-SPEED TRACKS */}
            <h2 >
                High-Speed Tracks ({tracks.length})
            </h2>
            {tracks.length === 0 ? (
                <p>No high-speed tracks recorded.</p>
            ) : (
                <div
                    className="dashboard-scroll"
                    style={{
                        maxHeight: 360,
                        minHeight: 130,
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