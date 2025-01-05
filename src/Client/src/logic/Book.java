package logic;


public class Book {
    private int id;
    private String name;
    private String description;
    private String subject;
    private int availableCopies;
    private String location;

    // Constructor with parameters matching the types you're passing
    public Book(int id, String name, String description, String subject, int availableCopies, String location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.subject = subject;
        this.availableCopies = availableCopies;
        this.location = location;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSubject() {
        return subject;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public String getLocation() {
        return location;
    }

    // You can override toString() for easy printing if needed
    @Override
    public String toString() {
        return "Book{id=" + id + ", name='" + name + "', description='" + description + "', subject='" + subject + "', availableCopies=" + availableCopies + ", location='" + location + "'}";
    }
}

