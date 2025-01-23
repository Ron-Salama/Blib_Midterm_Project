package gui.LibrarianSubscriberStatusReportWindow;

import java.awt.Button;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import client.ChatClient;
import client.ClientUI;
import gui.baseController.BaseController;
import javafx.application.Platform;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import logic.ClientTimeDiffController;
import logic.SubscriberData;

public class LibrarianSubscriberStatusReportController extends BaseController {
	@FXML
	private TableView<SubscriberData> subscriberTable;

	@FXML
	private TableColumn<SubscriberData, String> colSubscriberId;

	@FXML
	private TableColumn<SubscriberData, String> colSubscriberName;

	@FXML
	private TableColumn<SubscriberData, String> colFrozenAt;

	@FXML
	private TableColumn<SubscriberData, String> colExpectedRelease;
    @FXML
    private ScatterChart<String, String> scatterChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private CategoryAxis yAxis;

    @FXML
    private PieChart pieChartFrozen;
    @FXML
    private BarChart barChart;
    @FXML
    private CategoryAxis barXAxis;
    @FXML
    private NumberAxis barYAxis;

    
    @FXML
    private ComboBox<String> comboMonths;

    private int year = 2025;
    private Month month = Month.JANUARY;
    private ClientTimeDiffController clock = ChatClient.clock;
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
                        expectedRelease = convertDateFormat(clock.convertStringToLocalDateTime(extractFreezeDate(subscriberFields[5])).toLocalDate().plusMonths(1).toString());
                    }

                    // Create a new SubscriberData object and add it to the filtered list
                    filteredData.add(new SubscriberData(subscriberId, subscriberName, frozenAt, expectedRelease));
                }
            }
        }

        // Set the filtered data into the TableView
        subscriberTable.setItems(filteredData);
    }


    	
   
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
                String idDateByDate = convertDateFormat(recordFields[0]);
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
    private boolean isDataForSelectedMonth(String dateStr) {
        // Split the date string into day, month, and year
        String[] dateParts = dateStr.split("-");
        int month = Integer.parseInt(dateParts[1]);  // Extract the month from the date

        // Check if the month matches the selected month from the ComboBox
        return month == this.month.getValue();
    }

    private List<String> getAllFrozenData() throws InterruptedException {
        List<String> frozenData = new ArrayList<>();

        // Request frozen subscriber information from the server
        ClientUI.chat.accept("FetchAllFrozenInformationForReports:");
        addDelayInMilliseconds(200);  // Wait for server response

        if (ChatClient.allFrozenDataForReport != null) {
            frozenData.addAll(ChatClient.allFrozenDataForReport);
        } else {
            System.out.println("Error: No frozen subscriber data received.");
        }

        return frozenData;
    }

    private void initializeChart() {
    	
        xAxis.setLabel("Freeze Date");
        yAxis.setLabel("Frozen Subscriber");
        yAxis.setAutoRanging(true);


    }

    public void setMonth(Month month) throws InterruptedException {
        this.month = month;
        updateChartForMonth();
    }

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

    private void updatePieChart(List<String> allSubscribers) {
        int totalSubscribers = allSubscribers.size();
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

    private void addPieChartLabels(PieChart.Data data, int count) {
        // Set custom label with the number count
        data.setName(data.getName() + "\n" + count); // Adds the count below the label
    }



    private String extractFreezeDate(String status) {
        if (status != null && status.contains("Frozen at:")) {
            return status.split("Frozen at:")[1].trim();  // Extract the date (e.g., "05-01-2025")
        }
        return "Unknown";
    }

    private boolean isFrozenInSelectedMonth(String freezeDate) {
        if (freezeDate.equals("Unknown")) return false;

        String[] dateParts = freezeDate.split("-");
        int month = Integer.parseInt(dateParts[1]);

        // Check if the subscriber was frozen in the specified month
        return month == this.month.getValue();
    }

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
    
    public void back(ActionEvent event) throws Exception{
    	openWindow(event,
    			"/gui/ReportsWindow/ReportsWindow.fxml",
    			"/gui/ReportsWindow/ReportsWindow.css",
    			"Subscriber Status Report");
    }
    public static String convertDateFormat(String dateStr) 
    {
    	
        // Define the input and output date formats
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
      // Parse the original string into a LocalDate object
        LocalDate date = LocalDate.parse(dateStr, inputFormatter);
        
        // Format the LocalDate object to the new string format
        return date.format(outputFormatter);
}
}
