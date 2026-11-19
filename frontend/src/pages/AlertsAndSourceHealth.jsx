import React, {useState, useEffect, useCallback} from 'react';
import Grid from '@mui/material/Grid';
import '../styles/App.css';
import {
    fetchAllAlerts,
    fetchAlertsBySeverity,
    fetchAlertSummary
} from '../api/alertsApi';
import {fetchAllSources} from '../api/sourcesApi';
import {fetchTrackStatistics} from '../api/statisticsApi';
import {fetchOverallAnalytics} from '../api/analyticsApi';
import {
    fetchSimulationState,
    controlSimulation
} from '../api/simulationApi';
import {
    fetchCorrelations,
    fetchNearestPairs
} from '../api/correlationsApi';
import SimulationControlCard from '../components/cards/SimulationControlCard.jsx';
import AlertSummaryCard from '../components/cards/AlertSummaryCard.jsx';
import TrackStatisticsCard from '../components/cards/TrackStatisticsCard.jsx';
import OverallAnalyticsCard from '../components/cards/OverallAnalyticsCard.jsx';
import CorrelationsCard from '../components/cards/CorrelationsCard.jsx';
import AlertEventsCard from '../components/cards/AlertEventsCard.jsx';
import SourceRegistryCard from '../components/cards/SourceRegistryCard.jsx';
export default function AlertsDashboardSection() {
    const [simState, setSimState] = useState('UNKNOWN');
    const [simPending, setSimPending] = useState(false);
    const [simFeedback, setSimFeedback] = useState(null);
    const [summary, setSummary] = useState(null);
    const [trackStats, setTrackStats] = useState(null);
    const [overallAnalytics, setOverallAnalytics] = useState(null);
    const [correlations, setCorrelations] = useState([]);
    const [nearestPairs, setNearestPairs] = useState([]);
    const [alerts, setAlerts] = useState([]);
    const [sources, setSources] = useState([]);
    const [selectedSeverity, setSelectedSeverity] = useState('ALL');
    const [loading, setLoading] = useState(true);
    const [filtering, setFiltering] = useState(false);
    const [error, setError] = useState(null);
    // Fetch all dashboard data
    const refreshDashboard = useCallback(async (severity) => {
        const [
            currentSimState,
            summaryData,
            statsData,
            overallData,
            correlationList,
            nearestList,
            alertList,
            sourceList
        ] = await Promise.all([
            fetchSimulationState(),
            fetchAlertSummary(),
            fetchTrackStatistics(),
            fetchOverallAnalytics(),
            fetchCorrelations(),
            fetchNearestPairs(),
            severity === 'ALL'
                ? fetchAllAlerts()
                : fetchAlertsBySeverity(severity),
            fetchAllSources()
        ]);
        const nextState = String(currentSimState ?? 'UNKNOWN');
        setSimState(previous => {
            if (
                String(previous).toUpperCase() === 'RUNNING' &&
                nextState.toUpperCase() === 'STOPPED'
            ) {
                setSimFeedback('Simulation completed and stopped.');
            }
            return nextState;
        });
        setSummary(summaryData);
        setTrackStats(statsData);
        setOverallAnalytics(overallData);
        setCorrelations(
            Array.isArray(correlationList) ? correlationList : []
        );
        setNearestPairs(
            Array.isArray(nearestList) ? nearestList : []
        );
        setAlerts(Array.isArray(alertList) ? alertList : []);
        setSources(Array.isArray(sourceList) ? sourceList : []);
    }, []);
    // Refresh automatically every 2 seconds
    useEffect(() => {
        let active = true;
        let busy = false;
        async function load() {
            if (busy) return;
            busy = true;
            try {
                await refreshDashboard(selectedSeverity);
                if (active) setError(null);
            } catch (err) {
                if (active) {
                    setError(
                        err.message ||
                        'Network error while loading dashboard data.'
                    );
                }
            } finally {
                if (active) setLoading(false);
                busy = false;
            }
        }
        load();
        const intervalId = setInterval(load, 2000);
        return () => {
            active = false;
            clearInterval(intervalId);
        };
    }, [refreshDashboard, selectedSeverity]);
    // Filter alerts by severity
    const handleSelectSeverity = async (severity) => {
        setSelectedSeverity(severity);
        setFiltering(true);
        setError(null);
        try {
            const data = severity === 'ALL'
                ? await fetchAllAlerts()
                : await fetchAlertsBySeverity(severity);
            setAlerts(Array.isArray(data) ? data : []);
        } catch (err) {
            setError(err.message || 'Failed to filter alerts.');
        } finally {
            setFiltering(false);
        }
    };
    // Start or stop simulation
    const handleSimulationControl = async (action) => {
        setSimPending(true);
        setSimFeedback(null);
        setError(null);
        try {
            const message = await controlSimulation(action);
            setSimFeedback(message);
            await refreshDashboard(selectedSeverity);
        } catch (err) {
            setError(err.message || 'Failed to control simulator.');
        } finally {
            setSimPending(false);
        }
    };
    if (loading) {
        return (
            <div className="app-container">
                <main className="app-content">
                    <p className="page-subtitle" role="status">
                        Loading Alerts and Source Health Dashboard...
                    </p>
                </main>
            </div>
        );
    }
    return (
        <div className="app-container">
            <main className="app-content">
                {/* HEADER AND SIMULATION CONTROLS */}
                <SimulationControlCard
                    simulationState={simState}
                    isPending={simPending}
                    feedbackMessage={simFeedback}
                    onControl={handleSimulationControl}
                />
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
                    {/* ROW 2: TRACKS AND ANALYTICS */}
                    <Grid size={{xs: 12, lg: 5}}>
                        <TrackStatisticsCard stats={trackStats}/>
                    </Grid>
                    <Grid size={{xs: 12, lg: 7}}>
                        <OverallAnalyticsCard
                            analytics={overallAnalytics}
                        />
                    </Grid>
                    {/* ROW 3: CORRELATIONS */}
                    <Grid size={12}>
                        <CorrelationsCard
                            correlations={correlations}
                            nearestPairs={nearestPairs}
                        />
                    </Grid>
                    {/* ROW 4: SOURCE REGISTRY */}
                    <Grid size={12}>
                        <SourceRegistryCard
                            sources={sources}
                            loading={false}
                            error={null}
                        />
                    </Grid>
                    {/* ROW 5: ALERT EVENTS */}
                    <Grid size={12}>
                        <AlertEventsCard
                            alerts={alerts}
                            selectedSeverity={selectedSeverity}
                            onSelectSeverity={handleSelectSeverity}
                            filtering={filtering}
                        />
                    </Grid>
                </Grid>
            </main>
        </div>
    );
}