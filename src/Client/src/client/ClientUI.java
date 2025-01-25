package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The {@code ClientUI} class serves as the entry point for the JavaFX client application.
 * It initializes the user interface and manages the creation of the client controller for
 * establishing a connection to the server.
 */
public class ClientUI extends Application {
	
	/** The single instance of the {@code ClientController} used for communication. */
    public static ClientController chat;
    
    /** The default port number for server connection. */
    final public static int DEFAULT_PORT = 5555;
    
    /**
     * The main method serves as the starting point of the application.
     *
     * @param args command-line arguments passed during application launch
     * @throws Exception if an error occurs during application execution
     */
    public static void main(String args[]) throws Exception {   
        launch(args);
    }

    /**
     * Initializes and displays the primary stage of the application.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if an error occurs during stage initialization
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the IP Input Frame (initial frame to enter IP)
        Parent root = FXMLLoader.load(getClass().getResource("/gui/IPInputWindow/IPInputFrame.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/IPInputWindow/IPInputFrame.css").toExternalForm());
        loadIcon(primaryStage);
        primaryStage.setTitle("IP Input");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Loads and sets the application icon for the primary stage.
     *
     * @param primaryStage the primary stage for this application
     */
    private void loadIcon(Stage primaryStage) {
        String iconRelativePath = "/assets/BLib_Icon.png"; 

        // Load the icon image
        Image icon = new Image(getClass().getResourceAsStream(iconRelativePath));
        
        // Set the icon for the primary stage.
        primaryStage.getIcons().add(icon);
   }
    
    /**
     * Creates a new {@code ClientController} instance to establish a connection with the server.
     *
     * @param ip the IP address of the server
     */ 
   public static void createChatConnection(String ip) {
	   chat = new ClientController(ip, DEFAULT_PORT);
   }
}
