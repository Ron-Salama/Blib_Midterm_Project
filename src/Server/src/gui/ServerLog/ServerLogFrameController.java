package gui.ServerLog;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ResourceBundle;

import gui.baseController.BaseController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;


/**
 * Controller class for the message log window. Displays a dynamic log of messages received by the server.
 */
public class ServerLogFrameController extends BaseController implements Initializable {

    @FXML
    private TextArea logTextArea;

    @FXML 
    private Label serverIPDynamicText;
    
    private static final String LOG_FILE_PATH = "src/logic/serverLog.txt";

    // File pointer to track the last read position
    private long filePointer = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startFileWatcher(); // Start monitoring the log file for updates
        try {
			showColoredLabelMessageOnGUI(serverIPDynamicText, "Server IP: " + InetAddress.getLocalHost().getHostAddress(), "-fx-text-fill: #4682B4;" // Steel Blue
					+ "");
		} catch (UnknownHostException e) {
			e.printStackTrace();
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

                            // Read and append only the new content
                            appendNewLogEntries();
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
        try (RandomAccessFile reader = new RandomAccessFile(LOG_FILE_PATH, "r")) {
            // Move to the last known position
            reader.seek(filePointer);

            String line;
            StringBuilder newLogEntries = new StringBuilder();

            // Read all new lines
            while ((line = reader.readLine()) != null) {
                newLogEntries.append(line).append("\n");
            }

            // Update the TextArea with new content on the JavaFX Application thread
            Platform.runLater(() -> logTextArea.appendText(newLogEntries.toString()));

            // Update the file pointer to the current position
            filePointer = reader.getFilePointer();
        } catch (IOException e) {
            System.out.println("Error reading the log file: " + e.getMessage());
        }
    }
}
