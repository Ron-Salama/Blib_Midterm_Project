package gui.HistoryWindow;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import gui.MyBooksWindow.MyBooksController;
import gui.SubscriberWindow.SubscriberWindowController;
import gui.baseController.BaseController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.Subscriber;

/**
 * Controller for the Search Window in the Library Management Tool.
 * 
 * <p>This class allows users to search for books based on name, description, 
 * and subject filters, and displays the results in a TableView. It also provides 
 * an option to navigate back to the Main Menu.</p>
 */
public class HistoryController extends BaseController implements Initializable {
    @FXML
    private Button btnBackF;
    @FXML
    private Button btnExit;
    @FXML
    private TableView<String> tableView;
    @FXML
    private TableColumn<String, String> tableDescription;
   
    private Subscriber currentSubscriber;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentSubscriber = MyBooksController.currentSub;
       
        // Configure TableColumn to display String values directly
        tableDescription.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()));

        // Fetch and populate books
        new Thread(() -> {
            ClientUI.chat.accept("GetHistory:" + currentSubscriber.getSubscriber_id());
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
        if (ChatClient.myHistoryInfo != null && !ChatClient.myHistoryInfo.isEmpty()) {
            Platform.runLater(() -> {
                tableView.getItems().clear(); // Clear any existing data
                
                // Add all items from the list to the TableView
                tableView.getItems().addAll(ChatClient.myHistoryInfo);
                ChatClient.myHistoryInfo.clear(); // Clears all elements from the ArrayList
                
            });
        } else {
            System.out.println("No history data to display.");
        }
    }

}
