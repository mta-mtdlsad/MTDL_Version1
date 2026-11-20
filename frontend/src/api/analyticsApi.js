export async function fetchOverallAnalytics() {
    const response = await fetch('/api/analytics/overall');
    if (!response.ok) {
        throw new Error(`Failed to fetch overall analytics (${response.status})`);
    }
    return response.json();
}