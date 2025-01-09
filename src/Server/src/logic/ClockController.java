package logic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The ClockController class provides methods to retrieve and format
 * the current date and time.
 */
public class ClockController {

    // The current date and time
    private LocalDateTime currentDateTime;

    /**
     * Constructor initializes the clock with the current date and time.
     */
    public ClockController() {
        updateDateTime();
    }

    /**
     * Updates the current date and time to the system's current date and time.
     */
    public void updateDateTime() {
        this.currentDateTime = LocalDateTime.now();
    }

    /**
     * Returns the current date as a formatted string.
     *
     * @return the current date in the format "yyyy-MM-dd"
     */
    public String getCurrentDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return currentDateTime.format(dateFormatter);
    }
}
