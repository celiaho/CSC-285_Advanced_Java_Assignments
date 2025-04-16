package edu.bhcc;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet handles HTTP GET requests for the main logger page.
 * It retrieves existing log messages from the database using DBHelper.java, prepares a model for
 * the Freemarker template, and renders the logger.html template with the retrieved data and any
 * potential error messages from the session.
 */
public class CreateFormServlet extends HttpServlet {
    // Create a logger to log events during execution
    private static final Logger logger = LoggerFactory.getLogger(CreateFormServlet.class);

    /**
     * Process an HTTP request.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        // Get existing logs from database
        List<Log> logList = new ArrayList<>();
        DBHelper DBHelper = null;
        try {
            DBHelper = new DBHelper(request.getSession());
            logList = DBHelper.getAllLogs();
        } catch (SQLException e) {
            // Handle the exception: Log the error and redirect to an error page
            logger.error("Error retrieving logs: {}", e.getMessage(), e);
            // Set error message in session for template
            request.setAttribute("errorMessage", "Error retrieving logs. Please try again.");
            // Forward to error.html
            request.getRequestDispatcher("/error.html").forward(request, response);
            return;
        }

        // Create the model
        Map<String, Object> root = new HashMap<String, Object>();

        // Check if the log list is empty
        if (logList.isEmpty()) {
            // Log error message
            logger.warn("No logs found in the database.");
            // Display error message in the template
            root.put("errorMessage", "There are no log messages yet.");
        } else {
            // Add the log list to the root map if retrieved
            root.put("logList", logList);
        }

        // Pass the error message from session to the model
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");
        if (errorMessage != null) {
            // Add error message to model
            root.put("errorMessage", errorMessage);
            // Clear the error message after using it
            request.getSession().removeAttribute("errorMessage");
        }

        // Process the template using Freemarker
        try {
            // Get the FreeMarker Template Engine
            FreeMarkerUtil setup = FreeMarkerUtil.getInstance();
            Configuration cfg = setup.getFreeMarkerConfiguration();
            Template template = cfg.getTemplate("logger.html");
            //  Merge the model with the template
            PrintWriter writer = response.getWriter();
            template.process(root, writer);
        } catch (TemplateException | IOException e) {
            // Handle potential template processing issues
            logger.error("Error processing template: {}", e.getMessage(), e);
            // Display a generic error message in the response
            response.getWriter().println("Error processing template. Please try again later.");
        }
    }
}