export async function fetchSimulationState() {
    const response = await fetch('/api/simulation/state');
    if (!response.ok) {
        throw new Error(`Failed to fetch simulation state (${response.status})`);
    }
    const data = await response.json();
    return data.simulationState || 'UNKNOWN';
}

export async function controlSimulation(action) {
    const response = await fetch(
        `/api/simulation/control?action=${encodeURIComponent(action)}`,
        { method: 'POST' }
    );
    const message = await response.text();
    if (!response.ok) {
        throw new Error(message || `Simulation command failed (${response.status})`);
    }
    return message;
}
