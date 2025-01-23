package client;

import ocsf.client.*;
import client.*;
import common.ChatIF;
import logic.Book;
import logic.BorrowedBook;
import logic.ReservedBook;
import logic.ClientTimeDiffController;
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
	//server sends info from db to client 
  // Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  public static List<BorrowedBook> borrowedBookList = new ArrayList<>(); // List to hold borrowed books
  public static List<ReservedBook> reservedBookList = new ArrayList<>(); // List to hold borrowed books
  public static Subscriber s1 = new Subscriber(0, 0, null, null, null, null);
  public static Librarian l1 = new Librarian(0, null);
  public static List<Book> bookList = new ArrayList<>(); // List to hold books
  public static List<String[][]> br = new ArrayList<>(); 
  public static String[] BorrowedBookInfo;
  public static String[] BorrowedBookInformationForBarcodeScanner;
  public static ArrayList<String> myHistoryInfo = new ArrayList<String>(); 
  public static boolean awaitResponse = false;
  public static boolean alertIndicator = true;
  public static boolean isBookReservedFlag = false;
  public static List<Subscriber> allSubscriberData;
  public static List<String> allSubscriberDataForReport = new ArrayList<>();  // Ensures it's initialized

  public static boolean isIPValid = false;
  
  public static boolean messageReceivedFromServer = false;
  
  public static ClientTimeDiffController clock = new ClientTimeDiffController();

  
  
  public static boolean isIDInDataBase;
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
  @Override
  public void handleMessageFromServer(Object msg) 
  {
	    System.out.println("--> handleMessageFromServer");
	    if (!(msg instanceof String)) {
	        System.out.println("Invalid message type received.");
	        return;
	    }

	    String response = (String) msg;
	    System.out.println(msg);
	    // Dispatch handling based on message prefix
	    if (response.startsWith("Client connected to IP:")) {
	    	isIPValid = true;
	    } else if (response.startsWith("Could not connect to the server.")) {
	 	        isIPValid = false;
	    }else if (response.startsWith("BorrowedBooks:")) {
	        handleBorrowedBooksResponse(response.substring("BorrowedBooks:".length()));
	    } else if (response.startsWith("subscriber_id:")) {
	        handleSubscriberData(response);
	    }
	    else if (response.startsWith("BookReserved:")) {
	        handleBookReservedResponse(response.substring("BookReserved:".length()));
	    }else if (response.startsWith("BorrowedBooks:")) {
            handleBorrowedBooksResponse(response.substring("BorrowedBooks:".length()));
          //***************************************************************************
          //***************************************************************************
          //***************************************************************************
          //***************************************************************************
	    }else if (response.startsWith("ReservedBooks:")) {
            handleReservedBooksResponse(response.substring("ReservedBooks:".length()));
            //***************************************************************************
            //***************************************************************************
            //***************************************************************************
            //***************************************************************************
        }else if (response.startsWith("librarian_id:")) {
	        handleLibrarianData(response);
	    }else if (response.startsWith("Subscriber updated successfully.")) {
	    	handleUpdateSubInfoSuccess();
	    }else if (response.startsWith("Subscriber ID does not exist.")) {
	    	handleUpdateSubInfoFail();
	    } else if (response.equals("Librarian ID does not exist.") ||
	 		   	response.equals("ID does not exist.")) {
	        handleNonexistentID();
	    } else if (response.startsWith("returnedBookData:")) {
	        handleBookData(response.substring("returnedBookData:".length()));
	    } else if (response.startsWith("BookInfo:")){
	    	handleBookInfo(response.substring("BookInfo:".length()));
	    } else if (response.startsWith("FetchedBorrowedBooks:")){
	    	handleFetchedBorrowedBooks(response.substring("FetchedBorrowedBooks:".length()));
	    }else if (response.startsWith("FetchedHistory:")){
	    	processMyHistoryData(response.substring("FetchedHistory:".length()));
	    }else if (response.startsWith("FetchedRegisterRequests:")){
	    	FetchedRegisterRequests(response.substring("FetchedRegisterRequests:".length()));
	    }else if (response.startsWith("RegistrationSucceed:")) {
	    	handleRegisterRequestSuccess();
  		}else if (response.startsWith("RegistrationFailed:")) {
	    	handleRegisterRequestFailed();
	    }else if (response.startsWith("FetchedReturnRequest:")) {
	    	handleReturnRequestSucess(response.substring("FetchedReturnRequest:".length()));
	    }else if (response.startsWith("An error occurred while fetching the return request data:")) {
	    	handleReturnRequestfailure();
	    }else if (response.startsWith("AllSubscriberInformationForReports")) {
	    	handleAllSubscriberInformationForReports(response.substring("AllSubscriberInformationForReports:".length()));
	    }
	    else if (response.startsWith("BorrowedBooksForBarcodeScanner:")){
	    	handleBarcodeFetchBorrowedBookRequest(response.substring("BorrowedBooksForBarcodeScanner:".length()));
	    }else if(response.equals("Client disconnected")) {
	    	System.exit(1);
	    }
	    else if (response.startsWith("AllSubscriberInformation:")) {
	    	handleAllSubscriberInformation(response.substring("AllSubscriberInformation:".length()));
	    }
	    
	    // release the lock so that the client's window can continue on working.
	    messageReceivedFromServer = true;
	}
  
  private void handleBookReservedResponse(String data) {
	    switch (data.trim()) {
	        case "Yes":
	            System.out.println("The book is reserved.");
	            ChatClient.isBookReservedFlag = true;
	            break;
	        case "No":
	            System.out.println("The book is not reserved.");
	            ChatClient.isBookReservedFlag = false;
	            break;
	        default:
	            System.out.println("Error checking book reservation status: " + data);
	            break;
	    }
	}

  
  private void handleReturnRequestfailure() {
	System.out.print("Fetch return request failed");
	
}

private void handleReturnRequestSucess(String data) {
	 br.clear(); // Clear the existing list to avoid appending duplicate data

	    // Split the input data by semicolon (;) to separate each request
	    String[] requests = data.split(";");

	    // Iterate over each request (which is now a string) and split it by comma (,) to get the fields
	    for (String request : requests) {
	        // Split each request into fields by comma
	        String[] RegisterDetails = request.split(",");
	        // Ensure the bookDetails array has the expected length (8 fields)
	        if (RegisterDetails.length == 8) {
	            br.add(new String[][]{RegisterDetails}); // Add the book details array to the br list
	        } else {
	            System.out.println("Invalid book data received: " + String.join(",", RegisterDetails));
	        }
	    }
	}
	

private void handleBorrowedBooksResponse(String data) {
	    if (data.equals("NoBooksFound")) {
	        System.out.println("No borrowed books found.");
	        ChatClient.borrowedBookList.clear();
	    } else if (data.startsWith("Error")) {
	        System.out.println("Error fetching borrowed books: " + data);
	    } else {
	        String[] bookStrings = data.split(";"); // Split rows
	        ChatClient.borrowedBookList.clear();
	        for (String bookData : bookStrings) {
	            String[] fields = bookData.split(","); // Split fields
	            int borrowId = Integer.parseInt(fields[0]);
	            int subscriberId = Integer.parseInt(fields[1]);
	            String name = fields[2];
	            String borrowedTime = fields[3]; // Assuming numeric
	            String returnTime = fields[4];   // Assuming numeric
	            String ISBN = fields[5];
	            int timeLeftToReturn = clock.howMuchTimeLeftToReturnABook(returnTime); // Calculate time left to return
	            
	            ChatClient.borrowedBookList.add(new BorrowedBook(borrowId, subscriberId, name, borrowedTime, returnTime,timeLeftToReturn, ISBN));
	        }
	    }
	}

//***********************************************************************************************
//***********************************************************************************************
//***********************************************************************************************
//***********************************************************************************************
//***********************************************************************************************
//***********************************************************************************************
private void handleReservedBooksResponse(String data) {
    if (data.equals("NoBooksFound")) {
        System.out.println("No Reserved books found.");
        ChatClient.reservedBookList.clear();
    } else if (data.startsWith("Error")) {
        System.out.println("Error fetching Reserved books: " + data);
    } else {
        String[] bookStrings = data.split(";"); // Split rows
        ChatClient.reservedBookList.clear();
        for (String bookData : bookStrings) {
            String[] fields = bookData.split(","); // Split fields
            int reserveId = Integer.parseInt(fields[0]);
            int subscriberId = Integer.parseInt(fields[1]);
            String name = fields[2];
            String reserveTime = fields[3]; // Assuming numeric
            String timeLeftToRetrieve = fields[4];   // Assuming numeric
            String ISBN = fields[5];
            
            ChatClient.reservedBookList.add(new ReservedBook(reserveId, subscriberId, name, reserveTime, timeLeftToRetrieve, ISBN));
        }
    }
}
//***********************************************************************************************
//***********************************************************************************************
//***********************************************************************************************
//***********************************************************************************************
//***********************************************************************************************
//***********************************************************************************************
 
  private void handleBookInfo(String data) {
	    try {
	        if (data.equals("NoBooksFound")) {
	            System.out.println("No books found in the database.");
	            BorrowedBookInfo = null;
	        } else if (data.startsWith("Error")) {
	            System.out.println("Error from server: " + data);
	            BorrowedBookInfo = null;
	        } else {
	            BorrowedBookInfo = data.split(",");

	            if (BorrowedBookInfo.length < 8) {
	                throw new IllegalArgumentException("Incomplete book data received.");
	            }
	          /*System.out.println("Book ID: " + BorrowedBookInfo[0] + "\n" +
	                    "Book Name: " + BorrowedBookInfo[1] + "\n" +
	                    "Subject: " + BorrowedBookInfo[2] + "\n" +
	                    "Description: " + BorrowedBookInfo[3] + "\n" +
	                    "Copies: " + BorrowedBookInfo[4] + "\n" +
	                    "Location on Shelf: "+ BorrowedBookInfo[5] + "\n"+
	                    "Available copies number: "+ BorrowedBookInfo[6]+ "\n"+
	                    "Reserved copies number: "+ BorrowedBookInfo[7]);*/
	          
	        }
	    } catch (Exception e) {
	        System.err.println("Error handling book info: " + e.getMessage());
	        BorrowedBookInfo = null;
	    }
	}
  
	private void handleSubscriberData(String response) {
	    l1.setLibrarian_id(-1); // Reset librarian data
	    processSubscriberData(response);
	}

	private void handleLibrarianData(String response) {
	    s1.setSubscriber_id(-1); // Reset subscriber data
	    processLibrarianData(response);
	}

	private void handleNonexistentID() {
	    l1.setLibrarian_id(-1);
	    s1.setSubscriber_id(-1);
	}
	private void handleUpdateSubInfoSuccess() {
		alertIndicator = true;
	}
	
	private void handleUpdateSubInfoFail() {
		alertIndicator = false;
	}
	
	private void FetchedRegisterRequests(String data) {
	    br.clear(); // Clear the existing list to avoid appending duplicate data

	    // Split the input data by semicolon (;) to separate each request
	    String[] requests = data.split(";");

	    // Iterate over each request (which is now a string) and split it by comma (,) to get the fields
	    for (String request : requests) {
	        // Split each request into fields by comma
	        String[] RegisterDetails = request.split(",");

	        // Ensure the bookDetails array has the expected length (8 fields)
	        if (RegisterDetails.length == 8) {
	            br.add(new String[][]{RegisterDetails}); // Add the book details array to the br list
	        } else {
	            System.out.println("Invalid book data received: " + String.join(",", RegisterDetails));
	        }
	    }
	}
	
	private void handleFetchedBorrowedBooks(String data) {
	    br.clear(); // Clear the existing list to avoid appending duplicate data

	    // Split the input data by semicolon (;) to separate each request
	    String[] requests = data.split(";");

	    // Iterate over each request (which is now a string) and split it by comma (,) to get the fields
	    for (String request : requests) {
	        // Split each request into fields by comma
	        String[] bookDetails = request.split(",");

	        // Ensure the bookDetails array has the expected length (8 fields)
	        if (bookDetails.length == 8) {
	            br.add(new String[][]{bookDetails}); // Add the book details array to the br list
	        } else {
	            System.out.println("Invalid book data received: " + String.join(",", bookDetails));
	        }
	    }
	}
	
	private void processMyHistoryData(String msg) {
	    try {
	        // Split the message by ';' and store it in a String array
	        String[] historyData = msg.split(";");
	        
	        for (String activity : historyData) {
	        	myHistoryInfo.add(activity);
	        }

	        for (String fck : myHistoryInfo) {
	        	System.out.println(fck);
	        }
	        
	        
	    } catch (Exception e) {
	        System.out.println("Error parsing My History data: " + e.getMessage());
	    }
	}

	private void handleBookData(String data) {
	    if (data.equals("NoBooksFound")) {
	        System.out.println("No books found in the database.");
	    } else if (data.startsWith("Error")) {
	        System.out.println("Error from server: " + data);
	    } else {
	        parseBookData(data);
	    }
	}
/* unneccesery
	private void handleUnknownResponse(String response) {
	    System.out.println("" + response);
	}
*/
	private void parseBookData(String data) {
	    try {
	        String[] bookStrings = data.split(";");
	        bookList.clear(); // Clear existing book data
	        for (String bookData : bookStrings) {
	            String[] fields = bookData.split(",");
	            int id = Integer.parseInt(fields[0]);
	            String name = fields[1];
	            String subject = fields[2];
	            String description = fields[3];
	            int copies = Integer.parseInt(fields[4]);
	            String location = fields[5];
	            int availableCopies = Integer.parseInt(fields[6]);
	            int reservedCopies = Integer.parseInt(fields[7]); 
	            bookList.add(new Book(id, name, description, subject, copies, location, availableCopies, reservedCopies));
	        }
	    } catch (Exception e) {
	        System.out.println("Error parsing book data: " + e.getMessage());
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
          String status = parts[5]; // put in s1 the string "Frozen at:dd-MM-yyyy", after that process the information accordingly to your needs in another class.
          
          // Set values to the Subscriber object
          s1.setSubscriber_id(Integer.parseInt(subscriberId));
          s1.setSubscriber_name(subscriberName);
          s1.setDetailed_subscription_history(Integer.parseInt(subscriptionHistory));
          s1.setSubscriber_phone_number(phone);
          s1.setSubscriber_email(email);
          s1.setStatus(status);
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
	
	private void handleAllSubscriberInformation(String msg) {
		List<Subscriber> subscriberList = new ArrayList<Subscriber>();
		// Split the msg so each element contains all of the information for a subscriber one at a time.
		String[] subscribersInformation = msg.split(";");
		
		for (String subscriberData : subscribersInformation) {
			String[] subscriberInformation = subscriberData.split(","); // Split the information of the subscriber themselves.
			
			subscriberList.add(new Subscriber(Integer.valueOf(subscriberInformation[0]),
					Integer.valueOf(subscriberInformation[1]),
					subscriberInformation[2],
					subscriberInformation[3],
					subscriberInformation[4],
					subscriberInformation[5]));
		}
		
		allSubscriberData = subscriberList;
	}
	
	private void handleAllSubscriberInformationForReports(String msg) {
	    // Check and remove leading '[' if present
	    if (msg.startsWith("[")) {
	        msg = msg.substring(1);  // Removes the first character
	    }
	    if (msg.endsWith("]")) {
	        msg = msg.substring(0, msg.length() - 1);  // Removes the last character
	    }

	    // List of strings to hold the subscriber data in the desired format
	    List<String> subscriberList = new ArrayList<String>();
	    
	    // Split the msg so each element contains all of the information for a subscriber one at a time.
	    String[] subscribersInformation = msg.split(";");
	    System.out.println("Subscribers information count: " + subscribersInformation.length);  // Debugging line

	    for (String subscriberData : subscribersInformation) {
	        System.out.println("Processing subscriber data: " + subscriberData);  // Debugging line
	        String[] subscriberInformation = subscriberData.split(","); // Split the information of the subscriber
	        
	        if (subscriberInformation.length < 6) {  // Ensure that there's enough data
	            System.out.println("Invalid data: " + subscriberData);  // Debugging line
	            continue;  // Skip invalid data
	        }
	        
	        // Convert subscriber information into a formatted string
	        try {
	            String formattedSubscriberData = subscriberInformation[0] + "," +
	                subscriberInformation[1] + "," +
	                subscriberInformation[2] + "," +
	                subscriberInformation[3] + "," +
	                subscriberInformation[4] + "," +
	                subscriberInformation[5];
	            
	            // Add the formatted string to the list
	            subscriberList.add(formattedSubscriberData);
	        } catch (Exception e) {
	            System.out.println("Error processing subscriber data: " + subscriberData);  // Debugging line
	            e.printStackTrace();  // Print the stack trace for debugging
	        }
	    }

	    // At this point, we should have the subscriberList populated
	    if (subscriberList.isEmpty()) {
	        System.out.println("No valid subscribers found.");  // Debugging line
	    } else {
	        System.out.println("Successfully added " + subscriberList.size() + " subscribers.");  // Debugging line
	    }

	    // Check if subscriberList is correctly populated
	    System.out.println("Subscriber list before clearing: " + subscriberList);  // Debugging line

	    // Clear the previous data
	    allSubscriberDataForReport.clear();  // Clears all elements in the list
	    // Assign to your global variable
	    
	    //It doesnt reahc and do the following commend
	    allSubscriberDataForReport = subscriberList;
	    // Print the updated global list
	    System.out.println("Updated allSubscriberDataForReport: " + allSubscriberDataForReport);  // Debugging line
	}

	private void handleBarcodeFetchBorrowedBookRequest(String data) {
		// TODO: Handle multiple books with the same id.
	    BorrowedBookInformationForBarcodeScanner = data.split(":"); // Parse the request's information.
	}
	

	private String extractValue(String part) {
      return part.split(":")[1].trim();
  }
	
	private void handleRegisterRequestFailed() {
		isIDInDataBase = true;
	}
	
	private void handleRegisterRequestSuccess() {
		isIDInDataBase = false;
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
