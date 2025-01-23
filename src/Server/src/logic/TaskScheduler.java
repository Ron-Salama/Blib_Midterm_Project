package logic;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import server.EchoServer;

public class TaskScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private FreezeController freezeController = new FreezeController();
    private SMSandEmailController smsAndEmailController = new SMSandEmailController();
    private ReserveRequestDailyTasksController reserveRequestDailyTasksController = new ReserveRequestDailyTasksController();
    
    public void startDailyTasks() {
        Runnable dailyTask = new Runnable() {
            public void run() {
            	try { // TODO: Add more functions that need to run daily in here.
					Thread.sleep(1000); // Allow the SQL connection to be set so on startup so the function can work correctly.
					freezeController.freezeControllerDailyActivities(); // Run all of the daily activities needed from the freezeController.
					reserveRequestDailyTasksController.reserveRequestsDailyActivity(); // Run all of the methods that are related to the reserve functionality.
					smsAndEmailController.smsAndEmailControllerDailyActivities(); // Send Email and SMS to users that need to return their book the next day.
				} catch (InterruptedException e) {
					e.printStackTrace();
				}catch (SQLException e) {
					e.printStackTrace();
				}
            	
            	
				
            	// Notify in CMD and Log that the daily tasks finished running.
                EchoServer.outputInOutputStreamAndLog("Server daily tasks done.");
            }
        };
        scheduler.scheduleAtFixedRate(dailyTask, 0, 1, TimeUnit.DAYS); // Change timeUnit value to DAYS, WEEKS, MONTHS etc. accordingly to your needs.
    }


    public void stop() {
        scheduler.shutdown();
    }
}
