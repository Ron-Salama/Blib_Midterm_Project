import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import common.ConnectToDb;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 */
public class EchoServer extends AbstractServer 
{
    // Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;

    // Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port) {
        super(port);
    }

    // Instance methods ************************************************

    /**
     * This method handles any messages received from the client.
     *
     * @param msg The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
    	System.out.println("Message received: " + msg + " from " + client);

        if ("Show Subscribers DB".equals(msg)) {
            try (Connection conn = ConnectToDb.getConnection()) {
                List<String> data = ConnectToDb.fetchAllData(conn);
                if (data.isEmpty()) {
                    client.sendToClient("No data found in the database.");
                } else {
                    for (String row : data) {
                        client.sendToClient(row);
                    }
                }
            } catch (SQLException e) {
                try {
                    client.sendToClient("Database error: " + e.getMessage());
                } catch (IOException ioException) {
                    System.out.println("Error sending message to client: " + ioException.getMessage());
                }
                System.out.println("Database error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error sending message to client: " + e.getMessage());
            }
        }
        else if ("Edit Subscribers DB".equals(msg)) {
            System.out.println("Waiting for subscriber ID...");
        }
        else if (((String) msg).startsWith("Update:")) { // Format: Update:ID,Phone,Email
            String[] parts = msg.toString().substring(7).split(","); // Remove "Update:" and split
            if (parts.length == 3) {
                String subscriberId = parts[0].trim();
                String phone = parts[1].trim();
                String email = parts[2].trim();

                // Check if the subscriber ID exists in the database
                try (Connection conn = ConnectToDb.getConnection()) {
                    if (ConnectToDb.checkSubscriberExists(conn, subscriberId)) {
                        // Subscriber exists, update the details
                        ConnectToDb.updateSubscriber(conn, subscriberId, phone, email);
                        client.sendToClient("Subscriber updated successfully.");
                    } else {
                        // Subscriber ID does not exist
                        client.sendToClient("Error: Subscriber ID does not exist.");
                    }
                } catch (SQLException | IOException e) {
                    System.out.println("Error handling subscriber update: " + e.getMessage());
                    try {
                        client.sendToClient("Database error: " + e.getMessage());
                    } catch (IOException ioException) {
                        System.out.println("Error sending message to client: " + ioException.getMessage());
                    }
                }
            } else {
                try {
					client.sendToClient("Error: Invalid message format. Expected 'Update:ID,Phone,Email'.");
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        } else {
            this.sendToAllClients(msg); // Default behavior
        }
    }

    /**
     * This method overrides the one in the superclass. Called
     * when the server starts listening for connections.
     */
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }

    /**
     * This method overrides the one in the superclass. Called
     * when the server stops listening for connections.
     */
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }

    // Class methods ***************************************************

    /**
     * This method is responsible for the creation of 
     * the server instance (there is no UI in this phase).
     *
     * @param args[0] The port number to listen on. Defaults to 5555 
     *          if no argument is entered.
     */
    public static void main(String[] args) {
        int port = 0; // Port to listen on

        try {
            port = Integer.parseInt(args[0]); // Get port from command line
        } catch (Throwable t) {
            port = DEFAULT_PORT; // Set port to 5555
        }

        EchoServer sv = new EchoServer(port);

        try {
            sv.listen(); // Start listening for connections
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }
    }
}
