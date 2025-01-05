package common;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ConnectToDb {
    // Method to establish a connection to the database
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            System.out.println("Driver definition succeed");
        } catch (Exception ex) {
            System.out.println("Driver definition failed");
            throw new SQLException("Driver loading failed", ex);
        }

        // Return the established connection
        return DriverManager.getConnection("jdbc:mysql://localhost/blib?serverTimezone=IST&allowPublicKeyRetrieval=true&useSSL=false", "root", "Vdsa0512!");
    }
    /*
        // Method to fetch all data from the subscriber table
        public static List<String> fetchAllData(Connection conn) {
            List<String> result = new ArrayList<>();
            String query = "SELECT * FROM subscriber";

            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                // Process each row in the result set
                while (rs.next()) {
                    int id = rs.getInt("subscriber_id");
                    String name = rs.getString("subscriber_name");
                    int detailed = rs.getInt("detailed_subscription_history");
                    String phone = rs.getString("subscriber_phone_number");
                    String email = rs.getString("subscriber_email");

                    String row = "subscriber_id:" + id + ", subscriber_name:" + name +
                                 ", detailed_subscription_history:" + detailed +
                                 ", subscriber_phone_number:" + phone +
                                 ", subscriber_email:" + email;

                    result.add(row);
                }
            } catch (SQLException e) {
                System.out.println("Error while fetching data: " + e.getMessage());
            }

            return result; // Return all data as a list of strings
        }
    */
    // Method to fetch data for a specific subscriber based on their ID
    public static String fetchSubscriberData(Connection conn, String subscriberId) {
        String query = "SELECT * FROM subscriber WHERE subscriber_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(subscriberId));

            try (ResultSet rs = pstmt.executeQuery()) {
                // Check if a record exists
                if (rs.next()) {
                    int id = rs.getInt("subscriber_id");
                    String name = rs.getString("subscriber_name");
                    int detailed = rs.getInt("detailed_subscription_history");
                    String phone = rs.getString("subscriber_phone_number");
                    String email = rs.getString("subscriber_email");

                    return "subscriber_id:" + id + ", subscriber_name:" + name +
                            ", detailed_subscription_history:" + detailed +
                            ", subscriber_phone_number:" + phone +
                            ", subscriber_email:" + email;
                } else {
                    return "No subscriber found";
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching subscriber data: " + e.getMessage());
            return "Error fetching subscriber data.";
        }
    }
    
    public static List<String> fetchBooksData(Connection conn) {
        String query = "SELECT * FROM blib.books"; // Adjust the query as needed

        List<String> booksList = new ArrayList<>(); // Initialize the list to store books as strings

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Check if ResultSet has any rows
            if (!rs.next()) {
                System.out.println("No data found in books table.");
                return booksList;  // Return empty list if no rows are found
            }

            // Loop through the result set and convert each row into a string
            do {
                int id = rs.getInt("ISBN");
                String name = rs.getString("Name");
                String subject = rs.getString("Subject");
                String description = rs.getString("ShortDescription");
                int availableCopies = rs.getInt("NumCopies");
                String location = rs.getString("ShelfLocation");

                // Handle potential null values
                if (name == null) name = "Unknown";
                if (subject == null) subject = "N/A";
                if (description == null) description = "No description available";
                if (location == null) location = "Unknown location";

                // Format the book data into a single string (e.g., CSV format)
                String bookData = id + "," + name + "," + subject + "," + description + "," 
                                  + availableCopies + "," + location;

                booksList.add(bookData); // Add the formatted string to the list
            } while (rs.next());

        } catch (SQLException e) {
            e.printStackTrace();  // This will show the full exception stack trace
            System.out.println("Error while fetching books data: " + e.getMessage());
            return null; // Return null in case of an error (or handle this as needed)
        }

        return booksList;  // Return the list of books as strings
    }

    // Check if a subscriber exists based on their ID(PK)
    public static boolean checkSubscriberExists(Connection conn, String subscriberId) throws SQLException {
        String query = "SELECT * FROM subscriber WHERE subscriber_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(subscriberId));
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();  // Returns true if subscriber exists, otherwise false
            }
        }
    }

    // Update a subscriber's phone and email
    public static void updateSubscriber(Connection conn, String subscriberId, String phone, String email) throws SQLException {
        String query = "UPDATE subscriber SET subscriber_phone_number = ?, subscriber_email = ? WHERE subscriber_id = ?";

        // Debug log to check inputs
        System.out.println("Updating subscriber: " + subscriberId + " with phone: " + phone + " and email: " + email);

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, email);
            pstmt.setInt(3, Integer.parseInt(subscriberId));

            // Execute update and check the number of affected rows
            int affectedRows = pstmt.executeUpdate();

            // Debugging: Check if rows were affected
            if (affectedRows > 0) {
                System.out.println("Update successful: " + affectedRows + " row(s) affected.");
            } else {
                System.out.println("Update failed: No rows updated. Subscriber ID might not exist.");
            }
        }
    }
}
