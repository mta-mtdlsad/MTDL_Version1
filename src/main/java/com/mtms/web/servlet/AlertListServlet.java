package com.mtms.web.servlet;

import com.mtms.domain.Alert;
import com.mtms.service.AlertService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@WebServlet("/alerts/list")
public class AlertListServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Set response content type
        response.setContentType("text/plain;charset=UTF-8");

        // Retrieve all alerts from AlertService
        List<Alert> alerts = AlertService.getAllAlerts();

        // Create PrintWriter
        PrintWriter out = response.getWriter();


        out.println("========== LIST OF ALL ALERTS ==========");
        if (alerts == null || alerts.isEmpty()) {
            sendBadRequest(response, "No alert available");
        } else {
            for (Alert alert : alerts) {
                out.println("------------------------------");
                out.println("Alert ID: " + alert.getAlertId());
                out.println("Track Number: " + alert.getTrackNumber());
                out.println("Alert Message: " + alert.getAlertMessage());
                out.println("Alert Severity: " + alert.getAlertSeverity());
                out.println("Alert Time: " + alert.getAlertTime());
                out.println("------------------------------");
            }
        }
        out.close();
    }
}