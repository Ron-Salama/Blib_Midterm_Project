package tests.clientTests;

import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import gui.LibrarianWindow.LibrarianController;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import javafx.event.ActionEvent;

/**
 * Test class for LibrarianController.
 * Tests the behavior of the LibrarianController's methods to ensure proper functionality.
 */
public class LibrarianControllerTest extends ApplicationTest {

    private LibrarianController librarianController;
    private ActionEvent mockEvent;

    private Button btnExit;
    private Button btnReturnToMainMenu;
    private Button btnSearchBook;
    private Button btnSearchSubscriber;
    private Button btnSubscriberRequests;
    private Button btnViewReports;

    /**
     * Initializes the JavaFX environment for testing.
     *
     * @param stage JavaFX stage for the application.
     */
    @Override
    public void start(Stage stage) {
        // Required for initializing JavaFX components in the test environment.
    }

    /**
     * Sets up the LibrarianController and its dependencies before each test.
     */
    @BeforeEach
    public void setUp() {
        librarianController = new LibrarianController();
        mockEvent = mock(ActionEvent.class);

        // Initialize buttons to simulate @FXML-injected fields
        btnExit = new Button("Exit");
        btnReturnToMainMenu = new Button("Return to Main Menu");
        btnSearchBook = new Button("Search Book");
        btnSearchSubscriber = new Button("Search Subscriber");
        btnSubscriberRequests = new Button("Subscriber Requests");
        btnViewReports = new Button("View Reports");

        // Inject buttons into the controller
        librarianController.setBtnExit(btnExit);
        librarianController.setBtnReturnToMainMenu(btnReturnToMainMenu);
        librarianController.setBtnSearchBook(btnSearchBook);
        librarianController.setBtnSearchSubscriber(btnSearchSubscriber);
        librarianController.setBtnSubscriberRequests(btnSubscriberRequests);
        librarianController.setBtnViewReports(btnViewReports);
    }

    /**
     * Tests the getExitBtn method to ensure it exits the application correctly.
     */
//    @Test
//    public void testGetExitBtn() {
//        assertDoesNotThrow(() -> librarianController.getExitBtn(mockEvent));
//    }

    /**
     * Tests the navigateToViewReports method to ensure no exceptions occur.
     */
    @Test
    public void testNavigateToViewReports() {
        assertDoesNotThrow(() -> librarianController.navigateToViewReports(mockEvent));
    }

    /**
     * Tests the navigateToSearchSubscriber method to ensure no exceptions occur.
     */
    @Test
    public void testNavigateToSearchSubscriber() {
        assertDoesNotThrow(() -> librarianController.navigateToSearchSubscriber(mockEvent));
    }

    /**
     * Tests the navigateToSubscriberRequests method to ensure proper navigation without exceptions.
     */
    @Test
    public void testNavigateToSubscriberRequests() {
        assertDoesNotThrow(() -> librarianController.navigateToSubscriberRequests(mockEvent));
    }

    /**
     * Tests the navigateToSearchWindow method to ensure it navigates correctly without exceptions.
     */
    @Test
    public void testNavigateToSearchWindow() {
        assertDoesNotThrow(() -> librarianController.navigateToSearchWindow(mockEvent));
    }

    /**
     * Tests the navigateToMainMenu method to ensure it navigates back to the main menu without exceptions.
     */
    @Test
    public void testNavigateToMainMenu() {
        assertDoesNotThrow(() -> librarianController.navigateToMainMenu(mockEvent));
    }
}
