package gui.MyBooksWindow;

import java.net.URL;
import java.time.LocalDateTime;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import logic.BorrowedBook;
import logic.ClientTimeDiffController;
import logic.Subscriber;

/**
 * Controller for the Search Window in the Library Management Tool.
 * 
 * <p>This class allows users to search for books based on name, description, 
 * and subject filters, and displays the results in a TableView. It also provides 
 * an option to navigate back to the Main Menu.</p>
 */
public class MyBooksController extends BaseController implements Initializable {
    public static String FlagForSearch = "";
    
    @FXML
	private Label extensionDynamicLabel;
    @FXML
	private Label title;
    @FXML
    private Label LBLview;
    
	@FXML
    private Button btnExit;
	
    @FXML
    private Button btnBackF;
    @FXML
    private Button btnView;
    
    @FXML
    private Button btnHistory;
    
    @FXML
    private TableView<BorrowedBook> tableView;

    @FXML
    private TableColumn<BorrowedBook, Integer> tableID;
    
    @FXML
    private TableColumn<BorrowedBook, String> tableBorrowDate;
    
    @FXML
    private TableColumn<BorrowedBook, String> tableReturnDate;
    
    @FXML
    private TableColumn<BorrowedBook, String> tableName;
    
    @FXML
    private TableColumn<BorrowedBook, Integer> tableTimeLeft;
    
    @FXML
    private TableColumn<BorrowedBook, Void> tableActions;


    @FXML
    private TextField nameInput;
    @FXML
    private TextField TXTFview;

    @FXML
    private TextField descriptionInput;
    public static Subscriber currentSub = new Subscriber (0,0,null,null,null,null);
    public static int librarianViewing = -1;
    public static String LibrarianName;
    public static Boolean viewing = false;
    public static Boolean fromHistory = false;
    private ClientTimeDiffController clock = ChatClient.clock;
    
    /**
     * Initializes the Search Window by setting up the TableView and ComboBox.
     * Populates the table with all available books when the page loads.
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

        // Set up the actions column
        setupActionsColumn();
        tableView.getItems().clear(); // Clear any items before populating the table
    }


    
    private void loadBooks() throws InterruptedException {

            ClientUI.chat.accept("GetBorrowedBooks:" + currentSub.getSubscriber_id());
            
            waitForServerResponse();
            
            if(viewing) {
            	if (ChatClient.borrowedBookList != null && !ChatClient.borrowedBookList.isEmpty()) {
                    tableView.getItems().clear();
                    tableView.getItems().addAll(ChatClient.borrowedBookList);
                } else {
                    System.out.println("No borrowed books to display.");
                    tableView.getItems().clear();
                }
            }else {
                Platform.runLater(() -> {
                	if (ChatClient.borrowedBookList != null && !ChatClient.borrowedBookList.isEmpty()) {
                        tableView.getItems().clear();
                        tableView.getItems().addAll(ChatClient.borrowedBookList);
                    } else {
                        System.out.println("No borrowed books to display.");
                        tableView.getItems().clear();
                    }
                });
            }  
    }
    
    


    public void updateView(ActionEvent event) throws InterruptedException {
    	updateViewInfo();
    }
    
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
    
        private void setupActionsColumn() {
            tableActions.setCellFactory(param -> new TableCell<BorrowedBook, Void>() { // Explicitly specify the generic types
                private final Button extendButton = new Button("Extend borrowing length");
                private final Button returnButton = new Button("Return");
                private final HBox buttonBox = new HBox(10, extendButton, returnButton);

                {
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
                                    borrowedBook.getBorrowId() + "," +
                                    clock.timeNow() + "," + ignore;

                            ClientUI.chat.accept("IsBookReserved:" + borrowedBook.getISBN());
                            waitForServerResponse();

                            if (!ChatClient.isBookReservedFlag) {
                                if (borrowedBook.getTimeLeftToReturn() < 7 && borrowedBook.getTimeLeftToReturn() > 0) {
                                    extendedReturnDate = clock.extendReturnDate(borrowedBook.getReturnDate(), 14);
                                    ClientUI.chat.accept("UpdateReturnDate:" + borrowedBook.getBorrowId() + "," + extendedReturnDate);
                                    ClientUI.chat.accept("UpdateHistoryInDB:" + body + librarianMessage);
                                    waitForServerResponse();
                                    showColoredLabelMessageOnGUI(extensionDynamicLabel, "Extension approved!", "-fx-text-fill: green;"); // XXX
                                    tableView.refresh();
                                    tableReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
                                    tableView.getItems().clear();
                                    try {
										loadBooks();
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
                                } else {

                                   showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(extensionDynamicLabel, "Extension denied! Return time must be 7 days or less and non negative.", "-fx-text-fill: red;",3);
                                }
                            } else {
                            	showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(extensionDynamicLabel, "Extension denied! Book already reserved.", "-fx-text-fill: red;",3);

                            }
                        }
                    });

                    returnButton.setOnAction(event -> {
                        BorrowedBook borrowedBook = getTableView().getItems().get(getIndex());
                        String extendedReturnDate = clock.extendReturnDate(borrowedBook.getReturnDate(), 14);
                        
                        System.out.println("Return book: " + borrowedBook.getName());
                        ClientUI.chat.accept("Return request: Subscriber ID is:" + currentSub.getSubscriber_id() + " " + currentSub.getSubscriber_name() + " Borrow info: " + borrowedBook);
                        // Send information about the request to the librarians.
                        ClientUI.chat.accept("NewExtensionApprovedBySubscriber:" + clock.timeNow() + "," + currentSub.getSubscriber_id() + "," + currentSub.getSubscriber_name() + "," + borrowedBook.getName() + "," + extendedReturnDate + ";");
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