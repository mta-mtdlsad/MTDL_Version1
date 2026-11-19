export async function fetchAllAlerts() {
    const response = await fetch('/api/alerts/list');
    if (!response.ok) {
        throw new Error(`Failed to fetch alerts (${response.status})`);
    }
    const data = await response.json();
    return Array.isArray(data) ? data : [];
}

export async function fetchAlertsBySeverity(severity) {
    const url = `/api/reports/alerts/by-severity?severity=${encodeURIComponent(severity)}`;
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`Failed to filter alerts by ${severity} (${response.status})`);
    }
    const data = await response.json();
    return Array.isArray(data) ? data : [];
}

export async function fetchAlertSummary() {
    const response = await fetch('/api/reports/alerts/summary');
    if (!response.ok) {
        throw new Error(`Failed to fetch alert summary (${response.status})`);
    }
    return response.json();
}
