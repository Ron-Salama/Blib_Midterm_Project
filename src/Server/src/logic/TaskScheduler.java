package logic;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startDailyTasks() {
        Runnable dailyTask = new Runnable() {
            public void run() {
                updateDatabase();
            }
        };
        scheduler.scheduleAtFixedRate(dailyTask, 0, 1, TimeUnit.DAYS);
    }

    private void updateDatabase() {
        // Your database update logic here
        System.out.println("Database updated!");
    }

    public void stop() {
        scheduler.shutdown();
    }
}
