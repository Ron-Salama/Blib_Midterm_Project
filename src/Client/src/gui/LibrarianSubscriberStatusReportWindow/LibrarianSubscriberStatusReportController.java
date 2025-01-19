package gui.LibrarianSubscriberStatusReportWindow;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import client.ChatClient;
import client.ClientUI;
import gui.baseController.BaseController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import logic.Subscriber;

public class LibrarianSubscriberStatusReportController extends BaseController{

    @FXML
    private AnchorPane root;

    @FXML
    private BarChart<String, Number> ganttChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private int year = 2025; // Set the year dynamically
    private Month month = Month.JANUARY; // Set the month dynamically

    /**
     * Initializes the view by preparing the chart data and configurations.
     * @throws InterruptedException 
     */
    @FXML
    public void initialize() throws InterruptedException {
        // Ensure all elements are injected
        System.out.println("ganttChart: " + ganttChart);
        System.out.println("xAxis: " + xAxis);
        System.out.println("yAxis: " + yAxis);

        // Prepare chart data with custom dates
        updateChart(year, month);
    }

    /**
     * Updates the chart with subscriber status data for a given year and month.
     * @throws InterruptedException 
     */
    private void updateChart(int year, Month month) throws InterruptedException {
    	 if (ganttChart != null && xAxis != null && yAxis != null) {
    	        // Generate date range for the selected month
    	        List<String> datesNotFormatted = getDateRange(year, month);

    	        // Prepare a list to store the correctly formatted dates
    	        List<String> dates = new ArrayList<>();

    	        // Formatter to handle input date format (yyyy-MM-dd)
    	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");  // Desired output format

    	        for (String date : datesNotFormatted) {
    	            try {
    	                // Parse the date string to LocalDate using the input format
    	                LocalDate parsedDate = LocalDate.parse(date, inputFormatter);

    	                // Format the LocalDate to the desired format (dd-MM-yyyy)
    	                dates.add(parsedDate.format(outputFormatter)); // Add formatted date to the list
    	            } catch (Exception e) {
    	                // Handle error if the date format does not match or conversion fails
    	                System.err.println("Error parsing date: " + date);
    	            }
    	        }
            

            // Set up the x-axis categories with the dates (Days of the month)
            xAxis.setCategories(FXCollections.observableArrayList(dates)); 

            // Creating data series for multiple subscribers
            List<XYChart.Series<String, Number>> subscriberSeriesList = new ArrayList<>();

            // Gather subscriber data from database.
            ClientUI.chat.accept("FetchAllSubscriberData:");// XXX
            
            // XXX add delay
            addDelayInMilliseconds(2000);
            
            // Implement all of the subscriber information into a local variable.
            
            // XXX I stopped here on 19.1 23:50 because allSubscriberData is null, which means that the function in ChatClient doesn't get invoked.
            List<Subscriber> subscribers = ChatClient.allSubscriberData;

            // Add each subscriber's data to the chart
            for (Subscriber subscriber : subscribers) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(subscriber.getSubscriber_name());

                String status = subscriber.getStatus();
                
                // XXX chcek that everything is all good and well up until this point.
                
                
//                // Add frozen periods for this subscriber (color bars on the Gantt chart)
//                for (FrozenPeriod period : subscriber.getFrozenPeriods()) {
//                    String startDate = period.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
//                    String endDate = period.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
//
//                    // Add the frozen period to the chart data series
//                    for (String date : dates) {
//                        if (date.equals(startDate) || date.equals(endDate)) {
//                            series.getData().add(new XYChart.Data<>(date, subscriber.getId()));
//                        }
//                    }
//                }
//                subscriberSeriesList.add(series);
            }

            // Add the series to the chart
            ganttChart.getData().setAll(subscriberSeriesList);
        } else {
            System.out.println("Something went wrong: One of the components is null.");
        }
    }

    /**
     * Generate a list of date strings for the given year and month.
     * @param year The year of the desired month
     * @param month The month for which to generate the date range
     * @return List of date strings in format YYYY-MM-DD
     */
    private List<String> getDateRange(int year, Month month) {
        List<String> dateRange = new ArrayList<>();
        LocalDate start = LocalDate.of(year, month, 1);
        int daysInMonth = start.lengthOfMonth(); // Get the number of days in the month

        // Generate the date strings for all days in the month
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate currentDate = start.withDayOfMonth(i);
            dateRange.add(currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        return dateRange;
    }

}
