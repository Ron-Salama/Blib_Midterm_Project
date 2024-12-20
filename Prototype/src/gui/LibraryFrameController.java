package gui;

import java.io.IOException;

import javafx.animation.PauseTransition;
import client.ChatClient;
import client.ClientController;
import client.ClientUI;
import common.ChatIF;
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


public  class LibraryFrameController   {
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
	        System.out.println("Please Enter Valid ID");
	    } else {
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
