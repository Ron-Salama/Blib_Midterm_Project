package gui.MyBooksWindow;

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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import logic.BorrowedBook;
import logic.ClientTimeDiffController;
import logic.Subscriber;

/**
 * Controller for the My Books Window in the Library Management Tool.
 * 
 * <p>This class allows users to view their borrowed books, extend borrow dates, return books, 
 * and view the history of borrowed books. It handles user interactions and communicates with the server.</p>
 */
public class MyBooksController extends BaseController implements Initializable {
    
	/** Flag indicating whether the view is for searching books. */ 
	public static String FlagForSearch = "";
    
	/** Label for displaying dynamic messages */
    @FXML
	private Label extensionDynamicLabel;
    
    /** Label for the window title */
    @FXML
	private Label title;
    
    /** Label for showing additional information about the view */
    @FXML
    private Label LBLview;
    
    /** Button for exiting the window */
	@FXML
    private Button btnExit;
	
	/** Button for navigating back */
    @FXML
    private Button btnBack;
    
    /** Button for viewing details */
    @FXML
    private Button btnView;
    
    /** Button for viewing history */
    @FXML
    private Button btnHistory;
    
    /** Table displaying borrowed books */
    @FXML
    private TableView<BorrowedBook> tableView;

    /** Column for ISBN */
    @FXML
    private TableColumn<BorrowedBook, Integer> tableID;
    
    /** Column for borrow date */
    @FXML
    private TableColumn<BorrowedBook, String> tableBorrowDate;
    
    /** Column for return date */
    @FXML
    private TableColumn<BorrowedBook, String> tableReturnDate;
    
    /** Column for book name */
    @FXML
    private TableColumn<BorrowedBook, String> tableName;
    
    /** Column for time left to return */
    @FXML
    private TableColumn<BorrowedBook, Integer> tableTimeLeft;
    
    /** Column for actions (extend, return) */
    @FXML
    private TableColumn<BorrowedBook, Void> tableActions;

    /** Input field for book name search */
    @FXML
    private TextField nameInput;
    
    /** Input field for subscriber ID to view their books */
    @FXML
    private TextField TXTFview;

    /** Input field for book name search */
    @FXML
    private TextField descriptionInput;
    
    /** Current subscriber being viewed or interacting with. */
    public static Subscriber currentSub = new Subscriber (0,0,null,null,null,null);
    
    /** Indicates if a librarian is currently viewing a subscriber's history. */
    public static int librarianViewing = -1;
    
    /** The name of the librarian viewing the subscriber. */
    public static String LibrarianName;
    
    /** Flag indicating whether the view is for a subscriber or a librarian. */
    public static Boolean viewing = false;
    
    /** Flag indicating if the history view is being used. */
    public static Boolean fromHistory = false;
    
    /**
     * Initializes the My Books Window, setting up the TableView columns and populating 
     * the table with books based on the current subscriber's information.
     *
     * @param url the location of the FXML file.
     * @param resourceBundle the resource bundle for internationalization.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize TableView columns
        tableID.setCellValueFactory(new PropertyValueFactory<>("ISBN")); // ISBN column
        tableName.setCellValueFactory(new PropertyValueFactory<>("name")); // Name column
        tableBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate")); // Borrow Date column
        tableReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate")); // Return Date column
        tableTimeLeft.setCellValueFactory(new PropertyValueFactory<>("timeLeftToReturn")); // Time Left column

        if (viewing) {
        	if(fromHistory) {
        		TXTFview.setText(""+currentSub.getSubscriber_id());
        		try {
					updateViewInfo();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}else {
                currentSub = new Subscriber(0, 0, null, null, null, null);
        	}
            tableView.getItems().clear();
            title.setText("waiting for librarian input");
            btnView.setVisible(true);
            TXTFview.setVisible(true);
            LBLview.setVisible(true);

        } else {
            currentSub = SubscriberWindowController.currentSubscriber;
            title.setText("My Books");
            btnView.setVisible(false);
            TXTFview.setVisible(false);
            LBLview.setVisible(false);
            try {
				loadBooks();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }

        // Set up the actions column for extending borrow periods and returning books
        setupActionsColumn();
        tableView.getItems().clear(); // Clear any items before populating the table
    }
    
    /**
     * Loads the list of books borrowed by the current subscriber.
     * 
     * @throws InterruptedException if the thread is interrupted while waiting for server response.
     */
    private void loadBooks() throws InterruptedException {
        ClientUI.chat.accept("GetBorrowedBooks:" + currentSub.getSubscriber_id());
        
        waitForServerResponse();
        
        if(viewing) {
        	if (ChatClient.borrowedBookList != null && !ChatClient.borrowedBookList.isEmpty()) {
                tableView.getItems().clear();
                tableView.getItems().addAll(ChatClient.borrowedBookList);
            } else {
                tableView.getItems().clear();
            }
        }else {
            Platform.runLater(() -> {
            	if (ChatClient.borrowedBookList != null && !ChatClient.borrowedBookList.isEmpty()) {
                    tableView.getItems().clear();
                    tableView.getItems().addAll(ChatClient.borrowedBookList);
                } else {
                    tableView.getItems().clear();
                }
            });
        }  
    }

    /**
     * Updates the view with the information for a specific subscriber.
     *
     * @param event the event triggered by clicking the update button.
     * @throws InterruptedException if the thread is interrupted while waiting for server response.
     */
    public void updateView(ActionEvent event) throws InterruptedException {
    	updateViewInfo();
    }
    
    /**
     * Fetches and updates the current subscriber's information, including their borrowed books.
     *
     * @throws InterruptedException if the thread is interrupted while waiting for server response.
     */
    private void updateViewInfo() throws InterruptedException {
        Platform.runLater(() -> {
            showColoredLabelMessageOnGUI(extensionDynamicLabel, "", "-fx-text-fill: red;");
            String subID = TXTFview.getText();

            if (subID.isEmpty()) {
                showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(extensionDynamicLabel, "You must enter a subscriber ID to find their history.", "-fx-text-fill: red;", 3);
                return;
            }

            ClientUI.chat.accept("Fetch:" + subID);

            waitForServerResponse();

            currentSub = new Subscriber(
                ChatClient.s1.getSubscriber_id(),
                ChatClient.s1.getDetailed_subscription_history(),
                ChatClient.s1.getSubscriber_name(),
                ChatClient.s1.getSubscriber_phone_number(),
                ChatClient.s1.getSubscriber_email(),
                ChatClient.s1.getStatus()
            );

            if (currentSub.getSubscriber_id() == -1) {
                title.setText("No ID Found");
            } else {
                title.setText("Now Viewing Subscriber: " + currentSub.getSubscriber_id() + " , " + currentSub.getSubscriber_name());
            }

            try {
				loadBooks();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
    }
    
    /**
     * Sets up the actions column in the TableView for each book, allowing users to extend borrowing time or return the book.
     */
    private void setupActionsColumn() {
        tableActions.setCellFactory(param -> new TableCell<BorrowedBook, Void>() { // Explicitly specify the generic types
            private final Button extendButton = new Button("Extend borrowing length");
            private final Button returnButton = new Button("Return");
            private final HBox buttonBox = new HBox(10, extendButton, returnButton);

            {
            	// Styles for returnButton (unchanged)
            	String buttonStyle = "-fx-background-color: #171717; " +
            	                     "-fx-text-fill: #DE8F5F; " +
            	                     "-fx-font-size: 16px; " +
            	                     "-fx-font-family: 'Rubik Bold'; " +
            	                     "-fx-font-weight: bold; " +
            	                     "-fx-cursor: hand; " +
            	                     "-fx-effect: dropshadow(gaussian, #000000, 5, 0.3, 0, 1); " +
            	                     "-fx-background-radius: 20px; " +
            	                     "-fx-border-radius: 20px; " +
            	                     "-fx-padding: 5px 15px;";

            	String hoverStyle = "-fx-background-color: #DE8F5F; " +
            	                    "-fx-text-fill: #171717; " +
            	                    "-fx-background-radius: 20px; " +
            	                    "-fx-border-radius: 20px; " +
            	                    "-fx-padding: 5px 15px;";

            	// Wider style for extendButton
            	String extendButtonStyle = "-fx-background-color: #171717; " +
            	                           "-fx-text-fill: #DE8F5F; " +
            	                           "-fx-font-size: 16px; " +
            	                           "-fx-font-family: 'Rubik Bold'; " +
            	                           "-fx-font-weight: bold; " +
            	                           "-fx-cursor: hand; " +
            	                           "-fx-effect: dropshadow(gaussian, #000000, 5, 0.3, 0, 1); " +
            	                           "-fx-background-radius: 20px; " +
            	                           "-fx-border-radius: 20px; " +
            	                           "-fx-padding: 5px 20px; " + // Wider padding
            	                           "-fx-min-width: 220px; " +  // Wider button
            	                           "-fx-max-width: 220px;";

            	String extendButtonHoverStyle = "-fx-background-color: #DE8F5F; " +
            	                                "-fx-text-fill: #171717; " +
            	                                "-fx-background-radius: 20px; " +
            	                                "-fx-border-radius: 20px; " +
            	                                "-fx-padding: 5px 20px; " +
            	                                "-fx-min-width: 220px; " +
            	                                "-fx-max-width: 220px;";

            	// Apply styles to buttons
            	extendButton.setStyle(extendButtonStyle);
            	returnButton.setStyle(buttonStyle);

            	// Hover effects for extendButton
            	extendButton.setOnMouseEntered(e -> extendButton.setStyle(extendButtonHoverStyle));
            	extendButton.setOnMouseExited(e -> extendButton.setStyle(extendButtonStyle));

            	// Hover effects for returnButton (unchanged)
            	returnButton.setOnMouseEntered(e -> returnButton.setStyle(hoverStyle));
            	returnButton.setOnMouseExited(e -> returnButton.setStyle(buttonStyle));


                buttonBox.setStyle("-fx-alignment: CENTER;");

                // Extend Button Action
                extendButton.setOnAction(event -> {
                    BorrowedBook borrowedBook = getTableView().getItems().get(getIndex());
                    if (borrowedBook != null) {
                        ClientTimeDiffController clock = new ClientTimeDiffController();
                        String extendedReturnDate;
                        String ignore = "ignore";
                        String librarianMessage = ",Extended Successfully by Librarian " + librarianViewing + ":" + LibrarianName;
                        String body = currentSub.getSubscriber_name() + "," +
                                      currentSub.getSubscriber_id() + "," +
                                      borrowedBook.getName() + "," +
                                      borrowedBook.getISBN() + "," +
                                      clock.timeNow() + "," + ignore;

                        ClientUI.chat.accept("IsBookReserved:" + borrowedBook.getISBN());
                        waitForServerResponse();

                        if (!ChatClient.isBookReservedFlag) {
                            if (borrowedBook.getTimeLeftToReturn() < 7 && borrowedBook.getTimeLeftToReturn() > 0) {
                                extendedReturnDate = clock.extendReturnDate(borrowedBook.getReturnDate(), 14);
                                ClientUI.chat.accept("UpdateReturnDate:" + borrowedBook.getBorrowId() + "," + extendedReturnDate);
                                if (librarianViewing == -1) {
                                    librarianMessage = ",Extended Successfully by the Subscriber";
                                    ClientUI.chat.accept("NewExtensionApprovedBySubscriber:" + clock.timeNow() + "," + currentSub.getSubscriber_id() + "," + currentSub.getSubscriber_name() + "," + borrowedBook.getName() + "," + extendedReturnDate + ";");
                                }
                                ClientUI.chat.accept("UpdateHistoryInDB:" + body + librarianMessage);
                                waitForServerResponse();
                                showColoredLabelMessageOnGUI(extensionDynamicLabel, "Extension approved!", "-fx-text-fill: green;");
                                tableView.refresh();
                                tableReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
                                tableView.getItems().clear();
                                try {
                                    loadBooks();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(extensionDynamicLabel, "Extension denied! Return time must be 7 days or less and non-negative.", "-fx-text-fill: red;", 3);
                            }
                        } else {
                            showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(extensionDynamicLabel, "Extension denied! Book already reserved.", "-fx-text-fill: red;", 3);
                        }
                    }
                });

                // Return Button Action
                returnButton.setOnAction(event -> {
                    BorrowedBook borrowedBook = getTableView().getItems().get(getIndex());
                    ClientUI.chat.accept("Return request: Subscriber ID is:" + currentSub.getSubscriber_id() + " " + currentSub.getSubscriber_name() + " Borrow info: " + borrowedBook);
                    waitForServerResponse();
                    showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(extensionDynamicLabel, "The book \"" + borrowedBook.getName() + "\" has returned back to the library.", "-fx-text-fill: green;", 3);
                    returnButton.setDisable(true); // Lock the button so the user can't press this button infinitely.
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // Clear the cell if empty
                } else {
                    setGraphic(buttonBox); // Add the buttons to the cell
                }
            }
        });
    }

    /**
     * Navigates to the History Window when the History button is clicked.
     *
     * @param event the event triggered by clicking the History button.
     */
    public void navigateToHistory(ActionEvent event) {
    	openWindow(event,
    			"/gui/HistoryWindow/HistoryFrame.fxml",
    			"/gui/HistoryWindow/HistoryFrame.css",
    			"History");        	
    }

    /**
     * Handles the Exit button action, navigating back to the Main Menu.
     *
     * @param event the event triggered by clicking the exit button.
     */
    public void getExitBtn(ActionEvent event) {
    	fromHistory = false;
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"MainMenu");
    }
   
	
	/**
	 * Handles the Back button action, navigating back to the appropriate view (Librarian or Subscriber view).
	 *
	 * @param event the event triggered by clicking the back button.
	 */
    public void backFromUser(ActionEvent event) {
    	fromHistory = false;
    	if(viewing) {
    		tableView.getItems().clear();
        	openWindow(event,
        			"/gui/LibrarianWindow/LibrarianFrame.fxml",
        			"/gui/LibrarianWindow/LibrarianFrame.css",
        			"Librarian View");
    	}else {
        	openWindow(event,
        			"/gui/SubscriberWindow/SubscriberWindow.fxml",
        			"/gui/SubscriberWindow/SubscriberWindow.css",
        			"Subscriber View");
    	}
    }
}