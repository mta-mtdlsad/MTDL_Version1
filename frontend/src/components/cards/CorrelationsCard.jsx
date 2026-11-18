import React from 'react';

export default function CorrelationsCard({ correlations, nearestPairs }) {
    return (
        <section aria-label="Correlations and Nearest Source Pairs" className="two-col-grid">
            <div className="dash-card">
                <h2 className="dash-heading">Correlation Groups ({correlations.length})</h2>
                {correlations.length === 0 ? (
                    <p className="muted-text">No correlated track groups detected.</p>
                ) : (
                    <div className="table-scroll" style={{ padding: '10px', maxHeight: '250px' }}>
                        {correlations.map((group, idx) => (
                            <div key={group.groupId ?? idx} className="group-item">
                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                                    <strong>Group #{group.groupId ?? idx + 1}</strong>
                                    <span className="badge badge-low">{group.sourceCount ?? 0} sources</span>
                                </div>
                                <p className="muted-text" style={{ marginBottom: '4px' }}>
                                    Max Distance: <strong>{group.maxDistanceKm ?? 0} km</strong> | Max Time Diff:{' '}
                                    <strong>{group.maxTimeDiffSec ?? 0} s</strong>
                                </p>
                                <p style={{ margin: 0, fontSize: '12px', color: '#475569' }}>
                                    Members:{' '}
                                    {Array.isArray(group.tracks) && group.tracks.length > 0
                                        ? group.tracks.map((t) => `${t.sourceId || '-'} (${t.trackNumber || '-'})`).join(', ')
                                        : 'None'}
                                </p>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            <div className="dash-card">
                <h2 className="dash-heading">Nearest Source Pairs ({nearestPairs.length})</h2>
                {nearestPairs.length === 0 ? (
                    <p className="muted-text">No nearest source pairs available.</p>
                ) : (
                    <div className="table-scroll" style={{ maxHeight: '250px' }}>
                        <table className="dash-table">
                            <thead>
                            <tr>
                                <th>Source ID</th>
                                <th>Nearest Source</th>
                                <th>Distance (km)</th>
                            </tr>
                            </thead>
                            <tbody>
                            {nearestPairs.map((pair, idx) => (
                                <tr key={`${pair.sourceId}-${pair.nearestSourceId}-${idx}`}>
                                    <td style={{ fontWeight: 600 }}>{pair.sourceId || '-'}</td>
                                    <td style={{ fontWeight: 600 }}>{pair.nearestSourceId || '-'}</td>
                                    <td>{pair.distanceKm ?? 0} km</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </section>
    );
}
