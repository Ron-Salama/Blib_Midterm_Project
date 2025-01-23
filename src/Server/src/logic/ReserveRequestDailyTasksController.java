package logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
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


 //TODO: imporve log&CMD messages.
public class ReserveRequestDailyTasksController extends BaseController {
	ServerTimeDiffController clock = EchoServer.clock;
	
	
	public void reserveRequestsDailyActivity() {
		System.out.println("THIS IS A TEST TO SEE IF Daily Activity is entered");

		deleteOldRequests();
		updateReservationRequestsThatHaveBooksInStock();
		sendMailToSubscriberThatNeedsToRetrieveBookFromTheLibrary();
	}
	
	/*public void deleteOldRequests() {
		// TODO: pass on the requests
		// 1. Fetch information about the reservation
		// 2. put the information in Map<Integer, String>
		// 3. for each element in the map
		// 3.3 is clock.timeDifference...(String bookInWhichTheBookIsAvailable, clock.timeNOW) >= 2;
		// 4. use array to store IDs of reservation requests that need to be deleted.
		// For each element in the array - delete reservation.
	}*/
	
	public void deleteOldRequests() {
		
		System.out.println("THIS IS A TEST TO SEE IF DELETE REQUESTS IS EVEN CALLED");
	    // Step 1: Fetch reserved books data
	    List<String> reservedBooksData = ConnectToDb.fetchAllReservedBooks(EchoServer.taskSchedulerConnection);
	    
	    if (reservedBooksData == null || reservedBooksData.isEmpty()) {
	        System.out.println("No reserved books found.");
	        return;
	    }

	    // Step 2: Store the reservation data in a map (reserve_id -> time_left_to_retrieve)
	    Map<Integer, String> reserveMap = new HashMap<>();
	    for (String reservedBook : reservedBooksData) {
	        // Assuming the string format is: reserve_id, subscriber_id, name, reserve_time, time_left_to_retrieve, ISBN
	        String[] fields = reservedBook.split(",");
	        int reserveId = Integer.parseInt(fields[0]);
	        String timeLeftToRetrieve = fields[4]; // Assuming time_left_to_retrieve is at index 4
	        reserveMap.put(reserveId, timeLeftToRetrieve);
	    }

	    // Step 3: Identify the reservations that need deletion (time difference >= 2 days)
	    List<Integer> reserveIdsToDelete = new ArrayList<>();
	    for (Map.Entry<Integer, String> entry : reserveMap.entrySet()) {
	        int reserveId = entry.getKey();
	        String timeLeftToRetrieve = entry.getValue();

	        // Step 3.1: Calculate the time difference using the server's clock
	        long daysDifference = EchoServer.clock.timeDateDifferenceBetweenTwoDates(timeLeftToRetrieve, EchoServer.clock.timeNow());
	        if (daysDifference > 2) {
	            // Step 3.2: If the time difference is >= 2 days, mark for deletion
	            reserveIdsToDelete.add(reserveId);
	        }
	    }

	    // Step 4: Delete the reservations
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
	        String deleteQuery = "DELETE FROM reserved_books WHERE reserve_id = ?";
	        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
	            pstmt.setInt(1, reserveId);
	            pstmt.executeUpdate();
	            System.out.println("Reservation with ID " + reserveId + " deleted.");
	        } catch (SQLException e) {
	            System.err.println("Error deleting reservation with ID " + reserveId + ": " + e.getMessage());
	        }
	    } catch (SQLException e) {
	        System.err.println("Error getting connection for deletion: " + e.getMessage());
	    }
	}


	
	
	public void updateReservationRequestsThatHaveBooksInStock() {
		
	}

	
	public void sendMailToSubscriberThatNeedsToRetrieveBookFromTheLibrary() {
		// USE PUBLIC STATIC METHOD FOR SENDING MAIL FRIM SMSANDEMAILCONTROLLER.
		
	}

	public void freeze() throws SQLException {
		List<String> borrowedBooksList = ConnectToDb.fetchBorrowedBooksForTaskScheduler(EchoServer.taskSchedulerConnection);
		
		for (String borrowedBook : borrowedBooksList) {
			String[] bookData = parseBorrowedBook(borrowedBook);
			LocalDateTime borrowDate = clock.convertStringToLocalDateTime(bookData[4]);
			LocalDateTime returnDate = clock.convertStringToLocalDateTime(bookData[5]); 
			
			if (clock.hasWeekPassed(borrowDate, returnDate)) {
				int subscriberID = Integer.valueOf(bookData[1]); // Grab subscriber ID and convert it to integer.
				String bookName = bookData[2];
				
				ConnectToDb.freezeSubscriber(EchoServer.taskSchedulerConnection, subscriberID); // Freeze user given their id.

				// log on both CMD and Log that the user has been frozen.
				String subscriber = ConnectToDb.fetchSubscriberData(EchoServer.taskSchedulerConnection, bookData[1]); 
				EchoServer.outputInOutputStreamAndLog(clock.timeNow() + ": " + subscriber.toString() + " has been frozen because they were late at returning the book: " + bookName);
				
			}
		}
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void unfreeze() throws SQLException{
		List<String> subscribersList = ConnectToDb.fetchAllData(EchoServer.taskSchedulerConnection);
		for (String subscriber : subscribersList) {
			String[] subscriberInformation = subscriber.split(", ");
			String[] status = subscriberInformation[5].split(":");

			if (!status.equals("Not Frozen")) { // subscriber is frozen.
				int subscriberID = Integer.valueOf(subscriberInformation[0].split(":")[1]);  // Get the subscriber's ID.
				String frozenAt = status[2]; // Grab the date in which the subscriber got frozen,
				if (clock.hasMonthPassed(frozenAt)) { // Check if the subscriber is frozen for a month or more. if so, "unfreeze" him.
					
					ConnectToDb.unfreezeSubscriber(EchoServer.taskSchedulerConnection, subscriberID);
					// Log that the subscriber got unfrozen both in CMD and log.
					EchoServer.outputInOutputStreamAndLog(clock.timeNow() + ": " + subscriber.toString() + " unfrozen since a month has passed.");
					
				}
			}
		}
	}
}
