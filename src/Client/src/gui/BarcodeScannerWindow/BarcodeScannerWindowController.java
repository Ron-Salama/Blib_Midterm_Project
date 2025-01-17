package gui.BarcodeScannerWindow;

import client.ChatClient;
import client.ClientUI;
import gui.baseController.BaseController;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Subscriber;


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
        openWindow(event, 
        		"/gui/SubscriberRequestsWindowsController/SubscriberRequestsWindowsFrame.fxml", 
        		"/gui/SubscriberRequestsWindowsController/SubscriberRequestsWindowsFrame.css", 
                "Subscriber Requests Window");
    }

	
    public void Send(ActionEvent event) throws Exception {
        String borrowedBookID = getBorrowedBookID();

        if (borrowedBookID.trim().isEmpty()) {
        	showColoredLabelMessageOnGUI(borrowedBookIDDynamicLabel, "The ID of the back cannot be empty", "-fx-text-fill: red;");
            return;
        }
        
        // Send a request to the DB to get the information of the borrowed book.
        ClientUI.chat.accept("FetchBorrowedBooksForBarcodeScanner:" + borrowedBookID);
        
        // XXX
        if (ChatClient.BorrowedBookInformationForBarcodeScanner == null) { // No information received.
        	showColoredLabelMessageOnGUI(borrowedBookIDDynamicLabel, "No books found in the library with book ID: " + borrowedBookID, "-fx-text-fill: red;");
        	return; 
        }
        
        // Information received, now put it in the librarian requests window. // TODO: enter the data in the librarian window.
        // my code ^^^^^ 

    }
}