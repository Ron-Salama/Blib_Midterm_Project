package gui.BorrowBookWindow;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import client.ChatClient;
import client.ClientController;
import client.ClientUI;
import common.ChatIF;
import gui.MainMenu.MainMenuController;
import gui.SearchWindow.SearchFrameController;
import gui.SubscriberRequestsWindows.SubscriberRequestsWindowsController;
import gui.SubscriberWindow.SubscriberWindowController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.Subscriber;
import gui.baseController.*;

/**
 * Controller class for the IP Input window of the Library Management Tool.
 * 
 * <p>This class manages the user interactions for inputting the server IP address,
 * validating it, and navigating to the main menu if the connection is successful.</p>
 */
public class BorrowBookController extends BaseController implements Initializable {
    Subscriber currentSub = SubscriberWindowController.currentSubscriber;
    
    @FXML
    private Button btnExit = null;

    @FXML
    private Button btnSubmit = null;

    @FXML
    private Button btnBack = null;
    @FXML
    private Button btnSearch = null;
    @FXML
    private Button btnSubmitToLibrarian = null;
    @FXML
    private Button btnMainMenu = null;
    @FXML
    private TextField IDtxt;
    @FXML
    private Label awaitingTextID;
    @FXML
    private Button btnReserve = null;
    @FXML
    private Label Book_Description;

    String bookId = "";
    String bookName = "";
    String borrowStatus = "CAN_BORROW";  // String to hold the borrow status

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        awaitingTextID.setText("");
        btnReserve.setVisible(false);
    }

    public void start(Stage primaryStage) throws Exception {
        start(primaryStage, 
              "/gui/BorrowBookWindow/BorrowBookFrame.fxml", 
              "/gui/BorrowBookWindow/BorrowBookFrame.css", 
              "Library Management Tool");
    }

    public void Submit(ActionEvent event) throws Exception {
        bookId = IDtxt.getText();
        ChatClient.awaitResponse = true; // Ensure waiting for response
        ClientUI.chat.accept("GetBookInfo:" + bookId);  // Request book info

        // Wait until response is received
        PauseTransition pause = new PauseTransition(Duration.seconds(0.1)); // Adjust duration if necessary
        pause.setOnFinished(e -> {
            if (ChatClient.BorrowedBookInfo != null) {
                Book_Description.setText(
                    "Book ID: " + bookId + "\n" +
                    "Book Name: " + ChatClient.BorrowedBookInfo[1] + "\n" +
                    "Subject: " + ChatClient.BorrowedBookInfo[2] +"\n" +
                    "Description: " + ChatClient.BorrowedBookInfo[3] + "\n" +
                    "Available Copies: " + ChatClient.BorrowedBookInfo[4] +"\n" +
                    "Location on Shelf: " + ChatClient.BorrowedBookInfo[5]
                );
                bookName = ChatClient.BorrowedBookInfo[1];

                // Update borrow status based on available copies
                if (Integer.parseInt(ChatClient.BorrowedBookInfo[4]) <= 0) {
                    borrowStatus = "NO_COPIES";
                    btnReserve.setVisible(true);
                    awaitingTextID.setText("There are no more Copies of the book " + bookName + "\nWould you like to Reserve it?");
                } else {
                    borrowStatus = "CAN_BORROW";
                    awaitingTextID.setText("");
                    btnReserve.setVisible(false);
                }
            } else {
                borrowStatus = "BOOK_NOT_FOUND";
                Book_Description.setText("No Book Found");
            }
        });
        pause.play();
    }

    public void Submit_Borrow_Request(ActionEvent event) throws Exception {
        if ("CAN_BORROW".equals(borrowStatus)) {
            // Collect subscriber and book details
            String subscriberId = "" + SubscriberWindowController.currentSubscriber.getSubscriber_id();
            String subscriberName = SubscriberWindowController.currentSubscriber.getSubscriber_name();
            
            String borrowRequest = "" + subscriberId + "," + subscriberName + "," + bookId + "," + bookName + "," + subscriberId + "," + subscriberId;
            ClientUI.chat.accept("BorrowRequest:" + borrowRequest);

            // Feedback to the user
            showAlert("Success", "Borrow request submitted successfully!\nAwaiting Librarian approval");
        } else {
            String errorMessage = getErrorMessage(borrowStatus); // Get appropriate error message
            showAlert("Error", errorMessage);
        }
    }

    public void Submit_Reserve_Request(ActionEvent event) throws Exception {
        if ("NO_COPIES".equals(borrowStatus)) {
            // Proceed with reservation logic
            showAlert("Success", "You have successfully reserved the book.");
        } else {
            showAlert("Error", "Book is available, no need to reserve.");
        }
    }

    public void Search(ActionEvent event) throws Exception {
        SearchFrameController.FlagForSearch = "SubscriberBorrower";
        openWindow(event,
                "/gui/SearchWindow/SearchFrame.fxml",
                "/gui/SearchWindow/SearchFrame.css",
                "Search a Book");
    }

    public void Back(ActionEvent event) throws Exception {
        openWindow(event,
                "/gui/SubscriberWindow/SubscriberWindow.fxml",
                "/gui/SubscriberWindow/SubscriberWindow.css",
                "MainMenu");
    }

    public void Main_Menu(ActionEvent event) throws Exception {
        SearchFrameController.FlagForSearch = "";
        openWindow(event, 
                "/gui/MainMenu/MainMenuFrame.fxml", 
                "/gui/MainMenu/MainMenuFrame.css", 
                "MainMenu");
    }

    /**
     * Returns the appropriate error message based on the borrow status.
     *
     * @param status the borrow status
     * @return the error message
     */
    private String getErrorMessage(String status) {
        switch (status) {
            case "NO_COPIES":
                return "There are no copies available for this book.";
            case "BOOK_NOT_FOUND":
                return "Book not found.";
            default:
                return "Unable to borrow this book due to an unknown error.";
        }
    }

    /**
     * Displays a message in the console.
     *
     * @param message the message to display.
     */
    public void display(String message) {
        System.out.println(message);
    }
}
