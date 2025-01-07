package logic;

public class Librarian {

    private int librarian_id;  // Corrected spelling to 'librarian_id'
    private String librarian_name;  // Corrected spelling to 'librarian_name'

    // Constructor to initialize librarian_id and librarian_name
    public Librarian(int librarian_id, String librarian_name) {
        this.librarian_id = librarian_id;
        this.librarian_name = librarian_name;
    }

    // Getter and setter methods for librarian_id
    public int getLibrarian_id() {
        return librarian_id;
    }
    public void setLibrarian_id(int librarian_id) {
        this.librarian_id = librarian_id;
    }

    // Getter and setter methods for librarian_name
    public String getLibrarian_name() {
        return librarian_name;
    }
    public void setLibrarian_name(String librarian_name) {
        this.librarian_name = librarian_name;
    }

    // Overriding the toString() method to represent librarian as a string
    @Override
    public String toString() {
        return "Librarian [librarian_id=" + librarian_id + ", librarian_name=" + librarian_name + "]";
    }
}
