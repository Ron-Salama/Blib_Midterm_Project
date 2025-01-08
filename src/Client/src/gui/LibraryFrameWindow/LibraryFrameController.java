package gui.LibraryFrameWindow;

import java.io.IOException;

import client.ChatClient;
import client.ClientUI;
import gui.MainMenu.MainMenuController;
import gui.baseController.BaseController;
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


public class LibraryFrameController extends BaseController{
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
	
	public void Send(ActionEvent event) throws Exception {
	    String id = getID();
	    
	    if (id.trim().isEmpty()) {
	        System.out.println("You must enter an ID number");
	        awaitingLoginText.setStyle("-fx-text-fill: red;");
	        awaitingLoginText.setText("You must enter an ID number.");
	        return; // Early exit if ID is not provided
	    }
	    
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

	private void handleResponse(ActionEvent event) throws Exception {
	    // Debugging logs to check both IDs
	    System.out.println("Librarian ID: " + ChatClient.l1.getLibrarian_id());
	    System.out.println("Subscriber ID: " + ChatClient.s1.getSubscriber_id());

	    // Check if Librarian ID is found first
	    if (ChatClient.l1.getLibrarian_id() != -1) {
	        System.out.println("Librarian ID Found");
	        awaitingLoginText.setStyle("-fx-text-fill: green;");
	        awaitingLoginText.setText("Welcome Back Librarian " + ChatClient.l1.getLibrarian_name());
	    }
	    // Check if Subscriber ID is found next
	    else if (ChatClient.s1.getSubscriber_id() != -1) {
	        System.out.println("Subscriber ID Found");
	        awaitingLoginText.setStyle("-fx-text-fill: green;");
	        awaitingLoginText.setText("Welcome Back Subscriber " + ChatClient.s1.getSubscriber_name());
	        navigateToSubscriberWindow(event);
	        
	    }
	    // Handle the case where neither Librarian nor Subscriber is found
	    else {
	        System.out.println("No matching ID found for Librarian or Subscriber");
	        awaitingLoginText.setStyle("-fx-text-fill: red;");
	        awaitingLoginText.setText("No user found.");
	    }
	}


	public void start(Stage primaryStage) throws Exception {	
		start(primaryStage,
				"/gui/LibraryFramehWindow/LibraryFrame.fxml",
				"/gui/LibraryFramehWindow/LibraryFrame.css",
				"Library Managment Tool",
				null,
				"",
				"-fx-text-fill: green;");	 	   
	}
	
	public void getExitBtn(ActionEvent event) throws Exception {
		openWindow(event,
				"/gui/MainMenu/MainMenuFrame.fxml",
				"/gui/MainMenu/MainMenuFrame.css",
				"Main Menu");
	}
	
	public void loadSubscriber(Subscriber s1) {
		this.lfc.loadSubscriber(s1);
	}	
	
	public  void display(String message) {
		System.out.println("message");
		
	}
	
	private void navigateToSubscriberWindow(ActionEvent event) throws Exception {
		openWindow(event,
				"/gui/SubscriberWindow/SubscriberWindow.fxml",
				"/gui/SubscriberWindow/SubscriberWindow.css",
				"Library Management Tool");
    }
}
