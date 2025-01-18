package gui.MyReservationsWindow;
import java.net.URL;
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

public class MyReservationsController extends BaseController implements Initializable {
    private MyReservationsController mrc;
    
    @FXML
	private Label title;

    
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
    private TableColumn<ReservedBook, String> actions;
    
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
        tableReservationStatus.setCellValueFactory(new PropertyValueFactory<>("reservationStatus")); // Reservation Status column
        actions.setCellValueFactory(new PropertyValueFactory<>("Actions")); // Actions column
        

        currentSub = SubscriberWindowController.currentSubscriber;
        title.setText("My Reservations");

        
        
        loadBooks();
        
        
        
        //********************
        //********************
        //********************
        //setupActionsColumn();
        //********************
        //********************
        //********************
        
        
        
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

    
   /* private void setupActionsColumn() {
        actions.setCellFactory(param -> new TableCell<ReservedBook, Void>() { 
            private final Button retrieveButton = new Button("Retrieve Book");

            {
                // Button action when clicked
                retrieveButton.setOnAction(event -> {
                    ReservedBook reservedBook = getTableView().getItems().get(getIndex());
                    handleRetrieveBook(reservedBook);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    ReservedBook reservedBook = getTableRow().getItem();
                    if ("Available".equals(reservedBook.getReservationStatus())) {
                        // If a copy is available, show the retrieve button
                        setGraphic(retrieveButton);
                    } else {
                        setGraphic(null); // No button if not available
                    }
                }
            }
        });
    }*/
    
    
    
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







