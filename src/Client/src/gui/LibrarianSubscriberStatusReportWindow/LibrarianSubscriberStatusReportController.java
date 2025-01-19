package gui.LibrarianSubscriberStatusReportWindow;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Arrays;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

public class LibrarianSubscriberStatusReportController {

    @FXML
    private BarChart<Number, String> ganttChart;  // Change to Number for X-axis (NumberAxis)

    @FXML
    private AnchorPane root;

    // Sample data for subscribers' frozen times
    private List<FreezePeriod> freezePeriods;

    @FXML
    public void initialize() {
        // Initialize chart title and axis labels
        ganttChart.setTitle("Subscriber Freeze Status");
        ganttChart.getYAxis().setLabel("Subscriber");

        // Adding freeze data (this can be loaded from a database)
        freezePeriods = Arrays.asList(
                new FreezePeriod("Subscriber 1", LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 1, 3, 0, 0)),
                new FreezePeriod("Subscriber 2", LocalDateTime.of(2025, 1, 2, 0, 0), LocalDateTime.of(2025, 1, 4, 0, 0)),
                new FreezePeriod("Subscriber 3", LocalDateTime.of(2025, 1, 4, 0, 0), LocalDateTime.of(2025, 1, 7, 0, 0))
        );

        // Update chart with the freeze data
        updateChart();
    }

    private void updateChart() {
        // Calculate number of days in the current month
        int daysInMonth = getDaysInMonth();

        // Get the CategoryAxis from FXML for Y-axis
        CategoryAxis yAxis = (CategoryAxis) ganttChart.getYAxis();

        // Create the series for the chart
        XYChart.Series<Number, String> series = new XYChart.Series<>();
        series.setName("Frozen Period");

        // Loop over the freeze periods to add the data
        for (FreezePeriod period : freezePeriods) {
            // Convert start and end times to integer days
            int startDay = period.getStart().getDayOfMonth();
            int endDay = period.getEnd().getDayOfMonth();

            // Add data points for the frozen periods
            for (int day = startDay; day <= endDay; day++) {
                series.getData().add(new XYChart.Data<>(day, period.getSubscriberName()));
            }
        }

        // Clear previous data and add the new data series
        ganttChart.getData().clear();
        ganttChart.getData().add(series);
    }
    
    private int getDaysInMonth() {
        // Get the number of days in the current month
        int year = LocalDateTime.now().getYear();
        return LocalDateTime.now().getMonth().length(Year.isLeap(year));
    }


    public static class FreezePeriod {
        private final String subscriberName;
        private final LocalDateTime start;
        private final LocalDateTime end;

        public FreezePeriod(String subscriberName, LocalDateTime start, LocalDateTime end) {
            this.subscriberName = subscriberName;
            this.start = start;
            this.end = end;
        }

        public String getSubscriberName() {
            return subscriberName;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public LocalDateTime getEnd() {
            return end;
        }
    }
}
