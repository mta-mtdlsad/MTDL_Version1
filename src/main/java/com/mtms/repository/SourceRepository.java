package com.mtms.repository;

import com.mtms.domain.Source;
import com.mtms.domain.Alert;
import com.mtms.util.DbConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SourceRepository {

    private static Connection connection = DbConnection.getConnection();
    private static PreparedStatement preparedStatement = null;
    private static List<Source> listOfSources = null;

    // Register a new source
    public static void registerSource(Source source) {
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO SOURCE " +
                    "(source_id, first_seen_at, last_received_at, latest_sequence, source_status) "
                    + "VALUES (?, ?, ?, ?, ?) ON CONFLICT (source_id) DO NOTHING");
            preparedStatement.setString(1, source.getSourceId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(source.getFirstSeenAt()));
            // last_received_at by default is null, that's why we use if statement
            if (source.getLastReceivedAt() != null) {
                preparedStatement.setTimestamp(3, Timestamp.valueOf(source.getLastReceivedAt()));
            } else {
                preparedStatement.setTimestamp(3, null);
            }
            preparedStatement.setLong(4, source.getLatestSequence());
            preparedStatement.setString(5, source.getSourceStatus());
            preparedStatement.executeUpdate();
            System.out.println("Source registered successfully");
        } catch (Exception e) {
            System.out.println("Source details not inserted: " + e);
            e.printStackTrace();
        }
    }

    // Retrieve all registered sources
    public static List<Source> getAllSources() {
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM SOURCE");
            ResultSet resultSet = preparedStatement.executeQuery();
            listOfSources = new ArrayList<Source>();

            while (resultSet.next()) {
                Timestamp lastReceived = resultSet.getTimestamp("last_received_at");
                listOfSources.add(new Source(
                        resultSet.getString("source_id"),
                        resultSet.getTimestamp("first_seen_at").toLocalDateTime(), lastReceived != null ? lastReceived.toLocalDateTime() : null,
                        resultSet.getLong("latest_sequence"),
                        resultSet.getString("source_status")));
            }
            if (!listOfSources.isEmpty())
                return listOfSources;
        } catch (Exception exy) {
            System.out.println("Unable to fetch all sources");
            exy.printStackTrace();
        }
        return null;
    }

    // Retrieve a source using its ID
    public static Source getSourceById(String sourceId) {
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM SOURCE WHERE source_id = ?");
            preparedStatement.setString(1, sourceId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Timestamp lastReceived = resultSet.getTimestamp("last_received_at");
                return new Source(
                        resultSet.getString("source_id"),
                        resultSet.getTimestamp("first_seen_at").toLocalDateTime(), lastReceived != null ? lastReceived.toLocalDateTime() : null,
                        resultSet.getLong("latest_sequence"),
                        resultSet.getString("source_status"));
            }
        } catch (Exception e) {
            System.out.println("Unable to retrieve source: " + e);
            e.printStackTrace();
        }
        return null;
    }

    // Update the last received time
    public static void updateLastReceivedTime(String sourceId, LocalDateTime receivedTime) {
        try {
            preparedStatement = connection.prepareStatement("UPDATE SOURCE SET last_received_at = ?, source_status = 'ACTIVE' " + "WHERE source_id = ?");
            preparedStatement.setTimestamp(1, Timestamp.valueOf(receivedTime));
            preparedStatement.setString(2, sourceId);
            preparedStatement.executeUpdate();
            System.out.println("Source receive time updated successfully");
        } catch (Exception e) {
            System.out.println("Unable to update receive time: " + e);
            e.printStackTrace();
        }
    }

    // Update sequence number only if it is greater than the latest sequence
    public static boolean updateLatestSequenceIfValid(String sourceId, long sequenceNumber) {
        try {
            preparedStatement = connection.prepareStatement("UPDATE SOURCE SET latest_sequence = ? " + "WHERE source_id = ? AND latest_sequence < ?");
            preparedStatement.setLong(1, sequenceNumber);
            preparedStatement.setString(2, sourceId);
            preparedStatement.setLong(3, sequenceNumber);
            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated == 1;
        } catch (Exception e) {
            System.out.println("Unable to update sequence number: " + e);
            e.printStackTrace();
        }
        return false;
    }

    // Calculate source status using the latest receive time
    private static String calculateStatus(LocalDateTime lastReceived, LocalDateTime checkTime) {
        if (lastReceived == null) {
            return "NOT SEEN";
        }
        Duration timeDifference = Duration.between(lastReceived, checkTime);
        if (timeDifference.compareTo(Duration.ofSeconds(15)) <= 0) {
            return "ACTIVE";
        } else if (timeDifference.compareTo(Duration.ofSeconds(60)) <= 0) {
            return "STALE";
        } else {
            return "DISCONNECTED";
        }
    }

    // Update source status and insert its alert in one transaction
    public static boolean updateSourceStatus(String sourceId, String oldStatus, String newStatus, LocalDateTime checkTime, Alert alert) {
        try {
            // Disable auto-commit to start a database transaction
            // This allows the source status update and alert insertion to be saved together
            connection.setAutoCommit(false);
            // Retrieve the last received time and current status of the source
            // FOR UPDATE locks the selected row to prevent simultaneous status changes
            preparedStatement = connection.prepareStatement("SELECT last_received_at, source_status FROM SOURCE WHERE source_id = ? FOR UPDATE");
            preparedStatement.setString(1, sourceId);
            ResultSet resultSet = preparedStatement.executeQuery();
            // Check whether the source exists in the database
            // If it does not exist, close resources, cancel the transaction and return false
            if (!resultSet.next()) {
                resultSet.close();
                preparedStatement.close();
                connection.rollback();
                return false;
            }
            // Retrieve the last received time and current source status
            Timestamp timestamp = resultSet.getTimestamp("last_received_at");
            String currentStatus = resultSet.getString("source_status");
            // Convert the last received time into LocalDateTime
            // If it is NULL, the source has never received a message
            LocalDateTime lastReceived = null;
            if (timestamp != null) {
                lastReceived = timestamp.toLocalDateTime();
            }
            resultSet.close();
            preparedStatement.close();
            // Check whether the current database status matches the old status
            // If another operation has already changed the status, cancel the transaction
            if (currentStatus == null || !currentStatus.equals(oldStatus)) {
                connection.rollback();
                return false;
            }
            // Recalculate the source status using its latest receive time
            // This ensures that the new status is still correct
            String actualStatus = calculateStatus(lastReceived, checkTime);
            // Check whether the calculated status matches the expected new status
            // If they are different, cancel the transaction
            if (!actualStatus.equals(newStatus)) {
                connection.rollback();
                return false;
            }
            // Update the source status in the SOURCE table
            preparedStatement = connection.prepareStatement("UPDATE SOURCE SET source_status = ? WHERE source_id = ?");
            preparedStatement.setString(1, newStatus);
            preparedStatement.setString(2, sourceId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            // Insert an alert if one was provided
            // The alert uses the same database transaction as the source update
            // alert only when the source changes to STALE or DISCONNECTED
            if (alert != null &&
                    (newStatus.equals("STALE") || newStatus.equals("DISCONNECTED")) &&
                    !oldStatus.equals(newStatus)) {

                AlertRepository.addAlert(connection, alert);
            }
            // Commit the transaction
            // Save the source status update and alert insertion together
            connection.commit();
            System.out.println("Source status updated successfully");
            return true;
        } catch (Exception e) {
            // If an error occurs, cancel all uncommitted database changes
            try {
                connection.rollback();
            } catch (Exception ex) {
                System.out.println(ex);
            }
            System.out.println("Unable to update source status");
            return false;
        }
    }
}