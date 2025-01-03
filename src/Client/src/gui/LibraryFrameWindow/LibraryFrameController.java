package gui.LibraryFrameWindow;

import java.io.IOException;

import javafx.animation.PauseTransition;
import client.ChatClient;
import client.ClientController;
import client.ClientUI;
import common.ChatIF;
import gui.SubscriberFormWindow.SubscriberFormController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
	    } else {
	        // Send the request and handle response asynchronously
	        ClientUI.chat.accept("Fetch:" + id);

	        // Schedule a task to check the response without blocking
	        PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(6.0));
	        pause.setOnFinished(e -> checkSubscriberResponse(event));
	        pause.play();
	    }
	}
	
	
	
	
	
	private void checkSubscriberResponse(ActionEvent event) {
	    if (ChatClient.s1.getSubscriber_id() == -1) {
	        System.out.println("Subscriber ID Not Found");
	    } else {
	        System.out.println("Subscriber ID Found");

	        try {
	            // Hide the current window
	            ((Node) event.getSource()).getScene().getWindow().hide();

	            // Load the SubscriberForm window
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscriberForm.fxml"));
	            Pane root = loader.load();

	            SubscriberFormController subscriberFormController = loader.getController();
	            subscriberFormController.loadSubscriber(ChatClient.s1);

	            Stage primaryStage = new Stage();
	            Scene scene = new Scene(root);
	            scene.getStylesheets().add(getClass().getResource("/gui/SubscriberForm.css").toExternalForm());
	            primaryStage.setTitle("Subscriber Management Tool");
	            primaryStage.setScene(scene);
	            primaryStage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	

	public void start(Stage primaryStage) throws Exception {	
		Parent root = FXMLLoader.load(getClass().getResource("/gui/LibraryFrame.fxml"));
				
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/LibraryFrame.css").toExternalForm());
		primaryStage.setTitle("Library Managment Tool");
		primaryStage.setScene(scene);
		
		primaryStage.show();	 	   
	}
	
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("exit Library Tool");	
		System.exit(1);
	}
	
	public void loadSubscriber(Subscriber s1) {
		this.lfc.loadSubscriber(s1);
	}	
	
	public  void display(String message) {
		System.out.println("message");
		
	}
	
}
