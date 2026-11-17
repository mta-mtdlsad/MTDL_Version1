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


// Identity colours
export const identityColors = {
    FRIEND: '#22c55e',
    HOSTILE: '#ef4444',
    NEUTRAL: '#3b82f6',
    UNKNOWN: '#f59e0b'
};
// Create custom marker
function createTrackIcon(identity) {
    const color = identityColors[identity];
    return L.divIcon({
        className: 'custom-track-icon',
        html: `
   <div class="track-marker"
      style="background-color: ${color}">
   </div>
  `,
        iconSize: [20, 20],
        iconAnchor: [10, 10]
    });
}
function CurrentMap({ tracks = [] }) {
    return (
        <div>
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
                            ]} icon={createTrackIcon(track.identityType)}>
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
        </div>
    );
}

export default CurrentMap;
