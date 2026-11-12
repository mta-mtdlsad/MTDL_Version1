
package com.mtms.service;

import com.mtms.model.Track;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Test class for the actual MessageParserService.
// We test the parser directly without starting Spring Boot or PostgreSQL.
@DisplayName("MTDL Message Parser Service Tests")
class MessageParserServiceTest {
    // The real service being tested.
    private MessageParserService parser;
    // A valid UDP message containing exactly 9 fields.
    private String validMessage;
    // Runs ONCE before all tests in this class.
    @BeforeAll
    static void beforeAllTests() {
        System.out.println("Starting MTDL Message Parser Tests");
    }

    // Runs BEFORE every test (create Service Object and give msg).
    // Each test receives a fresh parser and valid message.
    @BeforeEach
    void setUp() {
        parser = new MessageParserService();
        validMessage = "LINK-A|101|TRACK101|24.5|54.3|450|" + "FRIEND|ACTIVE|2026-11-12T09:00:00";
    }

    // Runs AFTER every test.
    @AfterEach
    void afterEachTestCase() {
        System.out.println("Test completed");
    }

    // Runs ONCE after all tests finish.
    @AfterAll
    static void afterAllTests() {System.out.println("Finished MTDL Message Parser Tests");}


    // POSITIVE TESTS VALID MESSAGES
    @Nested
    @DisplayName("Positive Tests :: Valid Messages")
    class PositiveTests {
        @Test
        @DisplayName("Valid UDP message should create a Track")
        void validMessageShouldCreateTrack() {
            // ACT: Convert the UDP message into a Track.
            Track track = parser.parse(validMessage);
            // ASSERT: A Track object must be returned.
            assertNotNull(track);
        }

        @Test
        @DisplayName("Parsed Track should contain correct values")
        void parsedTrackShouldContainCorrectValues() {
            Track track = parser.parse(validMessage);
            // Check all parsed fields together.
            assertAll("Track field verification",
                    () -> assertEquals("LINK-A", track.getSourceId()),
                    () -> assertEquals(101, track.getSequenceNumber()),
                    () -> assertEquals("TRACK101", track.getTrackNumber()),
                    () -> assertEquals(24.5, track.getLatitude()),
                    () -> assertEquals(54.3, track.getLongitude()),
                    () -> assertEquals(450.0, track.getSpeed()),
                    () -> assertEquals("FRIEND", track.getIdentityType()),
                    () -> assertEquals("ACTIVE", track.getTrackStatus()),
                    () -> assertEquals(LocalDateTime.of(2026, 11, 12, 9, 0), track.getCreatedTime())
            );
        }

        @Test
        @DisplayName("Whitespace around fields should be trimmed!")
        void whitespaceShouldBeTrimmed() {
            // The parser should remove surrounding spaces.
            String message = " LINK-A | 101 | TRACK101 | 24.5 | 54.3 | " + "450 | FRIEND | ACTIVE | 2026-11-12T09:00:00 ";
            Track track = parser.parse(message);
            assertAll(
                    () -> assertEquals("LINK-A", track.getSourceId()),
                    () -> assertEquals("TRACK101", track.getTrackNumber()),
                    () -> assertEquals("FRIEND", track.getIdentityType()),
                    () -> assertEquals("ACTIVE", track.getTrackStatus())
            );
        }


        @Test
        @DisplayName("Valid timestamp should be converted to LocalDateTime")
        void validTimestampShouldParse() {
            Track track = parser.parse(validMessage);
            LocalDateTime expected = LocalDateTime.of(2026, 11, 12, 9, 0);
            assertEquals(expected, track.getCreatedTime());
        }
    }

    // NEGATIVE TESTS :: INVALID MESSAGES
    @Nested
    @DisplayName("Negative Tests :: Invalid Messages")
    class NegativeTests {
        @Test
        @DisplayName("Null message should be rejected")
        void nullMessageShouldBeRejected() {
            // assertThrows passes if the expected exception occurs.
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(null));
            assertTrue(exception.getMessage().contains("Message is empty")
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("Empty or blank message should be rejected")
        void blankMessageShouldBeRejected(String message) {
            assertThrows(IllegalArgumentException.class, () -> parser.parse(message)
            );
        }

        @Test
        @DisplayName("Message with fewer than 9 fields should fail")
        void tooFewFieldsShouldFail() {
            String message = "LINK|101|TRACK101|24.5|54.3";
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(message));
            assertTrue(exception.getMessage().contains("9 fields required")
            );
        }

        @Test
        @DisplayName("Message with more than 9 fields should fail")
        void tooManyFieldsShouldFail() {
            String message = validMessage + "|EXTRA";
            assertThrows(IllegalArgumentException.class, () -> parser.parse(message)
            );
        }

        @Test
        @DisplayName("Missing Source ID should be rejected")
        void missingSourceIdShouldFail() {
            String message = "|101|TRACK101|24.5|54.3|450|" + "FRIEND|ACTIVE|2026-11-12T09:00:00";
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(message));
            assertTrue(exception.getMessage().contains("Field 1 is missing")
            );
        }

        @Test
        @DisplayName("Missing Track Number should be rejected")
        void missingTrackNumberShouldFail() {
            String message = "LINK-A|101||24.5|54.3|450|" + "FRIEND|ACTIVE|2026-11-12T09:00:00";
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> parser.parse(message)
                    );
            assertTrue(exception.getMessage().contains("Field 3 is missing")
            );
        }

        //Invalid numeric formats
        @ParameterizedTest
        @CsvSource({"ABC, 24.5, 54.3, 450", "101, INVALID, 54.3, 450", "101, 24.5, INVALID, 450", "101, 24.5, 54.3, FAST"
        })
        @DisplayName("Invalid numeric formats should be rejected")
        void invalidNumericFormatsShouldFail(String sequence, String latitude, String longitude, String speed
        ) {
            String message = "LINK-A|" + sequence + "|TRACK101|" + latitude + "|" + longitude + "|" + speed + "|FRIEND|ACTIVE|2026-11-12T09:00:00";
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(message)
            );
            assertTrue(exception.getMessage().contains("Invalid number format")
            );
        }

        @Test
        @DisplayName("Invalid timestamp should be rejected")
        void invalidTimestampShouldFail() {
            String message = "SOURCE1|101|TRACK101|24.5|54.3|450|" + "FRIEND|ACTIVE|WRONG_DATE";

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(message));

            assertTrue(exception.getMessage().contains("Invalid observation time")
            );
        }

        @Test
        @DisplayName("Missing timestamp should be rejected")
        void missingTimestampShouldFail() {
            String message = "SOURCE1|101|TRACK101|24.5|54.3|450|" + "FRIEND|ACTIVE|";

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(message));

            assertTrue(exception.getMessage().contains("Field 9 is missing")
            );
        }
    }
}