import {useEffect, useRef, useState} from "react";
import {
    MapContainer,
    TileLayer,
    Marker,
    Polyline,
    Popup,
    useMap,
} from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { IDENTITY_COLOURS, PLATFORM_TYPES, createTrackIcon, platformIconDataUri, platformTypeLabel } from "../../utils/trackSymbology.js";

function getIdentityColour(identity) {
    return IDENTITY_COLOURS[String(identity ?? "UNKNOWN").toUpperCase()] ?? IDENTITY_COLOURS.UNKNOWN;
}

// Move the map to follow the current track position
function MapFollower({position}) {
    const map = useMap();
    const latitude = position?.[0];
    const longitude = position?.[1];
    useEffect(() => {
        if (latitude != null && longitude != null) {
            map.panTo([latitude, longitude], {
                animate: true,
                duration: 0.4,
            });
        }
    }, [map, latitude, longitude]);
    return null;
}
function TrackReplay() {
    // Replay settings
    const [trackOptions, setTrackOptions] = useState([]);
    const [selectedTrack, setSelectedTrack] = useState("");
    const [start, setStart] = useState("");
    const [end, setEnd] = useState("");
    const [speed, setSpeed] = useState(1);
    // Replay data and playback state
    const [history, setHistory] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [playing, setPlaying] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const timerRef = useRef(null);
    // Fetch available tracks from the backend
    useEffect(() => {
        let cancelled = false;
        async function loadTracks() {
            try {
                const response = await fetch("/api/tracks/list");
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}`);
                }
                const data = await response.json();
                if (!Array.isArray(data)) {
                    throw new Error("Invalid track list response");
                }
                // Extract unique track numbers
                const options = [
                    ...new Set(
                        data
                            .map((track) =>
                                typeof track === "string"
                                    ? track
                                    : track.trackNumber
                            )
                            .filter(Boolean)
                    ),
                ].sort();
                if (!cancelled) {
                    setTrackOptions(options);
                    setError("");
                }
            } catch (err) {
                console.error("Unable to load tracks:", err);
                if (!cancelled) {
                    setTrackOptions([]);
                    setError("Unable to load available tracks.");
                }
            }
        }
        loadTracks();
        return () => {
            cancelled = true;
        };
    }, []);
    // Move to the next position based on playback speed
    useEffect(() => {
        if (!playing || history.length < 2) return;
        timerRef.current = setInterval(() => {
            setCurrentIndex((previous) => {
                if (previous >= history.length - 1) {
                    setPlaying(false);
                    return previous;
                }
                return previous + 1;
            });
        }, 1000 / speed);
        return () => clearInterval(timerRef.current);
    }, [playing, speed, history.length]);
    // Clear the timer when leaving the page
    useEffect(() => {
        return () => clearInterval(timerRef.current);
    }, []);
    // Fetch historical positions for the selected track
    async function loadReplay() {
        setError("");
        setPlaying(false);
        setHistory([]);
        setCurrentIndex(0);
        // Check required fields
        if (!selectedTrack || !start || !end) {
            setError("Select a track, start date and end date.");
            return;
        }
        // Validate date range
        if (new Date(start) > new Date(end)) {
            setError("Start date must be before end date.");
            return;
        }
        setLoading(true);
        try {
            const params = new URLSearchParams({
                start,
                end,
                trackNumber: selectedTrack,
            });
            const response = await fetch(
                `/api/tracks/history?${params.toString()}`
            );
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }
            const data = await response.json();
            if (!Array.isArray(data)) {
                throw new Error("Invalid history response");
            }
            // Keep the selected track with valid coordinates
            const filtered = data
                .filter(track =>
                    String(track.trackNumber) === String(selectedTrack) &&
                    track.latitude != null &&
                    track.longitude != null &&
                    Number.isFinite(Number(track.latitude)) &&
                    Number.isFinite(Number(track.longitude)) &&
                    Math.abs(Number(track.latitude)) <= 90 &&
                    Math.abs(Number(track.longitude)) <= 180
                )
                .sort(
                    (a, b) =>
                        new Date(a.createdTime) - new Date(b.createdTime)
                );

            setHistory(filtered);
            if (filtered.length === 0) {
                setError("No historical positions found for this track.");
            }
        } catch (err) {
            console.error("Replay loading failed:", err);
            setError("Unable to load track history.");
        } finally {
            setLoading(false);
        }
    }
    // Get the track at the current replay position
    const currentTrack = history[currentIndex] ?? null;
    // Get its coordinates for the map marker
    const currentPosition = currentTrack
        ? [
            Number(currentTrack.latitude),
            Number(currentTrack.longitude),
        ]
        : null;
    // Get all coordinates for the complete route
    const fullPath = history.map((track) => [
        Number(track.latitude),
        Number(track.longitude),
    ]);
    // Build the travelled route using identity colours
    const travelledSegments = history
        .slice(1, currentIndex + 1)
        .map((track, index) => ({
            positions: [
                [
                    Number(history[index].latitude),
                    Number(history[index].longitude),
                ],
                [
                    Number(track.latitude),
                    Number(track.longitude),
                ],
            ],
            colour: getIdentityColour(track.identityType),
        }));
    // Current identity colour
    const currentColour = getIdentityColour(
        currentTrack?.identityType
    );
    // Start or restart playback
    function startPlayback() {
        if (history.length < 2) return;
        if (currentIndex >= history.length - 1) {
            setCurrentIndex(0);
        }
        setPlaying(true);
    }
    // Pause playback
    function pausePlayback() {
        setPlaying(false);
    }
    // Return to the first position
    function resetPlayback() {
        setPlaying(false);
        setCurrentIndex(0);
    }
    return (
        <div>
            {/* Replay configuration */}
            <section className="replay-panel">
                <div>
                    <h1 className="app-content-headerText">
                        Track Replay
                    </h1>
                </div> <br/><br/>
                <div className="replay-section-heading">
                    <h2>Replay Configuration</h2>
                </div>
                <div className="replay-form">
                    {/* Track selection */}
                    <div className="replay-field">
                        <label>Select Track</label>
                        <select
                            value={selectedTrack}
                            onChange={(e) =>
                                setSelectedTrack(e.target.value)
                            }
                        >
                            <option value="">Select track</option>
                            {trackOptions.map((trackNumber) => (
                                <option key={trackNumber} value={trackNumber}>
                                    {trackNumber}
                                </option>
                            ))}
                        </select>
                    </div>
                    {/* Start date */}
                    <div className="replay-field">
                        <label>Start Date & Time</label>
                        <input
                            type="datetime-local"
                            value={start}
                            onChange={(e) => setStart(e.target.value)}
                        />
                    </div>
                    {/* End date */}
                    <div className="replay-field">
                        <label>End Date & Time</label>
                        <input
                            type="datetime-local"
                            value={end}
                            onChange={(e) => setEnd(e.target.value)}
                        />
                    </div>
                    {/* Playback speed */}
                    <div className="replay-field">
                        <label>Playback Speed</label>
                        <select
                            value={speed}
                            onChange={(e) =>
                                setSpeed(Number(e.target.value))
                            }
                        >
                            <option value={0.5}>Half Speed (0.5Ã—)</option>
                            <option value={1}>Normal (1Ã—)</option>
                            <option value={2}>Double Speed (2Ã—)</option>
                        </select>
                    </div>
                </div>
                {/* Load replay */}
                <div className="replay-config-footer">
                    <button
                        className="replay-button replay-primary"
                        onClick={loadReplay}
                        disabled={loading}
                    >
                        {loading ? "Loading..." : "Load Replay"}
                    </button>

                </div>
                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}
                {/* Replay map */}
                <br/><br/>
                <div className="replay-map-heading">
                    <div>
                        <h2>Track Movement</h2>
                    </div>
                    {/* Current identity badge */}
                    {currentTrack && (
                        <span
                            className="replay-identity-badge"
                            style={{
                                color: currentColour,
                                borderColor: currentColour,
                                backgroundColor: `${currentColour}18`,
                            }}
                        >
        <span
            className="replay-identity-dot"
            style={{backgroundColor: currentColour}}
        />
                            {currentTrack.identityType ?? "UNKNOWN"}
       </span>
                    )}
                </div>
                <div className="replay-map">
                    <MapContainer
                        center={[24.4539, 54.3773]}
                        zoom={7}
                        style={{height: "100%", width: "100%"}}
                    >
                        {/* Map background */}
                        <TileLayer
                            attribution="&copy; OpenStreetMap contributors"
                            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                        />
                        {/* Current track marker */}
                        {currentPosition && (
                            <>
                                <MapFollower position={currentPosition}/>
                                <Marker
                                    position={currentPosition}
                                    icon={createTrackIcon(L, currentTrack.identityType, currentTrack.trackType ?? currentTrack.platformType, true)}
                                >
                                    <Popup>
                                        <div className="replay-popup">
                                            <strong>{currentTrack.trackNumber}</strong>
                                            <p>Source: {currentTrack.sourceId}</p>
                                            <p>Speed: {currentTrack.speed}</p>
                                            <p>Platform Type: {platformTypeLabel(currentTrack.trackType ?? currentTrack.platformType)}</p>
                                            <p>
                                                Identity:{" "}
                                                <strong style={{color: currentColour}}>
                                                    {currentTrack.identityType}
                                                </strong>
                                            </p>
                                            <p>Status: {currentTrack.trackStatus}</p>
                                            <p>Time: {currentTrack.createdTime}</p>
                                        </div>
                                    </Popup>
                                </Marker>
                            </>
                        )}
                        {/* Full route shown as a dashed line */}
                        {fullPath.length > 1 && (
                            <Polyline
                                positions={fullPath}
                                pathOptions={{
                                    color: "#94a3b8",
                                    weight: 3,
                                    opacity: 0.35,
                                    dashArray: "6, 8",
                                }}
                            />
                        )}
                        {/* Travelled route coloured by identity */}
                        {travelledSegments.map((segment, index) => (
                            <Polyline
                                key={index}
                                positions={segment.positions}
                                pathOptions={{
                                    color: segment.colour,
                                    weight: 5,
                                    opacity: 0.95,
                                }}
                            />
                        ))}
                    </MapContainer>
                </div>
                {/* Identity colour legend */}
                <div className="replay-legend">
                    {Object.entries(IDENTITY_COLOURS).map(
                        ([identity, colour]) => (
                            <div className="replay-legend-item" key={identity}>

         <span
             className="replay-identity-dot"
             style={{backgroundColor: colour}}
         />
                                {identity}
                            </div>
                        )
                    )}
                </div>
                <div className="platform-legend" aria-label="Replay platform type symbology">
                    {PLATFORM_TYPES.map(type => (
                        <div className="platform-legend-item" key={type}>
                            <img src={platformIconDataUri(type)} alt="" />
                            {platformTypeLabel(type)}
                        </div>
                    ))}
                </div>
                {/* Playback controls */}
                <div className="replay-playback-panel">
                    <div className="replay-controls"style={{
                        display: "flex",
                        justifyContent: "center",
                        alignItems: "center",
                        gap: "15px",
                        width: "100%"
                    }} >
                        <button
                            className="replay-button replay-play"
                            onClick={startPlayback}
                            disabled={history.length < 2 || playing}
                        >
                            â–¶ Play
                        </button>
                        <button
                            className="replay-button replay-pause"
                            onClick={pausePlayback}
                            disabled={!playing}
                        >
                            âšâš Pause
                        </button>
                        <button
                            className="replay-button replay-reset"
                            onClick={resetPlayback}
                            disabled={history.length === 0}
                        >
                            â†º Reset
                        </button>
                    </div>
                    {/* Playback progress */}
                    {history.length > 0 ? (
                        <div className="replay-progress">
                            <div className="replay-progress-header">
         <span>
          Position {currentIndex + 1} of {history.length}
         </span>
                                <span>
          {Math.round(((currentIndex + 1) / history.length) * 100)}%
         </span>
                            </div>
                            {/* Move manually between recorded positions */}
                            <input
                                type="range"
                                min="0"
                                max={history.length - 1}
                                value={currentIndex}
                                onChange={(e) => {
                                    setPlaying(false);
                                    setCurrentIndex(Number(e.target.value));
                                }}
                            />

                        </div>
                    ) : (
                        <p className="replay-empty-message">
                        </p>
                    )}
                </div>

                {/* Current track information */}
                {currentTrack && (
                    <section>
                        <br/>
                        <br/>
                        <h2>Current Replay Position</h2>
                        <div className="replay-details-grid">
                            <div className="replay-detail">
                                <span>Track Number</span>
                                <strong>{currentTrack.trackNumber}</strong>
                            </div>
                            <div className="replay-detail">
                                <span>Source ID</span>
                                <strong>{currentTrack.sourceId}</strong>
                            </div>
                            <div className="replay-detail">
                                <span>Identity</span>
                                <strong style={{color: currentColour}}>
                                    {currentTrack.identityType}
                                </strong>
                            </div>
                            <div className="replay-detail">
                                <span>Track Status</span>
                                <strong>{currentTrack.trackStatus}</strong>
                            </div>
                            <div className="replay-detail">
                                <span>Latitude</span>
                                <strong>{currentTrack.latitude}</strong>
                            </div>
                            <div className="replay-detail">
                                <span>Longitude</span>
                                <strong>{currentTrack.longitude}</strong>
                            </div>
                            <div className="replay-detail">
                                <span>Speed</span>
                                <strong>{currentTrack.speed}</strong>
                            </div>
                            <div className="replay-detail">
                                <span>Sequence Number</span>
                                <strong>{currentTrack.sequenceNumber}</strong>
                            </div>
                        </div>
                    </section>
                )}
            </section>
        </div>
    );
}
export default TrackReplay;