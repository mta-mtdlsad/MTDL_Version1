import {useEffect, useState} from 'react';
import '../../styles/App.css';

export default function SimulationStatus({onSimulationChange}) {
    // Stores the current simulation state
    const [state, setState] = useState('LOADING');
    // Tracks whether a start or stop operation is in progress
    const [loading, setLoading] = useState(false);
    // Stores any API error messages
    const [error, setError] = useState('');
    const [simScenario, setSimScenario] = useState("NORMAL");

    // Fetches the current simulation state from the backend
    async function loadState() {
        try {
            const response = await fetch('/api/simulator/state');
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }
            const data = await response.json();
            // Update the displayed simulation state
            setState(data.simulationState);
            setError('');
        } catch (err) {
            setError(err.message);
        }
    }

    // Starts or stops the simulation
    async function controlSimulation(action, scenario) {
        setLoading(true);
        setError('');
        try {
            // Send the requested action to the backend
            const response = await fetch(
                `/api/simulator/control?action=${action}&scenario=${scenario}`,
                {method: 'POST'}
            );
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }
            // Refresh the state after starting or stopping
            await loadState();
            // Notify the parent dashboard
            onSimulationChange?.();
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    // Load the state initially and refresh every 3 seconds
    useEffect(() => {
        loadState();
        const interval = setInterval(loadState, 3000);
        return () => clearInterval(interval);
    }, []);
    return (
        <section className="dashboard-panel">
            <h2>Simulation Control</h2>
            {/* Display the current simulation state */}
            <p>
                Current State: <strong>{state}</strong>
            </p>

            {/* Display API errors */}
            {error && (
                <p role="alert">Error: {error}</p>
            )}

            <label>Simulation Mode:&ensp;
                <select
                    value={simScenario}
                    onChange={(event) => setSimScenario(event.target.value)}>
                    <option value="NORMAL" select>NORMAL</option>
                    <option value="MIXED">MIXED</option>
                    <option value="HOSTILE">HOSTILE</option>
                </select>
            </label><br/><br/>

            {/* Simulation control buttons */}
            <div style={{
                display: 'flex',
                gap: '12px',
                flexWrap: 'wrap'
            }}>

                {/* Start button */}
                <button
                    className="start-button"
                    disabled={loading || state !== 'STOPPED'}
                    onClick={() => controlSimulation('START', simScenario)}
                >
                    {loading ? 'Please wait ...' : 'Start Simulation'}
                </button>

                {/* Stop button */}
                <button
                    className="stop-button"
                    disabled={loading || state !== 'RUNNING'}
                    onClick={() => controlSimulation('STOP', simScenario)}
                >
                    Stop Simulation
                </button>

            </div>
        </section>
    );
}