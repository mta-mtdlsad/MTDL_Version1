package com.web.sevlet;

import com.domain.Source;
import com.domain.Track;
import com.service.SourceHealthService;
import com.service.TrackService;
import com.web.util.HtmlUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class SourceListServlet extends BaseServlet {
    private SourceHealthService source_service;
    private HtmlUtil html_util = new HtmlUtil();

    @Override
    public void init() throws ServletException {
        super.init();
        ApplicationContext context = (ApplicationContext) getServletContext().getAttribute("springContext");
        if (context == null) {
            throw new ServletException("Spring context not initialised");
        }
        source_service = context.getBean(SourceHealthService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Prepare the HTTP response and obtain the PrintWriter
        PrintWriter writer = prepareResponse(response);

        // Start the HTML page with the title "Track List Result"
        startPage(writer, "Source List Result");

        try {
            List<Source> listOfSource;
            listOfSource = source_service.getAllSources();
            writer.print(html_util.convertSourcesToHtmlTable(listOfSource));

        } catch (Exception e) {
            // Set the HTTP response status to 500 (Internal Server Error)
            response.setStatus(500);
            // Display an error message if retrieval fails
            writer.println("<p>Unable to get Sources.</p>");
        }
        endPage(writer, request);// Finish the HTML page and include any common page elements
    }
}

