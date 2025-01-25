package logic;

import common.ConnectToDb;
import server.EchoServer;

/**
 * Controller class responsible for managing extensions by subscribers in the librarian system.
 * <p>This class interacts with the database to clean the daily extensions data in the librarian table.</p>
 */
public class ExtensionBySubscriberController {
	/**
     * Deletes the text inside the `extensions_by_subscribers` field in the librarian table of the database.
     * <p>This method calls the `ConnectToDb.cleanExtensionsBySubscribersInLibrarian()` method to remove
     * the extension data, and then logs the success or error message to the console using the 
     * `EchoServer.outputInOutputStreamAndLog()` method.</p>
     */
	public void cleanEveryDayExetnsionsInLibrarian() {
	        String message = ConnectToDb.cleanExtensionsBySubscribersInLibrarian(EchoServer.taskSchedulerConnection); 
	        EchoServer.outputInOutputStreamAndLog(message); // Log the success or error message to the console
	}
}
