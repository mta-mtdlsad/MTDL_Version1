export async function fetchCorrelations() {
    const response = await fetch('/api/analytics/correlations');
    if (!response.ok) {
        throw new Error(`Failed to fetch correlation groups (${response.status})`);
    }
    const data = await response.json();
    return Array.isArray(data) ? data : [];
}

export async function fetchNearestPairs() {
    const response = await fetch('/api/tracks/nearest');
    if (!response.ok) {
        throw new Error(`Failed to fetch nearest source pairs (${response.status})`);
    }
    const data = await response.json();
    return Array.isArray(data) ? data : [];
}
