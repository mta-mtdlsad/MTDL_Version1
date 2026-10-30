package com.web.util;

import com.domain.Alert;
import com.domain.Track;

import java.util.List;

public class HtmlUtil {
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

    public static void startPage(
            java.io.PrintWriter out,
            String title) {

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>" + escape(title) + "</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>" + escape(title) + "</h1>");
    }

    public static void endPage(
            java.io.PrintWriter out,
            String contextPath) {

        out.println("<hr>");
        out.println("<p><a href='"
                + escape(contextPath)
                + "/index.html'>Return Home</a></p>");

        out.println("</body>");
        out.println("</html>");
    }

    public static String convertTracksToHtmlTable(List<Track> list) {
        StringBuilder html = new StringBuilder();

        // Open table and define headers
        html.append("<table border=\"1\" style=\"border-collapse: collapse;\">\n");
        html.append("  <thead>\n");
        html.append("    <tr>\n");
        html.append("      <th>Number</th>\n");
        html.append("      <th>Latitude</th>\n");
        html.append("      <th>Longitude</th>\n");
        html.append("      <th>Speed</th>\n");
        html.append("      <th>Identity Type</th>\n");
        html.append("      <th>Status</th>\n");
        html.append("      <th>Created Time</th>\n");
        html.append("    </tr>\n");
        html.append("  </thead>\n");
        html.append("  <tbody>\n");

        // Iterate over the list and inject data
        for (Track track : list) {
            html.append("    <tr>\n");
            html.append("      <td>").append(track.getTrack_number()).append("</td>\n");
            html.append("      <td>").append(track.getLatitude()).append("</td>\n");
            html.append("      <td>").append(track.getLongitude()).append("</td>\n");
            html.append("      <td>").append(track.getSpeed()).append("</td>\n");
            html.append("      <td>").append(track.getIdentity_type()).append("</td>\n");
            html.append("      <td>").append(track.getTrack_status()).append("</td>\n");
            html.append("      <td>").append(track.getCreated_time()).append("</td>\n");
            html.append("    </tr>\n");
        }

        // Close table
        html.append("  </tbody>\n");
        html.append("</table>");

        return html.toString();
    }

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
            html.append("      <td>").append(alert.getTrack_number()).append("</td>\n");
            html.append("      <td>").append(alert.getAlert_message()).append("</td>\n");
            html.append("      <td>").append(alert.getAlert_severity()).append("</td>\n");
            html.append("      <td>").append(alert.getAlert_time()).append("</td>\n");
            html.append("    </tr>\n");
        }

        // Close table
        html.append("  </tbody>\n");
        html.append("</table>");

        return html.toString();
    }
}
