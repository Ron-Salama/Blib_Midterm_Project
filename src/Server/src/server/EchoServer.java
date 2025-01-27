package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import common.ConnectToDb;
import logic.ServerTimeDiffController;
import logic.TaskScheduler;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

/**
 * <p>The EchoServer class extends the {@link ocsf.server.AbstractServer} and handles all
 * communication between clients and the server, as well as interactions with
 * the database. This includes actions such as fetching data, updating subscriber
 * information, handling book borrow/return requests, logging, and more.</p>
 * 
 * <p>When the server starts, it initializes a daily TaskScheduler,
 * connects to the database, and sets up logging.
 * When the server stops, it stops the TaskScheduler and closes the database connection.</p>
 */
public class EchoServer extends AbstractServer {

    /** The default port on which the server listens. */
    final public static int DEFAULT_PORT = 5555;
    public static Boolean Terminated = false;
    /** A single {@link Connection} used by the TaskScheduler thread for daily tasks. */
    public static Connection taskSchedulerConnection;

    /** A controller that calculates time differences for server operations. */
    public static ServerTimeDiffController clock = new ServerTimeDiffController();

    /** The single database connection used by this server. */
    private Connection dbConnection;

    /** A TaskScheduler instance used to run daily tasks (like DB updates). */
    private TaskScheduler taskScheduler;

    // --------------------------------------------------------------
    //                      CONSTRUCTORS
    // --------------------------------------------------------------

    /**
     * Constructs a new EchoServer that listens on the specified port.
     * 
     * @param port the port number on which the server will listen for connections.
     */
    public EchoServer(int port) {
        super(port);
    }

    // --------------------------------------------------------------
    //       OVERRIDDEN METHODS FROM AbstractServer / HOOKS
    // --------------------------------------------------------------

    /**
     * Called when the server starts listening for connections.
     * <ul>
     *   <li>Initializes the log file.</li>
     *   <li>Outputs a log message.</li>
     *   <li>Establishes the database connection.</li>
     *   <li>Starts the {@link TaskScheduler} for daily tasks.</li>
     * </ul>
     */
    @Override
    protected void serverStarted() {
        initializeLogFile();
        outputInOutputStreamAndLog("Server listening for connections on port " + getPort());
        try {
            dbConnection = ConnectToDb.getConnection(); // Open the connection
            taskSchedulerConnection = dbConnection;
            outputInOutputStreamAndLog("Connected to the database.");

            taskScheduler = new TaskScheduler();
            taskScheduler.startDailyTasks(); // Run the thread to update the DB daily.
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }

    /**
     * Called when the server stops listening for connections.
     * <ul>
     *   <li>Stops the daily TaskScheduler thread.</li>
     *   <li>Outputs a log message.</li>
     *   <li>Closes the database connection.</li>
     * </ul>
     */
    @Override
    protected void serverStopped() {
        if (taskScheduler != null) { // Stop the daily scheduler when the server is closing.
            taskScheduler.stop();
        }

        outputInOutputStreamAndLog("Server has stopped listening for connections.");
        if (dbConnection != null) {
            try {
                dbConnection.close(); // Close the connection
                outputInOutputStreamAndLog("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing the database connection: " + e.getMessage());
            }
        }
    }

    // --------------------------------------------------------------
    //        HANDLING CLIENT CONNECTION / DISCONNECTION
    // --------------------------------------------------------------

    /**
     * Handles the disconnection process of a client.
     * <ul>
     *   <li>Logs the disconnection information (IP, Hostname, Status).</li>
     *   <li>Sends a message to the client indicating they've been disconnected.</li>
     * </ul>
     *
     * @param client the {@link ConnectionToClient} that is disconnecting
     * @throws IOException if an I/O error occurs when sending the message
     */
    public void clientDisconnect(ConnectionToClient client) throws IOException {
        String clientIP = client.getInetAddress().getHostAddress();
        String clientHost = client.getInetAddress().getHostName();
        String connectionStatus = "Disconnected";

        String IPMessage = "Client disconnected.\n"
                + "Client IP: " + clientIP + "\n"
                + "Client Hostname: " + clientHost + "\n"
                + "Connection Status: " + connectionStatus;

        outputInOutputStreamAndLog(IPMessage);
        client.sendToClient("Client disconnected");
    }

    // --------------------------------------------------------------
    //        CORE MESSAGE HANDLING FROM CLIENT
    // --------------------------------------------------------------

    /**
     * Handles messages received from the client. Parses the message prefix
     * to determine the type of request, and delegates processing to the appropriate
     * method.
     *
     * @param msg the message object from the client (expected to be a String)
     * @param client the {@link ConnectionToClient} sending the message
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        outputInOutputStreamAndLog("Message received: " + msg + " from " + client);

        try {
            String message = (String) msg;
            int delimiterIndex = message.indexOf(":");
            if (delimiterIndex == -1) {
                client.sendToClient("Invalid command format.");
                return;
            }

            // Extract prefix and body
            String prefix = message.substring(0, delimiterIndex).trim();
            String body = message.substring(delimiterIndex + 1).trim();

            switch (prefix) {
                case "IP":
                    handleIPCase(client, body);
                    break;

                case "Fetch return request":
                    HandleFetchreturnrequest(client, body);
                    break;

                case "Fetch":
                    handleFetchCase(client, body);
                    break;

                case "Update":
                    handleUpdateCase(client, body);
                    break;

                case "IsBookReserved":
                    handleIsBookReservedCase(client, body);
                    break;

                case "GetBooks":
                    handleGetBooksCase(client, body);
                    break;

                case "GetBookInfo":
                    handleGetBookInfoCase(client, body);
                    break;

                case "BorrowRequest":
                    handleBorrowRequestCase(client, body);
                    break;

                case "Reserve":
                    handleReserveRequestCase(client, body);
                    break;

                case "ReserveSuccess":
                    handleReserveSuccessCase(client, body);
                    break;
                case "ExistingReservationCheck":
                    handleExistingReservationCheck(client, body);
                    break;
                    
                case "AlreadyBorrowedCheck":
                    handleAlreadyBorrowedCheck(client, body);
                    break;

                case "AlreadyRequestedCheck":
                    handleAlreadyRequestedCheck(client, body);
                    break;

                case "FetchBorrowRequest":
                    handleFetchBorrowRequestCase(client, body);
                    break;

                case "GetBorrowedBooks":
                    handleGetBorrowedBooksCase(client, body);
                    break;

                case "GetReservedBooks":
                    handleGetReservedBooksCase(client, body);
                    break;

                case "UpdateReturnDate":
                    handleUpdateReturnDate(client, body);
                    break;

                case "RegisterRequest":
                    handleRegisterRequestCase(client, body);
                    break;

                case "FetchRegisterRequest":
                    handleFetchRegisterRequestCase(client, body);
                    break;

                case "GetHistory":
                    handleMyHistoryData(client, body);
                    break;

                case "SubmitBorrowRequest":
                    SubmitBorrowRequest(client, body);
                    break;

                case "SubmitRetrieve":
                    SubmitRetrieve(client, body);
                    break;

                case "Return request":
                    handlereturnrequest(client, body);
                    break;

                case "UpdateHistoryInDB":
                    updateHistoryInDB(client, body);
                    break;

                case "Handle return":
                    HandleBookReturn(client, body);
                    break;

                case "FetchBorrowedBooksForBarcodeScanner":
                    handleGetBorrowedBooksCaseForBarcodeScanner(client, body);
                    break;

                case "Handle register":
                    HandleRegisterOfSubscriber(client, body);
                    break;

                case "FetchClosestReturnDate":
                    handleFetchClosestReturnDate(client, body);
                    break;

                case "Handle Lost":
                    HandleLost(client, body);
                    break;

                case "NewExtensionApprovedBySubscriber":
                    handleBookReturnExtensionBySubscriber(body);
                    break;

                case "PullNewExtenstion":
                    handleNewReturnDatesForLibrarian(client);
                    break;

                case "EXIT":
                    clientDisconnect(client);
                    break;

                case "FetchAllSubscriberData":
                    handleFetchAllSubscriberData(client);
                    break;

                case "FetchAllSubscriberInformationForReports":
                    handleFetchAllSubscriberDataForReports(client);
                    break;

                case "FetchAllFrozenInformationForReports":
                    handleFetchAllFrozenDataForReports(client);
                    break;

                case "SubmitBorrowRequestBarcode":
                    handleBorrowfrombarcode(client, body);
                    break;

                case "GetAllBorrowedBooksInfo":
                    GetAllBorrowedBooksInfoCase(client, body);
                    break;

                default:
                    client.sendToClient("Unknown command.");
                    break;
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error handling client message: " + e.getMessage());
            try {
                client.sendToClient("Server error: " + e.getMessage());
            } catch (IOException ioException) {
                System.err.println("Error sending message to client: " + ioException.getMessage());
            }
        }
    }

   

	// --------------------------------------------------------------
    //             SPECIFIC REQUEST HANDLERS
    // --------------------------------------------------------------
    
    
    
    /**
     * Handles a request to check if a subscriber has already submitted a borrow request for a specific book.
     * <p>
     * Expected message format: <br>
     * {@code CheckRequest:subscriberId,bookId}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the request details as a comma-separated string containing subscriber ID and book ID
     */
    private void handleAlreadyRequestedCheck(ConnectionToClient client, String body) {
    	// Parse the body to extract subscriber ID and book ID
   	 	String[] parts = body.split(",");
        String subscriberId = parts[0];
        String bookId = parts[1];
        
        try {
            // Fetch the reserved books for the given subscriber ID
            List<String> requestedBooks = ConnectToDb.fetchBorrowRequestsBySubscriberId(dbConnection, subscriberId);
            boolean alreadyRequested = false;

            // Check if the subscriber has already reserved the book
            for (String bookData : requestedBooks) {
                String[] fields = bookData.split(",");
                if (fields[4].equals(bookId)) { // Compare book IDs
                	alreadyRequested = true;
                    break;
                }
            }

            // Send the appropriate response back to the client
            if (alreadyRequested) {
                client.sendToClient("AlreadyRequested");
            } else {
                client.sendToClient("NotRequested");
            }
        } catch (Exception e) {
            System.err.println("Error in handleExistingReservationCheck: " + e.getMessage());
            try {
                client.sendToClient("Error: Unable to process reservation check.");
            } catch (IOException ioException) {
                System.err.println("Failed to send error message to client: " + ioException.getMessage());
            }
        }
		
	}

    
    
    
    
    
    /**
     * Handles a request to check if a subscriber has already borrowed a specific book.
     * <p>
     * Expected message format: <br>
     * {@code CheckBorrowed:subscriberId,bookId}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the request details as a comma-separated string containing subscriber ID and book ID
     * @throws SQLException 
     */
	private void handleAlreadyBorrowedCheck(ConnectionToClient client, String body) throws SQLException {
    	// Parse the body to extract subscriber ID and book ID
    	 String[] parts = body.split(",");
         String subscriberId = parts[0];
         String bookId = parts[1];
         
         boolean alreadyborrowed = ConnectToDb.bookalreadyborrowed(dbConnection, subscriberId, bookId);
         // Insert the request
         if(alreadyborrowed) {
         	try {
				client.sendToClient("AlreadyBorrowed");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         	
         	return;
         }
         
         else {
        	 try {
 				client.sendToClient("NotBorrowed");
 			} catch (IOException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
        	 return;
         }
         
         
	}

	
	
	
	/**
	 * Handles a request to check if a subscriber has already reserved a specific book.
	 * <p>
	 * Expected message format: <br>
	 * {@code CheckReservation:subscriberId,bookId}
	 * </p>
	 *
	 * @param client the {@link ConnectionToClient} sending the request
	 * @param body   the request details as a comma-separated string containing subscriber ID and book ID
	 */
    private void handleExistingReservationCheck(ConnectionToClient client, String body) {
        // Parse the body to extract subscriber ID and book ID
        String[] parts = body.split(",");
        String subscriberId = parts[0];
        String bookId = parts[1];

        try {
            // Fetch the reserved books for the given subscriber ID
            List<String> reservedBooks = ConnectToDb.fetchReservedBooksBySubscriberId(dbConnection, subscriberId);
            boolean alreadyReserved = false;

            // Check if the subscriber has already reserved the book
            for (String bookData : reservedBooks) {
                String[] fields = bookData.split(",");
                if (fields[5].equals(bookId)) { // Compare book IDs
                    alreadyReserved = true;
                    break;
                }
            }

            // Send the appropriate response back to the client
            if (alreadyReserved) {
                client.sendToClient("AlreadyReserved");
            } else {
                client.sendToClient("NotReserved");
            }
        } catch (Exception e) {
            System.err.println("Error in handleExistingReservationCheck: " + e.getMessage());
            try {
                client.sendToClient("Error: Unable to process reservation check.");
            } catch (IOException ioException) {
                System.err.println("Failed to send error message to client: " + ioException.getMessage());
            }
        }
    }


	/**
     * Handles a request to borrow a book using a barcode scanner.
     * <p>
     * Expected message format: <br>
     * {@code SubmitBorrowRequestBarcode:subscriberName,subscriberId,bookTitle,bookID,requestDate,returnDate}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the request details as a comma-separated string
     * @throws IOException if sending a response to the client fails
     */
    private void handleBorrowfrombarcode(ConnectionToClient client, String body) throws IOException {
        try {
            String[] requestDetails = body.split(",");
            if (requestDetails.length >= 6) {
                String subscriberName = requestDetails[0]; 
                String subscriberId = requestDetails[1];
                String bookTitle = requestDetails[2];
                String bookID = requestDetails[3];
                String requestDate = requestDetails[4];
                String returnDate = requestDetails[5];
                String requestType = "Borrow For Subscriber";

                boolean alreadyborrowed = ConnectToDb.bookalreadyborrowed(dbConnection, subscriberId, bookID);
                // Insert the request
                if(alreadyborrowed) {
                	client.sendToClient("Book already borrowed");
                	return;
                }
                //if the book isnt borrowed already
                // Step 1: Process the actual borrowing logic
                boolean isBorrowed = ConnectToDb.insertBorrowBook(dbConnection, body);

                if (isBorrowed) {
                    // Step 2: Decrease the available number of copies
                    ConnectToDb.decreaseAvaliabeNumCopies(dbConnection, bookID);
                    client.sendToClient("Borrow request processed successfully and amount of available books decreased.");
                } else {
                    client.sendToClient("Borrow request could not be processed.");
                }
            } else {
                throw new IllegalArgumentException("Invalid request details provided: " + body);
            }
        } catch (Exception e) {
            client.sendToClient("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles marking a book as lost by a subscriber.
     * <p>
     * Expected message format: <br>
     * {@code Handle Lost:subscriberName,subscriberId,bookName,bookId,bookTime,returnTime}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the request details as a comma-separated string
     * @throws IOException if sending a response to the client fails
     * @throws SQLException if a database access error occurs
     */
    private void HandleLost(ConnectionToClient client, String body) throws IOException, SQLException {
    	try{
        String[] messageParts = body.split(",");
        String subscriberName = messageParts[0].trim();
        String subscriberId = messageParts[1].trim();
        String bookName = messageParts[2].trim();
        String bookid = messageParts[3].trim();
        String bookTime = messageParts[4].trim();
        String returnTime = messageParts[5].trim();
        String returnstatus = ConnectToDb.returnbook(this.dbConnection, subscriberId, bookid);
        if(returnstatus.equalsIgnoreCase("Book returned successfully")) {
        boolean requestDeleted = ConnectToDb.deleteRequest(this.dbConnection, "Return For Subscriber", subscriberId, bookid);
        boolean reduceamount = ConnectToDb.decreaseNumCopies(dbConnection, bookid);
        if (reduceamount && requestDeleted) {
            client.sendToClient("return request was committed and deleted but the book is lost and "+"Successfully decreased NumCopies for bookId: " + bookid);
        }
        else if (!reduceamount && requestDeleted) {
            client.sendToClient("request deleted but NumCopies of bookId: "+bookid+" wasnt decreased");
        }
        else if(reduceamount && !requestDeleted) {
        	client.sendToClient("NumCopies of bookId: "+bookid+" was decreased but request wasnt deleted");
        }
        else {
        	client.sendToClient("NumCopies of bookId: "+bookid+" wasnt decreased and request wasnt deleted");
        }
        }
        else {
        	client.sendToClient("Return request status: "+returnstatus);
        }
        }
        catch (Exception e) {
            try {
                client.sendToClient("An error occurred while processing the book return: " + e.getMessage());
            } catch (IOException ioException) {
                System.err.println("Failed to send error message to client: " + ioException.getMessage());
            }
            e.printStackTrace();
        }
    }

    /**
     * Fetches a list of all borrowed books for the task scheduler and sends to the client.
     * <p>
     * Expected message format: <br>
     * {@code GetAllBorrowedBooksInfo: <any_body_text>}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the request details (unused in logic, can be any text)
     * @throws IOException if sending a response to the client fails
     * @throws SQLException if a database access error occurs
     */
    private void GetAllBorrowedBooksInfoCase(ConnectionToClient client, String body) throws IOException, SQLException {
        List<String> allInfo = ConnectToDb.fetchBorrowedBooksForTaskScheduler(dbConnection);
        String allInfoString = String.join(";", allInfo);
        client.sendToClient("allBorrowInfo:" + allInfoString);
    }

    /**
     * Checks if a particular book is reserved.
     * <p>
     * Expected message format: <br>
     * {@code IsBookReserved:ISBN}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the ISBN of the book to check
     * @throws IOException if sending a response to the client fails
     */
    private void handleIsBookReservedCase(ConnectionToClient client, String body) throws IOException {
        try {
            String ISBN = body.trim();
            boolean isReserved = ConnectToDb.isBookReserved(dbConnection, ISBN);

            if (isReserved) {
                client.sendToClient("BookReserved:Yes");
            } else {
                client.sendToClient("BookReserved:No");
            }
        } catch (Exception e) {
            e.printStackTrace();
            client.sendToClient("BookReserved:Error:" + e.getMessage());
        }
    }

    /**
     * Handles updating the return date for a borrowed book (e.g., extensions).
     * <p>
     * Expected message format: <br>
     * {@code UpdateReturnDate:borrowID,extendedReturnDate}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the comma-separated values of borrowID and new return date
     */
    private void handleUpdateReturnDate(ConnectionToClient client, String body) {
        String[] parts = body.split(",");
        int borrowID = Integer.parseInt(parts[0].trim());
        String extendedReturnDate = parts[1].trim();
        ConnectToDb.updateReturnDateAfterExtension(borrowID, extendedReturnDate, dbConnection);
    }

    /**
     * Handles fetching a list of return requests from the database.
     * <p>
     * Expected prefix: {@code Fetch return request}
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the message body (unused in logic)
     * @throws IOException if sending a response to the client fails
     */
    private void HandleFetchreturnrequest(ConnectionToClient client, String body) throws IOException {
        try {
            String ReturnRequests = ConnectToDb.fetchReturnRequest(this.dbConnection);
            client.sendToClient("FetchedReturnRequest:" + ReturnRequests);
        } catch (Exception e) {
            client.sendToClient("An error occurred while fetching the return request data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Processes a return request from a subscriber and inserts it into the database
     * for librarian approval.
     * <p>
     * Expected message format: <br>
     * {@code Return request: Subscriber ID is <ID> <SubscriberName> Borrow info: <BookID> <BookName> <borrowDate> <returnDate> <timeLeft>}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the full message containing subscriber and book details
     * @throws IOException if an I/O error occurs when sending a response
     */
    private void handlereturnrequest(ConnectionToClient client, String body) throws IOException {
        try {
            // Extract Subscriber ID
            int idStartIndex = body.indexOf("Subscriber ID is") + 17;  
            int idEndIndex = body.indexOf(" ", idStartIndex);
            String subscriberId = body.substring(idStartIndex, idEndIndex).trim();

            // Extract Subscriber Name
            int nameStartIndex = idEndIndex + 1;
            int nameEndIndex = body.indexOf("Borrow info:");
            String subscriberName = body.substring(nameStartIndex, nameEndIndex).trim();

            // Extract Borrow info
            int borrowInfoStartIndex = body.indexOf("Borrow info:") + 12;
            String borrowInfo = body.substring(borrowInfoStartIndex).trim();
            String[] bookInfo = borrowInfo.split(" ");

            // Book ID
            String bookID = bookInfo[0];
            // Build book name (may contain multiple words)
            StringBuilder bookNameBuilder = new StringBuilder();
            for (int i = 1; i < bookInfo.length - 3; ++i) {
                bookNameBuilder.append(bookInfo[i]).append(" ");
            }
            String bookName = bookNameBuilder.toString().trim();

            // Borrow date, return date, time left
            String borrowDate = bookInfo[bookInfo.length - 3];
            String returnDate = bookInfo[bookInfo.length - 2];
            String timeLeft = bookInfo[bookInfo.length - 1];

            // Define request type
            String requestType = "Return For Subscriber";

            // Insert the return request into the database
            ConnectToDb.insertRequest(this.dbConnection, requestType, subscriberId, subscriberName,
                                      bookName, bookID, borrowDate, returnDate,timeLeft);

            client.sendToClient("Return request submitted, awaiting librarian approval.");
        } catch (SQLException e) {
            e.printStackTrace();
            client.sendToClient("Error inserting return request.");
        } catch (Exception e) {
            e.printStackTrace();
            client.sendToClient("Error processing the return request message.");
        }
    }

    /**
     * Fetches subscriber or librarian data (if the identifier exists in either table).
     * <p>
     * Expected message format: <br>
     * {@code Fetch:ID}
     * </p>
     *
     * @param client     the {@link ConnectionToClient} sending the request
     * @param identifier the subscriber or librarian ID to fetch
     * @throws SQLException if a database access error occurs
     * @throws IOException  if sending a response to the client fails
     */
    private void handleFetchCase(ConnectionToClient client, String identifier) throws SQLException, IOException {
        if (ConnectToDb.checkSubscriberExists(dbConnection, identifier)) {
            String subscriberData = ConnectToDb.fetchSubscriberData(dbConnection, identifier);
            client.sendToClient(subscriberData);
        } else if (ConnectToDb.checkLibrarianExists(dbConnection, identifier)) {
            String librarianData = ConnectToDb.fetchLibrarianData(dbConnection, identifier);
            client.sendToClient(librarianData);
        } else {
            client.sendToClient("ID does not exist.");
        }
    }

    /**
     * Submits a borrow request by inserting a borrowed book record into the DB,
     * then removes the request from the requests table if successful.
     * <p>
     * Expected message format: <br>
     * {@code SubmitBorrowRequest:subscriberName,subscriberId,bookTitle,bookID,requestDate,returnDate}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the request details
     * @throws SQLException if a database access error occurs
     * @throws IOException  if sending a response to the client fails
     */
    private void SubmitBorrowRequest(ConnectionToClient client, String body) throws SQLException, IOException {
        try {
            String[] requestDetails = body.split(",");
            if (requestDetails.length >= 6) {
                String subscriberName = requestDetails[0];
                String subscriberId = requestDetails[1];
                String bookTitle = requestDetails[2];
                String bookID = requestDetails[3];
                String requestDate = requestDetails[4];
                String returnDate = requestDetails[5];
                String requestType = "Borrow For Subscriber";

                // Insert into Borrow
                boolean isBorrowed = ConnectToDb.insertBorrowBook(dbConnection, body);

                if (isBorrowed) {
                    // Remove from requests table
                    boolean isDeleted = ConnectToDb.deleteRequest(dbConnection, requestType, subscriberId, bookID);
                    if (isDeleted) {
                        client.sendToClient("Borrow request processed successfully and removed from requests db.");
                    } else {
                        client.sendToClient("Borrow request processed, but failed to remove from requests db.");
                    }
                } else {
                    client.sendToClient("Borrow request could not be processed.");
                }
            } else {
                throw new IllegalArgumentException("Invalid request details provided: " + body);
            }
        } catch (Exception e) {
            client.sendToClient("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Submits a 'Retrieve' action (similar to a borrow) directly by inserting a borrowed book record.
     * Decreases the available number of copies accordingly.
     * <p>
     * Expected message format: <br>
     * {@code SubmitRetrieve:subscriberName,subscriberId,bookTitle,bookID,requestDate,returnDate}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the comma-separated request details
     * @throws SQLException if a database access error occurs
     * @throws IOException  if sending a response to the client fails
     */
    private void SubmitRetrieve(ConnectionToClient client, String body) throws SQLException, IOException {
        try {
            String[] requestDetails = body.split(",");
            if (requestDetails.length >= 6) {
                String subscriberName = requestDetails[0];
                String subscriberId = requestDetails[1];
                String bookTitle = requestDetails[2];
                String bookID = requestDetails[3];
                String requestDate = requestDetails[4];
                String returnDate = requestDetails[5];

                boolean isRetrieved = ConnectToDb.insertBorrowBook(dbConnection, body);
                if (isRetrieved) {
                    ConnectToDb.decreaseAvaliabeNumCopies(dbConnection, bookID);
                    client.sendToClient("Book retrieved successfully and has been added to your borrowed books list.");
                } else {
                    client.sendToClient("Retrieve action could not be processed.");
                }
            } else {
                throw new IllegalArgumentException("Invalid details provided: " + body);
            }
        } catch (Exception e) {
            client.sendToClient("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates the subscriber's history in the database with new data.
     * <p>
     * Expected message format: <br>
     * {@code UpdateHistoryInDB: <historyData>}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the data to update
     * @throws SQLException if a database access error occurs
     * @throws IOException  if sending a response to the client fails
     */
    private void updateHistoryInDB(ConnectionToClient client, String body) throws SQLException, IOException {
        ConnectToDb.updateHistoryInDB(dbConnection, body);
        client.sendToClient("History is updated");
    }

    /**
     * Updates subscriber information (phone, email) in the database.
     * <p>
     * Expected message format: <br>
     * {@code Update:subscriberID,phone,email}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the comma-separated list of ID, phone, and email
     * @throws SQLException if a database access error occurs
     * @throws IOException  if sending a response to the client fails
     */
    private void handleUpdateCase(ConnectionToClient client, String body) throws SQLException, IOException {
        String[] parts = body.split(",");
        if (parts.length == 3) {
            String subscriberId = parts[0].trim();
            String phone = parts[1].trim();
            String email = parts[2].trim();

            if (ConnectToDb.checkSubscriberExists(dbConnection, subscriberId)) {
                ConnectToDb.updateSubscriber(dbConnection, subscriberId, phone, email);
                client.sendToClient("Subscriber updated successfully.");
            } else {
                client.sendToClient("Subscriber ID does not exist.");
            }
        } else {
            client.sendToClient("Invalid Update command format. Expected 'Update:ID,Phone,Email'.");
        }
    }

    /**
     * Handles the IP case where the client first connects, verifying the server IP
     * and logging connection details.
     * <p>
     * Expected message format: <br>
     * {@code IP: <ipAddress>}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the IP address sent by the client
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleIPCase(ConnectionToClient client, String body) throws IOException {
        String clientIP = client.getInetAddress().getHostAddress();
        String clientHost = client.getInetAddress().getHostName();
        String connectionStatus = isOpen(client) ? "Connected" : "Disconnected";

        String IPMessege = "Client connected.\n"
                + "Client IP:" + clientIP + "\n"
                + "Client Hostname: " + clientHost + "\n"
                + "Connection Status: " + connectionStatus;

        outputInOutputStreamAndLog(IPMessege);

        try {
            // Retrieve the server's IP address
            String serverIP = InetAddress.getLocalHost().getHostAddress();
            // Compare with client-provided IP
            if (body.equals(serverIP)) {
                client.sendToClient("Client connected to IP:" + serverIP);
            } else {
                client.sendToClient("Could not connect to the server.");
            }
        } catch (IOException e) {
            System.err.println("Error communicating with the client: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            throw new IOException("An unexpected error occurred while handling the IP case.", e);
        }
    }

    /**
     * Handles fetching all book records for display on the client side.
     * <p>
     * Expected prefix: {@code GetBooks}
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the request body (unused in logic)
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleGetBooksCase(ConnectionToClient client, String body) throws IOException {
        try {
            List<String> booksData = ConnectToDb.fetchBooksData(dbConnection);
            if (booksData == null || booksData.isEmpty()) {
                client.sendToClient("returnedBookData:NoBooksFound");
            } else {
                String booksDataString = String.join(";", booksData);
                client.sendToClient("returnedBookData:" + booksDataString);
            }
        } catch (Exception e) {
            System.err.println("Error while processing GetBooks request: " + e.getMessage());
            client.sendToClient("returnedBookData:Error:CouldNotFetchBooks");
        }
    }

    /**
     * Fetches the borrowing history of a subscriber.
     * <p>
     * Expected prefix: {@code GetHistory:subscriberId}
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the subscriberId
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleMyHistoryData(ConnectionToClient client, String body) throws IOException {
        try {
            String historyData = ConnectToDb.fetchHistoryData(dbConnection, body);
            if (historyData == null || historyData.isEmpty()) {
                client.sendToClient("returnedBookData:NoBooksFound");
            } else {
                client.sendToClient("FetchedHistory:" + historyData);
            }
        } catch (Exception e) {
            System.err.println("Error while processing GetBooks request: " + e.getMessage());
            client.sendToClient("returnedBookData:Error:CouldNotFetchBooks");
        }
    }

    /**
     * Fetches detailed information for a single book based on its ID.
     * <p>
     * Expected prefix: {@code GetBookInfo:bookID}
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the book ID
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleGetBookInfoCase(ConnectionToClient client, String body) throws IOException {
        String bookId = body;
        try {
            String bookInfo = ConnectToDb.fetchBookInfo(dbConnection, bookId);
            if (bookInfo != null) {
                client.sendToClient("BookInfo:" + bookInfo);
            } else {
                client.sendToClient("BookInfo:NotFound");
            }
        } catch (Exception e) {
            System.err.println("Error while processing GetBookInfo request: " + e.getMessage());
            client.sendToClient("BookInfo:Error:CouldNotFetchBookInfo");
        }
    }

    /**
     * Inserts a new reservation for a book in the database.
     * <p>
     * Expected message format: <br>
     * {@code Reserve:subscriberId,bookName,reserveDate,bookId}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   comma-separated reservation details
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleReserveRequestCase(ConnectionToClient client, String body) throws IOException {
        String[] reserveParts = body.split(",");
        if (reserveParts.length == 4) {
            String subscriberId = reserveParts[0].trim();
            String bookName = reserveParts[1].trim();
            String reserveDate = reserveParts[2].trim();
            String bookId = reserveParts[3].trim();

            try {
                ConnectToDb.insertReservedBook(dbConnection, subscriberId, bookName, reserveDate, bookId);
                // increase reserved copies number
                ConnectToDb.incrementReservedCopiesNum(dbConnection, bookId);
                client.sendToClient("Reservation successfully processed for book: " + bookName);
            } catch (Exception e) {
                client.sendToClient("An error occurred while processing the reservation: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            client.sendToClient("Invalid reservation request format. Please provide the correct details.");
        }
    }

    /**
     * Inserts a new registration request (for new subscribers) into the database.
     * <p>
     * Expected message format: <br>
     * {@code RegisterRequest: RegisterId,RegisterName,RegisterEmail,RegisterPhone}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   comma-separated registration details
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleRegisterRequestCase(ConnectionToClient client, String body) throws IOException {
        String[] borrowParts = body.split(",");
        String RegisterId = borrowParts[0].trim();
        String RegisterName = borrowParts[1].trim();
        String RegisterEmail = borrowParts[2].trim();
        String RegisterPhone = borrowParts[3].trim();

        try {
            String borrowTime = "";
            String returnTime = "";
            String extendTime = "";
            // Check if subscriber already exists
            boolean Subscriberexists = ConnectToDb.checkSubscriberExists(dbConnection, RegisterId);
            if (Subscriberexists) {
                client.sendToClient("RegistrationFailed: Request for Register failed, ID " + RegisterId + " already exists.");
                return;
            }

            // Check if the request with this ID already exists
            boolean requestalreadyexist = ConnectToDb.checkIfrequestexists(dbConnection, RegisterId);
            if (requestalreadyexist) {
                client.sendToClient("RegistrationFailed: there is a request for registration already");
            } else {
                // Insert the request
                ConnectToDb.insertRequest(dbConnection, "Request For Register", RegisterId, RegisterName,
                        RegisterEmail, RegisterPhone, borrowTime, returnTime, extendTime);
                client.sendToClient("RegistrationSucceed: Request for Register successful!");
            }
        } catch (Exception e) {
            client.sendToClient("An error occurred while processing the Register request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inserts a new borrow request into the requests table for librarian approval.
     * <p>
     * Expected message format: <br>
     * {@code BorrowRequest: subscriberId,subscriberName,bookBorrowId,bookName,borrowDate,returnDate}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   comma-separated borrow request details
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleBorrowRequestCase(ConnectionToClient client, String body) throws IOException {
        String[] borrowParts = body.split(",");
        if (borrowParts.length == 6) {
            String subscriberId = borrowParts[0].trim();
            String subscriberName = borrowParts[1].trim();
            String bookId = borrowParts[2].trim();
            String bookName = borrowParts[3].trim();
            String borrowDate = borrowParts[4].trim();
            String returnDate = borrowParts[5].trim();

            try {
                String extendTime = "";
                boolean alreadyborrowed = ConnectToDb.bookalreadyborrowed(dbConnection, subscriberId, bookId);
                // Insert the request
                if(alreadyborrowed) {
                	client.sendToClient("Book already borrowed");
                	return;
                }
                ConnectToDb.insertRequest(dbConnection, "Borrow For Subscriber", subscriberId, subscriberName,
                        bookName, bookId, borrowDate, returnDate, extendTime);
                client.sendToClient("sucessfully inserted the request");
                // Decrease available copies
                ConnectToDb.decreaseAvaliabeNumCopies(dbConnection, bookId);
            } catch (Exception e) {
                client.sendToClient("An error occurred while processing the borrow request: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Fetches all borrowed books for a subscriber.
     * <p>
     * Expected prefix: {@code GetBorrowedBooks:subscriberId}
     *
     * @param client       the {@link ConnectionToClient} sending the request
     * @param subscriberId the subscriberId
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleGetBorrowedBooksCase(ConnectionToClient client, String subscriberId) throws IOException {
        try {
            List<String> borrowedBooks = ConnectToDb.fetchBorrowedBooksBySubscriberId(dbConnection, subscriberId);
            if (borrowedBooks.isEmpty()) {
                client.sendToClient("BorrowedBooks:NoBooksFound");
            } else {
                String response = String.join(";", borrowedBooks);
                client.sendToClient("BorrowedBooks:" + response);
            }
        } catch (Exception e) {
            client.sendToClient("BorrowedBooks:Error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fetches all reserved books for a subscriber.
     * <p>
     * Expected prefix: {@code GetReservedBooks:subscriberId}
     *
     * @param client       the {@link ConnectionToClient} sending the request
     * @param subscriberId the subscriberId
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleGetReservedBooksCase(ConnectionToClient client, String subscriberId) throws IOException {
        try {
            List<String> reservedBooks = ConnectToDb.fetchReservedBooksBySubscriberId(dbConnection, subscriberId);
            if (reservedBooks.isEmpty()) {
                client.sendToClient("ReservedBooks:NoBooksFound");
            } else {
                String response = String.join(";", reservedBooks);
                client.sendToClient("ReservedBooks:" + response);
            }
        } catch (Exception e) {
            client.sendToClient("ReservedBooks:Error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Finalizes a reservation success by deleting the first matching reservation record.
     * Decreases the reserved copies count for the book.
     * <p>
     * Expected message format: <br>
     * {@code ReserveSuccess:subscriberId,bookId}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   comma-separated subscriberId and bookId
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleReserveSuccessCase(ConnectionToClient client, String body) throws IOException {
        String[] borrowParts = body.split(",");
        if (borrowParts.length == 2) {
            String subscriberId = borrowParts[0].trim();
            String bookId = borrowParts[1].trim();

            String query = "SELECT * FROM reserved_books WHERE subscriber_id = ? AND ISBN = ?";

            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setString(1, subscriberId);
                stmt.setString(2, bookId);

                ResultSet resultSet = stmt.executeQuery();
                int smallestReserveId = Integer.MAX_VALUE;
                int reserveIdToDelete = -1;

                while (resultSet.next()) {
                    int reserveId = resultSet.getInt("reserve_id");
                    if (reserveId < smallestReserveId) {
                        smallestReserveId = reserveId;
                        reserveIdToDelete = reserveId;
                    }
                }

                if (reserveIdToDelete != -1) {
                    String deleteQuery = "DELETE FROM reserved_books WHERE reserve_id = ?";
                    try (PreparedStatement deleteStmt = dbConnection.prepareStatement(deleteQuery)) {
                        deleteStmt.setInt(1, reserveIdToDelete);
                        int rowsAffected = deleteStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            client.sendToClient("ReservedBooks:Success:Book borrowed successfully, reservation deleted.");
                            // Decrease reserved copies
                            ConnectToDb.decreaseReservedCopiesNum(dbConnection, bookId);
                        } else {
                            client.sendToClient("ReservedBooks:Error:Failed to delete reservation.");
                        }
                    }
                } else {
                    client.sendToClient("ReservedBooks:Error:No reservation found for the given subscriberId and bookId.");
                }
            } catch (Exception e) {
                client.sendToClient("ReservedBooks:Error:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Fetches all pending borrow requests from the database and sends them to the client.
     * <p>
     * Expected prefix: {@code FetchBorrowRequest}
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   (unused in logic)
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleFetchBorrowRequestCase(ConnectionToClient client, String body) throws IOException {
        try {
            String borrowRequests = ConnectToDb.fetchBorrowRequest(dbConnection);
            client.sendToClient("FetchedBorrowedBooks:" + borrowRequests);
        } catch (Exception e) {
            client.sendToClient("An error occurred while fetching the borrow request data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fetches all pending register requests from the database and sends them to the client.
     * <p>
     * Expected prefix: {@code FetchRegisterRequest}
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   (unused in logic)
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleFetchRegisterRequestCase(ConnectionToClient client, String body) throws IOException {
        try {
            String RegisterRequests = ConnectToDb.fetchRegisterRequest(dbConnection);
            client.sendToClient("FetchedRegisterRequests:" + RegisterRequests);
        } catch (Exception e) {
            client.sendToClient("An error occurred while fetching the Register Requests data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------
    //           LOGGING & INITIALIZATION METHODS
    // --------------------------------------------------------------

    /**
     * Appends a message to the server log file.
     *
     * @param message the message to log
     */
    private static void log(String message) {
        try (FileWriter writer = new FileWriter("src/logic/serverLog.txt", true)) {
            writer.write(message + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    /**
     * Outputs a message to the console and also appends it to the log file.
     *
     * @param msg the message to output and log
     */
    public static void outputInOutputStreamAndLog(String msg) {
        System.out.println(msg);
        log(msg);
    }

    /**
     * Initializes the log file by creating or clearing it at the start of a new server run.
     */
    private void initializeLogFile() {
        try {
            File logFile = new File("src/logic/serverLog.txt");
            File parentDir = logFile.getParentFile();

            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (logFile.exists()) {
                PrintWriter writer = new PrintWriter(logFile);
                writer.print(""); // Clear content
                writer.close();
            } else {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error initializing log file: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------
    //          REGISTER SUBSCRIBER & HANDLE RETURNS
    // --------------------------------------------------------------

    /**
     * Handles registering a new subscriber by updating the subscriber DB,
     * and then removing the associated registration request if successful.
     * <p>
     * Expected message format: <br>
     * {@code Handle register:someUnusedString,RegisterId,RegisterName,RegisterEmail,RegisterPhone}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   the comma-separated registration details
     */
    private void HandleRegisterOfSubscriber(ConnectionToClient client, String body) {
        String[] borrowParts = body.split(",");
        String RegisterId = borrowParts[1].trim();

        outputInOutputStreamAndLog("Received register request from client");
        try {
            String isRegisterSuccessfully = ConnectToDb.updateSubscriberDB(dbConnection, body);
            if (isRegisterSuccessfully.equals("True")) {
                ConnectToDb.deleteRegisterRequest(dbConnection, RegisterId);
                client.sendToClient("Subscriber registered successfully");
            } else {
                client.sendToClient("Error in registering subscriber");
            }
        } catch (Exception e) {
            System.err.println("Error while processing register request: " + e.getMessage());
        }
    }

    /**
     * Handles the process of returning a borrowed book, updates the DB,
     * and deletes the request if successful.
     * <p>
     * Expected message format: <br>
     * {@code Handle return:subscriberName,subscriberId,bookName,bookId,bookTime,returnTime}
     * </p>
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param body   comma-separated subscriber/book details
     */
    private void HandleBookReturn(ConnectionToClient client, String body) {
        try {
            String[] messageParts = body.split(",");
            if (messageParts.length != 6) {
                client.sendToClient("Error: Invalid input format. Expected format: SName,SID,BName,BID,BTime");
                return;
            }

            // parse messageParts
            String subscriberId = messageParts[1].trim();
            String bookid = messageParts[3].trim();

            String returnRequestStatus = ConnectToDb.returnbook(this.dbConnection, subscriberId, bookid);
            if ("Book returned successfully".equalsIgnoreCase(returnRequestStatus)) {
                boolean requestDeleted = ConnectToDb.deleteRequest(this.dbConnection, "Return For Subscriber", subscriberId, bookid);
                // tries to handle next reservation if any
                boolean bookUpdated = ConnectToDb.incrementBookCount(this.dbConnection, bookid);
                if (requestDeleted && bookUpdated) {
                	 client.sendToClient("Book return processed successfully and request was deleted. Book availability updated.");
                     ConnectToDb.updateFirstReservation(this.dbConnection, bookid);
                } else if (!requestDeleted && bookUpdated) {
                    client.sendToClient("return request wasnt deleted but book availability was updated");
                } else if(requestDeleted && !bookUpdated) {
                	client.sendToClient("request was deleted but book availability wasnt updated");
                }else {
                	 client.sendToClient("return request wasnt deleted and book availability wasnt updated");
                }
            } else {
                client.sendToClient("Return request status: " + returnRequestStatus);
            }
        } catch (Exception e) {
            try {
                client.sendToClient("An error occurred while processing the book return: " + e.getMessage());
            } catch (IOException ioException) {
                System.err.println("Failed to send error message to client: " + ioException.getMessage());
            }
            e.printStackTrace();
        }
    }

    /**
     * Fetches a single borrowed book's info (by ID) for a barcode scanner use case.
     * <p>
     * Expected prefix: {@code FetchBorrowedBooksForBarcodeScanner:bookID}
     *
     * @param client         the {@link ConnectionToClient} sending the request
     * @param borrowedBookID the ID of the borrowed book
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleGetBorrowedBooksCaseForBarcodeScanner(ConnectionToClient client, String borrowedBookID) throws IOException {
        try {
            String borrowedBook = ConnectToDb.fetchBookInfo(dbConnection, borrowedBookID);
            if (borrowedBook.isEmpty()) {
                client.sendToClient("BorrowedBooksForBarcodeScanner:NoBooksFound");
            } else {
                client.sendToClient("BorrowedBooksForBarcodeScanner:" + borrowedBook);
            }
        } catch (Exception e) {
            client.sendToClient("BorrowedBooksForBarcodeScanner:Error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------
    //     FETCHING SUBSCRIBER / FROZEN DATA FOR REPORTS
    // --------------------------------------------------------------

    /**
     * Fetches all subscriber data from the database and sends it to the client.
     * <p>
     * Expected prefix: {@code FetchAllSubscriberData}
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleFetchAllSubscriberData(ConnectionToClient client) throws IOException {
        client.sendToClient("AllSubscriberInformation:" + ConnectToDb.fetchAllData(dbConnection));
    }

    /**
     * Fetches all subscriber data (in a specific format suitable for reports)
     * and sends it to the client.
     * <p>
     * Expected prefix: {@code FetchAllSubscriberInformationForReports}
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleFetchAllSubscriberDataForReports(ConnectionToClient client) throws IOException {
        client.sendToClient("AllSubscriberInformationForReports:" + ConnectToDb.fetchAllDataForReports(dbConnection));
    }

    /**
     * Fetches all frozen subscriber data for reports.
     * <p>
     * Expected prefix: {@code FetchAllFrozenInformationForReports}
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @throws IOException if an I/O error occurs while sending a response
     */
    private void handleFetchAllFrozenDataForReports(ConnectionToClient client) throws IOException {
        client.sendToClient("AllFrozenInformationForReports:" + ConnectToDb.fetchAllFrozenDataForReports(dbConnection));
    }

    // --------------------------------------------------------------
    //   FETCHING CLOSEST RETURN DATE & HANDLE EXTENSIONS
    // --------------------------------------------------------------

    /**
     * Fetches the closest return date for a given ISBN (used when the book is currently all borrowed).
     * <p>
     * Expected prefix: {@code FetchClosestReturnDate:isbn}
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @param isbn   the book ISBN
     */
    private void handleFetchClosestReturnDate(ConnectionToClient client, String isbn) {
        try {
            String closestReturnDate = ConnectToDb.fetchClosestReturnDate(dbConnection, isbn);
            client.sendToClient("ClosestReturnDate:" + (closestReturnDate != null ? closestReturnDate : "Unavailable"));
        } catch (Exception e) {
            try {
                client.sendToClient("ClosestReturnDate:Error");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * Handles an event where a subscriber (or system) approves a new return date extension,
     * and updates the database accordingly.
     *
     * @param body the details of the new extension
     * @throws SQLException if a database access error occurs
     */
    private void handleBookReturnExtensionBySubscriber(String body) throws SQLException {
        ConnectToDb.updateExtensionApprovedBySubscriber(dbConnection, body);
    }

    /**
     * Fetches all new extended return dates (for librarians to see) and sends them to the client.
     * <p>
     * Expected prefix: {@code PullNewExtenstion}
     *
     * @param client the {@link ConnectionToClient} sending the request
     * @throws SQLException if a database access error occurs
     * @throws IOException  if an I/O error occurs while sending a response
     */
    private void handleNewReturnDatesForLibrarian(ConnectionToClient client) throws SQLException, IOException {
        String newReturnDates = ConnectToDb.pullNewExtendedReturnDates(dbConnection);
        client.sendToClient("ExtendedReturnDatesForsSubscriber:" + newReturnDates);
    }

    // --------------------------------------------------------------
    //          UTILITY & GETTER METHODS
    // --------------------------------------------------------------

    /**
     * Checks if the client's connection is open.
     *
     * @param client the {@link ConnectionToClient} to check
     * @return true if the connection is open, false otherwise
     */
    private boolean isOpen(ConnectionToClient client) {
        return client != null;
    }

    /**
     * Retrieves the active database connection used by the server.
     * 
     * @return the active {@link Connection}
     */
    public Connection getConnection() {
        return dbConnection;
    }
}
