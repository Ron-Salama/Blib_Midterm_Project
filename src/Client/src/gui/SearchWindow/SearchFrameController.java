package gui.SearchWindow;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import gui.baseController.BaseController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.Book;

/**
 * Controller for the Search Window in the Library Management Tool.
 *
 * <p>This class provides a graphical interface for searching books by name, 
 * description, and subject. Users can input their search criteria, and the 
 * results are displayed in a {@link TableView}. Additionally, it offers 
 * navigation back to either the main menu or a role-based view (Subscriber, 
 * Librarian, etc.), depending on the context.</p>
 * 
 * <p>Implements {@link Initializable} to set up UI components after the FXML 
 * is loaded.</p>
 */
public class SearchFrameController extends BaseController implements Initializable {

    /**
     * A flag used to differentiate the calling context for the SearchFrame.
     * Depending on the value, the "Back" button might be hidden or lead to different windows:
     * <ul>
     *   <li>"" (empty) - Hides the Back button</li>
     *   <li>"Subscriber" - Shows the Back button, navigates to Subscriber view</li>
     *   <li>"Librarian" - Shows the Back button, navigates to Librarian view</li>
     *   <li>"SubscriberBorrower" - Shows the Back button, navigates to Borrow Book window</li>
     * </ul>
     */
    public static String FlagForSearch = "";

    /**
     * Button used to navigate back to the main menu.
     */
    @FXML
    private Button btnExit;

    /**
     * Button to trigger the book search using the current filter inputs.
     */
    @FXML
    private Button btnSearchBooks;

    /**
     * Button to navigate back based on the {@link #FlagForSearch} context.
     */
    @FXML
    private Button btnBackF;

    /**
     * The table that displays the search results for books.
     */
    @FXML
    private TableView<Book> tableView;

    /**
     * Table column to display the ISBN of a book.
     */
    @FXML
    private TableColumn<Book, String> tableISBN;

    /**
     * Table column to display the name/title of a book.
     */
    @FXML
    private TableColumn<Book, String> tableName;

    /**
     * Table column to display the description of a book.
     */
    @FXML
    private TableColumn<Book, String> tableDescription;

    /**
     * Table column to display the subject/category of a book.
     */
    @FXML
    private TableColumn<Book, String> tableSubject;

    /**
     * Table column to display the available copies for a book.
     */
    @FXML
    private TableColumn<Book, Integer> tableCopies;

    /**
     * Table column to display the shelf location for a book.
     */
    @FXML
    private TableColumn<Book, String> tableLocation;

    /**
     * Table column to display the closest return date, if the book is currently not available.
     */
    @FXML
    private TableColumn<Book, String> tableClosestReturnDate;

    /**
     * Text field for entering a filter on the book name.
     */
    @FXML
    private TextField nameInput;

    /**
     * Text field for entering a filter on the book description.
     */
    @FXML
    private TextField descriptionInput;

    /**
     * ComboBox for selecting a filter on the subject/category of the book.
     */
    @FXML
    private ComboBox<String> subjectInput;

    /**
     * Initializes the Search Window by setting up the TableView columns, ComboBox,
     * and populating the table with all available books on page load.
     *
     * @param url            the location used to resolve relative paths, or null if not known.
     * @param resourceBundle the resource bundle containing localized objects, or null if not used.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize TableView columns
        tableISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        tableISBN.setCellFactory(column -> new TableCell<Book, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Display the ISBN string; custom formatting could be done here if needed
                    setText(item);
                }
            }
        });

        tableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tableSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        tableCopies.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        tableLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        tableClosestReturnDate.setCellValueFactory(new PropertyValueFactory<>("closestReturnDate"));

        // Populate the subjects in the ComboBox
        addSubjectsToComboBox();

        // Show or hide the Back button based on FlagForSearch
        if (FlagForSearch == "") {
            btnBackF.setVisible(false);
        } else if (FlagForSearch == "Subscriber") {
            btnBackF.setVisible(true);
        } else if (FlagForSearch == "Librarian") {
            btnBackF.setVisible(true);
        } else if (FlagForSearch == "SubscriberBorrower") {
            btnBackF.setVisible(true);
        }

        // Fetch all books from the server and load them into the table
        ClientUI.chat.accept("GetBooks:");
        waitForServerResponse();
        loadBooks();
    }

    /**
     * Loads all books from the {@link ChatClient#bookList} into the TableView.
     * If a book has copies available, sets the closestReturnDate to "Available".
     * Otherwise, requests the earliest return date from the server.
     */
    private void loadBooks() {
        if (ChatClient.bookList != null && !ChatClient.bookList.isEmpty()) {
            tableView.getItems().clear();

            for (Book book : ChatClient.bookList) {
                if (book.getAvailableCopies() > 0) {
                    book.setClosestReturnDate("Available");
                } else {
                    ClientUI.chat.accept("FetchClosestReturnDate:" + book.getISBN());
                    waitForServerResponse();
                    String currentBookClosestReturnDate = ChatClient.closestReturnDate;
                    book.setClosestReturnDate(currentBookClosestReturnDate);
                }
            }
            tableView.getItems().addAll(ChatClient.bookList);
        } else {
            System.out.println("No books to display.");
        }
    }

    /**
     * Searches for books using the inputs in {@link #nameInput}, {@link #descriptionInput},
     * and {@link #subjectInput}. Filters the main list of books from the server and
     * updates the TableView accordingly.
     *
     * @param event the {@link ActionEvent} triggered by clicking the "Search" button.
     * @throws Exception if an error occurs during the search process.
     */
    public void Search(ActionEvent event) throws Exception {
        // Refresh the book list from the server
        ClientUI.chat.accept("GetBooks:");
        waitForServerResponse();

        if (ChatClient.bookList != null && !ChatClient.bookList.isEmpty()) {
            // Check if all search fields are empty
            boolean isNameEmpty = nameInput.getText().isEmpty();
            boolean isDescEmpty = descriptionInput.getText().isEmpty();
            boolean isSubjectEmpty = (subjectInput.getValue() == null || subjectInput.getValue().isEmpty());

            if (isNameEmpty && isDescEmpty && isSubjectEmpty) {
                // No filters - display all books
                for (Book book : ChatClient.bookList) {
                    if (book.getAvailableCopies() > 0) {
                        book.setClosestReturnDate("Available");
                    } else if (book.getClosestReturnDate() == null || "Fetching...".equals(book.getClosestReturnDate())) {
                        ClientUI.chat.accept("FetchClosestReturnDate:" + book.getISBN());
                        waitForServerResponse();
                        String currentBookClosestReturnDate = ChatClient.closestReturnDate;
                        book.setClosestReturnDate(currentBookClosestReturnDate != null ? currentBookClosestReturnDate : "Unavailable");
                    }
                }

                // Update UI
                Platform.runLater(() -> {
                    tableView.getItems().clear();
                    tableView.getItems().addAll(ChatClient.bookList);
                });
            } else {
                // Apply filters to the book list
                List<Book> filteredBooks = new ArrayList<>();

                for (Book book : ChatClient.bookList) {
                    boolean matches = true;

                    // Check name filter
                    if (!isNameEmpty 
                            && !book.getName().toLowerCase().contains(nameInput.getText().toLowerCase())) {
                        matches = false;
                    }

                    // Check description filter
                    if (!isDescEmpty 
                            && !book.getDescription().toLowerCase().contains(descriptionInput.getText().toLowerCase())) {
                        matches = false;
                    }

                    // Check subject filter
                    if (!isSubjectEmpty 
                            && !book.getSubject().toLowerCase().contains(subjectInput.getValue().toLowerCase())) {
                        matches = false;
                    }

                    // If still matches, determine availability or closest return date
                    if (matches) {
                        if (book.getAvailableCopies() > 0) {
                            book.setClosestReturnDate("Available");
                        } else if (book.getAvailableCopies() == 0 
                                && (book.getClosestReturnDate() == null 
                                    || "Fetching...".equals(book.getClosestReturnDate()))) {
                            ClientUI.chat.accept("FetchClosestReturnDate:" + book.getISBN());
                            waitForServerResponse();
                            String currentBookClosestReturnDate = ChatClient.closestReturnDate;
                            book.setClosestReturnDate(currentBookClosestReturnDate != null ? currentBookClosestReturnDate : "Unavailable");
                        }
                        filteredBooks.add(book);
                    }
                }

                // Update UI with filtered results
                Platform.runLater(() -> {
                    tableView.getItems().clear();
                    tableView.getItems().addAll(filteredBooks);
                });
            }
        } else {
            System.out.println("No books to display.");
        }
    }

    /**
     * Navigates back to the Main Menu and resets {@link #FlagForSearch} to an empty string.
     *
     * @param event the {@link ActionEvent} triggered by clicking the "Exit" button.
     */
    public void getExitBtn(ActionEvent event) {
        SearchFrameController.FlagForSearch = "";
        openWindow(event,
                "/gui/MainMenu/MainMenuFrame.fxml",
                "/gui/MainMenu/MainMenuFrame.css",
                "MainMenu");
    }

    /**
     * Navigates back from the search window to the appropriate view:
     * <ul>
     *     <li>If {@code FlagForSearch} is "Subscriber", goes to Subscriber View.</li>
     *     <li>If {@code FlagForSearch} is "Librarian", goes to Librarian View.</li>
     *     <li>If {@code FlagForSearch} is "SubscriberBorrower", goes to Borrow Book Window.</li>
     * </ul>
     * Resets {@link #FlagForSearch} to an empty string after navigation.
     *
     * @param event the {@link ActionEvent} triggered by clicking the "Back" button.
     */
    public void backFromUser(ActionEvent event) {
        if (FlagForSearch == "Subscriber") {
            openWindow(event,
                    "/gui/SubscriberWindow/SubscriberWindow.fxml",
                    "/gui/SubscriberWindow/SubscriberWindow.css",
                    "Subscriber View");
            FlagForSearch = "";
        } else if (FlagForSearch == "Librarian") {
            openWindow(event,
                    "/gui/LibrarianWindow/LibrarianFrame.fxml",
                    "/gui/LibrarianWindow/LibrarianFrame.css",
                    "Librarian View");
            FlagForSearch = "";
        } else if (FlagForSearch == "SubscriberBorrower") {
            openWindow(event,
                    "/gui/BorrowBookWindow/BorrowBookFrame.fxml",
                    "/gui/BorrowBookWindow/BorrowBookFrame.css",
                    "Borrow a Book");
            FlagForSearch = "";
        }
    }

    /**
     * Populates the {@link #subjectInput} ComboBox with a predefined list of subject categories.
     * <p>Also sets the default value to an empty string for a "no-selection" state.</p>
     */
    public void addSubjectsToComboBox() {
        ArrayList<String> subjects = new ArrayList<>(Arrays.asList(
                "",           // Empty string for clear selection
                "Fantasy",
                "Fiction",
                "History",
                "Romance",
                "Epic",
                "Drama",
                "Historical",
                "Mystery",
                "Thriller",
                "Dystopian"
        ));

        subjectInput.getItems().addAll(subjects);
        subjectInput.setValue(""); 
    }
}
