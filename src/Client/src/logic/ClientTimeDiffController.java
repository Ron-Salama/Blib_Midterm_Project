package logic;

import java.time.LocalDateTime;

public class ClientTimeDiffController {
	
	// TODO: Create a function to calculate a new deadline.
	
	public Boolean hasEnoughTimeBeforeDeadline(LocalDateTime today, LocalDateTime deadLine, int timeNeededBeforeDeadline) {
		LocalDateTime sevenDaysFromDeadline = deadLine.minusDays(timeNeededBeforeDeadline);
		if (sevenDaysFromDeadline.isAfter(today)) { // Meaning there isn't enough time to act before the deadline.
			return false; // display message to client screen
		}
		
<<<<<<< Updated upstream
		return true; // Meaning there's enough time to act before the deadline.
		}
<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
=======
>>>>>>> Stashed changes
=======
		return true; // Meaning there's enough time to act before the deadline. // perform extend (Almog)
	}
>>>>>>> Stashed changes
	
	public String calculateReturnDate(LocalDateTime today, int amountOfDays) {
		LocalDateTime returnDate = today.plusDays(amountOfDays);
		return returnDate.format(dateFormatter).toString();
	}
	
	public String extendReturnDate(LocalDateTime returnDate, int amountOfDays) {
		LocalDateTime returnDateAfterExtension = returnDate.plusDays(amountOfDays);
		return returnDateAfterExtension.format(dateFormatter).toString();
	}
	
	public String timeNow() {
		LocalDateTime now = LocalDateTime.now();
		return now.format(dateFormatter);
	}
>>>>>>> Stashed changes
}
