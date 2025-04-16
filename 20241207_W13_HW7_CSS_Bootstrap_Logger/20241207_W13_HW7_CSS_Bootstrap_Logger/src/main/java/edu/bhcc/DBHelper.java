package edu.bhcc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class helps manage database operations related to logging.
 * It includes methods to create the database table if it doesn't already exist, insert new log
 * entries, and retrieve all existing logs.
 */
public class DBHelper {
    // Create a logger to log events during execution
    private static final Logger logger = LoggerFactory.getLogger(DBHelper.class);
    // SQLite database URL
    private static final String dbURL = "jdbc:sqlite:logger.db";
    private final HttpSession session;

    /**
     * Constructor. Takes an HttpSession object as input to allow setting error messages.
     * @param session The HttpSession object used to access the session scope.
     * @throws SQLException if an error occurs while initializing the database connection.
     */
    public DBHelper(HttpSession session) throws SQLException {
        this.session = session;
        initializeDatabase();
    }

    /**
     * Creates the database table ("logger") if it doesn't already exist.
     * @throws SQLException if an error occurs while creating the table.
     */
    private void initializeDatabase() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS logger ("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "timestamp TEXT NOT NULL,"
                + "message TEXT NOT NULL);";
        try (Connection conn = DriverManager.getConnection(dbURL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            logger.error("Error initializing database: {}", e.getMessage(), e);
            throw e; // Rethrow to be handled by the calling method
        }
    }

    /**
     * Inserts a new log entry into the database table.
     * @param log The Log object representing the log entry to be inserted.
     * @throws RuntimeException if an error occurs while inserting the log entry, including the
     * original SQLException.
     */
    public void insertLog(Log log) {
        String SQL = "INSERT INTO logger (timestamp, message) VALUES (?, ?)";

        // Create JDBC connection
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            // Set parameters and execute insert statement
            ps.setString(1, log.getTimestamp());
            ps.setString(2, log.getMessage());
            ps.executeUpdate();
        } catch (SQLException e) {
            // Log the error
            logger.error("Error inserting log: {}", e.getMessage(), e);
            // Show error message with session attribute
            session.setAttribute("errorMessage", "Error logging message. Please try again later.");
            throw new RuntimeException("Database insert failed", e); // Rethrow with original exception
        }
    }

    /**
     * Retrieves all existing log entries from the database table.
     * @return A list of Log objects representing the retrieved log entries.
     * @throws RuntimeException if an error occurs while retrieving logs, including the original SQLException.
     */
    public List<Log> getAllLogs() {
        List<Log> logList = new ArrayList<>();
        String SQL = "SELECT timestamp, message FROM logger";

        // Create JDBC connection
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            // Execute query and process results
            ResultSet rs = ps.executeQuery();

            // Iterate over the result set and add logs to the list
            while (rs.next()) {
                String timestamp = rs.getString("timestamp");
                String message = rs.getString("message");
                Log log = new Log(timestamp, message);
                logList.add(log);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving logs: {}", e.getMessage(), e);
            session.setAttribute("errorMessage", "Error retrieving logs. Please try again later.");
            throw new RuntimeException();
        }

        // If the list is empty, set an error message in the session
        if (logList.isEmpty()) {
            session.setAttribute("errorMessage", "No logs found in the database.");
        }

        return logList;
    }
}