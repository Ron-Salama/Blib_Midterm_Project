package gui.SubscriberRegisterWindow;

import java.net.URL;
import java.time.chrono.IsoChronology;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import gui.baseController.BaseController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Subscriber;

public class SubscriberRegisterWindowFrameController extends BaseController implements Initializable {
    private Subscriber s;
    
    @FXML
    private Label lblSubscriber_id;
    @FXML
    private Label lblSubscriber_name;
    @FXML
    private Label lblSubscriber_phoneNumber;
    @FXML
    private Label lblSubscriber_email;

    @FXML
    private Label registerDynamicLabel;

    @FXML
    private TextField txtSubscriber_id;
    @FXML
    private TextField txtSubscriber_name;
    @FXML
    private TextField txtSubscriber_phoneNumber;
    @FXML
    private TextField txtSubscriber_email;

    @FXML
    private Button btnReturnToMainMenu = null;
    
    @FXML
    private Button btnRegister = null;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	// Initialization logic, if needed
    }

    public void btnRegister(ActionEvent event) throws Exception {
        // Get the updated values from the text fields
        String id = txtSubscriber_id.getText();
        String phoneNumber = txtSubscriber_phoneNumber.getText();
        String email = txtSubscriber_email.getText();
        String name = txtSubscriber_name.getText();

        if (!isRegistrationFormValid(id, name, phoneNumber, email)) {
            return; // Show colored label and stop the function
        } else {
            // Prepare the update message
            String obj = id + "," + name + "," + phoneNumber + "," + email;

            // Send the update message to the server
            ClientUI.chat.accept("RegisterRequest:" + obj);
            
        }

        waitForServerResponse();

        // Now check if the ID is in the database
        if (ChatClient.isIDInDataBase) {
            String labelMessage = "The user ID, " + id + ", is already taken";
            showColoredLabelMessageOnGUI(registerDynamicLabel, labelMessage, "-fx-text-fill: red;");
        } else {
            String labelMessage = "Form sent, Awaiting approval from the Librarian.\nID: " + id + "\nName: " + name + "\nPhone Number: " + phoneNumber + "\nEmail: " + email;
            showColoredLabelMessageOnGUI(registerDynamicLabel, labelMessage, "-fx-text-fill: green;");
        }
    }

    public void btnReturnToMainMenu(ActionEvent event) {
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"MainMenu");
    }

    private boolean isRegistrationFormValid(String id, String name, String phoneNumber, String email) {
        if (id.isEmpty() && name.isEmpty() && phoneNumber.isEmpty() && email.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "The form is empty, make sure to fill it.", "-fx-text-fill: red;");
            return false;
        }
        if (id.isEmpty() && name.isEmpty() && phoneNumber.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "ID, Name, and Phone Number are missing.", "-fx-text-fill: red;");
            return false;
        }
        if (id.isEmpty() && name.isEmpty() && email.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "ID, Name, and Email are missing.", "-fx-text-fill: red;");
            return false;
        }
        if (id.isEmpty() && phoneNumber.isEmpty() && email.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "ID, Phone Number, and Email are missing.", "-fx-text-fill: red;");
            return false;
        }
        if (name.isEmpty() && phoneNumber.isEmpty() && email.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "Name, Phone Number, and Email are missing.", "-fx-text-fill: red;");
            return false;
        }
        if (id.isEmpty() && name.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "ID and Name are missing.", "-fx-text-fill: red;");
            return false;
        }
        if (id.isEmpty() && phoneNumber.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "ID and Phone Number are missing.", "-fx-text-fill: red;");
            return false;
        }
        if (id.isEmpty() && email.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "ID and Email are missing.", "-fx-text-fill: red;");
            return false;
        }
        if (name.isEmpty() && phoneNumber.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "Name and Phone Number are missing.", "-fx-text-fill: red;");
            return false;
        }
        if (name.isEmpty() && email.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "Name and Email are missing.", "-fx-text-fill: red;");
            return false;
        }
        if (phoneNumber.isEmpty() && email.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "Phone Number and Email are missing.", "-fx-text-fill: red;");
            return false;
        }
        if (id.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "ID is missing.", "-fx-text-fill: red;");
            return false;
        }
        if (name.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "Name is missing.", "-fx-text-fill: red;");
            return false;
        }
        if (phoneNumber.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "Phone Number is missing.", "-fx-text-fill: red;");
            return false;
        }
        if (email.isEmpty()) {
            showColoredLabelMessageOnGUI(registerDynamicLabel, "Email is missing.", "-fx-text-fill: red;");
            return false;
        }
        return true; // All fields are filled.
    }

}
