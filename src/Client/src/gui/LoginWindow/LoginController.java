package gui.LoginWindow;

import client.ChatClient;
import client.ClientUI;
import gui.baseController.BaseController;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Subscriber;

/**
 * Controller for the Library Frame window in the Library Management Tool.
 * 
 * <p>This class handles user interactions for entering a Librarian or Subscriber ID,
 * validating the ID, and navigating to the appropriate window based on the role.</p>
 *
 * @since 1.0
 */
public class LoginController extends BaseController {

    private LoginController lfc;

    @FXML
    private Button btnExit = null;

    @FXML
    private Button btnSend = null;

    @FXML
    private TextField idtxt;

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
            System.out.println("You must enter an ID number");
            awaitingLoginText.setStyle("-fx-text-fill: red;");
            awaitingLoginText.setText("You must enter an ID number.");
            return;
        }
        ChatClient.s1.setSubscriber_id(-1);
        ChatClient.l1.setLibrarian_id(-1);
        ClientUI.chat.accept("Fetch:" + id);

        PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(0.1));
        pause.setOnFinished(e -> {
            try {
                handleResponse(event);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        pause.play();
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
        System.out.println("Librarian ID: " + ChatClient.l1.getLibrarian_id());
        System.out.println("Subscriber ID: " + ChatClient.s1.getSubscriber_id());

        if (ChatClient.l1.getLibrarian_id() != -1) {
            System.out.println("Librarian ID Found");
            awaitingLoginText.setStyle("-fx-text-fill: green;");
            awaitingLoginText.setText("Welcome Back Librarian " + ChatClient.l1.getLibrarian_name());
            navigateToLibrarianWindow(event);
        } else if (ChatClient.s1.getSubscriber_id() != -1) {
            System.out.println("Subscriber ID Found");
            awaitingLoginText.setStyle("-fx-text-fill: green;");
            awaitingLoginText.setText("Welcome Back Subscriber " + ChatClient.s1.getSubscriber_name());
            navigateToSubscriberWindow(event);
        } else {
            System.out.println("No matching ID found for Librarian or Subscriber");
            awaitingLoginText.setStyle("-fx-text-fill: red;");
            awaitingLoginText.setText("No user found.");
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
              "Library Management Tool", 
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
     * Loads subscriber data into the current controller.
     *
     * @param s1 the {@link Subscriber} object to load.
     */
    public void loadSubscriber(Subscriber s1) {
        this.lfc.loadSubscriber(s1);
    }

    /**
     * Displays a message in the console.
     *
     * @param message the message to display.
     */
    public void display(String message) {
        System.out.println(message);
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
                   "Library Management Tool");
    }
    private void navigateToLibrarianWindow(ActionEvent event) throws Exception {
        openWindow(event, 
                   "/gui/LibrarianWindow/LibrarianFrame.fxml", 
                   "/gui/LibrarianWindow/LibrarianFrame.css", 
                   "Library Management Tool");
}
}