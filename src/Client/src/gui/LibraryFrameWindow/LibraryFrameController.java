package gui.LibraryFrameWindow;

import java.io.IOException;

import client.ChatClient;
import client.ClientUI;
import gui.MainMenu.MainMenuController;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.Subscriber;


public class LibraryFrameController   {
	private LibraryFrameController lfc;	
	
	@FXML
	private Button btnExit = null;
	
	@FXML
	private Button btnSend = null;
	
	@FXML
	private TextField idtxt;
	
	@FXML
	private Label awaitingLoginText;
	
	
	private String getID() {
		return idtxt.getText();
	}
	
	/*public void Send(ActionEvent event) throws Exception {
	    String id;
	    FXMLLoader loader = new FXMLLoader();
	    
	    id = getID();
	    if (id.trim().isEmpty()) {
	        System.out.println("You must enter an id number");  
	    } else {
	        // Send the ID to the server through the ClientController
	        ClientUI.chat.accept("Fetch:" + id);  // This will trigger the server-side action to search for the subscriber
	        
	        // Check if the subscriber exists and if the ID was found
	        if (ChatClient.s1.getSubscriber_id() == -1) {
	            System.out.println("Subscriber ID Not Found");
	            // You may want to display an error message to the user here
	        } else {
	            System.out.println("Subscriber ID does not exist.");

	            // Hide the current window
	            ((Node) event.getSource()).getScene().getWindow().hide(); 

	            // Load the SubscriberForm window
	            Stage primaryStage = new Stage();
	            loader.setLocation(getClass().getResource("/gui/SubscriberForm.fxml"));
	            Pane root = loader.load();
	            SubscriberFormController subscriberFormController = loader.getController(); 

	            // Load the subscriber details into the form
	            subscriberFormController.loadSubscriber(ChatClient.s1);

	            // Create and display the new scene
	            Scene scene = new Scene(root);
	            scene.getStylesheets().add(getClass().getResource("/gui/SubscriberForm.css").toExternalForm());
	            primaryStage.setTitle("Subscriber Management Tool");
	            primaryStage.setScene(scene);        
	            primaryStage.show();
	        }
	    }
	}*/
	
	
	public void Send(ActionEvent event) throws Exception {
	    String id = getID();
	    
	    if (id.trim().isEmpty()) {
	        System.out.println("You must enter an ID number");
	        awaitingLoginText.setStyle("-fx-text-fill: red;");
	        awaitingLoginText.setText("You must enter an ID number.");
	        return; // Early exit if ID is not provided
	    }
	    ChatClient.l1.setLibrarian_id(-1);
	    ChatClient.s1.setSubscriber_id(-1);
	    // Send the request and handle the response asynchronously
	    ClientUI.chat.accept("Fetch:" + id);

	    // Schedule a task to check the response without blocking
	    PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(0.1)); // Adjusted to 1 second for checking
	    pause.setOnFinished(e -> {
			try {
				handleResponse(event);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	    pause.play();
	}
    private void navigateToLibrarianMenu(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LibrarianWindow/LibrarianFrame.fxml"));
        Parent root = loader.load();

        // Set up the scene and stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianWindow/LibrarianFrame.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Library Management Tool");
        stage.show();
    }
	private void handleResponse(ActionEvent event) throws Exception {
		// Debugging logs to check both IDs
	    System.out.println("Librarian ID: " + ChatClient.l1.getLibrarian_id());
	    System.out.println("Subscriber ID: " + ChatClient.s1.getSubscriber_id());

	    // Check if Librarian ID is found first
	    if (ChatClient.l1.getLibrarian_id() != -1) {
	        System.out.println("Librarian ID Found");
	        awaitingLoginText.setStyle("-fx-text-fill: green;");
	        awaitingLoginText.setText("Welcome Back Librarian " + ChatClient.l1.getLibrarian_name());
	        navigateToLibrarianMenu(event);
	    }
	    // Check if Subscriber ID is found next
	    else if (ChatClient.s1.getSubscriber_id() != -1) {
	        System.out.println("Subscriber ID Found");
	        awaitingLoginText.setStyle("-fx-text-fill: green;");
	        awaitingLoginText.setText("Welcome Back Subscriber " + ChatClient.s1.getSubscriber_name());
	    }
	    // Handle the case where neither Librarian nor Subscriber is found
	    else {
	        System.out.println("No matching ID found for Librarian or Subscriber");
	        awaitingLoginText.setStyle("-fx-text-fill: red;");
	        awaitingLoginText.setText("No user found.");
	    }
	}


	public void start(Stage primaryStage) throws Exception {	
		Parent root = FXMLLoader.load(getClass().getResource("/gui/LibraryFramehWindow/LibraryFrame.fxml"));
				
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/LibraryFramehWindow/LibraryFrame.css").toExternalForm());
		primaryStage.setTitle("Library Managment Tool");
		primaryStage.setScene(scene);
        awaitingLoginText.setStyle("-fx-text-fill: green;");
		awaitingLoginText.setText ( "");
		primaryStage.show();	 	   
	}
	
	public void getExitBtn(ActionEvent event) throws Exception { // test
		try {
            // Hide the current window
            ((Node) event.getSource()).getScene().getWindow().hide();

            // Load the SubscriberForm window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainMenu/MainMenuFrame.fxml"));
            Pane root = loader.load();

            MainMenuController mainMenuController = loader.getController();
            //mainMenuController.loadSubscriber(ChatClient.s1);

            Stage primaryStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/MainMenu/MainMenuFrame.css").toExternalForm());
            primaryStage.setTitle("MainMenu");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void loadSubscriber(Subscriber s1) {
		this.lfc.loadSubscriber(s1);
	}	
	
	public  void display(String message) {
		System.out.println("message");
		
	}
	
}
