package com.mtms.service;

import com.mtms.domain.Source;
import com.mtms.repository.SourceRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SourceService Class Test")
public class SourceServiceTest {

    @Mock
    private SourceRepository sourceRepository;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private SourceService sourceService;

    // Runs before every test
    @BeforeEach
    void setUp(TestInfo testInfo) {
        System.out.println("Running Test: " + testInfo.getDisplayName());
    }

    // Runs after each test
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }

    // Sync source metrics when packet status is ACCEPTED
    @Test
    @DisplayName("Test Sync Source Metrics Accepted")
    void syncSourceMetricsTest() {
        // Call sync method
        sourceService.syncSourceMetrics("SRC_01", 5, "ACCEPTED");

        // Verify repository received accepted flag = 1
        verify(sourceRepository).syncSourceMetrics("SRC_01", 5, 1, 0, 0, 0);
    }

    // Transition ACTIVE source to STALE after 15+ seconds of inactivity
    @Test
    @DisplayName("Test Evaluate Source Health Stale")
    void evaluateSourceHealthStaleTest() {
        // Create an active source quiet for 25 seconds
        Source source = new Source();
        source.setSourceId("SRC_01");
        source.setSourceStatus("ACTIVE");
        source.setLastReceivedTime(LocalDateTime.now().minusSeconds(25));

        // Simulate repository returning the source
        when(sourceRepository.findByStatusActiveOrStale()).thenReturn(List.of(source));

        // Call health evaluation
        sourceService.evaluateSourceHealth();

        // Verify status updated to STALE and alert created
        verify(sourceRepository).updateSourceStatus("SRC_01", "STALE");
        verify(alertService).createAlert(eq(null), eq("SRC_01"), anyString(), eq("Source becomes stale"));
    }

    // Transition source to DISCONNECTED after 60+ seconds of inactivity
    @Test
    @DisplayName("Test Evaluate Source Health Disconnected")
    void evaluateSourceHealthDisconnectedTest() {
        // Create a stale source quiet for 75 seconds
        Source source = new Source();
        source.setSourceId("SRC_01");
        source.setSourceStatus("STALE");
        source.setLastReceivedTime(LocalDateTime.now().minusSeconds(75));

        // Simulate repository returning the source
        when(sourceRepository.findByStatusActiveOrStale()).thenReturn(List.of(source));

        // Call health evaluation
        sourceService.evaluateSourceHealth();

        // Verify status updated to DISCONNECTED and alert created
        verify(sourceRepository).updateSourceStatus("SRC_01", "DISCONNECTED");
        verify(alertService).createAlert(eq(null), eq("SRC_01"), anyString(), eq("Source becomes disconnected"));
    }
}
