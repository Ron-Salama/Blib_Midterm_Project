package logic;


/*]
 * Represents a borrowed book in the library system.
 */
public class ReservedBook {

    /** The unique identifier for the reserving record. */
    private int reserveId;
    
    private int subscriberId;

    /** The name of the borrowed book. */
    private String name;

    
    /** The current reserveDate of the borrowed book. */
    private String reserveDate;
    
    /** The time left to retrieve the book. */
    private String timeLeftToRetrieve;
    
    
    private String ISBN;
    
    
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


    public int getReserveId() {
		return reserveId;
	}


	public void setReserveId(int reserveId) {
		this.reserveId = reserveId;
	}


	public int getSubscriberId() {
		return subscriberId;
	}


	public void setSubscriberId(int subscriberId) {
		this.subscriberId = subscriberId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getReserveDate() {
		return reserveDate;
	}


	public void setReserveDate(String reserveDate) {
		this.reserveDate = reserveDate;
	}


	public String getTimeLeftToRetrieve() {
		return timeLeftToRetrieve;
	}


	public void setTimeLeftToRetrieve(String timeLeftToRetrieve) {
		this.timeLeftToRetrieve = timeLeftToRetrieve;
	}


	public String getISBN() {
		return ISBN;
	}


	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}
	
	
	
	public boolean isRetrieved() {
	    return retrieved;
	}

	public void setRetrieved(boolean retrieved) {
	    this.retrieved = retrieved;
	}
	
	@Override
    public String toString() {
        return  ISBN+" "+name+" "+reserveDate+" "+timeLeftToRetrieve;
    }
}
