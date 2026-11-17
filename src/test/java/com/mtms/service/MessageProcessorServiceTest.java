package com.mtms.service;

import com.mtms.domain.Track;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageProcessorService Class Test")
public class MessageProcessorServiceTest {

    @Mock
    private TrackService trackService;

    @Mock
    private MessageParserService messageParserService;

    @Mock
    private AlertService alertService;

    @Mock
    private SourceService sourceService;

    @InjectMocks
    private MessageProcessorService processorService;

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

    // Ignore empty or whitespace-only payloads
    @Test
    @DisplayName("Test Process Empty Payload")
    void processEmptyPayloadTest() {
        // Call processor with whitespace string
        processorService.processRawPayload("   ");

        // Verify no downstream services were invoked
        verifyNoInteractions(messageParserService, trackService, alertService);
    }

    // Forward successfully parsed tracks to TrackService
    @Test
    @DisplayName("Test Process Valid Payload")
    void processValidPayloadTest() {
        // Simulate valid parse result
        Track track = new Track();
        MessageParserService.ParseResult result =
                new MessageParserService.ParseResult(track, null, "TRK1001", "SRC01");
        when(messageParserService.parse("valid")).thenReturn(result);

        // Call processor
        processorService.processRawPayload("valid");

        // Verify track processing was invoked and no alert was created
        verify(trackService).processTrack(track);
        verifyNoInteractions(alertService);
    }

    // Log alert and record source failure when parsing fails
    @Test
    @DisplayName("Test Process Malformed Payload")
    void processMalformedPayloadTest() {
        // Simulate failed parse result with valid sourceId
        MessageParserService.ParseResult result =
                new MessageParserService.ParseResult(null, "Malformed error", "TRK1001", "SRC01");
        when(messageParserService.parse("bad")).thenReturn(result);

        // Call processor
        processorService.processRawPayload("bad");

        // Verify alert creation and FAILURE metric sync
        verify(alertService).createAlert("TRK1001", "SRC01", "Malformed error", "MALFORMED_MESSAGE");
        verify(sourceService).syncSourceMetrics("SRC01", 0, "FAILURE");
    }
}
