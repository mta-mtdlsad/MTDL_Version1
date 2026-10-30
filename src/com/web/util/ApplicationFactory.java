package com.web.util;

import com.repository.TrackRepository;
import com.repository.AlertRepository;

import com.service.ValidationService;
import com.service.TrackService;
import com.service.AlertService;
import com.service.ReplayService;
import com.service.TrackAnalyticsService;


// ApplicationFactory is responsible for creating and providing shared instances of repositories and services.
// Other classes can access these objects through getter methods without creating new instances every time.

public class ApplicationFactory {

    private static final TrackRepository trackRepository = new TrackRepository();

    private static final AlertRepository alertRepository = new AlertRepository();

    private static final ValidationService validationService = new ValidationService();

    //private static final TrackService trackService = new TrackService(trackRepository, alertRepository, validationService);

    //private static final AlertService alertService = new AlertService(alertRepository);

    //private static final ReplayService replayService = new ReplayService(trackRepository);

    //private static final TrackAnalyticsService analyticsService = new TrackAnalyticsService(trackRepository);

    public static TrackRepository getTrackRepository() {
        return trackRepository;
    }

    public static AlertRepository getAlertRepository() {
        return alertRepository;
    }

    public static ValidationService getValidationService() {
        return validationService;
    }

//    public static TrackService getTrackService() {
//        return trackService;
//    }

//    public static AlertService getAlertService() {
//        return alertService;
//    }

//    public static ReplayService getReplayService() {
//        return replayService;
//    }
//
//    public static TrackAnalyticsService getAnalyticsService() {
//        return analyticsService;
//    }
}
