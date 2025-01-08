package gui.SubscriberFormWindow;

import java.net.URL;
import java.util.ResourceBundle;

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

/**
 * The SubscriberFormController class is responsible for managing the subscriber form window in the GUI.
 * It allows users to view and update subscriber information such as ID, name, phone number, email, and subscription history.
 * The class also provides navigation back to the main subscriber form window.
 * 
 * <p>This class extends {@link BaseController} and implements {@link Initializable} for GUI initialization.
 * It interacts with the backend server via {@link ClientUI#chat} to send updates and manage subscriber data.</p>
 */
public class SubscriberFormController extends BaseController implements Initializable {
	/** The current subscriber object. */
	private Subscriber s;

	// Labels for displaying subscriber information
    @FXML
    private Label lblSubscriber_id;
    @FXML
    private Label lblSubscriber_name;
    @FXML
    private Label lblSubscriber_phoneNumber;
    @FXML
    private Label lblSubscriber_email;
    @FXML
    private Label lblSubscriber_detailedSubscriptionHistory;

    // Text fields for inputting and updating subscriber information
    @FXML
    private TextField txtSubscriber_id;
    @FXML
    private TextField txtSubscriber_name;
    @FXML
    private TextField txtSubscriber_phoneNumber;
    @FXML
    private TextField txtSubscriber_email;
    @FXML
    private TextField txtSubscriber_detailedSubscriptionHistory;

    // Buttons for closing and updating the form
    @FXML
    private Button btnClose = null;

    @FXML
    private Button btnUpdate = null;

    /** A list for storing observable data. */
    ObservableList<String> list;

    /**
     * Loads the subscriber details into the form for viewing or editing.
     * 
     * @param s1 the {@link Subscriber} object containing the subscriber's details
     */
    public void loadSubscriber(Subscriber s1) {
        this.s = s1;
        this.txtSubscriber_id.setText(String.valueOf(s.getSubscriber_id())); // Convert int to String
        this.txtSubscriber_name.setText(s.getSubscriber_name()); // Assuming this is already a String
        this.txtSubscriber_phoneNumber.setText(s.getSubscriber_phone_number()); // Assuming this is already a String
        this.txtSubscriber_email.setText(s.getSubscriber_email()); // Assuming this is already a String
        this.txtSubscriber_detailedSubscriptionHistory.setText(String.valueOf(s.getDetailed_subscription_history())); // Convert int to String
    }

    /**
     * Updates the subscriber information based on the input fields.
     * Sends the update message to the server and navigates back to the main subscriber form window.
     * 
     * @param event the {@link ActionEvent} triggered by the update button
     * @throws Exception if an error occurs during navigation or communication with the server
     */
    public void btnUpdate(ActionEvent event) throws Exception {
        // Get the updated values from the text fields
        String id = txtSubscriber_id.getText();
        String phoneNumber = txtSubscriber_phoneNumber.getText();
        String email = txtSubscriber_email.getText();

        // Prepare the update message
        String obj = "Update:" + id + "," + phoneNumber + "," + email;

        // Send the update message to the server
        ClientUI.chat.accept(obj);

        // Navigate back to the LibraryFrame
        getbtnClose(event);
    }

    /**
     * Handles the close button action and navigates back to the main subscriber form window.
     * 
     * @param event the {@link ActionEvent} triggered by the close button
     * @throws Exception if an error occurs during navigation
     */
    public void getbtnClose(ActionEvent event) throws Exception {
    	openWindow(event,
    			"/gui/SubscraberFormWindow/SubscriberForm.fxml",
    			"/gui/SubscraberFormWindow/SubscriberForm.css",
    			"Library Management Tool");
    }

    /**
     * Initializes the subscriber form controller.
     * This method is called automatically when the FXML file is loaded.
     * 
     * @param arg0 the location of the FXML file
     * @param arg1 the resources for the FXML file
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Initialization logic, if needed
    }
}
