package logic;

import java.sql.SQLException;

import common.ConnectToDb;
import server.EchoServer;

/**
 * Controller class responsible for handling the generation of reports related to library activities.
 * <p>This class interacts with the database to fetch data related to book borrows, late books, and frozen subscribers,
 * and updates the corresponding records in the database.</p>
 */
public class HandleReportController {
	/**
     * Generates a report for the previous day's library activities. This includes:
     * <ul>
     *     <li>Counting the number of borrowed books from the previous day</li>
     *     <li>Counting the number of late books from the previous day</li>
     *     <li>Counting the number of frozen and non-frozen subscribers</li>
     * </ul>
     * The report is then saved by updating the corresponding values in the database.
     * 
     * @throws SQLException if there is an error while interacting with the database
     */
	public void yesterdayborrowcounter() throws SQLException {
		// Insert current date into the database for tracking purposes
		ConnectToDb.insertCurrentDate(EchoServer.taskSchedulerConnection);

		// Fetch the number of borrowed books yesterday and update the database
		int borrowBooksCounter = ConnectToDb.FetchYesterdayBorrows(EchoServer.taskSchedulerConnection);
		ConnectToDb.updateAmountOfBorrowedBooksYesterday(EchoServer.taskSchedulerConnection, borrowBooksCounter);
		
		// Fetch the number of late books yesterday and update the database
		int latebooks = ConnectToDb.FetchYesterdaylates(EchoServer.taskSchedulerConnection);
		ConnectToDb.updateAmountOflateBooksYesterday(EchoServer.taskSchedulerConnection, latebooks);
		
		// Fetch the number of frozen subscribers and update the database
		int amountfrozen= ConnectToDb.FetchAmountFrozen(EchoServer.taskSchedulerConnection);
		ConnectToDb.amountfrozen(EchoServer.taskSchedulerConnection,amountfrozen);
		
		// Fetch the number of non-frozen subscribers and update the database
		int notfrozen=ConnectToDb.FetchAmountNotFrozen(EchoServer.taskSchedulerConnection);
		ConnectToDb.amountNotfrozen(EchoServer.taskSchedulerConnection,notfrozen);
	}
}
