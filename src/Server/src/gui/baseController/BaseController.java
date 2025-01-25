package gui.baseController;

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

import java.io.IOException;

/**
 * An abstract base controller class providing common utility methods for window management,
 * scene transitions, and user interface behaviors in a JavaFX application.
 * <p>
 * Subclasses can extend {@code BaseController} to inherit these helper methods,
 * simplifying the process of loading FXML files, switching between windows,
 * styling UI components, and displaying error or feedback messages.
 * </p>
 */
public abstract class BaseController {

    /**
     * Opens a new window (based on an FXML file) and hides the current one.
     *
     * <p>This method:
     * <ul>
     *   <li>Hides the existing window triggered by {@code event}.</li>
     *   <li>Loads the specified FXML resource into a new {@link Stage}.</li>
     *   <li>Applies an optional CSS stylesheet if provided.</li>
     *   <li>Sets a custom icon for the new window and shows it.</li>
     * </ul>
     * </p>
     *
     * @param event       The {@link ActionEvent} that triggered this window transition (e.g., a button click).
     * @param fxmlPath    The path to the FXML file for the new window (e.g., "/gui/ExampleView.fxml").
     * @param cssPath     The path to the CSS file for styling the new window (can be {@code null} if not needed).
     * @param windowTitle The title to be displayed on the new window's title bar.
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

            // Apply CSS if provided
            if (cssPath != null) {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            }

            loadIcon(primaryStage);           // Set application icon
            primaryStage.setTitle(windowTitle);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes and displays a {@link Stage} with the specified FXML and optional CSS file.
     *
     * <p>This method:
     * <ul>
     *   <li>Loads an FXML file into a {@link Parent} node.</li>
     *   <li>Creates a new {@link Scene} and optionally applies a CSS stylesheet.</li>
     *   <li>Sets the window title and shows the stage.</li>
     * </ul>
     * </p>
     *
     * @param primaryStage The primary {@link Stage} to set up.
     * @param fxmlPath     The path to the FXML file (e.g., "/gui/MainView.fxml").
     * @param cssPath      The path to the CSS file for styling (optional, can be {@code null} or empty).
     * @param title        The title of the window.
     * @throws Exception If loading the FXML fails, or any other error occurs in the process.
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
     * Initializes and displays a {@link Stage} with optional label customization.
     *
     * <p>This method:
     * <ul>
     *   <li>Loads an FXML file into a {@link Parent} node.</li>
     *   <li>Creates a new {@link Scene} and optionally applies a CSS stylesheet.</li>
     *   <li>Optionally customizes a {@link Label} (if provided).</li>
     *   <li>Sets the window title and shows the stage.</li>
     * </ul>
     * </p>
     *
     * @param primaryStage  The primary {@link Stage} to set up.
     * @param fxmlPath      The path to the FXML file.
     * @param cssPath       The path to the CSS file for styling (optional, can be {@code null} or empty).
     * @param title         The title of the window.
     * @param optionalLabel A {@link Label} to optionally customize (can be {@code null} if unused).
     * @param initialText   The initial text for the label (ignored if {@code optionalLabel} is null).
     * @param style         A CSS style string to be applied to the label (ignored if {@code optionalLabel} is null).
     * @throws Exception If loading the FXML fails, or any other error occurs in the process.
     */
    protected void start(Stage primaryStage, String fxmlPath, String cssPath, String title,
                         Label optionalLabel, String initialText, String style) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(root);

        if (cssPath != null && !cssPath.isEmpty()) {
            scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        }

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);

        // If an optional label is provided, apply custom text and style
        if (optionalLabel != null) {
            if (style != null) {
                optionalLabel.setStyle(style);
            }
            if (initialText != null) {
                optionalLabel.setText(initialText);
            }
        }

        primaryStage.show();
    }

    /**
     * Displays an alert dialog with the specified title and message.
     * <p>
     * The alert dialog uses {@link Alert.AlertType#ERROR}, and waits for the user to close it.
     * </p>
     *
     * @param title   The title for the alert dialog.
     * @param message The message content to display in the alert dialog.
     */
    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a message in the specified {@link Label} with a given style (e.g., color).
     * <p>This method overwrites the Label's text and style, so ensure the Label was intended for this use.</p>
     *
     * @param label   The {@link Label} where the message should appear.
     * @param message The text message to display.
     * @param color   A valid CSS style string (e.g., "-fx-text-fill: red;") to apply to the label.
     */
    protected void showColoredLabelMessageOnGUI(Label label, String message, String color) {
        label.setText(message);
        label.setStyle(color);
    }

    /**
     * Loads and sets the application icon for a given {@link Stage}.
     * <p>
     * The icon is loaded from the resource "/assets/BLib_Server_Icon.png". 
     * Modify this path if your project's assets are located elsewhere.
     * </p>
     *
     * @param primaryStage The stage to which the icon should be applied.
     */
    private void loadIcon(Stage primaryStage) {
        String iconRelativePath = "/assets/BLib_Server_Icon.png";

        // Load the icon image
        Image icon = new Image(getClass().getResourceAsStream(iconRelativePath));

        // Set the icon for the primary stage
        primaryStage.getIcons().add(icon);
    }

    /**
     * Parses a string containing borrowed book information, splitting it by commas.
     * <p>Designed to convert a comma-separated string into an array of relevant book data fields.</p>
     *
     * @param borrowedBook A comma-separated string representing borrowed book data.
     * @return An array of string tokens parsed from the input.
     */
    public String[] parseBorrowedBook(String borrowedBook) {
        return borrowedBook.split(",");
    }
}
