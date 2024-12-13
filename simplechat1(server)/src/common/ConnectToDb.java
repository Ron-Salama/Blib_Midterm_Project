package common;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectToDb {
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            // Test 1: Fetch all data from the subscriber table
            System.out.println("Fetching all data from the database:");
            List<String> data = fetchAllData(conn);
            if (data.isEmpty()) {
                System.out.println("No data found in the database.");
            } else {
                data.forEach(System.out::println);
            }

            // Test 2: Check if a subscriber exists (replace "1" with an actual subscriber ID)
            System.out.println("\nChecking if subscriber with ID 1 exists:");
            boolean exists = checkSubscriberExists(conn, "1");
            if (exists) {
                System.out.println("Subscriber with ID 1 exists.");
            } else {
                System.out.println("Subscriber with ID 1 does not exist.");
            }

            // Test 3: Try updating a subscriber (replace with actual subscriber ID, phone, and email)
            System.out.println("\nUpdating subscriber with ID 1...");
            updateSubscriber(conn, "1", "1234567890", "Daniel@Gmail.com");

            // Test 4: Try updating a subscriber that does not exist
            System.out.println("\nTrying to update a non-existing subscriber with ID 999...");
            updateSubscriber(conn, "999", "9876543210", "nonexistent@example.com");
         // Test 5: check if id 999 exists 
            if(false==checkSubscriberExists(conn, "999")) {
            	System.out.println("\n subscriber with ID 999 doesnt exist...");
            }

        } catch (SQLException ex) {
            // Handle any SQL errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

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
        return DriverManager.getConnection("jdbc:mysql://localhost/blib?serverTimezone=IST&useSSL=false", "root", "Vdsa0512!");
    }

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
