package gui.ReaderCardWindow;

import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import client.ChatClient;
import client.ClientController;
import client.ClientUI;
import common.ChatIF;
import gui.MainMenu.MainMenuController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.Subscriber;

public class ReaderCardController {

		private ReaderCardController rcc;	
		
		@FXML
		private Button btnExit = null;
		
		@FXML
		private Button btnSend = null;
		
		@FXML
		private TextField IPtxt;
		@FXML
		private Label awaitingLoginText;
		@FXML
		private Label welcomeLabel;
		
		private String getIP() {
			return IPtxt.getText();
		}
		
		
		public void Send(ActionEvent event) throws Exception {
		    String ip = getIP();  // Assuming getIP() fetches the IP entered in a TextField
		    if (ip.trim().isEmpty()) {
		        // Display error if IP is empty
		        awaitingLoginText.setText("You must enter an IP address.");
		        awaitingLoginText.setStyle("-fx-text-fill: red;");
		        System.out.println("You must enter an IP address.");
		        return;
		    }

		    // Set the system property for the server IP
		    System.setProperty("server.ip", ip);
		    ClientUI.chat.accept("IP:" + ip); // Send the request to the server

		    // Simulate asynchronous handling of the response
		    PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(0.1)); // Short delay
		    pause.setOnFinished(e -> {
		        if (!ClientUI.isIPValid) {
		            // If the IP is not valid
		            awaitingLoginText.setText("Invalid IP address.");
		            awaitingLoginText.setStyle("-fx-text-fill: red;");
		            System.out.println("ALERT: Invalid IP detected!");

		            // Use Platform.runLater to ensure the alert is shown after the transition
		            Platform.runLater(() -> {
		                showAlert("Error", "Invalid IP address. Please try again.");
		            });
		        } else {
		            // If the IP is valid
		            awaitingLoginText.setText("Connected successfully to IP: " + ip);
		            awaitingLoginText.setStyle("-fx-text-fill: green;");
		            System.out.println("Connected successfully to IP: " + ip);

		            // Create a small pause before opening the main menu
		            PauseTransition pause1 = new PauseTransition(javafx.util.Duration.seconds(1)); // Wait 1 seconds before moving on
		            pause1.setOnFinished(e1 -> {
		                openMainMenu(event);  // Navigate to the main menu
		            });
		            pause1.play();
		        }
		    });
		    pause.play();  // Start the initial delay
		}

		// Method to show an alert dialog
		private void showAlert(String title, String message) {
		    Alert alert = new Alert(Alert.AlertType.ERROR);
		    alert.setTitle(title);
		    alert.setHeaderText(null);
		    alert.setContentText(message);
		    alert.showAndWait();
		}

		
		private void openMainMenu(ActionEvent event){
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


//		private void checkSubscriberResponse(ActionEvent event) {
//		    if (ChatClient.s1.getSubscriber_id() == -1) {
//		        System.out.println("Subscriber ID Not Found");
//		    } else {
//		        System.out.println("Subscriber ID Found");
//
//		        try {
//		            // Hide the current window
//		            ((Node) event.getSource()).getScene().getWindow().hide();
//
//		            // Load the SubscriberForm window
//		            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscriberForm.fxml"));
//		            Pane root = loader.load();
//
//		            SubscriberFormController subscriberFormController = loader.getController();
//		            subscriberFormController.loadSubscriber(ChatClient.s1);
//
//		            Stage primaryStage = new Stage();
//		            Scene scene = new Scene(root);
//		            scene.getStylesheets().add(getClass().getResource("/gui/SubscriberForm.css").toExternalForm());
//		            primaryStage.setTitle("Subscriber Management Tool");
//		            primaryStage.setScene(scene);
//		            primaryStage.show();
//		        } catch (IOException e) {
//		            e.printStackTrace();
//		        }
//		    }
		
		

		
		
		
		

		public void start(Stage primaryStage) throws Exception {	
			Parent root = FXMLLoader.load(getClass().getResource("/gui/IPInputWindow/IPInputFrame.fxml"));
					
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/IPInputWindow/IPInputFrame.css").toExternalForm());
			primaryStage.setTitle("Library Managment Tool");
			primaryStage.setScene(scene);
			
			primaryStage.show();	 	   
		}
		
		public void getExitBtn(ActionEvent event) throws Exception {
			System.out.println("exit Library Tool");	
			System.exit(1);
		}
		
		public void loadSubscriber(Subscriber s1) {
			this.lipc.loadSubscriber(s1);
		}	
		
		public  void display(String message) {
			System.out.println("message");
			
		}
}
