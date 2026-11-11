//package com.mtms.web.servlet;
//
//import com.mtms.domain.Source;
//import com.mtms.service.SourceService;
//import com.mtms.service.TrackService;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.List;
//import java.util.Map;
//
//public class SourceMonitorServlet extends BaseServlet {
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//        //Set response content type
//        response.setContentType("text/plain;charset=UTF-8");
//
//        // Retrieve all sources from SourceService
//        List<Source> sources = SourceService.getAllSources();
//
//        // Create PrintWriter
//        PrintWriter out = response.getWriter();
//
//
//        out.println("========== LIST OF ALL SOURCES AND COUNTER ==========");
//        if (sources == null || sources.isEmpty()) {
//            sendBadRequest(response, "No source available");
//        } else {
//            for (Source source : sources) {
//                out.println("------------------------------");
//                out.println("Source ID: " + source.getSourceId());
//                out.println("First Seen At: " + source.getFirstSeenAt());
//                out.println("Last Received At: " + source.getLastReceivedAt());
//                out.println("Latest Sequence: " + source.getLatestSequence());
//                out.println("Source Status: " + source.getSourceStatus());
//                out.println("------------------------------");
//            }
//        }
//
//        // getting process counters
//        Map<String, Integer> counter = TrackService.processingCounter();
//
//        // Displaying counter
//        if (counter != null) {
//            out.println("\nDisplaying Processing Count:- ");
//            for (Map.Entry<String, Integer> entry : counter.entrySet()) {
//                out.println(entry.getKey() + ": " + entry.getValue());
//            }
//        }
//        out.close();
//    }
//
//}