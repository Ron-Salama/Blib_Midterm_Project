package logic;

/**
 * Represents a book borrowed by a subscriber in the library system.
 * <p>
 * This class holds information about the borrowing record, including unique
 * identifiers, the subscriber who borrowed it, book details, and time information
 * such as borrow date, return date, and the time left to return the book.
 * </p>
 * 
 * @author 
 * @version 1.0
 * @since 2025-01-01
 */
public class BorrowedBook {

    /**
     * The unique identifier for this borrowing record.
     */
    private int borrowId;

    /**
     * The unique identifier (subscriber ID) of the user who borrowed the book.
     */
    private int subscriberId;

    /**
     * The name or title of the borrowed book.
     */
    private String name;

    /**
     * Subject or category of the borrowed book (not fully utilized in this class, 
     * but included if needed for categorization).
     */
    private String subject;  // If used by the system, consider adding getter/setter and doc comments

    /**
     * The return date of the borrowed book as a string (e.g., "dd-MM-yyyy").
     */
    private String returnDate;

    /**
     * The borrow date of the book as a string (e.g., "dd-MM-yyyy").
     */
    private String borrowDate;

    /**
     * The number of days left for the subscriber to return the borrowed book.
     */
    private int timeLeftToReturn;

    /**
     * The ISBN of the borrowed book.
     */
    private String ISBN;

    /**
     * Constructs a new {@code BorrowedBook} with the specified details.
     * <p>
     * Note that {@code subject} is included as a field, but it may not be fully used
     * in this constructor. Modify or remove as needed if the logic changes.
     * </p>
     *
     * @param borrowId        the unique borrowing record ID.
     * @param subscriberId    the subscriber's ID who borrowed the book.
     * @param name            the name (title) of the book.
     * @param borrowDate      the date the book was borrowed.
     * @param returnDate      the date the book is to be returned.
     * @param timeLeftToReturn the time (in days) left to return the book.
     * @param ISBN            the ISBN of the book.
     */
    public BorrowedBook(int borrowId, int subscriberId, String name, String borrowDate, String returnDate,
            int timeLeftToReturn, String ISBN) {
        this.borrowId = borrowId;
        this.subscriberId = subscriberId;
        this.name = name;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.timeLeftToReturn = timeLeftToReturn;
        this.ISBN = ISBN;
    }

    /**
     * Gets the unique borrowing record ID.
     *
     * @return the borrowing record ID.
     */
    public int getBorrowId() {
        return borrowId;
    }

    /**
     * Sets the unique borrowing record ID.
     *
     * @param borrowId the new borrowing record ID.
     */
    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    /**
     * Gets the subscriber ID of the user who borrowed the book.
     *
     * @return the subscriber ID.
     */
    public int getSubscriberId() {
        return subscriberId;
    }

    /**
     * Sets the subscriber ID of the user who borrowed the book.
     *
     * @param subscriberId the new subscriber ID.
     */
    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }

    /**
     * Gets the name (title) of the book.
     *
     * @return the book name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name (title) of the book.
     *
     * @param name the new book name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the subject or category of the book.
     *
     * @return the subject of the book.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject or category of the book.
     *
     * @param subject the new subject of the book.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the return date of the borrowed book.
     *
     * @return the return date, formatted as a string.
     */
    public String getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the return date of the borrowed book.
     *
     * @param returnDate the new return date, formatted as a string.
     */
    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Gets the date when the book was borrowed.
     *
     * @return the borrow date, formatted as a string.
     */
    public String getBorrowDate() {
        return borrowDate;
    }

    /**
     * Sets the date when the book was borrowed.
     *
     * @param borrowDate the new borrow date, formatted as a string.
     */
    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    /**
     * Gets the number of days left to return the book.
     *
     * @return the time left to return the book, in days.
     */
    public int getTimeLeftToReturn() {
        return timeLeftToReturn;
    }

    /**
     * Sets the number of days left to return the book.
     *
     * @param timeLeftToReturn the new time (in days) left.
     */
    public void setTimeLeftToReturn(int timeLeftToReturn) {
        this.timeLeftToReturn = timeLeftToReturn;
    }

    /**
     * Gets the ISBN of the borrowed book.
     *
     * @return the ISBN as a string.
     */
    public String getISBN() {
        return ISBN;
    }

    /**
     * Sets the ISBN of the borrowed book.
     *
     * @param ISBN the new ISBN string.
     */
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    /**
     * Returns a string representation of the borrowed book,
     * including ISBN, name, borrow date, return date, and time left to return.
     *
     * @return a space-separated string of the relevant fields.
     */
    @Override
    public String toString() {
        return ISBN + " " + name + " " + borrowDate + " " + returnDate + " " + timeLeftToReturn;
    }
}
