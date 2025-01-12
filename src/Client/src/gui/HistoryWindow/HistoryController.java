package gui.HistoryWindow;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import gui.baseController.BaseController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

import logic.Book;

/**
 * Controller for the Search Window in the Library Management Tool.
 * 
 * <p>This class allows users to search for books based on name, description, 
 * and subject filters, and displays the results in a TableView. It also provides 
 * an option to navigate back to the Main Menu.</p>
 */
public class HistoryController extends BaseController {
	private HistoryController hc;
    @FXML
    private Button btnBackF;
    @FXML
    private Button btnExit;
    @FXML
    private TableView<String> tableView;
    @FXML
    private TableColumn<String, String> tableDescription;
   
    
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize TableView columns
    	tableDescription.setCellValueFactory(new PropertyValueFactory<String, String>("Description"));
       
        // Fetch and populate books
        new Thread(() -> {
        	ClientUI.chat.accept("GetHistory:");
            Platform.runLater(this::loadHistory); // Populate the table after data is fetched
        }).start();
    }
    
    
    public void backFromUser(ActionEvent event) {
    	openWindow(event,
    			"/gui/MyBooksWindow/MyBooksFrame.fxml",
    			"/gui/MyBooksWindow/MyBooksFrame.css",
    			"My Books");
    	 
    }
    
    public void getExitBtn(ActionEvent event) {
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"MainMenu");
    }
    
    private void loadHistory() {
        // Ensure HistoryList is not empty
        if (ChatClient.myHistoryInfo != null && ChatClient.myHistoryInfo.length > 0) {
            // Populate the TableView with history data
            Platform.runLater(() -> {
                tableView.getItems().clear();  // Clear any existing data

                // Add all history data to the table (each entry as a row)
                tableView.getItems().addAll(Arrays.asList(ChatClient.myHistoryInfo));
            });
        } else {
            System.out.println("No history data to display.");
        }
    }

    
    

}
