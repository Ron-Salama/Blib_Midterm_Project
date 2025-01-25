package logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.ConnectToDb;
import gui.baseController.BaseController;
import server.EchoServer;

/**
 * The {@code SMSandEmailController} class manages SMS and email notifications
 * for library subscribers. Specifically, it identifies subscribers with books due 
 * the next day and sends them a single consolidated message listing all such books.
 * <p>
 * Additionally, it can notify subscribers via email when a reserved book is back in stock, 
 * providing details on how long the reservation remains valid.
 * </p>
 *
 * <p>Typical usage:
 * <pre>{@code
 * SMSandEmailController controller = new SMSandEmailController();
 * controller.smsAndEmailControllerDailyActivities(); // sends notifications to those with books due tomorrow
 * }</pre>
 * </p>
 * 
 * <p>In a larger deployment, methods like {@link #sendSMSandEmail(String, List, String, String)} 
 * would connect to actual SMS/email gateways instead of just logging messages via the server console.</p>
 * 
 * @author  
 * @version 1.0
 * @since 2025-01-01
 */
public class SMSandEmailController extends BaseController {

    /**
     * An instance of {@link ServerTimeDiffController} to calculate
     * how much time is left before a book is due.
     */
    private final ServerTimeDiffController clock = EchoServer.clock;

    /**
     * Identifies subscribers who need to return books the next day and sends them 
     * a single SMS/email notification containing all such books.
     * <p>
     * This method:
     * <ul>
     *   <li>Fetches all borrowed books from the database.</li>
     *   <li>Uses {@code howMuchTimeLeftToReturnABook(returnDate)} to see if each book is due tomorrow.</li>
     *   <li>Groups these books by subscriber ID and sends a consolidated notification listing all books due.</li>
     * </ul>
     * </p>
     */
    public void smsAndEmailControllerDailyActivities() {
        // 1. Fetch list of borrowed books
        List<String> borrowedBooksList = ConnectToDb.fetchBorrowedBooksForTaskScheduler(EchoServer.taskSchedulerConnection);

        // 2. Group books needing return by subscriber
        Map<String, List<String>> subscriberBooksMap = new HashMap<>();

        for (String borrowedBook : borrowedBooksList) {
            String[] bookData = parseBorrowedBook(borrowedBook);
            String subscriberID = bookData[1];  // subscriber ID
            String bookName = bookData[2];      // book title
            String returnDate = bookData[4];    // return date (string)

            // If 1 day left until return, add the book to this subscriber's list
            if (clock.howMuchTimeLeftToReturnABook(returnDate) == 1) {
                subscriberBooksMap.computeIfAbsent(subscriberID, k -> new ArrayList<>()).add(bookName);
            }
        }

        // 3. For each subscriber, build and send a single message containing all their due-tomorrow books
        for (Map.Entry<String, List<String>> entry : subscriberBooksMap.entrySet()) {
            String subscriberID = entry.getKey();
            List<String> booksToReturn = entry.getValue();

            // 3a. Fetch subscriber details
            String[] subscriberInformation = ConnectToDb
                    .fetchSubscriberData(EchoServer.taskSchedulerConnection, subscriberID)
                    .split(", ");
            String subscriberName = subscriberInformation[1].split(":")[1];  // subscriber_name:<value>
            String subscriberPhoneNumber = subscriberInformation[3];         // subscriber_phone_number:<value>
            String subscriberEmail = subscriberInformation[4];               // subscriber_email:<value>

            // 3b. Send consolidated message
            sendSMSandEmail(subscriberName, booksToReturn, subscriberPhoneNumber, subscriberEmail);
        }
    }

    /**
     * Sends an SMS and email notification to a subscriber, listing all books they
     * need to return. In a real system, this would connect to actual gateways for SMS and email.
     *
     * @param name        The name of the subscriber.
     * @param bookNames   A list of book titles that need returning.
     * @param phoneNumber The subscriber's phone number (for SMS).
     * @param email       The subscriber's email address (for email).
     */
    public void sendSMSandEmail(String name, List<String> bookNames, String phoneNumber, String email) {
        // Build a consolidated message
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(name).append(",\n");
        message.append("You need to return the following books tomorrow:\n");

        for (String bookName : bookNames) {
            message.append("- ").append(bookName).append("\n");
        }

        message.append("We are looking forward to your visit.\nSee you soon!");

        // For this demo, just log the message instead of sending an actual SMS/Email
        EchoServer.outputInOutputStreamAndLog(message.toString());
    }

    /**
     * Sends an email notification to a subscriber when a reserved book is back in stock,
     * indicating how long the reservation remains valid.
     * <p>
     * In a full implementation, this would connect to an email service or SMTP server
     * to actually send the message.
     * </p>
     *
     * @param subscriberName     The name of the subscriber.
     * @param bookName           The title of the book now available.
     * @param email              The subscriber's email address.
     * @param reservationVoidDate The {@link LocalDateTime} indicating when the reservation expires.
     */
    public static void sendEmailWhenAReservedBookIsBackInStock(
            String subscriberName,
            String bookName,
            String email,
            LocalDateTime reservationVoidDate) {

        // Build the email body
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(subscriberName).append(",\n");
        message.append("The book: ").append(bookName).append(" you requested is now back in stock!\n");
        message.append("You have until: ").append(reservationVoidDate)
               .append(" to borrow the book.\n");
        message.append("We are looking forward to your visit.\nSee you soon!");

        // Placeholder for actual email logic
        System.out.println(message.toString());
    }

    /**
     * Sends a notification to the subscriber that a reserved book is back in stock,
     * highlighting how many days remain to pick it up.
     * <p>
     * This differs from {@link #sendEmailWhenAReservedBookIsBackInStock(String, String, String, LocalDateTime)}
     * in that it focuses on day count instead of a specific date/time stamp.
     * </p>
     *
     * @param subscriberName      The subscriber's name.
     * @param bookName            The title of the book back in stock.
     * @param daysLeftForRetrieval The remaining days to pick up the book.
     */
    public static void sendMailToSubscriberThatNeedToRetrieveBook(String subscriberName, String bookName, int daysLeftForRetrieval) {
        // Build the message
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(subscriberName).append(",\n");
        message.append("The book: ").append(bookName).append(" you ordered is now back in stock!\n");

        if (daysLeftForRetrieval == 1) {
            message.append("You have 1 more day left to retrieve the book.\n");
        } else {
            message.append("You have ").append(daysLeftForRetrieval).append(" more days left to retrieve the book.\n");
        }

        message.append("We are looking forward to your visit.\nSee you soon!");

        // Placeholder for actual mail logic
        EchoServer.outputInOutputStreamAndLog(message.toString());
    }
}
