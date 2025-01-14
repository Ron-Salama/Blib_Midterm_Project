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
    /*
    private ClockController clock = new ClockController();
    */
    // Instance variables ***********************************************
    private Connection dbConnection; // Single DB connection

    // Constructors ****************************************************
    public EchoServer(int port) {
        super(port);
    }
    
    // Instance methods ************************************************
    @Override
    protected void serverStarted() {
    	initializeLogFile();

        outputInOutputStreamAndLog("Server listening for connections on port " + getPort());
        try {
            dbConnection = ConnectToDb.getConnection(); // Open the connection
            outputInOutputStreamAndLog("Connected to the database.");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }
    
    @Override
    protected void serverStopped() {
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
                case "Fetch": // Handle Fetch:ID
                    handleFetchCase(client, body);
                    break;
                case "Update": // Handle Update:ID,Phone,Email
                	handleUpdateCase(client, body);
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
                /*case "GetDate": // Handle GetDate
                	handleGetDate(client, body);
                	*/
                case "GetHistory": //handle GetHistory
                	handleMyHistoryData(client, body);
                	 break;
                case "SubmitBorrowRequest": 
                	SubmitBorrowRequest(client, body);
                	break;
                case "Return request":
                	handlereturnrequest(client,body);
                	break;
                case "Handle return":
                	HandleBookReturn(client,body);
                	break;
                case "Fetch return request":
                	HandleFetchreturnrequest(client,body);
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
    
    private void handleUpdateReturnDate(ConnectionToClient client, String body) {
    	
    }
    
    private void HandleFetchreturnrequest(ConnectionToClient client, String body) throws IOException {
        try {
       	 String ReturnRequests = ConnectToDb.fetchReturnRequest(dbConnection);

            client.sendToClient("FetchedReturnRequest:" + ReturnRequests);
        } catch (Exception e) {
            client.sendToClient("An error occurred while fetching the return request data: " + e.getMessage());
            e.printStackTrace();
        }
   }
		

	private void handlereturnrequest(ConnectionToClient client, String body) throws IOException {
        System.out.println("Processing return request");

        // Expected message format: "Return request: Subscriber ID is 1 Yuval Borrow info: 1 1984 Fiction 0"

        try {
            // Extract the Subscriber ID and Name
            int idStartIndex = body.indexOf("Subscriber ID is") + 17; // 17 = length of "Subscriber ID is "
            int idEndIndex = body.indexOf(" ", idStartIndex); // Find the space after the ID
            String subscriberId = body.substring(idStartIndex, idEndIndex).trim();

            int nameStartIndex = idEndIndex + 1; // Start after the space following the ID
            int nameEndIndex = body.indexOf("Borrow info:"); // End before "Borrow info:"
            String subscribername = body.substring(nameStartIndex, nameEndIndex).trim();

            // Extract the Borrow info
            int borrowInfoStartIndex = body.indexOf("Borrow info:") + 13; // 13 = length of "Borrow info: "
            String borrowInfo = body.substring(borrowInfoStartIndex).trim(); // Remaining part is borrow info

            // Split borrow info to extract individual fields
            String[] bookInfo = borrowInfo.split(" ");
            String borrowId = bookInfo[0]; // First part (e.g., "1")
            String bookName = bookInfo[1]; // Second part (e.g., "1984")
            String subject = bookInfo[2];  // Third part (e.g., "Fiction")
            String timeLeft = bookInfo[3]; // Fourth part (e.g., "0")

            // Create the return request in the database
            String requestType = "Return For Subscriber";  // Specify the type of request
            String borrowTime = "temp";  // Placeholder value
            String extendTime = "temp";  // Placeholder value

            // Insert the request into the database
            ConnectToDb.insertRequest(
                dbConnection, requestType, subscriberId, subscribername, bookName, borrowId, borrowTime, timeLeft, extendTime
            );

            // Notify the client that the request was submitted
            client.sendToClient("Return request submitted, awaiting librarian approval.");
        } catch (SQLException e) {
            e.printStackTrace();
            client.sendToClient("Error inserting return request.");
        } catch (Exception e) {
            e.printStackTrace();
            client.sendToClient("Error processing the return request message.");
        }
    }


/*
	private void handleGetDate(ConnectionToClient client, String body) throws IOException {
    	String currentDate = clock.getCurrentDate();
    	client.sendToClient(currentDate);
    }
    */
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
    
    private void log(String message) {
    	// Append the message to the log file
        try (FileWriter writer = new FileWriter("src/logic/serverLog.txt", true)) { // 'true' enables append mode
            writer.write(message + System.lineSeparator()); // Add a new line after each message
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
    
    private void outputInOutputStreamAndLog(String msg){
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
            // Log the raw body for debugging
            System.out.println("Raw body received: " + body);

            // Split the body by commas to extract individual parts (subscriber name, ID, book name, ID, and time)
            String[] messageParts = body.split(",");

            // Validate that we have the expected number of parts (5 fields in total)
            if (messageParts.length != 5) {
                client.sendToClient("Error: Invalid input format. Expected format: SName,SID,BName,BID,BTime");
                return;
            }

            // Extract the relevant data from the split parts
            String subscriberName = messageParts[0].trim();  // Subscriber's name
            String subscriberId = messageParts[1].trim();    // Subscriber's ID
            String bookName = messageParts[2].trim();        // Book's name
            String borrowId = messageParts[3].trim();          // borrowID
            String bookTime = messageParts[4].trim();        // Book's time (or time remaining)

            // Log the extracted values for debugging
            System.out.println("Parsed Subscriber Name: " + subscriberName + ", Subscriber ID: " + subscriberId + 
                               ", Book Name: " + bookName + ", Borrow ID: " + borrowId + ", Book Time: " + bookTime);

            // Process the return request via the database connection
            String returnRequestStatus = ConnectToDb.returnbook(dbConnection, subscriberId, borrowId);

            // Send the return request status back to the client
            client.sendToClient("Return request status: " + returnRequestStatus);
        } catch (Exception e) {
            try {
                // Handle unexpected exceptions and send error details to the client
                client.sendToClient("An error occurred while processing the book return: " + e.getMessage());
            } catch (IOException ioException) {
                System.err.println("Failed to send error message to client: " + ioException.getMessage());
            }
            e.printStackTrace(); // Log the exception for server-side debugging
        }
    }

    private boolean isOpen(ConnectionToClient client) {
        return client != null;
    }

}
