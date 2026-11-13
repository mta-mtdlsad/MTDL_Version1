package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.main.MTMSApplication;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = MTMSApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ValidationServiceTest {

    @Autowired
    private ValidationService validationService;

    @Test
    @Tag("Validation")
    public void parseTrackStringTest(){
        String trackStringInvalid = "|450|FRIEND|ACTIVE|2026-11-02T09:10:00";
        String trackStringValid = "LINK-A|1|TRK1001|12.971600|77.594600|450|FRIEND|ACTIVE|2026-11-02T09:10:00";
        assertAll(
                () -> assertThat(validationService.parseTrackString(trackStringInvalid)).hasSize(5),
                () -> assertThat(validationService.parseTrackString(trackStringValid)).isEmpty()

        );
    }

    @Test
    @Tag("Validation")
    public void validateTrackTest(){
        Track trackValid = new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, LocalDateTime.now(), LocalDateTime.now());
        Track trackInvalid = new Track("TRK1004", 100, 50, -2, "HOSTILE",
                "", "LINK-D", 1, LocalDateTime.now(), LocalDateTime.now());
        assertAll(
                () -> assertThat(validationService.validateTrack(trackInvalid)).hasSize(3),
                () -> assertThat(validationService.validateTrack(trackValid)).isEmpty()

        );
    }

}
