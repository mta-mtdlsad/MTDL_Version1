import React from 'react';
import {
    MapContainer,
    TileLayer,
    Marker,
    Popup
} from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css'
import '../../styles/App.css'


import { IDENTITY_COLOURS, PLATFORM_TYPES, createTrackIcon, platformIconDataUri, platformTypeLabel } from '../../utils/trackSymbology.js';

export const identityColors = IDENTITY_COLOURS;

function CurrentMap({ tracks = [] }) {
    return (
        <section className="dashboard-panel">
            <div className="map-header">
                <h2>Current Track Locations</h2>
                <span className="live-indicator">
                                                ● Live Updates
                                            </span>

            </div>
            <div className="current-map">
                <MapContainer
                    center={[24.4539, 54.3773]}
                    zoom={8}
                    style={{width: '100%', height: '100%'}}>
                    <TileLayer attribution="&copy; OpenStreetMap contributors" url="https://tile.openstreetmap.org/{z}/{x}/{y}.png"/>
                    {tracks.map(track => (
                        <Marker
                            key={track.trackId}
                            position={[
                                track.latitude,
                                track.longitude
                            ]} icon={createTrackIcon(L, track.identityType, track.trackType)}>
                            <Popup>
                                <div className="track-popup">
                                    <h3>Track {track.trackNumber}</h3>
                                    <p><b>Track ID:</b> {track.trackId}</p>
                                    <p><b>Source ID:</b> {track.sourceId}</p>
                                    <p><b>Sequence:</b> {track.sequenceNumber}</p>
                                    <p><b>Path ID:</b> {track.pathId}</p>
                                    <p><b>Track Number:</b> {track.trackNumber}</p>
                                    <p><b>Latitude:</b> {track.latitude}</p>
                                    <p><b>Longitude:</b> {track.longitude}</p>
                                    <p><b>Speed:</b> {track.speed}</p>
                                    <p><b>Platform Type:</b> {platformTypeLabel(track.trackType)}</p>
                                    <p><b>Identity:</b> {track.identityType}</p>
                                    <p><b>Status:</b> {track.trackStatus}</p>
                                </div>
                            </Popup>
                        </Marker>
                    ))}
                </MapContainer>
            </div>

            <div className="map-legend">
                {Object.entries(identityColors).map(
                    ([identity, color]) => (
                        <div className="legend-item" key={identity}>
                            <span className="legend-dot" style={{ backgroundColor: color }}/>
                            {identity}
                        </div>
                    )
                )}
            </div>
            <div className="platform-legend" aria-label="Platform type symbology">
                {PLATFORM_TYPES.map(type => (
                    <div className="platform-legend-item" key={type}>
                        <img src={platformIconDataUri(type)} alt="" />
                        {platformTypeLabel(type)}
                    </div>
                ))}
            </div>
        </section>
    );
}

export default CurrentMap;