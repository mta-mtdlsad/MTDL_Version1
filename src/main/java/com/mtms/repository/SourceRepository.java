package com.mtms.repository;

import com.mtms.model.Source;
import com.mtms.util.DbConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SourceRepository {
    private static Connection connection = DbConnection.getConnection();
    private PreparedStatement preparedStatement = null;

    // Convert a database row into your existing Source domain.
    private Source mapSource(ResultSet rs) throws SQLException {

        Timestamp first = rs.getTimestamp("first_seen");
        Timestamp last = rs.getTimestamp("last_seen");

        return new Source(
                rs.getString("source_id"),
                rs.getString("source_status"),
                first == null ? null : first.toLocalDateTime(),
                last == null ? null : last.toLocalDateTime(),
                rs.getInt("highest_sequence"),
                rs.getInt("received_count"),
                rs.getInt("accepted_count"),
                rs.getInt("rejected_count"),
                rs.getInt("duplicate_count"),
                rs.getInt("processing_failure_count")
        );
    }

    // Register a source only if it does not already exist.
    // Existing counters must NOT be reset.
    public void registerSource(String sourceId) throws SQLException {
        String sql = """
                INSERT INTO sources (
                    source_id, source_status, highest_sequence,
                    received_count, accepted_count, rejected_count,
                    duplicate_count, processing_failure_count
                )
                VALUES (?, 'NOT SEEN', 0, 0, 0, 0, 0, 0)
                ON CONFLICT (source_id) DO NOTHING
                """;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, sourceId);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        }
    }

    // Update source counters after the worker processes a packet.
    // The SQL increments counters instead of overwriting them.
    public void recordOutcome(
            String sourceId,
            LocalDateTime receivedAt,
            String outcome,
            int sequence
    ) throws SQLException {
        if (!List.of("ACCEPTED", "REJECTED", "DUPLICATE",
                "FAILURE").contains(outcome)) {
            throw new IllegalArgumentException("Invalid outcome");
        }
        // Each processed packet increases received_count by one.
        int accepted = outcome.equals("ACCEPTED") ? 1 : 0;
        int rejected = outcome.equals("REJECTED") ? 1 : 0;
        int duplicate = outcome.equals("DUPLICATE") ? 1 : 0;
        int failure = (outcome.equals("REJECTED") || outcome.equals("DUPLICATE")) ? 1 : 0;
        // Only accepted observations advance highest_sequence.
        int highest = accepted == 1 ? sequence : 0;
        String sql = """
                INSERT INTO sources (
                    source_id, source_status, first_seen, last_seen,
                    highest_sequence, received_count, accepted_count,
                    rejected_count, duplicate_count,
                    processing_failure_count
                )
                VALUES (?, 'ACTIVE', ?, ?, ?, 1, ?, ?, ?, ?)
                ON CONFLICT (source_id) DO UPDATE SET
                    first_seen = COALESCE(
                        sources.first_seen, EXCLUDED.first_seen
                    ),
                    last_seen = GREATEST(
                        sources.last_seen, EXCLUDED.last_seen
                    ),
                    highest_sequence = GREATEST(
                        COALESCE(sources.highest_sequence, 0),
                        EXCLUDED.highest_sequence
                    ),
                    received_count =
                        COALESCE(sources.received_count, 0) + 1,
                    accepted_count =
                        COALESCE(sources.accepted_count, 0)
                        + EXCLUDED.accepted_count,
                    rejected_count =
                        COALESCE(sources.rejected_count, 0)
                        + EXCLUDED.rejected_count,
                    duplicate_count =
                        COALESCE(sources.duplicate_count, 0)
                        + EXCLUDED.duplicate_count,
                    processing_failure_count =
                        COALESCE(sources.processing_failure_count, 0)
                        + EXCLUDED.processing_failure_count
                """;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, sourceId);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(receivedAt));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(receivedAt));
            preparedStatement.setInt(4, highest);
            preparedStatement.setInt(5, accepted);
            preparedStatement.setInt(6, rejected);
            preparedStatement.setInt(7, duplicate);
            preparedStatement.setInt(8, failure);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        }
    }

    // Change health status without resetting any counters.
    public void updateStatus(
            String sourceId, String status
    ) throws SQLException {

        String sql = "UPDATE sources SET source_status = ? WHERE source_id = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, sourceId);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        }
    }

    // Read one source.
    public Source getSourceById(String sourceId) throws SQLException {
        String sql = "SELECT * FROM sources WHERE source_id = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, sourceId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next() ? mapSource(rs) : null;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    // Read every registered source.
    public List<Source> getAllSources() throws SQLException {
        List<Source> sources = new ArrayList<>();
        String sql = "SELECT * FROM sources ORDER BY source_id";
        try {
            preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                sources.add(mapSource(rs));
            }
        } catch (Exception e) {
            throw e;
        }
        return sources;
    }
}
