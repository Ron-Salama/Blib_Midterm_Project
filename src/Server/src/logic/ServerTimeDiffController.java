package logic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * The ClockController class provides methods to retrieve and format
 * the current date and time.
 */
public class ServerTimeDiffController {

    // The current date and time
    private LocalDateTime currentDateTime;

    /**
     * Constructor initializes the clock with the current date and time.
     */
    public ServerTimeDiffController() {
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
     * @return the current date in the format "dd-MM-yyyy"
     */
    public String getCurrentDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return currentDateTime.format(dateFormatter);
    }
    
    public int howMuchTimeLeftToReturnABook(LocalDateTime returnDateTime) {
		LocalDateTime today = LocalDateTime.now();
		int timeDiff = (int) today.until(today, ChronoUnit.DAYS);
		return timeDiff;
	}
    
    
}
