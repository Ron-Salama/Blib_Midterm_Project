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
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class LibrarianBorrowedBooksReportController extends BaseController {

    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private CategoryAxis barXAxis;
    @FXML
    private NumberAxis barYAxis;

    @FXML
    private ComboBox<String> monthComboBox;

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

        // Add listener to ComboBox to update chart and table based on selected month
        monthComboBox.setOnAction(event -> {
            try {
                populateBarChart(); // Update chart when month is selected
                populateTable();    // Update table when month is selected
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
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
        returnedSeries.setName("Returned");

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
