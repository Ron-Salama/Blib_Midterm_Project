package clientTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Label;
import java.awt.event.ActionEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import gui.baseController.BaseController;
import javafx.stage.Stage;

/**
 * Unit test for BaseController class.
 */
public class BaseControllerTest extends ApplicationTest {

    private BaseController controller;

    @BeforeEach
    public void setUp() {
        // Create a concrete implementation of BaseController for testing
        controller = new BaseController() {
        };
    }

    @Test
    public void testShowAlert() {
        // Call the showAlert method
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

        // Call the start method
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

        // Call the openWindow method (ensure no exceptions are thrown)
        assertDoesNotThrow(() -> controller.openWindow(event, "/path/to/fxml", null, "New Window Title"));
    }
}
