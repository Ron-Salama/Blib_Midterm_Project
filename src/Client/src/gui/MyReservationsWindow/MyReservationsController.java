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
    private TableColumn<ReservedBook, String> tableReservationStatus;
    
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	
        // Initialize TableView columns
        tableId.setCellValueFactory(new PropertyValueFactory<>("ISBN")); // ISBN column
        tableName.setCellValueFactory(new PropertyValueFactory<>("name")); // Name column
        tableReservationDate.setCellValueFactory(new PropertyValueFactory<>("reserveDate")); // Reserve Date column
        tableReservationStatus.setCellValueFactory(new PropertyValueFactory<>("reserveStatus")); // Reservation Status column
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
        new Thread(() -> {
            ClientUI.chat.accept("GetReservedBooks:" + currentSub.getSubscriber_id());
            Platform.runLater(() -> {
                if (ChatClient.reservedBookList != null && !ChatClient.reservedBookList.isEmpty()) {
                    tableView.getItems().clear();
                    tableView.getItems().addAll(ChatClient.reservedBookList);
                } else {
                    System.out.println("No reserved books to display.");
                }
            });
        }).start();
    }

    
    private void setupActionsColumn() {
        actions.setCellFactory(param -> new TableCell<ReservedBook, Void>() {
            private final Button retrieveButton = new Button("Retrieve");

            {
                // Button action when clicked
                retrieveButton.setOnAction(event -> {
                    ReservedBook reservedBook = getTableView().getItems().get(getIndex());
                    handleRetrieveBook(event, reservedBook);  // Handle the "Retrieve" logic
                    
                    // Disable the button when it's clicked
                    retrieveButton.setDisable(true);
                    
                    // After disabling the button, update the table view
                    getTableView().refresh();  // Refresh the table to ensure the button state is updated
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);  // No button if the row is empty or has no item
                } else {
                    ReservedBook reservedBook = (ReservedBook) getTableRow().getItem();
                    
                    // If the reserve status is what we expect, show the "Retrieve" button
                    if ("your book is waiting for you :)".equals(reservedBook.getReserveStatus())) {
                        setGraphic(retrieveButton);
                        
                        // If the button is already clicked, disable it
                        if (retrieveButton.isDisabled()) {
                            retrieveButton.setDisable(true);
                        }
                    } else {
                        setGraphic(null);  // Hide the button for other statuses
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
            Submit_Borrow_Request(event, reservedBook);
            // Feedback to the user
            showColoredLabelMessageOnGUI(feedBack, "Book reservation success! A borrow request has been sent.\nAwaiting Librarian approval", "-fx-text-fill: green;");
            
            // Disable the retrieve button after clicking
            Button retrieveButton = (Button) event.getSource();  // Get the button that was clicked
            retrieveButton.setDisable(true);  // Disable the button to prevent further clicks
            
        } catch (Exception e) {
            // Handle any potential errors
            showColoredLabelMessageOnGUI(feedBack, "An error occurred while processing the borrow request.", "-fx-text-fill: red;");
            e.printStackTrace();
        }

        // Step 3: Update table view to reflect changes
        tableView.refresh();
    }




    
    
    public void Submit_Borrow_Request(ActionEvent event, ReservedBook reservedBook) throws Exception {
        // Collect subscriber and book details from the passed reservedBook
        String subscriberId = "" + SubscriberWindowController.currentSubscriber.getSubscriber_id();
        String subscriberName = SubscriberWindowController.currentSubscriber.getSubscriber_name();

        // Use the details from the reservedBook
        String bookId = reservedBook.getISBN();
        String bookName = reservedBook.getName();
        String borrowDate = clockController.timeNow();
        String returnDate = clockController.calculateReturnDate(14);

        String borrowRequest = "" + subscriberId + "," + subscriberName + "," + bookId + "," + bookName + "," + borrowDate + "," + returnDate;
        ClientUI.chat.accept("BorrowRequest:" + borrowRequest);
        
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
    }

    
/* private void updateReservationStatus() {
        // Iterate through the reservations and update the status based on time remaining
        for (ReservedBook reservedBook : tableView.getItems()) {
            if ("Available".equals(reservedBook.getReservationStatus())) {
                // Calculate time remaining (2 days from available)
                LocalDateTime currentDate = LocalDateTime.now();
                LocalDateTime availableDate = reservedBook.getAvailableDate(); // Assuming this field exists
                long daysRemaining = ChronoUnit.DAYS.between(currentDate, availableDate.plusDays(2));

                if (daysRemaining <= 0) {
                    reservedBook.setReservationStatus("Expired"); // Reservation expired
                } else {
                    reservedBook.setReservationStatus("A copy is available! You have " + daysRemaining + " days to retrieve it.");
                }
            }
        }
        tableView.refresh(); // Refresh table to reflect status changes
    }
    */
    /*private void setupActionsColumn() {
        tableActions.setCellFactory(param -> new TableCell<ReservedBook, Void>() { // Explicitly specify the generic types
            private final Button retrieveButton = new Button("Retrieve Book");


            {


            	retrieveButton.setOnAction(event -> {
                    BorrowedBook borrowedBook = getTableView().getItems().get(getIndex());
                	ClientTimeDiffController clock = new ClientTimeDiffController();
                	String extendedReturnDate;
                	
                	extendedReturnDate = clock.extendReturnDate(borrowedBook.getReturnDate(), 14);
                	
                	if(clock.hasEnoughTimeBeforeDeadline(borrowedBook.getReturnDate(), 7)) {
                		if(viewing) {
                			System.out.println("Librarian Manual Extend");
                			ClientUI.chat.accept("UpdateReturnDate:" + borrowedBook.getBorrowId() + "," + extendedReturnDate);
                    		showColoredLabelMessageOnGUI(extensionDynamicLabel, "Extension approved!", "-fx-text-fill: green;");
                    		tableView.getItems().clear();
                    		loadBooks();
                		}else {
                    		ClientUI.chat.accept("UpdateReturnDate:" + borrowedBook.getBorrowId() + "," + extendedReturnDate);
                    		showColoredLabelMessageOnGUI(extensionDynamicLabel, "Extension approved!", "-fx-text-fill: green;");
                    		tableView.getItems().clear();
                    		loadBooks();
                		}
                	}else {
                		showColoredLabelMessageOnGUI(extensionDynamicLabel, "Extension denied!", "-fx-text-fill: red;");
                	}
                });

                returnButton.setOnAction(event -> {
                    BorrowedBook borrowedBook = getTableView().getItems().get(getIndex());
                    System.out.println("Return book: " + borrowedBook.getName());
                    ClientUI.chat.accept("Return request: Subscriber ID is:"+currentSub.getSubscriber_id()+" "+currentSub.getSubscriber_name()+" Borrow info: "+borrowedBook);
                    //ClientUI.chat.accept("Return Book: Subscriber ID is:"+currentSub.getSubscriber_id()+" Book info is:"+borrowedBook);
                    
                });
            }
    */
    
    /*
    /**
     * Sets up actions for the buttons.
    
    private void setupButtonActions() {
        cancelReservationButton.setOnAction(event -> handleCancelReservation());
        markAsCompletedButton.setOnAction(event -> handleMarkAsCompleted());
        backButton.setOnAction(event -> handleBackAction());
    }

    /**
     * Handles the cancel reservation button click.
     */
    /*private void handleCancelReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            // Perform cancel logic
            System.out.println("Canceling reservation for: " + selectedReservation.getTitle());
            // reservationService.cancelReservation(selectedReservation.getId());
            reservationTable.getItems().remove(selectedReservation);
        } else {
            System.out.println("No reservation selected.");
        }
    }*/

    

    
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







