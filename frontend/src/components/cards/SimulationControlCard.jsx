import React from 'react';
export default function SimulationControlCard({
                                                  simulationState,
                                                  isPending,
                                                  feedbackMessage,
                                                  onControl
                                              }) {
    const status = String(
        simulationState ?? 'UNKNOWN'
    ).toUpperCase();
    const running = status === 'RUNNING';
    const colour = running
        ? 'var(--friend)'
        : status === 'STOPPED'
            ? 'var(--hostile)'
            : 'var(--unknown)';
    return (
        <header
            className="app-content-header"
            aria-label="Simulator Controls"
        >
            <div>
                <h1 className="app-content-header-text">
                    Alerts & Source Health Dashboard
                </h1>
                <p className="page-subtitle">
                    Status:{' '}
                    <span style={{
                        color: colour,
                        fontWeight: 700
                    }}>

            {'● '}{status}

          </span>
                    <span style={{
                        margin: '0 9px'
                    }}>

            •

          </span>
                    <span className="live-indicator">

            Live updates (2s)

          </span>
                </p>
                {feedbackMessage && (
                    <p
                        className="last-updated"
                        role="status"
                        style={{marginTop: 6}}
                    >
                        {feedbackMessage}
                    </p>
                )}
            </div>
            {/* SIMULATION BUTTONS */}
            <div className="header-actions">
                <button
                    type="button"
                    className="start-button"
                    disabled={isPending || running}
                    onClick={() => onControl('START')}
                >
                    {isPending && !running
                        ? 'Starting...'
                        : 'Start'}
                </button>
                <button
                    type="button"
                    className="stop-button"
                    disabled={isPending || !running}
                    onClick={() => onControl('STOP')}
                >
                    {isPending && running
                        ? 'Stopping...'
                        : 'Stop'}
                </button>
            </div>
        </header>
    );
}