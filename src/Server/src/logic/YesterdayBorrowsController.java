package logic;

import common.ConnectToDb;
import server.EchoServer;

public class YesterdayBorrowsController {
	public void yesterdayborrowcounter() {
		int borrowBooksCounter =ConnectToDb.FetchYesterdayBorrows(EchoServer.taskSchedulerConnection);
	}
}
