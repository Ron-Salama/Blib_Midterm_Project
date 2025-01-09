package gui.ServerLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import gui.baseController.BaseController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * Controller class for the message log window. Displays a dynamic log of messages received by the server.
 */
public class ServerLogFrameController extends BaseController implements Initializable {

    @FXML
    private TextArea logTextArea;

    /**
     * Appends a message to the log.
     *
     * @param message The message to append to the log.
     */
    public void appendLog(String message) {
        System.out.println("Appending to TextArea: " + message);
        Platform.runLater(() -> {
            if (logTextArea != null) {
                logTextArea.appendText(message + "\n");
            } else {
                System.out.println("logTextArea is null!");
            }
        });
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	logTextArea.appendText("Server log initialized..\n");
    	
        loadLogFile(); // Load the log file content into the TextArea
        
    }
    
    public void loadLogFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("logic/serverLog.txt"))) {
            StringBuilder logContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                logContent.append(line).append("\n");
            }

            // Update the TextArea with the file content on the JavaFX Application thread
            Platform.runLater(() -> logTextArea.setText(logContent.toString()));
        } catch (IOException e) {
            System.out.println("Error reading the log file: " + e.getMessage());
        }
    }
}
