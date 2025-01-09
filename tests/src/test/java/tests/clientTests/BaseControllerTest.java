package tests.clientTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import gui.baseController.BaseController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Test class for BaseController.
 */
public class BaseControllerTest extends ApplicationTest {

    private TestBaseController testController;

    @Override
    public void start(Stage stage) {
        // Setup a dummy stage for testing purposes
        stage.setTitle("Test Stage");
        stage.show();
    }

    @BeforeEach
    public void setUp() {
        testController = new TestBaseController();
    }

    @Test
    public void testShowAlert() {
        Platform.runLater(() -> {
            assertDoesNotThrow(() -> testController.showAlert("Test Alert", "This is a test message."));
        });
    }

    /**
     * Test subclass to expose protected methods for testing.
     */
    private static class TestBaseController extends BaseController {

        @Override
        public void showAlert(String title, String message) {
            super.showAlert(title, message);
        }
    }
}
