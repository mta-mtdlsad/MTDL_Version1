import React from 'react';
const SEVERITIES = [
    {value: 'ALL', label: 'All Severities'},
    {value: 'HIGH', label: 'HIGH'},
    {value: 'MEDIUM', label: 'MEDIUM'},
    {value: 'LOW', label: 'LOW'}
];
// Severity badge colours
function severityClass(severity) {
    switch (String(severity ?? '').toUpperCase()) {
        case 'HIGH':
            return 'hostile';
        case 'MEDIUM':
            return 'unknown';
        default:
            return 'neutral';
    }
}
// Identify alert event type
function getEventType(alert) {
    const message = String(
        alert.alertMessage ?? alert.message ?? ''
    ).toLowerCase();
    const eventType = String(
        alert.eventType ?? ''
    ).toLowerCase();
    if (
        eventType.includes('duplicate') ||
        message.includes('duplicate')
    ) {
        return 'DUPLICATE';
    }
    if (
        eventType.includes('out_of_order') ||
        eventType.includes('out of order') ||
        eventType.includes('out-of-order') ||
        message.includes('out-of-order') ||
        message.includes('out of order')
    ) {
        return 'OUT OF ORDER';
    }
    if (
        eventType.includes('source') ||
        message.includes('source')
    ) {
        return 'SOURCE HEALTH';
    }
    return 'REJECTED';
}
// Event type colours
const eventColors = {
    DUPLICATE: '#ec4899',
    'OUT OF ORDER': '#f59e0b',
    REJECTED: '#ef4444',
    'SOURCE HEALTH': '#a78bfa'
};
export default function AlertEventsCard({
                                            alerts = [],
                                            selectedSeverity,
                                            onSelectSeverity,
                                            filtering
                                        }) {
    return (
        <section
            className="dashboard-panel"
            aria-label="Alert Events List"
        >
            <div className="table-header">
                <h2>
                    Alert Event List ({alerts.length})
                </h2>
                {/* SEVERITY FILTER BUTTONS */}
                <div
                    className="header-actions"
                    role="group"
                    aria-label="Filter alerts by severity"
                    style={{gap: 8}}
                >
                    {SEVERITIES.map(option => (
                        <button
                            type="button"
                            key={option.value}
                            disabled={filtering}
                            aria-pressed={
                                selectedSeverity === option.value
                            }
                            onClick={() =>
                                onSelectSeverity(option.value)
                            }
                            style={{
                                padding: '8px 12px',
                                fontSize: 12,
                                background:
                                    selectedSeverity === option.value
                                        ? 'var(--accent)'
                                        : '#30435e'
                            }}
                        >
                            {option.label}
                        </button>
                    ))}
                </div>
            </div>
            {/* ALERT TABLE */}
            {alerts.length === 0 && !filtering ? (
                <p>
                    No alerts found for{' '}
                    <strong>{selectedSeverity}</strong>.
                </p>
            ) : (
                <div
                    className="dashboard-scroll"
                    style={{
                        maxHeight: 350,
                        minHeight: 130,
                        overflowY: 'auto',
                        opacity: filtering ? 0.55 : 1
                    }}
                >
                    <table>
                        <thead>
                        <tr>
                            <th>Severity</th>
                            <th>Event Type</th>
                            <th>Message</th>
                            <th>Alert Time</th>
                        </tr>
                        </thead>
                        <tbody>
                        {alerts.map((alert, index) => {
                            const eventType = getEventType(alert);
                            const color = eventColors[eventType];
                            return (
                                <tr key={alert.alertId ?? index}>
                                    {/* SEVERITY */}
                                    <td>

                      <span
                          className={`identity-badge ${
                              severityClass(
                                  alert.alertSeverity
                              )
                          }`}
                      >

                        {alert.alertSeverity ?? 'UNKNOWN'}

                      </span>
                                    </td>
                                    {/* EVENT TYPE */}
                                    <td>

                      <span
                          className="identity-badge"
                          style={{
                              backgroundColor: `${color}25`,
                              color: color,
                              fontWeight: 600
                          }}
                      >

                        {eventType}

                      </span>
                                    </td>
                                    {/* MESSAGE */}
                                    <td
                                        style={{
                                            whiteSpace: 'normal',
                                            minWidth: 240
                                        }}
                                    >
                                        {alert.alertMessage ??
                                            alert.message ?? '-'}
                                    </td>
                                    {/* ALERT TIME */}
                                    <td>
                                        {alert.alertTime ?? '-'}
                                    </td>
                                </tr>
                            );
                        })}
                        </tbody>
                    </table>
                </div>
            )}
        </section>
    );
}