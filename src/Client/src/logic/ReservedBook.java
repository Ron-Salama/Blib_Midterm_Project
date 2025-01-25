package logic;

/**
 * Represents a reserved book in the library system.
 * <p>This class stores details of a book that has been reserved by a subscriber and includes information
 * such as the reserve ID, subscriber ID, book name, reserve date, time left to retrieve the book, and the book's ISBN.</p>
 */
public class ReservedBook {
    /** The unique identifier for the reserving record. */
    private int reserveId;
    
    /** The unique identifier for the subscriber who reserved the book. */
    private int subscriberId;

    /** The name of the borrowed book. */
    private String name;

    /** The current reserveDate of the borrowed book. */
    private String reserveDate;
    
    /** The time left to retrieve the book. */
    private String timeLeftToRetrieve;
    
    /** The ISBN of the reserved book. */
    private String ISBN;
    
    /** The flag indicating whether the reserved book has been retrieved. */
    private boolean retrieved = false;

    /**
     * Constructs a new BorrowedBook with the specified details.
     * 
     * @param borrowId the unique identifier of the borrowing record
     * @param name the name of the book
     * @param subject the subject or category of the book
     * @param timeLeftToReturn the time left to return the book, in days
     * @return 
     */
    public ReservedBook(int reserveId, int subscriberId, String name, String reserveDate, String timeLeftToRetrieve, String ISBN) {
        this.reserveId = reserveId;
        this.subscriberId = subscriberId;
        this.name = name;
        this.reserveDate = reserveDate;
        this.timeLeftToRetrieve = timeLeftToRetrieve;
        this.ISBN = ISBN;
    }

    /**
     * Gets the unique identifier for the reserve record.
     * 
     * @return the reserve ID
     */
    public int getReserveId() {
		return reserveId;
	}

    /**
     * Sets the unique identifier for the reserve record.
     * 
     * @param reserveId the reserve ID to set
     */
	public void setReserveId(int reserveId) {
		this.reserveId = reserveId;
	}

	/**
     * Gets the unique identifier for the subscriber who reserved the book.
     * 
     * @return the subscriber ID
     */
	public int getSubscriberId() {
		return subscriberId;
	}

	/**
     * Sets the unique identifier for the subscriber who reserved the book.
     * 
     * @param subscriberId the subscriber ID to set
     */
	public void setSubscriberId(int subscriberId) {
		this.subscriberId = subscriberId;
	}

	/**
     * Gets the name of the reserved book.
     * 
     * @return the name of the reserved book
     */
	public String getName() {
		return name;
	}

    /**
     * Sets the name of the reserved book.
     * 
     * @param name the name of the reserved book
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * Gets the date when the book was reserved.
     * 
     * @return the reserve date of the book
     */
	public String getReserveDate() {
		return reserveDate;
	}

	/**
     * Sets the date when the book was reserved.
     * 
     * @param reserveDate the reserve date to set
     */
	public void setReserveDate(String reserveDate) {
		this.reserveDate = reserveDate;
	}

    /**
     * Gets the time left to retrieve the reserved book.
     * 
     * @return the time left to retrieve the book
     */
	public String getTimeLeftToRetrieve() {
		return timeLeftToRetrieve;
	}

    /**
     * Sets the time left to retrieve the reserved book.
     * 
     * @param timeLeftToRetrieve the time left to retrieve the book
     */
	public void setTimeLeftToRetrieve(String timeLeftToRetrieve) {
		this.timeLeftToRetrieve = timeLeftToRetrieve;
	}

	/**
     * Gets the ISBN of the reserved book.
     * 
     * @return the ISBN of the reserved book
     */
	public String getISBN() {
		return ISBN;
	}

	/**
     * Sets the ISBN of the reserved book.
     * 
     * @param ISBN the ISBN of the reserved book
     */
	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}
	
    /**
     * Checks whether the reserved book has been retrieved.
     * 
     * @return true if the book has been retrieved, false otherwise
     */
	public boolean isRetrieved() {
	    return retrieved;
	}

	/**
     * Sets whether the reserved book has been retrieved.
     * 
     * @param retrieved true if the book has been retrieved, false otherwise
     */
	public void setRetrieved(boolean retrieved) {
	    this.retrieved = retrieved;
	}
	
	/**
     * Returns a string representation of the reserved book, which includes the book's ISBN, name, reserve date,
     * and time left to retrieve the book.
     * 
     * @return a string containing the reserved book's details
     */
	@Override
    public String toString() {
        return  ISBN + " " + name + " " + reserveDate + " " + timeLeftToRetrieve;
    }
}
