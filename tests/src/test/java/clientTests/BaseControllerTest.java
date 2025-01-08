package clientTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import gui.baseController.BaseController;

import java.lang.reflect.Method;

/**
 * Unit test for BaseController class.
 */
public class BaseControllerTest extends ApplicationTest {

    // A concrete subclass of BaseController for testing
    private static class TestBaseController extends BaseController {
        // No additional methods or overrides are needed for now
    }

    private TestBaseController controller;

    @BeforeEach
    public void setUp() {
        // Initialize the concrete subclass for testing
        controller = new TestBaseController();
    }

    @Test
    public void testShowAlert() throws Exception {
        // Use reflection to access the protected showAlert method
        Method showAlertMethod = BaseController.class.getDeclaredMethod("showAlert", String.class, String.class);
        showAlertMethod.setAccessible(true); // Make the protected method accessible

        // Call the method using reflection
        String title = "Test Alert";
        String message = "This is a test message.";
        assertDoesNotThrow(() -> showAlertMethod.invoke(controller, title, message));
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

        // Call the openWindow method and verify no exceptions are thrown
        assertDoesNotThrow(() -> controller.openWindow(event, "/path/to/fxml", null, "New Window Title"));
    }
}
