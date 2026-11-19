package com.mtms.service;

import org.junit.jupiter.api.*;
import com.mtms.domain.TrackType;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MessageParserService Class Test")
public class MessageParserServiceTest {

    private MessageParserService parserService;

    // Runs before every test
    @BeforeEach
    void setUp(TestInfo testInfo) {
        // Initialize parser service instance
        parserService = new MessageParserService();
        System.out.println("Running Test: " + testInfo.getDisplayName());
    }

    // Runs after each test
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }

    // Parse a valid 9-field pipe-delimited payload
    @Test
    @DisplayName("Test Parse Valid Payload")
    void parseValidPayloadTest() {
        String payload = "SRC1|1|TRK1001|25.20|55.27|450.5|FRIEND|ACTIVE|2026-11-16 09:00:00";

        // Call parse method
        MessageParserService.ParseResult result = parserService.parse(payload);

        // Check that track is populated and errorMessage is null
        assertNotNull(result.track, "Test Failed: Track should not be null");
        assertNull(result.errorMessage, "Test Failed: Error message should be null");
        assertEquals("TRK1001", result.track.getTrackNumber());
    }

    // Return error when payload does not have 9 fields
    @Test
    @DisplayName("Test Parse Wrong Field Count")
    void parseInvalidFieldCountTest() {
        String payload = "SRC1|1|TRK1001";

        // Call parse method
        MessageParserService.ParseResult result = parserService.parse(payload);

        // Check that track is null and field count error is returned
        assertNull(result.track, "Test Failed: Track should be null");
        assertTrue(result.errorMessage.contains("Expected 9 fields"));
    }

    // Return error when numeric field contains non-digits
    @Test
    @DisplayName("Test Parse Invalid Number Format")
    void parseInvalidNumberTest() {
        String payload = "SRC1|ABC|TRK1001|25.20|55.27|450.5|FRIEND|ACTIVE|2026-11-16 09:00:00";

        // Call parse method
        MessageParserService.ParseResult result = parserService.parse(payload);

        // Check that track is null and number format error is returned
        assertNull(result.track, "Test Failed: Track should be null");
        assertTrue(result.errorMessage.contains("Numeric fields contain invalid formats"));
    }

    // Return error when timestamp does not match yyyy-MM-dd HH:mm:ss
    @Test
    @DisplayName("Test Parse Invalid Timestamp Format")
    void parseInvalidTimestampTest() {
        String payload = "SRC1|1|TRK1001|25.20|55.27|450.5|FRIEND|ACTIVE|invalid-date";

        // Call parse method
        MessageParserService.ParseResult result = parserService.parse(payload);

        // Check that track is null and timestamp error is returned
        assertNull(result.track, "Test Failed: Track should be null");
        assertTrue(result.errorMessage.contains("Timestamp parse exception"));
    }

    @Test
    @DisplayName("Legacy 9-field payload defaults platform type to UNKNOWN")
    void parseLegacyPayloadUsesUnknownPlatform() {
        MessageParserService.ParseResult result = parserService.parse(
                "SRC1|1|TRK1001|25.20|55.27|450.5|FRIEND|ACTIVE|2026-11-16 09:00:00");
        assertNull(result.errorMessage);
        assertEquals(TrackType.UNKNOWN, result.track.getPlatformType());
    }

    @Test
    @DisplayName("10-field payload parses the platform type after the timestamp")
    void parsePlatformTypePayload() {
        MessageParserService.ParseResult result = parserService.parse(
                "SRC1|1|TRK1001|25.20|55.27|450.5|FRIEND|ACTIVE|2026-11-16 09:00:00|ship");
        assertNull(result.errorMessage);
        assertEquals(TrackType.SHIP, result.track.getPlatformType());
    }

    @Test
    @DisplayName("Unsupported platform value safely becomes UNKNOWN")
    void parseUnsupportedPlatformUsesUnknown() {
        MessageParserService.ParseResult result = parserService.parse(
                "SRC1|1|TRK1001|25.20|55.27|450.5|FRIEND|ACTIVE|2026-11-16 09:00:00|not-a-type");
        assertNull(result.errorMessage);
        assertEquals(TrackType.UNKNOWN, result.track.getPlatformType());
    }
}
