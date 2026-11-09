package com.web.util;

import com.domain.Alert;
import com.domain.Source;
import com.domain.Track;

import java.util.List;
// Utility class for generating HTML content.
// It provides methods to escape HTML characters, create page layouts, and convert Track and Alert lists into HTML tables.

public class HtmlUtil {
    //Escapes special HTML characters to prevent HTML injection and cross-site scripting (XSS).
    public static String escape(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    //generate the HTML opening structure
    public static void startPage(java.io.PrintWriter out, String title) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>" + escape(title) + "</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>" + escape(title) + "</h1>");
    }

    public static void endPage( java.io.PrintWriter out, String contextPath) {

        out.println("<hr>");
        out.println("<p><a href='" + escape(contextPath)+ "/index.html'>Return Home</a></p>");

        out.println("</body>");
        out.println("</html>");
    }

    // Converts alerts into an HTML table
    public static String convertTracksToHtmlTable(List<Track> list) {
        StringBuilder html = new StringBuilder();

        // Open table and define headers
        html.append("<table border=\"1\" style=\"border-collapse: collapse;\">\n");
        html.append("  <thead>\n");
        html.append("    <tr>\n");
        html.append("      <th>Source ID</th>\n");
        html.append("      <th>Sequence Number</th>\n");
        html.append("      <th>Number</th>\n");
        html.append("      <th>Latitude</th>\n");
        html.append("      <th>Longitude</th>\n");
        html.append("      <th>Speed</th>\n");
        html.append("      <th>Identity Type</th>\n");
        html.append("      <th>Status</th>\n");
        html.append("      <th>Created Time</th>\n");
        html.append("      <th>Recieved Time</th>\n");
        html.append("    </tr>\n");
        html.append("  </thead>\n");
        html.append("  <tbody>\n");

        // Iterate over the list and inject data
        for (Track track : list) {
            html.append("    <tr>\n");
            html.append("      <td>").append(track.getSourceId()).append("</td>\n");
            html.append("      <td>").append(track.getSequenceNumber()).append("</td>\n");
            html.append("      <td>").append(track.getTrackNumber()).append("</td>\n");
            html.append("      <td>").append(track.getLatitude()).append("</td>\n");
            html.append("      <td>").append(track.getLongitude()).append("</td>\n");
            html.append("      <td>").append(track.getSpeed()).append("</td>\n");
            html.append("      <td>").append(track.getIdentityType()).append("</td>\n");
            html.append("      <td>").append(track.getTrackStatus()).append("</td>\n");
            html.append("      <td>").append(track.getCreatedTime()).append("</td>\n");
            html.append("      <td>").append(track.getRecievedTime()).append("</td>\n");
            html.append("    </tr>\n");
        }

        // Close table
        html.append("  </tbody>\n");
        html.append("</table>");

        return html.toString();
    }

    // Converts alerts into an HTML table
    public static String convertAlertsToHtmlTable(List<Alert> list) {
        StringBuilder html = new StringBuilder();

        // Open table and define headers
        html.append("<table border=\"1\" style=\"border-collapse: collapse;\">\n");
        html.append("  <thead>\n");
        html.append("    <tr>\n");
        html.append("      <th>Track Number</th>\n");
        html.append("      <th>Message</th>\n");
        html.append("      <th>Severity</th>\n");
        html.append("      <th>Created Time</th>\n");
        html.append("    </tr>\n");
        html.append("  </thead>\n");
        html.append("  <tbody>\n");

        // Iterate over the list and inject data
        for (Alert alert : list) {
            html.append("    <tr>\n");
            html.append("      <td>").append(alert.getTrackNumber()).append("</td>\n");
            html.append("      <td>").append(alert.getAlertMessage()).append("</td>\n");
            html.append("      <td>").append(alert.getAlertSeverity()).append("</td>\n");
            html.append("      <td>").append(alert.getAlertTime()).append("</td>\n");
            html.append("    </tr>\n");
        }

        // Close table
        html.append("  </tbody>\n");
        html.append("</table>");

        return html.toString();
    }

    // Converts sources into an HTML table
    public static String convertSourcesToHtmlTable(List<Source> list) {
        StringBuilder html = new StringBuilder();

        // Open table and define headers
        html.append("<table border=\"1\" style=\"border-collapse: collapse;\">\n");
        html.append("  <thead>\n");
        html.append("    <tr>\n");
        html.append("      <th>Source ID</th>\n");
        html.append("      <th>Source Status</th>\n");
        html.append("      <th>First Seen</th>\n");
        html.append("      <th>Last Seen</th>\n");
        html.append("      <th>Highest Sequence</th>\n");
        html.append("      <th>Recieved Count</th>\n");
        html.append("      <th>Accepted Count</th>\n");
        html.append("      <th>Rejected Count</th>\n");
        html.append("      <th>Duplicate Count</th>\n");
        html.append("      <th>Processing Failure Count</th>\n");
        html.append("    </tr>\n");
        html.append("  </thead>\n");
        html.append("  <tbody>\n");

        // Iterate over the list and inject data
        for (Source source : list) {
            html.append("    <tr>\n");
            html.append("      <td>").append(source.getSourceId()).append("</td>\n");
            html.append("      <td>").append(source.getSourceStatus()).append("</td>\n");
            html.append("      <td>").append(source.getFirstSeen()).append("</td>\n");
            html.append("      <td>").append(source.getLastSeen()).append("</td>\n");
            html.append("      <td>").append(source.getHighestSequence()).append("</td>\n");
            html.append("      <td>").append(source.getReceivedCount()).append("</td>\n");
            html.append("      <td>").append(source.getAcceptedCount()).append("</td>\n");
            html.append("      <td>").append(source.getRejectedCount()).append("</td>\n");
            html.append("      <td>").append(source.getDuplicateCount()).append("</td>\n");
            html.append("      <td>").append(source.getProcessingFailureCount()).append("</td>\n");
            html.append("    </tr>\n");
        }

        // Close table
        html.append("  </tbody>\n");
        html.append("</table>");

        return html.toString();
    }
}