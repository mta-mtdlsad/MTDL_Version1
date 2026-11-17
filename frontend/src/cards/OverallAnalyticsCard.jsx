import React from 'react';

export default function OverallAnalyticsCard({ analytics }) {
    const highSpeedTracks = Array.isArray(analytics?.highSpeedTracks)
        ? analytics.highSpeedTracks
        : [];

    return (
        <section aria-label="UDP Intake and High-Speed Records" className="dash-card">
            <h2 className="dash-heading">UDP Intake Totals & High-Speed Records</h2>

            <div className="stats-grid">
                <div className="stat-box">
                    <span className="stat-label">Total Processed</span>
                    <span className="stat-value">{analytics?.totalProcessed ?? 0}</span>
                </div>
                <div className="stat-box success">
                    <span className="stat-label">Accepted</span>
                    <span className="stat-value" style={{ color: '#16a34a' }}>{analytics?.acceptedCount ?? 0}</span>
                </div>
                <div className="stat-box danger">
                    <span className="stat-label">Rejected</span>
                    <span className="stat-value" style={{ color: '#dc2626' }}>{analytics?.rejectedCount ?? 0}</span>
                </div>
                <div className="stat-box warning">
                    <span className="stat-label">Duplicate</span>
                    <span className="stat-value" style={{ color: '#d97706' }}>{analytics?.duplicateCount ?? 0}</span>
                </div>
            </div>

            <h3 className="dash-subheading">High-Speed Tracks ({highSpeedTracks.length})</h3>
            {highSpeedTracks.length === 0 ? (
                <p className="muted-text">No high-speed tracks recorded.</p>
            ) : (
                <div className="table-scroll" style={{ maxHeight: '220px' }}>
                    <table className="dash-table">
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
                        {highSpeedTracks.map((t, idx) => (
                            <tr key={t.trackId ?? idx}>
                                <td style={{ fontWeight: 600 }}>{t.trackNumber || '-'}</td>
                                <td>{t.sourceId || '-'}</td>
                                <td style={{ color: '#dc2626', fontWeight: 700 }}>{t.speed ?? 0}</td>
                                <td>{t.identityType || '-'}</td>
                                <td>{t.trackStatus || '-'}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </section>
    );
}
