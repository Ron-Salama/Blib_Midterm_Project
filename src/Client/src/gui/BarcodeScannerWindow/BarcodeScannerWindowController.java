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


public class BarcodeScannerWindowController extends BaseController {

	@FXML
	private Button btnBack = null;
	
	@FXML
	private Button btnSend = null;
	
	@FXML
	private TextField borrowedBookIDtxt = null;
	
	@FXML
	private Label borrowedBookIDDynamicLabel = null;
    
	String borrowedBookData = null;
	
	private String getBorrowedBookID() {
		return borrowedBookIDtxt.getText();
	}
	
	public void start(Stage primaryStage) throws Exception {
		start(primaryStage, 
				"/gui/BarcodeScannerWindowController/BarcodeScannerWindowFrame.fxml",
				"/gui/BarcodeScannerWindowController/BarcodeScannerWindowFrame.css", 
				"Barcode Window");
	}
	

    public void getBackBtn(ActionEvent event) throws Exception {
        SubscriberRequestsWindowsController.borrowInformationFromBarcode = false;
    	openWindow(event, 
        		"/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.fxml", 
        		"/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.css", 
                "Subscriber Requests Window");
    }

	
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
        
        // Add a small delay for the server response (if needed).
        addDelayInMilliseconds(2000); //XXX

        // Check if no information was received for the borrowed book
        if (ChatClient.BorrowedBookInformationForBarcodeScanner[0].equals("NoBooksFound")) { 
            showColoredLabelMessageOnGUI(borrowedBookIDDynamicLabel, "No books found in the library with book ID: " + borrowedBookID, "-fx-text-fill: red;");
            return; // Stop execution if no information is found
        }

        // If valid data is received, process and open the next window
        String[] borrowRequestInformation = ChatClient.BorrowedBookInformationForBarcodeScanner[0].split(",");
        SubscriberRequestsWindowsController.borrowedBookInformationFromBarcode = borrowRequestInformation;
        
        // Close this window and open the Subscriber Requests Window
        openWindow(event, 
            "/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.fxml", 
            "/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.css", 
            "Subscriber Requests Window");
    }
}