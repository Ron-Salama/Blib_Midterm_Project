package logic;

import common.ConnectToDb;
import server.EchoServer;

public class ExtensionBySubscriberController {
	//delete the text inside extensions_by_subscribers in librarian table
	public void cleanEveryDayExetnsionsInLibrarian() {
	        String message = ConnectToDb.cleanExtensionsBySubscribersInLibrarian(EchoServer.taskSchedulerConnection); 
	        EchoServer.outputInOutputStreamAndLog(message); // Log the success or error message to the console
	}
}
