package gui.LoginWindow;

import client.ChatClient;
import client.ClientUI;
import gui.baseController.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the Library Frame window in the Library Management Tool.
 * 
 * <p>This class handles user interactions for entering a Librarian or Subscriber ID,
 * validating the ID, and navigating to the appropriate window based on the role.</p>
 */
public class LoginController extends BaseController {

	/** Button to exit the login window */
    @FXML
    private Button btnExit = null;

    /** Button to send the entered ID to the server */
    @FXML
    private Button btnSend = null;

    /** Text field for the user to enter their Librarian or Subscriber ID */
    @FXML
    private TextField idtxt;

    /** Label to display messages to the user during the login process */
    @FXML
    private Label awaitingLoginText;

    /**
     * Retrieves the ID entered by the user.
     *
     * @return the ID entered in the {@link #idtxt} TextField.
     */
    private String getID() {
        return idtxt.getText();
    }

    /**
     * Sends the entered ID to the server and handles the response asynchronously.
     *
     * @param event the event triggered by clicking the send button.
     * @throws Exception if an error occurs while sending the ID.
     */
    public void Send(ActionEvent event) throws Exception {
        String id = getID();

        if (id.trim().isEmpty()) {
        	showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(awaitingLoginText, "You must enter an ID number", "-fx-text-fill: red;", 3);
            return;
        }
        ChatClient.s1.setSubscriber_id(-1);
        ChatClient.l1.setLibrarian_id(-1);
        ClientUI.chat.accept("Fetch:" + id);

        waitForServerResponse();

        if ((ChatClient.l1.getLibrarian_id() == -1) && (ChatClient.s1.getSubscriber_id() == -1)) {
        	showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(awaitingLoginText, "No user found.", "-fx-text-fill: red;", 3);
        	return; // No user found therefore there's no need to continue.
        }
        
        handleResponse(event);
    }

    /**
     * Handles the response from the server after sending the ID.
     *
     * <p>This method determines whether the ID corresponds to a Librarian or Subscriber,
     * and performs the appropriate actions, such as displaying a welcome message or
     * navigating to a new window.</p>
     *
     * @param event the event triggered by the server response.
     * @throws Exception if an error occurs while handling the response.
     */
    private void handleResponse(ActionEvent event) throws Exception {
        if (ChatClient.l1.getLibrarian_id() != -1) {
            navigateToLibrarianWindow(event);
        } else if (ChatClient.s1.getSubscriber_id() != -1) {
            navigateToSubscriberWindow(event);
        }
    }

    /**
     * Starts the Library Frame window.
     *
     * @param primaryStage the primary stage of the application.
     * @throws Exception if an error occurs while starting the stage.
     */
    public void start(Stage primaryStage) throws Exception {
        start(primaryStage, 
              "/gui/LibraryFramehWindow/LibraryFrame.fxml", 
              "/gui/LibraryFramehWindow/LibraryFrame.css", 
              "Login", 
              null, 
              "", 
              "-fx-text-fill: green;");
    }

    /**
     * Exits the Library Frame and navigates to the main menu.
     *
     * @param event the event triggered by clicking the exit button.
     * @throws Exception if an error occurs while navigating to the main menu.
     */
    public void getExitBtn(ActionEvent event) throws Exception {
        openWindow(event, 
                   "/gui/MainMenu/MainMenuFrame.fxml", 
                   "/gui/MainMenu/MainMenuFrame.css", 
                   "Main Menu");
    }

    /**
     * Navigates to the Subscriber window if a valid Subscriber ID is provided.
     *
     * @param event the event triggered by identifying a valid Subscriber.
     * @throws Exception if an error occurs while navigating to the Subscriber window.
     */
    private void navigateToSubscriberWindow(ActionEvent event) throws Exception {
        openWindow(event, 
                   "/gui/SubscriberWindow/SubscriberWindow.fxml", 
                   "/gui/SubscriberWindow/SubscriberWindow.css", 
                   "Subsriber View");
    }
    
    /**
     * Navigates to the Librarian window if a valid Librarian ID is provided.
     *
     * @param event the event triggered by identifying a valid Librarian.
     * @throws Exception if an error occurs while navigating to the Librarian window.
     */
    private void navigateToLibrarianWindow(ActionEvent event) throws Exception {
        openWindow(event, 
                   "/gui/LibrarianWindow/LibrarianFrame.fxml", 
                   "/gui/LibrarianWindow/LibrarianFrame.css", 
                   "Librarian View");
    }
}