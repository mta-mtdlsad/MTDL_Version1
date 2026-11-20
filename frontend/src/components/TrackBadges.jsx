import React from "react";
import { normalizePlatformType, platformIconDataUri, platformTypeLabel } from "../utils/trackSymbology.js";

// Reuses the platform SVG artwork already used by the map.
// Identity cells and identity colours are deliberately unchanged.
export function PlatformBadge({ value }) {
    const type = normalizePlatformType(value);
    return (
        <span className="table-platform-badge" title={platformTypeLabel(type)}>
            <span className="table-platform-symbol" aria-hidden="true">
                <img src={platformIconDataUri(type)} alt="" />
            </span>
            <span>{platformTypeLabel(type)}</span>
        </span>
    );
}