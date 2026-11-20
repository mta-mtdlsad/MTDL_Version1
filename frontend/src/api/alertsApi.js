const BASE_URL = '/api/alerts';
export async function fetchAllAlerts() {
    const response = await fetch(`${BASE_URL}/list`);
    if (!response.ok) {
        throw new Error(`Failed to fetch alerts: ${response.status}`);
    }
    return response.json();
}
export async function fetchAlertsBySeverity(severity) {
    const alerts = await fetchAllAlerts();
    if (severity === 'ALL') {
        return alerts;
    }
    return alerts.filter(alert =>
        String(alert.alertSeverity ?? '').toUpperCase()
        === String(severity).toUpperCase()
    );
}
export async function fetchAlertSummary() {
    const alerts = await fetchAllAlerts();
    const severitySummary = {
        HIGH: 0,
        MEDIUM: 0,
        LOW: 0
    };
    const eventTypeSummary = {};
    alerts.forEach(alert => {
        const severity = String(
            alert.alertSeverity ?? ''
        ).toUpperCase();
        if (severity in severitySummary) {
            severitySummary[severity]++;
        }
        if (alert.eventType) {
            eventTypeSummary[alert.eventType] =
                (eventTypeSummary[alert.eventType] ?? 0) + 1;
        }
    });
    return {
        totalAlerts: alerts.length,
        severitySummary,
        eventTypeSummary
    };
}