package com.mtms.controller;

import com.mtms.model.Alert;
import com.mtms.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    @Autowired
    private AlertService alertService;

    // CREATE: POST /api/alerts
    @PostMapping
    public Alert createAlert(@RequestBody Alert alert) {
        return alertService.saveAlert(alert);
    }

    // READ: GET /api/alerts
    @GetMapping
    public List<Alert> getAllAlerts() {
        return alertService.getAlerts();
    }

    // READ: GET /api/alerts/1
    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable Integer id) {
        return alertService.findById(id);
    }

    // DELETE: DELETE /api/alerts/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(
            @PathVariable Integer id) {

        if (!alertService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        alertService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}