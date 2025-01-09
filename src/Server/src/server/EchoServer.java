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
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;


/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 */
public class EchoServer extends AbstractServer {
    // Class variables *************************************************
    final public static int DEFAULT_PORT = 5555;

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
                    break;

                case "Update": // Handle Update:ID,Phone,Email
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
                    break;

                case "IP": // Handle IP:<Address>
                    String clientIP = body;
                    String serverIP = InetAddress.getLocalHost().getHostAddress();

                    if (clientIP.equals(serverIP)) {
                        client.sendToClient("Client connected to IP: " + clientIP);
                    } else {
                        client.sendToClient("Could not connect to the server. The IP: " + clientIP + " does not match the IP of the server.");
                    }
                    break;

                case "GetBooks": // Handle GetBooks:
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
                    break;
                case "GetBookInfo": // Handle GetBookInfo:BookId
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
    

    private boolean isOpen(ConnectionToClient client) {
        return client != null;
    }
}
