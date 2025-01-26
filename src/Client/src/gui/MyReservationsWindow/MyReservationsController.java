package gui.MyReservationsWindow;

import java.net.URL;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import gui.SubscriberWindow.SubscriberWindowController;
import gui.baseController.BaseController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import logic.ClientTimeDiffController;
import logic.ReservedBook;
import logic.Subscriber;

/**
 * Controller for the "My Reservations" window in the client application.
 * <p>
 * This controller handles the logic for displaying and retrieving books 
 * that have been reserved by a subscriber. The subscriber can view their 
 * reserved books, check availability, and retrieve them if possible.
 * </p>
 *
 * <p>Implements {@link Initializable} to allow for custom initialization
 * after the FXML file has been loaded.</p>
 *
 * @author 
 * @version 1.0
 * @since 2025-01-01
 */
public class MyReservationsController extends BaseController implements Initializable {

    /**
     * A reference to this controller.
     */
    private MyReservationsController mrc;  

    /**
     * A controller that tracks client-side time differences.
     */
    private final ClientTimeDiffController clockController = new ClientTimeDiffController();

    /**
     * Label displayed at the top of the window.
     */
    @FXML
    private Label title;

    /**
     * Label used to provide feedback messages to the user.
     */
    @FXML
    private Label feedBack;

    /**
     * Label displaying the user's current status (e.g., messages about availability/frozen state).
     */
    @FXML
    private Label statusLabel;

    /**
     * A table displaying reserved books.
     */
    @FXML
    private TableView<ReservedBook> tableView;

    /**
     * Table column for displaying the ISBN of the reserved book.
     */
    @FXML
    private TableColumn<ReservedBook, Integer> tableId;

    /**
     * Table column for displaying the title/name of the reserved book.
     */
    @FXML
    private TableColumn<ReservedBook, String> tableName;

    /**
     * Table column for displaying the reservation date of the book.
     */
    @FXML
    private TableColumn<ReservedBook, String> tableReservationDate;

    /**
     * Table column for the action buttons, such as retrieve.
     */
    @FXML
    private TableColumn<ReservedBook, Void> actions;

    /**
     * A button allowing the user to cancel a reservation (currently not in use in the code).
     */
    @FXML
    private Button btnCancelReservation;

    /**
     * A button to exit the window and go back to the Main Menu.
     */
    @FXML
    private Button btnExit;

    /**
     * A button to navigate back to the previous screen (subscriber window).
     */
    @FXML
    private Button btnBack;

    /**
     * A button to refresh the list of reserved books.
     */
    @FXML
    private Button btnRefresh;

    /**
     * A button to retrieve the selected reserved book.
     */
    @FXML
    private Button retrieveButton;

    /**
     * A container (HBox) for holding action buttons or labels.
     */
    @FXML
    private HBox actionBox;

    /**
     * The currently logged-in subscriber. It is shared with the Subscriber Window controller.
     */
    public static Subscriber currentSub = new Subscriber(0, 0, null, null, null, null);

    /**
     * Represents the total available copies (if needed for logic checks).
     */
    int availableCopiesNum;

    /**
     * Initializes the controller class. This method is automatically called after
     * the FXML file has been loaded.
     *
     * @param url            The location used to resolve relative paths, or null if unknown.
     * @param resourceBundle The resources used to localize the root object, or null if none.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize TableView columns
        tableId.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        tableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableReservationDate.setCellValueFactory(new PropertyValueFactory<>("reserveDate"));
        actions.setCellValueFactory(new PropertyValueFactory<>("Actions"));

        if (actionBox == null) {
            System.out.println("actionBox is null. FXML file is not loading correctly.");
        }

        // Retrieve the current subscriber from the subscriber window
        currentSub = SubscriberWindowController.currentSubscriber;

        // Set UI labels
        title.setText("My Reservations");

        // Load the reserved books from the server and populate the table
        loadBooks();
        setupActionsColumn();

        // Clear the table items to ensure fresh data
        tableView.getItems().clear();

        // Optional delay for UI update (to handle async responses)
        try {
            addDelayInMilliseconds(1000); // one second delay.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the subscriber's reserved books from the server.
     * Populates the TableView with the fetched data.
     */
    private void loadBooks() {
        // Send a request to the server to get the reserved books for the current subscriber
        ClientUI.chat.accept("GetReservedBooks:" + currentSub.getSubscriber_id());

        // Wait for the response to be stored in ChatClient.reservedBookList
        waitForServerResponse();

        // Update UI on the JavaFX Application Thread
        Platform.runLater(() -> {
            if (ChatClient.reservedBookList != null && !ChatClient.reservedBookList.isEmpty()) {
                tableView.getItems().clear();
                tableView.getItems().addAll(ChatClient.reservedBookList);
            } else {
                System.out.println("No reserved books to display.");
            }
        });
    }

    /**
     * Sets up the "Actions" column in the TableView to include a "Retrieve" button and any status labels.
     */
    private void setupActionsColumn() {
        actions.setCellFactory(param -> new TableCell<ReservedBook, Void>() {

            private final Button retrieveButton = new Button("Retrieve");
            private final Label statusLabel = new Label();

            /**
             * A container (HBox) that holds both the retrieve button and the status label.
             */
            private final HBox actionHBox = new HBox(retrieveButton, statusLabel);

            {
                // Style the button and label
                retrieveButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
                statusLabel.setStyle("-fx-font-size: 12px; -fx-padding: 5px;");

                // Align the button and label
                actionHBox.setStyle("-fx-alignment: CENTER_LEFT;");
                actionHBox.setSpacing(10);

                // Button action when clicked
                retrieveButton.setOnAction(event -> {
                    ReservedBook reservedBook = getTableView().getItems().get(getIndex());
                    handleRetrieveBook(event, reservedBook);

                    // Mark the book as retrieved in the ReservedBook object
                    reservedBook.setRetrieved(true);

                    // Refresh the table view to reflect the updated state
                    getTableView().refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    ReservedBook reservedBook = (ReservedBook) getTableRow().getItem();
                    String isAccountFrozen = currentSub.getStatus();

                    // Determine button state and message
                    if (!(isAccountFrozen.equals(" status:Not Frozen"))) {
                        retrieveButton.setDisable(true);
                        statusLabel.setText("Your account is frozen, you can't retrieve the book.");
                    } else if ("Book is not available yet".equals(reservedBook.getTimeLeftToRetrieve())) {
                        retrieveButton.setDisable(true);
                        statusLabel.setText("Book is not available yet.");
                    } else {
                        retrieveButton.setDisable(false);
                        statusLabel.setText("Your book is here!");
                    }

                    setGraphic(actionHBox); // Display the button and label
                }
            }
        });
    }

    /**
     * Handles the logic when the user clicks the "Retrieve" button.
     * Updates the reservation state and calls the server to finalize the retrieve process.
     *
     * @param event         The {@link ActionEvent} triggered by clicking the "Retrieve" button.
     * @param reservedBook  The {@link ReservedBook} being retrieved.
     */
    protected void handleRetrieveBook(ActionEvent event, ReservedBook reservedBook) {
        try {
            // Submit the retrieve request to the server
            SubmitRetrieve(event, reservedBook);

            // Provide feedback to the user
            showColoredLabelMessageOnGUI(feedBack,
                    "Book Retrieve success! Your book list has been updated with your new book addition :)",
                    "-fx-text-fill: green;");

            // Disable the retrieve button after successful click
            Button retrieveButton = (Button) event.getSource();
            retrieveButton.setDisable(true);

        } catch (Exception e) {
            showColoredLabelMessageOnGUI(feedBack,
                    "An error occurred while processing the retrieve action.",
                    "-fx-text-fill: red;");
            e.printStackTrace();
        }

        // Update the table view to reflect any changes
        tableView.refresh();
    }

    /**
     * Submits a "Retrieve" request to the server for the specified reserved book.
     * <p>
     * Also calculates the borrow/return times and sends them to the server.
     * Once submitted, it removes the reservation from the database.
     *
     * @param event         The {@link ActionEvent} from the "Retrieve" button click.
     * @param reservedBook  The {@link ReservedBook} to be retrieved.
     * @throws Exception If an error occurs during the server request or date calculation.
     */
    public void SubmitRetrieve(ActionEvent event, ReservedBook reservedBook) throws Exception {
        // Collect subscriber and book details
        String subscriberId = "" + SubscriberWindowController.currentSubscriber.getSubscriber_id();
        String subscriberName = SubscriberWindowController.currentSubscriber.getSubscriber_name();

        String bookId = reservedBook.getISBN();
        String bookName = reservedBook.getName();
        String borrowDate = clockController.timeNow();
        String returnDate = clockController.calculateReturnDate(14);

        // Format the request to submit
        String retrieveMsg = subscriberName + "," + subscriberId + "," + bookName + "," + bookId + "," + borrowDate + "," + returnDate;
        ClientUI.chat.accept("SubmitRetrieve:" + retrieveMsg);

        waitForServerResponse();
        // After successful retrieve, remove the reservation record
        deleteReservationFromDatabase(reservedBook);
    }

    /**
     * Sends a request to the server to delete the specified reservation from the database.
     *
     * @param reservedBook The {@link ReservedBook} whose reservation should be deleted.
     */
    private void deleteReservationFromDatabase(ReservedBook reservedBook) {
        // Collect subscriber and book details from the passed reservedBook
        String subscriberId = "" + SubscriberWindowController.currentSubscriber.getSubscriber_id();
        String bookId = reservedBook.getISBN();

        String reserveSuccess = subscriberId + "," + bookId;
        ClientUI.chat.accept("ReserveSuccess:" + reserveSuccess);

        // Wait for the server to process the request
        waitForServerResponse();
    }

    /**
     * Handles the "Exit" button click, navigating back to the Main Menu.
     *
     * @param event The {@link ActionEvent} triggered by clicking the exit button.
     */
    public void getExitBtn(ActionEvent event) {
        openWindow(event,
                "/gui/MainMenu/MainMenuFrame.fxml",
                "/gui/MainMenu/MainMenuFrame.css",
                "MainMenu");
    }

    /**
     * Handles the "Back" button click, navigating to the Subscriber Window.
     *
     * @param event The {@link ActionEvent} triggered by clicking the back button.
     * @throws Exception If loading the new window fails.
     */
    public void getBackBtn(ActionEvent event) throws Exception {
        openWindow(event,
                "/gui/SubscriberWindow/SubscriberWindow.fxml",
                "/gui/SubscriberWindow/SubscriberWindow.css",
                "Subscriber View");
    }
}
