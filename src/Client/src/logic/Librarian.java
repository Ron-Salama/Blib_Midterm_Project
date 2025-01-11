package logic;


public class Librarian {


    private int librarian_id;
    private String librarian_name;


    public Librarian(int librarian_id, String librarian_name) {
        this.librarian_id = librarian_id;
        this.librarian_name = librarian_name;
    }


    public int getLibrarian_id() {
        return librarian_id;
    }


    public void setLibrarian_id(int librarian_id) {
        this.librarian_id = librarian_id;
    }


    public String getLibrarian_name() {
        return librarian_name;
    }

    public void setLibrarian_name(String librarian_name) {
        this.librarian_name = librarian_name;
    }


    @Override
    public String toString() {
        return "Librarian [librarian_id=" + librarian_id + ", librarian_name=" + librarian_name + "]";
    }
}
