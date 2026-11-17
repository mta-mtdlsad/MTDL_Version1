package com.mtms.service;

import com.mtms.domain.Path;
import com.mtms.repository.PathRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PathService Class Test")
public class PathServiceTest {

    @Mock
    private PathRepository pathRepository;

    @InjectMocks
    private PathService pathService;

    private Path path;

    // Runs before every test
    @BeforeEach
    void setUp(TestInfo testInfo) {
        // Create a mock Path with ID 10
        path = mock(Path.class);
        when(path.getPathId()).thenReturn(10L);
        System.out.println("Running Test: " + testInfo.getDisplayName());
    }

    // Runs after each test
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }

    // Retrieve a path by ID from cached paths
    @Test
    @DisplayName("Test Get Path By ID")
    void getPathByIdTest() {
        // Simulate repository returning the mock path
        when(pathRepository.findAll()).thenReturn(List.of(path));

        // Call service method
        Optional<Path> result = pathService.getPathById(10L);

        // Check that path ID 10 was found
        assertTrue(result.isPresent(), "Test Failed: Path should be found");
        assertEquals(10L, result.get().getPathId());
    }

    // Find matching path ID when coordinates lie on path
    @Test
    @DisplayName("Test Find Matching Path For Coordinates")
    void findMatchingPathTest() {
        // Simulate coordinate check returning true
        when(path.isTrackOnPath(25.0, 55.0, 1.5)).thenReturn(true);
        when(pathRepository.findAll()).thenReturn(List.of(path));

        // Call service method
        Long matchedId = pathService.findMatchingPathIdForCoordinates(25.0, 55.0, 1.5);

        // Check that path ID 10 was matched
        assertEquals(10L, matchedId, "Test Failed: Expected matching path ID 10");
    }
}
