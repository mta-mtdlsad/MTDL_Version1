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
    SHIP: '<path d="M4 22h28l-4 8a3 3 0 0 1-2.7 1.7H10.7A3 3 0 0 1 8 30z"/><path d="M11 20V13h8v7m2 0v-5h6v5M14 10h5" fill="none" stroke="#fff" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/>',
    AIRCRAFT: '<path d="M18 2l3 12 11 6v3l-11-2v8l5 3v2l-8-2-8 2v-2l5-3v-8L4 23v-3l11-6z"/>',
    LAND_VEHICLE: '<path d="M5 13h16l5 5h5a2 2 0 0 1 2 2v8H4v-9a6 6 0 0 1 1-6zm3 3-2 5h13l-4-5z"/><circle cx="10" cy="29" r="3"/><circle cx="26" cy="29" r="3"/>',
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
        html: `<div class="${markerClass}" style="background:${colour};box-shadow:0 0 0 4px ${colour}33,0 2px 8px #0006"><svg viewBox="0 0 36 36" aria-hidden="true">${svg}</svg></div>`,
        iconSize: [size, size],
        iconAnchor: [size / 2, size / 2],
    });
}