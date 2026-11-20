import {useState, useEffect, useCallback} from 'react';
import Grid from '@mui/material/Grid';
import '../styles/App.css';
import {fetchAllAlerts} from '../api/alertsApi';
import {fetchAllSources} from '../api/sourcesApi';
import {fetchTrackStatistics} from '../api/statisticsApi';
import AlertSummaryCard from '../components/cards/AlertSummaryCard.jsx';
import TrackStatisticsCard from '../components/cards/TrackStatisticsCard.jsx';
import OverallAnalyticsCard from '../components/cards/OverallAnalyticsCard.jsx';
import AlertEventsCard from '../components/cards/AlertEventsCard.jsx';
import SourceRegistryCard from '../components/cards/SourceRegistryCard.jsx';
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
    // Fetch all dashboard data
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
        // Alert data and summary
        if (alertResult.status === 'fulfilled') {
            const alertData = Array.isArray(alertResult.value)
                ? alertResult.value : [];
            setAlerts(alertData);
            const severitySummary = {
                HIGH: 0,
                MEDIUM: 0,
                LOW: 0
            };
            const eventTypeSummary = {};
            alertData.forEach(alert => {
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
        // Source registry
        if (sourceResult.status === 'fulfilled') {
            sourceData = Array.isArray(sourceResult.value)
                ? sourceResult.value : [];
            setSources(sourceData);
        } else {
            errors.push('Sources: ' + sourceResult.reason.message);
        }
        // Track statistics
        if (statsResult.status === 'fulfilled') {
            setTrackStats(statsResult.value);
        } else {
            errors.push('Statistics: ' + statsResult.reason.message);
        }
        // Track data
        if (trackResult.status === 'fulfilled') {
            trackData = Array.isArray(trackResult.value)
                ? trackResult.value : [];
        } else {
            errors.push('Tracks: ' + trackResult.reason.message);
        }
        // Overall analytics
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
                totalProcessed: accepted + rejected + duplicate + failures,
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
    // Manual refresh without reloading the page
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
    // Automatically refresh every 2 seconds
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
    // Filter alerts by severity
    const filteredAlerts = selectedSeverity === 'ALL'
        ? alerts
        : alerts.filter(alert =>
            String(alert.alertSeverity ?? '').toUpperCase()
            === selectedSeverity
        );
    // Loading screen
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
                {/* ERROR MESSAGE */}
                {error && (
                    <div role="alert" className="error-message">
                        <strong>Error:</strong> {error}
                    </div>
                )}
                <Grid container spacing={3}>
                    {/* ROW 1: ALERT SUMMARY */}
                    <Grid size={12}>
                        <AlertSummaryCard summary={summary}/>
                    </Grid>
                    {/* ROW 2: TRACK STATISTICS AND ANALYTICS */}
                    <Grid size={{xs: 12, lg: 5}}>
                        <TrackStatisticsCard stats={trackStats}/>
                    </Grid>
                    <Grid size={{xs: 12, lg: 7}}>
                        <OverallAnalyticsCard
                            analytics={overallAnalytics}
                        />
                    </Grid>
                    {/* ROW 3: SOURCE REGISTRY */}
                    <Grid size={12}>
                        <SourceRegistryCard
                            sources={sources}
                            loading={false}
                            error={null}
                        />
                    </Grid>
                    {/* ROW 4: ALERT EVENTS */}
                    <Grid size={12}>
                        <AlertEventsCard
                            alerts={filteredAlerts}
                            selectedSeverity={selectedSeverity}
                            onSelectSeverity={setSelectedSeverity}
                            filtering={false}
                        />
                    </Grid>
                </Grid>
            </main>
        </div>
    );
}