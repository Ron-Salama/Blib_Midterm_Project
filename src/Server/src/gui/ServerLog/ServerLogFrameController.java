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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import server.EchoServer;

/**
 * Controller class for the message log window. Displays a dynamic log of messages received by the server.
 * <p>This class monitors the server log file for updates and displays new log entries in a TextArea.
 * It also shows the server's IP address on the GUI.</p>
 */
public class ServerLogFrameController extends BaseController implements Initializable {
	/** The TextArea where the log entries are displayed. */
	@FXML
    private TextArea logTextArea;
	@FXML
    private Button exitServer;
	
    /** The Label displaying the server's IP address. */
    @FXML 
    private Label serverIPDynamicText;
    
    /** The path to the server log file. */
    private static final String LOG_FILE_PATH = "src/logic/serverLog.txt";

    // File pointer to track the last read position
    private long filePointer = 0;

    /**
     * Initializes the controller by starting the log file watcher and displaying the server's IP address.
     * 
     * @param location the location used to resolve relative paths for the root object, or null if the location is not known
     * @param resources the resources used to localize the root object, or null if no localization is needed
     */
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
    public void exitServer(ActionEvent event)  throws Exception {
    	//EchoServer.Terminated = true;
    	System.out.println("Server Terminated");
    	System.exit(0);
    }
    
    /**
     * Starts a file watcher to monitor the log file for updates and dynamically update the TextArea.
     * <p>The file watcher runs in a separate thread to detect modifications to the log file and 
     * appends new log entries to the TextArea in the JavaFX Application thread.</p>
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
                System.err.println("Error watching log file: " + e.getMessage());
            }
        });

        fileWatcherThread.setDaemon(true); // Allow the thread to stop when the application exits
        fileWatcherThread.start();
    }

    /**
     * Appends only the new log entries from the log file to the TextArea.
     * <p>This method reads the new content from the log file starting from the last known position
     * and appends it to the TextArea in the JavaFX Application thread.</p>
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
            System.err.println("Error reading the log file: " + e.getMessage());
        }
    }
}
