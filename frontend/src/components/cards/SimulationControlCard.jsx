import React from 'react';

export default function SimulationControlCard({
                                                  simulationState,
                                                  isPending,
                                                  feedbackMessage,
                                                  onControl,
                                              }) {
    const normalizedState = (simulationState || 'UNKNOWN').toUpperCase();
    const isRunning = normalizedState === 'RUNNING';

    return (
        <header aria-label="Simulator Controls" className="dash-card dash-card-header">
            <div>
                <h1 className="dash-title">Alerts & Source Health Dashboard</h1>
                <p className="sim-meta">
                    Status:{' '}
                    <span
                        aria-live="polite"
                        className={`status-dot-label ${isRunning ? 'running' : 'stopped'}`}
                    >
            <span className="status-dot" />
                        {normalizedState}
          </span>
                    <span className="meta-sep">•</span>
                    <span>Live updates (2s)</span>
                </p>
                {feedbackMessage && (
                    <p role="status" className="sim-feedback">
                        {feedbackMessage}
                    </p>
                )}
            </div>

            <div className="btn-group">
                <button
                    type="button"
                    className="btn btn-primary"
                    disabled={isPending || isRunning}
                    onClick={() => onControl('START')}
                    aria-label="Start Simulation"
                >
                    {isPending && !isRunning ? 'Starting...' : 'Start'}
                </button>

                <button
                    type="button"
                    className="btn btn-outline"
                    disabled={isPending || !isRunning}
                    onClick={() => onControl('STOP')}
                    aria-label="Stop Simulation"
                >
                    {isPending && isRunning ? 'Stopping...' : 'Stop'}
                </button>
            </div>
        </header>
    );
}
