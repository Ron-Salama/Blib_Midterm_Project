package gui.LibrarianSubscriberStatusReportWindow;

import java.time.LocalDate;
import java.time.Month;
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
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.ClientTimeDiffController;
import logic.SubscriberData;
/**
 * Controller class for managing the Librarian Subscriber Status Report window.
 * This class handles the functionality of displaying subscriber data and statistics,
 * including frozen status, expected release dates, and visualizing the data with charts.
 */
public class LibrarianSubscriberStatusReportController extends BaseController {
	
	/** TableView to display subscriber data */ 
	@FXML
	private TableView<SubscriberData> subscriberTable; 

	/** TableColumn for displaying Subscriber ID */
	@FXML
	private TableColumn<SubscriberData, String> colSubscriberId;

	/** TableColumn for displaying Subscriber Name */
	@FXML
	private TableColumn<SubscriberData, String> colSubscriberName;

	/** TableColumn for displaying the Frozen At date */
	@FXML
	private TableColumn<SubscriberData, String> colFrozenAt;

	/** TableColumn for displaying the Expected Release date */ 
	@FXML
	private TableColumn<SubscriberData, String> colExpectedRelease;
	
	/** Scatter chart for visualizing frozen subscriber data */ 
    @FXML
    private ScatterChart<String, String> scatterChart;

    /** X-axis for the scatter chart */
    @FXML
    private CategoryAxis xAxis;

    /** Y-axis for the scatter chart */
    @FXML
    private CategoryAxis yAxis;

    /** Pie chart for displaying the proportion of frozen and unfrozen subscribers */ 
    @FXML
    private PieChart pieChartFrozen;
   
    /** Bar chart for displaying frozen and not frozen data statistics */ 
    @FXML
    private BarChart barChart;
   
    /** X-axis for the bar chart */
    @FXML
    private CategoryAxis barXAxis;
    
    /**  Y-axis for the bar chart */
    @FXML
    private NumberAxis barYAxis;

    /** ComboBox for selecting the month */
    @FXML
    private ComboBox<String> comboMonths;

    /** The year to be displayed in reports */
    private int year = 2025;
    
    /** The current month selected for reporting */
    private Month month = Month.JANUARY;
    
    /** The controller for time-related functionalities */
    private ClientTimeDiffController clock = ChatClient.clock;
    
    /**
     * Initializes the components of the Librarian Subscriber Status Report window,
     * including setting up table columns, ComboBox for month selection, and populating charts.
     */
    @FXML
    public void initialize() throws InterruptedException {
        scatterChart.setVisible(false);
        colSubscriberId.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
        colSubscriberName.setCellValueFactory(new PropertyValueFactory<>("subscriberName"));
        colFrozenAt.setCellValueFactory(new PropertyValueFactory<>("frozenAt"));
        colExpectedRelease.setCellValueFactory(new PropertyValueFactory<>("expectedRelease"));

        // Fetch and process subscriber data
        List<String> allSubscribers = getAllSubscribers();
        ObservableList<SubscriberData> subscriberDataList = FXCollections.observableArrayList();

        // Populate the ComboBox with month names
        ObservableList<String> months = FXCollections.observableArrayList(
            "January", "February", "March", "April", "May", "June", 
            "July", "August", "September", "October", "November", "December"
        );
        comboMonths.setItems(months);
        comboMonths.getSelectionModel().select(month.ordinal()); // Set default month to January

        // Add listener to update the chart and table when a month is selected
        comboMonths.setOnAction(event -> {
            try {
                setMonth(Month.values()[comboMonths.getSelectionModel().getSelectedIndex()]);
                filterAndUpdateTable(allSubscribers);  // Re-populate the table when the month changes
                populateBarChart();  // Re-populate the bar chart when the month changes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Initial population of the table and charts for the selected month
        filterAndUpdateTable(allSubscribers);
        populateBarChart();  // Populate the bar chart

        // Set the default month and update the chart
        setMonth(Month.JANUARY);
        updateChartForMonth();
    }

    /**
     * Filters and updates the subscriber table based on the selected month.
     * @param allSubscribers List of all subscribers to be filtered.
     */
    private void filterAndUpdateTable(List<String> allSubscribers) {
        // Filter the subscriber data for the selected month
        ObservableList<SubscriberData> filteredData = FXCollections.observableArrayList();
        
        for (String subscriberData : allSubscribers) {
            String[] subscriberFields = subscriberData.split(",");
            if (subscriberFields.length >= 6) {
                String frozenAt = extractFreezeDate(subscriberFields[5]);
                if (isFrozenInSelectedMonth(frozenAt)) {
                    String subscriberId = subscriberFields[0];
                    String subscriberName = subscriberFields[1];
                    String expectedRelease = extractFreezeDate(subscriberFields[5]);
                    
                    // If expectedRelease is "Unknown" or empty, set it to "Not Frozen"
                    if (expectedRelease.equals("Unknown") || expectedRelease.isEmpty()) {
                        expectedRelease = "Not Frozen";
                    } else {
                        expectedRelease = clock.convertDateFormat(clock.convertStringToLocalDate(extractFreezeDate(subscriberFields[5])).plusMonths(1).toString());
                    }

                    // Create a new SubscriberData object and add it to the filtered list
                    filteredData.add(new SubscriberData(subscriberId, subscriberName, frozenAt, expectedRelease));
                }
            }
        }

        // Set the filtered data into the TableView
        subscriberTable.setItems(filteredData);
    }

    /**
     * Populates the bar chart with frozen and not frozen subscriber data.
     * @throws InterruptedException if the data fetch is interrupted.
     */
    private void populateBarChart() throws InterruptedException {
        // Clear previous data
        barChart.getData().clear();

        List<String> frozenData = getAllFrozenData();

        // Create series for Frozen and Not Frozen
        XYChart.Series<String, Number> frozenSeries = new XYChart.Series<>();
        frozenSeries.setName("Frozen");

        XYChart.Series<String, Number> notFrozenSeries = new XYChart.Series<>();
        notFrozenSeries.setName("Not Frozen");

        // Filter and add data to the series for the selected month
        for (String record : frozenData) {
            String[] recordFields = record.split(",");
            if (recordFields.length >= 3) {
                String idDateByDate = clock.convertDateFormat(recordFields[0]);
                int frozenCount = Integer.parseInt(recordFields[1]);
                int notFrozenCount = Integer.parseInt(recordFields[2]);

                // Only add data for the selected month
                if (isDataForSelectedMonth(idDateByDate)) {
                    frozenSeries.getData().add(new XYChart.Data<>(idDateByDate, frozenCount));
                    notFrozenSeries.getData().add(new XYChart.Data<>(idDateByDate, notFrozenCount));
                }
            }
        }

        // Add series to the bar chart
        barChart.getData().addAll(frozenSeries, notFrozenSeries);
        
    }
    
    /**
     * Checks if the data belongs to the selected month.
     * @param dateStr the date string to be checked.
     * @return true if the data is for the selected month.
     */
    private boolean isDataForSelectedMonth(String dateStr) {
        // Split the date string into day, month, and year
        String[] dateParts = dateStr.split("-");
        int month = Integer.parseInt(dateParts[1]);  // Extract the month from the date

        // Check if the month matches the selected month from the ComboBox
        return month == this.month.getValue();
    }

    /**
     * Requests frozen subscriber data from the server.
     * @return a list of frozen data records.
     * @throws InterruptedException if the request is interrupted.
     */
    private List<String> getAllFrozenData() throws InterruptedException {
        List<String> frozenData = new ArrayList<>();

        // Request frozen subscriber information from the server
        ClientUI.chat.accept("FetchAllFrozenInformationForReports:");
        waitForServerResponse();

        if (ChatClient.allFrozenDataForReport != null) {
            frozenData.addAll(ChatClient.allFrozenDataForReport);
        } else {
            System.out.println("Error: No frozen subscriber data received.");
        }

        return frozenData;
    }

    /**
     * Initializes the chart axes.
     */
    private void initializeChart() {
        xAxis.setLabel("Freeze Date");
        yAxis.setLabel("Frozen Subscriber");
        yAxis.setAutoRanging(true);
    }

    /**
     * Sets the month for report and updates the chart accordingly.
     * @param month the month to be set.
     * @throws InterruptedException if the month setting is interrupted.
     */
    public void setMonth(Month month) throws InterruptedException {
        this.month = month;
        updateChartForMonth();
    }

    /**
     * Updates the chart for the selected month.
     * @throws InterruptedException if the chart update is interrupted.
     */
    private void updateChartForMonth() throws InterruptedException {
        // Clear the existing chart data
        scatterChart.getData().clear();

        // Reset the axis labels for the new month
        xAxis.setLabel("Freeze Date (" + month + " " + year + ")");
        yAxis.setLabel("Frozen Subscriber");

        // Fetch subscriber data for the updated month
        List<String> allSubscribers = getAllSubscribers();

        // Create the series for frozen accounts, filtering by the selected month
        XYChart.Series<String, String> frozenAccountsSeries = createFrozenAccountsSeries(allSubscribers);

        // Only add series to chart if there's data for the selected month
        if (!frozenAccountsSeries.getData().isEmpty()) {
            scatterChart.getData().add(frozenAccountsSeries);
        } else {
            System.out.println("No data available for the selected month.");
        }

        // Update the PieChart data for the selected month
        updatePieChart(allSubscribers);
    }
    public void update(ActionEvent event) throws Exception {
    	scatterChart.getData().clear();
    	updateChartForMonth();
    }
    
    /**
     * Gets a list of all subscribers.
     * @return a list of all subscriber data.
     */
    private List<String> getAllSubscribers() throws InterruptedException {
        List<String> subscribers = new ArrayList<>();

        // Request all subscriber information from the server
        ClientUI.chat.accept("FetchAllSubscriberInformationForReports:");

        waitForServerResponse();

        if (ChatClient.allSubscriberDataForReport != null) {
            subscribers.addAll(ChatClient.allSubscriberDataForReport);
        } else {
            System.out.println("Error: No subscriber data received.");
        }

        return subscribers;
    }

    private XYChart.Series<String, String> createFrozenAccountsSeries(List<String> allSubscribers) {
        XYChart.Series<String, String> frozenAccountsSeries = new XYChart.Series<>();
        frozenAccountsSeries.setName("Frozen Accounts");

        // Sort the list of subscribers by the freeze date
        allSubscribers.sort((subscriber1, subscriber2) -> {
            String freezeDate1 = extractFreezeDate(subscriber1.split(",")[5]);
            String freezeDate2 = extractFreezeDate(subscriber2.split(",")[5]);
            LocalDate date1 = convertToLocalDate(freezeDate1);
            LocalDate date2 = convertToLocalDate(freezeDate2);
            return date1.compareTo(date2);
        });

        // Iterate over all subscribers and extract frozen account details
        for (String subscriberData : allSubscribers) {
            String[] subscriberFields = subscriberData.split(",");

            if (subscriberFields.length >= 6 && subscriberFields[5].contains("Frozen at:")) {
                String freezeDate = extractFreezeDate(subscriberFields[5]);

                // Only include the subscriber if frozen in the selected month
                if (isFrozenInSelectedMonth(freezeDate)) {
                    String subscriberName = subscriberFields[1];  // Assuming name is the second field
                    XYChart.Data<String, String> data = new XYChart.Data<>(freezeDate, subscriberName);

                    // REMOVE THE CIRCLE PART (RED CIRCLE)

                    frozenAccountsSeries.getData().add(data);
                }
            }
        }

        return frozenAccountsSeries;
    }

    /**
     * Updates the PieChart with frozen and unfrozen subscriber data for the selected month.
     * @param allSubscribers the list of all subscribers.
     */
    private void updatePieChart(List<String> allSubscribers) {
        int frozenCount = 0;
        int unfrozenCount = 0;

        // Iterate through the subscriber data and classify into frozen and unfrozen
        for (String subscriberData : allSubscribers) {
            String[] subscriberFields = subscriberData.split(",");

            if (subscriberFields.length >= 6 && subscriberFields[5].contains("Frozen at:")) {
                String freezeDate = extractFreezeDate(subscriberFields[5]);
                if (isFrozenInSelectedMonth(freezeDate)) {
                    frozenCount++;
                }
            } else {
                unfrozenCount++;
            }
        }

        // Create the PieChart data for Frozen and Unfrozen accounts
        PieChart.Data frozenData = new PieChart.Data("Frozen", frozenCount);
        PieChart.Data unfrozenData = new PieChart.Data("Unfrozen", unfrozenCount);

        // Clear previous data and add new data
        pieChartFrozen.getData().clear();
        pieChartFrozen.getData().addAll(frozenData, unfrozenData);

        // Add custom labels with the number of subscribers
        addPieChartLabels(frozenData, frozenCount);
        addPieChartLabels(unfrozenData, unfrozenCount);
    }

    /**
     * Adds custom labels to the PieChart.
     * @param data the data for the PieChart slice.
     * @param count the count of subscribers for the slice.
     */
    private void addPieChartLabels(PieChart.Data data, int count) {
        // Set custom label with the number count
        data.setName(data.getName() + "\n" + count); // Adds the count below the label
    }

    /**
     * Extracts the freeze date from the status string.
     * @param status the status string containing the freeze date.
     * @return the extracted freeze date.
     */
    private String extractFreezeDate(String status) {
        if (status != null && status.contains("Frozen at:")) {
            return status.split("Frozen at:")[1].trim();  // Extract the date (e.g., "05-01-2025")
        }
        return "Unknown";
    }

    /**
     * Checks if the subscriber was frozen in the selected month.
     * @param freezeDate the freeze date of the subscriber.
     * @return true if the subscriber was frozen in the selected month.
     */
    private boolean isFrozenInSelectedMonth(String freezeDate) {
        if (freezeDate.equals("Unknown")) return false;

        String[] dateParts = freezeDate.split("-");
        int month = Integer.parseInt(dateParts[1]);

        // Check if the subscriber was frozen in the specified month
        return month == this.month.getValue();
    }

    /**
     * Converts a freeze date string in the format "dd-MM-yyyy" to a LocalDate object.
     * If the freeze date is "Unknown", it returns LocalDate.MAX as a placeholder.
     * 
     * @param freezeDate the freeze date in "dd-MM-yyyy" format or "Unknown".
     * @return a LocalDate object representing the freeze date, or LocalDate.MAX if the date is "Unknown".
     */
    private LocalDate convertToLocalDate(String freezeDate) {
        if (freezeDate.equals("Unknown")) {
            return LocalDate.MAX;
        }

        String[] dateParts = freezeDate.split("-");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);

        return LocalDate.of(year, month, day);
    }
    
    /**
     * Handles the action when the back button is clicked. It opens the ReportsWindow.fxml 
     * and applies the associated CSS stylesheet for the ReportsWindow.
     * 
     * @param event the ActionEvent triggered by the back button click.
     * @throws Exception if there is an error in loading the window.
     */
    public void back(ActionEvent event) throws Exception{
    	openWindow(event,
    			"/gui/ReportsWindow/ReportsWindow.fxml",
    			"/gui/ReportsWindow/ReportsWindow.css",
    			"Subscriber Status Report");
    }
}
