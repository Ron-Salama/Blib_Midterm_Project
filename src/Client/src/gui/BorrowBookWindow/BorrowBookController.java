package gui.BorrowBookWindow;

import java.net.URL;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import gui.SearchWindow.SearchFrameController;
import gui.SubscriberWindow.SubscriberWindowController;
import gui.baseController.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.ClientTimeDiffController;
import logic.Subscriber;

/**
 * Controller class for the Borrow Book Window of the Library Management Tool.
 * 
 * <p>This class manages the user interactions for borrowing books, including searching for books,
 * submitting borrow requests, and reserving unavailable books. It provides feedback to users and
 * communicates with the server to handle book information and requests.</p>
 */
public class BorrowBookController extends BaseController implements Initializable {
	
	
    // The currently logged-in subscriber.
    Subscriber currentSub = SubscriberWindowController.currentSubscriber;
    
    // Controller for managing time differences and calculating dates.
    ClientTimeDiffController clockController = new ClientTimeDiffController();
    
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
    private TextField IDtxt = null;
    @FXML
    private Label awaitingTextID = null;
    @FXML
    private Button btnReserve = null;
    @FXML
    private Label Book_Description = null;
    @FXML
    private Label RequestStatus = null;

    // Stores the book ID entered by the user.
    String bookId = "";
    
    // Stores the name of the selected book.
    String bookName = "";
    
    // Number of copies of the selected book.
    int copiesNum;
    
    // Number of reserved copies of the selected book.
    int reservedCopiesNum;
    
    // Status indicating whether the book can be borrowed.
    String borrowStatus = "CAN_BORROW";  // String to hold the borrow status

    /**
     * Initializes the controller and sets up event handlers.
     * 
     * @param url the location used to resolve relative paths for the root object
     * @param resourceBundle the resources used to localize the root object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        awaitingTextID.setText("");
        btnReserve.setVisible(false);
        
        btnSubmitToLibrarian.setDisable(true); // Disable the borrow request button by default
        
        // Add an event handler to clear the details when the text field is clicked
        IDtxt.setOnMouseClicked(event -> Clear());
    }

    /**
     * Starts the Borrow Book Window.
     * 
     * @param primaryStage the primary stage for the application
     * @throws Exception if an error occurs during initialization
     */
    public void start(Stage primaryStage) throws Exception {
        start(primaryStage, 
              "/gui/BorrowBookWindow/BorrowBookFrame.fxml", 
              "/gui/BorrowBookWindow/BorrowBookFrame.css", 
              "Borrow a Book");
    }

    /**
     * Handles the submission of a book ID to retrieve book details.
     * 
     * @param event the action event triggered by the user
     * @throws Exception if an error occurs while communicating with the server
     */
    public void Submit(ActionEvent event) throws Exception {
    	showColoredLabelMessageOnGUI(RequestStatus, "", "-fx-text-fill: black;");
        bookId = IDtxt.getText();
        ChatClient.awaitResponse = true; // Ensure waiting for response
        ClientUI.chat.accept("GetBookInfo:" + bookId);  // Request book info

        waitForServerResponse();

        if (ChatClient.BorrowedBookInfo != null) {
            Book_Description.setText(
            	"Book Details:\n" +
                "Book ID: " + bookId + "\n" +
                "Book Name: " + ChatClient.BorrowedBookInfo[1] + "\n" +
                "Subject: " + ChatClient.BorrowedBookInfo[2] +"\n" +
                "Description: " + ChatClient.BorrowedBookInfo[3] + "\n" +
                "Number of Copies: " + ChatClient.BorrowedBookInfo[4] +"\n" +
                "Location on Shelf: " + ChatClient.BorrowedBookInfo[5] + "\n" +
                "Available Copies Number: " + ChatClient.BorrowedBookInfo[6] + "\n" +
                "Reserved Copies Number: " + ChatClient.BorrowedBookInfo[7] + "\n"
            	
            );
            bookName = ChatClient.BorrowedBookInfo[1];
            copiesNum = Integer.parseInt(ChatClient.BorrowedBookInfo[4]);
            reservedCopiesNum = Integer.parseInt(ChatClient.BorrowedBookInfo[7]);
            
            // Update borrow status based on available copies
            if (Integer.parseInt(ChatClient.BorrowedBookInfo[6]) <= 0) {
                borrowStatus = "NO_COPIES";
                btnReserve.setVisible(true);
                awaitingTextID.setText("There are no more Copies of the book " + bookName + "\nWould you like to Reserve it?");
                btnSubmitToLibrarian.setDisable(true); // Disable if there are no copies
            } else {
                borrowStatus = "CAN_BORROW";
                awaitingTextID.setText("");
                btnReserve.setVisible(false);
                btnSubmitToLibrarian.setDisable(false); // Enable the button if book is available
            }
        } else {
            borrowStatus = "BOOK_NOT_FOUND";
            Book_Description.setText("No Book Found");
            btnSubmitToLibrarian.setDisable(true); // Disable the button if no book is found 
        }
    }

    /**
     * Submits a borrow request for the selected book.
     * 
     * @param event the action event triggered by the user
     * @throws Exception if an error occurs during the submission process
     */
    public void Submit_Borrow_Request(ActionEvent event) throws Exception {
        if ("CAN_BORROW".equals(borrowStatus)) {
            // Collect subscriber and book details
            String subscriberId = "" + SubscriberWindowController.currentSubscriber.getSubscriber_id();
            String subscriberName = SubscriberWindowController.currentSubscriber.getSubscriber_name();
            
            String returnDate = clockController.calculateReturnDate(14);
            String borrowDate = clockController.timeNow();
            
            String borrowRequest = "" + subscriberId + "," + subscriberName + "," + bookId + "," + bookName + "," + borrowDate + "," + returnDate;
            ClientUI.chat.accept("BorrowRequest:" + borrowRequest);
            
            // Feedback to the user
            showColoredLabelMessageOnGUI(RequestStatus, "Borrow request submitted successfully!\nAwaiting Librarian approval", "-fx-text-fill: green;");
            btnSubmitToLibrarian.setDisable(true); // Optionally disable the button after submitting
        } else {
            String errorMessage = getErrorMessage(borrowStatus); // Get appropriate error message
            showColoredLabelMessageOnGUI(RequestStatus, errorMessage, "-fx-text-fill: red;");
        }
    }

    /**
     * Submits a reservation request for the selected book.
     * 
     * @param event the action event triggered by the user
     * @throws Exception if an error occurs during the reservation process
     */
    public void Submit_Reserve_Request(ActionEvent event) throws Exception {
        if (reservedCopiesNum == copiesNum) {
            // All copies are reserved
            showColoredLabelMessageOnGUI(RequestStatus, 
                "All the current copies of the book are reserved.\nPlease try to reserve at a later time when a copy is\navailable to be reserved.", 
                "-fx-text-fill: red;");
        } else if ("NO_COPIES".equals(borrowStatus)) {
            // Proceed with reservation logic
        	String subscriberName = SubscriberWindowController.currentSubscriber.getSubscriber_name();
            // Collect subscriber and book details
            String subscriberId = "" + SubscriberWindowController.currentSubscriber.getSubscriber_id();
            //String subscriberName = SubscriberWindowController.currentSubscriber.getSubscriber_name();
            String borrowDate = clockController.timeNow();
            
            String reserveDate = clockController.timeNow();
            String body = "" + subscriberName + "," + subscriberId + "," + bookName + "," + bookId + "," + borrowDate + "," + "temp";
            
            String reservation = "" + subscriberId + "," + bookName + "," + reserveDate + ","  + bookId;
            
            ClientUI.chat.accept("Reserve:" + reservation);
            ClientUI.chat.accept("UpdateHistoryInDB:" + body + ",Reserved Successfully");
            waitForServerResponse();
            
            // Feedback to the user
            showColoredLabelMessageOnGUI(RequestStatus, 
                "You have successfully reserved the book.", 
                "-fx-text-fill: green;");
            
            // Disable the Reserve button
            btnReserve.setDisable(true);

        } else {
            showColoredLabelMessageOnGUI(RequestStatus, 
                "Book is available, no need to reserve.", 
                "-fx-text-fill: red;");
        }
    }
    
    /**
     * Clears the input fields and resets the view.
     */
    public void Clear() {
        // Clear the text field for the book ID
        IDtxt.clear();
        
        // Clear the details displayed on the window
        Book_Description.setText("");
        awaitingTextID.setText("");
        RequestStatus.setText("");
        
        // Reset the Reserve button
        btnReserve.setVisible(false); // Hide the Reserve button if visible
        btnReserve.setDisable(false); // Re-enable the Reserve button
        
        // Disable the borrow request button
        btnSubmitToLibrarian.setDisable(true);
    }

    /**
     * Opens the search window for finding books.
     * 
     * @param event the action event triggered by the user
     * @throws Exception if an error occurs while opening the search window
     */
    public void openSearchWindow(ActionEvent event) throws Exception {
        SearchFrameController.FlagForSearch = "SubscriberBorrower";
        openWindow(event,
                "/gui/SearchWindow/SearchFrame.fxml",
                "/gui/SearchWindow/SearchFrame.css",
                "Search a Book");
    }

    /**
     * Navigates back to the Subscriber View.
     * 
     * @param event the action event triggered by the user
     * @throws Exception if an error occurs during navigation
     */
    public void Back(ActionEvent event) throws Exception {
        openWindow(event,
                "/gui/SubscriberWindow/SubscriberWindow.fxml",
                "/gui/SubscriberWindow/SubscriberWindow.css",
                "Subscriber View");
    }

    /**
     * Navigates to the Main Menu.
     * 
     * @param event the action event triggered by the user
     * @throws Exception if an error occurs during navigation
     */
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
}
