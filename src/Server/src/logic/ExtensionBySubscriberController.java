package logic;

import java.sql.Connection;
import java.sql.SQLException;

import common.ConnectToDb;
import server.EchoServer;

public class ExtensionBySubscriberController {
	//delete the text inside extensions_by_subscribers in librarian table
	public void cleanEveryDayExetnsionsInLibrarian() {
	    try (Connection conn = ConnectToDb.getConnection()) { 
	        String message = ConnectToDb.cleanExtensionsBySubscribersInLibrarian(conn); 
	        System.out.println(message); // Log the success or error message to the console
	    } catch (SQLException e) {
	        System.err.println("Error in cleaning daily extensions in librarian table: " + e.getMessage());
	    }
	}

		 
}
