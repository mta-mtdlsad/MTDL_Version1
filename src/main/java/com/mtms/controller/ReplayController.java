package com.mtms.controller;

import com.mtms.model.Track;
import com.mtms.service.ReplayService;
import com.mtms.util.ReplaySpeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/api/tracks")
public class ReplayController {

    @Autowired
    private ReplayService replayService;

    // Open the existing HTML form
    @GetMapping("/replay")
    public ModelAndView showReplayForm() {
        return new ModelAndView("forward:/replay-form.html");
    }

    // Start replaying tracks
    @GetMapping(value = "/replay/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter replayTracks(
            @RequestParam ReplaySpeed speed,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startTime,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        SseEmitter emitter = new SseEmitter(300000L);
        Thread replayThread = new Thread(() -> {
            try {
                List<Track> tracks = replayService.replayTracks(speed, startTime, endTime,
                        track -> {
                            try {
                                emitter.send(
                                        SseEmitter.event().name("track").data(track));
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
                emitter.send(SseEmitter.event().name("done").data(tracks.size()));
                emitter.complete();
            } catch (UncheckedIOException e) {
                System.err.println("Replay stream disconnected: " + e.getMessage());
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }, "MTMS-Replay");
        emitter.onTimeout(replayThread::interrupt);
        replayThread.start();
        return emitter;
    }
}