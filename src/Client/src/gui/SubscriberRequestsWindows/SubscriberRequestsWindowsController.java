package gui.SubscriberRequestsWindows;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.Subscriber;

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
    private Label LBL4; // Add the TextField for LBL4

    @FXML
    private TextField TXTF1;
    @FXML
    private TextField TXTF2;
    @FXML
    private TextField TXTF3;
    @FXML
    private TextField TXTF4;
    @FXML
    private ComboBox<String> RequestTypeCB;

    @FXML
    private ComboBox<String> RequestedByCB;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	RequestTypeCB.getItems().add("");
        RequestTypeCB.getItems().add("Registers");
        RequestTypeCB.getItems().add("Extend Book Borrow");
        RequestTypeCB.getItems().add("Borrow For Subscriber");
        RequestTypeCB.getItems().add("Return For Subscriber");

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

        // Set the fields based on the selected request type
        switch (selectedRequestType) {
            case "Registers":
                LBL1.setText("Name:");
                LBL2.setText("ID:");
                LBL3.setText("Email:");
                LBL4.setText("Phone Number:");
                TXTF4.setVisible(true);
                break;
            case "Borrow For Subscriber":
                LBL1.setText("Name:");
                LBL2.setText("ID:");
                LBL3.setText("Borrow time:");
                LBL4.setText("Expected Return:");
                TXTF4.setVisible(true);
                break;
            case "Return For Subscriber":
                LBL1.setText("Name:");
                LBL2.setText("ID:");
                LBL3.setText("Return time:");
                LBL4.setText(""); // No fourth label needed
                TXTF4.setVisible(false);

                break;
            case "Extend Book Borrow":
                LBL1.setText("Name:");
                LBL2.setText("ID:");
                LBL3.setText("Borrow time:");
                LBL4.setText("Expected Return:");
                TXTF4.setVisible(true);
                break;
            default:
                // If the ComboBox is empty or any other option, reset labels
                LBL1.setText("");
                LBL2.setText("");
                LBL3.setText("");
                LBL4.setText("");
                TXTF4.setVisible(true);
                break;
        }
    }

    public void getExitBtn(ActionEvent event) throws Exception {
        try {
            // Hide the current window
            ((Node) event.getSource()).getScene().getWindow().hide();

            // Load the SubscriberForm window
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
        System.out.println("message");
    }
}
