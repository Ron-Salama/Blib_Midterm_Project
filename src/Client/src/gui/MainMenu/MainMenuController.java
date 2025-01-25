package gui.MainMenu;

import gui.baseController.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller for the Main Menu of the Library Management Tool.
 * 
 * <p>This class manages user interactions in the main menu, allowing navigation to 
 * the search window, login window, or previous screens. It also handles application exit.</p>
 */
public class MainMenuController extends BaseController {
		
	/** Button to navigate to the registration window */
	@FXML
	private Button btnRegister = null;
	
	/** Button to navigate to the search window */
	@FXML
	private Button btnSearch = null;
	
	/** Button to navigate to the login window */
	@FXML
	private Button btnLogin = null;
	
	/** Button to go back to the previous window */
	@FXML
	private Button btnBack = null;
		
	/**
	 * Opens the search window for the user to search for books.
	 *
	 * @param event the event triggered by clicking the search button.
	 * @throws Exception if an error occurs while opening the search window.
	 */
	public void openSearchWindow(ActionEvent event) throws Exception {
		openWindow(event,
				"/gui/SearchWindow/SearchFrame.fxml",
				"/gui/SearchWindow/SearchFrame.css",
				"Search a Book");
	}

	/**
	 * Opens the login window for the user to log in to the system.
	 *
	 * @param event the event triggered by clicking the login button.
	 */
	public void openLoginWindow(ActionEvent event){
		openWindow(event,
				"/gui/LoginWindow/LoginFrame.fxml",
				"/gui/LoginWindow/LoginFrame.css",
				"Login");
	}
	
	/**
	 * Opens the registration window for new users to register.
	 *
	 * @param event the event triggered by clicking the register button.
	 */
	public void openRegisterWindow(ActionEvent event){
		openWindow(event,
				"/gui/SubscriberRegisterWindow/SubscriberRegisterWindowFrame.fxml",
				"/gui/SubscriberRegisterWindow/SubscriberRegisterWindowFrame.css",
				"Register");
	}
	
	/**
	 * Opens the IP input window for entering IP information.
	 *
	 * @param event the event triggered by clicking the IP input button.
	 * @throws Exception if an error occurs while opening the IP input window.
	 */
	public void openIPWindow(ActionEvent event) throws Exception {
		openWindow(event,
				"/gui/IPInputWindow/IPInputFrame.fxml",
				"/gui/IPInputWindow/IPInputFrame.css",
				"IP Input");
	}
	
	/**
	 * Navigates back to the IP input window.
	 *
	 * @param event the event triggered by clicking the back button.
	 * @throws Exception if an error occurs while navigating to the IP input window.
	 */
	public void back(ActionEvent event) throws Exception {
        openIPWindow(event);
	}
	
	/**
	 * Starts the Main Menu window.
	 *
	 * @param primaryStage the primary stage of the application.
	 * @throws Exception if an error occurs while starting the stage.
	 */
	public void start(Stage primaryStage) throws Exception {
		start(primaryStage,
				"/gui/MainMenuController/MainMenuFrame.fxml",
				"/gui/MainMenuController/MainMenuFrame.css",
				"Main Menu");	 
	}
}
