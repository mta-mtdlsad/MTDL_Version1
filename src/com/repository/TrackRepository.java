package com.repository;

import com.domain.Track;
import com.util.DbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TrackRepository {
    private static Connection connection = DbConnection.getConnection();
    private PreparedStatement preparedStatement = null;
    private List<Track> listOfTracks = null;

    // INSERT
    public void saveTrack(Track track) {
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO tracks (track_number,latitude,longitude,speed,identity_type,track_status,created_time) VALUES (?,?,?,?,?,?,?);");

            preparedStatement.setString(1, track.getTrack_number());
            preparedStatement.setDouble(2, track.getLatitude());
            preparedStatement.setDouble(3, track.getLongitude());
            preparedStatement.setDouble(4, track.getSpeed());
            preparedStatement.setString(5, track.getIdentity_type());
            preparedStatement.setString(6, track.getTrack_status());
            preparedStatement.setTimestamp(7, Timestamp.valueOf(track.getCreated_time()));

            preparedStatement.executeUpdate();
            System.out.println("Track added successfully");
        }catch (Exception e) {
            System.out.println("Failed to add track");
        }
    }

    // SELECT
    public List<Track> getAllTracks() {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tracks ORDER BY created_time, track_id;");
            ResultSet resultSet = preparedStatement.executeQuery();

            listOfTracks = new ArrayList<Track>();
            while(resultSet.next()) {
                listOfTracks.add(
                        new Track(resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getDouble(3),
                                resultSet.getDouble(4),
                                resultSet.getDouble(5),
                                resultSet.getString(6),
                                resultSet.getString(7),
                                resultSet.getTimestamp(8).toLocalDateTime()));
            }

            return listOfTracks;
        }catch (Exception e) {
            System.out.println("Unable to fetch all tracks");
        }
        return null;
    }

    // SELECT
    public Track getTrackByNo(String number) {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tracks WHERE track_number = ? ORDER BY created_time DESC, track_id DESC LIMIT 1;");
            preparedStatement.setString(1, number);
            ResultSet resultSet = preparedStatement.executeQuery();

            Track track;

            if(resultSet.next()) {
                track = new Track(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getDouble(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getTimestamp(8).toLocalDateTime());
                return track;
            }
            System.out.println("Track with number " + number + " not found");
            return null;
        }catch (Exception e) {
            System.out.println("Unable to fetch track");
        }
        return null;
    }

    // DELETE
    public String deleteTrackByNo(String number) {
        try {
            preparedStatement = connection.prepareStatement(
                    "DELETE FROM tracks WHERE track_number=?;");
            preparedStatement.setString(1, number);
            preparedStatement.executeUpdate();
            return "Track " + number + " deleted successfully";
        }catch(Exception e) {
            return "Track " + number + " cannot be deleted";
        }
    }

    // SELECT
    public List<Track> getAllHostileTracks() {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tracks WHERE identity_type = 'HOSTILE' ORDER BY created_time;");
            ResultSet resultSet = preparedStatement.executeQuery();

            listOfTracks = new ArrayList<Track>();
            while(resultSet.next()) {
                listOfTracks.add(
                        new Track(resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getDouble(3),
                                resultSet.getDouble(4),
                                resultSet.getDouble(5),
                                resultSet.getString(6),
                                resultSet.getString(7),
                                resultSet.getTimestamp(8).toLocalDateTime()));
            }

            return listOfTracks;
        }catch (Exception e) {
            System.out.println("Unable to fetch all tracks");
        }
        return null;
    }

    // SELECT
    public List<Track> getTracksBySpeed(Double speed) {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tracks WHERE speed > ? ORDER BY speed DESC;");
            preparedStatement.setDouble(1, speed);
            ResultSet resultSet = preparedStatement.executeQuery();

            listOfTracks = new ArrayList<Track>();
            while(resultSet.next()) {
                listOfTracks.add(
                        new Track(resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getDouble(3),
                                resultSet.getDouble(4),
                                resultSet.getDouble(5),
                                resultSet.getString(6),
                                resultSet.getString(7),
                                resultSet.getTimestamp(8).toLocalDateTime()));
            }

            return listOfTracks;
        }catch (Exception e) {
            System.out.println("Unable to fetch tracks");
        }
        return null;
    }

    // SELECT
    public void getStatistics() {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT identity_type, COUNT(*) FROM tracks GROUP BY identity_type;");
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                String identity_type = resultSet.getString(1);
                int count = resultSet.getInt(2);
                System.out.println(identity_type + " | " + count);
            }
        }catch (Exception e) {
            System.out.println("Unable to fetch tracks");
        }
    }
}
