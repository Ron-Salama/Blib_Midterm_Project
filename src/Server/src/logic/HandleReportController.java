package logic;

import java.sql.SQLException;

import common.ConnectToDb;
import server.EchoServer;

public class HandleReportController {
	public void yesterdayborrowcounter() throws SQLException {
		ConnectToDb.insertCurrentDate(EchoServer.taskSchedulerConnection);
		int borrowBooksCounter = ConnectToDb.FetchYesterdayBorrows(EchoServer.taskSchedulerConnection);
		ConnectToDb.updateAmountOfBorrowedBooksYesterday(EchoServer.taskSchedulerConnection, borrowBooksCounter);
		int latebooks = ConnectToDb.FetchYesterdaylates(EchoServer.taskSchedulerConnection);
		ConnectToDb.updateAmountOflateBooksYesterday(EchoServer.taskSchedulerConnection, latebooks);
		int amountfrozen= ConnectToDb.FetchAmountFrozen(EchoServer.taskSchedulerConnection);
		ConnectToDb.amountfrozen(EchoServer.taskSchedulerConnection,amountfrozen);
		int notfrozen=ConnectToDb.FetchAmountNotFrozen(EchoServer.taskSchedulerConnection);
		ConnectToDb.amountNotfrozen(EchoServer.taskSchedulerConnection,notfrozen);
	}
}
