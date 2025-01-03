package gui.IPInputWindow;

import java.io.IOException;
import javafx.animation.PauseTransition;
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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.Subscriber;

public class IPInputController {

		private IPInputController lipc;	
		
		@FXML
		private Button btnExit = null;
		
		@FXML
		private Button btnSend = null;
		
		@FXML
		private TextField IPtxt;
		
		private String getIP() {
			return IPtxt.getText();
		}
		
		
		public void Send(ActionEvent event) throws Exception {
		    String ip = getIP();
		    if (ip.trim().isEmpty()) {
		    	//TODO: enter an alert here.
		        System.out.println("You must enter an IP address.");
		        return;
		    }
		    
		    System.setProperty("server.ip", ip);
		    ClientUI.chat.accept("IP:" + ip); // Send the request
		    
		    // Simulate asynchronous handling of the response
		    PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(0.5));
		    pause.setOnFinished(e -> {
		        if (!ClientUI.isIPValid) {
		            System.out.println("ALERT: Invalid IP detected!");
		            // TODO: enter an alert here.
		        } else {
		            openMainMenu(event);
		        }
		    });
		    pause.play();
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
