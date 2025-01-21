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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class LibrarianSubscriberStatusReportController extends BaseController {

    @FXML
    private ScatterChart<String, String> scatterChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private CategoryAxis yAxis;

    @FXML
    private PieChart pieChartFrozen;

    @FXML
    private ComboBox<String> comboMonths;

    private int year = 2025;
    private Month month = Month.JANUARY;

    @FXML
    public void initialize() throws InterruptedException {
        System.out.println("Scatter chart successfully initialized!");

        initializeChart();

        // Populate the ComboBox with month names
        ObservableList<String> months = FXCollections.observableArrayList(
            "January", "February", "March", "April", "May", "June", 
            "July", "August", "September", "October", "November", "December"
        );
        comboMonths.setItems(months);
        comboMonths.getSelectionModel().select(month.ordinal()); // Set default month to January

        // Add a listener to update the chart when a month is selected
        comboMonths.setOnAction(event -> {
            try {
                setMonth(Month.values()[comboMonths.getSelectionModel().getSelectedIndex()]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Fetch and process subscriber data
        List<String> allSubscribers = getAllSubscribers();

        // Create the series for frozen accounts, but filter by the selected month
        XYChart.Series<String, String> frozenAccountsSeries = createFrozenAccountsSeries(allSubscribers);

        // Only add series to chart if there's data for the selected month
        if (!frozenAccountsSeries.getData().isEmpty()) {
            scatterChart.getData().add(frozenAccountsSeries);
        }

        // Update the PieChart data
        updatePieChart(allSubscribers);

        // Set the default month and update the chart
        setMonth(Month.JANUARY);
        updateChartForMonth();
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

        // Fetch subscriber data for the updated month
        List<String> allSubscribers = getAllSubscribers();

        // Create the series for frozen accounts, filtering by the selected month
        XYChart.Series<String, String> frozenAccountsSeries = createFrozenAccountsSeries(allSubscribers);

        // Only add series to chart if there's data for the selected month
        if (!frozenAccountsSeries.getData().isEmpty()) {
            scatterChart.getData().add(frozenAccountsSeries);
        }

        // Update the PieChart data for the selected month
        updatePieChart(allSubscribers);
    }

    private List<String> getAllSubscribers() throws InterruptedException {
        List<String> subscribers = new ArrayList<>();

        // Request all subscriber information from the server
        ClientUI.chat.accept("FetchAllSubscriberInformationForReports:");
        addDelayInMilliseconds(200);  // Wait for server response

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

        for (String subscriberData : allSubscribers) {
            String[] subscriberFields = subscriberData.split(",");

            if (subscriberFields.length >= 6 && subscriberFields[5].contains("Frozen at:")) {
                String freezeDate = extractFreezeDate(subscriberFields[5]);
                if (isFrozenInSelectedMonth(freezeDate)) {
                    frozenCount++;
                }
            }
        }

        PieChart.Data frozenData = new PieChart.Data("Frozen Accounts", frozenCount);
        PieChart.Data activeData = new PieChart.Data("Active Accounts", totalSubscribers - frozenCount);

        // Clear previous data and add new data
        pieChartFrozen.getData().clear();
        pieChartFrozen.getData().addAll(frozenData, activeData);

        // Add labels to the pie chart slices
        addPieChartLabels(frozenData, frozenCount, totalSubscribers);
        addPieChartLabels(activeData, totalSubscribers - frozenCount, totalSubscribers);
    }

    private void addPieChartLabels(PieChart.Data data, int count, int total) {
        double percentage = ((double) count / total) * 100;
        String label = count + " (" + String.format("%.2f", percentage) + "%)";  // Display count and percentage

        data.setName(label); // Set the label text as the slice's name (this will be displayed in the chart)
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
    
}
