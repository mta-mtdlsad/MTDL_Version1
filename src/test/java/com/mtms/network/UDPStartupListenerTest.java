package com.mtms.network;

import com.mtms.domain.GeoPath;
import com.mtms.simulator.RandomPathGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UDPStartupListenerTest {
    @Test
    void requestsLandSeaAndAirPathsFromTheInjectedGeographicGenerator() {
        RandomPathGenerator pathGenerator = mock(RandomPathGenerator.class);
        when(pathGenerator.generateRandomPaths(anyDouble(), anyDouble(), anyDouble(), anyInt(), anyInt(), anyDouble(), anyString()))
                .thenAnswer(invocation -> List.of(new GeoPath(1, List.of(), invocation.getArgument(6))));
        UDPStartupListener listener = new UDPStartupListener();
        org.springframework.test.util.ReflectionTestUtils.setField(listener, "pathGenerator", pathGenerator);

        listener.generatePaths();

        assertEquals(3, listener.getPaths().size());
        verify(pathGenerator).generateRandomPaths(23.5, 54.5, 150, 30, 12, 12, "LAND");
        verify(pathGenerator).generateRandomPaths(25.5, 54.0, 150, 30, 12, 12, "SEA");
        verify(pathGenerator).generateRandomPaths(24.0, 54.0, 250, 40, 12, 12, "AIR");
    }
}
