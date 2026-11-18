import React, { useState, useEffect, useCallback } from 'react';
import '../styles/dashboard.css';
import {
    fetchAllAlerts,
    fetchAlertsBySeverity,
    fetchAlertSummary,
} from '../api/alertsApi';
import { fetchAllSources } from '../api/sourcesApi';
import { fetchTrackStatistics } from '../api/statisticsApi';
import { fetchOverallAnalytics } from '../api/analyticsApi';
import { fetchSimulationState, controlSimulation } from '../api/simulationApi';
import { fetchCorrelations, fetchNearestPairs } from '../api/correlationsApi';
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

    const refreshDashboard = useCallback(async (activeSeverity) => {
        const [
            currentSimState,
            summaryData,
            statsData,
            overallData,
            correlationList,
            nearestList,
            alertList,
            sourceList,
        ] = await Promise.all([
            fetchSimulationState(),
            fetchAlertSummary(),
            fetchTrackStatistics(),
            fetchOverallAnalytics(),
            fetchCorrelations(),
            fetchNearestPairs(),
            activeSeverity === 'ALL'
                ? fetchAllAlerts()
                : fetchAlertsBySeverity(activeSeverity),
            fetchAllSources(),
        ]);

        setSimState((prevState) => {
            if (
                prevState.toUpperCase() === 'RUNNING' &&
                currentSimState.toUpperCase() === 'STOPPED'
            ) {
                setSimFeedback('Simulation completed and stopped.');
            }
            return currentSimState;
        });

        setSummary(summaryData);
        setTrackStats(statsData);
        setOverallAnalytics(overallData);
        setCorrelations(correlationList);
        setNearestPairs(nearestList);
        setAlerts(alertList);
        setSources(sourceList);
    }, []);


    useEffect(() => {
        let isMounted = true;

        async function load() {
            try {
                await refreshDashboard(selectedSeverity);
                if (isMounted) setError(null);
            } catch (err) {
                if (isMounted) {
                    setError(err.message || 'Network error while loading dashboard data.');
                }
            } finally {
                if (isMounted) setLoading(false);
            }
        }

        load();
        const intervalId = setInterval(load, 2000);

        return () => {
            isMounted = false;
            clearInterval(intervalId);
        };
    }, [refreshDashboard, selectedSeverity]);

    const handleSelectSeverity = async (severity) => {
        setSelectedSeverity(severity);
        setFiltering(true);
        setError(null);
        try {
            const data =
                severity === 'ALL'
                    ? await fetchAllAlerts()
                    : await fetchAlertsBySeverity(severity);
            setAlerts(data);
        } catch (err) {
            setError(err.message || 'Failed to filter alerts.');
        } finally {
            setFiltering(false);
        }
    };

    const handleSimulationControl = async (action) => {
        setSimPending(true);
        setSimFeedback(null);
        setError(null);
        try {
            const responseText = await controlSimulation(action);
            setSimFeedback(responseText);
            await refreshDashboard(selectedSeverity);
        } catch (err) {
            setError(err.message || 'Failed to control simulator.');
        } finally {
            setSimPending(false);
        }
    };

    if (loading) {
        return (
            <div role="status" aria-live="polite" style={{ padding: '24px', color: '#111827' }}>
                Loading Alerts and Source Health Dashboard...
            </div>
        );
    }

    return (
        <div style={{ backgroundColor: '#f9fafb', minHeight: '100vh', padding: '20px' }}>
            <div style={{ maxWidth: '1100px', margin: '0 auto' }}>
                <SimulationControlCard
                    simulationState={simState}
                    isPending={simPending}
                    feedbackMessage={simFeedback}
                    onControl={handleSimulationControl}
                />

                {error && (
                    <div
                        role="alert"
                        style={{
                            backgroundColor: '#fef2f2',
                            border: '1px solid #f87171',
                            color: '#991b1b',
                            padding: '12px 16px',
                            borderRadius: '6px',
                            marginBottom: '16px',
                        }}
                    >
                        <strong>Error:</strong> {error}
                    </div>
                )}

                <AlertSummaryCard summary={summary} />

                <TrackStatisticsCard stats={trackStats} />

                <OverallAnalyticsCard analytics={overallAnalytics} />

                <CorrelationsCard
                    correlations={correlations}
                    nearestPairs={nearestPairs}
                />

                <SourceRegistryCard
                    sources={sources}
                    loading={false}
                    error={null}
                />

                <AlertEventsCard
                    alerts={alerts}
                    selectedSeverity={selectedSeverity}
                    onSelectSeverity={handleSelectSeverity}
                    filtering={filtering}
                />
            </div>
        </div>
    );
}
