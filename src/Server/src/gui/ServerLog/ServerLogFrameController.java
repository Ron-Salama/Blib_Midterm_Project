package gui.ServerLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
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

    private static final String LOG_FILE_PATH = "logic/serverLog.txt";

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
        loadLogFile(); // Load the existing log file content
        startFileWatcher(); // Start monitoring the log file for updates
    }

    /**
     * Reads the entire log file and loads its content into the TextArea.
     */
    public void loadLogFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
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

    /**
     * Starts a file watcher to monitor the log file for updates and dynamically update the TextArea.
     */
    private void startFileWatcher() {
        Thread fileWatcherThread = new Thread(() -> {
            try {
                Path logFilePath = Paths.get(LOG_FILE_PATH);
                Path parentDir = logFilePath.getParent();

                WatchService watchService = FileSystems.getDefault().newWatchService();
                parentDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY &&
                            logFilePath.getFileName().equals(event.context())) {

                            // Read the new content and update the TextArea
                            Platform.runLater(this::appendNewLogEntries);
                        }
                    }
                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Error watching log file: " + e.getMessage());
            }
        });

        fileWatcherThread.setDaemon(true); // Allow the thread to stop when the application exits
        fileWatcherThread.start();
    }

    /**
     * Appends only the new log entries from the log file to the TextArea.
     */
    private void appendNewLogEntries() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
            String line;
            StringBuilder newLogEntries = new StringBuilder();

            // Read each line and append to the TextArea
            while ((line = reader.readLine()) != null) {
                newLogEntries.append(line).append("\n");
            }

            // Append the new content to the TextArea
            logTextArea.appendText(newLogEntries.toString());
        } catch (IOException e) {
            System.out.println("Error reading the log file: " + e.getMessage());
        }
    }
}
