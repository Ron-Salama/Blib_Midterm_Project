package common;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import server.EchoServer;
//import org.omg.CORBA.Request;


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
        return DriverManager.getConnection("jdbc:mysql://localhost/blib?serverTimezone=IST&allowPublicKeyRetrieval=true&useSSL=false", "root", "Aa123456");
    }
    public static List<String> fetchReturnDates(Connection dbConnection, String isbn) throws SQLException {
        List<String> returnDates = new ArrayList<>();
        String query = "SELECT return_date FROM borrow_table WHERE isbn = ?";
        
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String returnDate = rs.getString("return_date");
                returnDates.add(returnDate);
            }
        }
        
        return returnDates;
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
                    String status = rs.getString("status");
                    
                    String row = "subscriber_id:" + id + ", subscriber_name:" + name +
                                 ", detailed_subscription_history:" + detailed +
                                 ", subscriber_phone_number:" + phone +
                                 ", subscriber_email:" + email +
                                 ", status:" + status;

                    result.add(row);
                }
            } catch (SQLException e) {
                System.out.println("Error while fetching data: " + e.getMessage());
            }

            return result; // Return all data as a list of strings
        }
        
        public static List<String> fetchAllFrozenDataForReports(Connection conn) {
            List<String> result = new ArrayList<>();
            String query = "SELECT * FROM databydate"; // Query updated to fetch from 'databydate' table

            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                StringBuilder fullResult = new StringBuilder();

                // Process each row in the result set
                while (rs.next()) {
                    Date id = rs.getDate("idDataByDate");  // Fetch idDataByDate
                    int frozen = rs.getInt("Frozen");    // Fetch Frozen
                    int notFrozen = rs.getInt("NotFrozen"); // Fetch NotFrozen
                    int borrowedBooks = rs.getInt("BorrowedBooks"); // Fetch BorrowedBooks
                    int late = rs.getInt("Late");        // Fetch Late

                    // Build the row string and append to the full result
                    fullResult.append(id).append(",")
                              .append(frozen).append(",")
                              .append(notFrozen).append(",")
                              .append(borrowedBooks).append(",")
                              .append(late).append(";");

                    // Add a newline for readability (optional)
                    // fullResult.append("\n"); // Uncomment if you want rows on separate lines
                }

                // Add the final result to the list
                result.add(fullResult.toString());

            } catch (SQLException e) {
                System.out.println("Error while fetching data: " + e.getMessage());
            }
            return result; // Return all data as a list of strings
        }


        public static List<String> fetchAllDataForReports(Connection conn) {
            List<String> result = new ArrayList<>();
            String query = "SELECT * FROM subscriber";

            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                StringBuilder fullResult = new StringBuilder();

                // Process each row in the result set
                while (rs.next()) {
                    int id = rs.getInt("subscriber_id");
                    String name = rs.getString("subscriber_name");
                    int detailed = rs.getInt("detailed_subscription_history");
                    String phone = rs.getString("subscriber_phone_number");
                    String email = rs.getString("subscriber_email");
                    String status = rs.getString("status");

                    // Build the row string and append to the full result
                    fullResult.append(id).append(",")
                              .append(name).append(",")
                              .append(detailed).append(",")
                              .append(phone).append(",")
                              .append(email).append(",")
                              .append(status).append(";");

                    // Add a newline for readability (optional)
                    // fullResult.append("\n"); // Uncomment if you want rows on separate lines
                }

                // Add the final result to the list
                result.add(fullResult.toString());

            } catch (SQLException e) {
                System.out.println("Error while fetching data: " + e.getMessage());
            }
            return result; // Return all data as a list of strings
        }


    // Method to return the book by removing it from the borrowed_books table
    public static String returnbook(Connection dbConnection, String subscriberId, String bookID) {
        String result = "Book return failed"; // Default return status

        // SQL query to delete the book from the borrowed_books table based on subscriber_id and book_name
        String sql = "DELETE FROM borrowed_books WHERE subscriber_id = ? AND ISBN = ?";

        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, subscriberId); // Set the subscriber ID
            stmt.setString(2, bookID); // Set the book name
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                result = "Book returned successfully"; // Success message
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database exceptions
            result = "Error while returning book";
        }

        return result; // Return the status message
    }
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
                    String status = rs.getString("status");
                    
                    return "subscriber_id:" + id + ", subscriber_name:" + name +
                            ", detailed_subscription_history:" + detailed +
                            ", subscriber_phone_number:" + phone +
                            ", subscriber_email:" + email +
                            ", status:" + status;
                    
                } else {
                    return "No subscriber found";
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching subscriber data: " + e.getMessage());
            return "Error fetching subscriber data.";
        }
    }
    public static String fetchLibrarianData(Connection conn, String librarianId) {
        String query = "SELECT * FROM librarian WHERE librarian_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(librarianId));

            try (ResultSet rs = pstmt.executeQuery()) {
                // Check if a record exists
                if (rs.next()) {
                    int id = rs.getInt("librarian_id");
                    String name = rs.getString("librarian_name");

                    return "librarian_id:" + id + ", librarian_name:" + name;

                } else {
                    return "No labrarian found";
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching subscriber data: " + e.getMessage());
            return "Error fetching subscriber data.";
        }
    }
    
    
    public static String fetchHistoryData(Connection conn, String subscriberId) {
    	String historyData = "";
        String query = "SELECT history FROM detailed_subscription_history WHERE detailed_subscription_history = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, subscriberId);  // Bind the subscriberId parameter
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                historyData = rs.getString("history");  // Corrected column name to "history"
            }

        } catch (SQLException e) {
            System.out.println("Error fetching history data: " + e.getMessage());
        }

        return historyData;
    }
    
    
    /**
     * Registers a new subscriber in the database and inserts their ID into the detailed_subscription_history table.
     *
     * @param conn  Database connection object.
     * @param body  Comma-separated string: "subscriberName,SubscriberID,PhoneNumber,Email".
     * @return      "True" if both insertions are successful, "False" otherwise.
     */
    public static String updateSubscriberDB(Connection conn, String body) {
        System.out.println("Starting subscriber registration.");
        // Split the input body into parts
        String[] details = body.split(",");

        // Validate input format
        if (details.length != 4) {
            throw new IllegalArgumentException("Invalid input format. Expected: subscriberName,SubscriberID,PhoneNumber,Email");
        }
        
        // Extract subscriber details
        String subscriberName = details[0].trim();
        String subscriberId = details[1].trim();
        String phoneNumber = details[2].trim();
        String email = details[3].trim();
        String status = "Not Frozen";  // Default status

        // SQL query to check if subscriber ID exists in detailed_subscription_history
        String checkHistorySql = "SELECT 1 FROM detailed_subscription_history WHERE detailed_subscription_history = ?";

        // SQL query to insert subscriber ID into detailed_subscription_history
        String insertHistorySql = "INSERT INTO detailed_subscription_history (detailed_subscription_history, history) VALUES (?, ?)";

        // SQL query to insert a new subscriber
        String insertSubscriberSql = "INSERT INTO subscriber (subscriber_id, subscriber_name, detailed_subscription_history, subscriber_phone_number, subscriber_email, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement checkHistoryStmt = conn.prepareStatement(checkHistorySql)) {
            // Check if the subscriber ID already exists in detailed_subscription_history
            checkHistoryStmt.setString(1, subscriberId);
            try (ResultSet rs = checkHistoryStmt.executeQuery()) {
                if (!rs.next()) {
                    // Insert subscriber ID into detailed_subscription_history only if it doesn't exist
                    try (PreparedStatement insertHistoryStmt = conn.prepareStatement(insertHistorySql)) {
                        insertHistoryStmt.setString(1, subscriberId);
                        insertHistoryStmt.setString(2, ""); // Initialize history as empty

                        insertHistoryStmt.executeUpdate();
                        System.out.println("Subscriber ID inserted into detailed_subscription_history for ID: " + subscriberId);
                    }
                } else {
                    System.out.println("Subscriber ID already exists in detailed_subscription_history: " + subscriberId);
                }
            }

            // Insert subscriber details into subscriber table
            try (PreparedStatement insertSubscriberStmt = conn.prepareStatement(insertSubscriberSql)) {
                insertSubscriberStmt.setString(1, subscriberId);
                insertSubscriberStmt.setString(2, subscriberName);
                insertSubscriberStmt.setString(3, subscriberId);
                insertSubscriberStmt.setString(4, phoneNumber);
                insertSubscriberStmt.setString(5, email);
                insertSubscriberStmt.setString(6, status);

                int insertSubscriberRows = insertSubscriberStmt.executeUpdate();
                if (insertSubscriberRows > 0) {
                    System.out.println("New subscriber inserted successfully with ID: " + subscriberId);
                    return "True";
                } else {
                    System.out.println("Failed to insert new subscriber with ID: " + subscriberId);
                    return "False";
                }
            }
        } catch (SQLException e) {
            // Handle SQL errors
            System.err.println("Error inserting subscriber data: " + e.getMessage());
            return "False";
        }
    }







    
    
    
    
    public static List<String> fetchBorrowedBooksBySubscriberId(Connection conn, String subscriberId) {
        String query = "SELECT * FROM blib.borrowed_books WHERE subscriber_id = ?";

        List<String> borrowedBooks = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, subscriberId); // Set the subscriber_id parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Combine fields into a single delimited string for sending to the client
                    String bookData = rs.getInt("borrow_id") + "," +
                    				  rs.getInt("subscriber_id") + "," +
                                      rs.getString("Name") + "," +
                                      rs.getString("Borrowed_Time") + "," +
                                      rs.getString("Return_Time") + "," +
                                      rs.getString("ISBN");
                    borrowedBooks.add(bookData);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching borrowed books: " + e.getMessage());
        }

        return borrowedBooks; // Return the list of borrowed books
    }
    
    
  
    
    public static List<String> fetchReservedBooksBySubscriberId(Connection conn, String subscriberId) {
        String query = "SELECT * FROM blib.reserved_books WHERE subscriber_id = ?";

        List<String> reservedBooks = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, subscriberId); // Set the subscriber_id parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Combine fields into a single delimited string for sending to the client
                    String bookData = rs.getInt("reserve_id") + "," +
                    				  rs.getInt("subscriber_id") + "," +
                                      rs.getString("name") + "," +
                                      rs.getString("reserve_time") + "," +
                                      rs.getString("time_left_to_retrieve") + "," +
                                      rs.getString("ISBN");
                    reservedBooks.add(bookData);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching borrowed books: " + e.getMessage());
        }

        return reservedBooks; // Return the list of reserved books
    }    
    
    public static boolean checkIfrequestexists(Connection dbConnection, String RegisterId) throws SQLException {
        String query = "SELECT COUNT(*) FROM requests WHERE RequestedById = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, RegisterId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0; // If count > 0, the ID exists
            }
        }
        return false; // Default to false if no records are found
    }

    public static List<String> fetchBooksData(Connection conn) {
        String query = "SELECT * FROM blib.books"; // Adjust the query as needed

        List<String> booksList = new ArrayList<>(); // Initialize the list to store books as strings

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Check if ResultSet has any rows
            if (!rs.next()) {
                System.out.println("No data found in books table.");
                return booksList; // Return empty list if no rows are found
            }

            // Loop through the result set and convert each row into a string
            do {
                String id = rs.getString("ISBN"); // Treat ISBN as a String
                String name = rs.getString("Name");
                String subject = rs.getString("Subject");
                String description = rs.getString("ShortDescription");
                int copies = rs.getInt("NumCopies");
                String location = rs.getString("ShelfLocation");
                int availableCopies = rs.getInt("AvailableCopiesNum");
                int reservedCopies = rs.getInt("ReservedCopiesNum");
                


                // Handle potential null values
                if (id == null) id = "Unknown ID";
                if (name == null) name = "Unknown";
                if (subject == null) subject = "N/A";
                if (description == null) description = "No description available";
                if (location == null) location = "Unknown location";


                // Format the book data into a single string (e.g., CSV format)
                String bookData = id + "," + name + "," + subject + "," + description + ","
                                  + copies + "," + location + "," + availableCopies + "," + reservedCopies;

                booksList.add(bookData); // Add the formatted string to the list
            } while (rs.next());

        } catch (SQLException e) {
            e.printStackTrace(); // This will show the full exception stack trace
            System.out.println("Error while fetching books data: " + e.getMessage());
            return null; // Return null in case of an error (or handle this as needed)
        }

        return booksList; // Return the list of books as strings
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
    public static boolean checkLibrarianExists(Connection conn, String librarianId) throws SQLException {
        String query = "SELECT * FROM librarian WHERE librarian_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(librarianId));
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
    
    public static String fetchBookInfo(Connection conn, String bookId) {
        String query = "SELECT * FROM books WHERE ISBN = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bookId); // Use setString

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("ISBN"); // Get ID as String
                    String name = rs.getString("Name");
                    String subject = rs.getString("Subject");
                    String description = rs.getString("ShortDescription");
                    int copies = rs.getInt("NumCopies");
                    String location = rs.getString("ShelfLocation");
                    int availableCopies = rs.getInt("AvailableCopiesNum");
                    int reservedCopies = rs.getInt("ReservedCopiesNum");

                    name = (name == null) ? "Unknown" : name;
                    subject = (subject == null) ? "N/A" : subject;
                    description = (description == null) ? "No description available" : description;
                    location = (location == null) ? "Unknown location" : location;

                    return String.format(
                            "%s,%s,%s,%s,%d,%s,%d,%d",
                            id, name, subject, description, copies, location, availableCopies, reservedCopies);
                } else {
                    return "No book found";
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching book info: " + e.getMessage());
            return "Error fetching book info.";
        }
    }
    public static void insertRequest(Connection conn, String requestType, String requestedByID, String requestedByName,
            String bookName, String bookId, String borrowTime, String returnTime, String extendTime)
            throws SQLException {

    // SQL query to insert a new record into the requests table
    String query = "INSERT INTO requests (requestType, requestedByID, requestedByName, bookName, bookId, borrowTime, returnTime, extendTime) "
                 + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        // Set the values for each field in the query
        pstmt.setString(1, requestType);
        pstmt.setString(2, requestedByID);
        pstmt.setString(3, requestedByName);
        pstmt.setString(4, bookName);
        pstmt.setString(5, bookId);
        pstmt.setString(6, (borrowTime != null && !borrowTime.isEmpty()) ? borrowTime : "temp");
        pstmt.setString(7, (returnTime != null && !returnTime.isEmpty()) ? returnTime : "temp");
        pstmt.setString(8, (extendTime != null && !extendTime.isEmpty()) ? extendTime : "temp");

        // Execute the insert and get the number of affected rows
        int affectedRows = pstmt.executeUpdate();

        // Debugging: Check if rows were inserted
        if (affectedRows > 0) {
            System.out.println("Insert successful: " + affectedRows + " row(s) inserted.");
        } else {
            System.out.println("Insert failed: No rows inserted.");
        }
    }
}
    
    
    //**********************************************************************************************************
    //**********************************************************************************************************
    //**********************************************************************************************************
    //**********************************************************************************************************
    //**********************************************************************************************************
    //**********************************************************************************************************

    public static void insertReservedBook(Connection conn, String subscriber_id,
            String bookName, String reserveTime, String BookId)
            throws SQLException {

    // SQL query to insert a new record into the reserved_books table without reserveId
    String query = "INSERT INTO reserved_books (subscriber_id, name, reserve_time, ISBN) "
                 + "VALUES (?, ?, ?, ?)";

    try (PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
        // Set the values for each field in the query
        pstmt.setString(1, subscriber_id);
        pstmt.setString(2, bookName);
        pstmt.setString(3, reserveTime);
        pstmt.setString(4, BookId);

        // Execute the insert and get the number of affected rows]
        int affectedRows = pstmt.executeUpdate();

        // Debugging: Check if rows were inserted
        if (affectedRows > 0) {
            // Retrieve the generated reserve_id
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int reserveId = generatedKeys.getInt(1); // The generated reserve_id
                    System.out.println("Insert successful, generated reserveId: " + reserveId);
                } else {
                    System.out.println("Insert failed: No generated keys returned.");
                }
            }
        } else {
            System.out.println("Insert failed: No rows inserted.");
        }
    }
}

    
    //**********************************************************************************************************
    //**********************************************************************************************************
    //**********************************************************************************************************
    //**********************************************************************************************************
    //**********************************************************************************************************
    //**********************************************************************************************************
    public static String fetchBorrowRequest(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder();

        // SQL query to fetch Borrow request based on the requestedByID and bookId
        String query = "SELECT * FROM requests WHERE requestType = 'Borrow For Subscriber'";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Execute the query and get the result set
            try (ResultSet rs = pstmt.executeQuery()) {
                // Process each result
                while (rs.next()) {
                    // Concatenate the fields with commas
                    result.append(rs.getString("requestType")).append(",")
                          .append(rs.getString("requestedByID")).append(",")
                          .append(rs.getString("requestedByName")).append(",")
                          .append(rs.getString("bookName")).append(",")
                          .append(rs.getString("bookId")).append(",")
                          .append(rs.getString("borrowTime")).append(",")
                          .append(rs.getString("returnTime")).append(",")
                          .append(rs.getString("extendTime"));

                    // Append a semicolon to separate each request
                    result.append(";");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }

        // Remove the trailing semicolon if there are any results
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }
    
    public static void decreaseAvaliabeNumCopies(Connection conn, String bookId) throws SQLException {
        // SQL query to decrease NumCopies by 1 for the given bookId
        String query = "UPDATE books SET AvailableCopiesNum = AvailableCopiesNum - 1 WHERE ISBN = ? AND NumCopies > 0";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Set the bookId parameter
            pstmt.setString(1, bookId);

            // Execute the update statement
            int affectedRows = pstmt.executeUpdate();

            // If no rows were updated, it means there are no copies left or the bookId does not exist
            if (affectedRows == 0) {
                System.out.println("No copies available or invalid bookId: " + bookId);
            } else {
                System.out.println("Successfully decreased AvailableCopiesNum for bookId: " + bookId);
            }
        }
    }
    
    
    
    
    
  	 //************************************************************************************
  	 //************************************************************************************
  	 //************************************************************************************
  	 //************************************************************************************
  	 //************************************************************************************

   	 //@SuppressWarnings("unused")
	public static void incrementReservedCopiesNum(Connection conn, String bookId) throws SQLException {
        // SQL query to increment ReservedCopiesNum by 1 for the given bookId
        String query = "UPDATE books SET ReservedCopiesNum = ReservedCopiesNum + 1 WHERE ISBN = ? AND ReservedCopiesNum >= 0";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Set the bookId parameter
            pstmt.setString(1, bookId);

            // Execute the update statement
            int affectedRows = pstmt.executeUpdate();

            // If no rows were updated, it means there are no copies left or the bookId does not exist
            if (affectedRows == 0) {
                System.out.println("invalid bookId: " + bookId);
            } else {
                System.out.println("Successfully incremented ReservedCopesNum for bookId: " + bookId);
            }
        }
    }
   	 
   	 
   	 
   	 //************************************************************************************
   	 //************************************************************************************
   	 //************************************************************************************
   	 //************************************************************************************
   	 //************************************************************************************
	
	
	//************************************************************************************
 	 //************************************************************************************
 	 //************************************************************************************
 	 //************************************************************************************
 	 //************************************************************************************

  	 //@SuppressWarnings("unused")
	public static void decreaseReservedCopiesNum(Connection conn, String bookId) throws SQLException {
	    // SQL query to decrement ReservedCopiesNum by 1 for the given bookId
	    String query = "UPDATE books SET ReservedCopiesNum = ReservedCopiesNum - 1 WHERE ISBN = ? AND ReservedCopiesNum > 0";

	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        // Set the bookId parameter
	        pstmt.setString(1, bookId);

	        // Execute the update statement
	        int affectedRows = pstmt.executeUpdate();

	        // If no rows were updated, it means there are no copies left or the bookId does not exist
	        if (affectedRows == 0) {
	            System.out.println("Invalid bookId: " + bookId + " or no reserved copies left.");
	        } else {
	            System.out.println("Successfully decremented ReservedCopiesNum for bookId: " + bookId);
	        }
	    }
	}
  	 
  	 
  	 
  	 //************************************************************************************
  	 //************************************************************************************
  	 //************************************************************************************
  	 //************************************************************************************
  	 //************************************************************************************

    public static String fetchReturnRequest(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder();

        // SQL query to fetch Return request based on the requestedByID and bookId
        String query = "SELECT * FROM requests WHERE requestType = 'Return For Subscriber'";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Execute the query and get the result set
            try (ResultSet rs = pstmt.executeQuery()) {
                // Process each result
                while (rs.next()) {
                    // Concatenate the fields with commas
                    result.append(rs.getString("requestType")).append(",")
                          .append(rs.getString("requestedByID")).append(",")
                          .append(rs.getString("requestedByName")).append(",")
                          .append(rs.getString("bookName")).append(",")
                          .append(rs.getString("bookId")).append(",")
                          .append(rs.getString("borrowTime")).append(",")
                          .append(rs.getString("returnTime")).append(",")
                          .append(rs.getString("extendTime"));

                    // Append a semicolon to separate each request
                    result.append(";");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }

        // Remove the trailing semicolon if there are any results
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }

    public static String fetchExtendRequest(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder();

        // SQL query to fetch Extend request based on the requestedByID and bookId
        String query = "SELECT * FROM requests WHERE requestType = 'Extend For Subscriber'";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Execute the query and get the result set
            try (ResultSet rs = pstmt.executeQuery()) {
                // Process each result
                while (rs.next()) {
                    // Concatenate the fields with commas
                    result.append(rs.getString("requestType")).append(",")
                          .append(rs.getString("requestedByID")).append(",")
                          .append(rs.getString("requestedByName")).append(",")
                          .append(rs.getString("bookName")).append(",")
                          .append(rs.getString("bookId")).append(",")
                          .append(rs.getString("borrowTime")).append(",")
                          .append(rs.getString("returnTime")).append(",")
                          .append(rs.getString("extendTime"));

                    // Append a semicolon to separate each request
                    result.append(";");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }

        // Remove the trailing semicolon if there are any results
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }
    
    
    public static boolean insertBorrowBook(Connection conn, String body) throws SQLException {
        // Split the body string by commas
        String[] parts = body.split(",");
        
        // Assuming the body contains the following values:
        // SName, SID, BName, ISBN, Btime in the respective order
        String SName = parts.length > 0 ? parts[0] : "temp"; // Subscriber Name
        int SID = parts.length > 1 ?   Integer.parseInt(parts[1]) : -1; // Subscriber ID
        String BName = parts.length > 2 ? parts[2] : "temp"; // Book Name (not used in the query, but included for reference)
        String ISBN = parts.length > 3 ? parts[3] : "temp"; // Book ISBN
        String Btime = parts.length > 4 ? parts[4] : "temp"; // Borrow Time
        String Rtime = parts.length > 4 ? parts[5] : "temp"; // Borrow Time
        
        // SQL query to insert a new record into the borrowed_books table
        String query = "INSERT INTO borrowed_books (ISBN, subscriber_id, Name, Borrowed_Time, Return_Time) "
                     + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Set the values for each field in the query
            pstmt.setString(1, ISBN); // ISBN
            pstmt.setInt(2, SID); // subscriber_id
            pstmt.setString(3, BName); // Book Name
            pstmt.setString(4, Btime); // Borrow Time
            pstmt.setString(5, Rtime); // Return Time 

            // Execute the insert and get the number of affected rows
            int affectedRows = pstmt.executeUpdate();

            // Debugging: Check if rows were inserted
            if (affectedRows > 0) {
                System.out.println("Insert successful: " + affectedRows + " row(s) inserted.");
                return true;
            } else {
                System.out.println("Insert failed: No rows inserted.");
                return false;
            }
        }
    }
    
    
    
    
    
   //

    /*
    public static void updateCopiesOfBook(Connection conn, String body) throws SQLException {
        // Split the 'body' string to extract necessary information
        String[] details = body.split(","); // assuming ',' is the delimiter

        // Extract the bookId (ISBN) from the array
        String bookId = details[3].trim(); // The bookId is at index 3 in this case
        String checkSql = "SELECT AvailableCopiesNum FROM books WHERE ISBN = ?";
        String updateSql = "UPDATE books SET AvailableCopiesNum = AvailableCopiesNum - 1 WHERE ISBN = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            // Check if the book exists and has more than 0 copies
            checkStmt.setString(1, bookId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    int AvailableCopiesNum = rs.getInt("AvailableCopiesNum");
                    if (AvailableCopiesNum > 0) {
                        // Proceed to update NumCopies
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, bookId);
                            int rowsAffected = updateStmt.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Number of AvailableCopies for the book with ISBN " + bookId + " updated successfully.");
                            } else {
                                System.out.println("Failed to update the number of AvailableCopies for the book.");
                            }
                        }
                    } else {
                        throw new SQLException("The number of available copies for the book with ISBN " + bookId + " is already 0.");
                    }
                } else {
                    throw new SQLException("Book with ISBN " + bookId + " not found.");
                }
            }
        } catch (SQLException e) {
            // Handle exception
            System.err.println("Error updating number of available copies: " + e.getMessage());
            throw e;
        }
        
        
        /*
        // Prepare the history message to be appended
        String checkSql1 = "SELECT history FROM detailed_subscription_history WHERE detailed_subscription_history = ?";
        String updateHistorySql = "UPDATE detailed_subscription_history SET history = ? WHERE detailed_subscription_history = ?";

        try (PreparedStatement checkStmt1 = conn.prepareStatement(checkSql1)) {
            checkStmt1.setString(1, details[1].trim()); // details[1] is subscriberId
            try (ResultSet rs1 = checkStmt1.executeQuery()) {
                if (rs1.next()) {
                    // The subscriber exists, proceed to append to their history
                    String existingHistory = rs1.getString("history");
                    if (existingHistory == null) {
                        existingHistory = ""; // Ensure we have an empty string if no history exists
                    }

                    String myHistoryMessage = details[4] + "," + details[3] + "," + details[2] + "," + "Borrowed successfully";
                    String newHistory = existingHistory + myHistoryMessage + ";"; // Always append a semicolon to the new message


                    // Update history by appending the new message
                    try (PreparedStatement updateHistoryStmt = conn.prepareStatement(updateHistorySql)) {
                        updateHistoryStmt.setString(1, newHistory); // Set the new history with appended message
                        updateHistoryStmt.setString(2, details[1].trim()); // subscriber_id

                        // Execute update
                        int rowsAffectedHistory = updateHistoryStmt.executeUpdate();
                        if (rowsAffectedHistory > 0) {
                            System.out.println("History updated successfully for subscriber with ID " + details[1].trim());
                        } else {
                            System.out.println("Failed to update history for subscriber with ID " + details[1].trim());
                        }
                    }
                } else {
                    throw new SQLException("Subscriber with ID " + details[1].trim() + " not found in history.");
                }
            }
        } catch (SQLException e) {
            // Handle exception for history update
            System.err.println("Error updating history: " + e.getMessage());
            throw e;
        }*/


    
    public static void updateHistoryInDB(Connection conn, String body) throws SQLException {
        System.out.println("Updating history with body: " + body);
        String[] details = body.split(",");
        System.out.println("Parsed details length: " + details.length);

        String checkSql = "SELECT history FROM detailed_subscription_history WHERE detailed_subscription_history = ?";
        String insertSql = "INSERT INTO detailed_subscription_history (detailed_subscription_history, history) VALUES (?, ?)";
        String updateHistorySql = "UPDATE detailed_subscription_history SET history = ? WHERE detailed_subscription_history = ?";
        String sqlMessage = details[6].trim();

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, details[1].trim());  // Subscriber ID
            System.out.println("Checking history for subscriber ID: " + details[1].trim());

            try (ResultSet rs = checkStmt.executeQuery()) {
                String historyMessage = details[4] + "," + details[3] + "," + details[2] + "," + sqlMessage + ";";

                if (rs.next()) {
                    // Subscriber exists → Update history
                    String existingHistory = rs.getString("history");
                    if (existingHistory == null) {
                        existingHistory = "";
                    }
                    String newHistory = existingHistory + historyMessage;
                    System.out.println("Updating history: " + newHistory);

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateHistorySql)) {
                        updateStmt.setString(1, newHistory);
                        updateStmt.setString(2, details[1].trim());
                        int rowsAffected = updateStmt.executeUpdate();

                        if (rowsAffected > 0) {
                            System.out.println("History updated successfully for subscriber with ID " + details[1].trim());
                        } else {
                            System.out.println("Failed to update history for subscriber with ID " + details[1].trim());
                        }
                    }
                } else {
                    // Subscriber does not exist → Insert new subscriber
                    System.out.println("Subscriber not found. Inserting new subscriber with ID: " + details[1].trim());
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, details[1].trim());
                        insertStmt.setString(2, historyMessage);
                        int rowsInserted = insertStmt.executeUpdate();

                        if (rowsInserted > 0) {
                            System.out.println("New subscriber inserted and history updated for ID " + details[1].trim());
                        } else {
                            System.out.println("Failed to insert new subscriber with ID " + details[1].trim());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating history: " + e.getMessage());
            throw e;
        }
    }



    
    
    
    public static String fetchRegisterRequest(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder();

        // SQL query to fetch Register request based on the requestedByID and bookId
        String query = "SELECT * FROM requests WHERE requestType = 'Request For Register'";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Execute the query and get the result set
            try (ResultSet rs = pstmt.executeQuery()) {
                // Process each result
                while (rs.next()) {
                    // Concatenate the fields with commas
                    result.append(rs.getString("requestType")).append(",")
                          .append(rs.getString("requestedByID")).append(",")
                          .append(rs.getString("requestedByName")).append(",")
                          .append(rs.getString("bookName")).append(",")
                          .append(rs.getString("bookId")).append(",")
                          .append(rs.getString("borrowTime")).append(",")
                          .append(rs.getString("returnTime")).append(",")
                          .append(rs.getString("extendTime"));

                    // Append a semicolon to separate each request
                    result.append(";");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }

        // Remove the trailing semicolon if there are any results
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }
    public static boolean deleteRequest(Connection dbConnection, String requestType, String subscriberId, String bookID) {
        System.out.println("delete request: Type: " + requestType + ", Subscriber id: " + subscriberId + ", book id: " + bookID);
        try (PreparedStatement stmt = dbConnection.prepareStatement(
                "DELETE FROM requests WHERE requestType = ? AND requestedByID = ? AND bookId = ?")) {
            stmt.setString(1, requestType);
            stmt.setString(2, subscriberId);
            stmt.setString(3, bookID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean deleteRegisterRequest(Connection dbConnection, String subscriberId) {
        System.out.println("delete register request for Subscriber id:" + subscriberId);
        try (PreparedStatement stmt = dbConnection.prepareStatement(
                "DELETE FROM requests WHERE requestType = 'Request For Register' AND requestedByID = ?")) {
            stmt.setString(1, subscriberId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean incrementBookCount(Connection dbConnection, String bookID) {
    	System.out.println("book id for increment"+bookID);
        try (PreparedStatement stmt = dbConnection.prepareStatement(
                "UPDATE books SET AvailableCopiesNum = AvailableCopiesNum + 1 WHERE ISBN = ?")) {
            stmt.setString(1, bookID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean isBookReserved(Connection dbConnection, String ISBN) {
        String query = "SELECT ReservedCopiesNum FROM books WHERE ISBN = ?";
        
        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
            preparedStatement.setString(1, ISBN); // Use setString for String type
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int reservedCopies = resultSet.getInt("ReservedCopiesNum");
                    return reservedCopies > 0; // Return true if ReservedCopiesNum > 0
                } else {
                    // Book not found in the database
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception appropriately, e.g., log it or rethrow it
            return false;
        }
    }

    
    public static void updateReturnDateAfterExtension(int borrowId,String extendedReturnDate, Connection con) {
        // SQL query to update the return date for a specific borrowed book
        String query = "UPDATE borrowed_books SET Return_Time = ? WHERE borrow_id = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            // Set the query parameters
            preparedStatement.setString(1, extendedReturnDate); // New return date
            preparedStatement.setInt(2, borrowId); // Borrow ID of the book
            
            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Return date updated successfully for Borrow ID: " + borrowId);
            } else {
                System.out.println("No record found for Borrow ID: " + borrowId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to update the return date for Borrow ID: " + borrowId);
        }
    }


    public static List<String> fetchBorrowedBooksForTaskScheduler(Connection conn) {
        String query = "SELECT * FROM blib.borrowed_books";

        List<String> borrowedBooks = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Combine fields into a single delimited string for sending to the client
                    String bookData = rs.getInt("borrow_id") + "," +
                    				  rs.getInt("subscriber_id") + "," +
                                      rs.getString("Name") + "," +
                                      rs.getString("Borrowed_Time") + "," +
                                      rs.getString("Return_Time") + "," +
                                      rs.getString("ISBN");
                    borrowedBooks.add(bookData);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching borrowed books: " + e.getMessage());
        }
        return borrowedBooks; // Return the list of borrowed books
    }
    
    public static void freezeSubscriber(Connection conn, int subscriberID) throws SQLException {
    	// Update a subscriber's phone and email
    	String query = "UPDATE subscriber SET status = ? WHERE subscriber_id = ?";

    	String status = "Frozen at:" + EchoServer.clock.timeNow();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        	pstmt.setString(1, status); // Update SQL statement.
        	pstmt.setInt(2, subscriberID);
            pstmt.executeUpdate(); // Run the statement.
        }
    }
    
    public static void unfreezeSubscriber(Connection conn, int subscriberID) throws SQLException {
    	// Update a subscriber's phone and email
    	String query = "UPDATE subscriber SET status = ? WHERE subscriber_id = ?";

    	String status = "Not Frozen";
    	// Debug log to check inputs
        System.out.println("Updating subscriber: " + subscriberID + " with status: " + status);

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        	pstmt.setString(1, status); // Update SQL statement.
        	pstmt.setInt(2, subscriberID);
            pstmt.executeUpdate(); // Run the statement.
        }
    }
    

    public static String fetchBorrowRequestGivenBorrowedBookID(Connection conn, String borrowedBookID) {
        String query = "SELECT * FROM requests WHERE bookId = ? AND requestType = 'Borrow For Subscriber'";

        StringBuilder bookInfo = new StringBuilder();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, borrowedBookID.trim());

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                        // If this is the first row, add book information
                        String bookData = rs.getString("bookId") + "," +
                        				  rs.getString("bookName") + "," +
                        				  rs.getString("requestedByID") + "," +
                        				  rs.getString("requestedByName") + "," +
                                          rs.getString("borrowTime") + "," +
                                          rs.getString("returnTime");
                        bookInfo.append(bookData);
                        break;
                    }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching borrow requests: " + e.getMessage());
            e.printStackTrace();
        }

        return bookInfo.toString();
    }
    
    public static List<String> fetchAllReservedBooksWhereBookIsAvailable(Connection conn) {
        String query = "SELECT * FROM blib.reserved_books WHERE time_left_to_retrieve != 'Book is not available yet'";

        List<String> reservedBooks = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Combine fields into a single delimited string for sending to the client
                    String bookData = rs.getInt("reserve_id") + "," +
                                      rs.getInt("subscriber_id") + "," +
                                      rs.getString("name") + "," +
                                      rs.getString("reserve_time") + "," +
                                      rs.getString("time_left_to_retrieve") + "," +
                                      rs.getString("ISBN");
                    reservedBooks.add(bookData);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reserved books: " + e.getMessage());
        }

        return reservedBooks; // Return the list of reserved books
    }

    
    public static String fetchClosestReturnDate(Connection dbConnection, String isbn) throws SQLException {
        String query = "SELECT MIN(Return_Time) AS ClosestReturnDate " +
                       "FROM borrowed_books " +
                       "WHERE ISBN = ? AND Return_Time IS NOT NULL";

        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("ClosestReturnDate");
            }
        }

        return null; // If no records are found
    }


    public static boolean decreaseNumCopies(Connection conn, String bookId) throws SQLException {
        // SQL query to decrease NumCopies by 1 for the given bookId
        String query = "UPDATE books SET NumCopies = NumCopies - 1 WHERE ISBN = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Set the bookId parameter
            pstmt.setString(1, bookId);

            // Execute the update statement
            int affectedRows = pstmt.executeUpdate();

            // If no rows were updated, it means there are no copies left or the bookId does not exist
            if (affectedRows == 0) {
                System.out.println("No copies available or invalid bookId: " + bookId);
                return false;
            } else {
                System.out.println("Successfully decreased AvailableCopiesNum for bookId: " + bookId);
                return true;
            }
        }
    }
    
    public static List<String> fetchAllReservedBooks(Connection conn) {
        String query = "SELECT * FROM blib.reserved_books";

        List<String> reservedBooks = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Combine fields into a single delimited string for sending to the client
                    String bookData = rs.getInt("reserve_id") + "," +
                                      rs.getInt("subscriber_id") + "," +
                                      rs.getString("name") + "," +
                                      rs.getString("reserve_time") + "," +
                                      rs.getString("time_left_to_retrieve") + "," +
                                      rs.getString("ISBN");
                    reservedBooks.add(bookData);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reserved books: " + e.getMessage());
        }

        return reservedBooks; // Return the list of reserved books
    }
}
