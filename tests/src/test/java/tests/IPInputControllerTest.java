package tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import client.ClientUI;
import gui.IPInputWindow.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Subscriber;

/**
 * Test class for IPInputController.
 * This class tests the various functionalities of the IP Input Controller.
 */
public class IPInputControllerTest extends ApplicationTest {

    private IPInputController ipInputController;
    private Stage stage;
    private TextField ipTextField;
    private Label awaitingLoginText;
    private Button sendButton;
    private Button exitButton;

    @BeforeEach
    public void setUp() {
        ipInputController = new IPInputController();
        ipTextField = new TextField();
        awaitingLoginText = new Label();
        sendButton = new Button();
        exitButton = new Button();

        ipInputController.IPtxt = ipTextField;
        ipInputController.awaitingLoginText = awaitingLoginText;
        ipInputController.btnSend = sendButton;
        ipInputController.btnExit = exitButton;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        try {
            ipInputController.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the `getIP()` method.
     */
    @Test
    public void testGetIP() {
        ipTextField.setText("127.0.0.1");
        assertEquals("127.0.0.1", ipInputController.IPtxt.getText());
    }

    /**
     * Tests the `Send()` method with a valid IP.
     */
    @Test
    public void testSendWithValidIP() {
        Platform.runLater(() -> {
            ipTextField.setText("127.0.0.1");
            ClientUI.isIPValid = true;

            ActionEvent event = new ActionEvent(sendButton, null);
            assertDoesNotThrow(() -> ipInputController.Send(event));

            // Ensure awaitingLoginText is updated correctly
            assertEquals("Connected successfully to IP: 127.0.0.1", awaitingLoginText.getText());
            assertEquals("-fx-text-fill: green;", awaitingLoginText.getStyle());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Tests the `Send()` method with an invalid IP.
     */
    @Test
    public void testSendWithInvalidIP() {
        Platform.runLater(() -> {
            ipTextField.setText("192.168.1.256");
            ClientUI.isIPValid = false;

            ActionEvent event = new ActionEvent(sendButton, null);
            assertDoesNotThrow(() -> ipInputController.Send(event));

            // Ensure awaitingLoginText is updated correctly
            assertEquals("Invalid IP address.", awaitingLoginText.getText());
            assertEquals("-fx-text-fill: red;", awaitingLoginText.getStyle());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Tests the `Send()` method with an empty IP.
     */
    @Test
    public void testSendWithEmptyIP() {
        Platform.runLater(() -> {
            ipTextField.setText("");

            ActionEvent event = new ActionEvent(sendButton, null);
            assertDoesNotThrow(() -> ipInputController.Send(event));

            // Ensure awaitingLoginText is updated correctly
            assertEquals("You must enter an IP address.", awaitingLoginText.getText());
            assertEquals("-fx-text-fill: red;", awaitingLoginText.getStyle());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Tests the `openMainMenu()` method.
     */
    @Test
    public void testOpenMainMenu() {
        Platform.runLater(() -> {
            ActionEvent event = new ActionEvent(sendButton, null);
            assertDoesNotThrow(() -> ipInputController.Send(event));

            // Validate the main menu window opens successfully
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Tests the `getExitBtn()` method.
     */
    @Test
    public void testGetExitBtn() {
        Platform.runLater(() -> {
            ActionEvent event = new ActionEvent(exitButton, null);
            assertDoesNotThrow(() -> ipInputController.getExitBtn(event));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Tests the `display()` method.
     */
    @Test
    public void testDisplay() {
        Platform.runLater(() -> {
            String testMessage = "Test Message";
            ipInputController.display(testMessage);
            // Capture console output (if necessary) or assert expected behavior
        });
    }

    /**
     * Tests the `loadSubscriber()` method.
     */
    @Test
    public void testLoadSubscriber() {
        Subscriber mockSubscriber = new Subscriber();
        Platform.runLater(() -> {
            assertDoesNotThrow(() -> ipInputController.loadSubscriber(mockSubscriber));
            // Add assertions as needed to verify behavior
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
