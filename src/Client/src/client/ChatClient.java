package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.ChatIF;
import gui.BorrowBookWindow.BorrowBookController;
import javafx.application.Platform;
import logic.Book;
import logic.BorrowedBook;
import logic.ClientTimeDiffController;
import logic.Librarian;
import logic.ReservedBook;
import logic.Subscriber;
import ocsf.client.AbstractClient;

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
     * The interface type variable allowing the implementation of 
     * the display method in the client.
     */
    ChatIF clientUI;
    public static boolean alreadyborrowed =false;
    /** List to hold borrowed books. */
    public static List<BorrowedBook> borrowedBookList = new ArrayList<>();

    /** List to hold reserved books. */
    public static List<ReservedBook> reservedBookList = new ArrayList<>();

    /** Subscriber object to manage subscriber data. */
    public static Subscriber s1 = new Subscriber(0, 0, null, null, null, null);

    /** Librarian object to manage librarian data. */
    public static Librarian l1 = new Librarian(0, null);

    /** List to hold book data. */
    public static List<Book> bookList = new ArrayList<>();

    /** List to hold various borrowed book information. */
    public static List<String[][]> br = new ArrayList<>();

    /** Array to hold borrowed book information. */
    public static String[] BorrowedBookInfo;

    /** List to store borrowed book information for reports. */
    public static List<BorrowedBook> BorrowedBookInfoForReports = new ArrayList<>();

    /** Array for storing borrowed book information for barcode scanning. */
    public static String[] BorrowedBookInformationForBarcodeScanner;

    /** List to store the borrowing history of the client. */
    public static ArrayList<String> myHistoryInfo = new ArrayList<>();

    /** Indicates whether the client is waiting for a server response. */
    public static boolean awaitResponse = false;

    /** Flag to control alert display. */
    public static boolean alertIndicator = true;

    /** Indicates if a book is reserved. */
    public static boolean isBookReservedFlag = false;

    /** List to store all subscriber data. */
    public static List<Subscriber> allSubscriberData = null;

    /** List to store subscriber data for reports. */
    public static List<String> allSubscriberDataForReport = new ArrayList<>();

    /** List to store frozen data for reports. */
    public static List<String> allFrozenDataForReport = new ArrayList<>();

    /** Indicates if the IP address is valid. */
    public static boolean isIPValid = false;

    /** Indicates if a message has been received from the server. */
    public static boolean messageReceivedFromServer = false;

    /** Stores the closest return date of a borrowed book. */
    public static String closestReturnDate = null;

    /** Stores the extended return dates for a subscriber. */
    public static String extendedReturnDatesFromSubscriber = null;

    /** Controller for managing client time differences. */
    public static ClientTimeDiffController clock = new ClientTimeDiffController();

    /** Indicates if the ID exists in the database. */
    public static boolean isIDInDataBase;

    /** Current ISBN being processed. */
    public static String currentISBN;

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
  		// openConnection(); // XXX CHECK THIS BEFORE SUBMITTING!
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
	    if (!(msg instanceof String)) {
	        System.err.println("Invalid message type received.");
	        return;
	    }

	    String response = (String) msg;
	    // Dispatch handling based on message prefix
	    if (response.startsWith("Client connected to IP:")) {
	    	isIPValid = true;
	    } else if (response.startsWith("Could not connect to the server.")) {
	 	        isIPValid = false;
	    } else if (response.startsWith("BorrowedBooks:")) {
	        handleBorrowedBooksResponse(response.substring("BorrowedBooks:".length()));
	    } else if (response.startsWith("subscriber_id:")) {
	        handleSubscriberData(response);
	    } else if (response.startsWith("BookReserved:")) {
	        handleBookReservedResponse(response.substring("BookReserved:".length()));
	    } else if (response.startsWith("BorrowedBooks:")) {
            handleBorrowedBooksResponse(response.substring("BorrowedBooks:".length()));
	    } else if (response.startsWith("ReservedBooks:")) {
            handleReservedBooksResponse(response.substring("ReservedBooks:".length()));
	    } else if (response.startsWith("AlreadyReserved")) {
	        handleAlreadyReservedResponse();
	    } else if (response.startsWith("NotReserved")) {
	        handleNotReservedResponse();
        } else if (response.startsWith("librarian_id:")) {
	        handleLibrarianData(response);
	    } else if (response.startsWith("Subscriber updated successfully.")) {
	    	handleUpdateSubInfoSuccess();
	    } else if (response.startsWith("Subscriber ID does not exist.")) {
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
	    } else if (response.startsWith("ClosestReturnDate:")){
	    	closestReturnDate = response.substring("ClosestReturnDate:".length());
	        ChatClient.closestReturnDate = closestReturnDate.equals("Unavailable") ? "Unavailable" : closestReturnDate;
	        awaitResponse = false;
	    } else if (response.startsWith("FetchedHistory:")){
	    	processMyHistoryData(response.substring("FetchedHistory:".length()));
	    } else if (response.startsWith("FetchedRegisterRequests:")){
	    	FetchedRegisterRequests(response.substring("FetchedRegisterRequests:".length()));
	    } else if (response.startsWith("RegistrationSucceed:")) {
	    	handleRegisterRequestSuccess();
  		} else if (response.startsWith("RegistrationFailed:")) {
	    	handleRegisterRequestFailed();
	    } else if (response.startsWith("FetchedReturnRequest:")) {
	    	handleReturnRequestSucess(response.substring("FetchedReturnRequest:".length()));
	    } else if (response.startsWith("An error occurred while fetching the return request data:")) {
	    	handleReturnRequestfailure();
	    } else if (response.startsWith("AllSubscriberInformationForReports")) {
	    	handleAllSubscriberInformationForReports(response.substring("AllSubscriberInformationForReports:".length()));
	    } else if (response.startsWith("AllFrozenInformationForReports")) {
	    	handleAllFrozenInformationForReports(response.substring("AllFrozenInformationForReports:".length()));
	    } else if (response.startsWith("BorrowedBooksForBarcodeScanner:")){
	    	handleBarcodeFetchBorrowedBookRequest(response.substring("BorrowedBooksForBarcodeScanner:".length()));
	    } else if(response.equals("Client disconnected")) {
	    	System.exit(1);
	    } else if (response.startsWith("ExtendedReturnDatesForsSubscriber:")) {
	    	handleExtendedReturnDatesFromSubscriber(response.substring("ExtendedReturnDatesForsSubscriber:".length()));
	    }else if (response.startsWith("AllSubscriberInformation:")) {
	    	handleAllSubscriberInformation(response.substring("AllSubscriberInformation:".length()));
	    }else if (response.startsWith("allBorrowInfo:")) {
	    	handleAllBorrowInformation(response.substring("allBorrowInfo:".length()));
	    } else if(response.equals("Book already borrowed")){
	    	alreadyborrowed = true;
	    } else if(response.equals("AlreadyBorrowed")){
	    	handleAlreadyBorrowedResponse();
	    } else if(response.equals("NotBorrowed")){
	    	handleNotBorrowedResponse();
	    } else if(response.equals("AlreadyRequested")){
	    	handleAlreadyRequestedResponse(); 	    
	    }else if(response.equals("NotRequested")){
	    	handleNotRequestedResponse(); 
	    
	    }
	    // release the lock so that the client's window can continue on working.
	    messageReceivedFromServer = true;
	}
  
  	
  	
  	/**
  	 * Handles the response when a subscriber has already submitted a borrow request for the book.
  	 * <p>
  	 * Updates the {@code BorrowBookController.result} field to {@code "AlreadyRequested"}.
  	 * </p>
  	 */
  	private void handleAlreadyRequestedResponse() {
    	BorrowBookController.result = "AlreadyRequested";
}
  	
  	
  	/**
  	 * Handles the response when a subscriber has not submitted a borrow request for the book.
  	 * <p>
  	 * Updates the {@code BorrowBookController.result} field to {@code "NotRequested"}.
  	 * </p>
  	 */
  	private void handleNotRequestedResponse() {
    	BorrowBookController.result = "NotRequested";
}
  	
  	
  	/**
  	 * Handles the response when a subscriber has already borrowed the book.
  	 * <p>
  	 * Updates the {@code BorrowBookController.result} field to {@code "AlreadyBorrowed"}.
  	 * </p>
  	 */
	private void handleAlreadyBorrowedResponse() {
		BorrowBookController.result = "AlreadyBorrowed";
}
	
	
	
	/**
	 * Handles the response when a subscriber has not borrowed the book.
	 * <p>
	 * Updates the {@code BorrowBookController.result} field to {@code "NotBorrowed"}.
	 * </p>
	 */
	private void handleNotBorrowedResponse() {
    	BorrowBookController.result = "NotBorrowed";
}
	
	
	
	/**
	 * Handles the response when a subscriber has not reserved the book.
	 * <p>
	 * Updates the {@code BorrowBookController.result} field to {@code "NotReserved"}.
	 * </p>
	 */
    private void handleNotReservedResponse() {
    	BorrowBookController.result = "NotReserved";
}
    
    
    
    /**
     * Handles the response when a subscriber has already reserved the book.
     * <p>
     * Updates the {@code BorrowBookController.result} field to {@code "AlreadyReserved"}.
     * </p>
     */
	private void handleAlreadyReservedResponse() {
		BorrowBookController.result = "AlreadyReserved";
}

	/**
     * Handles the response for reserved book status.
     *
     * @param data The response data from the server.
     */
  	private void handleBookReservedResponse(String data) {
	    switch (data.trim()) {
	        case "Yes":
	            ChatClient.isBookReservedFlag = true;
	            break;
	        case "No":
	            ChatClient.isBookReservedFlag = false;
	            break;
	        default:
	            System.err.println("Error checking book reservation status: " + data);
	            break;
	    }
	}

  	/**
     * Handles the response for borrowed books.
     *
     * @param data The response data containing borrowed book information.
     */
  	private void handleAllBorrowInformation(String data) {
	    // Check if no books were found
	    if (data.equals("NoBooksFound")) {
	        ChatClient.BorrowedBookInfoForReports.clear();
	    } 
	    // Check if there's an error in fetching data
	    else if (data.startsWith("Error")) {
	        System.err.println("Error fetching borrowed books: " + data);
	    } 
	    else {
	        // Remove the "allBorrowInfo:" prefix
	        String bookData = data.replace("allBorrowInfo:", "");

	        // Split the data into individual book strings
	        String[] bookStrings = bookData.split(";");

	        // Clear the current borrowed book info
	        ChatClient.BorrowedBookInfoForReports.clear();

	        // Process each book entry
	        for (String bookDetails : bookStrings) {
	            String[] fields = bookDetails.split(","); // Split fields by comma

	            // Ensure there are 6 fields as expected
	            if (fields.length == 6) {
	                try {
	                    int borrowId = Integer.parseInt(fields[0]);
	                    int subscriberId = Integer.parseInt(fields[1]);
	                    String name = fields[2];
	                    String borrowedTime = fields[3];
	                    String returnTime = fields[4];
	                    String ISBN = fields[5];

	                    // Calculate the time left to return the book
	                    int timeLeftToReturn = clock.howMuchTimeLeftToReturnABook(returnTime);

	                    // Add the book information to the report list
	                    ChatClient.BorrowedBookInfoForReports.add(
	                        new BorrowedBook(borrowId, subscriberId, name, borrowedTime, returnTime, timeLeftToReturn, ISBN)
	                    );
	                } catch (NumberFormatException e) {
	                    System.err.println("Error parsing book data: " + e.getMessage());
	                }
	            } else {
	                System.err.println("Invalid data format for book: " + bookDetails);
	            }
	        }
	    }
	}

  	/**
  	 * Handles a failure in fetching return requests by logging a message to the console.
  	 */
  	private void handleReturnRequestfailure() {
  		System.err.print("Fetch return request failed");
  	}

  	/**
  	 * Processes and handles the successful return request response.
  	 *
  	 * @param data The return request data received from the server.
  	 */
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
		    	System.err.println("Invalid book data received: " + String.join(",", RegisterDetails));
		    }
		}
	}
		
	/**
	 * Handles the response for borrowed books and updates the borrowed book list.
	 *
	 * @param data The borrowed books data or an error message received from the server.
	 */
	private void handleBorrowedBooksResponse(String data) {
	    if (data.equals("NoBooksFound")) {
	        ChatClient.borrowedBookList.clear();
	    } else if (data.startsWith("Error")) {
	        System.err.println("Error fetching borrowed books: " + data);
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

	/**
	 * Handles the response for reserved books and updates the reserved book list.
	 *
	 * @param data The reserved books data or an error message received from the server.
	 */
	private void handleReservedBooksResponse(String data) {
	    if (data.equals("NoBooksFound")) {
	        ChatClient.reservedBookList.clear();
	    } else if (data.startsWith("Error")) {
	        System.err.println("Error fetching Reserved books: " + data);
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
	
	/**
	 * Processes and handles the book information received from the server.
	 *
	 * @param data The book data string received from the server.
	 */
	private void handleBookInfo(String data) {
	    try {
	        if (data.equals("NoBooksFound")) {
	            BorrowedBookInfo = null;
	        } else if (data.startsWith("Error")) {
	            System.err.println("Error from server: " + data);
	            BorrowedBookInfo = null;
	        } else {
	            BorrowedBookInfo = data.split(",");

	            if (BorrowedBookInfo.length < 8) {
	                throw new IllegalArgumentException("Incomplete book data received.");
	            }
	        }
	    } catch (Exception e) {
	        System.err.println("Error handling book info: " + e.getMessage());
	        BorrowedBookInfo = null;
	    }
	}
  
	/**
	 * Processes subscriber data and updates the subscriber object.
	 *
	 * @param response The subscriber data string received from the server.
	 */
	private void handleSubscriberData(String response) {
	    l1.setLibrarian_id(-1); // Reset librarian data
	    processSubscriberData(response);
	}

	/**
	 * Handles the response for librarian data by resetting subscriber data and processing the librarian data.
	 * 
	 * @param response The response containing librarian data.
	 */
	private void handleLibrarianData(String response) {
	    s1.setSubscriber_id(-1); // Reset subscriber data
	    processLibrarianData(response);
	}

	/**
	 * Handles the case where a nonexistent ID is provided by resetting librarian and subscriber IDs.
	 */
	private void handleNonexistentID() {
	    l1.setLibrarian_id(-1);
	    s1.setSubscriber_id(-1);
	}
	
	/**
	 * Handles the success of subscriber info update by setting the alert indicator to true.
	 */
	private void handleUpdateSubInfoSuccess() {
		alertIndicator = true;
	}
	
	/**
	 * Handles the failure of subscriber info update by setting the alert indicator to false.
	 */
	private void handleUpdateSubInfoFail() {
		alertIndicator = false;
	}
	
	/**
	 * Processes the fetched register requests data and populates the list with parsed data.
	 * 
	 * @param data The data string containing register requests separated by semicolons.
	 */
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
	            System.err.println("Invalid book data received: " + String.join(",", RegisterDetails));
	        }
	    }
	}
	
	/**
	 * Processes the fetched borrowed books data and populates the list with parsed data.
	 * 
	 * @param data The data string containing borrowed books information separated by semicolons.
	 */
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
	            System.err.println("Invalid book data received: " + String.join(",", bookDetails));
	        }
	    }
	}
	
	/**
	 * Processes the history data and populates the user's history information list.
	 * 
	 * @param msg The message containing history data separated by semicolons.
	 */
	private void processMyHistoryData(String msg) {
	    try {
	        // Split the message by ';' and store it in a String array
	        String[] historyData = msg.split(";");
	        
	        for (String activity : historyData) {
	        	myHistoryInfo.add(activity);
	        }
	    } catch (Exception e) {
	        System.err.println("Error parsing My History data: " + e.getMessage());
	    }
	}

	/**
	 * Handles book data received from the server by parsing or displaying appropriate messages.
	 * 
	 * @param data The data string containing book information or error messages.
	 */
	private void handleBookData(String data) {
	    if (data.equals("NoBooksFound")) {
	    } else if (data.startsWith("Error")) {
	        System.err.println("Error from server: " + data);
	    } else {
	        parseBookData(data);
	    }
	}

	/**
	 * Parses the book data string and populates the book list.
	 * 
	 * @param data The data string containing book information separated by semicolons.
	 */
	private void parseBookData(String data) {
	    try {
	        String[] bookStrings = data.split(";");
	        bookList.clear(); // Clear existing book data
	        for (String bookData : bookStrings) {
	            String[] fields = bookData.split(",");
	            String isbn = fields[0];
	            String name = fields[1];
	            String subject = fields[2];
	            String description = fields[3];
	            int copies = Integer.parseInt(fields[4]);
	            String location = fields[5];
	            int availableCopies = Integer.parseInt(fields[6]);
	            int reservedCopies = Integer.parseInt(fields[7]); 
	            bookList.add(new Book(isbn, name, description, subject, copies, location, availableCopies, reservedCopies));
	        }
	    } catch (Exception e) {
	        System.err.println("Error parsing book data: " + e.getMessage());
	    }
	}
  
	/**
	 * Handles the client UI message and establishes a connection with the server.
	 * 
	 * @param message The message to be sent to the server.
	 */
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
	 * Parses and sets subscriber data.
	 * 
	 * @param msg The message containing subscriber data.
	 */
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
      		System.err.println("Error parsing subscriber data: " + e.getMessage());
      		s1.setSubscriber_id(-1); // Mark as not found
      	}
	}

	/**
	 * Processes the librarian data from the given message and updates the {@code l1} object.
	 *
	 * @param msg the string containing librarian information in the format "key:value,key:value,..."
	 */
	private void processLibrarianData(String msg) {
	  try {
	      String[] parts = msg.split(",");
	      String librarianId = extractValue(parts[0]);
	      String librarianName = extractValue(parts[1]);
	
	      // Set values to the Librarian object
	      l1.setLibrarian_id(Integer.parseInt(librarianId));
	      l1.setLibrarian_name(librarianName);
	  } catch (Exception e) {
	      System.err.println("Error parsing librarian data: " + e.getMessage());
	      l1.setLibrarian_id(-1); // Mark as not found
	      }
	  }
	
	/**
	 * Parses a message containing subscriber information, creates {@code Subscriber} objects, 
	 * and updates the global list of all subscribers.
	 *
	 * @param msg the string containing all subscriber information, separated by ';'
	 */
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
	
	/**
	 * Handles frozen information for reports by parsing the message and formatting the data.
	 *
	 * @param msg the string containing frozen records in the format "[record1;record2;...]"
	 */
	private void handleAllFrozenInformationForReports(String msg) {
	    // Check and remove leading '[' if present
	    if (msg.startsWith("[")) {
	        msg = msg.substring(1);  // Removes the first character
	    }
	    if (msg.endsWith("]")) {
	        msg = msg.substring(0, msg.length() - 1);  // Removes the last character
	    }

	    // List of strings to hold the data from the 'databydate' table in the desired format
	    List<String> dataList = new ArrayList<String>();
	    
	    // Split the msg so each element contains all of the information for a record one at a time.
	    String[] records = msg.split(";");

	    for (String recordData : records) {
	        String[] recordInformation = recordData.split(","); // Split the information of the record
	        
	        if (recordInformation.length < 5) {  // Ensure that there's enough data
	            continue;  // Skip invalid data
	        }
	        
	        // Convert the record information into a formatted string
	        try {
	            String formattedRecordData = recordInformation[0] + "," +  // idDateByDate
	                recordInformation[1] + "," +  // Frozen
	                recordInformation[2] + "," +  // NotFrozen
	                recordInformation[3] + "," +  // BorrowedBooks
	                recordInformation[4];         // Late
	            
	            // Add the formatted string to the list
	            dataList.add(formattedRecordData);
	        } catch (Exception e) {
	            System.err.println("Error processing record data: " + recordData);  // Debugging line
	            e.printStackTrace();  // Print the stack trace for debugging
	        }
	    }

	    // Clear the previous data
	    allFrozenDataForReport.clear();  // Clears all elements in the list
	    // Assign to your global variable
	    allFrozenDataForReport = dataList;
	}

	/**
	 * Processes all subscriber information for reports by formatting and updating the global list.
	 *
	 * @param msg the string containing subscriber data in the format "[subscriber1;subscriber2;...]"
	 */
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

	    for (String subscriberData : subscribersInformation) {
	        String[] subscriberInformation = subscriberData.split(","); // Split the information of the subscriber
	        
	        if (subscriberInformation.length < 6) {  // Ensure that there's enough data
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
	            e.printStackTrace();  // Print the stack trace for debugging
	        }
	    }

	    // Clear the previous data
	    allSubscriberDataForReport.clear();  // Clears all elements in the list
	    // Assign to your global variable
	    allSubscriberDataForReport = subscriberList;
	}

	/**
	 * Parses the data for a borrowed book request fetched by the barcode scanner.
	 *
	 * @param data the string containing book details separated by ':'
	 */
	private void handleBarcodeFetchBorrowedBookRequest(String data) {
	    BorrowedBookInformationForBarcodeScanner = data.split(":"); // Parse the request's information.
	}
	
	/**
	 * Extracts the value part from a key-value pair string (e.g., "key:value").
	 *
	 * @param part the string containing the key-value pair
	 * @return the extracted value, trimmed of whitespace
	 */
	private String extractValue(String part) {
      return part.split(":")[1].trim();
	}
	
	/**
	 * Handles a failed registration request by updating the state to indicate the ID exists in the database.
	 */
	private void handleRegisterRequestFailed() {
		isIDInDataBase = true;
	}
	
	/**
	 * Handles a successful registration request by updating the state to indicate the ID does not exist in the database.
	 */
	private void handleRegisterRequestSuccess() {
		isIDInDataBase = false;
	}
	
	/**
	 * Updates the extended return dates of a subscriber.
	 *
	 * @param extendedReturnDatesByUser the string containing the extended return dates
	 */
	private void handleExtendedReturnDatesFromSubscriber(String extendedReturnDatesByUser){
		extendedReturnDatesFromSubscriber = extendedReturnDatesByUser;
	}
	
	/**
	 * Terminates the client and closes the connection.
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
