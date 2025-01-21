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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import logic.ClientTimeDiffController;
import logic.Subscriber;

public class LibrarianSubscriberStatusReportController extends BaseController {

    // Declare the LineChart and its axes
    @FXML
    private LineChart<String, String> lineChart;  // LineChart with String x-axis and y-axis

    @FXML
    private CategoryAxis xAxis;  // X-Axis for freeze dates
    @FXML
    private CategoryAxis yAxis;  // Y-Axis for subscriber names

    private int year = 2025; // Set the year dynamically
    private Month month = Month.JANUARY; // Set the month dynamically
    private ClientTimeDiffController clock = ChatClient.clock;
    
    // Initialization method called when the FXML is loaded
    @FXML
    public void initialize() {
        // You can initialize the chart and other UI components here
        System.out.println("Line chart successfully initialized!");
        
        // You can populate your chart with some initial data here if needed.
        setupChart();
    }

    private void setupChart() {
        // Set the categories for the X and Y axes
        xAxis.setLabel("Freeze Dates (dd-MM-yyyy)");
        yAxis.setLabel("Subscriber Names");

        // Fetch subscriber data
        List<Subscriber> subscribers = gatherFrozenDate(year, month);

        if (subscribers != null && !subscribers.isEmpty()) {
            // Create a new series for each subscriber
            for (Subscriber subscriber : subscribers) {
                XYChart.Series<String, String> series = new XYChart.Series<>();
                series.setName(subscriber.getSubscriber_name()); // Set subscriber name as the series name
                
                //TODO: Calculate the frozen status duration.
       
                String[] status = clock.parseFrozenSubscriberStatus(subscriber.getStatus());
                if (status[0].equals("Not Frozen")) {
                	// TODO: insert only the name of the subscriber without a line.
                }
                else { // Add the subscriber data to the plot.
                	LocalDate frozenAt = clock.convertStringToLocalDateTime(status[1]).toLocalDate(); // Check if the frozen end date is within the same month.
                	 
                	
                if (frozenAt.getMonth() == month && frozenAt.getYear() == year) { // Meaning the subscriber was frozen throughout the year -> freeze them until the end of the month.
                	// TODO: add a series from the frozen at day until the end of the month.
                	series.getData().add(new XYChart.Data<>(frozenAt.toString(), subscriber.getSubscriber_name())); // The day in which the subscriber got frozen.
                	
                	// Create the first day of the month
                    LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
                    
                    // Use lengthOfMonth() to get the last day of the month
                    String lastDayOfTheMonth =  firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth()).toString();
                	
                    // Add the last day of the month as another plot point in the graph.
                    series.getData().add(new XYChart.Data<>(frozenAt.toString(), subscriber.getSubscriber_name())); // The day in which the subscriber got frozen.
                    
                }else { // Meaning the subscriber's status is frozen from last month and is getting unfrozen throughout this month.
                	String unfreezeDate = frozenAt.plusMonths(1).toString();
                	//TODO: add a series from the start of the month until the day they are getting unfrozen.
                	series.getData().add(new XYChart.Data<>(unfreezeDate, subscriber.getSubscriber_name()));
                }                
                }

                // Add the series to the chart
                lineChart.getData().add(series);
            }
        }
    }


	private List<Subscriber> gatherFrozenDate(int year, Month month) {
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
	
	    // Call ChatClient to fetch subscriber data (should populate ChatClient.allSubscriberData)
	    ClientUI.chat.accept("FetchAllSubscriberData:"); // Asynchronous data fetch
	    
	    // Wait for the data to be returned (preferably using a callback, but this is just a placeholder)
	    try {
	        addDelayInMilliseconds(2000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	
	    return ChatClient.allSubscriberData;  // Assuming this contains the list of Subscriber objects
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
