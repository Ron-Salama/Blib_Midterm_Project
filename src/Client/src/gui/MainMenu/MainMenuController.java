package gui.MainMenu;

import java.io.IOException;

import gui.LoginWindow.LoginController;
import gui.SearchWindow.SearchFrameController;
import gui.SubscriberRegisterWindow.SubscriberRegisterWindowFrameController;
import gui.baseController.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller for the Main Menu of the Library Management Tool.
 * 
 * <p>This class manages user interactions in the main menu, allowing navigation to 
 * the search window, login window, or previous screens. It also handles application exit.</p>
 */
public class MainMenuController extends BaseController {
		
		@FXML
		private Button btnRegister = null;
		
		@FXML
		private Button btnSearch = null;
		@FXML
		private Button btnLogin = null;
		@FXML
		private Button btnBack = null;
		
		public void openSearchWindow(ActionEvent event) throws Exception {
			openWindow(event,
					"/gui/SearchWindow/SearchFrame.fxml",
					"/gui/SearchWindow/SearchFrame.css",
					"Search a Book");
		}

		public void openLoginWindow(ActionEvent event){
			openWindow(event,
					"/gui/LoginWindow/LoginFrame.fxml",
					"/gui/LoginWindow/LoginFrame.css",
					"Login");
		}
		
		public void openRegisterWindow(ActionEvent event){
			openWindow(event,
					"/gui/SubscriberRegisterWindow/SubscriberRegisterWindowFrame.fxml",
					"/gui/SubscriberRegisterWindow/SubscriberRegisterWindowFrame.css",
					"Register");
		}
		
		public void openIPWindow(ActionEvent event) throws Exception {
			openWindow(event,
					"/gui/IPInputWindow/IPInputFrame.fxml",
					"/gui/IPInputWindow/IPInputFrame.css",
					"IP Input");
		}
		
		public void back(ActionEvent event) throws Exception {
	        openIPWindow(event);
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
			start(primaryStage,
					"/gui/MainMenuController/MainMenuFrame.fxml",
					"/gui/MainMenuController/MainMenuFrame.css",
					"Main Menu");	 
		}
}
