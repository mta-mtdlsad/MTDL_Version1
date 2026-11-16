package com.mtms.web.rest;

import com.mtms.domain.Alert;
import com.mtms.service.AlertService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/list")
    public List<Alert> listAlerts() {
        return alertService.getAllAlerts();
    }
}
