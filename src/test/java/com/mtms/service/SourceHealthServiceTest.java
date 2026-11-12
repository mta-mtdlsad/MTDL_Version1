
package com.mtms.service;

import com.mtms.model.Source;
import com.mtms.repository.SourceRepository;
import com.mtms.repository.AlertRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SourceHealthService Class Test")
public class SourceHealthServiceTest {

    // Mock repositories to isolate service testing
    @Mock
    private SourceRepository source_repo;

    @Mock
    private AlertRepository alertRepo;

    // Inject mocked repositories into the service
    @InjectMocks
    private SourceHealthService sourceHealthService;

    // Test data shared across test cases
    private Source source;
    private LocalDateTime receivedAt;

    // Initialize test data before each test
    @BeforeEach
    void setUp(TestInfo testInfo) {
        source = new Source();
        source.setSourceId("LINK-A");
        source.setHighestSequence(100);
        receivedAt = LocalDateTime.now();

        System.out.println("Running Test: " + testInfo.getDisplayName());
    }

    // Display test completion information
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }

    // Test 1: Verify that a source can be found using its ID
    @Test
    @DisplayName("Test Find Source By ID")
    void findSourceByIdTest() {
        when(source_repo.findBySourceId("LINK-A"))
                .thenReturn(Optional.of(source));

        Source result = sourceHealthService.findSourceById("LINK-A");

        assertEquals(source, result, "Test Failed: Incorrect source returned");
    }

    // Test 2: Verify that all sources can be retrieved
    @Test
    @DisplayName("Test Get All Sources")
    void getAllSourcesTest() {
        when(source_repo.findAll()).thenReturn(List.of(source));

        List<Source> result = sourceHealthService.getAllSources();

        assertEquals(1, result.size(), "Test Failed: Incorrect number of sources");
    }

    // Test 3: Verify that source IDs and their sequence numbers are returned
    @Test
    @DisplayName("Test Get Sources And Sequences")
    void getSourcesAndSequencesTest() {
        when(source_repo.findAll()).thenReturn(List.of(source));

        Map<String, Long> result = sourceHealthService.getSourcesAndSequences();

        assertEquals(100L, result.get("LINK-A"), "Test Failed: Incorrect sequence number");
    }

    // Test 4: Verify that a new source can be registered successfully
    @Test
    @DisplayName("Test Register New Source")
    void registerSourceTest() throws Exception {
        when(source_repo.findBySourceId("LINK-A")).thenReturn(Optional.empty());
        when(source_repo.save(any(Source.class))).thenReturn(source);

        Boolean result = sourceHealthService.registerSource("LINK-A");

        assertTrue(result, "Test Failed: Source was not registered");
        verify(source_repo).save(any(Source.class));
    }

    // Test 5: Verify that recording a received message does not access repositories
    @Test
    @DisplayName("Test Record Received Message")
    void recordReceivedTest() {
        sourceHealthService.recordReceived("LINK-A", receivedAt);

        verifyNoInteractions(source_repo, alertRepo);
    }

    // Test 6: Verify that an active source does not require a status update
    @Test
    @DisplayName("Test Active Source Health")
    void checkHealthActiveTest() {
        sourceHealthService.recordReceived("LINK-A", receivedAt);
        sourceHealthService.checkHealth();

        verify(source_repo, never()).updateStatus(anyString(), anyString());
    }

    // Test 7: Verify that a stale source is updated and a health alert is created
    @Test
    @DisplayName("Test Stale Source Health")
    void checkHealthStaleTest() {
        sourceHealthService.recordReceived("LINK-A", LocalDateTime.now().minusSeconds(30));
        when(source_repo.updateStatus("LINK-A", "STALE")).thenReturn(1);

        sourceHealthService.checkHealth();

        verify(source_repo).updateStatus("LINK-A", "STALE");
        verify(alertRepo).save(any());
    }

    // Test 8: Verify that a disconnected source is updated and an alert is created
    @Test
    @DisplayName("Test Disconnected Source Health")
    void checkHealthDisconnectedTest() {
        sourceHealthService.recordReceived("LINK-A", LocalDateTime.now().minusSeconds(90));
        when(source_repo.updateStatus("LINK-A", "DISCONNECTED")).thenReturn(1);

        sourceHealthService.checkHealth();

        verify(source_repo).updateStatus("LINK-A", "DISCONNECTED");
        verify(alertRepo).save(any());
    }

    // Test 9: Verify that an accepted outcome updates the correct counters
    @Test
    @DisplayName("Test Record Accepted Outcome")
    void recordAcceptedOutcomeTest() {
        sourceHealthService.recordOutcome("LINK-A", receivedAt, "ACCEPTED", 100);

        verify(source_repo).recordOutcome("LINK-A", receivedAt, 100, 1, 0, 0, 0);
    }

    // Test 10: Verify that a rejected outcome updates the correct counters
    @Test
    @DisplayName("Test Record Rejected Outcome")
    void recordRejectedOutcomeTest() {
        sourceHealthService.recordOutcome("LINK-A", receivedAt, "REJECTED", 100);

        verify(source_repo).recordOutcome("LINK-A", receivedAt, 0, 0, 1, 0, 1);
    }

    // Test 11: Verify that a duplicate outcome updates the correct counters
    @Test
    @DisplayName("Test Record Duplicate Outcome")
    void recordDuplicateOutcomeTest() {
        sourceHealthService.recordOutcome("LINK-A", receivedAt, "DUPLICATE", 100);

        verify(source_repo).recordOutcome("LINK-A", receivedAt, 0, 0, 0, 1, 1);
    }

    // Test 12: Verify that an invalid outcome throws an exception without repository interaction
    @Test
    @DisplayName("Test Invalid Outcome")
    void invalidOutcomeTest() {
        assertThrows(IllegalArgumentException.class,
                () -> sourceHealthService.recordOutcome("LINK-A", receivedAt, "UNKNOWN", 100),
                "Test Failed: Expected IllegalArgumentException");

        verifyNoInteractions(source_repo);
    }
}