import React from 'react';
// Match health status to existing badge colours
function healthClass(status) {
    switch (
        String(status ?? '').trim().toUpperCase()
        ) {
        case 'ACTIVE':
            return 'friend';
        case 'STALE':
            return 'unknown';
        case 'DISCONNECTED':
            return 'hostile';
        default:
            return 'neutral';
    }
}
export default function SourceRegistryCard({
                                               sources = [],
                                               loading,
                                               error
                                           }) {
    return (
        <section
            className="dashboard-panel"
            aria-label="UDP Source Registry"
        >
            <h2>
                UDP Source Registry ({sources.length})
            </h2>
            {loading ? (
                <p role="status">
                    Loading UDP sources...
                </p>
            ) : error ? (
                <div
                    className="error-message"
                    role="alert"
                >
                    {error}
                </div>
            ) : sources.length === 0 ? (
                <p>No UDP sources registered yet.</p>
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
                        {sources.map((source, index) => {
                            const status = String(
                                source.sourceStatus ?? 'NOT SEEN'
                            ).trim().toUpperCase();
                            return (
                                <tr
                                    key={source.sourceId ?? index}
                                >
                                    <td style={{fontWeight: 600}}>
                                        {source.sourceId ?? '-'}
                                    </td>
                                    <td>

                      <span
                          className={`identity-badge ${
                              healthClass(status)
                          }`}
                      >

                        {status}

                      </span>
                                    </td>
                                    <td>
                                        {source.lastReceivedTime ?? 'Never'}
                                    </td>
                                    <td>
                                        {source.latestSequence ?? '-'}
                                    </td>
                                    <td>
                                        {source.receivedCount ?? 0}
                                    </td>
                                    <td>
                                        {source.acceptedCount ?? 0}
                                    </td>
                                    <td>
                                        {source.rejectedCount ?? 0}
                                    </td>
                                    <td>
                                        {source.duplicateCount ?? 0}
                                    </td>
                                    <td>
                                        {source.processingFailureCount ?? 0}
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