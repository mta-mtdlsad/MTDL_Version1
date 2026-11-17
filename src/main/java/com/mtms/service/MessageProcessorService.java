package com.mtms.service;

import org.springframework.stereotype.Service;

@Service
public class MessageProcessorService {
    private final TrackService trackService;
    private final MessageParserService messageParserService;
    private final AlertService alertService;
    private final SourceService sourceService;

    public MessageProcessorService(TrackService trackService, MessageParserService messageParserService, AlertService alertService, SourceService sourceService) {
        this.trackService = trackService;
        this.messageParserService = messageParserService;
        this.alertService = alertService;
        this.sourceService = sourceService;
    }

    public void processRawPayload(String payload) {
        if (payload == null || payload.trim().isEmpty()) {
            return;
        }

        MessageParserService.ParseResult result = messageParserService.parse(payload);

        if (result.track == null) {
            alertService.createAlert(
                    result.trackNumber,
                    result.sourceId,
                    result.errorMessage,
                    "MALFORMED_MESSAGE"
            );
            if (result.sourceId != null && result.sourceId.matches("^[A-Za-z0-9_-]{2,30}$")) {
                sourceService.syncSourceMetrics(result.sourceId, 0, "FAILURE");
            }
            return;
        }

        trackService.processTrack(result.track);
    }
}
