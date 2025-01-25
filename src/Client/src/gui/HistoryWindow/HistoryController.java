package gui.HistoryWindow;

import java.net.URL;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import gui.MyBooksWindow.MyBooksController;
import gui.baseController.BaseController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import logic.Subscriber;

/**
 * Controller for the History Window in the Library Management Tool.
 * 
 * <p>This class allows users to view the history of books the current subscriber has interacted with.
 * It also provides options for navigating back to the MyBooks window or exiting to the Main Menu.</p>
 */
public class HistoryController extends BaseController implements Initializable {
    
	// Button to navigate back to the MyBooks window.
	@FXML
    private Button btnBack = null;
    
    // Button to exit the current window and return to the Main Menu.
    @FXML
    private Button btnExit = null;
    
    // TableView to display the history of books the subscriber has interacted with.
    @FXML
    private TableView<String> tableView;
    
    // TableColumn to display book descriptions in the TableView.
    @FXML
    private TableColumn<String, String> tableDescription;
   
    // The current subscriber whose history is being viewed.
    private Subscriber currentSubscriber = null;
    
    /**
     * Initializes the History Window by setting up the TableColumn and loading the subscriber's history.
     * 
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known
     * @param resourceBundle the resources used to localize the root object, or null if no localization is desired
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentSubscriber = MyBooksController.currentSub;
       
        // Configure TableColumn to display String values directly
        tableDescription.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()));
        
        ClientUI.chat.accept("GetHistory:" + currentSubscriber.getSubscriber_id());
        waitForServerResponse();
        loadHistory();
    }

    /**
     * Handles the action of navigating back to the MyBooks window.
     * 
     * @param event the action event that triggers the method
     */
    public void backFromUser(ActionEvent event) {
    	MyBooksController.fromHistory = true;
    	openWindow(event,
    			"/gui/MyBooksWindow/MyBooksFrame.fxml",
    			"/gui/MyBooksWindow/MyBooksFrame.css",
    			"My Books");
    }
    
    /**
     * Handles the action of exiting the current window and navigating to the Main Menu.
     * 
     * @param event the action event that triggers the method
     */
    public void getExitBtn(ActionEvent event) {
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"MainMenu");
    }
    
    /**
     * Loads the history of books the current subscriber has interacted with into the TableView.
     * It ensures the history list is not empty and updates the UI on the JavaFX Application Thread.
     */
    private void loadHistory() {
        // Ensure HistoryList is not empty
        if (ChatClient.myHistoryInfo != null && !ChatClient.myHistoryInfo.isEmpty()) {
            Platform.runLater(() -> {
                tableView.getItems().clear(); // Clear any existing data
                
                // Add all items from the list to the TableView
                tableView.getItems().addAll(ChatClient.myHistoryInfo);
                ChatClient.myHistoryInfo.clear(); // Clears all elements from the ArrayList
                
            });
        }
    }
}
