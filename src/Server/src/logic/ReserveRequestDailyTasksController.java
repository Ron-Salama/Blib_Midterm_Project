package logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.ConnectToDb;
import gui.baseController.BaseController;
import ocsf.server.ConnectionToClient;
import server.EchoServer;

public class ReserveRequestDailyTasksController extends BaseController {
	ServerTimeDiffController clock = EchoServer.clock;
	
	
	public void reserveRequestsDailyActivity() {

		deleteOldRequests();
		sendMailToSubscriberThatNeedsToRetrieveBookFromTheLibrary();
	}
	
	public void deleteOldRequests() {
	    //Fetch reserved books data
	    List<String> reservedBooksData = ConnectToDb.fetchAllReservedBooks(EchoServer.taskSchedulerConnection);

	    if (reservedBooksData == null || reservedBooksData.isEmpty()) {
	        System.out.println("No reserved books found.");
	        return;
	    }

	    // Store the reservation data in a map (reserve_id -> time_left_to_retrieve)
	    Map<Integer, String> reserveMap = new HashMap<>();
	    for (String reservedBook : reservedBooksData) {
	        // The string format is: reserve_id, subscriber_id, name, reserve_time, time_left_to_retrieve, ISBN
	        String[] fields = reservedBook.split(",");
	        int reserveId = Integer.parseInt(fields[0]);
	        String timeLeftToRetrieve = fields[4];
	        reserveMap.put(reserveId, timeLeftToRetrieve);
	    }

	    
	    // Identify the reservations that need deletion (time difference <= 0 days)
	    List<Integer> reserveIdsToDelete = new ArrayList<>();
	    for (Map.Entry<Integer, String> entry : reserveMap.entrySet()) {
	        int reserveId = entry.getKey();
	        String timeLeftToRetrieve = entry.getValue();

	        if (!timeLeftToRetrieve.equals("Book is not available yet")) {
	        	// Calculate the time difference using the server's clock
	        	long daysDifference = EchoServer.clock.timeDateDifferenceBetweenTwoDates(EchoServer.clock.timeNow(), timeLeftToRetrieve);
	        	
	        	if (daysDifference <= 0) {
	        		// If the time difference is <= 0 days, mark for deletion
	        		reserveIdsToDelete.add(reserveId);
	        	}	        	
	        }
	    }
	    // Delete the reservations
	    if (!reserveIdsToDelete.isEmpty()) {
	        try (Connection conn = EchoServer.taskSchedulerConnection) {
	            for (Integer reserveId : reserveIdsToDelete) {
	                deleteReservation(conn, reserveId);
	            }
	            System.out.println("Deleted " + reserveIdsToDelete.size() + " old reservation(s).");
	        } catch (SQLException e) {
	            System.err.println("Error deleting reservations: " + e.getMessage());
	        }
	    } else {
	        System.out.println("No reservations need deletion.");
	    }
	}


	private void deleteReservation(Connection conn, int reserveId) {
	    try {
	        // Step 1: Fetch the ISBN of the book being deleted
	        String fetchISBNQuery = "SELECT ISBN FROM reserved_books WHERE reserve_id = ?";
	        String isbn = null;

	        try (PreparedStatement fetchPstmt = conn.prepareStatement(fetchISBNQuery)) {
	            fetchPstmt.setInt(1, reserveId);
	            try (ResultSet rs = fetchPstmt.executeQuery()) {
	                if (rs.next()) {
	                    isbn = rs.getString("ISBN");
	                }
	            }
	        }

	        // If ISBN is null, return because no matching reservation was found
	        if (isbn == null) {
	            System.err.println("Reservation with ID " + reserveId + " not found. Skipping deletion.");
	            return;
	        }

	        // Step 2: Delete the reservation
	        String deleteQuery = "DELETE FROM reserved_books WHERE reserve_id = ?";
	        try (PreparedStatement deletePstmt = conn.prepareStatement(deleteQuery)) {
	            deletePstmt.setInt(1, reserveId);
	            deletePstmt.executeUpdate();
	            System.out.println("Reservation with ID " + reserveId + " deleted.");
	        }

	        // Step 3: Decrement the reservedCopiesNum for the corresponding ISBN
	        String updateCopiesQuery = "UPDATE books SET reservedCopiesNum = reservedCopiesNum - 1 WHERE ISBN = ?";
	        try (PreparedStatement updatePstmt = conn.prepareStatement(updateCopiesQuery)) {
	            updatePstmt.setString(1, isbn);
	            updatePstmt.executeUpdate();
	            System.out.println("Decremented reservedCopiesNum for book with ISBN " + isbn);
	        }

	    } catch (SQLException e) {
	        System.err.println("Error processing reservation deletion for ID " + reserveId + ": " + e.getMessage());
	    }
	}
	
	public void sendMailToSubscriberThatNeedsToRetrieveBookFromTheLibrary() {
	    // 1. Fetch all reservation requests with a status indicating they need to retrieve their books
	    List<String> reservationRequests = ConnectToDb.fetchAllReservedBooksWhereBookIsAvailable(EchoServer.taskSchedulerConnection);

	    if (reservationRequests == null || reservationRequests.isEmpty()) {
	        System.out.println("No reservations require retrieval notification.");
	        return;
	    }

	    // Map to store subscriberID -> List of reservation data
	    Map<Integer, List<String>> subscriberReservationsMap = new HashMap<>();

	    for (String request : reservationRequests) {
	        // Updated reservation data format: reserve_id, subscriber_id, name (book name), reserve_time, time_left_for_retrieval, ISBN
	        String[] fields = request.split(",");
	        int subscriberId = Integer.parseInt(fields[1]); // Extract subscriber ID

	        // Ensure the map has a list for this subscriber ID
	        subscriberReservationsMap.putIfAbsent(subscriberId, new ArrayList<>());

	        // Add the request to the subscriber's list
	        subscriberReservationsMap.get(subscriberId).add(request);
	    }

	    // Iterate over each subscriber and their reservations
	    for (Map.Entry<Integer, List<String>> entry : subscriberReservationsMap.entrySet()) {
	        int subscriberId = entry.getKey();
	        List<String> requests = entry.getValue();

	        // Fetch subscriber data using subscriberId
	        String subscriberData = ConnectToDb.fetchSubscriberData(EchoServer.taskSchedulerConnection, String.valueOf(subscriberId));

	        // Parse the subscriber name from the returned string
	        String subscriberName = parseSubscriberName(subscriberData);

	        if (subscriberName.equals("No subscriber found")) {
	            System.out.println("No subscriber found for ID: " + subscriberId);
	            continue;
	        }

	        // Group reservations by book name
	        Map<String, List<Integer>> bookReservationsMap = new HashMap<>();

	        for (String request : requests) {
	            String[] fields = request.split(",");
	            String bookName = fields[2]; // Book name
	            String timeLeftToRetrieve = fields[4];

	            // Calculate the time difference using the server's clock
	            int daysLeftForRetrieval = EchoServer.clock.timeDateDifferenceBetweenTwoDates(EchoServer.clock.timeNow(), timeLeftToRetrieve);

	            // Group by book name
	            bookReservationsMap.putIfAbsent(bookName, new ArrayList<>());
	            bookReservationsMap.get(bookName).add(daysLeftForRetrieval);
	        }

	        // Construct the consolidated email message
	        StringBuilder message = new StringBuilder();
	        message.append("Dear ").append(subscriberName).append(",\n\n");
	        message.append("You have the following reservations ready for retrieval:\n");

	        for (Map.Entry<String, List<Integer>> bookEntry : bookReservationsMap.entrySet()) {
	            String bookName = bookEntry.getKey();
	            List<Integer> daysLeftList = bookEntry.getValue();

	            message.append("- ").append(bookName).append(": ");

	            // Append days left details for each reservation
	            for (int i = 0; i < daysLeftList.size(); i++) {
	                message.append(daysLeftList.get(i)).append(" day(s) left");
	                if (i < daysLeftList.size() - 1) {
	                    message.append(", ");
	                }
	            }

	            message.append("\n");
	        }

	        message.append("\nPlease retrieve your books before the specified time to avoid cancellation.\n");

	        // Send the email using the existing method (adapted for the full message)
	        EchoServer.outputInOutputStreamAndLog(message.toString());
	    }
	}


	
	
	
	private static String parseSubscriberName(String subscriberData) {
	    // Check if there's an error or no subscriber was found
	    if (subscriberData.startsWith("No subscriber found") || subscriberData.startsWith("Error")) {
	        return "No subscriber found";
	    }

	    // Extract the subscriber_name field from the data]
	    String[] fields = subscriberData.split(", ");
	    for (String field : fields) {
	        if (field.startsWith("subscriber_name:")) {
	            return field.split(":")[1]; // Return the name part
	        }
	    }

	    return "No subscriber found"; // Fallback in case the name field is missing
	}
}
