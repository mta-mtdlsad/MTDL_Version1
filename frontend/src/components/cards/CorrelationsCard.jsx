import React from 'react';
import Grid from '@mui/material/Grid';
export default function CorrelationsCard({
                                             correlations = [],
                                             nearestPairs = []
                                         }) {
    return (
        <Grid
            container
            spacing={3}
            aria-label="Correlations and Nearest Source Pairs"
        >
            {/* LEFT: CORRELATION GROUPS */}
            <Grid size={{xs: 12, md: 6}}>
                <section
                    className="dashboard-panel"
                    style={{height: '100%'}}
                >
                    <h2>
                        Correlation Groups ({correlations.length})
                    </h2>
                    {correlations.length === 0 ? (
                        <p>No correlated track groups detected.</p>
                    ) : (
                        <div
                            className="dashboard-scroll"
                            style={{
                                maxHeight: 280,
                                overflowY: 'auto'
                            }}
                        >
                            {correlations.map((group, index) => (
                                <div
                                    key={group.groupId ?? index}
                                    style={{
                                        padding: '12px 0',
                                        borderBottom:
                                            '1px solid var(--border)'
                                    }}
                                >
                                    <div style={{
                                        display: 'flex',
                                        justifyContent: 'space-between',
                                        gap: 10,
                                        alignItems: 'center'
                                    }}>
                                        <strong>
                                            Group #{group.groupId ?? index + 1}
                                        </strong>
                                        <span className="identity-badge neutral">

                      {group.sourceCount ?? 0} sources

                    </span>
                                    </div>
                                    <p style={{
                                        margin: '8px 0 4px',
                                        fontSize: 12
                                    }}>
                                        Max Distance:{' '}
                                        <strong>
                                            {group.maxDistanceKm ?? 0} km
                                        </strong>
                                        {' | '}
                                        Max Time Diff:{' '}
                                        <strong>
                                            {group.maxTimeDiffSec ?? 0} s
                                        </strong>
                                    </p>
                                    <p style={{
                                        margin: 0,
                                        fontSize: 12,
                                        overflowWrap: 'anywhere'
                                    }}>
                                        Members:{' '}
                                        {Array.isArray(group.tracks) &&
                                        group.tracks.length
                                            ? group.tracks.map(
                                                track =>
                                                    `${track.sourceId ?? '-'} (${track.trackNumber ?? '-'})`
                                            ).join(', ')
                                            : 'None'}
                                    </p>
                                </div>
                            ))}
                        </div>
                    )}
                </section>
            </Grid>
            {/* RIGHT: NEAREST SOURCE PAIRS */}
            <Grid size={{xs: 12, md: 6}}>
                <section
                    className="dashboard-panel"
                    style={{height: '100%'}}
                >
                    <h2>
                        Nearest Source Pairs ({nearestPairs.length})
                    </h2>
                    {nearestPairs.length === 0 ? (
                        <p>No nearest source pairs available.</p>
                    ) : (
                        <div
                            className="dashboard-scroll"
                            style={{
                                maxHeight: 280,
                                overflowY: 'auto'
                            }}
                        >
                            <table>
                                <thead>
                                <tr>
                                    <th>Source ID</th>
                                    <th>Nearest Source</th>
                                    <th>Distance (km)</th>
                                </tr>
                                </thead>
                                <tbody>
                                {nearestPairs.map((pair, index) => (
                                    <tr
                                        key={`${pair.sourceId}-${pair.nearestSourceId}-${index}`}
                                    >
                                        <td>
                                            {pair.sourceId ?? '-'}
                                        </td>
                                        <td>
                                            {pair.nearestSourceId ?? '-'}
                                        </td>
                                        <td>
                                            {pair.distanceKm ?? 0} km
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </section>
            </Grid>
        </Grid>
    );
}