package tests.clientTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import logic.ClientTimeDiffController;

class ClientTimeDiffControllerTest {

    private ClientTimeDiffController controller;

    @BeforeEach
    void setUp() {
        controller = new ClientTimeDiffController();
    }

    @Test
    void testIsInTimeWindowToDoAction_WithEnoughTime() {
        LocalDateTime deadline = LocalDateTime.now().plusDays(6);
        int timeNeeded = 7;
        boolean result = controller.isInTimeWindowToDoAction(deadline, timeNeeded);
        System.out.println("Deadline: " + deadline);
        System.out.println("Time needed: " + timeNeeded);
        System.out.println("Result: " + result);
        assertTrue(result);
    }

    @Test
    void testIsInTimeWindowToDoAction_WithoutEnoughTime() {
        LocalDateTime deadline = LocalDateTime.now().minusDays(1);
        int timeNeeded = 7;
        boolean result = controller.isInTimeWindowToDoAction(deadline, timeNeeded);
        System.out.println("Deadline: " + deadline);
        System.out.println("Time needed: " + timeNeeded);
        System.out.println("Result: " + result);
        assertFalse(result);
    }
    
    @Test
    void testIsInTimeWindowToDoAction_DeadlineOnTheSameDayAsToday() {
            LocalDateTime deadline = LocalDateTime.now();
            int timeNeeded = 7;
            boolean result = controller.isInTimeWindowToDoAction(deadline, timeNeeded);
            System.out.println("Deadline: " + deadline);
            System.out.println("Time needed: " + timeNeeded);
            System.out.println("Result: " + result);
            assertTrue(result);
    }

    @Test
    void testCalculateReturnDate() {
        int amountOfDays = 10;
        String expectedDate = LocalDateTime.now().plusDays(amountOfDays)
                .format(controller.getDateFormatter());
        String result = controller.calculateReturnDate(amountOfDays);
        System.out.println("Amount of days: " + amountOfDays);
        System.out.println("Expected date: " + expectedDate);
        System.out.println("Result: " + result);
        assertEquals(expectedDate, result);
    }

    @Test
    void testExtendReturnDate() {
        LocalDateTime returnDate = LocalDateTime.now();
        int extensionDays = 5;
        String expectedDate = returnDate.plusDays(extensionDays)
                .format(controller.getDateFormatter());
        String result = controller.extendReturnDate(returnDate, extensionDays);
        System.out.println("Return date: " + returnDate);
        System.out.println("Extension days: " + extensionDays);
        System.out.println("Expected date: " + expectedDate);
        System.out.println("Result: " + result);
        assertEquals(expectedDate, result);
    }

    @Test
    void testTimeNow() {
        String expectedDate = LocalDateTime.now().format(controller.getDateFormatter());
        String result = controller.timeNow();
        System.out.println("Expected date: " + expectedDate);
        System.out.println("Result: " + result);
        assertEquals(expectedDate, result);
    }

    @Test
    void testConvertStringToLocalDateTime_ValidString() {
        String validDate = "14-01-2025";
        LocalDateTime expectedDate = LocalDateTime.of(2025, 1, 14, 0, 0);
        LocalDateTime result = controller.convertStringToLocalDateTime(validDate);
        System.out.println("Input string: " + validDate);
        System.out.println("Expected LocalDateTime: " + expectedDate);
        System.out.println("Result: " + result);
        assertEquals(expectedDate, result);
    }

    @Test
    void testConvertStringToLocalDateTime_InvalidString() {
        String invalidDate = "invalid-date";
        System.out.println("Input string: " + invalidDate);
        assertThrows(DateTimeParseException.class, 
            () -> controller.convertStringToLocalDateTime(invalidDate));
    }

    @Test
    void testIsInTimeWindowToDoAction_WithValidString() {
        String deadline = LocalDateTime.now().plusDays(10)
                .format(controller.getDateFormatter());
        int timeNeeded = 7;
        boolean result = controller.hasEnoughTimeBeforeDeadline(deadline, timeNeeded);
        System.out.println("Deadline string: " + deadline);
        System.out.println("Time needed: " + timeNeeded);
        System.out.println("Result: " + result);
        assertFalse(result);
    }

    @Test
    void testIsInTimeWindowToDoAction_WithInvalidString() {
        String invalidDeadline = "invalid-date";
        int timeNeeded = 7;
        System.out.println("Deadline string: " + invalidDeadline);
        System.out.println("Time needed: " + timeNeeded);
        assertThrows(DateTimeParseException.class, 
            () -> controller.hasEnoughTimeBeforeDeadline(invalidDeadline, timeNeeded));
    }

    @Test
    void testExtendReturnDate_WithValidString() {
        String returnDate = LocalDateTime.now()
                .format(controller.getDateFormatter());
        int extensionDays = 5;
        String expectedDate = LocalDateTime.now().plusDays(extensionDays)
                .format(controller.getDateFormatter());
        String result = controller.extendReturnDate(returnDate, extensionDays);
        System.out.println("Return date string: " + returnDate);
        System.out.println("Extension days: " + extensionDays);
        System.out.println("Expected date: " + expectedDate);
        System.out.println("Result: " + result);
        assertEquals(expectedDate, result);
    }

    @Test
    void testExtendReturnDate_WithInvalidString() {
        String invalidReturnDate = "invalid-date";
        int extensionDays = 5;
        System.out.println("Return date string: " + invalidReturnDate);
        System.out.println("Extension days: " + extensionDays);
        assertThrows(DateTimeParseException.class, 
            () -> controller.extendReturnDate(invalidReturnDate, extensionDays));
    }
    

        @Test
        void testHowMuchTimeLeftToReturnABook_WithFutureDate() {
            LocalDateTime returnDate = LocalDateTime.now().plusDays(10);
            int expectedDays = 10;
            int result = controller.howMuchTimeLeftToReturnABook(returnDate);
            assertEquals(expectedDays, result, "The time difference should be 10 days.");
        }

        @Test
        void testHowMuchTimeLeftToReturnABook_WithPastDate() {
            LocalDateTime returnDate = LocalDateTime.now().minusDays(5);
            int expectedDays = -5;
            int result = controller.howMuchTimeLeftToReturnABook(returnDate);
            assertEquals(expectedDays, result, "The time difference should be -5 days.");
        }

        @Test
        void testHowMuchTimeLeftToReturnABook_WithTodayDate() {
            LocalDateTime returnDate = LocalDateTime.now();
            int expectedDays = 0;
            int result = controller.howMuchTimeLeftToReturnABook(returnDate);
            assertEquals(expectedDays, result, "The time difference should be 0 days.");
        }

        @Test
        void testHowMuchTimeLeftToReturnABook_WithFutureDateIncludingTime() {
            LocalDateTime returnDate = LocalDateTime.now().plusDays(2).plusHours(3);
            int expectedDays = 2; // Only full days are considered
            int result = controller.howMuchTimeLeftToReturnABook(returnDate);
            assertEquals(expectedDays, result, "The time difference should be 2 days, ignoring hours.");
        }

        @Test
        void testHowMuchTimeLeftToReturnABook_WithPastDateIncludingTime() {
            LocalDateTime returnDate = LocalDateTime.now().minusDays(2).minusHours(3);
            int expectedDays = -2; // Only full days are considered
            int result = controller.howMuchTimeLeftToReturnABook(returnDate);
            assertEquals(expectedDays, result, "The time difference should be -2 days, ignoring hours.");
        }

        @Test
        void testTimeDateDifferenceBetweenTwoDates_BothDatesLocalDateTime() {
            LocalDateTime firstDate = LocalDateTime.of(2025, 1, 10, 0, 0);
            LocalDateTime secondDate = LocalDateTime.of(2025, 1, 15, 0, 0);
            int expectedDiff = 5;
            assertEquals(expectedDiff, controller.timeDateDifferenceBetweenTwoDates(firstDate, secondDate));
        }

        @Test
        void testTimeDateDifferenceBetweenTwoDates_BothDatesLocalDateTime_NegativeResult() {
            LocalDateTime firstDate = LocalDateTime.of(2025, 1, 15, 0, 0);
            LocalDateTime secondDate = LocalDateTime.of(2025, 1, 10, 0, 0);
            int expectedDiff = -5;
            assertEquals(expectedDiff, controller.timeDateDifferenceBetweenTwoDates(firstDate, secondDate));
        }

        @Test
        void testTimeDateDifferenceBetweenTwoDates_OneDateAsString() {
            LocalDateTime firstDate = LocalDateTime.of(2025, 1, 10, 0, 0);
            String secondDateAsString = "15-01-2025";
            int expectedDiff = 5;
            assertEquals(expectedDiff, controller.timeDateDifferenceBetweenTwoDates(firstDate, secondDateAsString));
        }

        @Test
        void testTimeDateDifferenceBetweenTwoDates_OneDateAsString_InvalidString() {
            LocalDateTime firstDate = LocalDateTime.of(2025, 1, 10, 0, 0);
            String invalidSecondDate = "invalid-date";
            assertThrows(Exception.class, 
                () -> controller.timeDateDifferenceBetweenTwoDates(firstDate, invalidSecondDate));
        }

        @Test
        void testTimeDateDifferenceBetweenTwoDates_OneDateAsString_NegativeResult() {
            String firstDateAsString = "15-01-2025";
            LocalDateTime secondDate = LocalDateTime.of(2025, 1, 10, 0, 0);
            int expectedDiff = -5;
            assertEquals(expectedDiff, controller.timeDateDifferenceBetweenTwoDates(firstDateAsString, secondDate));
        }

        @Test
        void testTimeDateDifferenceBetweenTwoDates_BothDatesAsString() {
            String firstDateAsString = "10-01-2025";
            String secondDateAsString = "15-01-2025";
            int expectedDiff = 5;
            assertEquals(expectedDiff, controller.timeDateDifferenceBetweenTwoDates(firstDateAsString, secondDateAsString));
        }

        @Test
        void testTimeDateDifferenceBetweenTwoDates_BothDatesAsString_InvalidFirstDate() {
            String invalidFirstDate = "invalid-date";
            String secondDateAsString = "15-01-2025";
            assertThrows(Exception.class, 
                () -> controller.timeDateDifferenceBetweenTwoDates(invalidFirstDate, secondDateAsString));
        }

        @Test
        void testTimeDateDifferenceBetweenTwoDates_BothDatesAsString_InvalidSecondDate() {
            String firstDateAsString = "10-01-2025";
            String invalidSecondDate = "invalid-date";
            assertThrows(Exception.class, 
                () -> controller.timeDateDifferenceBetweenTwoDates(firstDateAsString, invalidSecondDate));
        }

        @Test
        void testTimeDateDifferenceBetweenTwoDates_BothDatesAsString_NegativeResult() {
            String firstDateAsString = "15-01-2025";
            String secondDateAsString = "10-01-2025";
            int expectedDiff = -5;
            assertEquals(expectedDiff, controller.timeDateDifferenceBetweenTwoDates(firstDateAsString, secondDateAsString));
        }
        
        @Test
        void testHowMuchTimeLeftToReturnABook() {
            String returnDate = LocalDateTime.now().plusDays(5).format(controller.getDateFormatter());
            assertEquals(5, controller.howMuchTimeLeftToReturnABook(returnDate));

            String pastReturnDate = LocalDateTime.now().minusDays(3).format(controller.getDateFormatter());
            assertEquals(-3, controller.howMuchTimeLeftToReturnABook(pastReturnDate));
        }
        
        @Test
        void testIsInTimeWindowToDoAction() {
            String deadline = LocalDateTime.now().plusDays(5).format(controller.getDateFormatter());
            assertTrue(controller.isInTimeWindowToDoAction(deadline, 5));

            String pastDeadline = LocalDateTime.now().minusDays(1).format(controller.getDateFormatter());
            assertFalse(controller.isInTimeWindowToDoAction(pastDeadline, 5));

            String distantFutureDeadline = LocalDateTime.now().plusDays(20).format(controller.getDateFormatter());
            assertFalse(controller.isInTimeWindowToDoAction(distantFutureDeadline, 10));
        }
    }

