package client;

import gui.IPInputWindow.IPInputController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ClientUI extends Application {
    public static ClientController chat; // only one instance
    final public static int DEFAULT_PORT = 5555;
    public static boolean isIPValid;
    
    public static void main(String args[]) throws Exception {   
        launch(args);
    }

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
        
        // Retrieve the IP from the system property (if any) or use a default IP
        String ip = System.getProperty("server.ip");  // default to localhost if no property is set
        
        // Initialize the client controller with the provided IP and default port
        chat = new ClientController(ip, DEFAULT_PORT);
    }
    
    private void loadIcon(Stage primaryStage) {
        String iconRelativePath = "/assets/BLib_Icon.png"; 

        // Load the icon image
        Image icon = new Image(getClass().getResourceAsStream(iconRelativePath));
        
        // Set the icon for the primary stage.
        primaryStage.getIcons().add(icon);
   }
}
