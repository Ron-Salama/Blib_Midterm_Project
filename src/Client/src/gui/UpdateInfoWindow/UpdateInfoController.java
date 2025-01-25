package gui.UpdateInfoWindow;

import java.net.URL;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import gui.SubscriberWindow.SubscriberWindowController;
import gui.baseController.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logic.Subscriber;

/**
 * Controller class for updating a subscriber's personal information such as phone number and email.
 * <p>
 * This window is accessed by a subscriber who wants to edit their contact details. The 
 * subscriber ID and name fields are displayed but disabled, since they cannot be changed. 
 * The user can modify the phone number and email fields, and submit the changes to the server.
 * </p>
 * 
 * <p>Implements {@link Initializable} to allow setup tasks after the associated FXML is loaded.</p>
 * 
 * @author  
 * @version 1.0
 * @since 2025-01-01
 */
public class UpdateInfoController extends BaseController implements Initializable {

    /** Label for displaying "subscriber_id" text. */
    @FXML
    private Label lblSubscriber_id;

    /** Label for displaying "subscriber_name" text. */
    @FXML
    private Label lblSubscriber_name;

    /** Label for displaying "subscriber_phoneNumber" text. */
    @FXML
    private Label lblSubscriber_phoneNumber;

    /** Label for displaying "subscriber_email" text. */
    @FXML
    private Label lblSubscriber_email;

    /** Label used to provide feedback regarding update status. */
    @FXML
    private Label UpdateStatus;

    /** TextField for the subscriber's ID (displayed but not editable). */
    @FXML
    private TextField txtSubscriber_id;

    /** TextField for the subscriber's name (displayed but not editable). */
    @FXML
    private TextField txtSubscriber_name;

    /** TextField for the subscriber's phone number (editable). */
    @FXML
    private TextField txtSubscriber_phoneNumber;

    /** TextField for the subscriber's email address (editable). */
    @FXML
    private TextField txtSubscriber_email;

    /** Button to close the current window (not used in provided code, but declared). */
    @FXML
    private Button btnClose;

    /** Button to submit the updated contact information. */
    @FXML
    private Button btnUpdate;

    /** Button to return to the subscriber's main window. */
    @FXML
    private Button btnReturnToSubscriberForm;

    /** Button potentially for registering a new user (not used in provided code, but declared). */
    @FXML
    private Button btnRegister;

    /**
     * Represents the currently logged-in subscriber. 
     * Pulled from the {@link SubscriberWindowController#currentSubscriber}.
     */
    private Subscriber currentSub = SubscriberWindowController.currentSubscriber;

    /**
     * Initializes the controller after the FXML is loaded. 
     * Sets up default field values from the current subscriber and disables editing for ID & name.
     *
     * @param arg0 The location used to resolve relative paths (not used here).
     * @param arg1 The resources for localization, or null if not used.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        refreshSubscriberData();

        // Prevent editing of subscriber ID and name.
        txtSubscriber_id.setDisable(true);
        txtSubscriber_name.setDisable(true);

        // Populate the fields with current subscriber data.
        txtSubscriber_id.setText(String.valueOf(currentSub.getSubscriber_id()));
        txtSubscriber_name.setText(currentSub.getSubscriber_name());
        txtSubscriber_email.setText(currentSub.getSubscriber_email());
        txtSubscriber_phoneNumber.setText(currentSub.getSubscriber_phone_number());
    }

    /**
     * Fetches the latest subscriber data from the server and updates {@link #currentSub}.
     */
    private void refreshSubscriberData() {
        ClientUI.chat.accept("Fetch:" + currentSub.getSubscriber_id());
        waitForServerResponse();
        currentSub = ChatClient.s1;  // Updated subscriber instance from the server
    }

    /**
     * Event handler for the "Update" button. 
     * Sends the modified phone number and email to the server to update the subscriber's record.
     * <p>
     * Validates that both fields are not empty before sending.
     * Displays success/failure messages to {@link #UpdateStatus}.
     * </p>
     *
     * @param event The {@link ActionEvent} triggered by clicking the "Update" button.
     * @throws Exception If an error occurs during server communication.
     */
    public void btnUpdate(ActionEvent event) throws Exception {
        String phoneNumber = txtSubscriber_phoneNumber.getText();
        String email = txtSubscriber_email.getText();
        String id = txtSubscriber_id.getText();

        // Validation checks
        if (email.isEmpty() && phoneNumber.isEmpty()) {
            showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(
                    UpdateStatus,
                    "Phone number and Email must be entered.",
                    "-fx-text-fill: red;",
                    2
            );
            return;
        } else if (email.isEmpty()) {
            showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(
                    UpdateStatus,
                    "Email must be entered.",
                    "-fx-text-fill: red;",
                    2
            );
            return;
        } else if (phoneNumber.isEmpty()) {
            showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(
                    UpdateStatus,
                    "Phone number must be entered.",
                    "-fx-text-fill: red;",
                    2
            );
            return;
        }

        // Send update request to the server
        String obj = id + "," + phoneNumber + "," + email;
        ClientUI.chat.accept("Update:" + obj);
        waitForServerResponse();

        // Server side sets ChatClient.alertIndicator (true if success, false otherwise)
        if (ChatClient.alertIndicator) {
            showColoredLabelMessageOnGUI(UpdateStatus,
                    "You have successfully updated your personal information.",
                    "-fx-text-fill: green;");
        } else {
            showColoredLabelMessageOnGUI(UpdateStatus,
                    "Updating your personal information has failed.",
                    "-fx-text-fill: red;");
        }
    }

    /**
     * Event handler for the "Return To Subscriber Form" button. 
     * Navigates back to the {@link SubscriberWindowController}.
     *
     * @param event The {@link ActionEvent} triggered by clicking the button.
     */
    public void btnReturnToSubscriberForm(ActionEvent event) {
        openWindow(
                event,
                "/gui/SubscriberWindow/SubscriberWindow.fxml",
                "/gui/SubscriberWindow/SubscriberWindow.css",
                "Subscriber View"
        );
    }
}
