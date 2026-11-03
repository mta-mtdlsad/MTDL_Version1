package com.repository;

import com.domain.Source;
import com.util.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SourceRepository {
    private static Connection connection = DbConnection.getConnection();
    private PreparedStatement preparedStatement = null;
    private List<Source> listOfSources = null;

    // INSERT new source
    public boolean saveSource(Source source) throws Exception{
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO sources VALUES (?,?,?,?,?,?,?,?,?,?);");

            preparedStatement.setString(1, source.getSource_id());
            preparedStatement.setString(2, source.getSource_status());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(source.getFirst_seen()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(source.getLast_seen()));
            preparedStatement.setInt(5, source.getHighest_sequence());
            preparedStatement.setInt(6, source.getReceived_count());
            preparedStatement.setInt(7, source.getAccepted_count());
            preparedStatement.setInt(8, source.getRejected_count());
            preparedStatement.setInt(9, source.getDuplicate_count());
            preparedStatement.setInt(10, source.getProcessing_failure_count());

            return preparedStatement.executeUpdate() == 1;
        }catch (Exception e) {
            System.out.println("Failed to add source");
            throw e;
        }
    }
    // SELECT general function that returns a list based on selected statement
    public List<Source> getList(ResultSet resultSet) throws SQLException {
        try {
            if(resultSet.wasNull()) return null;
            listOfSources = new ArrayList<>();
            while(resultSet.next()) {
                listOfSources.add(
                        new Source(resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getTimestamp(3).toLocalDateTime(),
                                resultSet.getTimestamp(4).toLocalDateTime(),
                                resultSet.getInt(5),
                                resultSet.getInt(6),
                                resultSet.getInt(7),
                                resultSet.getInt(8),
                                resultSet.getInt(9),
                                resultSet.getInt(10)));
            }

            return listOfSources;
        }catch (Exception e) {
            throw e;
        }
    }

    // SELECT
    public List<Source> getAllSources() {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM sources;");
            ResultSet resultSet = preparedStatement.executeQuery();
            return getList(resultSet);
        }catch (Exception e) {
            System.out.println("Unable to fetch all tracks");
        }
        return null;
    }

    // SELECT
    public Source getSourceById(String id) {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM sources WHERE source_id = ?;");
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return new Source(resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getTimestamp(3).toLocalDateTime(),
                        resultSet.getTimestamp(4).toLocalDateTime(),
                        resultSet.getInt(5),
                        resultSet.getInt(6),
                        resultSet.getInt(7),
                        resultSet.getInt(8),
                        resultSet.getInt(9),
                        resultSet.getInt(10));
            }
        }catch (Exception e) {
            System.out.println("Unable to fetch source");
        }
        return null;
    }
}
