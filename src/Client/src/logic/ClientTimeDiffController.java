package logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * The {@code ClientTimeDiffController} class provides utilities for working with dates and times,
 * including calculating differences, formatting, and managing deadlines and return dates.
 */
public class ClientTimeDiffController {

    // Fields
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Date Window Methods

    /**
     * Checks if the current date falls within the time window defined by the deadline and the required time before it.
     *
     * @param deadLine               the deadline date and time
     * @param timeNeededBeforeDeadline the number of days needed before the deadline
     * @return {@code true} if the current date is within the time window; otherwise, {@code false}
     */
    public Boolean isInTimeWindowToDoAction(LocalDateTime deadLine, int timeNeededBeforeDeadline) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime timeWindowStart = deadLine.minusDays(timeNeededBeforeDeadline);

        return !today.isBefore(timeWindowStart) && !today.isAfter(deadLine);
    }

    /**
     * Checks if the current date falls within the time window defined by the deadline
     * and the required time before it, with both parameters as strings.
     *
     * @param deadLineAsString        the deadline as a formatted string
     * @param timeNeededBeforeDeadline the number of days needed before the deadline
     * @return {@code true} if the current date is within the time window; otherwise, {@code false}
     */
    public Boolean isInTimeWindowToDoAction(String deadLineAsString, int timeNeededBeforeDeadline) {
        LocalDateTime deadLine = convertStringToLocalDateTime(deadLineAsString);
        return isInTimeWindowToDoAction(deadLine, timeNeededBeforeDeadline);
    }

    /**
     * Checks if there is enough time before a deadline to complete an action.
     *
     * @param deadLineAsString        the deadline as a string
     * @param timeNeededBeforeDeadline the number of days needed before the deadline
     * @return {@code true} if there is enough time; otherwise, {@code false}
     */
    public Boolean hasEnoughTimeBeforeDeadline(String deadLineAsString, int timeNeededBeforeDeadline) {
        LocalDateTime deadLine = convertStringToLocalDateTime(deadLineAsString);
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime sevenDaysFromDeadline = deadLine.minusDays(timeNeededBeforeDeadline);
        return !sevenDaysFromDeadline.isAfter(today);
    }

    // Return Date Methods

    /**
     * Calculates the return date by adding the specified number of days to the current date.
     *
     * @param amountOfDays the number of days to add
     * @return the formatted return date as a string
     */
    public String calculateReturnDate(int amountOfDays) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime returnDate = today.plusDays(amountOfDays);
        return returnDate.format(getDateFormatter());
    }

    /**
     * Extends a given return date by the specified number of days.
     *
     * @param returnDate    the original return date
     * @param amountOfDays  the number of days to extend
     * @return the extended return date as a formatted string
     */
    public String extendReturnDate(LocalDateTime returnDate, int amountOfDays) {
        LocalDateTime returnDateAfterExtension = returnDate.plusDays(amountOfDays);
        return returnDateAfterExtension.format(getDateFormatter());
    }

    /**
     * Extends a return date, provided as a string, by the specified number of days.
     *
     * @param returnDateAsString the original return date as a string
     * @param amountOfDays       the number of days to extend
     * @return the extended return date as a formatted string
     */
    public String extendReturnDate(String returnDateAsString, int amountOfDays) {
        LocalDateTime returnDate = convertStringToLocalDateTime(returnDateAsString);
        return extendReturnDate(returnDate, amountOfDays);
    }

    /**
     * Calculates the number of days remaining until a return date.
     *
     * @param returnDateTime the return date
     * @return the number of days left
     */
    public int howMuchTimeLeftToReturnABook(LocalDateTime returnDateTime) {
        LocalDate today = LocalDate.now();
        LocalDate returnDate = returnDateTime.toLocalDate();
        return (int) today.until(returnDate, ChronoUnit.DAYS);
    }

    /**
     * Calculates how much time is left to return a book based on the given return date as a string.
     *
     * @param returnDateTimeAsString the return date as a formatted string
     * @return the number of days remaining until the return date
     */
    public int howMuchTimeLeftToReturnABook(String returnDateTimeAsString) {
        LocalDateTime returnDateTime = convertStringToLocalDateTime(returnDateTimeAsString);
        return howMuchTimeLeftToReturnABook(returnDateTime);
    }

    // Time Difference Methods

    /**
     * Calculates the difference in days between two {@code LocalDateTime} objects.
     *
     * @param firstDate  the first date
     * @param secondDate the second date
     * @return the difference in days between the two dates
     */
    public int timeDateDifferenceBetweenTwoDates(LocalDateTime firstDate, LocalDateTime secondDate) {
        return (int) firstDate.until(secondDate, ChronoUnit.DAYS);
    }

    /**
     * Calculates the difference in days between a {@code LocalDateTime} and a date string.
     *
     * @param firstDate          the first date
     * @param secondDateAsString the second date as a string
     * @return the difference in days between the two dates
     */
    public int timeDateDifferenceBetweenTwoDates(LocalDateTime firstDate, String secondDateAsString) {
        LocalDateTime secondDate = convertStringToLocalDateTime(secondDateAsString);
        return timeDateDifferenceBetweenTwoDates(firstDate, secondDate);
    }

    /**
     * Calculates the difference in days between a date string and a {@code LocalDateTime}.
     *
     * @param firstDateAsString the first date as a string
     * @param secondDate        the second date
     * @return the difference in days between the two dates
     */
    public int timeDateDifferenceBetweenTwoDates(String firstDateAsString, LocalDateTime secondDate) {
        LocalDateTime firstDate = convertStringToLocalDateTime(firstDateAsString);
        return timeDateDifferenceBetweenTwoDates(firstDate, secondDate);
    }

    /**
     * Calculates the difference in days between two date strings.
     *
     * @param firstDateAsString  the first date as a string
     * @param secondDateAsString the second date as a string
     * @return the difference in days between the two dates
     */
    public int timeDateDifferenceBetweenTwoDates(String firstDateAsString, String secondDateAsString) {
        LocalDateTime firstDate = convertStringToLocalDateTime(firstDateAsString);
        LocalDateTime secondDate = convertStringToLocalDateTime(secondDateAsString);
        return timeDateDifferenceBetweenTwoDates(firstDate, secondDate);
    }

    // Utility Methods

    /**
     * Returns the current date formatted as a string.
     *
     * @return the formatted current date
     */
    public String timeNow() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(getDateFormatter());
    }

    /**
     * Converts a date string into a {@code LocalDateTime} object.
     *
     * @param stringToConvert the date string to convert
     * @return the corresponding {@code LocalDateTime} object
     */
    public LocalDateTime convertStringToLocalDateTime(String stringToConvert) {
        LocalDate localDate = LocalDate.parse(stringToConvert, getDateFormatter());
        return localDate.atStartOfDay();
    }

    /**
     * Gets the current date formatter.
     *
     * @return the current {@code DateTimeFormatter}
     */
    public DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    /**
     * Sets a new date formatter.
     *
     * @param dateFormatter the new {@code DateTimeFormatter} to set
     */
    public void setDateFormatter(DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }
}
