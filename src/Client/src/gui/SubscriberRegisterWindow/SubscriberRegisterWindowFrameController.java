package gui.SubscriberRegisterWindow;

import java.net.URL;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import gui.baseController.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for the Subscriber Registration Window in the library system.
 * This window allows the user to register a new subscriber by entering required details.
 */
public class SubscriberRegisterWindowFrameController extends BaseController implements Initializable {
    
	/** This label is used to display the text "Subscriber ID" in the registration form. */
    @FXML
    private Label lblSubscriber_id;
    
    /** This label is used to display the text "Subscriber Name" in the registration form. */
    @FXML
    private Label lblSubscriber_name;
    
    /** This label is used to display the text "Subscriber Phone Number" in the registration form. */
    @FXML
    private Label lblSubscriber_phoneNumber;
    
    /** This label is used to display the text "Subscriber Email" in the registration form. */
    @FXML
    private Label lblSubscriber_email;

    /** This label will display success or error messages related to registration. */
    @FXML
    private Label registerDynamicLabel;

    /** This text field is used for the user to input the Subscriber ID during registration. */
    @FXML
    private TextField txtSubscriber_id;
    
    /** This text field is used for the user to input the Subscriber's Name during registration. */
    @FXML
    private TextField txtSubscriber_name;
    
    /** This text field is used for the user to input the Subscriber's Phone Number during registration. */
    @FXML
    private TextField txtSubscriber_phoneNumber;
    
    /** This text field is used for the user to input the Subscriber's Email during registration. */
    @FXML
    private TextField txtSubscriber_email;

    /** This button allows the user to navigate back to the main menu of the system. */
    @FXML
    private Button btnReturnToMainMenu = null;
    
    /** This button is used to submit the registration form and register the subscriber. */
    @FXML
    private Button btnRegister = null;
    
    /**
     * Initializes the controller, if necessary. No specific initialization is required here.
     *
     * @param arg0 the URL location of the FXML file used to initialize the controller.
     * @param arg1 the resources used to localize the root object.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	// Initialization logic, if needed
    }

    /**
     * Handles the Register button click event. 
     * It validates the registration form and sends the registration request to the server.
     *
     * @param event the event triggered by clicking the Register button.
     * @throws Exception if there is an error during the registration process.
     */
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
            waitForServerResponse();  
        }

        // Now check if the ID is in the database
        if (ChatClient.isIDInDataBase) {
            String labelMessage = "The user ID, " + id + ", is already taken";
            showColoredLabelMessageOnGUI(registerDynamicLabel, labelMessage, "-fx-text-fill: red;");
        } else {
            String labelMessage = "Form sent, Awaiting approval from the Librarian.\nID: " + id + "\nName: " + name + "\nPhone Number: " + phoneNumber + "\nEmail: " + email;
            showColoredLabelMessageOnGUI(registerDynamicLabel, labelMessage, "-fx-text-fill: green;");
        }
    }

    /**
     * Handles the Return to Main Menu button click event. 
     * It opens the Main Menu window.
     *
     * @param event the event triggered by clicking the Return to Main Menu button.
     */
    public void btnReturnToMainMenu(ActionEvent event) {
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"MainMenu");
    }

    /**
     * Validates the registration form to ensure that all required fields are filled.
     * Displays appropriate error messages if any required fields are empty.
     *
     * @param id the subscriber's ID.
     * @param name the subscriber's name.
     * @param phoneNumber the subscriber's phone number.
     * @param email the subscriber's email.
     * @return true if all required fields are filled; false otherwise.
     */
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
