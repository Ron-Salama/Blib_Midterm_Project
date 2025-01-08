package gui.MainMenu;

import java.io.IOException;

import gui.LibraryFrameWindow.LibraryFrameController;
import gui.SearchWindow.SearchFrameController;
import gui.SubscriberRegisterWindow.SubscriberRegisterWindowController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import gui.baseController.*;

/**
 * Controller for the Main Menu of the Library Management Tool.
 * 
 * <p>This class manages user interactions in the main menu, allowing navigation to 
 * the search window, login window, or previous screens. It also handles application exit.</p>
 */
public class MainMenuController extends BaseController {

		private MainMenuController mmc;	
		
		@FXML
		private Button btnRegister = null;
		
		@FXML
		private Button btnSearch = null;
		@FXML
		private Button btnLogin = null;
		@FXML
		private Button btnBack = null;
		
		public void search(ActionEvent event) throws Exception {
		    openSearchWindow(event);
		}
		public void login(ActionEvent event) throws Exception {
	        openLoginWindow(event);
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
		
		
		private void openSearchWindow(ActionEvent event){
			try {
	            // Hide the current window
	            ((Node) event.getSource()).getScene().getWindow().hide();

	            // Load the SubscriberForm window
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SearchWindow/SearchFrame.fxml"));
	            Pane root = loader.load();

	            SearchFrameController searchFrameController = loader.getController();
//	            mainMenuController.loadSubscriber(ChatClient.s1);

	            Stage primaryStage = new Stage();
	            Scene scene = new Scene(root);
	            scene.getStylesheets().add(getClass().getResource("/gui/SearchWindow/SearchFrame.css").toExternalForm());
	            primaryStage.setTitle("Search a Book");
	            primaryStage.setScene(scene);
	            primaryStage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		
		private void openLoginWindow(ActionEvent event){
			try {
	            // Hide the current window
	            ((Node) event.getSource()).getScene().getWindow().hide();

	            // Load the SubscriberForm window
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LibraryFrameWindow/LibraryFrame.fxml"));
	            Pane root = loader.load();

	            LibraryFrameController libraryFrameController = loader.getController();
//	            mainMenuController.loadSubscriber(ChatClient.s1);

	            Stage primaryStage = new Stage();
	            Scene scene = new Scene(root);
	            scene.getStylesheets().add(getClass().getResource("/gui/LibraryFrameWindow/LibraryFrame.css").toExternalForm());
	            primaryStage.setTitle("Login");
	            primaryStage.setScene(scene);
	            primaryStage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		
		public void openRegisterWindow(ActionEvent event){
			try {
	            // Hide the current window
	            ((Node) event.getSource()).getScene().getWindow().hide();

	            // Load the SubscriberForm window
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscriberRegisterWindow/SubscriberRegisterWindowFrame.fxml"));
	            Pane root = loader.load();

	            SubscriberRegisterWindowController RegisterWindowFrameController = loader.getController();
//	            mainMenuController.loadSubscriber(ChatClient.s1);

	            Stage primaryStage = new Stage();
	            Scene scene = new Scene(root);
	            scene.getStylesheets().add(getClass().getResource("/gui/SubscriberRegisterWindow/SubscriberRegisterWindowFrame.css").toExternalForm());
	            primaryStage.setTitle("Login");
	            primaryStage.setScene(scene);
	            primaryStage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		
		public void start(Stage primaryStage) throws Exception {	
			Parent root = FXMLLoader.load(getClass().getResource("/gui/MainMenuController/MainMenuFrame.fxml"));
					
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/MainMenuController/MainMenuFrame.css").toExternalForm());
			primaryStage.setTitle("Library Managment Tool");
			primaryStage.setScene(scene);
			
			primaryStage.show();	 	   
		}
		
		public void getExitBtn(ActionEvent event) throws Exception {
			System.out.println("exit Library Tool");	
			System.exit(1);
		}
		
		public void openIPWindow(ActionEvent event) throws Exception {
			((Node) event.getSource()).getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/IPInputWindow/IPInputFrame.fxml"));
            Pane root = loader.load();

//            IPInputController iPInputController = loader.getController();

            Stage primaryStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/IPInputWindow/IPInputFrame.css").toExternalForm());
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.show();

		}
}
