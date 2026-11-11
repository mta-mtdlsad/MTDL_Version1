package com.mtms.service;

import com.mtms.model.Alert;
import com.mtms.model.Track;
import com.mtms.model.ValidationResult;
import com.mtms.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {
    @Autowired
    private AlertRepository alert_repo;

    // CREATE: Alert associated with a Track
    public void createAlert(Track track, ValidationResult result) {
        String trackNumber = null;
        // Malformed UDP messages may not have a Track
        if (track != null) {
            trackNumber = track.getTrackNumber();
        }
        Alert alert = new Alert(
                trackNumber,
                result.getMessage(),
                result.getSeverity(),
                LocalDateTime.now()
        );
        // JPA replaces the old saveAlert(alert)
        alert_repo.save(alert);
    }

    // CREATE: Alert using a Track Number
    public void createAlert(String trackNo, ValidationResult result) {
        Alert alert = new Alert(
                trackNo,
                result.getMessage(),
                result.getSeverity(),
                LocalDateTime.now()
        );
        alert_repo.save(alert);
    }

    // CREATE: Alert without a Track Number
    public void createAlert(ValidationResult result) {
        Alert alert = new Alert(
                null,
                result.getMessage(),
                result.getSeverity(),
                LocalDateTime.now()
        );
        alert_repo.save(alert);
    }

    //
    public Alert saveAlert(Alert alert) {
        return alert_repo.save(alert);
    }

    // READ: Retrieve all alerts chronologically
    public List<Alert> getAlerts() {
        // instead of previous getAllAlerts
        List<Alert> alerts = alert_repo.findAllByOrderByAlertTimeAsc();

        if (alerts.isEmpty()) {
            System.out.println("No Alerts found in the database");
        }
        return alerts;
    }

    // DELETE: Remove all alerts
    @Transactional
    public void clearAlerts() {
        alert_repo.deleteAll();
        System.out.println("Alert table cleared successfully");
    }

    public ResponseEntity<Alert> findById(int id) {
        return alert_repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public boolean existsById(Integer id) {
        return alert_repo.existsById(id);
    }

    public void deleteById(Integer id) {
        alert_repo.deleteById(id);
    }

    public List getSeverityStatistics() {
        return alert_repo.countBySeverity();
    }
}
/*
 @Transactional ensures that related database operations are executed as a single transaction, committing all changes on success or rolling them back on failure.
*/