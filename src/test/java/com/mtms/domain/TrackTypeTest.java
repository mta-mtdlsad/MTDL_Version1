package com.mtms.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrackTypeTest {
    @Test
    void normalizesTypesAndAliases() {
        assertEquals(TrackType.SHIP, TrackType.fromValue(" ship "));
        assertEquals(TrackType.SHIP, TrackType.fromValue("vessel"));
        assertEquals(TrackType.AIRCRAFT, TrackType.fromValue("air"));
        assertEquals(TrackType.LAND_VEHICLE, TrackType.fromValue("land vehicle"));
    }

    @Test
    void missingOrUnsupportedValuesBecomeUnknown() {
        assertEquals(TrackType.UNKNOWN, TrackType.fromValue(null));
        assertEquals(TrackType.UNKNOWN, TrackType.fromValue("submarine-ish"));
        assertEquals(TrackType.UNKNOWN, new Track().getPlatformType());
    }
}
