package client;
import javafx.application.Application;

import javafx.stage.Stage;
import logic.Subscriber;


import java.util.Vector;
import gui.LibraryFrameController;
import gui.SubscriberFormController;
import client.ClientController;

public class ClientUI extends Application {
	public static ClientController chat; //only one instance

	public static void main( String args[] ) throws Exception
	   { 
		    launch(args);  
	   } // end main
	 
	@Override
	public void start(Stage primaryStage) throws Exception {
		 chat= new ClientController("10.244.2.9", 5555);
		// TODO Auto-generated method stub
						  		
		 LibraryFrameController aFrame = new LibraryFrameController();
		 
		aFrame.start(primaryStage);
	}
	
	
}
