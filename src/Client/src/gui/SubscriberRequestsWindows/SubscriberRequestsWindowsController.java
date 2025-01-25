package gui.SubscriberRequestsWindows;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import client.ChatClient;
import client.ClientUI;
import gui.MainMenu.MainMenuController;
import gui.baseController.BaseController;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.BorrowedBook;
import logic.ClientTimeDiffController;
import logic.Librarian;
import logic.Subscriber;
import gui.BarcodeScannerWindow.BarcodeScannerWindowController;
import gui.LibrarianWindow.LibrarianController;
import gui.MainMenu.MainMenuController;
import gui.SubscriberWindow.SubscriberWindowController;
import gui.baseController.BaseController;

public class SubscriberRequestsWindowsController extends BaseController implements Initializable {
    
    public static String[] borrowedBookInformationFromBarcode = null; // Using a Barcode the librarian can receive information about a borrowed book request.    
    public static boolean borrowInformationFromBarcode = false;
    
    Librarian currentLibrarian = LibrarianController.currentLibrarian;
    
    private ClientTimeDiffController clock = ChatClient.clock;
    
    @FXML
    private Button btnExit = null;
    @FXML
    private Button btnAccept = null;

    @FXML
    private Button btnSend = null;
    @FXML
    private Button btnBack = null;
    @FXML
    private Button ScanBarcode = null;
    @FXML
    private ToggleButton Clear = null;
    @FXML
    private ToggleButton BorrowForSubscriber = null;
    @FXML
    private ToggleButton ReturnForSubscriber = null;
    @FXML
    private ToggleButton Register = null;
    @FXML
    private DatePicker datePicker = null;
    @FXML
    private CheckBox isLost;
    @FXML
    private Label LBL1;
    @FXML
    private Label LBL2;
    @FXML
    private Label LBL3;
    @FXML
    private Label LBL4;
    @FXML
    private Label LBL5;
    @FXML
    private Label LBL6;
    @FXML
	private Label feedbackLabel;
    @FXML
    private TextField TXTF1;
    @FXML
    private TextField TXTF2;
    @FXML
    private TextField TXTF3;
    @FXML
    private TextField TXTF4;
    @FXML
    private TextField TXTF5;

    @FXML
    private ComboBox<String> RequestedByCB;
    @FXML
    private ComboBox<String> RequestCB;
    
    private List<String[]> borrowRequests = new ArrayList<>();
    private List<String[]> RegisterRequests = new ArrayList<>();
    private List<String[]> ReturnRequests = new ArrayList<>();
    private String requestType = "";
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	btnAccept.setDisable(false);
    	if (borrowInformationFromBarcode) {
    		try {
				borrowRequestSetupFromBarcode(borrowedBookInformationFromBarcode[0],
						borrowedBookInformationFromBarcode[1]);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}    		
    	}else {
    		ScanBarcode.setVisible(false);
    		Clear.setSelected(true);
    	}
        RequestedByCB.getItems().add("");
        RequestCB.getItems().add("");
        RequestedByCB.setOnAction(event -> autofillSubscriberData());
        RequestCB.setOnAction(event -> autofillRequestData());
        Clear.setOnAction(event -> {
			try {
				Clear();
				btnAccept.setDisable(false);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
        BorrowForSubscriber.setOnAction(event -> {
			try {
				BorrowForSubscriber();
				btnAccept.setDisable(false);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
        ReturnForSubscriber.setOnAction(event -> {
			try {
				ReturnForSubscriber();
				btnAccept.setDisable(false);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
        Register.setOnAction(event -> {
			try {
				Register();
				btnAccept.setDisable(false);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
        
    }

    public void Clear() throws InterruptedException {
    	ScanBarcode.setVisible(false);
    	updateLabels("Clear");
        deselectOtherButtons(Clear);
        requestType="Clear";
        datePicker.setVisible(false);
        LBL5.setVisible(true);
        LBL6.setVisible(false);
        Clear.setSelected(true);
        datePicker.setValue(null);
        isLost.setVisible(false);
    }
    public void Register() throws InterruptedException {
    	ScanBarcode.setVisible(false);
    	updateLabels("Registers");
        deselectOtherButtons(Register);
        requestType="Registers";
        datePicker.setVisible(false);
        isLost.setVisible(false);
        LBL5.setVisible(false);
        LBL6.setVisible(false);
        Register.setSelected(true);
        datePicker.setValue(null);
    }
    public void BorrowForSubscriber() throws InterruptedException {
    	ScanBarcode.setVisible(true);
    	updateLabels("Borrow For Subscriber");
        deselectOtherButtons(BorrowForSubscriber);
        requestType="Borrow For Subscriber";
        datePicker.setVisible(true);
        isLost.setVisible(false);
        LBL5.setVisible(true);
        LBL6.setVisible(true);
        BorrowForSubscriber.setSelected(true);
        datePicker.setValue(null);
        datePicker.setDisable(true);

    }
    public void ReturnForSubscriber() throws InterruptedException {
    	ScanBarcode.setVisible(false);
    	updateLabels("Return For Subscriber");
        deselectOtherButtons(ReturnForSubscriber);
        requestType="Return For Subscriber";
        datePicker.setVisible(true);
        isLost.setVisible(true);
        LBL5.setVisible(true);
        LBL6.setVisible(true);
        ReturnForSubscriber.setSelected(true);
        datePicker.setValue(null);
        datePicker.setDisable(false);

    }
    private void deselectOtherButtons(ToggleButton selectedButton) {
        if (selectedButton != Clear) Clear.setSelected(false);
        if (selectedButton != BorrowForSubscriber) BorrowForSubscriber.setSelected(false);
        if (selectedButton != ReturnForSubscriber) ReturnForSubscriber.setSelected(false);
        if (selectedButton != Register) Register.setSelected(false);
    }
 // Method to update labels based on the selected request type
    public void updateLabels(String type) throws InterruptedException {
        String selectedRequestType = type;
        // Clear all ComboBoxes and text fields
        clearFieldsAndComboBoxes();
        
        // Set the fields and populate the RequestedByCB based on the selected request type
        switch (selectedRequestType) {
            case "Registers":
                registerRequestSetUp();
                break;
            case "Borrow For Subscriber":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Book Name:");
                LBL4.setText("Book ID:");
                LBL5.setText("Borrowed At:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
                datePicker.setDisable(true);
                LBL6.setText("Expected Return");
                ClientUI.chat.accept("FetchBorrowRequest:");
                requestType = "Borrow For Subscriber";
                waitForServerResponse();
                handleFetchedBorrowedBooks();
                break;
            case "Return For Subscriber":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Book Name:");
                LBL4.setText("Book ID:");
                LBL5.setText("Borrowed At:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
                LBL6.setText("Return Time:");
                isLost.setVisible(true);
                datePicker.setDisable(false);

                ClientUI.chat.accept("Fetch return request:");
                waitForServerResponse();
                handleReturnofBorrowedBook();
                break;
            default:
                LBL1.setText("");
                LBL2.setText("");
                LBL3.setText("");
                LBL4.setText("");
                LBL5.setText("");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
                isLost.setVisible(false);
                break;
        }
    }

    private void handleReturnofBorrowedBook() {
    	 ReturnRequests.clear();  // Clear the existing list to avoid duplicate data
         clearFieldsAndComboBoxes();

         // Ensure ChatClient.br is not null or empty
         if (ChatClient.br != null && ChatClient.br.size() > 0) {
             // Iterate over each row in ChatClient.br (each row is a borrow request)
             for (int i = 0; i < ChatClient.br.size(); i++) {
                 String[][] ReturnRequestsArray = ChatClient.br.get(i); // Get the i-th 2D array

                 // Iterate over each borrow request in the i-th 2D array (assuming each request has 8 fields)
                 for (String[] request : ReturnRequestsArray) {
                     if (request.length == 8) {
                    	 ReturnRequests.add(request);  // Add the borrow request (which is a String array) to the list
                     } else {
                         System.out.println("Invalid Return request data at index " + i + ": " + String.join(",", request));
                     }
                 }
             }

             // Create a set to store unique subscriber names to avoid duplicates
             ObservableList<String> requestedByList = FXCollections.observableArrayList();
             Set<String> uniqueNames = new HashSet<>();

             for (String[] returnRequest : ReturnRequests) {
                 String subscriberName = returnRequest[2];  // Assuming the name is at index 2

                 // Add the name only if it's not already in the set
                 if (!uniqueNames.contains(subscriberName)) {
                     uniqueNames.add(subscriberName);
                     requestedByList.add(subscriberName);  // Add the unique name to the list
                 }
             }

             RequestedByCB.setItems(requestedByList);  // Set the unique subscriber names in the ComboBox
         } else {
             System.out.println("No borrow requests available.");
         }
    }

 // Method to autofill text fields based on the selected subscriber
    private void autofillSubscriberData() {
        String selectedName = RequestedByCB.getValue();

        if (selectedName != null && !selectedName.isEmpty()) {
            // Clear RequestCB and text fields
            clearRequestCBAndTextFields();

            // Create a list to store the selected subscriber's requests (either borrow or register requests)
            List<String[]> selectedRequests = new ArrayList<>();

            String selectedRequestType = requestType;

            if ("Registers".equals(selectedRequestType)) {
                // Add register requests for the selected subscriber
                for (String[] request : RegisterRequests) {
                    if (request[2].equals(selectedName)) {
                        selectedRequests.add(request);
                    }
                }
            } else if ("Borrow For Subscriber".equals(selectedRequestType)) {	
            	// Add borrow requests for the selected subscriber
                for (String[] request : borrowRequests) {
                    if (request[2].equals(selectedName)) {
                        selectedRequests.add(request);
                    }
                }
            }
            else if ( "Return For Subscriber".equals(selectedRequestType)) {
              for (String[] request : ReturnRequests) {
                       if (request[2].equals(selectedName)) {
                            selectedRequests.add(request);
                          }
                }
            }

            // Populate the RequestCB ComboBox with request information
            for (String[] request : selectedRequests) {
                if ("Registers".equals(selectedRequestType)) {
                    RequestCB.getItems().add("(ID: " + request[1] + " ,Name: "+ request[2] + ")");
                } else {
                    RequestCB.getItems().add(request[3] + " (Book ID: " + request[4] + ")");
                }
            }
        }
    }

    private void autofillRequestData() {
        String selectedRequest = RequestCB.getValue();
        clearTextFields();

        if (selectedRequest != null && !selectedRequest.isEmpty()) {
            List<String[]> selectedRequests = new ArrayList<>();
            String selectedRequestType = requestType;

            if ("Registers".equals(selectedRequestType)) {
                selectedRequests = RegisterRequests;
            } else if ("Borrow For Subscriber".equals(selectedRequestType)) {
                selectedRequests = borrowRequests;
                TXTF5.setText(clock.timeNow());
                datePicker.setValue(clock.convertStringToLocalDate(clock.extendReturnDate(clock.timeNow(), 14))); 
            	
            } else if ("Return For Subscriber".equals(selectedRequestType)) {
                selectedRequests = ReturnRequests;
                TXTF5.setText(clock.timeNow());
                datePicker.setValue(clock.convertStringToLocalDateTime(clock.timeNow()).toLocalDate()); 
            	
            }

            for (String[] request : selectedRequests) {
                String formattedRequest = formatRequest(selectedRequestType, request);

                if (formattedRequest.equals(selectedRequest)) {
                    TXTF1.setText(request[2]); // Subscriber Name
                    TXTF2.setText(request[1]); // Subscriber ID
                    TXTF3.setText(request[3]); // Book Name or Email
                    TXTF4.setText(request[4]); // Book ID or Phone Number
                    TXTF5.setText(request[5]);
                     // Borrow/Return Time if applicable
                    break;
                }
            }
        }
    }

    private String formatRequest(String requestType, String[] request) {
        if ("Registers".equals(requestType)) {
            return "(ID: " + request[1] + " ,Name: " + request[2] + ")";
        } else {
            return request[3] + " (Book ID: " + request[4] + ")";
        }
    }

    private void clearTextFields() {
        TXTF1.clear();
        TXTF2.clear();
        TXTF3.clear();
        TXTF4.clear();
        TXTF5.clear();
    }


    // Method to handle the fetched borrow requests from ChatClient
    public void handleFetchedBorrowedBooks() {
        borrowRequests.clear();  // Clear the existing list to avoid duplicate data
        clearFieldsAndComboBoxes();

        // Ensure ChatClient.br is not null or empty
        if (ChatClient.br != null && ChatClient.br.size() > 0) {
            // Iterate over each row in ChatClient.br (each row is a borrow request)
            for (int i = 0; i < ChatClient.br.size(); i++) {
                String[][] borrowRequestArray = ChatClient.br.get(i); // Get the i-th 2D array

                // Iterate over each borrow request in the i-th 2D array (assuming each request has 8 fields)
                for (String[] request : borrowRequestArray) {
                    if (request.length == 8) {
                        borrowRequests.add(request);  // Add the borrow request (which is a String array) to the list
                    } else {
                        System.out.println("Invalid borrow request data at index " + i + ": " + String.join(",", request));
                    }
                }
            }

            // Create a set to store unique subscriber names to avoid duplicates
            ObservableList<String> requestedByList = FXCollections.observableArrayList();
            Set<String> uniqueNames = new HashSet<>();

            for (String[] borrowRequest : borrowRequests) {
                String subscriberName = borrowRequest[2];  // Assuming the name is at index 2

                // Add the name only if it's not already in the set
                if (!uniqueNames.contains(subscriberName)) {
                    uniqueNames.add(subscriberName);
                    requestedByList.add(subscriberName);  // Add the unique name to the list
                }
            }

            RequestedByCB.setItems(requestedByList);  // Set the unique subscriber names in the ComboBox
        } else {
            System.out.println("No borrow requests available.");
        }
    }

    public void handleFetchedRegister() {
        RegisterRequests.clear();  // Clear the existing list to avoid duplicate data
        clearFieldsAndComboBoxes();
        // Ensure ChatClient.br is not null or empty
        if (ChatClient.br != null && ChatClient.br.size() > 0) {
            // Iterate over each row in ChatClient.br (each row is a register request)
            for (int i = 0; i < ChatClient.br.size(); i++) {
                String[][] RegisterRequestsArray = ChatClient.br.get(i); // Get the i-th 2D array

                // Iterate over each register request in the i-th 2D array (assuming each request has 8 fields)
                for (String[] request : RegisterRequestsArray) {
                    if (request.length == 8) {
                        RegisterRequests.add(request);  // Add the register request (which is a String array) to the list
                    } else {
                        System.out.println("Invalid register request data at index " + i + ": " + String.join(",", request));
                    }
                }
            }

            // Populate the ComboBox with subscriber names from the register requests
            ObservableList<String> requestedByList = FXCollections.observableArrayList();
            for (String[] registerRequest : RegisterRequests) {
                requestedByList.add(registerRequest[2]);  // Assuming the name is at index 2 (requestedByName)
            }
            
            RequestedByCB.setItems(requestedByList);
        } else {
            System.out.println("No register requests available.");
        }
    }
  
	// Method to clear all ComboBoxes and text fields
	private void clearFieldsAndComboBoxes() {
	    RequestedByCB.getItems().clear();
	    clearRequestCBAndTextFields();
	}
	
	// Method to clear only the RequestCB and text fields
	private void clearRequestCBAndTextFields() {
	    RequestCB.getItems().clear();
	    TXTF1.setText("");
	    TXTF2.setText("");
	    TXTF3.setText("");
	    TXTF4.setText("");
	    TXTF5.setText("");
	}

	public void acceptRequest(ActionEvent event) throws Exception 
	{
		String selectedRequestType = requestType;
		
		if (!areAllFieldsFilled(feedbackLabel, selectedRequestType, TXTF1, TXTF2, TXTF3, TXTF4, TXTF5)) {
			return;
		}
		
		if(selectedRequestType=="Borrow For Subscriber") 
		{
            String SName = TXTF1.getText();
            String SID = TXTF2.getText();
            String BName = TXTF3.getText();
            String BID = TXTF4.getText();
            String Btime = TXTF5.getText();
            String Rtime =  clock.convertDateFormat("" + datePicker.getValue()); 
            String body = ""+SName+","+SID+","+BName+","+BID+","+Btime+","+Rtime;
            
            // Check if the subscriber is frozen.
            ClientUI.chat.accept("Fetch:" + SID); // Send the subscriber ID to the server so we can check if that subscriber is frozen.
            waitForServerResponse();
            if (ChatClient.s1.getStatus().split(":")[1].equals("Frozen at")) {
            	showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(feedbackLabel, "The subscriber " + ChatClient.s1.getSubscriber_name() + " is currently frozen and can't borrow books.", "-fx-text-fill: blue;", 3);
            	return;
            }
            
            if(borrowInformationFromBarcode) {
            	ClientUI.chat.accept("SubmitBorrowRequestBarcode:"+body);
            	waitForServerResponse();
            	btnAccept.setDisable(true);
            }else {
    			ClientUI.chat.accept("SubmitBorrowRequest:"+body);
    			waitForServerResponse();
    			btnAccept.setDisable(true);
            }
            ClientUI.chat.accept("UpdateHistoryInDB:"+body+",Borrowed Successfully");
            waitForServerResponse();
            showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(feedbackLabel, "Borrow request accepted successfully!", "-fx-text-fill: green;", 3);
		}
		else if (selectedRequestType=="Return For Subscriber"){
			String statusOfReturn = "";
			String SName = TXTF1.getText();
            String SID = TXTF2.getText();
            String BName = TXTF3.getText();
            String BID = TXTF4.getText();
            String Btime = TXTF5.getText();
            String Rtime = clock.convertDateFormat("" + datePicker.getValue()); 
            String body = "" + SName + "," + SID + "," + BName + "," + BID + "," + Rtime + "," + Btime;
            
            boolean lostBook = isLost.isSelected(); //Check if the checkBox isLost is selected
            if (lostBook) 
            {
            	ClientUI.chat.accept("Handle Lost:" + body);
            	waitForServerResponse();
            	ClientUI.chat.accept("UpdateHistoryInDB:" + body + ",Lost");
            	waitForServerResponse();
                showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(feedbackLabel, "Return request accepted successfully! (Book marked as lost)", "-fx-text-fill: green;", 3);
                btnAccept.setDisable(true);
            }
            else
            {	
            	String Expected_Return=clock.convertStringToLocalDate(Btime).plusDays(14).toString();
            	int numOfDaysOfReturn = clock.timeDateDifferenceBetweenTwoDates(clock.convertDateFormat(Expected_Return), clock.convertDateFormat(clock.convertStringToLocalDate(Rtime).toString()));
                if (numOfDaysOfReturn <= 0) {
                	statusOfReturn = "early";
                	numOfDaysOfReturn = Math.abs(numOfDaysOfReturn);
    			}
                else{
                	statusOfReturn = "late";
    			}
                ClientUI.chat.accept("Handle return:" + body); 
                waitForServerResponse();
            	ClientUI.chat.accept("UpdateHistoryInDB:" + body + ",Return Successfully " + numOfDaysOfReturn + " days " + statusOfReturn);
            	waitForServerResponse();
                showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(feedbackLabel, "Return request accepted successfully! (" + statusOfReturn + ")", "-fx-text-fill: green;", 7);;
                btnAccept.setDisable(true);
            }
		}
		else if (selectedRequestType == "Registers") {
			 String SName = TXTF1.getText();
			 String SID = TXTF2.getText();
			 String PhoneNum = TXTF3.getText();
			 String Email = TXTF4.getText();
			 String date = clock.timeNow();
			 String ignore2 = "ignore";
			 String body1 = "" + SName + "," + SID + "," + PhoneNum + "," + Email;
			 String body2 = "" + SName + "," + SID + "," + PhoneNum + "," + Email + "," +date+ "," + ignore2;

			 ClientUI.chat.accept("Handle register:" + body1);
			 waitForServerResponse();
			 ClientUI.chat.accept("UpdateHistoryInDB:" + body2 + ",Register Successfully");
			 waitForServerResponse();
             
	         showColoredLabelMessageOnGUI(feedbackLabel, "Registration request accepted successfully!", "-fx-text-fill: green;");
	         btnAccept.setDisable(true);
		}
	}

    // Method to handle Exit button click
    public void getExitBtn(ActionEvent event) throws Exception {
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"MainMenu");
    }
    
    public void Back(ActionEvent event) throws Exception {
    	borrowInformationFromBarcode = false;
    	openWindow(event,
    			"/gui/LibrarianWindow/LibrarianFrame.fxml",
    			"/gui/LibrarianWindow/LibrarianFrame.css",
    			"Librarian Window");
    }
    
    public void getScanBarcodeBtn(ActionEvent event) throws Exception {
    	borrowInformationFromBarcode = true;
    	openWindow(event,
    			"/gui/BarcodeScannerWindow/BarcodeScannerWindowFrame.fxml",
    			"/gui/BarcodeScannerWindow/BarcodeScannerWindowFrame.fxml",
    			"Scan Barcode");
    }
    
    private void borrowRequestSetupFromBarcode(String borrowedBookID, String borrowedBookName) throws InterruptedException {
    	Clear(); // Clear the current fields.
    	BorrowForSubscriber(); // Set up the elements for borrow request.
    	
    	if (borrowInformationFromBarcode) {
    		TXTF3.setText(borrowedBookName); // Borrowed book name.
    		TXTF4.setText(borrowedBookID); // borrowed book id.
    		
    		String borrowDate = clock.timeNow();
    		TXTF5.setText(borrowDate); // borrow time
    		
    		// Get the return date as a string, convert and set it in the correct field.
    		LocalDate returnDate = clock.convertStringToLocalDate(clock.calculateReturnDate(14));
    		datePicker.setValue(returnDate); // return date. 	 
    		datePicker.isDisable();
    	}
    }
    
    
    
    private void registerRequestSetUp() throws InterruptedException {
    	LBL1.setText("Subscriber Name:");
        LBL2.setText("Subscriber ID:");
        LBL3.setText("Phone Number:");
        LBL4.setText("Email:");
        TXTF4.setVisible(true);
        TXTF5.setVisible(false);
        ClientUI.chat.accept("FetchRegisterRequest:");
        
        waitForServerResponse();
        handleFetchedRegister();
    }
    
    
    private boolean areAllFieldsFilled(Label feedbackLabel, String selectedRequestType, TextField TXTF1, TextField TXTF2, TextField TXTF3, TextField TXTF4, TextField TXTF5) {
        StringBuilder missingFields = new StringBuilder("Please fill out the following fields: ");
        boolean allFieldsFilled = true;

        switch (selectedRequestType) {
            case "Borrow For Subscriber":
                if (TXTF1.getText() == null || TXTF1.getText().trim().isEmpty()) {
                    missingFields.append("Subscriber Name, ");
                    allFieldsFilled = false;
                }
                if (TXTF2.getText() == null || TXTF2.getText().trim().isEmpty()) {
                    missingFields.append("Subscriber ID, ");
                    allFieldsFilled = false;
                }
                if (TXTF3.getText() == null || TXTF3.getText().trim().isEmpty()) {
                    missingFields.append("Book Name, ");
                    allFieldsFilled = false;
                }
                if (TXTF4.getText() == null || TXTF4.getText().trim().isEmpty()) {
                    missingFields.append("Book ID, ");
                    allFieldsFilled = false;
                }
                if (TXTF5.getText() == null || TXTF5.getText().trim().isEmpty()) {
                    missingFields.append("Borrow Time, ");
                    allFieldsFilled = false;
                }
                break;

            case "Return For Subscriber":
                if (TXTF1.getText() == null || TXTF1.getText().trim().isEmpty()) {
                    missingFields.append("Subscriber Name, ");
                    allFieldsFilled = false;
                }
                if (TXTF2.getText() == null || TXTF2.getText().trim().isEmpty()) {
                    missingFields.append("Subscriber ID, ");
                    allFieldsFilled = false;
                }
                if (TXTF3.getText() == null || TXTF3.getText().trim().isEmpty()) {
                    missingFields.append("Book Name, ");
                    allFieldsFilled = false;
                }
                if (TXTF4.getText() == null || TXTF4.getText().trim().isEmpty()) {
                    missingFields.append("Book ID, ");
                    allFieldsFilled = false;
                }
                if (TXTF5.getText() == null || TXTF5.getText().trim().isEmpty()) {
                    missingFields.append("Return Time, ");
                    allFieldsFilled = false;
                }
                break;

            case "Registers":
                if (TXTF1.getText() == null || TXTF1.getText().trim().isEmpty()) {
                    missingFields.append("Subscriber Name, ");
                    allFieldsFilled = false;
                }
                if (TXTF2.getText() == null || TXTF2.getText().trim().isEmpty()) {
                    missingFields.append("Subscriber ID, ");
                    allFieldsFilled = false;
                }
                if (TXTF3.getText() == null || TXTF3.getText().trim().isEmpty()) {
                    missingFields.append("Phone Number, ");
                    allFieldsFilled = false;
                }
                if (TXTF4.getText() == null || TXTF4.getText().trim().isEmpty()) {
                    missingFields.append("Email, ");
                    allFieldsFilled = false;
                }
                break;

            default:
                showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(feedbackLabel, "Unknown request type.", "-fx-text-fill: red;", 3);
                return false;
        }

        if (!allFieldsFilled) {
            // Remove the trailing comma and space
            missingFields.setLength(missingFields.length() - 2);
            showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(feedbackLabel, missingFields.toString(), "-fx-text-fill: red;", 10);
        }

        return allFieldsFilled;
    }

}
