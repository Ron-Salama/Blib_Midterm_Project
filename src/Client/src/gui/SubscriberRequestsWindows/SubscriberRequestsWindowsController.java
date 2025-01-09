package gui.SubscriberRequestsWindows;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.Subscriber;
import gui.MainMenu.MainMenuController;

public class SubscriberRequestsWindowsController implements Initializable {
    private SubscriberRequestsWindowsController srwc;

    @FXML
    private Button btnExit = null;

    @FXML
    private Button btnSend = null;

    @FXML
    private Label LBL1;
    @FXML
    private Label LBL2;
    @FXML
    private Label LBL3;
    @FXML
    private Label LBL4;
    @FXML
    private Label LBL5;

    @FXML
    private TextField TXTF1;
    @FXML
    private TextField TXTF2;
    @FXML
    private TextField TXTF3;
    @FXML
    private TextField TXTF4;
    @FXML
    private TextField TXTF5;

    @FXML
    private ComboBox<String> RequestTypeCB;

    @FXML
    private ComboBox<String> RequestedByCB;

    private List<String[]> borrowRequests = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize request types
        RequestTypeCB.getItems().addAll("", "Registers", "Extend Book Borrow", "Borrow For Subscriber", "Return For Subscriber");
        RequestedByCB.getItems().add("");

        // Set an event listener to handle ComboBox changes
        RequestTypeCB.setOnAction(event -> updateLabels());
        
        
    }

    public void updateLabels() {
        String selectedRequestType = RequestTypeCB.getValue();

        // Reset fields to empty by default
        LBL1.setText("");
        LBL2.setText("");
        LBL3.setText("");
        LBL4.setText("");
        LBL5.setText("");

        // Clear the RequestedByCB ComboBox
        RequestedByCB.getItems().clear();
        
        // Set the fields and populate the RequestedByCB based on the selected request type
        switch (selectedRequestType) {
            case "Registers":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Email:");
                LBL4.setText("Phone Number:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(false);
                break;
            case "Borrow For Subscriber":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Book Name:");
                LBL4.setText("Book ID:");
                LBL5.setText("Borrow Time:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
                ClientUI.chat.accept("FetchBorrowRequest:"); 
                handleFetchedBorrowedBooks();
                break;
            case "Return For Subscriber":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Book Name:");
                LBL4.setText("Book ID:");
                LBL5.setText("Return Time:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
                break;
            case "Extend Book Borrow":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Book Name:");
                LBL4.setText("Book ID:");
                LBL5.setText("Extend Time:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
                break;
            default:
                LBL1.setText("");
                LBL2.setText("");
                LBL3.setText("");
                LBL4.setText("");
                LBL5.setText("");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
                break;
        }
    }
   

    public void handleFetchedBorrowedBooks() {
        borrowRequests.clear();  // Clear the existing list to avoid duplicate data

        // Ensure ChatClient.br is not null or empty
        if (ChatClient.br != null && ChatClient.br.size() > 0) {
            // Iterate over each row in ChatClient.br (each row is a borrow request)
            for (int i = 0; i < ChatClient.br.size(); i++) {
                String[][] borrowRequestArray = ChatClient.br.get(i); // Get the i-th 2D array

                // Iterate over each borrow request in the i-th 2D array (assuming each request has 8 fields)
                for (String[] request : borrowRequestArray) {
                    if (request.length == 8) {
                        borrowRequests.add(request);  // Add the borrow request (which is a String array) to the list
                    } else {
                        System.out.println("Invalid borrow request data at index " + i + ": " + String.join(",", request));
                    }
                }
            }

            // Populate the ComboBox with subscriber names from the borrow requests
            ObservableList<String> requestedByList = FXCollections.observableArrayList();
            for (String[] borrowRequest : borrowRequests) {
                requestedByList.add(borrowRequest[2]);  // Assuming the name is at index 2
            }
            RequestedByCB.setItems(requestedByList);
        } else {
            System.out.println("No borrow requests available.");
        }
    }

    public void getExitBtn(ActionEvent event) throws Exception {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainMenu/MainMenuFrame.fxml"));
            Pane root = loader.load();

            MainMenuController mainMenuController = loader.getController();

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

    public void display(String message) {
        System.out.println(message);
    }
}
