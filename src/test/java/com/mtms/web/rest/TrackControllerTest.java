package com.mtms.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtms.domain.Track;
import com.mtms.service.MessageProcessorService;
import com.mtms.service.TrackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrackController.class)
class TrackControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private TrackService trackService;
    @MockBean private MessageProcessorService messageProcessorService;

    @Test
    void listEndpointReturnsTrackPlatformTypeAsJson() throws Exception {
        when(trackService.getAllTracks()).thenReturn(List.of(validTrack()));

        mockMvc.perform(get("/api/tracks/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trackNumber").value("TRK0001"))
                .andExpect(jsonPath("$[0].trackType").value("LAND_VEHICLE"));
    }

    @Test
    void createEndpointDelegatesTrackToProcessorAndReturnsCreated() throws Exception {
        when(messageProcessorService.processDetailed(any(Track.class)))
                .thenReturn(new MessageProcessorService.ProcessingResult(MessageProcessorService.Outcome.ACCEPTED, false));

        mockMvc.perform(post("/api/tracks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTrack())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.outcome").value("ACCEPTED"))
                .andExpect(jsonPath("$.outOfOrder").value(false));

        verify(messageProcessorService).processDetailed(any(Track.class));
    }

    private Track validTrack() {
        return new Track("SRC-A", 1, "TRK0001", "LAND_VEHICLE", 24.4539, 54.3773,
                100, "FRIEND", "ACTIVE", LocalDateTime.of(2026, 11, 23, 9, 0));
    }
}
