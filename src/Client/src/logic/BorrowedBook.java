package logic;


/*]
 * Represents a borrowed book in the library system.
 */
public class BorrowedBook {

    /** The unique identifier for the borrowing record. */
    private int borrowId;
    
    private int subscriberId;

    /** The name of the borrowed book. */
    private String name;

    /** The subject or category of the borrowed book. */
    private String subject;
    
    /** The current returnDate of the borrowed book. */
    private String returnDate;
    
    /** The current borrowDate of the borrowed book. */
    private String borrowDate;
    
    /** The time left to return the book. */
    private int timeLeftToReturn;
    
    private String ISBN;

    /**
     * Constructs a new BorrowedBook with the specified details.
     * 
     * @param borrowId the unique identifier of the borrowing record
     * @param name the name of the book
     * @param subject the subject or category of the book
     * @param timeLeftToReturn the time left to return the book, in days
     */
    public BorrowedBook(int borrowId, int subscriberId, String name, String borrowDate, String returnDate,int timeLeftToReturn, String ISBN) {
        this.borrowId = borrowId;
        this.subscriberId = subscriberId;
        this.name = name;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.ISBN = ISBN;
        this.timeLeftToReturn=timeLeftToReturn;
    }

    // Getters and setters
    public int getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }
    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setReturnDate(String returnDate) {
    	this.returnDate = returnDate;
    }
    
    public String getReturnDate() {
    	return returnDate;
    }
    
    public void setBorrowDate(String borrowDate) {
    	this.borrowDate = borrowDate;
    }
    
    public String getBorrowDate() {
    	return borrowDate;
    }
    	
    public int getTimeLeftToReturn() {
        return timeLeftToReturn;
    }

    public void setTimeLeftToReturn(int timeLeftToReturn) {
        this.timeLeftToReturn = timeLeftToReturn;
    }

    @Override
    public String toString() {
        return  ISBN+" "+name+" "+borrowDate+" "+returnDate+" "+timeLeftToReturn;
    }
}
