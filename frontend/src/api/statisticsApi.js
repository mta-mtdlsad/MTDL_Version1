export async function fetchTrackStatistics() {
    const response = await fetch('/api/statistics');
    if (!response.ok) {
        throw new Error(`Failed to fetch statistics: ${response.status}`);
    }
    return response.json();
}