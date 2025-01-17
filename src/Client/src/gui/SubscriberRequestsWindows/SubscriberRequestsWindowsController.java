package gui.SubscriberRequestsWindows;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.BorrowedBook;
import logic.ClientTimeDiffController;
import logic.Librarian;
import logic.Subscriber;
import gui.LibrarianWindow.LibrarianController;
import gui.MainMenu.MainMenuController;
import gui.SubscriberWindow.SubscriberWindowController;
import gui.baseController.BaseController;

public class SubscriberRequestsWindowsController extends BaseController implements Initializable {
    private SubscriberRequestsWindowsController srwc;
    Librarian currentLibrarian = LibrarianController.currentLibrarian;
    
    @FXML
    private Button btnExit = null;

    @FXML
    private Button btnSend = null;
    @FXML
    private Button btnBack = null;
    @FXML
    private Button btnScanBarcode = null;
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
    
    ClientTimeDiffController clock = new ClientTimeDiffController();
    
    private List<String[]> borrowRequests = new ArrayList<>();
    private List<String[]> RegisterRequests = new ArrayList<>();
    private List<String[]> ReturnRequests = new ArrayList<>();
    private String requestType = "";
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        RequestedByCB.getItems().add("");
        RequestCB.getItems().add("");
        RequestedByCB.setOnAction(event -> autofillSubscriberData());
        RequestCB.setOnAction(event -> autofillRequestData());
        Clear.setOnAction(event -> Clear());
        BorrowForSubscriber.setOnAction(event -> BorrowForSubscriber());
        ReturnForSubscriber.setOnAction(event -> ReturnForSubscriber());
        Register.setOnAction(event -> Register());
        Clear.setSelected(true);
    }

	

    public void Clear() {
    	updateLabels("Clear");
        deselectOtherButtons(Clear);
        requestType="Clear";
        datePicker.setVisible(false);
        LBL5.setVisible(true);
        LBL6.setVisible(false);
        Clear.setSelected(true);
        datePicker.setValue(null);
    }
    public void Register() {
    	updateLabels("Registers");
        deselectOtherButtons(Register);
        requestType="Registers";
        datePicker.setVisible(false);
        LBL5.setVisible(false);
        LBL6.setVisible(false);
        Register.setSelected(true);
        datePicker.setValue(null);
    }
    public void BorrowForSubscriber() {
    	updateLabels("Borrow For Subscriber");
        deselectOtherButtons(BorrowForSubscriber);
        requestType="Borrow For Subscriber";
        datePicker.setVisible(true);
        LBL5.setVisible(true);
        LBL6.setVisible(true);
        BorrowForSubscriber.setSelected(true);
        datePicker.setValue(null);
    }
    public void ReturnForSubscriber() {
    	updateLabels("Return For Subscriber");
        deselectOtherButtons(ReturnForSubscriber);
        requestType="Return For Subscriber";
        datePicker.setVisible(true);
        LBL5.setVisible(true);
        LBL6.setVisible(true);
        ReturnForSubscriber.setSelected(true);
        datePicker.setValue(null);
    }
    private void deselectOtherButtons(ToggleButton selectedButton) {
        if (selectedButton != Clear) Clear.setSelected(false);
        if (selectedButton != BorrowForSubscriber) BorrowForSubscriber.setSelected(false);
        if (selectedButton != ReturnForSubscriber) ReturnForSubscriber.setSelected(false);
        if (selectedButton != Register) Register.setSelected(false);
    }
 // Method to update labels based on the selected request type
    public void updateLabels(String type) {
        String selectedRequestType = type;
        // Clear all ComboBoxes and text fields
        clearFieldsAndComboBoxes();
        
        // Set the fields and populate the RequestedByCB based on the selected request type
        switch (selectedRequestType) {
            case "Registers":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Phone Number:");
                LBL4.setText("Email:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(false);
                ClientUI.chat.accept("FetchRegisterRequest:");
                addDelay();
                handleFetchedRegister();
                break;
            case "Borrow For Subscriber":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Book Name:");
                LBL4.setText("Book ID:");
                LBL5.setText("Borrowed At:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
                LBL6.setText("Expected Return");
                ClientUI.chat.accept("FetchBorrowRequest:");
                requestType = "Borrow For Subscriber";
                addDelay();
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
                ClientUI.chat.accept("Fetch return request:");
                addDelay();
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


 
	// Method waits for 0.5 seconds for asynchronous data fetch
    private void addDelay() {
        // Create a PauseTransition with a 0.5 second delay
    	try 
    	{
    		TimeUnit.MILLISECONDS.sleep(500);
    	}
    	catch (InterruptedException e)
    	{
    		// TODO
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
                datePicker.setValue(clock.convertStringToLocalDateTime(clock.extendReturnDate(clock.timeNow(), 14)).toLocalDate()); 
            	
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
    // Method to handle Exit button click
    public void getExitBtn(ActionEvent event) throws Exception {
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"MainMenu");
    }
    public void Back(ActionEvent event) throws Exception {
        openWindow(event,
                "/gui/LibrarianWindow/LibrarianFrame.fxml",
                "/gui/LibrarianWindow/LibrarianFrame.css",
                "Librarian Window");
    }
    
    public void getScanBarcodeBtn(ActionEvent event) throws Exception {
  
    }
    
    // Method to display messages (for debugging or logging)
    public void display(String message) {
        System.out.println(message);
    }

	// Method to clear all ComboBoxes and text fields
	private void clearFieldsAndComboBoxes() {
	    RequestedByCB.getItems().clear();
	    RequestCB.getItems().clear();
	    TXTF1.setText("");
	    TXTF2.setText("");
	    TXTF3.setText("");
	    TXTF4.setText("");
	    TXTF5.setText("");
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
		if(selectedRequestType=="Borrow For Subscriber") 
		{
            String SName = TXTF1.getText();
            String SID = TXTF2.getText();
            String BName = TXTF3.getText();
            String BID = TXTF4.getText();
            String Btime = TXTF5.getText();
            String Rtime =  convertDateFormat(""+datePicker.getValue()); 
            String body = ""+SName+","+SID+","+BName+","+BID+","+Btime+","+Rtime;
			ClientUI.chat.accept("SubmitBorrowRequest:"+body);
            ClientUI.chat.accept("UpdateCopiesOfBook:"+body);
            ClientUI.chat.accept("UpdateHistoryInDB:"+body+",Borrowed Successfully");
		}
		else if (selectedRequestType=="Return For Subscriber"){
			String statusOfReturn = "";
			String SName = TXTF1.getText();
            String SID = TXTF2.getText();
            String BName = TXTF3.getText();
            String BID = TXTF4.getText();
            String Btime = TXTF5.getText();
            String Rtime = convertDateFormat(""+datePicker.getValue()); 
            System.out.println("yaniv :\n\n\n\n\n\n Borrow time="+Btime+"   Return time=" + Rtime);
            int numOfDaysOfReturn = numOfDays(Btime, Rtime);
            if (numOfDaysOfReturn >= 0) {
				statusOfReturn = "early";
			}
            else {
				statusOfReturn = "late";
				numOfDaysOfReturn = Math.abs(numOfDaysOfReturn);
			}
            String body = ""+SName+","+SID+","+BName+","+BID+","+Btime+","+Rtime;
			ClientUI.chat.accept("Handle return:"+body); 
			ClientUI.chat.accept("UpdateHistoryInDB:"+body+",Return Successfully "+numOfDaysOfReturn+" days "+statusOfReturn);
		}
		else if (selectedRequestType=="Registers") {
			 String SName = TXTF1.getText();
			 String SID = TXTF2.getText();
			 String PhoneNum = TXTF3.getText();
			 String Email = TXTF4.getText();
			 String body = ""+SName+","+SID+","+PhoneNum+","+Email;
			 ClientUI.chat.accept("Handle register:"+body);
			 ClientUI.chat.accept("UpdateHistoryInDB:"+body+",Register Successfully");
		}
	}

	    // Method to convert date string from "yyyy-MM-dd" to "dd-MM-yyyy"
	    public static String convertDateFormat(String dateStr) 
	    {
	        // Define the input and output date formats
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	        
	        // Parse the original string into a LocalDate object
	        LocalDate date = LocalDate.parse(dateStr, inputFormatter);
	        
	        // Format the LocalDate object to the new string format
	        return date.format(outputFormatter);
	    }
	    
	    // method to convert date string from "dd-MM-yyyy" to number of days ("dd")
	    public static int numOfDays(String Borrowtime,String Returntime) 
	    {
	    	int dateOfBorrow = Integer.parseInt(Borrowtime.substring(0, 2));
	    	int dateOfReturn = Integer.parseInt(Returntime.substring(0, 2));
	    	return dateOfBorrow-dateOfReturn;
	    }
	}
