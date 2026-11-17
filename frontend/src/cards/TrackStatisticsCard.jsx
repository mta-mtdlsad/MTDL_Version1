import React from 'react';

export default function TrackStatisticsCard({ stats }) {
    return (
        <section aria-label="Aggregate Track Counts" className="dash-card">
            <h2 className="dash-heading">Aggregate Track Counts</h2>

            <div className="stats-grid">
                <div className="stat-box">
                    <span className="stat-label">Total Tracks</span>
                    <span className="stat-value">{stats?.totalTracks ?? 0}</span>
                </div>
                <div className="stat-box success">
                    <span className="stat-label">Active</span>
                    <span className="stat-value" style={{ color: '#16a34a' }}>{stats?.activeCount ?? 0}</span>
                </div>
                <div className="stat-box">
                    <span className="stat-label">Inactive</span>
                    <span className="stat-value" style={{ color: '#475569' }}>{stats?.inactiveCount ?? 0}</span>
                </div>
            </div>

            <div className="chip-list">
                <span className="chip">Friend: <strong>{stats?.friendCount ?? 0}</strong></span>
                <span className="chip">Hostile: <strong>{stats?.hostileCount ?? 0}</strong></span>
                <span className="chip">Neutral: <strong>{stats?.neutralCount ?? 0}</strong></span>
                <span className="chip">Unknown: <strong>{stats?.unknownCount ?? 0}</strong></span>
            </div>
        </section>
    );
}
