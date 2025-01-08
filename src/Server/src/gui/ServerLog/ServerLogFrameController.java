package gui.ServerLog;

import gui.baseController.BaseController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * Controller class for the message log window. Displays a dynamic log of messages received by the server.
 */
public class ServerLogFrameController extends BaseController {

    @FXML
    private TextArea logTextArea;

    /**
     * Appends a message to the log.
     *
     * @param message The message to append to the log.
     */
    public void appendLog(String message) {
        Platform.runLater(() -> logTextArea.appendText(message + "\n"));
    }
}
