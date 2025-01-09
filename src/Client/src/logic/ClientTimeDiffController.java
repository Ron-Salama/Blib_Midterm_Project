package logic;

import java.time.LocalDateTime;

public class ClientTimeDiffController {
	
	// TODO: Create a function to calculate a new deadline.
	
	public Boolean hasEnoughTimeBeforeDeadline(LocalDateTime today, LocalDateTime deadLine, int timeNeededBeforeDeadline) {
		LocalDateTime sevenDaysFromDeadline = deadLine.minusDays(timeNeededBeforeDeadline);
		if (sevenDaysFromDeadline.isAfter(today)) { // Meaning there isn't enough time to act before the deadline.
			return false;
		}
		
		return true; // Meaning there's enough time to act before the deadline.
		}
}
