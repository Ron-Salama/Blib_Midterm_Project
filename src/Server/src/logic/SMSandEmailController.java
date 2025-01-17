/**
 * The SMSandEmailController class is responsible for managing SMS and email notifications
 * for library subscribers. It identifies subscribers with books due for return the next day
 * and sends them a single notification listing all such books.
 */
package logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.cj.result.LocalTimeValueFactory;

import common.ConnectToDb;
import gui.baseController.BaseController;
import server.EchoServer;

public class SMSandEmailController extends BaseController {
    /**
     * An instance of ServerTimeDiffController to calculate the time left for book returns.
     */
    ServerTimeDiffController clock = EchoServer.clock; // Use the clock from echo server instead of creating another instance.

    /**
     * Identifies subscribers who need to return books the next day and sends them a single SMS
     * and email notification containing all such books.
     *
     * <p>This method fetches borrowed books from the database, groups them by subscriber ID,
     * and sends notifications for books due the next day.</p>
     */
    public void smsAndEmailControllerDailyActivities() {
        // Get the list of borrowed books in the library.
        List<String> borrowedBooksList = ConnectToDb.fetchBorrowedBooksForTaskScheduler(EchoServer.taskSchedulerConnection);

        // Map to group books by subscriber ID.
        Map<String, List<String>> subscriberBooksMap = new HashMap<>();

        for (String borrowedBook : borrowedBooksList) {
            String[] bookData = parseBorrowedBook(borrowedBook);
            String subscriberID = bookData[1]; // Get subscriber ID for later use.
            String bookName = bookData[2]; // Grab the book's name.
            String returnDate = bookData[5];

            // Find books that need to be returned by tomorrow.
            if (clock.howMuchTimeLeftToReturnABook(returnDate) == 1) {
                // Add the book to the subscriber's list in the map.
                subscriberBooksMap
                    .computeIfAbsent(subscriberID, k -> new ArrayList<>())
                    .add(bookName);
            }
        }

        // Send a message for each subscriber.
        for (Map.Entry<String, List<String>> entry : subscriberBooksMap.entrySet()) {
            String subscriberID = entry.getKey();
            List<String> booksToReturn = entry.getValue();

            // Get the subscriber's information.
            String[] subscriberInformation = ConnectToDb.fetchSubscriberData(EchoServer.taskSchedulerConnection, subscriberID).split(", ");
            String subscriberName = subscriberInformation[1].split(":")[1]; // Get name.
            String subscriberPhoneNumebr = subscriberInformation[3];
            String subscriberEmail = subscriberInformation[4];

            // Send a single message containing all books.
            sendSMSandEmail(subscriberName, booksToReturn, subscriberPhoneNumebr, subscriberEmail);
        }
    }

    /**
     * Sends an SMS and email notification to a subscriber.
     *
     * @param name        The name of the subscriber.
     * @param bookNames   A list of book names that need to be returned.
     * @param phoneNumber The subscriber's phone number.
     * @param email       The subscriber's email address.
     */
    public void sendSMSandEmail(String name, List<String> bookNames, String phoneNumber, String email) {
        // Build the message containing all books.
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(name).append(",\n");
        message.append("You need to return the following books tomorrow:\n");
        for (String bookName : bookNames) {
            message.append("- ").append(bookName).append("\n");
        }
        message.append("We are looking forward to your visit.\nSee you soon!");

        // Apply email and SMS sending logic when deployed.
        System.out.println(message.toString());
    }
    
    
    /**
     * Sends an email notification to a subscriber when a reserved book is back in stock.
     *
     * @param subscriberName     The name of the subscriber.
     * @param bookName           The name of the book that is back in stock.
     * @param email              The subscriber's email address.
     * @param reservationVoidDate The date until the reservation is valid.
     */
    public static void sendEmailWhenAReservedBookIsBackInStock(String subscriberName, String bookName, String email, LocalDateTime reservationVoidDate) {
        // Build the message containing all books.
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(subscriberName).append(",\n");
        message.append("The book: ").append(bookName).append(" you ordered is now back in stock!\n"); 
        message.append("You have until: ").append(reservationVoidDate).append(" to borrow the book.");
        message.append("We are looking forward to your visit.\nSee you soon!");

        // Apply mail logic when deployed.
        System.out.println(message.toString());
    }
}
