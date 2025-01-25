package logic;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import common.ConnectToDb;
import gui.baseController.BaseController;
import server.EchoServer;

/**
 * The {@code FreezeController} class handles the logic of freezing and unfreezing subscribers
 * based on overdue or long-term frozen status. It orchestrates the daily freezing/unfreezing
 * tasks to keep the library's subscriber statuses consistent.
 * 
 * <p>This class extends {@link BaseController} to leverage shared utility methods (e.g. parsing).</p>
 * 
 * <p>Usage:
 * <ul>
 *   <li>Call {@link #freeze()} to freeze subscribers who have overdue books beyond a week.</li>
 *   <li>Call {@link #unfreeze()} to unfreeze subscribers who have been frozen for more than a month.</li>
 *   <li>Call {@link #freezeControllerDailyActivities()} to run a daily process that attempts to unfreeze first,
 *       then freeze, ensuring all statuses are up to date.</li>
 * </ul>
 * </p>
 * 
 * @author 
 * @version 1.0
 * @since 2025-01-01
 */
public class FreezeController extends BaseController {

    /**
     * A reference to the server-side time difference controller, used for date/time operations.
     */
    private final ServerTimeDiffController clock = EchoServer.clock;

    /**
     * Freezes any subscribers who are more than a week late returning their borrowed books.
     * <p>
     * This method:
     * <ul>
     *   <li>Fetches all borrowed books from the database.</li>
     *   <li>Checks if the <em>return date</em> is more than a week overdue.</li>
     *   <li>If yes, and the subscriber is not already frozen, freezes the subscriber 
     *       and logs this event to both CMD and the server log.</li>
     * </ul>
     * </p>
     *
     * @throws SQLException if any SQL error occurs during the freeze process.
     */
    public void freeze() throws SQLException {
        List<String> borrowedBooksList = ConnectToDb.fetchBorrowedBooksForTaskScheduler(EchoServer.taskSchedulerConnection);

        for (String borrowedBook : borrowedBooksList) {
            // parseBorrowedBook(...) inherited from BaseController
            String[] bookData = parseBorrowedBook(borrowedBook);

            // bookData[4] is assumed to be the Return_Time
            LocalDateTime returnDate = clock.convertStringToLocalDateTime(bookData[4]);

            // Check if the book is overdue by more than 7 days
            if (clock.hasWeekPassed(returnDate, LocalDateTime.now())) {
                int subscriberID = Integer.parseInt(bookData[1]);  // Grab subscriber ID
                String bookName = bookData[2];                     // The title or name of the book

                // Retrieve subscriber data to check if they're already frozen
                String subscriber = ConnectToDb.fetchSubscriberData(EchoServer.taskSchedulerConnection, bookData[1]);
                String[] subscriberData = subscriber.split(",");

                // subscriberData[5] is "status:<value>"
                // Check if subscriber is already frozen
                if (subscriberData[5].split(":")[1].equals("Frozen at")) {
                    // Already frozen, skip
                    continue;
                }

                // Freeze the subscriber in the database
                ConnectToDb.freezeSubscriber(EchoServer.taskSchedulerConnection, subscriberID);

                // Log the event in CMD and server log
                String subscriberName = subscriberData[1].split(":")[1]; // subscriber_data[1] is "subscriber_name:<value>"
                EchoServer.outputInOutputStreamAndLog(
                        clock.timeNow()
                        + ": subscriber No' "
                        + subscriberID
                        + ", "
                        + subscriberName
                        + " has been frozen because they were late returning the book: "
                        + bookName
                );
            }
        }
    }

    /**
     * Unfreezes any subscribers who have been frozen for more than a month.
     * <p>
     * This method:
     * <ul>
     *   <li>Fetches all subscribers from the database.</li>
     *   <li>Checks if each subscriber is currently frozen.</li>
     *   <li>If so, determines how long they've been frozen (comparing current date with stored 'frozen at' date).</li>
     *   <li>Unfreezes the subscriber if a month has passed, and logs the event.</li>
     * </ul>
     * </p>
     *
     * @throws SQLException if any SQL error occurs during the unfreeze process.
     */
    @SuppressWarnings("unlikely-arg-type")
    public void unfreeze() throws SQLException {
        List<String> subscribersList = ConnectToDb.fetchAllData(EchoServer.taskSchedulerConnection);

        for (String subscriber : subscribersList) {
            String[] subscriberInformation = subscriber.split(", ");
            // subscriberInformation[5] is "status:<value>"
            String[] status = subscriberInformation[5].split(":");

            // If subscriber is not "Not Frozen", they are frozen
            if (!"Not Frozen".equals(status[1])) {
                // Retrieve subscriber ID & name
                int subscriberID = Integer.parseInt(subscriberInformation[0].split(":")[1]);
                String subscriberName = subscriberInformation[1].split(":")[1];
                String frozenAt = status[2]; // The date/time the subscriber was frozen

                // If a month has passed since 'frozenAt', unfreeze
                if (clock.hasMonthPassed(frozenAt)) {
                    ConnectToDb.unfreezeSubscriber(EchoServer.taskSchedulerConnection, subscriberID);

                    // Log the unfreeze event
                    EchoServer.outputInOutputStreamAndLog(
                            clock.timeNow()
                            + ": subscriber No' "
                            + subscriberID
                            + ", "
                            + subscriberName
                            + " unfrozen since a month has passed."
                    );
                }
            }
        }
    }

    /**
     * Executes the daily activities related to freezing and unfreezing subscribers.
     * <p>
     * The order is:
     * <ol>
     *   <li>{@link #unfreeze()} - check for subscribers needing unfreezing first</li>
     *   <li>{@link #freeze()} - then freeze any new overdue subscribers</li>
     * </ol>
     * This ensures no conflicts if a subscriber requires both actions on the same day.
     * </p>
     *
     * @throws SQLException if any SQL error occurs during the processes.
     */
    public void freezeControllerDailyActivities() throws SQLException {
        // Unfreeze first to avoid a scenario where a subscriber might be
        // re-frozen on the same day they are set to be unfrozen.
        unfreeze();
        freeze();
    }
}
