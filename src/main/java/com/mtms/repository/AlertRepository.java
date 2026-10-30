package com.mtms.repository;

import com.mtms.domain.Alert;
import com.mtms.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlertRepository {
    private static Connection connection = DbConnection.getConnection();
    private static PreparedStatement preparedStatement = null;
    private static List<Alert> listOfAlerts = null;

    // Insert an alert into the database
    public static void addAlert(Alert alert) {
        try{
            preparedStatement = connection.prepareStatement("INSERT INTO ALERTS " +
                    "(track_number, alert_message, alert_severity, alert_time) " +
                    "VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1,alert.getTrackNumber());
            preparedStatement.setString(2,alert.getAlertMessage());
            preparedStatement.setString(3,alert.getAlertSeverity());
            preparedStatement.setTimestamp(4,Timestamp.valueOf(alert.getAlertTime()));
            preparedStatement.executeUpdate();

            System.out.println("Alert added successfully");
        } catch(Exception e){
            System.out.println("Alert details not inserted" + e);
            e.printStackTrace();
        }
    }

    // Retrieve all alerts (select statement)
    public static List<Alert> getAllAlerts() {
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM ALERTS");
            ResultSet resultSet = preparedStatement.executeQuery();

            listOfAlerts = new ArrayList<Alert>();
            while(resultSet.next()) {
                listOfAlerts.add(
                        new Alert (resultSet.getInt("alert_id"),
                                resultSet.getString("track_number"),
                                resultSet.getString("alert_message"),
                                resultSet.getString("alert_severity"),
                                resultSet.getTimestamp("alert_time")
                                        .toLocalDateTime()));
            }
            if (!listOfAlerts.isEmpty())
                return listOfAlerts;
        } catch(Exception exy) {
            System.out.println("Unable to fetch all alerts");
        }
        return null;
    }

    // Select to count alerts by severity (LOW, MEDIUM, HIGH) using SQL GROUP BY
    public Map<String, Integer> getAlertSeverity() {
        Map<String, Integer> severitiesCount = new LinkedHashMap<>();

        try{
            preparedStatement= connection.prepareStatement("SELECT alert_severity, COUNT(*) FROM ALERTS GROUP BY alert_severity");
            ResultSet resultSetAlert = preparedStatement.executeQuery();

            while (resultSetAlert.next()){
                String alertSeverity = resultSetAlert.getString(1);
                Integer severityCount = resultSetAlert.getInt(2);

                severitiesCount.put(alertSeverity, severityCount);
            }

        } catch (Exception exs) {
            System.out.println("Unable to fetch alert severity details");
        }
        return severitiesCount;
    }
}