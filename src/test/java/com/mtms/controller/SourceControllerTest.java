package com.mtms.controller;
import com.mtms.model.Source;
import com.mtms.service.SourceHealthService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
@ExtendWith(MockitoExtension.class)
@DisplayName("SourceController Class Test")
public class SourceControllerTest {
    // Mock the service so we do not need PostgreSQL.
    @Mock
    private SourceHealthService sources;
    // Inject the mocked service into the real controller.
    @InjectMocks
    private SourceController sourceController;
    // Used to simulate HTTP requests.
    private MockMvc mockMvc;

    // Runs before every test.
    @BeforeEach
    public void init(TestInfo testInfo, TestReporter testReporter) {
        // Set up the controller without starting the server.
        mockMvc = MockMvcBuilders.standaloneSetup(sourceController).build();

        // Display the name of the test being executed.
        testReporter.publishEntry("\nRunning " + testInfo.getDisplayName());
    }

    // Runs after every test.
    @AfterEach
    void afterAllTestCases(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }

    // TEST 1: Register a source successfully.
    @Test
    @DisplayName("Test Register Source Successfully")
    public void registerSourceTest() throws Exception {
        // Simulate successful registration.
        when(sources.registerSource("S1")).thenReturn(true);
        // Send POST request and check HTTP 200 and true.
        mockMvc.perform(post("/api/sources/create")
                        .param("source_id", "S1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // Check that the service was called.
        verify(sources).registerSource("S1");
    }

    // TEST 2: Negative test of method 1 :: Registration is unsuccessful.
    @Test
    @DisplayName("Test Register Source Failure")
    public void registerSourceFailureTest() throws Exception {
        // Simulate unsuccessful registration.
        when(sources.registerSource("S1")).thenReturn(false);
        // Current controller returns HTTP 200 with false.
        mockMvc.perform(post("/api/sources/create")
                        .param("source_id", "S1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
        verify(sources).registerSource("S1");
    }

    // TEST 3: Register without a source ID.
    @Test
    @DisplayName("Test Register Source Missing ID")
    public void registerSourceMissingIdTest() throws Exception {
        // Send POST without the required parameter.
        mockMvc.perform(post("/api/sources/create"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        // Check that the service was not called.
        verifyNoInteractions(sources);
    }

    // TEST 4: Retrieve all sources.
    @Test
    @DisplayName("Test Get All Sources")
    public void getAllSourcesTest() throws Exception {
        // Create sample sources.
        Source source1 = new Source();
        source1.setSourceId("S1");
        source1.setSourceStatus("ACTIVE");
        Source source2 = new Source();
        source2.setSourceId("S2");
        source2.setSourceStatus("STALE");
        // Simulate the service returning two sources.
        when(sources.getAllSources())
                .thenReturn(List.of(source1, source2));
        // Check HTTP 200 and the JSON response.
        mockMvc.perform(get("/api/sources/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sourceId").value("S1"))
                .andExpect(jsonPath("$[0].sourceStatus").value("ACTIVE"))
                .andExpect(jsonPath("$[1].sourceId").value("S2"))
                .andExpect(jsonPath("$[1].sourceStatus").value("STALE"));

        verify(sources).getAllSources();
    }

    // TEST 5: Retrieve sources when none exist.
    @Test
    @DisplayName("Test Get All Sources When Empty")
    public void getAllSourcesEmptyTest() throws Exception {
        // Simulate an empty source list.
        when(sources.getAllSources()).thenReturn(List.of());
        // Check that an empty JSON array is returned.
        mockMvc.perform(get("/api/sources/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(sources).getAllSources();
    }

    // TEST 6: Search for an existing source.
    @Test
    @DisplayName("Test Search Source By ID")
    public void searchSourceByIdTest() throws Exception {
        // Create a source.
        Source source = new Source();
        source.setSourceId("S1");
        source.setSourceStatus("ACTIVE");
        source.setHighestSequence(100);
        // Simulate finding the source.
        when(sources.findSourceById("S1"))
                .thenReturn(source);
        // Check the returned source details.
        mockMvc.perform(get("/api/sources/search/S1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sourceId").value("S1"))
                .andExpect(jsonPath("$.sourceStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.highestSequence").value(100));

        verify(sources).findSourceById("S1");
    }

    // TEST 7: Retrieve sequence numbers.
    @Test
    @DisplayName("Test Get Sources And Sequences")
    public void getSourcesAndSequencesTest() throws Exception {

        // Simulate three sources with sequence numbers.
        Map<String, Long> sequences = Map.of(
                "S1", 100L,
                "S2", 200L,
                "S3", 300L
        );

        when(sources.getSourcesAndSequences())
                .thenReturn(sequences);

        // Check the returned JSON values.
        mockMvc.perform(get("/api/sources/search/sequences"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.S1").value(100))
                .andExpect(jsonPath("$.S2").value(200))
                .andExpect(jsonPath("$.S3").value(300));

        verify(sources).getSourcesAndSequences();
    }

    // TEST 8: Retrieve sequences when none exist.
    @Test
    @DisplayName("Test Get Sources And Sequences When Empty")
    public void getSourcesAndSequencesEmptyTest() throws Exception {
        // Simulate no sequence information.
        when(sources.getSourcesAndSequences()).thenReturn(Map.of());
        // Check that an empty JSON object is returned.
        mockMvc.perform(get("/api/sources/search/sequences"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$").isEmpty());
        verify(sources).getSourcesAndSequences();
    }
}