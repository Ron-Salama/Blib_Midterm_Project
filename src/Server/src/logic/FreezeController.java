package logic;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import common.ConnectToDb;
import server.EchoServer;


 //TODO: Method to freeze, method to unfreeze subscribers. 
public class FreezeController {
	ServerTimeDiffController clock = EchoServer.clock;
	
	public void freeze() throws SQLException {
		List<String> borrowedBooksList = ConnectToDb.fetchBorrowedBooksForTaskScheduler(EchoServer.taskSchedulerConnection);
		
		for (String borrowedBook : borrowedBooksList) {
			String[] bookData = parseBorrowedBook(borrowedBook);
			LocalDateTime borrowDate = clock.convertStringToLocalDateTime(bookData[4]);
			LocalDateTime returnDate = clock.convertStringToLocalDateTime(bookData[5]); 
			
			if (clock.hasWeekPassed(borrowDate, returnDate)) {
				int subscriberID = Integer.valueOf(bookData[1]); // Grab subscriber ID and convert it to integer.
				String bookName = bookData[2];
				
				// log on both CMD and Log that the user has been frozen.
				String subscriber = ConnectToDb.fetchSubscriberData(EchoServer.taskSchedulerConnection, bookData[1]); 
				EchoServer.outputInOutputStreamAndLog(clock.timeNow() + ": " + subscriber.toString() + " has been frozen because they were late at returning the book: " + bookName);
				
				ConnectToDb.freezeSubscriber(EchoServer.taskSchedulerConnection, subscriberID); // Freeze user given their id.
			}
		}
	}
	
	public void unfreeze() throws SQLException{
		List<String> subsribersInformation = ConnectToDb.fetchAllData(EchoServer.taskSchedulerConnection);
		System.out.println(subscri);
	}
	
	
	public void freezeControllerDailyActivities() throws SQLException {
		unfreeze(); // Unfreeze first in case the subscriber has to be frozen on the same day he's being unfreezed.
		freeze();
	}

	private String[] parseBorrowedBook(String borrowedBook) {
		String[] borrowedBookData = borrowedBook.split(",");
		return borrowedBookData;
	}
}
