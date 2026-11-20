import test from "node:test";
import assert from "node:assert/strict";
import { normalizePlatformType, platformTypeLabel, platformIconDataUri } from "./trackSymbology.js";

test("normalizes platform types and common aliases", () => {
    assert.equal(normalizePlatformType("ship"), "SHIP");
    assert.equal(normalizePlatformType("air"), "AIRCRAFT");
    assert.equal(normalizePlatformType("land vehicle"), "LAND_VEHICLE");
    assert.equal(normalizePlatformType("vessel"), "SHIP");
});

test("falls back safely for missing and unsupported values", () => {
    assert.equal(normalizePlatformType(null), "UNKNOWN");
    assert.equal(platformTypeLabel("unknown-value"), "Unknown platform");
});

test("each type has its own renderable vector marker", () => {
    const icons = ["SHIP", "AIRCRAFT", "LAND_VEHICLE", "UNKNOWN"].map(platformIconDataUri);
    assert.ok(icons.every(icon => icon.startsWith("data:image/svg+xml;charset=UTF-8,")));
    assert.equal(new Set(icons).size, 4);
});