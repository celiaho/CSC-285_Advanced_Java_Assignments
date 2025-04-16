// Source code recreated from a .class file by IntelliJ IDEA (powered by FernFlower decompiler)

package edu.bhcc;

/**
 * This class represents a log entry with an ID, timestamp, and message.
 */
public class Log {
    private int ID;
    private String timestamp;
    private String message;

    /**
     * Constructor. Creates a new Log object with the specified timestamp and message.
     * @param timestamp The timestamp of the log entry.
     * @param message The message content of the log entry.
     */
    public Log (String timestamp, String message) {
        this.ID = ID;
        this.timestamp = timestamp;
        this.message = message;
    }

    /**
     * Gets the ID of the log entry.
     * @return The ID assigned to the log entry by the database.
     */
    public int getID() {
        return ID;
    }

    /**
     * Sets the ID of the log entry.
     * @param ID The ID to be assigned to the log entry.
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Gets the timestamp of the log entry.
     * @return The timestamp string representing the time of the log entry.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the log entry.
     * @param timestamp The timestamp string representing the time of the log entry.
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the message content of the log entry.
     * @return The message string associated with the log entry.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message of the log entry.
     * @param message The message string to be set for the log entry.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}