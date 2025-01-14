package logic;


/*
 * Represents a borrowed book in the library system.
 */
public class BorrowedBook {

    /** The unique identifier for the borrowing record. */
    private int borrowId;

    /** The name of the borrowed book. */
    private String name;

    /** The subject or category of the borrowed book. */
    private String subject;
    
    /** The current returnDate of the borrowed book. */
    private String returnDate;
    
    /** The current borrowDate of the borrowed book. */
    private String borrowedDate;
    
    /** The time left to return the book. */
    private int timeLeftToReturn;

    /**
     * Constructs a new BorrowedBook with the specified details.
     * 
     * @param borrowId the unique identifier of the borrowing record
     * @param name the name of the book
     * @param subject the subject or category of the book
     * @param timeLeftToReturn the time left to return the book, in days
     */
    public BorrowedBook(int borrowId, String name, String subject, int timeLeftToReturn) {
        this.borrowId = borrowId;
        this.name = name;
        this.subject = subject;
        this.timeLeftToReturn = timeLeftToReturn;
    }

    // Getters and setters
    public int getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getTimeLeftToReturn() {
        return timeLeftToReturn;
    }

    public void setTimeLeftToReturn(int timeLeftToReturn) {
        this.timeLeftToReturn = timeLeftToReturn;
    }

    @Override
    public String toString() {
        return  borrowId+" "+name+" "+subject+" "+timeLeftToReturn;
    }
}
