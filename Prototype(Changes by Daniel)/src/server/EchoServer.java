package server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
        System.out.println("Server listening for connections on port " + getPort());
        try {
            dbConnection = ConnectToDb.getConnection(); // Open the connection
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }

    @Override
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
        if (dbConnection != null) {
            try {
                dbConnection.close(); // Close the connection
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.out.println("Error closing the database connection: " + e.getMessage());
            }
        }
    }

    @Override
    public void clientConnected(ConnectionToClient client) {
        // Display client connection details once when a client connects
        String clientIP = client.getInetAddress().getHostAddress();
        String clientHost = client.getInetAddress().getHostName();
        String connectionStatus = isOpen(client) ? "Connected" : "Disconnected";

        System.out.println("Client connected:");
        System.out.println("Client IP: " + clientIP);
        System.out.println("Client Hostname: " + clientHost);
        System.out.println("Connection Status: " + connectionStatus);
        // Send a message to the client to inform them of the successful connection
        try {
            client.sendToClient("You have successfully connected to the server.");
        } catch (IOException e) {
            System.out.println("Error sending connection message to client: " + e.getMessage());
        }
    }

    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Message received: " + msg + " from " + client);

        try {
            if (((String) msg).startsWith("Fetch:")) { // Format: Fetch:ID
                String subscriberId = msg.toString().substring(6).trim(); // Extract ID after "Fetch:"
                // Check if subscriber ID exists in the database
                if (ConnectToDb.checkSubscriberExists(dbConnection, subscriberId)) {
                    String subscriberData = ConnectToDb.fetchSubscriberData(dbConnection, subscriberId);
                    client.sendToClient(subscriberData);
                } else {
                    client.sendToClient("Subscriber ID does not exist.");
                }
            } else if (((String) msg).startsWith("Update:")) { // Format: Update:ID,Phone,Email
                String[] parts = msg.toString().substring(7).split(","); // Remove "Update:" and split
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
                    client.sendToClient("Invalid message format. Expected 'Update:ID,Phone,Email'.");
                }
            } else {
                client.sendToClient("Unknown command.");
            }

        } catch (SQLException | IOException e) {
            System.out.println("Error handling client message: " + e.getMessage());
            try {
                client.sendToClient("Server error: " + e.getMessage());
            } catch (IOException ioException) {
                System.out.println("Error sending message to client: " + ioException.getMessage());
            }
        }
    }

    private boolean isOpen(ConnectionToClient client) {
        return client != null;
    }
}
