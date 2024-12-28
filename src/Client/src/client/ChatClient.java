// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import client.*;
import common.ChatIF;
import logic.Subscriber;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  public static Subscriber s1 = new Subscriber(0,0,null,null,null);
  public static boolean awaitResponse = false;

  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
	 
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    //openConnection();
  }

  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
	  System.out.println("--> handleMessageFromServer");
	  System.out.println(msg);
	  if (((String) msg).startsWith("subscriber_id:")) {
          // Extract subscriber details from the response message
          try {
              String[] parts = ((String) msg).split(",");
              String subscriberId = parts[0].split(":")[1].trim();
              String subscriberName = parts[1].split(":")[1].trim();
              String subscriptionHistory = parts[2].split(":")[1].trim();
              String phone = parts[3].split(":")[1].trim();
              String email = parts[4].split(":")[1].trim();
              
              // Set the values to the Subscriber object
              s1.setSubscriber_id(Integer.parseInt(subscriberId));
              s1.setSubscriber_name(subscriberName);
              s1.setDetailed_subscription_history(Integer.parseInt(subscriptionHistory));
              s1.setSubscriber_phone_number(phone);
              s1.setSubscriber_email(email);
          } catch (Exception e) {
              System.out.println("Error parsing subscriber data: " + e.getMessage());
              s1.setSubscriber_id(-1); // Mark as not found
          }
      } else if (msg.equals("Subscriber ID does not exist.")) {
          // Mark the subscriber as not found
          s1.setSubscriber_id(-1);
      }
  }
  public void handleMessageFromClientUI(String message) {
	    awaitResponse = true;
	    try {
	        openConnection(); // Open connection for sending messages
	        sendToServer(message);

	        // Use a thread to wait for the response from the server
	        new Thread(() -> {
	            while (awaitResponse) {
	                try {
	                    Thread.sleep(100);  // Delay in checking response
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }

	            // Once the response is received, update the UI
	            Platform.runLater(() -> {
	                try {
	                    closeConnection();  // Close the connection after receiving the response
	                    awaitResponse = false;
	                    // Update the UI here, e.g., show the subscriber info
	                    // You can now update your GUI elements safely here
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    clientUI.display("Error closing connection: " + e.getMessage());
	                }
	            });
	        }).start();

	    } catch (IOException e) {
	        e.printStackTrace();
	        clientUI.display("Could not send message to server: Terminating client." + e);
	        quit();
	    }
	}

  
  
  
  
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
