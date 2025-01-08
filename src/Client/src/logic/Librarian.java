package logic;

/**
 * Represents a librarian in the library system. A Librarian object contains details such as the librarian's ID and name.
 * <p>This class is used to store and manage information about librarians in the system.</p>
 * 
 * @since 1.0
 */
public class Librarian {

    /** The unique identifier for the librarian. */
    private int librarian_id;

    /** The name of the librarian. */
    private String librarian_name;

    /**
     * Constructs a new Librarian with the specified ID and name.
     * 
     * @param librarian_id the unique identifier of the librarian
     * @param librarian_name the name of the librarian
     */
    public Librarian(int librarian_id, String librarian_name) {
        this.librarian_id = librarian_id;
        this.librarian_name = librarian_name;
    }

    /**
     * Gets the unique identifier of the librarian.
     * 
     * @return the unique identifier of the librarian
     */
    public int getLibrarian_id() {
        return librarian_id;
    }

    /**
     * Sets the unique identifier of the librarian.
     * 
     * @param librarian_id the unique identifier to be set for the librarian
     */
    public void setLibrarian_id(int librarian_id) {
        this.librarian_id = librarian_id;
    }

    /**
     * Gets the name of the librarian.
     * 
     * @return the name of the librarian
     */
    public String getLibrarian_name() {
        return librarian_name;
    }

    /**
     * Sets the name of the librarian.
     * 
     * @param librarian_name the name to be set for the librarian
     */
    public void setLibrarian_name(String librarian_name) {
        this.librarian_name = librarian_name;
    }

    /**
     * Returns a string representation of the librarian, which includes the librarian's ID and name.
     * 
     * @return a string containing the librarian's details
     */
    @Override
    public String toString() {
        return "Librarian [librarian_id=" + librarian_id + ", librarian_name=" + librarian_name + "]";
    }
}
