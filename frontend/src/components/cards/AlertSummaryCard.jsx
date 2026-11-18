import React from 'react';

export default function AlertSummaryCard({ summary }) {
    const totalAlerts = summary?.totalAlerts ?? 0;
    const severity = summary?.severitySummary ?? { HIGH: 0, MEDIUM: 0, LOW: 0 };
    const eventTypes = Object.entries(summary?.eventTypeSummary ?? {});

    return (
        <section aria-label="Alert Summary" className="dash-card">
            <h2 className="dash-heading">Alert Summary</h2>

            <div className="stats-grid">
                <div className="stat-box">
                    <span className="stat-label">Total Alerts</span>
                    <span className="stat-value">{totalAlerts}</span>
                </div>
                <div className="stat-box danger">
                    <span className="stat-label">HIGH</span>
                    <span className="stat-value" style={{ color: '#dc2626' }}>{severity.HIGH ?? 0}</span>
                </div>
                <div className="stat-box warning">
                    <span className="stat-label">MEDIUM</span>
                    <span className="stat-value" style={{ color: '#d97706' }}>{severity.MEDIUM ?? 0}</span>
                </div>
                <div className="stat-box info">
                    <span className="stat-label">LOW</span>
                    <span className="stat-value" style={{ color: '#2563eb' }}>{severity.LOW ?? 0}</span>
                </div>
            </div>

            <h3 className="dash-subheading">Totals by Event Type</h3>
            {eventTypes.length === 0 ? (
                <p className="muted-text">No alert event types recorded yet.</p>
            ) : (
                <ul className="chip-list">
                    {eventTypes.map(([eventType, count]) => (
                        <li key={eventType} className="chip">
                            {eventType}: <strong>{count}</strong>
                        </li>
                    ))}
                </ul>
            )}
        </section>
    );
}
