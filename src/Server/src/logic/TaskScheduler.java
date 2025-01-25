package logic;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import server.EchoServer;

/**
 * The {@code TaskScheduler} class is responsible for scheduling and executing daily tasks,
 * including activities such as freezing, reserving requests, sending SMS and email notifications,
 * and cleaning extensions for subscribers. It uses a scheduled executor service to run these tasks
 * at fixed intervals.
 */
public class TaskScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private FreezeController freezeController = new FreezeController();
    private SMSandEmailController smsAndEmailController = new SMSandEmailController();
    private ReserveRequestDailyTasksController reserveRequestDailyTasksController = new ReserveRequestDailyTasksController();
    private ExtensionBySubscriberController extensionBySubscriberController = new ExtensionBySubscriberController();
    private HandleReportController yesterdayBorrowsController = new HandleReportController();
    
    /**
     * Starts the scheduled daily tasks, which include activities such as freezing, handling 
     * reserve requests, sending notifications, and cleaning extensions. The tasks are executed 
     * daily at a fixed rate.
     */
    public void startDailyTasks() {
        Runnable dailyTask = new Runnable() {
            public void run() {
            	try {
					Thread.sleep(1000); // Allow the SQL connection to be set so on startup so the function can work correctly.
					freezeController.freezeControllerDailyActivities(); // Run all of the daily activities needed from the freezeController.
					reserveRequestDailyTasksController.reserveRequestsDailyActivity(); // Run all of the methods that are related to the reserve functionality.
					smsAndEmailController.smsAndEmailControllerDailyActivities(); // Send Email and SMS to users that need to return their book the next day.
					extensionBySubscriberController.cleanEveryDayExetnsionsInLibrarian();
					yesterdayBorrowsController.yesterdayborrowcounter();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}catch (SQLException e) {
					e.printStackTrace();
				}
            	
            	// Notify in CMD and Log that the daily tasks finished running.
                EchoServer.outputInOutputStreamAndLog("Server daily tasks done.");
            }
        };
        // Schedule the daily task to run once every day.
        scheduler.scheduleAtFixedRate(dailyTask, 0, 1, TimeUnit.DAYS); // Change timeUnit value to DAYS, WEEKS, MONTHS etc. accordingly to your needs.
    }

    /**
     * Stops the scheduled tasks by shutting down the scheduler.
     */
    public void stop() {
        scheduler.shutdown();
    }
}
