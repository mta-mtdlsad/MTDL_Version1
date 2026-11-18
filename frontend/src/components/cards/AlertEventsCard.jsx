import React from 'react';

const SEVERITY_OPTIONS = [
    { value: 'ALL', label: 'All Severities' },
    { value: 'HIGH', label: 'HIGH' },
    { value: 'MEDIUM', label: 'MEDIUM' },
    { value: 'LOW', label: 'LOW' },
];

function getSeverityBadgeClass(severity) {
    const level = (severity || '').toUpperCase();
    if (level === 'HIGH') return 'badge-high';
    if (level === 'MEDIUM') return 'badge-medium';
    return 'badge-low';
}

export default function AlertEventsCard({
                                            alerts,
                                            selectedSeverity,
                                            onSelectSeverity,
                                            filtering,
                                        }) {
    return (
        <section aria-label="Alert Events List" className="dash-card">
            <div className="dash-card-header">
                <h2 className="dash-heading">Alert Event List</h2>

                <div role="group" aria-label="Filter alerts by severity" className="btn-group">
                    {SEVERITY_OPTIONS.map((option) => {
                        const isActive = selectedSeverity === option.value;
                        return (
                            <button
                                key={option.value}
                                type="button"
                                disabled={filtering}
                                aria-pressed={isActive}
                                onClick={() => onSelectSeverity(option.value)}
                                className={`btn btn-filter ${isActive ? 'active' : ''}`}
                            >
                                {option.label}
                            </button>
                        );
                    })}
                </div>
            </div>

            {alerts.length === 0 && !filtering ? (
                <p className="muted-text">
                    No alerts found for <strong>{selectedSeverity}</strong>.
                </p>
            ) : (
                <div
                    className="table-scroll"
                    style={{
                        maxHeight: '300px',
                        minHeight: '150px',
                        opacity: filtering ? 0.55 : 1,
                        transition: 'opacity 150ms ease',
                    }}
                >
                    <table className="dash-table">
                        <thead>
                        <tr>
                            <th>Severity</th>
                            <th>Event Type</th>
                            <th>Message</th>
                            <th>Track Number</th>
                            <th>Source ID</th>
                            <th>Alert Time</th>
                        </tr>
                        </thead>
                        <tbody>
                        {alerts.map((alert, index) => (
                            <tr key={alert.alertId ?? index}>
                                <td>
                    <span className={`badge ${getSeverityBadgeClass(alert.alertSeverity)}`}>
                      {alert.alertSeverity || 'UNKNOWN'}
                    </span>
                                </td>
                                <td>{alert.eventType || '-'}</td>
                                <td>{alert.alertMessage || alert.message || '-'}</td>
                                <td>{alert.trackNumber || '-'}</td>
                                <td>{alert.sourceId || '-'}</td>
                                <td>{alert.alertTime || '-'}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </section>
    );
}
