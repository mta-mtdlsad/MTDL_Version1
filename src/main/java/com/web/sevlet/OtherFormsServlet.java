package com.web.sevlet;

import com.config.AppConfig;
import com.domain.Track;
import com.network.UDPReceiver;
import com.service.SourceHealthService;
import com.service.TrackService;
import com.simulator.TrackSimulator;
import com.web.util.HtmlUtil;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

//@WebServlet(urlPatterns = "/async", asyncSupported = true)
public class OtherFormsServlet extends BaseServlet {
    HtmlUtil html_util = new HtmlUtil();
    TrackService track_service;
    List<Track> listOfTracks = null;
    private UDPReceiver receiver;
    private SourceHealthService sourceHealthService;

    @Override
    public void init() throws ServletException {
        super.init();
        ApplicationContext context = (ApplicationContext) getServletContext().getAttribute("springContext");
        if (context == null) {
            throw new ServletException("Spring context not initialised");
        }
        track_service = context.getBean(TrackService.class);
        receiver = context.getBean(UDPReceiver.class);
        sourceHealthService = context.getBean(SourceHealthService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Prepare the HTTP response and obtain the PrintWriter
        PrintWriter writer = prepareResponse(response);
        StringBuilder resultPage = new StringBuilder();

        if (request.getParameter("purpose").equals("correlation")) {
            // Start the HTML page with the title
            startPage(writer, "Correlation Result");

            String s1 = " = '" + request.getParameter("correlationSource") + "'";
            String s2 = " = " + request.getParameter("correlationSequence");
            List<String> filter = Arrays.asList("source_id", "sequence_number");
            List<String> filter_values = Arrays.asList(s1, s2);
            Track selected_track = track_service.filterTracks(filter, filter_values, null, null, null).getFirst();

            listOfTracks = track_service.correlateTrack(selected_track);
            listOfTracks.forEach(System.out::println);
            resultPage.append("<p>Potential Correlation<br><br>");
            resultPage.append(html_util.convertTracksToHtmlTable(listOfTracks));
            resultPage.append("</p>");

        } else if ("simulation".equals(request.getParameter("purpose"))) {
            startPage(writer, "Simulation Result");
            try {
                // Use the existing Spring-managed source service.
                TrackSimulator simulator_random = new TrackSimulator(sourceHealthService);
//                // Record statistics before simulation.
//                long receivedBefore = receiver.receivedCount.get();
//                long acceptedBefore = receiver.acceptedCount.get();
//                long rejectedBefore = receiver.rejectedCount.get();
//                long duplicateBefore = receiver.duplicateCount.get();
//                long outOfOrderBefore = receiver.outOfOrderCount.get();
//                long failureBefore = receiver.failureCount.get();
//                long droppedBefore = receiver.droppedCount.get();

                int[] params = {
                        Integer.parseInt(request.getParameter("numberOfSources")),
                        Integer.parseInt(request.getParameter("numberOfTracks")),
                        Integer.parseInt(request.getParameter("numberOfObservations")),
                        Integer.parseInt(request.getParameter("sendingRate"))
                };
                simulator_random.initializeSimulator(
                        request.getParameter("simulationScenario"),
                        Integer.parseInt(request.getParameter("simulationMode")),
                        12345L,
                        params
                );
                while (simulator_random.isRunning()) {
                    Thread.sleep(200);
                }
                // The simulator has finished sending.
//                // The receiver may still be processing queued messages.
//                long received = receiver.receivedCount.get() - receivedBefore;
//                long accepted = receiver.acceptedCount.get() - acceptedBefore;
//                long rejected = receiver.rejectedCount.get() - rejectedBefore;
//                long duplicate = receiver.duplicateCount.get() - duplicateBefore;
//                long outOfOrder = receiver.outOfOrderCount.get() - outOfOrderBefore;
//                long failure = receiver.failureCount.get() - failureBefore;
//                long dropped = receiver.droppedCount.get() - droppedBefore;
                writer.println("<h2>Simulation Statistics</h2>");
                writer.println("<p>Received Count: " + receiver.receivedCount.get() + "</p>");
                writer.println("<p>Accepted Count: " + receiver.acceptedCount.get() + "</p>");
                writer.println("<p>Rejected Count: " + receiver.rejectedCount.get() + "</p>");
                writer.println("<p>Duplicate Count: " + receiver.duplicateCount.get() + "</p>");
                writer.println("<p>Out Of Order Count: " + receiver.outOfOrderCount.get() + "</p>");
                writer.println("<p>Failure Count: " + receiver.failureCount.get() + "</p>");
                writer.println("<p>Dropped Count: " + receiver.droppedCount.get() + "</p>");
                writer.println("<p>Queue Count: " + receiver.queue.size() + "</p>");
                writer.println("<p>Simulation sending completed.</p>");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                response.setStatus(500);
                writer.println("<p>Simulation interrupted.</p>");
            } catch (Exception e) {
                response.setStatus(500);
                writer.println("<p>Simulation failed.</p>");
                e.printStackTrace();
            }
        }
    }
}