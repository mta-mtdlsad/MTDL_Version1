package com.mtms.web.rest;

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

    /*
     * Creates and saves a new Alert from the JSON request body (POST /api/alerts).
     *
     * @param alert The Alert entity parsed from the request payload.
     * @return The saved Alert entity including its generated ID.
     */
    @PostMapping
    public Alert createAlert(@RequestBody Alert alert) {
        return alertService.saveAlert(alert);
    }

    /*
     * Retrieves all alerts ordered chronologically from oldest to newest (GET /api/alerts/list).
     *
     * @return A list of all Alert entities.
     */
    @GetMapping("/list")
    public List<Alert> getAllAlerts() {
        return alertService.getAlerts();
    }

    /*
     * Retrieves a single alert by its primary key ID (GET /api/alerts/{id}).
     *
     * @param id The ID of the alert to look up.
     * @return 200 OK with the Alert if found, or 404 Not Found if it does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable Integer id) {
        return alertService.findById(id);
    }

    /*
     * Deletes an existing alert by its primary key ID (DELETE /api/alerts/{id}).
     *
     * @param id The ID of the alert to delete.
     * @return 204 No Content if deleted successfully, or 404 Not Found if the alert does not exist.
     */
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