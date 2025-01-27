package gui.IPInputWindow;

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
 * Controller class for the IP Input window of the Library Management Tool.
 * 
 * <p>This class manages the user interactions for inputting the server IP address,
 * validating it, and navigating to the main menu if the connection is successful.</p>
 */
public class IPInputController extends BaseController {

    /** Button to exit the application. */
    @FXML
    private Button btnExit = null;

    /** Button to send the entered IP address for validation. */
    @FXML
    private Button btnSend = null;

    /** TextField where the user inputs the IP address. */
    @FXML
    private TextField IPtxt;

    /** Label showing messages related to the connection status. */
    @FXML
    private Label awaitingLoginText;

    /** Label displaying a welcome message. */
    @FXML
    private Label welcomeLabel;

    /**
     * Retrieves the IP address entered by the user.
     *
     * @return the IP address entered in the {@link #IPtxt} TextField.
     */
    private String getIP() {
        return IPtxt.getText();
    }

    /**
     * Sends the entered IP address to the server for validation and handles the response.
     *
     * @param event the event triggered by clicking the send button.
     * @throws Exception if an error occurs while sending the IP address.
     */
    public void Send(ActionEvent event) throws Exception {
        String ip = getIP();
        if (ip.trim().isEmpty()) {
        	showColoredLabelMessageOnGUI(awaitingLoginText, "You must enter an IP address.", "-fx-text-fill: red;");
            return;
        }

        ClientUI.createChatConnection(ip);
        
        ClientUI.chat.accept("IP:" + ip);

        waitForServerResponse();
    
        if (!ChatClient.isIPValid) {
        	showColoredLabelMessageOnGUI(awaitingLoginText, "Invalid IP address.", "-fx-text-fill: red;");
            System.err.println("ALERT: Invalid IP detected!");
        } else {
        	String labelMessage = "Connected successfully to IP: " + ip;
        	showColoredLabelMessageOnGUI(awaitingLoginText, labelMessage, "-fx-text-fill: green;");
            
            openMainMenu(event);
        }
    }

    /**
     * Opens the main menu of the application.
     *
     * @param event the event triggered by successfully connecting to the server.
     */
    private void openMainMenu(ActionEvent event) {
        openWindow(event, 
                   "/gui/MainMenu/MainMenuFrame.fxml", 
                   "/gui/MainMenu/MainMenuFrame.css", 
                   "MainMenu");
    }

    /**
     * Starts the IP Input window.
     *
     * @param primaryStage the primary stage of the application.
     * @throws Exception if an error occurs while starting the stage.
     */
    public void start(Stage primaryStage) throws Exception {
        start(primaryStage, 
              "/gui/IPInputWindow/IPInputFrame.fxml", 
              "/gui/IPInputWindow/IPInputFrame.css", 
              "Library Management Tool");
    }

    /**
     * Exits the application.
     *
     * @param event the event triggered by clicking the exit button.
     * @throws Exception if an error occurs while exiting.
     */
    public void getExitBtn(ActionEvent event) throws Exception {
    	if (ChatClient.isIPValid != true) { // Meaning there`s no connection and the client exists by itself.
    		System.exit(1);
    	}
        ClientUI.chat.accept("EXIT:");
    }
}