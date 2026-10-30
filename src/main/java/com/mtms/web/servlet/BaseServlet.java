package com.mtms.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {

    // Set UTF-8 request encoding before reading POST parameters
    protected void setRequestEncoding(HttpServletRequest request) throws IOException {
        request.setCharacterEncoding("UTF-8");
    }

    // Set UTF-8 response encoding
    protected void setResponseEncoding(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
    }

    // Handle bad input (HTTP 400)
    protected void sendBadRequest(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().println("Bad Request: " + message);
    }

    // Handle unexpected server errors (HTTP 500)
    protected void sendServerError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().println("Internal Server Error: " + message);
    }

    // Set the default response encoding for all requests
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setResponseEncoding(response);
        super.service(request, response);
    }
}