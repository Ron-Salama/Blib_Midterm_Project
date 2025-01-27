package gui.LibrarianBorrowedBooksReportWindow;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.ChatClient;
import client.ClientUI;
import gui.baseController.BaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import logic.BorrowedBook;
import logic.ClientTimeDiffController;

/**
 * Controller class for the Librarian Borrowed Books Report window.
 *
 * <p>This class manages the interactions within the librarian's borrowed books report window,
 * including displaying the borrowed books data in charts and tables, filtering books by month,
 * and showing pie charts with average daily borrowing statistics.</p>
 */
public class LibrarianBorrowedBooksReportController extends BaseController {

	/** The BarChart for visualizing book statistics (borrowed, early returns, late returns). */
    @FXML
    private BarChart<String, Number> barChart;
    
    /** The X-Axis of the BarChart, representing categories (e.g., book names). */
    @FXML
    private CategoryAxis barXAxis;
    
    /** The Y-Axis of the BarChart, representing numeric values (e.g., counts of events). */
    @FXML
    private NumberAxis barYAxis;
    
    /** A VBox container for holding the PieChart that displays the average data. */
    @FXML
    private VBox avgPieChartContainer;
    
    /** A ComboBox for filtering the book data based on selected criteria (e.g., book ID or name). */
    @FXML
    private ComboBox<String> bookFilterComboBox;
    
    /** A Label displaying the "Most Borrowed" book statistics. */
    @FXML
    private Label MPB;
    
    /** A Label displaying the "Least Used" book statistics. */
    @FXML
    private Label LUB;
    
    /** A Label displaying the "Early Returns" statistics for books. */
    @FXML
    private Label ERB;
    
    /** A Label displaying the "Least Used" book statistics (alternative). */
    @FXML
    private Label LUB1;
    
    /** A Label displaying the "Early Returns" statistics (alternative). */
    @FXML
    private Label ERB1;
    
    /** A ComboBox for selecting the month to display statistics. */
    @FXML
    private ComboBox<String> monthComboBox;
    
    /** A PieChart for displaying the overall statistics (e.g., distribution of borrowings). */
    @FXML
    private PieChart pieChart;
    
    /** A PieChart displaying the daily average data for the book statistics. */
    @FXML
    private PieChart avgPieChart;

    /** A TableView displaying a list of book data (ID, name, borrowed count, etc.). */
    @FXML
    private TableView<BookData> bookDataTable;
    
    /** A TableColumn displaying the book ID. */
    @FXML
    private TableColumn<BookData, String> bookIdColumn;
    
    /** A TableColumn displaying the book name. */
    @FXML
    private TableColumn<BookData, String> bookNameColumn;
    
    /** A TableColumn displaying the number of times the book has been borrowed. */
    @FXML
    private TableColumn<BookData, Integer> timesBorrowedColumn;
    
    /** A TableColumn displaying the number of early returns for each book. */
    @FXML
    private TableColumn<BookData, Integer> earlyReturnsColumn;
    
    /** A TableColumn displaying the number of late returns for each book. */
    @FXML
    private TableColumn<BookData, Integer> lateReturnsColumn;
    
    /** A TableColumn displaying the number of lost books. */
    @FXML
    private TableColumn<BookData, Integer> lostColumn;
    
    /** A reference to the ClientTimeDiffController for managing time-based operations. */
    private ClientTimeDiffController clock = ChatClient.clock;
    
    /** A Map that holds book data, where the key is the book ID and the value is the corresponding BookData object. */
    private Map<String, BookData> bookDataMap = new HashMap<>();

    /**
     * Initializes the UI components for the borrowed books report window,
     * including setting up the ComboBoxes, TableView, and PieCharts.
     *
     * @throws InterruptedException if the initialization process is interrupted.
     */
    public void initialize() throws InterruptedException {
        // Set up the month ComboBox
        ObservableList<String> months = FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        monthComboBox.setItems(months);
        monthComboBox.setValue("January"); // Set default value

        // Set up the book filter ComboBox with "All" as the default option
        ObservableList<String> bookFilterList = FXCollections.observableArrayList();
        bookFilterList.add("All"); // Add "All" option
        for (BookData bookData : bookDataMap.values()) {
            bookFilterList.add(bookData.getBookId() + " - " + bookData.getBookName());
        }
        bookFilterComboBox.setItems(bookFilterList);
        bookFilterComboBox.setValue("All"); // Set default value to "All"

        // Set up TableView columns
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        timesBorrowedColumn.setCellValueFactory(new PropertyValueFactory<>("timesBorrowed"));

        // Change the text to "On Time" and "Running Late"
        earlyReturnsColumn.setText("On Time");
        lateReturnsColumn.setText("Running Late");

        // Bind the columns
        earlyReturnsColumn.setCellValueFactory(new PropertyValueFactory<>("lateReturns"));
        lateReturnsColumn.setCellValueFactory(new PropertyValueFactory<>("earlyReturns"));

        // Populate the bar chart and table with initial data
        populateBarChart();
        populateTable();

        // Populate the PieChart with the latest borrowed/returned data
        populatePieChart();

        // Add listener to ComboBox to update chart and table based on selected month
        monthComboBox.setOnAction(event -> {
            try {
                populateBarChart(); // Update bar chart when month is selected
                populateTable();    // Update table when month is selected
                populatePieChart(); // Update pie chart when month is selected
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Add listener to the book filter ComboBox to filter the table
        bookFilterComboBox.setOnAction(event -> {
            try {
                filterBooks();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Populates the PieChart with the latest borrowed/returned data and
     * calculates the average borrows and returns per day for the selected month.
     * 
     * @throws InterruptedException if an error occurs during the data retrieval.
     */
    private void populatePieChart() throws InterruptedException {
        // Clear any previous data in the charts
        pieChart.getData().clear();
        avgPieChart.getData().clear();

        // Fetch data
        List<String> frozenData = getAllFrozenData();

        // Get the latest day’s data for the main PieChart
        String latestRecord = getLatestRecord(frozenData);
        if (latestRecord != null) {
            String[] recordFields = latestRecord.split(",");
            if (recordFields.length >= 5) {
                String dateStr = recordFields[0];
                int borrowedCount = Integer.parseInt(recordFields[3]);
                int returnedCount = Integer.parseInt(recordFields[4]);

                // Create the main pie chart sections
                PieChart.Data borrowedData = new PieChart.Data("Borrowed: " + borrowedCount, borrowedCount);
                PieChart.Data returnedData = new PieChart.Data("Late: " + returnedCount, returnedCount);

                // Add data to the main PieChart
                pieChart.getData().addAll(borrowedData, returnedData);
            }
        }

        // Calculate average borrows and returns per day for the selected month
        double avgBorrowed = 0;
        double avgReturned = 0;
        int totalDays = 0;
        int totalBorrowed = 0;
        int totalReturned = 0;

        String selectedMonth = monthComboBox.getValue();
        Month month = Month.valueOf(selectedMonth.toUpperCase()); // Convert to Month enum

        // Iterate over the data to accumulate total borrows and returns for each day in the selected month
        for (String record : frozenData) {
            String[] recordFields = record.split(",");
            if (recordFields.length >= 5) { // Ensure the record has the required fields
                String dateStr = recordFields[0];
                int borrowedCount = Integer.parseInt(recordFields[3]);
                int returnedCount = Integer.parseInt(recordFields[4]);

                // Parse the date
                LocalDate date = LocalDate.parse(dateStr);
                Month recordMonth = date.getMonth(); // Extract the month from the date

                // Only calculate for the selected month
                if (recordMonth == month) {
                    totalDays++;
                    totalBorrowed += borrowedCount;
                    totalReturned += returnedCount;
                }
            }
        }

        if (totalDays > 0) {
            avgBorrowed = (double) totalBorrowed / totalDays;
            avgReturned = (double) totalReturned / totalDays;
        }

        // Set a threshold to avoid issues with very small values
        double threshold = 0.01;
        avgBorrowed = Math.max(avgBorrowed, threshold);
        avgReturned = Math.max(avgReturned, threshold);

        // Create the pie chart sections for the daily average data with formatted values
        PieChart.Data avgBorrowedData = new PieChart.Data(String.format("Avg Borrowed: %.2f", avgBorrowed), avgBorrowed);
        PieChart.Data avgReturnedData = new PieChart.Data(String.format("Avg Late: %.2f", avgReturned), avgReturned);

        // Explicitly set the labels
        avgBorrowedData.nameProperty().set(String.format("Avg Borrowed: %.2f", avgBorrowed));
        avgReturnedData.nameProperty().set(String.format("Avg Late: %.2f", avgReturned));

        // Add data to the new PieChart (avgPieChart)
        avgPieChart.getData().addAll(avgBorrowedData, avgReturnedData);

        // Make sure the legend is visible and positioned
        avgPieChart.setLegendVisible(true);

        // Force layout update
        avgPieChart.layout();
    }

    /**
     * Retrieves the latest frozen subscriber record from a list of frozen data.
     *
     * @param frozenData A list of frozen subscriber records as strings, where each string contains the date and other relevant details.
     * @return The latest frozen record as a string, or null if no record is found.
     */
    private String getLatestRecord(List<String> frozenData) {
        String latestRecord = null;
        LocalDate latestDate = LocalDate.MIN;

        for (String record : frozenData) {
            String[] recordFields = record.split(",");
            if (recordFields.length >= 5) {
                String dateStr = recordFields[0];
                LocalDate recordDate = LocalDate.parse(dateStr);

                // Find the latest record
                if (recordDate.isAfter(latestDate)) {
                    latestDate = recordDate;
                    latestRecord = record;
                }
            }
        }
        return latestRecord;
    }
    
    /**
     * Populates the bar chart with borrowed and returned book data filtered by the selected month.
     *
     * @throws InterruptedException If the thread is interrupted while waiting for the data or populating the chart.
     */
    private void populateBarChart() throws InterruptedException {
        // Clear any previous data in the chart
        barChart.getData().clear();

        // Fetch data
        List<String> frozenData = getAllFrozenData();

        // Get the selected month from the ComboBox
        String selectedMonth = monthComboBox.getValue();
        Month month = Month.valueOf(selectedMonth.toUpperCase()); // Convert to Month enum

        // Create series for borrowed and returned books
        XYChart.Series<String, Number> borrowedSeries = new XYChart.Series<>();
        borrowedSeries.setName("Borrowed");

        XYChart.Series<String, Number> returnedSeries = new XYChart.Series<>();
        returnedSeries.setName("Late");

        // Add data to the series, filtering by selected month
        for (String record : frozenData) {
            String[] recordFields = record.split(",");
            if (recordFields.length >= 5) { // Ensure the record has the required fields
                String dateStr = recordFields[0];
                int borrowedCount = Integer.parseInt(recordFields[3]);
                int returnedCount = Integer.parseInt(recordFields[4]);

                // Parse the date
                LocalDate date = LocalDate.parse(dateStr);
                Month recordMonth = date.getMonth(); // Extract the month from the date

                // Filter by the selected month
                if (recordMonth == month) {
                    String dateFormatted = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")); // Convert date for display
                    borrowedSeries.getData().add(new XYChart.Data<>(dateFormatted, borrowedCount));
                    returnedSeries.getData().add(new XYChart.Data<>(dateFormatted, returnedCount));
                }
            }
        }

        // Add the series to the bar chart
        barChart.getData().addAll(borrowedSeries, returnedSeries);
    }

    /**
     * Populates the book data table with information about borrowed books, including early and late returns, and updates relevant statistics.
     * 
     * @throws InterruptedException If the thread is interrupted while fetching data or processing the books.
     */
    private void populateTable() throws InterruptedException {
        // Clear previous data in the table and the map
        bookDataTable.getItems().clear();
        bookDataMap.clear();  // Clear the map to reset the data

        // Request the borrowed books information
        ClientUI.chat.accept("GetAllBorrowedBooksInfo:");

        waitForServerResponse();

        // Fetch the borrowed book information from ChatClient.BorrowedBookInfoForReports
        List<BorrowedBook> borrowedBooks = ChatClient.BorrowedBookInfoForReports;

        // Get the current time as a string (DD-MM-YYYY)
        String currentTimeString = clock.timeNow(); // Assuming this is the string "DD-MM-YYYY"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate currentDate = LocalDate.parse(currentTimeString, formatter); // Parse the current date

        // Variables to track the MPB, ERB, LUB, ERB1, and LUB1
        List<BorrowedBook> mostPopularBooks = new ArrayList<>();
        int maxBorrowCount = 0;

        BorrowedBook earliestReturnedBook = null;
        BorrowedBook longestUnreturnedBook = null;
        BorrowedBook earliestReturnedBeforeToday = null;  // ERB1
        BorrowedBook longestUnreturnedBeforeToday = null;  // LUB1

        // Map to track how many times each book has been borrowed
        Map<String, Integer> borrowCountMap = new HashMap<>();

        // Iterate through each BorrowedBook in the list
        for (BorrowedBook borrowedBook : borrowedBooks) {
            try {
                // Get the relevant information from BorrowedBook
                String bookId = borrowedBook.getISBN();  // ISBN
                String bookName = borrowedBook.getName();  // Book Name

                // Fetch the return date using the getter method
                String returnTimeString = borrowedBook.getReturnDate();  // Return Time as String

                // Convert the return time to a LocalDate object
                LocalDate returnDate = LocalDate.parse(returnTimeString, formatter);

                // Initialize counts for early returns, late returns, and lost
                int earlyReturns = 0;
                int lateReturns = 0;

                // Check if the book is already in the map for times borrowed
                borrowCountMap.put(bookId, borrowCountMap.getOrDefault(bookId, 0) + 1);

                // Calculate early or late returns
                if (returnDate.isAfter(currentDate)) {
                    lateReturns = 1;  // It's a late return
                } else {
                    earlyReturns = 1;  // It's an early return
                }

                // Add the book to the bookDataMap (or update if it already exists)
                if (bookDataMap.containsKey(bookId)) {
                    BookData existingBookData = bookDataMap.get(bookId);
                    existingBookData.setEarlyReturns(existingBookData.getEarlyReturns() + earlyReturns);
                    existingBookData.setLateReturns(existingBookData.getLateReturns() + lateReturns);
                    existingBookData.setTimesBorrowed(borrowCountMap.get(bookId));
                } else {
                    // Create BookData and add it to the map
                    BookData newBookData = new BookData(bookId, bookName, borrowCountMap.get(bookId), earlyReturns, lateReturns);
                    bookDataMap.put(bookId, newBookData);
                }

                // Update the Most Popular Books (MPB)
                int borrowCount = borrowCountMap.get(bookId);
                if (borrowCount > maxBorrowCount) {
                    // Found a new book with more borrowings, reset the list
                    maxBorrowCount = borrowCount;
                    mostPopularBooks.clear();  // Clear the previous list
                    mostPopularBooks.add(borrowedBook);  // Add the current book
                } else if (borrowCount == maxBorrowCount) {
                    // Found another book with the same borrow count, add it to the list
                    mostPopularBooks.add(borrowedBook);
                }

                // Update the ERB (Earliest Returned Book)
                if (returnDate.isAfter(currentDate)) {
                    if (earliestReturnedBook == null || returnDate.isBefore(LocalDate.parse(earliestReturnedBook.getReturnDate(), formatter))) {
                        earliestReturnedBook = borrowedBook;
                    }
                }
                // Update the LUB (Longest Unreturned Book)
                if (longestUnreturnedBook == null || returnDate.isAfter(LocalDate.parse(longestUnreturnedBook.getReturnDate(), formatter))) {
                    longestUnreturnedBook = borrowedBook;
                }

                // Update the ERB1 (Earliest Returned Book Before Today)
                if (returnDate.isBefore(currentDate)) {
                    if (earliestReturnedBeforeToday == null || returnDate.isAfter(LocalDate.parse(earliestReturnedBeforeToday.getReturnDate(), formatter))) {
                        earliestReturnedBeforeToday = borrowedBook;
                    }
                }

                // Update the LUB1 (Longest Unreturned Book Before Today)
                if (returnDate.isBefore(currentDate)) {
                    if (longestUnreturnedBeforeToday == null || returnDate.isBefore(LocalDate.parse(longestUnreturnedBeforeToday.getReturnDate(), formatter))) {
                        longestUnreturnedBeforeToday = borrowedBook;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error processing borrowed book: " + e.getMessage());
            }
        }

        // Convert the map values (BookData) into a list and set it to the TableView
        ObservableList<BookData> bookDataList = FXCollections.observableArrayList(bookDataMap.values());
        bookDataTable.setItems(bookDataList);

        // Populate the ComboBox with book ID and name for filtering
        ObservableList<String> bookFilterList = FXCollections.observableArrayList();
        bookFilterList.add("All");
        for (BookData bookData : bookDataMap.values()) {
            bookFilterList.add(bookData.getBookId() + " - " + bookData.getBookName());
        }

        bookFilterComboBox.setItems(bookFilterList);

        // Set the MPB text to show the books with the highest borrow count
        if (!mostPopularBooks.isEmpty()) {
            StringBuilder mpbText = new StringBuilder();
            for (BorrowedBook book : mostPopularBooks) {
                mpbText.append(book.getName())
                       .append(" (")
                       .append(borrowCountMap.get(book.getISBN()))
                       .append(" times), ");
            }
            // Remove the trailing comma and space
            MPB.setText(mpbText.toString().substring(0, mpbText.length() - 2));
        }

        // Set the ERB, LUB, ERB1, and LUB1 labels
        if (earliestReturnedBook != null) {
            ERB.setText("" + earliestReturnedBook.getName() + " (Return date: " + earliestReturnedBook.getReturnDate() + ")");
        }
        if (longestUnreturnedBook != null) {
            LUB.setText("" + longestUnreturnedBook.getName() + " (Return date: " + longestUnreturnedBook.getReturnDate() + ")");
        }

        // Set the ERB1 and LUB1 labels
        if (earliestReturnedBeforeToday != null) {
            ERB1.setText("" + earliestReturnedBeforeToday.getName() + " (Return date: " + earliestReturnedBeforeToday.getReturnDate() + ")");
        }
        if (longestUnreturnedBeforeToday != null) {
            LUB1.setText("" + longestUnreturnedBeforeToday.getName() + " (Return date: " + longestUnreturnedBeforeToday.getReturnDate() + ")");
        }
    }

    /**
     * Filters the books shown in the book data table based on the selected filter from the combo box.
     * If no filter is selected or if "All" is chosen, all books are displayed.
     * 
     * @throws InterruptedException If the thread is interrupted while fetching or processing data.
     */
    private void filterBooks() throws InterruptedException {
    	populateTable();
        String selectedFilter = bookFilterComboBox.getValue();
        
        // If no selection is made, return to show all data
        if (selectedFilter == null || selectedFilter.isEmpty() || selectedFilter == "All") {
            // Reset the TableView to show all data
            bookDataTable.setItems(FXCollections.observableArrayList(bookDataTable.getItems()));
            return;
        }

        // Clear the previous filtered data in the TableView
        ObservableList<BookData> filteredList = FXCollections.observableArrayList();
        
        for (BookData bookData : bookDataTable.getItems()) {
            String filterText = bookData.getBookId() + " - " + bookData.getBookName();
            if (filterText.equals(selectedFilter)) {
                filteredList.add(bookData);
            }
        }
        
        // Update the table with the filtered list
        if (filteredList.isEmpty()) {
            // If no match is found, clear the table or display a message if necessary
            bookDataTable.setItems(FXCollections.observableArrayList());
        } else {
            bookDataTable.setItems(filteredList);
        }
        
    }
    
    /**
     * Fetches all frozen subscriber data for reports.
     * 
     * @return A list of strings containing the frozen subscriber data.
     * @throws InterruptedException If the thread is interrupted while fetching data.
     */
    private List<String> getAllFrozenData() throws InterruptedException {
        List<String> frozenData = new ArrayList<>();

        // Request frozen data from the server
        
        ClientUI.chat.accept("FetchAllFrozenInformationForReports:");
        waitForServerResponse();
        
        if (ChatClient.allFrozenDataForReport != null) {
            frozenData.addAll(ChatClient.allFrozenDataForReport);
        } else {
            System.err.println("Error: No frozen subscriber data received.");
        }

        return frozenData;
    }

    /**
     * Navigates back to the previous window in the application.
     *
     * @param event The action event triggering the back navigation.
     * @throws Exception If an error occurs while opening the previous window.
     */
    public void back(ActionEvent event) throws Exception{
        openWindow(event,
                "/gui/ReportsWindow/ReportsWindow.fxml",
                "/gui/ReportsWindow/ReportsWindow.css",
                "Subscriber Status Report");
    }

    /**
     * A class representing the data of a borrowed book, including its ID, name, borrow count, early returns, late returns, and lost status.
     */
    public class BookData {
        private String bookId;
        private String bookName;
        private int timesBorrowed;
        private int earlyReturns;
        private int lateReturns;
        private int lost;

        /**
         * Constructs a new BookData object with the specified details.
         * 
         * @param bookId the unique identifier (ISBN) of the book.
         * @param bookName the name of the book.
         * @param timesBorrowed the number of times the book has been borrowed.
         * @param earlyReturns the number of early returns for the book.
         * @param lateReturns the number of late returns for the book.
         */
        public BookData(String bookId, String bookName, int timesBorrowed, int earlyReturns, int lateReturns) {
            this.bookId = bookId;
            this.bookName = bookName;
            this.timesBorrowed = timesBorrowed;
            this.earlyReturns = earlyReturns;
            this.lateReturns = lateReturns;
        }

        /**
         * Gets the book's unique identifier (ISBN).
         * 
         * @return the book's unique identifier (ISBN).
         */
        public String getBookId() {
            return bookId;
        }

        /**
         * Sets the book's unique identifier (ISBN).
         * 
         * @param bookId the book's unique identifier (ISBN).
         */
        public void setBookId(String bookId) {
            this.bookId = bookId;
        }

        /**
         * Gets the name of the book.
         * 
         * @return the name of the book.
         */
        public String getBookName() {
            return bookName;
        }

        /**
         * Sets the name of the book.
         * 
         * @param bookName the name of the book.
         */
        public void setBookName(String bookName) {
            this.bookName = bookName;
        }

        /**
         * Gets the number of times the book has been borrowed.
         * 
         * @return the number of times the book has been borrowed.
         */
        public int getTimesBorrowed() {
            return timesBorrowed;
        }

        /**
         * Sets the number of times the book has been borrowed.
         * 
         * @param timesBorrowed the number of times the book has been borrowed.
         */
        public void setTimesBorrowed(int timesBorrowed) {
            this.timesBorrowed = timesBorrowed;
        }

        /**
         * Gets the number of early returns for the book.
         * 
         * @return the number of early returns for the book.
         */
        public int getEarlyReturns() {
            return earlyReturns;
        }

        /**
         * Sets the number of early returns for the book.
         * 
         * @param earlyReturns the number of early returns for the book.
         */
        public void setEarlyReturns(int earlyReturns) {
            this.earlyReturns = earlyReturns;
        }

        /**
         * Gets the number of late returns for the book.
         * 
         * @return the number of late returns for the book.
         */
        public int getLateReturns() {
            return lateReturns;
        }

        /**
         * Sets the number of late returns for the book.
         * 
         * @param lateReturns the number of late returns for the book.
         */
        public void setLateReturns(int lateReturns) {
            this.lateReturns = lateReturns;
        }
    }
}
