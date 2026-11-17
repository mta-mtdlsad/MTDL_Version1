import { useEffect, useState } from 'react';

export default function SimulationStatus({ onSimulationChange }) {

    // Stores the current simulation state (LOADING, RUNNING, or STOPPED)
    const [state, setState] = useState('LOADING');

    // Tracks whether a start or stop operation is in progress
    const [loading, setLoading] = useState(false);

    // Stores any error messages returned by API requests
    const [error, setError] = useState('');

    // Fetches the current simulation state from the backend
    async function loadState() {
        try {
            // Send GET request to retrieve the simulation state
            const response = await fetch('/api/simulation/state');

            // Check whether the API request was successful
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            // Convert the API response into JSON
            const data = await response.json();

            // Update the simulation state displayed in the UI
            setState(data.simulationState);

            // Clear any previous error messages
            setError('');

        } catch (err) {
            // Display the error if the API request fails
            setError(err.message);
        }
    }

    // Starts or stops the simulation based on the provided action
    async function controlSimulation(action) {

        // Disable buttons while the operation is in progress
        setLoading(true);

        // Clear any previous errors
        setError('');

        try {
            // Send POST request to control the simulation
            const response = await fetch(
                `/api/simulation/control?action=${action}`,
                { method: 'POST' }
            );

            // Check whether the control request was successful
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            // Fetch the updated simulation state after the action
            await loadState();

            // Notify the parent component that the simulation changed
            onSimulationChange?.();

        } catch (err) {
            // Display an error if the control operation fails
            setError(err.message);

        } finally {
            // Re-enable buttons after the operation completes
            setLoading(false);
        }
    }

    // Runs when the component is first mounted
    useEffect(() => {

        // Fetch the initial simulation state
        loadState();

        // Automatically refresh the simulation state every 3 seconds
        const interval = setInterval(loadState, 3000);

        // Clear the interval when the component is unmounted
        return () => clearInterval(interval);

    }, []);

    // Render the simulation status and control buttons
    return (
        <section className="stats-section">

            {/* Section heading */}
            <h2>Simulation Control</h2>

            {/* Display the current simulation state */}
            <p>
                Current State: <strong>{state}</strong>
            </p>

            {/* Display an error message if an API request fails */}
            {error && <p role="alert">Error: {error}</p>}

            {/* Container for simulation control buttons */}
            <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>

                {/* Start button: enabled only when simulation is stopped */}
                <button
                    className="refresh-button"
                    disabled={loading || state !== 'STOPPED'}
                    onClick={() => controlSimulation('START')}
                >
                    {loading ? 'Please wait ... ' : 'Start Simulation'}
                </button>

                {/* Stop button: enabled only when simulation is running */}
                <button
                    className="refresh-button"
                    disabled={loading || state !== 'RUNNING'}
                    onClick={() => controlSimulation('STOP')}
                >
                    Stop Simulation
                </button>
            </div>
        </section>
    );
}

