import {useState, useEffect, useCallback} from 'react';
import Grid from '@mui/material/Grid';
import '../styles/App.css';
import {fetchAllAlerts} from '../api/alertsApi';
import {fetchAllSources} from '../api/sourcesApi';
import {fetchTrackStatistics} from '../api/statisticsApi';
import AlertSummaryCard from '../components/cards/AlertSummaryCard.jsx';
import SpeedAnalyticsCard from '../components/cards/SpeedAnalyticsCard.jsx';
import UDPAnalyticsCard from '../components/cards/UDPAnalyticsCard.jsx';
import AlertEventsCard from '../components/cards/AlertEventsCard.jsx';
import SourceRegistryCard from '../components/cards/SourceRegistryCard.jsx';

// Classify alerts into three event types
function getEventType(alert) {
    const message = String(
        alert.alertMessage ?? alert.message ?? ''
    ).toLowerCase();
    const eventType = String(
        alert.eventType ?? ''
    ).toLowerCase();
    if (
        eventType.includes('duplicate') ||
        message.includes('duplicate')
    ) {
        return 'DUPLICATE';
    }
    if (
        eventType.includes('out_of_order') ||
        eventType.includes('out of order') ||
        eventType.includes('out-of-order') ||
        message.includes('out-of-order') ||
        message.includes('out of order')
    ) {
        return 'OUT_OF_ORDER';
    }
    if (
        eventType.includes('source') ||
        message.includes('source')
    ) {
        return 'SOURCE_HEALTH';
    }
    return 'REJECTED';
}
export default function AlertsDashboardSection() {
    const [summary, setSummary] = useState(null);
    const [trackStats, setTrackStats] = useState(null);
    const [overallAnalytics, setOverallAnalytics] = useState(null);
    const [alerts, setAlerts] = useState([]);
    const [sources, setSources] = useState([]);
    const [selectedSeverity, setSelectedSeverity] = useState('ALL');
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [lastUpdated, setLastUpdated] = useState(null);
    const [error, setError] = useState(null);
    // Fetch dashboard data
    const refreshDashboard = useCallback(async () => {
        const results = await Promise.allSettled([
            fetchAllAlerts(),
            fetchAllSources(),
            fetchTrackStatistics(),
            fetch('/api/tracks/list').then(async response => {
                if (!response.ok) {
                    throw new Error(`Tracks: HTTP ${response.status}`);
                }
                return response.json();
            })
        ]);
        const [alertResult, sourceResult, statsResult, trackResult] = results;
        const errors = [];
        // ALERT SUMMARY
        if (alertResult.status === 'fulfilled') {
            const alertData = Array.isArray(alertResult.value)
                ? alertResult.value : [];
            setAlerts(alertData);
            const severitySummary = {
                HIGH: 0,
                MEDIUM: 0,
                LOW: 0
            };
            const eventTypeSummary = {
                DUPLICATE: 0,
                OUT_OF_ORDER: 0,
                REJECTED: 0,
                SOURCE_HEALTH: 0
            };
            alertData.forEach(alert => {
                const severity = String(
                    alert.alertSeverity ?? ''
                ).toUpperCase();
                if (severity in severitySummary) {
                    severitySummary[severity]++;
                }
                const eventType = getEventType(alert);
                eventTypeSummary[eventType]++;
            });
            setSummary({
                totalAlerts: alertData.length,
                severitySummary,
                eventTypeSummary
            });
        } else {
            errors.push('Alerts: ' + alertResult.reason.message);
        }
        let sourceData = [];
        let trackData = [];
        // SOURCE REGISTRY
        if (sourceResult.status === 'fulfilled') {
            sourceData = Array.isArray(sourceResult.value)
                ? sourceResult.value : [];
            setSources(sourceData);
        } else {
            errors.push('Sources: ' + sourceResult.reason.message);
        }
        // TRACK STATISTICS
        if (statsResult.status === 'fulfilled') {
            setTrackStats(statsResult.value);
        } else {
            errors.push('Statistics: ' + statsResult.reason.message);
        }
        // TRACK DATA
        if (trackResult.status === 'fulfilled') {
            trackData = Array.isArray(trackResult.value)
                ? trackResult.value : [];
        } else {
            errors.push('Tracks: ' + trackResult.reason.message);
        }
        // OVERALL ANALYTICS
        if (
            sourceResult.status === 'fulfilled' &&
            trackResult.status === 'fulfilled'
        ) {
            const sum = field => sourceData.reduce(
                (total, source) =>
                    total + (Number(source[field]) || 0), 0
            );
            const accepted = sum('acceptedCount');
            const rejected = sum('rejectedCount');
            const duplicate = sum('duplicateCount');
            const failures = sum('processingFailureCount');
            setOverallAnalytics({
                totalProcessed: sum('receivedCount'),
                acceptedCount: accepted,
                rejectedCount: rejected,
                duplicateCount: duplicate,
                highSpeedTracks: trackData.filter(
                    track => Number(track.speed) > 1000
                )
            });
        }
        setError(errors.length ? errors.join(' | ') : null);
        setLastUpdated(new Date());
        setLoading(false);
    }, []);
    // MANUAL REFRESH
    const handleRefresh = async () => {
        setRefreshing(true);
        try {
            await refreshDashboard();
        } catch (err) {
            setError(err.message || 'Failed to refresh dashboard.');
        } finally {
            setRefreshing(false);
        }
    };
    // AUTO REFRESH EVERY 2 SECONDS
    useEffect(() => {
        let active = true;
        let busy = false;
        const load = async () => {
            if (!active || busy) return;
            busy = true;
            try {
                await refreshDashboard();
            } catch (err) {
                if (active) {
                    setError(err.message || 'Dashboard refresh failed.');
                    setLoading(false);
                }
            } finally {
                busy = false;
            }
        };
        load();
        const intervalId = setInterval(load, 2000);
        return () => {
            active = false;
            clearInterval(intervalId);
        };
    }, [refreshDashboard]);
    // FILTER ALERTS BY SEVERITY
    const filteredAlerts = selectedSeverity === 'ALL'
        ? alerts
        : alerts.filter(alert =>
            String(alert.alertSeverity ?? '').toUpperCase()
            === selectedSeverity
        );
    // LOADING
    if (loading) {
        return (
            <div className="app-container">
                <main className="app-content">
                    <p>Loading Alerts and Source Health Dashboard...</p>
                </main>
            </div>
        );
    }
    return (
        <div className="app-container">
            <main className="app-content">
                {/* HEADER */}
                <header className="app-content-header">
                    <div>
                        <h1 className="app-content-header-text">
                            Alerts and Source Health Dashboard
                        </h1>
                    </div>
                    <div className="header-actions">



            <span className="last-updated">

              {lastUpdated
                  ? `Updated: ${lastUpdated.toLocaleTimeString()}`
                  : 'Waiting for data'}

            </span>
                        <button
                            className="app-content-headerButton"
                            onClick={handleRefresh}
                            disabled={refreshing}
                        >
                            {refreshing ? 'Refreshing...' : 'Refresh'}
                        </button>
                    </div>
                </header>
                {/* ERROR */}
                {error && (
                    <div role="alert" className="error-message">
                        <strong>Error:</strong> {error}
                    </div>
                )}
                <Grid container spacing={3}>
                    <Grid size={12}>
                        <Grid container spacing={3}>
                            <Grid size={{xs: 12, md: 5}}>
                                {/* ALERT SUMMARY */}
                                <AlertSummaryCard
                                    summary={summary}
                                    analytics={overallAnalytics}
                                />
                            </Grid>
                            <Grid size={{xs: 12, md: 7}}>
                                {/* SOURCE REGISTRY */}
                                <SourceRegistryCard
                                    sources={sources}
                                    loading={false}
                                    error={null}
                                />
                            </Grid>
                        </Grid>
                    </Grid>
                    <Grid size={12}>
                        <Grid container spacing={3}>
                            <Grid size={{xs: 12, md: 6}}>
                                {/* ALERT EVENTS */}
                                <AlertEventsCard
                                    alerts={filteredAlerts}
                                    selectedSeverity={selectedSeverity}
                                    onSelectSeverity={setSelectedSeverity}
                                    filtering={false}
                                />
                            </Grid>
                            <Grid size={{xs: 12, md: 6}}>
                                {/* SPEED ANALYTICS */}
                                <SpeedAnalyticsCard
                                    analytics={overallAnalytics}
                                />
                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>
            </main>
        </div>
    );
}