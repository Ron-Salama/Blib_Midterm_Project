package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import common.ConnectToDb;
import logic.ServerTimeDiffController;
import logic.TaskScheduler;
//import logic.ClockController;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;


/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 */
public class EchoServer extends AbstractServer {
    // Class variables *************************************************
    final public static int DEFAULT_PORT = 5555;
    public static Connection taskSchedulerConnection; // Used to send SQL statements each day.
    
    public static ServerTimeDiffController clock = new ServerTimeDiffController();
    
    // Instance variables ***********************************************
    private Connection dbConnection; // Single DB connection
    
    private TaskScheduler taskScheduler; // Used to run daily tasks.
    // Constructors ****************************************************
    public EchoServer(int port) {
        super(port);
    }
   
    
    // Instance methods ************************************************
    @Override
    protected void serverStarted() {
    	initializeLogFile();
    	
    	taskScheduler = new TaskScheduler();
        taskScheduler.startDailyTasks(); // Run the thread to update the DB daily.

        outputInOutputStreamAndLog("Server listening for connections on port " + getPort());
        try {
            dbConnection = ConnectToDb.getConnection(); // Open the connection
            taskSchedulerConnection = dbConnection;
            outputInOutputStreamAndLog("Connected to the database.");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }
    
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

    @Override
    public void clientConnected(ConnectionToClient client) {
        // Display client connection details once when a client connects
        String clientIP = client.getInetAddress().getHostAddress();
        String clientHost = client.getInetAddress().getHostName();
        String connectionStatus = isOpen(client) ? "Connected" : "Disconnected";
        
        String IPMessege = "Client connected.\n"
        		+ "Client IP:" + clientIP + "\n"
        		+ "Client Hostname: " + clientHost + "\n"
        		+ "Connection Status: " + connectionStatus;
        
        outputInOutputStreamAndLog(IPMessege);
        		
        
        
        // Send a message to the client to inform them of the successful connection
        try {
            client.sendToClient("You have successfully connected to the server.");
        } catch (IOException e) {
            System.err.println("Error sending connection message to client: " + e.getMessage());
        }
    }

    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        outputInOutputStreamAndLog("Message received: " + msg + " from " + client);

        try {
            String message = (String) msg;
            int delimiterIndex = message.indexOf(":");
            if (delimiterIndex == -1) {
                client.sendToClient("Invalid command format.");
                return;
            }

            String prefix = message.substring(0, delimiterIndex).trim(); // Extract the prefix
            String body = message.substring(delimiterIndex + 1).trim(); // Extract the body after the prefix

            switch (prefix) {
                case "Fetch return request":
                this.HandleFetchreturnrequest(client, body);
                case "Fetch": // Handle Fetch:ID
                    handleFetchCase(client, body);
                    break;
                case "Update": // Handle Update:ID,Phone,Email
                	handleUpdateCase(client, body);
                    break;
                case "IsBookReserved": // Handle isBookReservered:BookId
                	handleIsBookReservedCase(client,body);
                	break;
                case "IP": // Handle IP:<Address>
                    handleIPCase(client, body);
                    break;
                case "GetBooks": // Handle GetBooks:
                	handleGetBooksCase(client, body);
                    break;
                case "GetBookInfo": // Handle GetBookInfo:BookId
                    handleGetBookInfoCase(client, body);
                    break;
                case "BorrowRequest": // Handle BorrowRequest
                	handleBorrowRequestCase(client, body);
                	break;
                case "Reserve": //Handle Reservations
                	handleReserveRequestCase(client, body);
                case "FetchBorrowRequest": // Handle FetchBorrowRequest
                	handleFetchBorrowRequestCase(client, body);
                    break;
                case "GetBorrowedBooks":
                    handleGetBorrowedBooksCase(client, body); // body contains the subscriber_id
                    break;
                case "UpdateReturnDate":
                	handleUpdateReturnDate(client, body); // body contians the borrowId
                	break;
                case "RegisterRequest": // Handle RegisterRequest
                	handleRegisterRequestCase(client, body);
                    break;
                case "FetchRegisterRequest": // Handle FetchBorrowRequest
                	handleFetchRegisterRequestCase(client, body);
                    break;
                case "GetHistory": //handle GetHistory
                	handleMyHistoryData(client, body);
                	 break;
                case "SubmitBorrowRequest": 
                	SubmitBorrowRequest(client, body);
                	break;
                case "Return request":
                	handlereturnrequest(client,body);
                    break;
                case "UpdateCopiesOfBook":
                	updateCopiesOfBook(client, body);
                	break;
                case "Handle return":
                	HandleBookReturn(client,body);
                	break;
                default: // Handle unknown commands
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
    private void handleIsBookReservedCase(ConnectionToClient client, String body) throws IOException {
        try {
            String ISBN = body.trim(); // The ISBN is sent in the message body
            boolean isReserved = ConnectToDb.isBookReserved(dbConnection, ISBN); // Updated method call

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

    
    private void handleUpdateReturnDate(ConnectionToClient client, String body) {
    	
        String[] parts = body.split(",");
        int borrowID = Integer.parseInt(parts[0].trim());
        String extendedReturnDate = parts[1].trim();
        System.out.println(parts[0]);
        System.out.println(parts[1]);
    	ConnectToDb.updateReturnDateAfterExtension(borrowID, extendedReturnDate ,dbConnection );
    }
    
    private void HandleFetchreturnrequest(ConnectionToClient client, String body) throws IOException {
        try {
            String ReturnRequests = ConnectToDb.fetchReturnRequest(this.dbConnection);
            client.sendToClient("FetchedReturnRequest:" + ReturnRequests);
        } catch (Exception e) {
            client.sendToClient("An error occurred while fetching the return request data: " + e.getMessage());
            e.printStackTrace();
        }

    }
		

    private void handlereturnrequest(ConnectionToClient client, String body) throws IOException {
        System.out.println("Processing return request");

        try {
            int idStartIndex = body.indexOf("Subscriber ID is") + 17;
            int idEndIndex = body.indexOf(" ", idStartIndex);
            String subscriberId = body.substring(idStartIndex, idEndIndex).trim();
            int nameStartIndex = idEndIndex + 1;
            int nameEndIndex = body.indexOf("Borrow info:");
            String subscribername = body.substring(nameStartIndex, nameEndIndex).trim();
            int borrowInfoStartIndex = body.indexOf("Borrow info:") + 13;
            String borrowInfo = body.substring(borrowInfoStartIndex).trim();
            String[] bookInfo = borrowInfo.split(" ");
            String bookID = bookInfo[0];
            StringBuilder bookNameBuilder = new StringBuilder();

            for(int i = 1; i < bookInfo.length - 2; ++i) {
                bookNameBuilder.append(bookInfo[i]).append(" ");
            }

            String bookName = bookNameBuilder.toString().trim();
            String subject = bookInfo[bookInfo.length - 2];
            String timeLeft = bookInfo[bookInfo.length - 1];
            String requestType = "Return For Subscriber";
            String borrowTime = "temp";
            String extendTime = "temp";
            ConnectToDb.insertRequest(this.dbConnection, requestType, subscriberId, subscribername, bookName, bookID, borrowTime, timeLeft, extendTime);
            client.sendToClient("Return request submitted, awaiting librarian approval.");
        } catch (SQLException e) {
            e.printStackTrace();
            client.sendToClient("Error inserting return request.");
        } catch (Exception e) {
            e.printStackTrace();
            client.sendToClient("Error processing the return request message.");
        }

    }


    private void handleFetchCase(ConnectionToClient client, String body) throws SQLException, IOException {
    	String identifier = body;

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
    private void SubmitBorrowRequest(ConnectionToClient client, String body) throws SQLException, IOException {
       ConnectToDb.insertBorrowBook(dbConnection, body);
       client.sendToClient("Borrowed Book Request Accepted & inserted into borrowed_book db");
    }
    
    
    private void updateCopiesOfBook(ConnectionToClient client, String body) throws SQLException, IOException {
    	ConnectToDb.updateCopiesOfBook(dbConnection, body);
        client.sendToClient("Number of books updated");
    }
    
    
    
    private void handleUpdateCase(ConnectionToClient client, String body) throws SQLException, IOException {

    	String[] parts = body.split(",");

        if (parts.length == 3) {
            String subscriberId = parts[0].trim();
            String phone = parts[1].trim();
            String email = parts[2].trim();
            System.out.println("this:"+subscriberId+":this");
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

    private void handleIPCase(ConnectionToClient client, String body) throws IOException   {
    	String clientIP = body;
        String serverIP = InetAddress.getLocalHost().getHostAddress();

        if (clientIP.equals(serverIP)) {
            client.sendToClient("Client connected to IP: " + clientIP);
        } else {
            client.sendToClient("Could not connect to the server. The IP: " + clientIP + " does not match the IP of the server.");
        }
    }
    
    private void handleGetBooksCase(ConnectionToClient client, String body) throws IOException {
    	outputInOutputStreamAndLog("Received GetBooks request from client");

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
    
    
    
    
    
    private void handleMyHistoryData(ConnectionToClient client, String body) throws IOException {
    	outputInOutputStreamAndLog("Received GetHistory request from client");
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
    
    
    
    
    private void handleGetBookInfoCase(ConnectionToClient client, String body) throws IOException {
    	String bookId = body; // The body contains the BookId
        try {
            // Fetch book information from the database
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
    
    
    //********************************************************************************************
    //********************************************************************************************
    //********************************************************************************************
    //********************************************************************************************
    //********************************************************************************************
    private void handleReserveRequestCase(ConnectionToClient client, String body) throws IOException {
        outputInOutputStreamAndLog("Received Reserve Request from client");
        String[] reserveParts = body.split(",");

        if (reserveParts.length == 4) { 
            String subscriberId = reserveParts[0].trim();
            String subscriberName = reserveParts[1].trim();
            String reservedBookId = reserveParts[2].trim();
            String bookName = reserveParts[3].trim();

            try {
                // All time-related fields are set to empty strings for reservation except for reserved time
                String borrowDate = ""; // Leave empty if not used
                String returnDate = ""; // Leave empty if not used
                String extendTime = ""; // Leave empty if not used

                // Send the data to insertRequest for reservation
                ConnectToDb.insertRequest(dbConnection, 
                                           "Reserve For Subscriber", // requestType
                                           subscriberId,             // requestedByID
                                           subscriberName,           // requestedByName
                                           bookName,                 // bookName
                                           reservedBookId,            // bookId
                                           borrowDate,               // borrowDate (null for reservation)
                                           returnDate,               // returnDate (null for reservation)
                                           extendTime);              // extendTime (null for reservation)

                // Decrease available copies (if needed)
                ConnectToDb.incrementReservedCopiesNum(dbConnection, reservedBookId);
                
                client.sendToClient("Reservation successfully processed for book: " + bookName);
            } catch (Exception e) {
                client.sendToClient("An error occurred while processing the reservation: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            client.sendToClient("Invalid reservation request format. Please provide the correct details.");
        }
    }
    
    
    
    //********************************************************************************************
    //********************************************************************************************
    //********************************************************************************************
    //********************************************************************************************
    //********************************************************************************************
    
    
    
    private void handleRegisterRequestCase(ConnectionToClient client, String body) throws IOException {
   	 outputInOutputStreamAndLog("Received RegisterRequestCase from client");
        String[] borrowParts = body.split(",");

        String RegisterId = borrowParts[0].trim();
        String RegisterName = borrowParts[1].trim();
        String RegisterEmail = borrowParts[2].trim();
        String RegisterPhone = borrowParts[3].trim();

        try {
            // Example values for the time-related fields (can be empty strings if not needed)
            String borrowTime = ""; // You can pass the actual borrow time if you have it
            String returnTime = ""; // Leave empty if not used
            String extendTime = ""; // Leave empty if not used

            // Check if the RegisterId already exists in the database
            boolean idExists = ConnectToDb.checkIfIdExists(dbConnection, RegisterId);

            if (idExists) {
                // If the ID already exists, send a response to the client
                client.sendToClient("RegistrationFailed: Request for Register failed, ID " + RegisterId + " already exists.");
            } else {
                // If the ID doesn't exist, proceed with the insertRequest
                ConnectToDb.insertRequest(dbConnection, 
                                           "Request For Register", // requestType
                                           RegisterId,             // requestedByID
                                           RegisterName,           // requestedByName
                                           RegisterEmail,          // bookName
                                           RegisterPhone,          // bookId
                                           borrowTime,             // borrowTime (empty string if not available)
                                           returnTime,             // returnTime (empty string if not available)
                                           extendTime);            // extendTime (empty string if not available)

                client.sendToClient("RegistrationSucceed: Request for Register successful!");
            }
        } catch (Exception e) {
            client.sendToClient("An error occurred while processing the Register request: " + e.getMessage());
            e.printStackTrace();
        }

        
   }
    
    private void handleBorrowRequestCase(ConnectionToClient client, String body) throws IOException {
    	 outputInOutputStreamAndLog("Received BorrowRequest from client");
         String[] borrowParts = body.split(",");
         
         if (borrowParts.length == 6) {
             String subscriberId = borrowParts[0].trim();
             String subscriberName = borrowParts[1].trim();
             String bookBorrowId = borrowParts[2].trim();
             String bookName = borrowParts[3].trim();
             String borrowDate = borrowParts[4].trim();
             String returnDate = borrowParts[5].trim(); 
             

             try {
                 // Example values for the time-related fields (can be empty strings if not needed)
            	 
                 String extendTime = ""; // Leave empty if not used

                 // Send the data to insertRequest
                 ConnectToDb.insertRequest(dbConnection, 
                                            "Borrow For Subscriber", // requestType
                                            subscriberId,             // requestedByID
                                            subscriberName,           // requestedByName
                                            bookName,                 // bookName
                                            bookBorrowId,             // bookId
                                            borrowDate,               // borrowDate (empty string if not available)
                                            returnDate,               // returnDate (empty string if not available)
                                            extendTime);              // extendTime (empty string if not available)
                 
                 ConnectToDb.decreaseNumCopies(dbConnection,bookBorrowId);
             } catch (Exception e) {
                 client.sendToClient("An error occurred while processing the borrow request: " + e.getMessage());
                 e.printStackTrace();
             }
         }
    }
    private void handleGetBorrowedBooksCase(ConnectionToClient client, String subscriberId) throws IOException {
        try {
            List<String> borrowedBooks = ConnectToDb.fetchBorrowedBooksBySubscriberId(dbConnection, subscriberId);

            if (borrowedBooks.isEmpty()) {
                client.sendToClient("BorrowedBooks:NoBooksFound");
            } else {
                String response = String.join(";", borrowedBooks); // Format list into a single string
                client.sendToClient("BorrowedBooks:" + response);
            }
        } catch (Exception e) {
            client.sendToClient("BorrowedBooks:Error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    
    private void handleFetchBorrowRequestCase(ConnectionToClient client, String body) throws IOException{
    	outputInOutputStreamAndLog("Received FetchBorrowRequest from client"); 

         try {
        	 String borrowRequests = ConnectToDb.fetchBorrowRequest(dbConnection);

             client.sendToClient("FetchedBorrowedBooks:" + borrowRequests);
         } catch (Exception e) {
             client.sendToClient("An error occurred while fetching the borrow request data: " + e.getMessage());
             e.printStackTrace();
         }
    }
    
    private void handleFetchRegisterRequestCase(ConnectionToClient client, String body) throws IOException{
    	outputInOutputStreamAndLog("Received FetchBorrowRequest from client"); 

         try {
             String RegisterRequests = ConnectToDb.fetchRegisterRequest(dbConnection);
             client.sendToClient("FetchedRegisterRequests:"+RegisterRequests);
         } catch (Exception e) {
             client.sendToClient("An error occurred while fetching the Register Requests data: " + e.getMessage());
             e.printStackTrace();
         }
    }
    
    private static void log(String message) {
    	// Append the message to the log file
        try (FileWriter writer = new FileWriter("src/logic/serverLog.txt", true)) { // 'true' enables append mode
            writer.write(message + System.lineSeparator()); // Add a new line after each message
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
    
    public static void outputInOutputStreamAndLog(String msg){
    	System.out.println(msg);
    	log(msg);
    }
    
    private void initializeLogFile() {
        try {
            File logFile = new File("src/logic/serverLog.txt");
            File parentDir = logFile.getParentFile();
            
            // Create directories if they do not exist
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // Flush log file for a new server run
            if (logFile.exists()) {
                PrintWriter writer = new PrintWriter(logFile);
                writer.print(""); // Clear the content
                writer.close();
            } else {
                // Create the file if it does not exist
                logFile.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error initializing log file: " + e.getMessage());
        }
    }
    
    private void HandleBookReturn(ConnectionToClient client, String body) {
        try {
            // Split the body by commas to extract individual parts (subscriber name, ID, book name, ID, and time)
            String[] messageParts = body.split(",");
            if (messageParts.length != 6) {
                client.sendToClient("Error: Invalid input format. Expected format: SName,SID,BName,BID,BTime");
                return;
            }

            String subscriberName = messageParts[0].trim();
            String subscriberId = messageParts[1].trim();
            String bookName = messageParts[2].trim();
            String bookid = messageParts[3].trim();
            String bookTime = messageParts[4].trim();
            String returnTime = messageParts[5].trim();
            System.out.println("Parsed Subscriber Name: " + subscriberName + ", Subscriber ID: " + subscriberId + ", Book Name: " + bookName + ", Book ID: " + bookid + ", Book Time: " + bookTime +", return Time: "+ returnTime);
            String returnRequestStatus = ConnectToDb.returnbook(this.dbConnection, subscriberId, bookid);
            if ("Book returned successfully".equalsIgnoreCase(returnRequestStatus)) {
                boolean requestDeleted = ConnectToDb.deleteRequest(this.dbConnection, subscriberId, bookid);
                if (!requestDeleted) {
                    client.sendToClient("Warning: Book returned successfully, but the request could not be removed from the request table.");
                } else {
                    client.sendToClient("return request was commited and deleted");
                }

                boolean bookUpdated = ConnectToDb.incrementBookCount(this.dbConnection, bookid);
                if (bookUpdated) {
                    client.sendToClient("Book return processed successfully. Book availability updated.");
                } else {
                    client.sendToClient("Warning: Book returned successfully, but the book availability could not be updated.");
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




    private boolean isOpen(ConnectionToClient client) {
        return client != null;
    }
    
    public Connection getConnection() {
    	return dbConnection;
    }

}
