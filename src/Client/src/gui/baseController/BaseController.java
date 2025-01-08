package gui.baseController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

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
    
}
