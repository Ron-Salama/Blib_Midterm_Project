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
		updateReservationRequestsThatHaveBooksInStock();
		sendMailToSubscriberThatNeedsToRetrieveBookFromTheLibrary();
	}
	
	public void deleteOldRequests() {
	    //Fetch reserved books data
	    List<String> reservedBooksData = ConnectToDb.fetchAllReservedBooks(EchoServer.taskSchedulerConnection);
	    
	    if (reservedBooksData == null || reservedBooksData.isEmpty()) {
	        System.out.println("No reserved books found.");
	        return;
	    }

	    //Store the reservation data in a map (reserve_id -> time_left_to_retrieve)
	    Map<Integer, String> reserveMap = new HashMap<>();
	    for (String reservedBook : reservedBooksData) {
	        //The string format is: reserve_id, subscriber_id, name, reserve_time, time_left_to_retrieve, ISBN
	        String[] fields = reservedBook.split(",");
	        int reserveId = Integer.parseInt(fields[0]);
	        String timeLeftToRetrieve = fields[4]; 
	        reserveMap.put(reserveId, timeLeftToRetrieve);
	    }

	    //Identify the reservations that need deletion (time difference >= 2 days)
	    List<Integer> reserveIdsToDelete = new ArrayList<>();
	    for (Map.Entry<Integer, String> entry : reserveMap.entrySet()) {
	        int reserveId = entry.getKey();
	        String timeLeftToRetrieve = entry.getValue();

	        if (!timeLeftToRetrieve.equals("Book is not available yet")) {
		        //Calculate the time difference using the server's clock
		        long daysDifference = EchoServer.clock.timeDateDifferenceBetweenTwoDates(timeLeftToRetrieve, EchoServer.clock.timeNow());
		        if (daysDifference > 2) {
		        	//If the time difference is >= 2 days, mark for deletion
		        	reserveIdsToDelete.add(reserveId);
		        }
	        }
	    }

	    //Delete the reservations
	    if (!reserveIdsToDelete.isEmpty()) {
	        for (Integer reserveId : reserveIdsToDelete) {
	            deleteReservation(reserveId);
	        }
	        System.out.println("Deleted " + reserveIdsToDelete.size() + " old reservation(s).");
	    } else {
	        System.out.println("No reservations need deletion.");
	    }
	}

	private void deleteReservation(int reserveId) {
	    // Perform the actual delete operation on the database
	    try (Connection conn = EchoServer.taskSchedulerConnection) {
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

	        //Delete the reservation
	        String deleteQuery = "DELETE FROM reserved_books WHERE reserve_id = ?";
	        try (PreparedStatement deletePstmt = conn.prepareStatement(deleteQuery)) {
	            deletePstmt.setInt(1, reserveId);
	            deletePstmt.executeUpdate();
	            System.out.println("Reservation with ID " + reserveId + " deleted.");
	        }

	        //Decrement the reservedCopiesNum for the corresponding ISBN
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

	public void updateReservationRequestsThatHaveBooksInStock() {
		// TODO: finish implementing this method.
		// Step 1: Grab all of the reservations from the database.
		// Step 2: Create a Map(ISBN, numberOfReservation).
		// Step 3: Grab amount of available copies in library.
		// Step 4: Check if availableCopies >= reservationCopies
		// 
		//* make sure to count reservation that have a date on them already.
		// -> count how many reserved copies of that book are already reserved.
		// decrease the amount of reservations by the amount of books that are already reserved.
		//
		// if the new amount is larger than 0: update each reservation that has "Book is not available yet" by today's date + 2 days.
		
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
	        String bookName = fields[2];                   // Extract book name

	        // Ensure the map has a list for this subscriber ID
	        subscriberReservationsMap.putIfAbsent(subscriberId, new ArrayList<>());
	        
	        // Add the request to the subscriber's list
	        subscriberReservationsMap.get(subscriberId).add(request);
	    }

	    
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

	        for (String request : requests) {
	            String[] fields = request.split(",");
	            String bookName = fields[2]; // Book name
	            String timeLeftToRetrieve = fields[4]; 
	            //Calculate the time difference using the server's clock
	            int daysLeftForRetrieval = EchoServer.clock.timeDateDifferenceBetweenTwoDates(EchoServer.clock.timeNow(), timeLeftToRetrieve);
	            // Send email notification
	            SMSandEmailController.sendMailToSubscriberThatNeedToRetrieveBook(subscriberName, bookName, daysLeftForRetrieval);
	        }
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
