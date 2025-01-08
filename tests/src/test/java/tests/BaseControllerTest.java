package tests;

// XXX NOT WORKING

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import gui.baseController.BaseController;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseControllerTest extends ApplicationTest {

    private BaseController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Start JavaFX application (necessary for JavaFX to be initialized)
        controller = new BaseController() {
            // Optionally, override methods here if needed for testing
        };
    }

    @BeforeEach
    public void setUp() {
        // Create a concrete implementation of BaseController for testing
        controller = new BaseController() {
            // Optionally, override methods here if needed for testing
        };
    }

    @Test
    public void testShowAlert() {
        // Call the showAlert method with test parameters
        String title = "Test Alert";
        String message = "This is a test message.";

        // Use TestFX to simulate the alert dialog
        assertDoesNotThrow(() -> controller.showAlert(title, message));
    }

    @Test
    public void testStartWithLabelCustomization() throws Exception {
        // Mock a Stage and Label for testing
        Stage primaryStage = new Stage();
        Label label = new Label();

        // Call the start method with test parameters
        controller.start(primaryStage, "/path/to/fxml", null, "Test Title", label, "Initial Text", "-fx-font-size: 14px");

        // Verify the Label's properties
        assertEquals("Initial Text", label.getText());
        assertEquals("-fx-font-size: 14px", label.getStyle());
    }

    @Test
    public void testOpenWindow() {
        // Mock an ActionEvent
        ActionEvent event = mock(ActionEvent.class);
        when(event.getSource()).thenReturn(mock(javafx.scene.Node.class));

        // Call the openWindow method with test parameters (ensure no exceptions are thrown)
        assertDoesNotThrow(() -> controller.openWindow(event, "/path/to/fxml", null, "New Window Title"));
    }
}
