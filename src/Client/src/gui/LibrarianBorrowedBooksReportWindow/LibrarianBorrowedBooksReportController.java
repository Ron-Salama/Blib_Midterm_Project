package gui.LibrarianBorrowedBooksReportWindow;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class LibrarianBorrowedBooksReportController extends BaseController {

    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private CategoryAxis barXAxis;
    @FXML
    private NumberAxis barYAxis;
    @FXML
    private VBox avgPieChartContainer; // Add a VBox container in your FXML

    @FXML
    private ComboBox<String> monthComboBox;
    @FXML
    private PieChart pieChart;
    @FXML
    private PieChart avgPieChart;  // Add a new PieChart for the daily average data

    @FXML
    private TableView<BookData> bookDataTable;
    @FXML
    private TableColumn<BookData, String> bookIdColumn;
    @FXML
    private TableColumn<BookData, String> bookNameColumn;
    @FXML
    private TableColumn<BookData, Integer> timesBorrowedColumn;
    @FXML
    private TableColumn<BookData, Integer> earlyReturnsColumn;
    @FXML
    private TableColumn<BookData, Integer> lateReturnsColumn;
    @FXML
    private TableColumn<BookData, Integer> lostColumn;

    public void initialize() throws InterruptedException {
        // Set up the month ComboBox
        ObservableList<String> months = FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        monthComboBox.setItems(months);
        monthComboBox.setValue("January"); // Set default value

        // Set up TableView columns
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        timesBorrowedColumn.setCellValueFactory(new PropertyValueFactory<>("timesBorrowed"));
        earlyReturnsColumn.setCellValueFactory(new PropertyValueFactory<>("earlyReturns"));
        lateReturnsColumn.setCellValueFactory(new PropertyValueFactory<>("lateReturns"));
        lostColumn.setCellValueFactory(new PropertyValueFactory<>("lost"));

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
    }
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
                PieChart.Data borrowedData = new PieChart.Data("Borrowed", borrowedCount);
                PieChart.Data returnedData = new PieChart.Data("Late", returnedCount);

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

        // Create the pie chart sections for the daily average data
        PieChart.Data avgBorrowedData = new PieChart.Data("Avg Borrowed", avgBorrowed);
        PieChart.Data avgReturnedData = new PieChart.Data("Avg Late", avgReturned);

        // Add data to the new PieChart (avgPieChart)
        avgPieChart.getData().addAll(avgBorrowedData, avgReturnedData);

        // Make sure the legend is visible and positioned
        avgPieChart.setLegendVisible(true);

    }

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

    private void populateTable() throws InterruptedException {
        // Clear previous data in the table
    	
        bookDataTable.getItems().clear();
/*
        // Fetch data
        List<String> frozenData = getAllFrozenData();

        ObservableList<BookData> bookDataList = FXCollections.observableArrayList();

        // Filter and collect data to populate the table
        for (String record : frozenData) {
            String[] recordFields = record.split(",");
            if (recordFields.length >= 6) { // Ensure the record has the required fields
                String bookId = recordFields[1];
                String bookName = recordFields[2];
                int timesBorrowed = Integer.parseInt(recordFields[3]);
                int earlyReturns = Integer.parseInt(recordFields[4]);
                int lateReturns = Integer.parseInt(recordFields[5]);
                int lost = Integer.parseInt(recordFields[6]);

                bookDataList.add(new BookData(bookId, bookName, timesBorrowed, earlyReturns, lateReturns, lost));
            }
        }

        // Set the data to the TableView
        bookDataTable.setItems(bookDataList);
*/
            // Clear previous data in the table
            bookDataTable.getItems().clear();

            // Create a list of hardcoded book data
            ObservableList<BookData> bookDataList = FXCollections.observableArrayList(
                new BookData("B001", "The Great Gatsby", 15, 2, 5, 1),
                new BookData("B002", "1984", 20, 1, 7, 2),
                new BookData("B003", "To Kill a Mockingbird", 30, 4, 8, 0),
                new BookData("B004", "The Catcher in the Rye", 25, 3, 6, 3),
                new BookData("B005", "Pride and Prejudice", 18, 2, 4, 0),
                new BookData("B006", "Moby-Dick", 22, 1, 5, 1),
                new BookData("B007", "War and Peace", 28, 5, 9, 2)
            );

            // Set the data to the TableView
            bookDataTable.setItems(bookDataList);
        }

    

    private List<String> getAllFrozenData() throws InterruptedException {
        List<String> frozenData = new ArrayList<>();

        // Request frozen data from the server
        ClientUI.chat.accept("FetchAllFrozenInformationForReports:");
        waitForServerResponse();

        if (ChatClient.allFrozenDataForReport != null) {
            frozenData.addAll(ChatClient.allFrozenDataForReport);
        } else {
            System.out.println("Error: No frozen subscriber data received.");
        }

        return frozenData;
    }

    public void back(ActionEvent event) throws Exception{
        openWindow(event,
                "/gui/ReportsWindow/ReportsWindow.fxml",
                "/gui/ReportsWindow/ReportsWindow.css",
                "Subscriber Status Report");
    }

    public static class BookData {
        private String bookId;
        private String bookName;
        private int timesBorrowed;
        private int earlyReturns;
        private int lateReturns;
        private int lost;

        public BookData(String bookId, String bookName, int timesBorrowed, int earlyReturns, int lateReturns, int lost) {
            this.bookId = bookId;
            this.bookName = bookName;
            this.timesBorrowed = timesBorrowed;
            this.earlyReturns = earlyReturns;
            this.lateReturns = lateReturns;
            this.lost = lost;
        }

        public String getBookId() {
            return bookId;
        }

        public String getBookName() {
            return bookName;
        }

        public int getTimesBorrowed() {
            return timesBorrowed;
        }

        public int getEarlyReturns() {
            return earlyReturns;
        }

        public int getLateReturns() {
            return lateReturns;
        }

        public int getLost() {
            return lost;
        }
    }
}
