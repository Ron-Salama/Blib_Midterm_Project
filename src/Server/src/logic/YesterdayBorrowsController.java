package logic;

import java.sql.SQLException;

import common.ConnectToDb;
import server.EchoServer;

public class YesterdayBorrowsController {
	public void yesterdayborrowcounter() throws SQLException {
		int borrowBooksCounter = ConnectToDb.FetchYesterdayBorrows(EchoServer.taskSchedulerConnection);
		
		ConnectToDb.updateAmountOfBorrowedBooksYesterday(EchoServer.taskSchedulerConnection, borrowBooksCounter);
	}
}
