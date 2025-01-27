package logic;

/**
 * Represents a librarian with an ID and a name.
 * 
 * <p>This class provides methods to get and set the librarian's ID and name, 
 * and overrides the {@code toString()} method for a meaningful string representation.</p>
 */
public class Librarian {

    /**
     * The unique identifier for the librarian.
     */
    private int librarian_id;

    /**
     * The name of the librarian.
     */
    private String librarian_name;

    /**
     * Constructs a new {@code Librarian} object with the specified ID and name.
     * 
     * @param librarian_id   the unique identifier of the librarian
     * @param librarian_name the name of the librarian
     */
    public Librarian(int librarian_id, String librarian_name) {
        this.librarian_id = librarian_id;
        this.librarian_name = librarian_name;
    }

    /**
     * Gets the ID of the librarian.
     * 
     * @return the ID of the librarian
     */
    public int getLibrarian_id() {
        return librarian_id;
    }

    /**
     * Sets the ID of the librarian.
     * 
     * @param librarian_id the new ID of the librarian
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
     * @param librarian_name the new name of the librarian
     */
    public void setLibrarian_name(String librarian_name) {
        this.librarian_name = librarian_name;
    }

    /**
     * Returns a string representation of the librarian.
     * 
     * <p>The string includes the librarian's ID and name in the following format:</p>
     * {@code Librarian [librarian_id=ID, librarian_name=NAME]}
     * 
     * @return a string representation of the librarian
     */
    @Override
    public String toString() {
        return "Librarian [librarian_id=" + librarian_id + ", librarian_name=" + librarian_name + "]";
    }
}
