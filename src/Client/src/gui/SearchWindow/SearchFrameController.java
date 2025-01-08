package gui.SearchWindow;

import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.Book;

/**
 * Controller for the Search Window in the Library Management Tool.
 * 
 * <p>This class allows users to search for books based on name, description, 
 * and subject filters, and displays the results in a TableView. It also provides 
 * an option to navigate back to the Main Menu.</p>
 */
public class SearchFrameController extends BaseController implements Initializable {
	private SearchFrameController sfc;
   
	@FXML
    private Button btnExit;

    @FXML
    private Button btnSearchBooks;

    @FXML
    private TableView<Book> tableView;

    @FXML
    private TableColumn<Book, Integer> tableID;
    
    @FXML
    private TableColumn<Book, String> tableName;
    
    @FXML
    private TableColumn<Book, String> tableDescription;
    
    @FXML
    private TableColumn<Book, String> tableSubject;
    
    @FXML
    private TableColumn<Book, Integer> tableCopies;
    
    @FXML
    private TableColumn<Book, String> tableLocation;


    @FXML
    private TextField nameInput;

    @FXML
    private TextField descriptionInput;

    @FXML
    private ComboBox<String> subjectInput;

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
        tableID.setCellValueFactory(new PropertyValueFactory<Book, Integer>("id"));
        tableName.setCellValueFactory(new PropertyValueFactory<Book, String>("name"));
        tableDescription.setCellValueFactory(new PropertyValueFactory<Book, String>("description"));
        tableSubject.setCellValueFactory(new PropertyValueFactory<Book, String>("subject"));
        tableCopies.setCellValueFactory(new PropertyValueFactory<Book, Integer>("availableCopies"));
        tableLocation.setCellValueFactory(new PropertyValueFactory<Book, String>("location"));
        
        // Add subjects to the ComboBox
        subjectInput.getItems().add("");  // Empty string for clear selection
        subjectInput.getItems().add("Fantasy");
        subjectInput.getItems().add("Fiction");
        subjectInput.getItems().add("History");
        subjectInput.getItems().add("Romance");
        subjectInput.getItems().add("Epic");
        subjectInput.getItems().add("Drama");
        subjectInput.getItems().add("Historical");
        subjectInput.getItems().add("Mystery");
        subjectInput.getItems().add("Thriller");
        subjectInput.getItems().add("Dystopian");

        subjectInput.setValue(""); 

        // Initially populate the table when the page loads
        loadBooks();
    }

    /**
     * Loads books into the TableView by fetching them from the server.
     * If no books are available, displays a message in the console.
     */
    private void loadBooks() {
        // Send "GetBooks" request to the server to fetch the books
        ClientUI.chat.accept("GetBooks");

        // Ensure bookList is not empty
        if (ChatClient.bookList != null && !ChatClient.bookList.isEmpty()) {
            // Populate the TableView with all books initially
            Platform.runLater(() -> {
                tableView.getItems().clear();  // Clear any existing data
                tableView.getItems().addAll(ChatClient.bookList);  // Add all books to the table
            });
        } else {
            System.out.println("No books to display.");
        }
    }

    /**
     * Searches for books based on filters entered in the name, description, and subject fields.
     * Displays the filtered results in the TableView.
     *
     * @param event the event triggered by clicking the search button.
     * @throws Exception if an error occurs during the search.
     */
    public void Search(ActionEvent event) throws Exception {
        ClientUI.chat.accept("GetBooks");  // Get the latest books

        // Ensure bookList is not empty
        if (ChatClient.bookList != null && !ChatClient.bookList.isEmpty()) {
            // If all filter fields are empty, show all books
            if (nameInput.getText().isEmpty() && descriptionInput.getText().isEmpty() && (subjectInput.getValue() == null || subjectInput.getValue().toString().isEmpty())) {
                // No filtering, show all books
                Platform.runLater(() -> {
                    tableView.getItems().clear();  // Clear any existing data
                    tableView.getItems().addAll(ChatClient.bookList);  // Add all books to the table
                });
            } else {
                // Otherwise, filter books based on the input fields
                List<Book> filteredBooks = new ArrayList<>();

                for (Book book : ChatClient.bookList) {
                    boolean matches = true;

                    // Check for name filter
                    if (!nameInput.getText().isEmpty() && !book.getName().toLowerCase().contains(nameInput.getText().toLowerCase())) {
                        matches = false;
                    }

                    // Check for description filter
                    if (!descriptionInput.getText().isEmpty() && !book.getDescription().toLowerCase().contains(descriptionInput.getText().toLowerCase())) {
                        matches = false;
                    }

                    // Check for subject filter from ComboBox
                    if (subjectInput.getValue() != null && !subjectInput.getValue().toString().isEmpty() && !book.getSubject().toLowerCase().contains(subjectInput.getValue().toString().toLowerCase())) {
                        matches = false;
                    }

                    // If all filters match, add the book to the filtered list
                    if (matches) {
                        filteredBooks.add(book);
                    }
                }

                // Populate the TableView with the filtered books
                Platform.runLater(() -> {
                    tableView.getItems().clear();  // Clear any existing data
                    tableView.getItems().addAll(filteredBooks);  // Add the filtered books to the table
                });
            }
        } else {
            System.out.println("No books to display.");
        }
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
}
