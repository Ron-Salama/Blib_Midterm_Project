package gui.MyReservationsWindow;
import java.net.URL;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import gui.SubscriberWindow.SubscriberWindowController;
import gui.baseController.BaseController;
import javafx.animation.PauseTransition;
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
import javafx.util.Duration;
import logic.BorrowedBook;
import logic.ClientTimeDiffController;
import logic.ReservedBook;
import logic.Subscriber;
import gui.BorrowBookWindow.BorrowBookController;
import logic.ClientTimeDiffController;

public class MyReservationsController extends BaseController implements Initializable {
    private MyReservationsController mrc;
    ClientTimeDiffController clockController = new ClientTimeDiffController();
    
    @FXML
	private Label title;
    
    @FXML
	private Label feedBack;
    
    @FXML
    private TableView<ReservedBook> tableView;

    @FXML
    private TableColumn<ReservedBook, Integer> tableId;

    @FXML
    private TableColumn<ReservedBook, String> tableName;

    @FXML
    private TableColumn<ReservedBook, String> tableReservationDate;

    
    @FXML
    private TableColumn<ReservedBook, Void> actions;
    
    @FXML
    private Button btnCancelReservation;


    @FXML
    private Button btnExit;
	
    @FXML
    private Button btnBack;
    
    @FXML
    private Button btnRefresh;
    
    public static Subscriber currentSub = new Subscriber (0,0,null,null,null,null);
    
    int availableCopiesNum;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	
        // Initialize TableView columns
        tableId.setCellValueFactory(new PropertyValueFactory<>("ISBN")); // ISBN column
        tableName.setCellValueFactory(new PropertyValueFactory<>("name")); // Name column
        tableReservationDate.setCellValueFactory(new PropertyValueFactory<>("reserveDate")); // Reserve Date column
        actions.setCellValueFactory(new PropertyValueFactory<>("Actions")); // Actions column
        

        currentSub = SubscriberWindowController.currentSubscriber;
        title.setText("My Reservations");

        
        
        loadBooks();
        
        
        
        
        setupActionsColumn();
        
        
        tableView.getItems().clear();
        try {
			addDelayInMilliseconds(1000); // one second delay.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

    }

    private void loadBooks() {
//        new Thread(() -> {
            ClientUI.chat.accept("GetReservedBooks:" + currentSub.getSubscriber_id());
            
            waitForServerResponse();
            
           Platform.runLater(() -> {
                if (ChatClient.reservedBookList != null && !ChatClient.reservedBookList.isEmpty()) {
                	//MAYBE ADD HERE TO DELETE RESERVATION TODAY IS 1 DAY MORE THAN THE ACTUAL DATE FOR TIME LEFT TO RETRIEVE
                    tableView.getItems().clear();
                    tableView.getItems().addAll(ChatClient.reservedBookList);
                } else {
                    System.out.println("No reserved books to display.");
                }
            });
        }

    
    private void setupActionsColumn() {
        actions.setCellFactory(param -> new TableCell<ReservedBook, Void>() {
            private final Button retrieveButton = new Button("Retrieve");
            private final HBox centeredBox = new HBox(retrieveButton);

            {
                // Style the button
                retrieveButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

                // Center the button inside the HBox
                centeredBox.setStyle("-fx-alignment: CENTER;");
                centeredBox.setSpacing(10);

                // Button action when clicked
                retrieveButton.setOnAction(event -> {
                    ReservedBook reservedBook = getTableView().getItems().get(getIndex());
                    handleRetrieveBook(event, reservedBook); // Handle the "Retrieve" logic

                    // Mark the book as retrieved
                    reservedBook.setRetrieved(true);

                    // Refresh the table view to reflect the updated state
                    getTableView().refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null); // No button if the row is empty or has no item
                } else {
                    ReservedBook reservedBook = (ReservedBook) getTableRow().getItem();
                    if (!reservedBook.getTimeLeftToRetrieve().equals("Book is not available yet")) {
                        setGraphic(centeredBox);

                        // Enable or disable the button based on the retrieved status
                        retrieveButton.setDisable(reservedBook.isRetrieved());
                    } else {
                        setGraphic(null); // Hide the button for other statuses
                    }
                }
            }
        });
    }






    
    
    
    protected void handleRetrieveBook(ActionEvent event, ReservedBook reservedBook) {
        // Log for debugging
        System.out.println("Retrieve button clicked for book: " + reservedBook.getName());

        // Step 2: Directly submit the borrow request with the specific reservedBook
        try {
        	SubmitRetrieve(event, reservedBook);
            // Feedback to the user
            showColoredLabelMessageOnGUI(feedBack, "Book Retrieve success! Your book list has been updated with your new book addition :)", "-fx-text-fill: green;");
            
            // Disable the retrieve button after clicking
            Button retrieveButton = (Button) event.getSource();  // Get the button that was clicked
            retrieveButton.setDisable(true);  // Disable the button to prevent further clicks
            
        } catch (Exception e) {
            // Handle any potential errors
            showColoredLabelMessageOnGUI(feedBack, "An error occurred while processing the retrieve action.", "-fx-text-fill: red;");
            e.printStackTrace();
        }

        // Step 3: Update table view to reflect changes
        tableView.refresh();
    }




    
    
    public void SubmitRetrieve(ActionEvent event, ReservedBook reservedBook) throws Exception {
        // Collect subscriber and book details from the passed reservedBook
        String subscriberId = "" + SubscriberWindowController.currentSubscriber.getSubscriber_id();
        String subscriberName = SubscriberWindowController.currentSubscriber.getSubscriber_name();

        // Use the details from the reservedBook
        String bookId = reservedBook.getISBN();
        String bookName = reservedBook.getName();
        String borrowDate = clockController.timeNow();
        String returnDate = clockController.calculateReturnDate(14);

        String Retrieve = "" + subscriberName + "," + subscriberId + "," + bookName + "," + bookId + "," + borrowDate + "," + returnDate;
        ClientUI.chat.accept("SubmitRetrieve:" + Retrieve);
        
        waitForServerResponse();
        
        // Step 2: Call the method to delete the reservation from the database
        deleteReservationFromDatabase(reservedBook);
    }

    private void deleteReservationFromDatabase(ReservedBook reservedBook) {
    	// Collect subscriber and book details from the passed reservedBook
        String subscriberId = "" + SubscriberWindowController.currentSubscriber.getSubscriber_id();
    	// Use the details from the reservedBook
        String bookId = reservedBook.getISBN();
        
        String reserveSuccess = subscriberId + "," + bookId;
        ClientUI.chat.accept("ReserveSuccess:" + reserveSuccess);
        waitForServerResponse();
    }

    


    
    public void getRefreshBtn(ActionEvent event) {
    	
    }
    
    
    
    
    
    
    
    /**
     * Handles the Exit button action, navigating back to the Main Menu.
     *
     * @param event the event triggered by clicking the exit button.
     */
    public void getExitBtn(ActionEvent event) {
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"MainMenu");
    }
    
    
    
    
    
    
    /**
     * Handles the back button action, navigating back to the previous window.
     *
     * @param event the event triggered by clicking the back button.
     */
    public void getBackBtn(ActionEvent event) throws Exception {
    	openWindow(event,
    			"/gui/SubscriberWindow/SubscriberWindow.fxml",
    			"/gui/SubscriberWindow/SubscriberWindow.css",
    			"Subscriber View");
	}
    
    
    
}