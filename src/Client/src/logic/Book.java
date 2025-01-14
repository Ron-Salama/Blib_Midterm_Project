package logic;

/**
 * Represents a book in the library system. A Book object contains details such as the book's ID,
 * name, description, subject, available copies, and location.
 * <p>This class is used to store and retrieve information about books in the library.</p>
 */
public class Book {
    
    /** The unique identifier for the book. */
    private int id;
    
    /** The name of the book. */
    private String name;
    
    /** A description of the book. */
    private String description;
    
    /** The subject or category the book belongs to. */
    private String subject;
    
    /** The number of available copies of the book in the library. */
    private int copies;
    
    /** The location of the book in the library. */
    private String location;
    
    /** The location of the book in the library. */
    private int availableCopies;
    
    /** The location of the book in the library. */
    private int reservedCopies;

    /**
     * Constructs a new Book with the specified details.
     * 
     * @param id the unique identifier of the book
     * @param name the name of the book
     * @param description a brief description of the book
     * @param subject the subject or category of the book
     * @param availableCopies the number of available copies of the book
     * @param location the location of the book in the library
     */
    public Book(int id, String name, String description, String subject, int copies, String location , int availableCopies, int reservedCopies) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.subject = subject;
        this.copies = availableCopies;
        this.location = location;
        this.availableCopies = availableCopies;
        this.reservedCopies = reservedCopies;
    }

    /**
     * Gets the unique identifier of the book.
     * 
     * @return the unique identifier of the book
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the book.
     * 
     * @return the name of the book
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the book.
     * 
     * @return the description of the book
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the subject or category of the book.
     * 
     * @return the subject of the book
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the number of available copies of the book.
     * 
     * @return the number of available copies
     */
    public int getCopies() {
        return copies;
    }

    /**
     * Gets the location of the book in the library.
     * 
     * @return the location of the book
     */
    public String getLocation() {
        return location;
    }
    
    
    public int getAvailableCopies() {
		return availableCopies;
	}
    
    
    public int getReservedCopies() {
		return reservedCopies;
	}

    /**
     * Returns a string representation of the book, which includes all its details.
     * 
     * @return a string containing the book's details
     */
    @Override
    public String toString() {
        return "Book{id=" + id + ", name='" + name + "', description='" + description + "', subject='" + subject + "', Copies=" + copies + ", location='" + location + "', availableCopies=" + availableCopies + ", reservedCopies=" + reservedCopies + "}";
    }



	
}
