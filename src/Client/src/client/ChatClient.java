package client;

import ocsf.client.*;
import client.*;
import common.ChatIF;
import logic.Book;
import logic.Subscriber;
import logic.Librarian;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
	
  // Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  public static Subscriber s1 = new Subscriber(0, 0, null, null, null);
  public static Librarian l1 = new Librarian(0, null);
  public static List<Book> bookList = new ArrayList<>(); // List to hold books
  
  public static boolean awaitResponse = false;

  // Constructors ****************************************************
  
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
    super(host, port); // Call the superclass constructor
    this.clientUI = clientUI;
    //openConnection();
  }

  // Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    System.out.println("--> handleMessageFromServer");
    System.out.println(msg);
    
 // Handle the message based on its type
    if (((String) msg).startsWith("subscriber_id:")) {
        // Process subscriber data
    	l1.setLibrarian_id(-1);
        processSubscriberData((String) msg);
    } else if (((String) msg).startsWith("librarian_id:")) {
        // Process librarian data
    	s1.setSubscriber_id(-1);
        processLibrarianData((String) msg);
    } else if (msg.equals("Subscriber ID does not exist.")) {
        // Handle case where subscriber doesn't exist
    	l1.setLibrarian_id(-1);
        s1.setSubscriber_id(-1);
    }else if (msg.equals("Librarian ID does not exist.")) {
            // Handle case where subscriber doesn't exist
    	l1.setLibrarian_id(-1);
        s1.setSubscriber_id(-1);
    } else if (((String) msg).startsWith("Could not connect to the server.")) {
        ClientUI.isIPValid = false; // Turn on the flag for the IP controller.
    } else if (((String) msg).startsWith("Client connected to IP: ")) {
        ClientUI.isIPValid = true; // Turn off the flag when the IP is correct.
    }else if (((String) msg).startsWith("returnedBookData:")) {
        String data = ((String) msg).substring("returnedBookData:".length()); // Remove the indicator
        
        if (data.equals("NoBooksFound")) {
            System.out.println("No books found in the database.");
        } else if (data.startsWith("Error")) {
            System.out.println("Error from server: " + data);
        } else {
            // Split the data into individual book strings
            String[] bookStrings = data.split(";");
            
            // Parse each string into a Book object
            bookList.clear(); // Clear the current list before adding new books
            for (String bookData : bookStrings) {
                String[] fields = bookData.split(","); // Split fields by comma

                // Create a Book object from the fields
                int id = Integer.parseInt(fields[0]);
                String name = fields[1];
                String subject = fields[2];
                String description = fields[3];

                int availableCopies = Integer.parseInt(fields[4]);
                String location = fields[5];

                bookList.add(new Book(id, name, description, subject, availableCopies, location));
                
            }

        }
    } else {
        System.out.println("Unknown response from server: " + msg);
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
  private void processSubscriberData(String msg) {
      try {
          String[] parts = msg.split(",");
          String subscriberId = extractValue(parts[0]);
          String subscriberName = extractValue(parts[1]);
          String subscriptionHistory = extractValue(parts[2]);
          String phone = extractValue(parts[3]);
          String email = extractValue(parts[4]);

          // Set values to the Subscriber object
          s1.setSubscriber_id(Integer.parseInt(subscriberId));
          s1.setSubscriber_name(subscriberName);
          s1.setDetailed_subscription_history(Integer.parseInt(subscriptionHistory));
          s1.setSubscriber_phone_number(phone);
          s1.setSubscriber_email(email);
      } catch (Exception e) {
          System.out.println("Error parsing subscriber data: " + e.getMessage());
          s1.setSubscriber_id(-1); // Mark as not found
      }
  }

  private void processLibrarianData(String msg) {
      try {
          String[] parts = msg.split(",");
          String librarianId = extractValue(parts[0]);
          String librarianName = extractValue(parts[1]);

          // Set values to the Librarian object
          l1.setLibrarian_id(Integer.parseInt(librarianId));
          l1.setLibrarian_name(librarianName);
      } catch (Exception e) {
          System.out.println("Error parsing librarian data: " + e.getMessage());
          l1.setLibrarian_id(-1); // Mark as not found
      }
  }

  private String extractValue(String part) {
      return part.split(":")[1].trim();
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
