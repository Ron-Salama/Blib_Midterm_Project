package gui.ServerPort;

import gui.baseController.BaseController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import server.ServerUI;

/**
 * Controller class for specifying a server port before starting the server.
 * <p>
 * This controller enables the user to enter a port number via a text field 
 * and proceed to the server log window if the entered port is valid. It also 
 * allows exiting the application directly.
 * </p>
 * 
 * <p>Extends {@link BaseController} to reuse common window-handling methods.</p>
 * 
 * @author  
 * @version 1.0
 * @since 2025-01-01
 */
public class ServerPortFrameController extends BaseController {

    /** Temporary storage for any internal string operations (not used much in the current code). */
    private String temp = "";

    /** Button to exit the entire application. */
    @FXML
    private Button btnExit;

    /** Button to confirm the entered port number and launch the server. */
    @FXML
    private Button btnDone;

    /** A label (potentially for displaying lists or messages, not currently in active use). */
    @FXML
    private Label lbllist;

    /** A label used to display dynamic messages or error prompts. */
    @FXML
    private Label dynamicLabel;

    /** Text field where the user enters the desired server port. */
    @FXML
    private TextField portxt;

    /** An observable list (not currently used) for potential UI list logic. */
    private ObservableList<String> list;

    /**
     * Retrieves the text from the port input field.
     *
     * @return A {@code String} representing the port number entered by the user.
     */
    private String getport() {
        return portxt.getText();
    }

    /**
     * Event handler for the "Done" button. 
     * <p>
     * Validates that the port field is not empty, then:
     * <ul>
     *   <li>Closes the current window.</li>
     *   <li>Starts the server on the entered port via {@link ServerUI#runServer(String)}.</li>
     *   <li>Opens the server log window on success.</li>
     * </ul>
     * If the field is empty, displays an error message.
     * </p>
     *
     * @param event The {@link ActionEvent} triggered by clicking the "Done" button.
     * @throws Exception If any error occurs while opening the new window or running the server.
     */
    public void Done(ActionEvent event) throws Exception {
        String p = getport();

        if (p.trim().isEmpty()) {
            showColoredLabelMessageOnGUI(dynamicLabel, "You must enter a port number.", "-fx-text-fill: red;");
        } else {
            // Hide the current window
            ((Node) event.getSource()).getScene().getWindow().hide();
            // Start the server
            ServerUI.runServer(p);

            // Open the server log window
            openWindow(event,
                    "/gui/ServerLog/ServerLogFrame.fxml",
                    "/gui/ServerLog/ServerLogFrame.css",
                    "Server Log");
        }
    }

    /**
     * Entry point to initialize and show the port selection window.
     *
     * @param primaryStage The primary {@link Stage} for this view.
     * @throws Exception If an error occurs loading the FXML or applying styles.
     */
    public void start(Stage primaryStage) throws Exception {
        start(primaryStage,
                "/gui/ServerPort/ServerPort.fxml",
                "/gui/ServerPort/ServerPort.css",
                "Server - Enter Port");
        loadIcon(primaryStage);
    }

    /**
     * Event handler for the "Exit" button. 
     * <p>
     * Prints a message to the console and exits the application process.
     * </p>
     *
     * @param event The {@link ActionEvent} triggered by clicking the "Exit" button.
     * @throws Exception If any error occurs during the exit process.
     */
    public void getExitBtn(ActionEvent event) throws Exception {
        System.exit(0);
    }

    /**
     * Loads the application icon for the provided stage.
     * <p>
     * Uses "/assets/BLib_Server_Icon.png" to set an icon in the title bar.
     * </p>
     *
     * @param primaryStage The {@link Stage} for which the icon is loaded.
     */
    private void loadIcon(Stage primaryStage) {
        String iconRelativePath = "/assets/BLib_Server_Icon.png";

        // Load the icon image
        Image icon = new Image(getClass().getResourceAsStream(iconRelativePath));

        // Set the icon for the primary stage
        primaryStage.getIcons().add(icon);
    }
}
