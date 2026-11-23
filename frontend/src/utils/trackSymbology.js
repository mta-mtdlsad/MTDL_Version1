export const IDENTITY_COLOURS = {
    FRIEND: "#22c55e",
    HOSTILE: "#ef4444",
    NEUTRAL: "#3b82f6",
    UNKNOWN: "#f59e0b",
};

export const PLATFORM_TYPES = ["SHIP", "AIRCRAFT", "LAND_VEHICLE", "UNKNOWN"];

const platformLabels = {
    SHIP: "Ship",
    AIRCRAFT: "Aircraft",
    LAND_VEHICLE: "Land vehicle",
    UNKNOWN: "Unknown platform",
};

const platformSvg = {
    SHIP: '<path d="M4 22h28l-4.1 8.1a3.2 3.2 0 0 1-2.9 1.8H11a3.2 3.2 0 0 1-2.9-1.8z"/><path d="M11 20V11h12v9m3 0v-6h4v6M15 8h4v3h-4z"/><path d="M7 34c2.2-1.6 4.4-1.6 6.6 0 2.2-1.6 4.4-1.6 6.6 0 2.2-1.6 4.4-1.6 6.6 0" fill="none" stroke="#0f172a" stroke-width="1.8" stroke-linecap="round"/>',
    AIRCRAFT: '<path d="M18 2.2c1.5 0 2.5 1.3 2.8 3.1l1.6 9.2 10.2 6.1v3.2l-10-2.7.1 7.2 4.5 3.4v2.1L18 32l-9.2 1.8v-2.1l4.5-3.4.1-7.2-10 2.7v-3.2l10.2-6.1 1.6-9.2c.3-1.8 1.3-3.1 2.8-3.1z"/>',
    LAND_VEHICLE: '<path d="M5 13h16.5c1.6 0 2.7.6 3.7 1.8l3.5 4.2h2.1c1.2 0 2.2 1 2.2 2.2V28H3v-8.2c0-2.7.6-4.8 2-6.8zm3 3-2 5h17l-4-5z"/><circle cx="10" cy="28" r="4"/><circle cx="27" cy="28" r="4"/><circle cx="10" cy="28" r="1.5" fill="#0f172a"/><circle cx="27" cy="28" r="1.5" fill="#0f172a"/>',
    UNKNOWN: '<circle cx="18" cy="18" r="12" fill="none" stroke="#fff" stroke-width="3"/><path d="M18 10v9m0 5v1" stroke="#fff" stroke-width="3" stroke-linecap="round"/>',
};

export function normalizePlatformType(value) {
    const normalized = String(value ?? "UNKNOWN").trim().toUpperCase().replace(/[ -]+/g, "_");
    if (["SHIP", "VESSEL", "MARITIME", "BOAT", "SEA"].includes(normalized)) return "SHIP";
    if (["AIR", "AIRCRAFT", "AERIAL", "PLANE"].includes(normalized)) return "AIRCRAFT";
    if (["LAND", "LAND_VEHICLE", "VEHICLE", "GROUND_VEHICLE"].includes(normalized)) return "LAND_VEHICLE";
    return "UNKNOWN";
}

export function platformTypeLabel(value) {
    return platformLabels[normalizePlatformType(value)];
}

export function platformIconDataUri(value) {
    const type = normalizePlatformType(value);
    const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 36 36" fill="#fff">${platformSvg[type]}</svg>`;
    return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`;
}

export function createTrackIcon(Leaflet, identity, platformType, replay = false) {
    const identityKey = String(identity ?? "UNKNOWN").toUpperCase();
    const colour = IDENTITY_COLOURS[identityKey] || IDENTITY_COLOURS.UNKNOWN;
    const svg = platformSvg[normalizePlatformType(platformType)];
    const markerClass = replay ? "replay-track-marker" : "track-marker";
    const size = replay ? 34 : 30;
    return Leaflet.divIcon({
        className: replay ? "replay-marker-wrapper" : "custom-track-icon",
        html: `<div class="${markerClass}" style="background:${colour};box-shadow:0 0 0 4px ${colour}33,0 2px 8px #0006"><svg viewBox="0 0 42 42" aria-hidden="true">${svg}</svg></div>`,
        iconSize: [size, size],
        iconAnchor: [size / 2, size / 2],
    });
}
