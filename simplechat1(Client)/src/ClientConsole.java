// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String host, int port) 
  {
    try 
    {
      client= new ChatClient(host, port, this);
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() {
	    try {
	        BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
	        String message;
	        System.out.println("What would you like to do: \n 1.Show Subscribers DB \n 2.Edit Subscribers DB \n 3.Exit");

	        while (true) {
	        	/*
	            message = fromConsole.readLine();
	            
	            if (message.equals("RunLibrarySystem")) {
	            */
	                
	                message = fromConsole.readLine();

	                if (message.equals("1")) {
	                    client.handleMessageFromClientUI("Show Subscribers DB");
	                    Thread.sleep(5000);
	                    System.out.println("\nWhat would you like to do: \n 1.Show Subscribers DB \n 2.Edit Subscribers DB \n 3.Exit");

	                } else if (message.equals("2")) {
	                    client.handleMessageFromClientUI("Edit Subscribers DB");

	                    // Prompt for Subscriber ID
	                    System.out.println("Type Subscriber's ID:");
	                    String subscriberId = fromConsole.readLine();  // Get ID input

	                    // Prompt for Subscriber Phone
	                    System.out.println("Type Subscriber's Phone:");
	                    String phone = fromConsole.readLine();  // Get Phone input

	                    // Prompt for Subscriber Email
	                    System.out.println("Type Subscriber's Email:");
	                    String email = fromConsole.readLine();  // Get Email input

	                    // Construct the update message in the format: Update:ID,Phone,Email
	                    String updateMessage = "Update:" + subscriberId + "," + phone + "," + email;

	                    // Send the update message to the client
	                    client.handleMessageFromClientUI(updateMessage);
	                    Thread.sleep(2000);
	                    System.out.println("\nWhat would you like to do: \n 1.Show Subscribers DB \n 2.Edit Subscribers DB \n 3.Exit");

	                } else if (message.equals("3")) {
	                	System.out.println("\nExiting... Thank You For Using Library System");
	                	System.exit(0);
	                	break;
	                }else {
	                	System.out.println("\nUnknown Commend!");
	                	System.out.println("\nWhat would you like to do: \n 1.Show Subscribers DB \n 2.Edit Subscribers DB \n 3.Exit");

	                }
	        }
	    } catch (Exception ex) {
	        System.out.println("Unexpected error while reading from console: " + ex.getMessage());
	    }
	}



  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
    String host = "";
    int port = 0;  //The port number

    try
    {
      host = args[0];
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      host = "localhost";
    }
    ClientConsole chat= new ClientConsole(host, DEFAULT_PORT);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
