package gui.SubscriberRequestsWindows;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import client.ChatClient;
import client.ClientUI;
import gui.LibrarianWindow.LibrarianController;
import gui.baseController.BaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import logic.ClientTimeDiffController;
import logic.Librarian;

/**
 * Controller class for handling subscriber requests in the library management
 * system.
 * <p>
 * This controller manages requests such as "Borrow For Subscriber", "Return For
 * Subscriber", and "Register" (new subscriber), and also allows clearing any
 * current input. It also provides functionality to handle barcoded books for
 * borrow operations.
 * </p>
 *
 * <p>
 * Implements {@link Initializable} to set up UI components after the FXML is
 * loaded.
 * </p>
 *
 * @author
 * @version 1.0
 * @since 2025-01-01
 */
public class SubscriberRequestsWindowsController extends BaseController implements Initializable {

	/**
	 * An array holding information about a borrowed book when scanned by a barcode.
	 * Used to populate the form for a "Borrow For Subscriber" request.
	 */
	public static String[] borrowedBookInformationFromBarcode = null;

	/**
	 * Flag indicating whether the controller should use barcode-based information.
	 */
	public static boolean borrowInformationFromBarcode = false;

	/**
	 * Holds the current librarian information.
	 */
	Librarian currentLibrarian = LibrarianController.currentLibrarian;

	/**
	 * A controller for managing client-server time differences.
	 */
	private final ClientTimeDiffController clock = ChatClient.clock;

	/**
	 * Button for exiting to the main menu.
	 */
	@FXML
	private Button btnExit;

	/**
	 * Button to accept or confirm the selected request type (borrow, return, etc.).
	 */
	@FXML
	private Button btnAccept;

	/**
	 * Button to send or finalize the current operation (not used in the provided
	 * code, but declared).
	 */
	@FXML
	private Button btnSend;

	/**
	 * Button to navigate back to the previous window (the librarian window in this
	 * case).
	 */
	@FXML
	private Button btnBack;

	/**
	 * Button to initiate a barcode scan for borrowing a book.
	 */
	@FXML
	private Button ScanBarcode;

	/**
	 * ToggleButton for clearing any current input (resets the fields).
	 */
	@FXML
	private ToggleButton Clear;

	/**
	 * ToggleButton for marking the operation as "Borrow For Subscriber".
	 */
	@FXML
	private ToggleButton BorrowForSubscriber;

	/**
	 * ToggleButton for marking the operation as "Return For Subscriber".
	 */
	@FXML
	private ToggleButton ReturnForSubscriber;

	/**
	 * ToggleButton for marking the operation as "Register" (add new subscriber).
	 */
	@FXML
	private ToggleButton Register;

	/**
	 * A DatePicker for specifying (or displaying) a borrow or return date.
	 */
	@FXML
	private DatePicker datePicker;

	/**
	 * A checkbox to indicate if a returned book was lost.
	 */
	@FXML
	private CheckBox isLost;

	/**
	 * Label for describing the first text field (e.g., "Subscriber Name").
	 */
	@FXML
	private Label LBL1;

	/**
	 * Label for describing the second text field (e.g., "Subscriber ID").
	 */
	@FXML
	private Label LBL2;

	/**
	 * Label for describing the third text field (e.g., "Book Name" or "Phone
	 * Number").
	 */
	@FXML
	private Label LBL3;

	/**
	 * Label for describing the fourth text field (e.g., "Book ID" or "Email").
	 */
	@FXML
	private Label LBL4;

	/**
	 * Label for describing the fifth text field (e.g., "Borrowed At" or "Return
	 * Time").
	 */
	@FXML
	private Label LBL5;

	/**
	 * Label for describing a sixth field (some requests require an extra label).
	 */
	@FXML
	private Label LBL6;

	/**
	 * A label used to give feedback messages to the user.
	 */
	@FXML
	private Label feedbackLabel;

	/**
	 * A text field typically representing the first piece of data (e.g., subscriber
	 * name or request detail).
	 */
	@FXML
	private TextField TXTF1;

	/**
	 * A text field representing the second piece of data (e.g., subscriber ID).
	 */
	@FXML
	private TextField TXTF2;

	/**
	 * A text field representing the third piece of data (e.g., book name, phone
	 * number).
	 */
	@FXML
	private TextField TXTF3;

	/**
	 * A text field representing the fourth piece of data (e.g., book ID, email).
	 */
	@FXML
	private TextField TXTF4;

	/**
	 * A text field representing the fifth piece of data (e.g., borrow time).
	 */
	@FXML
	private TextField TXTF5;

	/**
	 * ComboBox listing subscriber names or other relevant identifiers for the
	 * request.
	 */
	@FXML
	private ComboBox<String> RequestedByCB;

	/**
	 * ComboBox listing the specific request details (e.g., which book is being
	 * borrowed).
	 */
	@FXML
	private ComboBox<String> RequestCB;

	/**
	 * Holds all "Borrow For Subscriber" requests retrieved from the server.
	 */
	private final List<String[]> borrowRequests = new ArrayList<>();

	/**
	 * Holds all "Register" requests retrieved from the server.
	 */
	private final List<String[]> RegisterRequests = new ArrayList<>();

	/**
	 * Holds all "Return For Subscriber" requests retrieved from the server.
	 */
	private final List<String[]> ReturnRequests = new ArrayList<>();

	/**
	 * A string indicating the current request type (e.g., "Borrow For Subscriber",
	 * "Return For Subscriber", "Registers", "Clear").
	 */
	private String requestType = "";

	/**
	 * Initializes the controller after the FXML is loaded.
	 *
	 * @param url            the location used to resolve relative paths, or null if
	 *                       not known.
	 * @param resourceBundle the resource bundle containing localized objects, or
	 *                       null if not used.
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		btnAccept.setDisable(false);

		// If we already have borrowed book info from a barcode, load it directly
		if (borrowInformationFromBarcode) {
			try {
				borrowRequestSetupFromBarcode(borrowedBookInformationFromBarcode[0],
						borrowedBookInformationFromBarcode[1]);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			ScanBarcode.setVisible(false);
			Clear.setSelected(true);
		}

		// Prepare ComboBoxes
		RequestedByCB.getItems().add("");
		RequestCB.getItems().add("");

		// Event handlers to fill data automatically upon selection
		RequestedByCB.setOnAction(event -> autofillSubscriberData());
		RequestCB.setOnAction(event -> autofillRequestData());

		// ToggleButtons for different request flows
		Clear.setOnAction(event -> {
			try {
				Clear();
				btnAccept.setDisable(false);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		BorrowForSubscriber.setOnAction(event -> {
			try {
				BorrowForSubscriber();
				btnAccept.setDisable(false);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		ReturnForSubscriber.setOnAction(event -> {
			try {
				ReturnForSubscriber();
				btnAccept.setDisable(false);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		Register.setOnAction(event -> {
			try {
				Register();
				btnAccept.setDisable(false);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Clears the form (e.g., resets fields and hides certain controls).
	 *
	 * @throws InterruptedException if waiting for server responses is interrupted.
	 */
	public void Clear() throws InterruptedException {
		ScanBarcode.setVisible(false);
		updateLabels("Clear");
		deselectOtherButtons(Clear);
		requestType = "Clear";
		datePicker.setVisible(false);
		LBL5.setVisible(true);
		LBL6.setVisible(false);
		Clear.setSelected(true);
		datePicker.setValue(null);
		isLost.setVisible(false);
	}

	/**
	 * Sets up the form for a "Register" request. This allows registering a new
	 * subscriber.
	 *
	 * @throws InterruptedException if waiting for server responses is interrupted.
	 */
	public void Register() throws InterruptedException {
		ScanBarcode.setVisible(false);
		updateLabels("Registers");
		deselectOtherButtons(Register);
		requestType = "Registers";
		datePicker.setVisible(false);
		isLost.setVisible(false);
		LBL5.setVisible(false);
		LBL6.setVisible(false);
		Register.setSelected(true);
		datePicker.setValue(null);
	}

	/**
	 * Sets up the form for a "Borrow For Subscriber" request, optionally showing a
	 * barcode scanner button if needed.
	 *
	 * @throws InterruptedException if waiting for server responses is interrupted.
	 */
	public void BorrowForSubscriber() throws InterruptedException {
		ScanBarcode.setVisible(true);
		updateLabels("Borrow For Subscriber");
		deselectOtherButtons(BorrowForSubscriber);
		requestType = "Borrow For Subscriber";
		datePicker.setVisible(true);
		isLost.setVisible(false);
		LBL5.setVisible(true);
		LBL6.setVisible(true);
		BorrowForSubscriber.setSelected(true);
		datePicker.setValue(null);
		datePicker.setDisable(true);
	}

	/**
	 * Sets up the form for a "Return For Subscriber" request.
	 *
	 * @throws InterruptedException if waiting for server responses is interrupted.
	 */
	public void ReturnForSubscriber() throws InterruptedException {
		ScanBarcode.setVisible(false);
		updateLabels("Return For Subscriber");
		deselectOtherButtons(ReturnForSubscriber);
		requestType = "Return For Subscriber";
		datePicker.setVisible(true);
		isLost.setVisible(true);
		LBL5.setVisible(true);
		LBL6.setVisible(true);
		ReturnForSubscriber.setSelected(true);
		datePicker.setValue(null);
		datePicker.setDisable(false);
	}

	/**
	 * Deselects all ToggleButtons except for the specified one.
	 *
	 * @param selectedButton the button that should remain selected.
	 */
	private void deselectOtherButtons(ToggleButton selectedButton) {
		if (selectedButton != Clear) {
			Clear.setSelected(false);
		}
		if (selectedButton != BorrowForSubscriber) {
			BorrowForSubscriber.setSelected(false);
		}
		if (selectedButton != ReturnForSubscriber) {
			ReturnForSubscriber.setSelected(false);
		}
		if (selectedButton != Register) {
			Register.setSelected(false);
		}
	}

	/**
	 * Updates the label text, toggles visible components, and fetches relevant
	 * requests from the server based on the selected request type.
	 *
	 * @param type the type of request selected (e.g., "Clear", "Registers", "Borrow
	 *             For Subscriber", "Return For Subscriber").
	 * @throws InterruptedException if waiting for server responses is interrupted.
	 */
	public void updateLabels(String type) throws InterruptedException {
		String selectedRequestType = type;
		// Clear all ComboBoxes and text fields
		clearFieldsAndComboBoxes();

		// Set the fields and populate the RequestedByCB based on the selected request
		// type
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

		default: // Clear
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

	/**
	 * Processes "Return For Subscriber" requests retrieved from the server and
	 * populates the UI fields accordingly.
	 */
	private void handleReturnofBorrowedBook() {
		ReturnRequests.clear(); // Clear the existing list to avoid duplicate data
		clearFieldsAndComboBoxes();

		if (ChatClient.br != null && !ChatClient.br.isEmpty()) {
			// Each element in ChatClient.br is a 2D array of return requests
			for (int i = 0; i < ChatClient.br.size(); i++) {
				String[][] ReturnRequestsArray = ChatClient.br.get(i);

				for (String[] request : ReturnRequestsArray) {
					if (request.length == 8) {
						ReturnRequests.add(request);
					} else {
						System.out.println(
								"Invalid Return request data at index " + i + ": " + String.join(",", request));
					}
				}
			}

			// Create a set to store unique subscriber names to avoid duplicates
			ObservableList<String> requestedByList = FXCollections.observableArrayList();
			Set<String> uniqueNames = new HashSet<>();

			for (String[] returnRequest : ReturnRequests) {
				String subscriberName = returnRequest[2];
				if (!uniqueNames.contains(subscriberName)) {
					uniqueNames.add(subscriberName);
					requestedByList.add(subscriberName);
				}
			}
			RequestedByCB.setItems(requestedByList);
		} else {
			System.out.println("No borrow requests available.");
		}
	}

	/**
	 * Called when the user selects a subscriber name in the {@link #RequestedByCB}
	 * ComboBox. Automatically populates the other ComboBox with all relevant
	 * requests for that subscriber.
	 */
	private void autofillSubscriberData() {
		String selectedName = RequestedByCB.getValue();
		if (selectedName != null && !selectedName.isEmpty()) {
			// Clear request-specific combos and text fields
			clearRequestCBAndTextFields();

			// Gather requests for the selected subscriber
			List<String[]> selectedRequests = new ArrayList<>();
			String selectedRequestType = requestType;

			if ("Registers".equals(selectedRequestType)) {
				for (String[] request : RegisterRequests) {
					if (request[2].equals(selectedName)) {
						selectedRequests.add(request);
					}
				}
			} else if ("Borrow For Subscriber".equals(selectedRequestType)) {
				for (String[] request : borrowRequests) {
					if (request[2].equals(selectedName)) {
						selectedRequests.add(request);
					}
				}
			} else if ("Return For Subscriber".equals(selectedRequestType)) {
				for (String[] request : ReturnRequests) {
					if (request[2].equals(selectedName)) {
						selectedRequests.add(request);
					}
				}
			}

			// Populate the RequestCB ComboBox with the relevant data
			for (String[] request : selectedRequests) {
				if ("Registers".equals(selectedRequestType)) {
					RequestCB.getItems().add("(ID: " + request[1] + " ,Name: " + request[2] + ")");
				} else {
					// For borrow/return, we show the book name and ID
					RequestCB.getItems().add(request[3] + " (Book ID: " + request[4] + ")");
				}
			}
		}
	}

	/**
	 * Called when the user selects a specific request in the {@link #RequestCB}
	 * ComboBox. Automatically fills in the text fields with details from that
	 * request.
	 */
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
					TXTF5.setText(request[5]); // Borrow/Return Time if applicable
					break;
				}
			}
		}
	}

	/**
	 * Formats a request into a string suitable for display in the
	 * {@link #RequestCB} ComboBox.
	 *
	 * @param requestType the type of request ("Registers", "Borrow For Subscriber",
	 *                    or "Return For Subscriber").
	 * @param request     the array of fields that represent the request.
	 * @return a formatted string matching the display style for that request type.
	 */
	private String formatRequest(String requestType, String[] request) {
		if ("Registers".equals(requestType)) {
			return "(ID: " + request[1] + " ,Name: " + request[2] + ")";
		} else {
			return request[3] + " (Book ID: " + request[4] + ")";
		}
	}

	/**
	 * Clears all text fields in the UI.
	 */
	private void clearTextFields() {
		TXTF1.clear();
		TXTF2.clear();
		TXTF3.clear();
		TXTF4.clear();
		TXTF5.clear();
	}

	/**
	 * Processes "Borrow For Subscriber" requests retrieved from the server and
	 * populates the UI fields accordingly.
	 */
	public void handleFetchedBorrowedBooks() {
		borrowRequests.clear();
		clearFieldsAndComboBoxes();

		if (ChatClient.br != null && !ChatClient.br.isEmpty()) {
			for (int i = 0; i < ChatClient.br.size(); i++) {
				String[][] borrowRequestArray = ChatClient.br.get(i);
				for (String[] request : borrowRequestArray) {
					if (request.length == 8) {
						borrowRequests.add(request);
					} else {
						System.out.println(
								"Invalid borrow request data at index " + i + ": " + String.join(",", request));
					}
				}
			}

			// Collect unique subscriber names
			ObservableList<String> requestedByList = FXCollections.observableArrayList();
			Set<String> uniqueNames = new HashSet<>();

			for (String[] borrowRequest : borrowRequests) {
				String subscriberName = borrowRequest[2];
				if (!uniqueNames.contains(subscriberName)) {
					uniqueNames.add(subscriberName);
					requestedByList.add(subscriberName);
				}
			}
			RequestedByCB.setItems(requestedByList);
		} else {
			System.out.println("No borrow requests available.");
		}
	}

	/**
	 * Processes "Register" requests retrieved from the server and populates the UI
	 * fields accordingly.
	 */
	public void handleFetchedRegister() {
		RegisterRequests.clear();
		clearFieldsAndComboBoxes();

		if (ChatClient.br != null && !ChatClient.br.isEmpty()) {
			for (int i = 0; i < ChatClient.br.size(); i++) {
				String[][] RegisterRequestsArray = ChatClient.br.get(i);
				for (String[] request : RegisterRequestsArray) {
					if (request.length == 8) {
						RegisterRequests.add(request);
					} else {
						System.out.println(
								"Invalid register request data at index " + i + ": " + String.join(",", request));
					}
				}
			}

			// Populate the subscriber names from the register requests
			ObservableList<String> requestedByList = FXCollections.observableArrayList();
			for (String[] registerRequest : RegisterRequests) {
				requestedByList.add(registerRequest[2]); // name at index 2
			}
			RequestedByCB.setItems(requestedByList);
		} else {
			System.out.println("No register requests available.");
		}
	}

	/**
	 * Clears both the subscriber ComboBox, request ComboBox, and text fields.
	 */
	private void clearFieldsAndComboBoxes() {
		RequestedByCB.getItems().clear();
		clearRequestCBAndTextFields();
	}

	/**
	 * Clears only the "RequestCB" ComboBox and the text fields.
	 */
	private void clearRequestCBAndTextFields() {
		RequestCB.getItems().clear();
		TXTF1.setText("");
		TXTF2.setText("");
		TXTF3.setText("");
		TXTF4.setText("");
		TXTF5.setText("");
	}

	/**
	 * Handles the "Accept Request" action when the user clicks the accept button.
	 * <p>
	 * Validates the fields based on the current request type and sends relevant
	 * messages to the server. Also updates subscriber history when appropriate.
	 * </p>
	 *
	 * @param event The event triggered by clicking the "Accept" button.
	 * @throws Exception if any server communication or logic error occurs.
	 */
	public void acceptRequest(ActionEvent event) throws Exception {
		String selectedRequestType = requestType;

		// Check if required fields are filled
		if (!areAllFieldsFilled(feedbackLabel, selectedRequestType, TXTF1, TXTF2, TXTF3, TXTF4, TXTF5)) {
			return;
		}

		// Borrow For Subscriber
		if ("Borrow For Subscriber".equals(selectedRequestType)) {
			String SName = TXTF1.getText();
			String SID = TXTF2.getText();
			String BName = TXTF3.getText();
			String BID = TXTF4.getText();
			String Btime = TXTF5.getText();
			String Rtime = clock.convertDateFormat("" + datePicker.getValue());
			String body = SName + "," + SID + "," + BName + "," + BID + "," + Btime + "," + Rtime;

			// Check if the subscriber is frozen
			ClientUI.chat.accept("Fetch:" + SID);
			waitForServerResponse();

			if (ChatClient.s1.getStatus().split(":")[1].equals("Frozen at")) {
				showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(feedbackLabel, "The subscriber "
						+ ChatClient.s1.getSubscriber_name() + " is currently frozen and can't borrow books.",
						"-fx-text-fill: blue;", 3);
				return;
			}

			if (borrowInformationFromBarcode) {
				ClientUI.chat.accept("SubmitBorrowRequestBarcode:" + body);
				waitForServerResponse();
				btnAccept.setDisable(true);
			} else {
				ClientUI.chat.accept("SubmitBorrowRequest:" + body);
				waitForServerResponse();
				btnAccept.setDisable(true);
			}
			ClientUI.chat.accept("UpdateHistoryInDB:" + body + ",Borrowed Successfully");
			waitForServerResponse();

			showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(feedbackLabel,
					"Borrow request accepted successfully!", "-fx-text-fill: green;", 3);

			// Return For Subscriber
		} else if ("Return For Subscriber".equals(selectedRequestType)) {
			String statusOfReturn;
			String SName = TXTF1.getText();
			String SID = TXTF2.getText();
			String BName = TXTF3.getText();
			String BID = TXTF4.getText();
			String Btime = TXTF5.getText();
			String Rtime = clock.convertDateFormat("" + datePicker.getValue());
			String body = SName + "," + SID + "," + BName + "," + BID + "," + Rtime + "," + Btime;

			boolean lostBook = isLost.isSelected();
			if (lostBook) {
				// Handle lost book
				ClientUI.chat.accept("Handle Lost:" + body);
				waitForServerResponse();
				ClientUI.chat.accept("UpdateHistoryInDB:" + body + ",Lost");
				waitForServerResponse();

				showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(feedbackLabel,
						"Return request accepted successfully! (Book marked as lost)", "-fx-text-fill: green;", 3);
				btnAccept.setDisable(true);

			} else {
				// Normal return
				String expectedReturn = clock.convertStringToLocalDate(Btime).plusDays(14).toString();
				int numOfDaysOfReturn = clock.timeDateDifferenceBetweenTwoDates(clock.convertDateFormat(expectedReturn),
						clock.convertDateFormat(clock.convertStringToLocalDate(Rtime).toString()));

				if (numOfDaysOfReturn <= 0) {
					statusOfReturn = "early";
					numOfDaysOfReturn = Math.abs(numOfDaysOfReturn);
				} else {
					statusOfReturn = "late";
				}

				ClientUI.chat.accept("Handle return:" + body);
				waitForServerResponse();
				ClientUI.chat.accept("UpdateHistoryInDB:" + body + ",Return Successfully " + numOfDaysOfReturn
						+ " days " + statusOfReturn);
				waitForServerResponse();

				showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(feedbackLabel,
						"Return request accepted successfully! (" + statusOfReturn + ")", "-fx-text-fill: green;", 7);
				btnAccept.setDisable(true);
			}

			// Register
		} else if ("Registers".equals(selectedRequestType)) {
			String SName = TXTF1.getText();
			String SID = TXTF2.getText();
			String PhoneNum = TXTF3.getText();
			String Email = TXTF4.getText();
			String date = clock.timeNow();
			String ignore2 = "ignore";

			String body1 = SName + "," + SID + "," + PhoneNum + "," + Email;
			String body2 = SName + "," + SID + "," + PhoneNum + "," + Email + "," + date + "," + ignore2;

			ClientUI.chat.accept("Handle register:" + body1);
			waitForServerResponse();
			ClientUI.chat.accept("UpdateHistoryInDB:" + body2 + ",Register Successfully");
			waitForServerResponse();

			showColoredLabelMessageOnGUI(feedbackLabel, "Registration request accepted successfully!",
					"-fx-text-fill: green;");
			btnAccept.setDisable(true);
		}
	}

	/**
	 * Navigates back to the Main Menu when the exit button is clicked.
	 *
	 * @param event the event triggered by clicking the "Exit" button.
	 * @throws Exception if loading the new window fails.
	 */
	public void getExitBtn(ActionEvent event) throws Exception {
		openWindow(event, "/gui/MainMenu/MainMenuFrame.fxml", "/gui/MainMenu/MainMenuFrame.css", "MainMenu");
	}

	/**
	 * Navigates back to the Librarian Window, disabling barcode usage for the next
	 * session.
	 *
	 * @param event the event triggered by clicking the "Back" button.
	 * @throws Exception if loading the new window fails.
	 */
	public void Back(ActionEvent event) throws Exception {
		borrowInformationFromBarcode = false;
		openWindow(event, "/gui/LibrarianWindow/LibrarianFrame.fxml", "/gui/LibrarianWindow/LibrarianFrame.css",
				"Librarian Window");
	}

	/**
	 * Opens the barcode scanner window to retrieve book information for a "Borrow
	 * For Subscriber" request.
	 *
	 * @param event the event triggered by clicking the "Scan Barcode" button.
	 * @throws Exception if loading the new window fails.
	 */
	public void getScanBarcodeBtn(ActionEvent event) throws Exception {
		borrowInformationFromBarcode = true;
		openWindow(event, "/gui/BarcodeScannerWindow/BarcodeScannerWindowFrame.fxml",
				"/gui/BarcodeScannerWindow/BarcodeScannerWindowFrame.fxml", "Scan Barcode");
	}

	/**
	 * Sets up the form fields using data read from a barcode scan.
	 * 
	 * @param borrowedBookID   The book's ID retrieved from the barcode.
	 * @param borrowedBookName The book's name retrieved from the barcode.
	 * @throws InterruptedException if waiting for server responses is interrupted.
	 */
	private void borrowRequestSetupFromBarcode(String borrowedBookID, String borrowedBookName)
			throws InterruptedException {
		Clear();
		BorrowForSubscriber();

		// Put text examples here.
		TXTF1.setPromptText("Ex: Kim_Possible");
		TXTF2.setPromptText("EX: 1");
		
		TXTF3.setText(borrowedBookName); // Book name
		TXTF4.setText(borrowedBookID); // Book ID
		String borrowDate = clock.timeNow();
		TXTF5.setText(borrowDate); // Borrow time

		LocalDate returnDate = clock.convertStringToLocalDate(clock.calculateReturnDate(14));
		datePicker.setValue(returnDate);
		datePicker.setDisable(true);

	}

	/**
	 * Configures the UI for a "Register" request type, fetching any server data as
	 * needed.
	 *
	 * @throws InterruptedException if waiting for server responses is interrupted.
	 */
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

	/**
	 * Checks if all required fields are filled based on the current request type.
	 * Displays an error message if some fields are missing.
	 *
	 * @param feedbackLabel       the {@link Label} used to display error messages.
	 * @param selectedRequestType the current request type (e.g., "Borrow For
	 *                            Subscriber").
	 * @param TXTF1               the first {@link TextField}.
	 * @param TXTF2               the second {@link TextField}.
	 * @param TXTF3               the third {@link TextField}.
	 * @param TXTF4               the fourth {@link TextField}.
	 * @param TXTF5               the fifth {@link TextField}.
	 * @return true if all fields are filled, false otherwise.
	 */
	private boolean areAllFieldsFilled(Label feedbackLabel, String selectedRequestType, TextField TXTF1,
			TextField TXTF2, TextField TXTF3, TextField TXTF4, TextField TXTF5) {

		StringBuilder missingFields = new StringBuilder("Please fill out the following fields: ");
		boolean allFieldsFilled = true;

		switch (selectedRequestType) {

		case "Borrow For Subscriber":
			if (isEmpty(TXTF1)) {
				missingFields.append("Subscriber Name, ");
				allFieldsFilled = false;
			}
			if (isEmpty(TXTF2)) {
				missingFields.append("Subscriber ID, ");
				allFieldsFilled = false;
			}
			if (isEmpty(TXTF3)) {
				missingFields.append("Book Name, ");
				allFieldsFilled = false;
			}
			if (isEmpty(TXTF4)) {
				missingFields.append("Book ID, ");
				allFieldsFilled = false;
			}
			if (isEmpty(TXTF5)) {
				missingFields.append("Borrow Time, ");
				allFieldsFilled = false;
			}
			break;

		case "Return For Subscriber":
			if (isEmpty(TXTF1)) {
				missingFields.append("Subscriber Name, ");
				allFieldsFilled = false;
			}
			if (isEmpty(TXTF2)) {
				missingFields.append("Subscriber ID, ");
				allFieldsFilled = false;
			}
			if (isEmpty(TXTF3)) {
				missingFields.append("Book Name, ");
				allFieldsFilled = false;
			}
			if (isEmpty(TXTF4)) {
				missingFields.append("Book ID, ");
				allFieldsFilled = false;
			}
			if (isEmpty(TXTF5)) {
				missingFields.append("Return Time, ");
				allFieldsFilled = false;
			}
			break;

		case "Registers":
			if (isEmpty(TXTF1)) {
				missingFields.append("Subscriber Name, ");
				allFieldsFilled = false;
			}
			if (isEmpty(TXTF2)) {
				missingFields.append("Subscriber ID, ");
				allFieldsFilled = false;
			}
			if (isEmpty(TXTF3)) {
				missingFields.append("Phone Number, ");
				allFieldsFilled = false;
			}
			if (isEmpty(TXTF4)) {
				missingFields.append("Email, ");
				allFieldsFilled = false;
			}
			break;

		default:
			showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(feedbackLabel, "Unknown request type.",
					"-fx-text-fill: red;", 3);
			return false;
		}

		if (!allFieldsFilled) {
			missingFields.setLength(missingFields.length() - 2); // remove trailing ", "
			showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(feedbackLabel, missingFields.toString(),
					"-fx-text-fill: red;", 10);
		}

		return allFieldsFilled;
	}

	/**
	 * Checks if the given {@link TextField} is null or empty.
	 *
	 * @param field the text field to check.
	 * @return true if null or empty, false otherwise.
	 */
	private boolean isEmpty(TextField field) {
		return (field.getText() == null || field.getText().trim().isEmpty());
	}
}