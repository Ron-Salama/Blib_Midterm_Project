package gui.baseController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import client.ChatClient;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * BaseController is an abstract class that provides utility methods for window and UI management 
 * in a JavaFX application.
 */
public abstract class BaseController {

    /**
     * Opens a new window and hides the current one.
     *
     * @param event       The ActionEvent triggering this method.
     * @param fxmlPath    The path to the FXML file for the new window.
     * @param cssPath     The path to the CSS file for the new window. Pass null if no CSS is needed.
     * @param windowTitle The title of the new window.
     */
    protected void openWindow(ActionEvent event, String fxmlPath, String cssPath, String windowTitle) {
        try {
            // Hide the current window
            ((Node) event.getSource()).getScene().getWindow().hide();

            // Load the new window
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane root = loader.load();

            // Initialize the new window
            Stage primaryStage = new Stage();
            Scene scene = new Scene(root);

            if (cssPath != null) {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            }
            
            loadIcon(primaryStage);
            primaryStage.setTitle(windowTitle);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Sets up and displays a new primary stage with the specified FXML and CSS files.
     *
     * @param primaryStage The primary stage to initialize.
     * @param fxmlPath     The path to the FXML file.
     * @param cssPath      The path to the CSS file (can be null if not required).
     * @param title        The title of the window.
     * @throws Exception If there is an issue loading the FXML file.
     */
    protected void start(Stage primaryStage, String fxmlPath, String cssPath, String title) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(root);

        if (cssPath != null && !cssPath.isEmpty()) {
            scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        }
        
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Sets up and displays a new primary stage with optional Label customization.
     *
     * @param primaryStage The primary stage to initialize.
     * @param fxmlPath     The path to the FXML file.
     * @param cssPath      The path to the CSS file (can be null if not required).
     * @param title        The title of the window.
     * @param optionalLabel An optional Label to customize (can be null).
     * @param initialText  Initial text for the Label (ignored if optionalLabel is null).
     * @param style        CSS style for the Label (ignored if optionalLabel is null).
     * @throws Exception If there is an issue loading the FXML file.
     */
    protected void start(Stage primaryStage, String fxmlPath, String cssPath, String title, Label optionalLabel, String initialText, String style) throws Exception {
    	
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(root);

        if (cssPath != null && !cssPath.isEmpty()) {
            scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        }

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);

        // Optional customization for the Label (if provided)
        if (optionalLabel != null) {
            optionalLabel.setStyle(style != null ? style : "");
            optionalLabel.setText(initialText != null ? initialText : "");
        }

        primaryStage.show();
    }

    /**
     * Displays an alert dialog with the specified title and message.
     *
     * @param title   The title of the alert dialog.
     * @param message The message content of the alert dialog.
     */
    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Displays a message in a specified Label with a given style.
     * Note: The label is supposed to be empty beforehand.
     *
     * @param label   The Label where the message will be displayed.
     * @param message The message to display in the Label.
     * @param color   The CSS style string specifying the color or other visual properties.
     */
    protected void showColoredLabelMessageOnGUI(Label label, String message, String color) {
    	label.setText(message);
        label.setStyle(color);
    }
    
    /**
     * Displays a message in a Label and clears it after a specified delay.
     *
     * @param label          The Label where the message will be displayed.
     * @param message        The message to display.
     * @param style          The CSS style for the Label.
     * @param delayInSeconds The delay in seconds before clearing the message.
     */
    protected void showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(Label label, String message, String style, int delayInSeconds) {
        Platform.runLater(() -> {
            label.setText(message);
            label.setStyle(style);

            // Create a PauseTransition to clear the message after 3 seconds
            PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(delayInSeconds));
            pause.setOnFinished(e -> label.setText("")); // Clear the label
            pause.play();
        });
    }
    
    /**
     * Determines if a subscriber is frozen based on their status.
     *
     * @param subscriberStatus The status of the subscriber.
     * @return true if the subscriber is frozen, false otherwise.
     */
    protected Boolean isSubsriberFrozen(String subscriberStatus) {
    	if (subscriberStatus.equals("Not Frozen")) {
    		return false;
    	}
    	return true;
    }

    /**
     * Loads the application icon onto the primary stage.
     *
     * @param primaryStage The primary stage to load the icon onto.
     */
    private void loadIcon(Stage primaryStage) {
        String iconRelativePath = "/assets/BLib_Icon.png"; 

        // Load the icon image
        Image icon = new Image(getClass().getResourceAsStream(iconRelativePath));
        
        // Set the icon for the primary stage.
        primaryStage.getIcons().add(icon);
    }

   /**
    * Introduces a delay in execution for the specified number of milliseconds.
    *
    * @param milliSeconds The number of milliseconds to delay.
    * @throws InterruptedException If the thread is interrupted during the delay.
    */
   protected void addDelayInMilliseconds(int milliSeconds) throws InterruptedException {
	   TimeUnit.MILLISECONDS.sleep(milliSeconds);
   }
   
   /**
    * Waits for a server response by checking a shared flag.
    */
   protected void waitForServerResponse() {
	    while (!ChatClient.messageReceivedFromServer) {
	        try {
	            Thread.sleep(10); // Sleep for 10 milliseconds
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt(); // Restore interrupt status
	            break;
	        }
	    }
	    ChatClient.messageReceivedFromServer = false; // Lock the window again for the next run.
	}
}
