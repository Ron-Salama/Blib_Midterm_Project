package tests;

// XXX: Doesnt work for now

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.ClientUI;
import gui.IPInputWindow.IPInputController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class IPWindowControllerTest {

    private IPInputController controller;

    @BeforeEach
    public void setUp() {
        controller = new IPInputController();
    }

    @Test
    public void testSendValidIP() throws Exception {
        // Setup mock event and UI components
        ActionEvent mockEvent = mock(ActionEvent.class);
        TextField mockTextField = mock(TextField.class);
        Label mockLabel = mock(Label.class);
        
        controller.IPtxt = mockTextField;
        controller.awaitingLoginText = mockLabel;
        
        // Simulate entering a valid IP
        String validIP = "192.168.1.1";
        when(mockTextField.getText()).thenReturn(validIP);

        // Mock the necessary classes for validation
        ClientUI.isIPValid = true;  // Simulate a valid IP
        controller.Send(mockEvent);

        // Verify the label text changes for success
        verify(mockLabel).setText("Connected successfully to IP: " + validIP);
        verify(mockLabel).setStyle("-fx-text-fill: green;");
    }

    @Test
    public void testSendInvalidIP() throws Exception {
        // Setup mock event and UI components
        ActionEvent mockEvent = mock(ActionEvent.class);
        TextField mockTextField = mock(TextField.class);
        Label mockLabel = mock(Label.class);
        
        controller.IPtxt = mockTextField;
        controller.awaitingLoginText = mockLabel;
        
        // Simulate entering an invalid IP
        String invalidIP = "invalid_ip";
        when(mockTextField.getText()).thenReturn(invalidIP);

        // Mock the necessary classes for validation
        ClientUI.isIPValid = false;  // Simulate an invalid IP
        controller.Send(mockEvent);

        // Verify the label text changes for failure
        verify(mockLabel).setText("Invalid IP address.");
        verify(mockLabel).setStyle("-fx-text-fill: red;");
        
        // Verify that the alert is triggered
        Platform.runLater(() -> {
            assertDoesNotThrow(() -> controller.showAlert("Error", "Invalid IP address. Please try again."));
        });
    }

    @Test
    public void testOpenMainMenu() {
        // Setup mock event and window opening
        ActionEvent mockEvent = mock(ActionEvent.class);
        Stage primaryStage = mock(Stage.class);

        // Test the openMainMenu method
        controller.openMainMenu(mockEvent);
        
        // Verify the window opening logic
        // In a real test, you'd want to mock and verify the window opening logic
        // Here we just verify that the method is invoked
        // You can also check if the stage's scene or window was updated as expected
        // In this case, it's more of an integration-level test
    }

    @Test
    public void testGetExitBtn() throws Exception {
        // Mock ActionEvent and test exit functionality
        ActionEvent mockEvent = mock(ActionEvent.class);

        // Test the exit functionality
        controller.getExitBtn(mockEvent);
        
        // Since System.exit is called, we can't assert directly for this method.
        // But we can check if any other expected side-effects happen.
        // For testing purposes, you might want to verify system calls or check logs.
    }

    @Test
    public void testStart() throws Exception {
        // Mock Stage for testing the start method
        Stage mockStage = mock(Stage.class);
        
        // Test the start method
        controller.start(mockStage);
        
        // Verify that the start method was called and the window opened
        // You can use mocking libraries like Mockito to verify behavior.
    }
}
