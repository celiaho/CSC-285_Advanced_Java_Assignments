package edu.bhcc;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet handles POST requests from the form submission.
 * It retrieves the message from the form, creates a Log object with timestamp, inserts the log into
 * the database using DBHelper.java, and redirects back to the main page ("/logger").
 * In case of errors during processing, it sets an error message in the session and redirects to the
 * error page ("/error").
 */
public class ProcessFormServlet extends HttpServlet {
    // Create a logger to log events during execution
    private static final Logger logger = LoggerFactory.getLogger(ProcessFormServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Get message from HTML form
        String message = request.getParameter("message");

        // Capture timestamp via LocalTime & convert to a string
        LocalDateTime now = LocalDateTime.now();
        // Reference: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
        String timestamp = now.format(DateTimeFormatter.ofPattern("EEE MM/dd/yyyy HH:mm:ss a"));

        // Create a Log object
        Log log = new Log(timestamp, message);
        // Create a List to store multiple Log objects & add the current log to the list
        List<Log> logList = new ArrayList<>();
        logList.add(log);

        // Create an instance of the DBHelper class
        DBHelper DBHelper;
        // Insert log into database
        try {
            DBHelper = new DBHelper(request.getSession());
            DBHelper.insertLog(log);
        } catch (SQLException e) {
            logger.error("Error processing form: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Error processing your log entry. Please try again.");
            request.getRequestDispatcher("/error.html").forward(request, response);
            return;
        }

        // Redirect to the main page
        response.sendRedirect("/logger");
    }
}
