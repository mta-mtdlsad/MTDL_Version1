import React from 'react';

function getHealthBadgeClass(status) {
    const label = (status || 'NOT SEEN').trim().toUpperCase();
    if (label === 'ACTIVE') return 'badge-active';
    if (label === 'STALE') return 'badge-stale';
    if (label === 'DISCONNECTED') return 'badge-disconnected';
    return 'badge-neutral';
}

export default function SourceRegistryCard({ sources, loading, error }) {
    return (
        <section aria-label="UDP Source Registry" className="dash-card">
            <h2 className="dash-heading">UDP Source Registry</h2>

            {loading ? (
                <p role="status" aria-live="polite" className="muted-text">Loading UDP sources...</p>
            ) : error ? (
                <div role="alert" className="alert-banner"><strong>Error:</strong> {error}</div>
            ) : sources.length === 0 ? (
                <p className="muted-text">No UDP sources registered yet.</p>
            ) : (
                <div className="table-scroll" style={{ maxHeight: '240px' }}>
                    <table className="dash-table">
                        <thead>
                        <tr>
                            <th>Source ID</th>
                            <th>Health Status</th>
                            <th>Last Received Time</th>
                            <th>Latest Seq</th>
                            <th>Received</th>
                            <th>Accepted</th>
                            <th>Rejected</th>
                            <th>Duplicate</th>
                            <th>Failures</th>
                        </tr>
                        </thead>
                        <tbody>
                        {sources.map((src, idx) => {
                            const statusLabel = (src.sourceStatus || 'NOT SEEN').trim().toUpperCase();
                            return (
                                <tr key={src.sourceId ?? idx}>
                                    <td style={{ fontWeight: 600 }}>{src.sourceId || '-'}</td>
                                    <td>
                      <span
                          aria-label={`Source status ${statusLabel}`}
                          className={`badge ${getHealthBadgeClass(statusLabel)}`}
                      >
                        {statusLabel}
                      </span>
                                    </td>
                                    <td>{src.lastReceivedTime || 'Never'}</td>
                                    <td>{src.latestSequence ?? '-'}</td>
                                    <td>{src.receivedCount ?? 0}</td>
                                    <td>{src.acceptedCount ?? 0}</td>
                                    <td>{src.rejectedCount ?? 0}</td>
                                    <td>{src.duplicateCount ?? 0}</td>
                                    <td>{src.processingFailureCount ?? 0}</td>
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
