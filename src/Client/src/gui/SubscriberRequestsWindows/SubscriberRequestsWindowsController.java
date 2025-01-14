package gui.SubscriberRequestsWindows;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
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
    private ComboBox<String> RequestTypeCB;

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
        // Initialize request types
        RequestTypeCB.getItems().addAll("", "Registers", "Extend Book Borrow", "Borrow For Subscriber", "Return For Subscriber");
        RequestedByCB.getItems().add(""); // Add an empty item for default value

        // Set event listeners
        RequestTypeCB.setOnAction(event -> updateLabels());
        RequestedByCB.setOnAction(event -> autofillSubscriberData());
        RequestCB.setOnAction(event -> autofillRequestData());

    }


 // Method to update labels based on the selected request type
    public void updateLabels() {
        String selectedRequestType = RequestTypeCB.getValue();

        // Clear all ComboBoxes and text fields
        clearFieldsAndComboBoxes();
        
        // Set the fields and populate the RequestedByCB based on the selected request type
        switch (selectedRequestType) {
            case "Registers":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Email:");
                LBL4.setText("Phone Number:");
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
                LBL5.setText("Borrow Time:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
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
                LBL5.setText("Return Time:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
                ClientUI.chat.accept("Fetch return request:");
                addDelay();
                handleReturnofBorrowedBook();
                break;
            case "Extend Book Borrow":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Book Name:");
                LBL4.setText("Book ID:");
                LBL5.setText("Extend Time:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
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



	// Method to introduce a delay of 0.5 seconds before fetching data
    private void addDelay() {
        // Create a PauseTransition with a 0.5 second delay
        PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(0.5));
        // Set the action to be executed after the pause
        pause.setOnFinished(event -> {

        });

        // Start the pause transition
        pause.play();
    }
 // Method to autofill text fields based on the selected subscriber
    private void autofillSubscriberData() {
        String selectedName = RequestedByCB.getValue();

        if (selectedName != null && !selectedName.isEmpty()) {
            // Clear RequestCB and text fields
            clearRequestCBAndTextFields();

            // Create a list to store the selected subscriber's requests (either borrow or register requests)
            List<String[]> selectedRequests = new ArrayList<>();

            String selectedRequestType = RequestTypeCB.getValue();

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
            String selectedRequestType = RequestTypeCB.getValue();

            if ("Registers".equals(selectedRequestType)) {
                selectedRequests = RegisterRequests;
            } else if ("Borrow For Subscriber".equals(selectedRequestType)) {
                selectedRequests = borrowRequests;
            } else if ("Return For Subscriber".equals(selectedRequestType)) {
                selectedRequests = ReturnRequests;
            }

            for (String[] request : selectedRequests) {
                String formattedRequest = formatRequest(selectedRequestType, request);

                if (formattedRequest.equals(selectedRequest)) {
                    TXTF1.setText(request[2]); // Subscriber Name
                    TXTF2.setText(request[1]); // Subscriber ID
                    TXTF3.setText(request[3]); // Book Name or Email
                    TXTF4.setText(request[4]); // Book ID or Phone Number
                    TXTF5.setText(request[5]); // Borrow/Return Time if applicable
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
		 String selectedRequestType = RequestTypeCB.getValue();
		if(selectedRequestType=="Borrow For Subscriber") 
		{
            String SName = TXTF1.getText();
            String SID = TXTF2.getText();
            String BName = TXTF3.getText();
            String BID = TXTF4.getText();
            String Btime = TXTF5.getText();
            String body = ""+SName+","+SID+","+BName+","+BID+","+Btime;
			ClientUI.chat.accept("SubmitBorrowRequest:"+body);
            ClientUI.chat.accept("UpdateCopiesOfBook:"+body);

		}
		else if (selectedRequestType=="Return For Subscriber"){
			String SName = TXTF1.getText();
            String SID = TXTF2.getText();
            String BName = TXTF3.getText();
            String BID = TXTF4.getText();
            String Btime = TXTF5.getText();
            String body = ""+SName+","+SID+","+BName+","+BID+","+Btime;
			ClientUI.chat.accept("Handle return:"+body); 
		}
		}
	}
