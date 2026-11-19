package com.mtms.simulator;

import com.mtms.domain.Track;
import com.mtms.domain.TrackType;
import com.mtms.service.TrackService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrackSimulatorTest {
    @Test
    void createsStableTypedHistoriesThatMoveWithinTheirConfiguredCorridors() {
        TrackService trackService = new TrackService(null, null, null, null, null) {
            @Override public Map<String, Integer> getLatestSequenceMap() { return Map.of(); }
        };
        List<Track> feed = new TrackSimulator(trackService, null).generateTracks();
        var startTime = feed.get(0).getCreatedTime();
        assertFalse(feed.isEmpty());

        for (int index = 0; index < 10; index++) {
            final String number = "TRK" + (10000 + index);
            List<Track> history = feed.stream().filter(track -> number.equals(track.getTrackNumber())
                    && !track.getCreatedTime().isBefore(startTime)).toList();
            assertEquals(50, history.size());
            TrackType expected = switch (index % 3) {
                case 0 -> TrackType.SHIP;
                case 1 -> TrackType.AIRCRAFT;
                default -> TrackType.LAND_VEHICLE;
            };
            assertTrue(history.stream().allMatch(track -> track.getPlatformType() == expected));
            assertNotEquals(history.get(0).getLatitude(), history.get(history.size() - 1).getLatitude());
            assertNotEquals(history.get(0).getLongitude(), history.get(history.size() - 1).getLongitude());
        }
    }
}
