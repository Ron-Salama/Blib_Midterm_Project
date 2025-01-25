package gui.BarcodeScannerWindow;

import client.ChatClient;
import client.ClientUI;
import gui.SubscriberRequestsWindows.SubscriberRequestsWindowsController;
import gui.baseController.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * The {@code BarcodeScannerWindowController} class handles the logic for the barcode scanner
 * window in the application. It manages user interactions, validates input, and communicates
 * with the server to retrieve information about borrowed books.
 */
public class BarcodeScannerWindowController extends BaseController {

	/** Button for navigating back to the previous window. */
	@FXML
	private Button btnBack = null;
	
	/** Button for sending the barcode ID to fetch book information. */
	@FXML
	private Button btnSend = null;
	
	/** Text field for entering the borrowed book's ID. */
	@FXML
	private TextField borrowedBookIDtxt = null;
	
	/** Dynamic label to display messages related to the borrowed book ID input. */
	@FXML
	private Label borrowedBookIDDynamicLabel = null;
    
	/** Holds the borrowed book data retrieved from the server. */
	String borrowedBookData = null;
	
	  /**
     * Retrieves the borrowed book ID entered in the text field.
     *
     * @return the borrowed book ID entered by the user
     */
	private String getBorrowedBookID() {
		return borrowedBookIDtxt.getText();
	}
	
	 /**
     * Initializes and displays the barcode scanner window.
     *
     * @param primaryStage the primary stage for this window
     * @throws Exception if an error occurs during initialization
     */
	public void start(Stage primaryStage) throws Exception {
		start(primaryStage, 
				"/gui/BarcodeScannerWindowController/BarcodeScannerWindowFrame.fxml",
				"/gui/BarcodeScannerWindowController/BarcodeScannerWindowFrame.css", 
				"Barcode Window");
	}
	
	 /**
     * Handles the "Back" button action. Navigates the user back to the
     * Subscriber Requests Window.
     *
     * @param event the action event triggered by clicking the back button
     * @throws Exception if an error occurs during navigation
     */
    public void getBackBtn(ActionEvent event) throws Exception {
        SubscriberRequestsWindowsController.borrowInformationFromBarcode = false;
    	openWindow(event, 
        		"/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.fxml", 
        		"/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.css", 
                "Subscriber Requests Window");
    }

    /**
     * Handles the "Send" button action. Validates the input, retrieves information
     * about the borrowed book from the server, and navigates to the Subscriber Requests Window
     * if valid data is received.
     *
     * @param event the action event triggered by clicking the send button
     * @throws Exception if an error occurs during data retrieval or navigation
     */
    @SuppressWarnings("unlikely-arg-type")
	public void Send(ActionEvent event) throws Exception {
        String borrowedBookID = getBorrowedBookID();

        // Validate if the borrowedBookID is empty
        if (borrowedBookID.trim().isEmpty()) {
            showColoredLabelMessageOnGUI(borrowedBookIDDynamicLabel, "The ID of the back cannot be empty", "-fx-text-fill: red;");
            return; // Stop execution if the ID is empty
        }
        
        // Send a request to the DB to get the information of the borrowed book.
        ClientUI.chat.accept("FetchBorrowedBooksForBarcodeScanner:" + borrowedBookID);
        
        waitForServerResponse();

        // Check if no information was received for the borrowed book
        if (ChatClient.BorrowedBookInformationForBarcodeScanner[0].equals("NoBooksFound")) { 
            showColoredLabelMessageOnGUI(borrowedBookIDDynamicLabel, "There are no borrowed books with book ID: " + borrowedBookID, "-fx-text-fill: red;");
            return; // Stop execution if no information is found
        }
        
        // If valid data is received, process and open the next window
        String[] borrowRequestInformation = ChatClient.BorrowedBookInformationForBarcodeScanner[0].split(",");

        // If the there are no available copies -> print and return.
        if (Integer.valueOf(borrowRequestInformation[6]) == 0) {
        	showColoredLabelMessageOnGUI(borrowedBookIDDynamicLabel, "There are no available copies of the book " + borrowRequestInformation[1] , "-fx-text-fill: red;");
        	return;
        }
        SubscriberRequestsWindowsController.borrowedBookInformationFromBarcode = borrowRequestInformation;
        
        // Close this window and open the Subscriber Requests Window
        openWindow(event, 
            "/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.fxml", 
            "/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.css", 
            "Subscriber Requests Window");
    }
}