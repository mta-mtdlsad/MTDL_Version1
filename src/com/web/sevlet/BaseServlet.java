package com.web.sevlet;

import com.web.util.HtmlUtil;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

// Abstract base servlet that provides common methods for other servlets in the application
public abstract class BaseServlet extends HttpServlet {

    // Prepares the HTTP response before sending HTML content
    protected PrintWriter prepareResponse(HttpServletResponse response) throws IOException {

        // Set the response content type to HTML
        response.setContentType("text/html");

        // Set the character encoding to UTF-8
        // This supports special characters correctly
        response.setCharacterEncoding("UTF-8");

        // Return a PrintWriter to write HTML into the response
        return response.getWriter();
    }

    // Starts an HTML page using the specified title
    protected void startPage(PrintWriter out, String title) {

        // Call HtmlUtil to generate the opening HTML structure
        HtmlUtil.startPage(out, title);
    }

    // Ends the HTML page and adds common closing elements
    protected void endPage(PrintWriter out, HttpServletRequest request) {

        // Get the application's context path and pass it to HtmlUtil to finish the page
        HtmlUtil.endPage(out, request.getContextPath());
    }

    // Displays an error page with a specific HTTP status code and an error message
    protected void showError(HttpServletRequest request, HttpServletResponse response, int status, String message) throws IOException {

        // Set the appropriate HTTP status code (400 (Bad Request) or 404 (Not Found))
        response.setStatus(status);

        // Prepare the HTTP response and obtain the PrintWriter
        PrintWriter out = prepareResponse(response);

        // Start the error page with the title "MTMS Error"
        startPage(out, "MTMS Error");

        // Display the error message HtmlUtil.escape() prevents HTML injection
        out.println("<p>" + HtmlUtil.escape(message) + "</p>");

        // Finish the HTML error page
        endPage(out, request);
    }
}

