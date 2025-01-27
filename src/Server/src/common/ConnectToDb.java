package common;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import logic.ServerTimeDiffController;
import server.EchoServer;

/**
 * This class provides methods to interact with the local "blib" MySQL database.
 * It includes operations such as fetching data, inserting records, updating records,
 * and retrieving information for reporting.
 */
public class ConnectToDb {

    /**
     * A shared controller instance used to calculate time differences between the server and various tasks.
     */
    public static ServerTimeDiffController clock = new ServerTimeDiffController();

    /**
     * Establishes a connection to the local "blib" MySQL database.
     *
     * @return A {@link Connection} object to the database.
     * @throws SQLException If the driver is not found or the connection fails.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            EchoServer.outputInOutputStreamAndLog("Driver definition succeed");
        } catch (Exception ex) {
        	EchoServer.outputInOutputStreamAndLog("Driver definition failed");
            throw new SQLException("Driver loading failed", ex);
        }

        return DriverManager.getConnection(
                "jdbc:mysql://localhost/blib?serverTimezone=IST&allowPublicKeyRetrieval=true&useSSL=false", 
                "root", 
                "Aa123456"
        );
    }

    /**
     * Fetches all return dates for a specific book (by ISBN) from the "borrow_table".
     *
     * @param dbConnection An open database connection.
     * @param isbn         The ISBN of the book.
     * @return A list of all return dates associated with the given ISBN.
     * @throws SQLException If any SQL error occurs.
     */
    public static List<String> fetchReturnDates(Connection dbConnection, String isbn) throws SQLException {
        List<String> returnDates = new ArrayList<>();
        String query = "SELECT return_date FROM borrow_table WHERE isbn = ?";

        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    returnDates.add(rs.getString("return_date"));
                }
            }
        }
        return returnDates;
    }

    /**
     * Fetches all data from the "subscriber" table.
     *
     * @param conn A valid database connection.
     * @return A list of string records with subscriber data.
     */
    public static List<String> fetchAllData(Connection conn) {
        List<String> result = new ArrayList<>();
        String query = "SELECT * FROM subscriber";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("subscriber_id");
                String name = rs.getString("subscriber_name");
                int detailed = rs.getInt("detailed_subscription_history");
                String phone = rs.getString("subscriber_phone_number");
                String email = rs.getString("subscriber_email");
                String status = rs.getString("status");

                String row = "subscriber_id:" + id 
                           + ", subscriber_name:" + name
                           + ", detailed_subscription_history:" + detailed
                           + ", subscriber_phone_number:" + phone
                           + ", subscriber_email:" + email
                           + ", status:" + status;

                result.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching data: " + e.getMessage());
        }
        return result;
    }

    /**
     * Fetches all frozen data (for reports) from the "databydate" table.
     *
     * @param conn A valid database connection.
     * @return A list of string data related to frozen/not-frozen subscribers, borrowed books, and late returns.
     */
    public static List<String> fetchAllFrozenDataForReports(Connection conn) {
        List<String> result = new ArrayList<>();
        String query = "SELECT * FROM databydate";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            StringBuilder fullResult = new StringBuilder();
            while (rs.next()) {
                Date id = rs.getDate("idDataByDate");
                int frozen = rs.getInt("Frozen");
                int notFrozen = rs.getInt("NotFrozen");
                int borrowedBooks = rs.getInt("BorrowedBooks");
                int late = rs.getInt("Late");

                fullResult.append(id).append(",")
                          .append(frozen).append(",")
                          .append(notFrozen).append(",")
                          .append(borrowedBooks).append(",")
                          .append(late).append(";");
            }
            result.add(fullResult.toString());
        } catch (SQLException e) {
            System.err.println("Error while fetching data: " + e.getMessage());
        }
        return result;
    }

    /**
     * Fetches all data (for reports) from the "subscriber" table.
     *
     * @param conn A valid database connection.
     * @return A list of formatted string data of all subscribers.
     */
    public static List<String> fetchAllDataForReports(Connection conn) {
        List<String> result = new ArrayList<>();
        String query = "SELECT * FROM subscriber";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            StringBuilder fullResult = new StringBuilder();
            while (rs.next()) {
                int id = rs.getInt("subscriber_id");
                String name = rs.getString("subscriber_name");
                int detailed = rs.getInt("detailed_subscription_history");
                String phone = rs.getString("subscriber_phone_number");
                String email = rs.getString("subscriber_email");
                String status = rs.getString("status");

                fullResult.append(id).append(",")
                          .append(name).append(",")
                          .append(detailed).append(",")
                          .append(phone).append(",")
                          .append(email).append(",")
                          .append(status).append(";");
            }
            result.add(fullResult.toString());
        } catch (SQLException e) {
            System.err.println("Error while fetching data: " + e.getMessage());
        }
        return result;
    }

    /**
     * Deletes a borrowed book record from the "borrowed_books" table to simulate returning a book.
     *
     * @param dbConnection  A valid database connection.
     * @param subscriberId  The subscriber's ID who is returning the book.
     * @param bookID        The book's ISBN being returned.
     * @return A status message indicating success or failure.
     */
    public static String returnbook(Connection dbConnection, String subscriberId, String bookID) {
        String result = "Book return failed";
        String sql = "DELETE FROM borrowed_books WHERE subscriber_id = ? AND ISBN = ?";

        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, subscriberId);
            stmt.setString(2, bookID);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                result = "Book returned successfully";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Error while returning book";
        }
        return result;
    }

    /**
     * Fetches subscriber data (full row) based on a subscriber ID.
     *
     * @param conn         A valid database connection.
     * @param subscriberId The subscriber's ID.
     * @return A string with subscriber information, or "No subscriber found" if no data matches.
     */
    public static String fetchSubscriberData(Connection conn, String subscriberId) {
        String query = "SELECT * FROM subscriber WHERE subscriber_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(subscriberId));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("subscriber_id");
                    String name = rs.getString("subscriber_name");
                    int detailed = rs.getInt("detailed_subscription_history");
                    String phone = rs.getString("subscriber_phone_number");
                    String email = rs.getString("subscriber_email");
                    String status = rs.getString("status");

                    return "subscriber_id:" + id
                         + ", subscriber_name:" + name
                         + ", detailed_subscription_history:" + detailed
                         + ", subscriber_phone_number:" + phone
                         + ", subscriber_email:" + email
                         + ", status:" + status;
                } else {
                    return "No subscriber found";
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching subscriber data: " + e.getMessage());
            return "Error fetching subscriber data.";
        }
    }

    /**
     * Fetches librarian data from the "librarian" table based on a librarian ID.
     *
     * @param conn        A valid database connection.
     * @param librarianId The librarian's ID.
     * @return A string with librarian information, or "No labrarian found" if no data matches.
     */
    public static String fetchLibrarianData(Connection conn, String librarianId) {
        String query = "SELECT * FROM librarian WHERE librarian_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(librarianId));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("librarian_id");
                    String name = rs.getString("librarian_name");
                    return "librarian_id:" + id + ", librarian_name:" + name;
                } else {
                    return "No labrarian found";
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching subscriber data: " + e.getMessage());
            return "Error fetching subscriber data.";
        }
    }

    /**
     * Fetches the detailed subscription history for a particular subscriber.
     *
     * @param conn         A valid database connection.
     * @param subscriberId The subscriber's ID.
     * @return The subscription history content (as a single string) from the "detailed_subscription_history" table.
     */
    public static String fetchHistoryData(Connection conn, String subscriberId) {
        String historyData = "";
        String query = "SELECT history FROM detailed_subscription_history WHERE detailed_subscription_history = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, subscriberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    historyData = rs.getString("history");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching history data: " + e.getMessage());
        }
        return historyData;
    }

    /**
     * Registers a new subscriber and optionally creates a new entry in the "detailed_subscription_history" table
     * if the subscriber is new.
     *
     * @param conn A valid database connection.
     * @param body A comma-separated string of: "subscriberName,SubscriberID,PhoneNumber,Email"
     * @return "True" if the insertion is successful, otherwise "False".
     */
    public static String updateSubscriberDB(Connection conn, String body) {
        
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
                    }
                } else {
                    System.err.println("Subscriber ID already exists in detailed_subscription_history: " + subscriberId);
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
                    return "True";
                } else {
                    return "False";
                }
            }
        } catch (SQLException e) {
            // Handle SQL errors
            System.err.println("Error inserting subscriber data: " + e.getMessage());
            return "False";
        }
    }



    /**
     * Clears the 'extensions_by_subscribers' column in the "librarian" table.
     *
     * @param conn A valid database connection.
     * @return A message indicating the result of the operation.
     */
    public static String cleanExtensionsBySubscribersInLibrarian(Connection conn) {
        String query = "UPDATE librarian SET extensions_by_subscribers = ''";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                return "Extension daily cleanup done.";
            } else {
                return "No rows were updated. The table may already be clean.";
            }
        } catch (SQLException e) {
            return "Error while clearing 'extensions_by_subscribers' column: " + e.getMessage();
        }
    }

    /**
     * Fetches all borrowed books for a given subscriber ID.
     *
     * @param conn         A valid database connection.
     * @param subscriberId The subscriber's ID.
     * @return A list of borrowed book data as formatted strings.
     */
    public static List<String> fetchBorrowedBooksBySubscriberId(Connection conn, String subscriberId) {
        String query = "SELECT * FROM blib.borrowed_books WHERE subscriber_id = ?";
        List<String> borrowedBooks = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, subscriberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String bookData = rs.getInt("borrow_id") + ","
                                    + rs.getInt("subscriber_id") + ","
                                    + rs.getString("Name") + ","
                                    + rs.getString("Borrowed_Time") + ","
                                    + rs.getString("Return_Time") + ","
                                    + rs.getString("ISBN");
                    borrowedBooks.add(bookData);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching borrowed books: " + e.getMessage());
        }
        return borrowedBooks;
    }

    
    
    
    
    
    
    /**
     * Fetches all borrow requests for a given subscriber ID.
     *
     * @param conn         A valid database connection.
     * @param subscriberId The subscriber's ID.
     * @return A list of borrow request data as formatted strings.
     */
    public static List<String> fetchBorrowRequestsBySubscriberId(Connection conn, String subscriberId) {
        String query = "SELECT * FROM requests WHERE requestType = 'Borrow For Subscriber' AND requestedByID = ?";
        List<String> borrowRequests = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, subscriberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String requestData = rs.getString("requestType") + ","
                                       + rs.getString("requestedByID") + ","
                                       + rs.getString("requestedByName") + ","
                                       + rs.getString("bookName") + ","
                                       + rs.getString("bookId") + ","
                                       + rs.getString("borrowTime") + ","
                                       + rs.getString("returnTime") + ","
                                       + rs.getString("extendTime");
                    borrowRequests.add(requestData);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching borrow requests: " + e.getMessage());
        }

        return borrowRequests;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Fetches all reserved books for a given subscriber ID.
     *
     * @param conn         A valid database connection.
     * @param subscriberId The subscriber's ID.
     * @return A list of reserved book data as formatted strings.
     */
    public static List<String> fetchReservedBooksBySubscriberId(Connection conn, String subscriberId) {
        String query = "SELECT * FROM blib.reserved_books WHERE subscriber_id = ?";
        List<String> reservedBooks = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, subscriberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String bookData = rs.getInt("reserve_id") + ","
                                    + rs.getInt("subscriber_id") + ","
                                    + rs.getString("name") + ","
                                    + rs.getString("reserve_time") + ","
                                    + rs.getString("time_left_to_retrieve") + ","
                                    + rs.getString("ISBN");
                    reservedBooks.add(bookData);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching borrowed books: " + e.getMessage());
        }
        return reservedBooks;
    }

    /**
     * Checks if a request exists for a given subscriber in the "requests" table.
     *
     * @param dbConnection A valid database connection.
     * @param RegisterId   The subscriber's ID.
     * @return True if a request exists, false otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    public static boolean checkIfrequestexists(Connection dbConnection, String RegisterId) throws SQLException {
        String query = "SELECT COUNT(*) FROM requests WHERE RequestedById = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, RegisterId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Fetches all books from the "books" table.
     *
     * @param conn A valid database connection.
     * @return A list of string representations of each book (CSV-like format).
     */
    public static List<String> fetchBooksData(Connection conn) {
        String query = "SELECT * FROM blib.books";
        List<String> booksList = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                // No data
                return booksList;
            }
            while (rs.next()) {
                String id = rs.getString("ISBN");
                String name = rs.getString("Name");
                String subject = rs.getString("Subject");
                String description = rs.getString("ShortDescription");
                int copies = rs.getInt("NumCopies");
                String location = rs.getString("ShelfLocation");
                int availableCopies = rs.getInt("AvailableCopiesNum");
                int reservedCopies = rs.getInt("ReservedCopiesNum");

                if (id == null) id = "Unknown ID";
                if (name == null) name = "Unknown";
                if (subject == null) subject = "N/A";
                if (description == null) description = "No description available";
                if (location == null) location = "Unknown location";

                String bookData = id + "," + name + "," + subject + "," + description + ","
                                + copies + "," + location + "," + availableCopies + "," + reservedCopies;
                booksList.add(bookData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while fetching books data: " + e.getMessage());
            return null;
        }
        return booksList;
    }

    /**
     * Checks if a subscriber exists in the "subscriber" table.
     *
     * @param conn         A valid database connection.
     * @param subscriberId The subscriber's ID.
     * @return True if the subscriber exists, false otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    public static boolean checkSubscriberExists(Connection conn, String subscriberId) throws SQLException {
        String query = "SELECT * FROM subscriber WHERE subscriber_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(subscriberId));
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Checks if a librarian exists in the "librarian" table.
     *
     * @param conn        A valid database connection.
     * @param librarianId The librarian's ID.
     * @return True if the librarian exists, false otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    public static boolean checkLibrarianExists(Connection conn, String librarianId) throws SQLException {
        String query = "SELECT * FROM librarian WHERE librarian_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(librarianId));
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Updates a subscriber's phone and email in the "subscriber" table.
     *
     * @param conn         A valid database connection.
     * @param subscriberId The subscriber's ID.
     * @param phone        The new phone number.
     * @param email        The new email address.
     * @throws SQLException If an SQL error occurs.
     */
    public static void updateSubscriber(Connection conn, String subscriberId, String phone, String email) throws SQLException {
        String query = "UPDATE subscriber SET subscriber_phone_number = ?, subscriber_email = ? WHERE subscriber_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, email);
            pstmt.setInt(3, Integer.parseInt(subscriberId));
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Update successful: " + affectedRows + " row(s) affected.");
            } else {
                System.out.println("Update failed: No rows updated. Subscriber ID might not exist.");
            }
        }
    }

    /**
     * Fetches book information from the "books" table by ISBN.
     *
     * @param conn   A valid database connection.
     * @param bookId The ISBN of the book.
     * @return A single CSV-like string of book details, or an error/no-book message.
     */
    public static String fetchBookInfo(Connection conn, String bookId) {
        String query = "SELECT * FROM books WHERE ISBN = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("ISBN");
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

                    return String.format("%s,%s,%s,%s,%d,%s,%d,%d",
                            id, name, subject, description, copies, location, availableCopies, reservedCopies);
                } else {
                    return "No book found";
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching book info: " + e.getMessage());
            return "Error fetching book info.";
        }
    }

    /**
     * Inserts a new request record into the "requests" table.
     *
     * @param conn           A valid database connection.
     * @param requestType    The type of the request (Borrow, Return, Extend, etc.).
     * @param requestedByID  The ID of the subscriber making/requesting the action.
     * @param requestedByName The name of the subscriber.
     * @param bookName       The name of the book related to the request.
     * @param bookId         The ISBN of the book.
     * @param borrowTime     The borrow time if applicable.
     * @param returnTime     The return time if applicable.
     * @param extendTime     The extension time if applicable.
     * @throws SQLException If an SQL error occurs.
     */
    public static void insertRequest(Connection conn, String requestType, String requestedByID, 
            String requestedByName, String bookName, String bookId, 
            String borrowTime, String returnTime, String extendTime) throws SQLException {

        String query = "INSERT INTO requests (requestType, requestedByID, requestedByName, bookName, bookId, borrowTime, returnTime, extendTime) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, requestType);
            pstmt.setString(2, requestedByID);
            pstmt.setString(3, requestedByName);
            pstmt.setString(4, bookName);
            pstmt.setString(5, bookId);
            pstmt.setString(6, (borrowTime != null && !borrowTime.isEmpty()) ? borrowTime : "temp");
            pstmt.setString(7, (returnTime != null && !returnTime.isEmpty()) ? returnTime : "temp");
            pstmt.setString(8, (extendTime != null && !extendTime.isEmpty()) ? extendTime : "temp");

            pstmt.executeUpdate();
        }
    }

    /**
     * Inserts a new reserved book record into the "reserved_books" table.
     *
     * @param conn          A valid database connection.
     * @param subscriber_id The subscriber ID reserving the book.
     * @param bookName      The name of the book being reserved.
     * @param reserveTime   The time the reservation was created.
     * @param BookId        The ISBN of the book being reserved.
     * @throws SQLException If an SQL error occurs.
     */
    public static void insertReservedBook(Connection conn, String subscriber_id,
            String bookName, String reserveTime, String BookId) throws SQLException {

        String query = "INSERT INTO reserved_books (subscriber_id, name, reserve_time, ISBN) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, subscriber_id);
            pstmt.setString(2, bookName);
            pstmt.setString(3, reserveTime);
            pstmt.setString(4, BookId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int reserveId = generatedKeys.getInt(1);
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

    /**
     * Fetches all "Borrow For Subscriber" requests from the "requests" table.
     *
     * @param conn A valid database connection.
     * @return A semicolon-separated string of all borrow requests.
     * @throws SQLException If an SQL error occurs.
     */
    public static String fetchBorrowRequest(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder();
        String query = "SELECT * FROM requests WHERE requestType = 'Borrow For Subscriber'";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                result.append(rs.getString("requestType")).append(",")
                      .append(rs.getString("requestedByID")).append(",")
                      .append(rs.getString("requestedByName")).append(",")
                      .append(rs.getString("bookName")).append(",")
                      .append(rs.getString("bookId")).append(",")
                      .append(rs.getString("borrowTime")).append(",")
                      .append(rs.getString("returnTime")).append(",")
                      .append(rs.getString("extendTime")).append(";");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }

        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }
        return result.toString();
    }

    /**
     * Decrements the "AvailableCopiesNum" by 1 for a specific book in the "books" table.
     *
     * @param conn   A valid database connection.
     * @param bookId The ISBN of the book to update.
     * @throws SQLException If the book doesn't exist or an SQL error occurs.
     */
    public static void decreaseAvaliabeNumCopies(Connection conn, String bookId) throws SQLException {
        String query = "UPDATE books SET AvailableCopiesNum = AvailableCopiesNum - 1 WHERE ISBN = ? AND NumCopies > 0";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bookId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Increments the "ReservedCopiesNum" by 1 for a specific book in the "books" table.
     *
     * @param conn   A valid database connection.
     * @param bookId The ISBN of the book to update.
     * @throws SQLException If the book doesn't exist or an SQL error occurs.
     */
    public static void incrementReservedCopiesNum(Connection conn, String bookId) throws SQLException {
        String query = "UPDATE books SET ReservedCopiesNum = ReservedCopiesNum + 1 WHERE ISBN = ? AND ReservedCopiesNum >= 0";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bookId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Decrements the "ReservedCopiesNum" by 1 for a specific book in the "books" table.
     *
     * @param conn   A valid database connection.
     * @param bookId The ISBN of the book to update.
     * @throws SQLException If the book doesn't exist or an SQL error occurs.
     */
    public static void decreaseReservedCopiesNum(Connection conn, String bookId) throws SQLException {
        String query = "UPDATE books SET ReservedCopiesNum = ReservedCopiesNum - 1 WHERE ISBN = ? AND ReservedCopiesNum > 0";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bookId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Fetches all "Return For Subscriber" requests from the "requests" table.
     *
     * @param conn A valid database connection.
     * @return A semicolon-separated string of all return requests.
     * @throws SQLException If an SQL error occurs.
     */
    public static String fetchReturnRequest(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder();
        String query = "SELECT * FROM requests WHERE requestType = 'Return For Subscriber'";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                result.append(rs.getString("requestType")).append(",")
                      .append(rs.getString("requestedByID")).append(",")
                      .append(rs.getString("requestedByName")).append(",")
                      .append(rs.getString("bookName")).append(",")
                      .append(rs.getString("bookId")).append(",")
                      .append(rs.getString("borrowTime")).append(",")
                      .append(rs.getString("returnTime")).append(",")
                      .append(rs.getString("extendTime")).append(";");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }
        return result.toString();
    }

    /**
     * Fetches all "Extend For Subscriber" requests from the "requests" table.
     *
     * @param conn A valid database connection.
     * @return A semicolon-separated string of all extend requests.
     * @throws SQLException If an SQL error occurs.
     */
    public static String fetchExtendRequest(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder();
        String query = "SELECT * FROM requests WHERE requestType = 'Extend For Subscriber'";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                result.append(rs.getString("requestType")).append(",")
                      .append(rs.getString("requestedByID")).append(",")
                      .append(rs.getString("requestedByName")).append(",")
                      .append(rs.getString("bookName")).append(",")
                      .append(rs.getString("bookId")).append(",")
                      .append(rs.getString("borrowTime")).append(",")
                      .append(rs.getString("returnTime")).append(",")
                      .append(rs.getString("extendTime")).append(";");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }
        return result.toString();
    }

    /**
     * Inserts a new record into the "borrowed_books" table for a subscriber borrowing a book.
     *
     * @param conn A valid database connection.
     * @param body A comma-separated string containing: SubscriberName, SubscriberID, BookName, ISBN, BorrowTime, ReturnTime.
     * @return True if the insertion was successful, false otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    public static boolean insertBorrowBook(Connection conn, String body) throws SQLException {
        String[] parts = body.split(",");
        String SName = parts.length > 0 ? parts[0] : "temp";
        int SID = parts.length > 1 ? Integer.parseInt(parts[1]) : -1;
        String BName = parts.length > 2 ? parts[2] : "temp";
        String ISBN = parts.length > 3 ? parts[3] : "temp";
        String Btime = parts.length > 4 ? parts[4] : "temp";
        // The code indicates indexing for 5 as well:
        String Rtime = parts.length > 5 ? parts[5] : "temp";

        String query = "INSERT INTO borrowed_books (ISBN, subscriber_id, Name, Borrowed_Time, Return_Time) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, ISBN);
            pstmt.setInt(2, SID);
            pstmt.setString(3, BName);
            pstmt.setString(4, Btime);
            pstmt.setString(5, Rtime);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Appends a new history record into the "detailed_subscription_history" table for a specific subscriber.
     *
     * @param conn A valid database connection.
     * @param body A comma-separated string expected to contain multiple fields for the history entry.
     * @throws SQLException If any SQL operation fails.
     */
    public static void updateHistoryInDB(Connection conn, String body) throws SQLException {
        String[] details = body.split(",");
        System.out.println("Parsed details length: " + details.length);

        String checkSql = "SELECT history FROM detailed_subscription_history WHERE detailed_subscription_history = ?";
        String insertSql = "INSERT INTO detailed_subscription_history (detailed_subscription_history, history) VALUES (?, ?)";
        String updateHistorySql = "UPDATE detailed_subscription_history SET history = ? WHERE detailed_subscription_history = ?";

        String sqlMessage = details[6].trim(); // The text message to record in history
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, details[1].trim());
            System.out.println("Checking history for subscriber ID: " + details[1].trim());

            try (ResultSet rs = checkStmt.executeQuery()) {
                String historyMessage = details[4] + "," 
                                      + details[3] + "," 
                                      + details[2] + "," 
                                      + sqlMessage + ";";
                if (rs.next()) {
                    // Update existing history
                    String existingHistory = rs.getString("history");
                    if (existingHistory == null) {
                        existingHistory = "";
                    }
                    String newHistory = existingHistory + historyMessage;

                    
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateHistorySql)) {
                        updateStmt.setString(1, newHistory);
                        updateStmt.setString(2, details[1].trim());
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Subscriber not found in history, insert new
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, details[1].trim());
                        insertStmt.setString(2, historyMessage);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating history: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Fetches all "Request For Register" requests from the "requests" table.
     *
     * @param conn A valid database connection.
     * @return A semicolon-separated string of all "register" requests.
     * @throws SQLException If an SQL error occurs.
     */
    public static String fetchRegisterRequest(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder();
        String query = "SELECT * FROM requests WHERE requestType = 'Request For Register'";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                result.append(rs.getString("requestType")).append(",")
                      .append(rs.getString("requestedByID")).append(",")
                      .append(rs.getString("requestedByName")).append(",")
                      .append(rs.getString("bookName")).append(",")
                      .append(rs.getString("bookId")).append(",")
                      .append(rs.getString("borrowTime")).append(",")
                      .append(rs.getString("returnTime")).append(",")
                      .append(rs.getString("extendTime")).append(";");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }
        return result.toString();
    }

    /**
     * Deletes a specific request from the "requests" table.
     *
     * @param dbConnection A valid database connection.
     * @param requestType  The type of request (Borrow, Return, etc.).
     * @param subscriberId The subscriber's ID who made the request.
     * @param bookID       The ID/ISBN of the related book.
     * @return True if a request was deleted, false otherwise.
     */
    public static boolean deleteRequest(Connection dbConnection, String requestType, String subscriberId, String bookID) {
        String query = "DELETE FROM requests WHERE requestType = ? AND requestedByID = ? AND bookId = ?";

        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, requestType);
            stmt.setString(2, subscriberId);
            stmt.setString(3, bookID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a "Request For Register" request from the "requests" table.
     *
     * @param dbConnection A valid database connection.
     * @param subscriberId The subscriber's ID.
     * @return True if the request was deleted, false otherwise.
     */
    public static boolean deleteRegisterRequest(Connection dbConnection, String subscriberId) {
        String query = "DELETE FROM requests WHERE requestType = 'Request For Register' AND requestedByID = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, subscriberId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Increments the "AvailableCopiesNum" of a book by 1 in the "books" table.
     *
     * @param dbConnection A valid database connection.
     * @param bookID       The ISBN of the book.
     * @return True if successfully updated, false otherwise.
     */
    public static boolean incrementBookCount(Connection dbConnection, String bookID) {
        String query = "UPDATE books SET AvailableCopiesNum = AvailableCopiesNum + 1 WHERE ISBN = ?";

        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, bookID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the first reservation entry for a specific book from "Book is not available yet" to a valid 2-day retrieval window.
     *
     * @param dbConnection A valid database connection.
     * @param bookID       The ISBN of the book.
     * @return True if the update was successful, false otherwise.
     */
    public static boolean updateFirstReservation(Connection dbConnection, String bookID) {
        try {
            String fetchReservationQuery = 
                "SELECT reserve_id FROM reserved_books WHERE ISBN = ? AND time_left_to_retrieve = 'Book is not available yet' ORDER BY reserve_id ASC LIMIT 1";
            int smallestReserveId = -1;

            try (PreparedStatement fetchStmt = dbConnection.prepareStatement(fetchReservationQuery)) {
                fetchStmt.setString(1, bookID);
                try (ResultSet rs = fetchStmt.executeQuery()) {
                    if (rs.next()) {
                        smallestReserveId = rs.getInt("reserve_id");
                    } else {
                        return false;
                    }
                }
            }

            String updateReservationQuery = "UPDATE reserved_books SET time_left_to_retrieve = ? WHERE reserve_id = ?";
            String twoDays = EchoServer.clock.convertStringToLocalDate(EchoServer.clock.timeNow())
                                            .plusDays(2).toString();
            twoDays = convertDateFormat(twoDays);

            try (PreparedStatement updateStmt = dbConnection.prepareStatement(updateReservationQuery)) {
                updateStmt.setString(1, twoDays);
                updateStmt.setInt(2, smallestReserveId);

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a book is reserved by querying "ReservedCopiesNum" in the "books" table.
     *
     * @param dbConnection A valid database connection.
     * @param ISBN         The ISBN of the book.
     * @return True if the number of reserved copies is greater than 0, false otherwise.
     */
    public static boolean isBookReserved(Connection dbConnection, String ISBN) {
        String query = "SELECT ReservedCopiesNum FROM books WHERE ISBN = ?";
        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
            preparedStatement.setString(1, ISBN);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int reservedCopies = resultSet.getInt("ReservedCopiesNum");
                    return reservedCopies > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the return date of a borrowed book after an extension is approved.
     *
     * @param borrowId          The ID of the borrowed book record.
     * @param extendedReturnDate The new return date to be set.
     * @param con               A valid database connection.
     */
    public static void updateReturnDateAfterExtension(int borrowId, String extendedReturnDate, Connection con) {
        String query = "UPDATE borrowed_books SET Return_Time = ? WHERE borrow_id = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, extendedReturnDate);
            preparedStatement.setInt(2, borrowId);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to update the return date for Borrow ID: " + borrowId);
        }
    }

    /**
     * Fetches all borrowed books from the "borrowed_books" table. This is intended for a task scheduler operation.
     *
     * @param conn A valid database connection.
     * @return A list of borrowed book data as formatted strings.
     */
    public static List<String> fetchBorrowedBooksForTaskScheduler(Connection conn) {
        String query = "SELECT * FROM blib.borrowed_books";
        List<String> borrowedBooks = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String bookData = rs.getInt("borrow_id") + ","
                                + rs.getInt("subscriber_id") + ","
                                + rs.getString("Name") + ","
                                + rs.getString("Borrowed_Time") + ","
                                + rs.getString("Return_Time") + ","
                                + rs.getString("ISBN");
                borrowedBooks.add(bookData);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching borrowed books: " + e.getMessage());
        }
        return borrowedBooks;
    }

    /**
     * Freezes a subscriber by updating the "status" field in the "subscriber" table to "Frozen at:<current time>".
     *
     * @param conn         A valid database connection.
     * @param subscriberID The subscriber's ID to freeze.
     * @throws SQLException If an SQL error occurs.
     */
    public static void freezeSubscriber(Connection conn, int subscriberID) throws SQLException {
        String query = "UPDATE subscriber SET status = ? WHERE subscriber_id = ?";
        String status = "Frozen at:" + EchoServer.clock.timeNow();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, subscriberID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error freezing subscriber: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Unfreezes a subscriber by updating the "status" field in the "subscriber" table to "Not Frozen".
     *
     * @param conn         A valid database connection.
     * @param subscriberID The subscriber's ID to unfreeze.
     * @throws SQLException If an SQL error occurs.
     */
    public static void unfreezeSubscriber(Connection conn, int subscriberID) throws SQLException {
        String query = "UPDATE subscriber SET status = ? WHERE subscriber_id = ?";
        String status = "Not Frozen";
        System.out.println("Updating subscriber: " + subscriberID + " with status: " + status);

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, subscriberID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error unfreezing subscriber: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fetches a specific "Borrow For Subscriber" request from the "requests" table given a borrowed book ID.
     *
     * @param conn           A valid database connection.
     * @param borrowedBookID The book ID used in the request.
     * @return A comma-separated string with the request details.
     */
    public static String fetchBorrowRequestGivenBorrowedBookID(Connection conn, String borrowedBookID) throws SQLException{
        String query = "SELECT * FROM requests WHERE bookId = ? AND requestType = 'Borrow For Subscriber'";
        StringBuilder bookInfo = new StringBuilder();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, borrowedBookID.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String bookData = rs.getString("bookId") + ","
                                    + rs.getString("bookName") + ","
                                    + rs.getString("requestedByID") + ","
                                    + rs.getString("requestedByName") + ","
                                    + rs.getString("borrowTime") + ","
                                    + rs.getString("returnTime");
                    bookInfo.append(bookData);
                    break; // Only fetch the first matching request
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching borrow requests: " + e.getMessage());
            e.printStackTrace();
        }
        return bookInfo.toString();
    }

    /**
     * Fetches all reserved books that are currently available (i.e., "time_left_to_retrieve" != "Book is not available yet").
     *
     * @param conn A valid database connection.
     * @return A list of reserved book data as formatted strings.
     */
    public static List<String> fetchAllReservedBooksWhereBookIsAvailable(Connection conn) throws SQLException {
        String query = "SELECT * FROM blib.reserved_books WHERE time_left_to_retrieve != 'Book is not available yet'";
        List<String> reservedBooks = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String bookData = rs.getInt("reserve_id") + ","
                                  + rs.getInt("subscriber_id") + ","
                                  + rs.getString("name") + ","
                                  + rs.getString("reserve_time") + ","
                                  + rs.getString("time_left_to_retrieve") + ","
                                  + rs.getString("ISBN");
                reservedBooks.add(bookData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservedBooks;
    }

    /**
     * Fetches the closest return date for a given ISBN from the "borrowed_books" table.
     *
     * @param dbConnection A valid database connection.
     * @param isbn         The ISBN of the book.
     * @return The closest return date as a string, or null if not found.
     * @throws SQLException If an SQL error occurs.
     */
    public static String fetchClosestReturnDate(Connection dbConnection, String isbn) throws SQLException {
        String query = "SELECT MIN(Return_Time) AS ClosestReturnDate FROM borrowed_books WHERE ISBN = ? AND Return_Time IS NOT NULL";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ClosestReturnDate");
                }
            }
        }
        return null;
    }

    /**
     * Decrements the total "NumCopies" of a book by 1 in the "books" table.
     *
     * @param conn   A valid database connection.
     * @param bookId The ISBN of the book.
     * @return True if successfully decremented, false otherwise.
     * @throws SQLException If no copies are available or the book doesn't exist.
     */
    public static boolean decreaseNumCopies(Connection conn, String bookId) throws SQLException {
        String query = "UPDATE books SET NumCopies = NumCopies - 1 WHERE ISBN = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bookId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * Fetches all reserved books from the "reserved_books" table.
     *
     * @param conn A valid database connection.
     * @return A list of reserved book data as formatted strings.
     */
    public static List<String> fetchAllReservedBooks(Connection conn) throws SQLException {
        String query = "SELECT * FROM blib.reserved_books";
        List<String> reservedBooks = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String bookData = rs.getInt("reserve_id") + ","
                                + rs.getInt("subscriber_id") + ","
                                + rs.getString("name") + ","
                                + rs.getString("reserve_time") + ","
                                + rs.getString("time_left_to_retrieve") + ","
                                + rs.getString("ISBN");
                reservedBooks.add(bookData);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reserved books: " + e.getMessage());
        }
        return reservedBooks;
    }

    /**
     * Appends data to the "extensions_by_subscribers" field in the "librarian" table.
     *
     * @param conn A valid database connection.
     * @param data The data to be appended.
     * @throws SQLException If an SQL error occurs.
     */
    public static void updateExtensionApprovedBySubscriber(Connection conn, String data) throws SQLException {
        String currentData = "";
        String fetchQuery = "SELECT extensions_by_subscribers FROM blib.librarian";
        String updateQuery = "UPDATE blib.librarian SET extensions_by_subscribers = ?";

        try (PreparedStatement fetchStmt = conn.prepareStatement(fetchQuery);
             ResultSet rs = fetchStmt.executeQuery()) {
            if (rs.next()) {
                currentData = rs.getString("extensions_by_subscribers");
            }
        }

        String updatedData = currentData + data;
        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            updateStmt.setString(1, updatedData);
            updateStmt.executeUpdate();
        }
    }

    /**
     * Pulls the first entry (full string) from the "extensions_by_subscribers" field in the "librarian" table.
     *
     * @param conn A valid database connection.
     * @return The data from the first row, or a message if no data is found.
     * @throws SQLException If an SQL error occurs.
     */
    public static String pullNewExtendedReturnDates(Connection conn) throws SQLException {
        String query = "SELECT extensions_by_subscribers FROM librarian LIMIT 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getString("extensions_by_subscribers");
            }
        } catch (SQLException e) {
            throw new SQLException("Error while fetching data: " + e.getMessage(), e);
        }
        return "No data found in the 'extensions_by_subscribers' field.";
    }

    /**
     * Fetches the number of books borrowed yesterday from the "borrowed_books" table.
     *
     * @param conn A valid database connection.
     * @return The count of borrowed books that started yesterday, or -1 if none are found.
     * @throws SQLException If an SQL error occurs.
     */
    public static int FetchYesterdayBorrows(Connection conn) throws SQLException {
        String yesterday = EchoServer.clock.convertStringToLocalDate(EchoServer.clock.timeNow()).minusDays(1).toString();
        String query = "SELECT COUNT(*) FROM borrowed_books WHERE STR_TO_DATE(Borrowed_Time,'%d-%m-%Y') = STR_TO_DATE(?,'%Y-%m-%d')";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, yesterday);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while fetching data: " + e.getMessage(), e);
        }
        return -1;
    }

    /**
     * Updates the "BorrowedBooks" field in the "databydate" table for yesterday's date.
     *
     * @param conn                         A valid database connection.
     * @param amountOfBooksBorrowedYesterday The number of books borrowed yesterday.
     * @throws SQLException If an SQL error occurs.
     */
    public static void updateAmountOfBorrowedBooksYesterday(Connection conn, int amountOfBooksBorrowedYesterday) throws SQLException {
        String yesterday = EchoServer.clock.convertStringToLocalDate(EchoServer.clock.timeNow()).minusDays(1).toString();
        String query = "UPDATE databydate SET BorrowedBooks = ? WHERE idDataByDate = STR_TO_DATE(?,'%Y-%m-%d')";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, amountOfBooksBorrowedYesterday);
            stmt.setString(2, yesterday);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while updating data: " + e.getMessage(), e);
        }
    }

    /**
     * Inserts a row for today's date into the "databydate" table if it does not already exist.
     *
     * @param taskSchedulerConnection A valid database connection.
     * @throws SQLException If an SQL error occurs.
     */
    public static void insertCurrentDate(Connection taskSchedulerConnection) throws SQLException {
        String today = EchoServer.clock.convertStringToLocalDate(EchoServer.clock.timeNow()).toString();
        String query1 = "SELECT COUNT(*) FROM databydate WHERE idDataByDate = STR_TO_DATE(?, '%Y-%m-%d')";

        try (PreparedStatement stmt1 = taskSchedulerConnection.prepareStatement(query1)) {
            stmt1.setString(1, today);
            try (ResultSet rs = stmt1.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        return; // Row already exists
                    }
                }
            }
            String query2 = "INSERT INTO databydate (idDataByDate, NotFrozen, Frozen, BorrowedBooks, Late) "
                          + "VALUES (STR_TO_DATE(?, '%Y-%m-%d'), 0, 0, null, null)";
            try (PreparedStatement stmt2 = taskSchedulerConnection.prepareStatement(query2)) {
                stmt2.setString(1, today);
                stmt2.executeUpdate();
            } catch (SQLException e) {
                throw new SQLException("Error while inserting data: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Fetches the number of late books as of yesterday (i.e., books that should have been returned by yesterday).
     *
     * @param conn A valid database connection.
     * @return The count of overdue books, or -1 in case of an error.
     * @throws SQLException If an SQL error occurs.
     */
    public static int FetchYesterdaylates(Connection conn) throws SQLException {
        String yesterday = EchoServer.clock.convertStringToLocalDate(EchoServer.clock.timeNow()).minusDays(1).toString();
        String query = "SELECT COUNT(*) FROM borrowed_books WHERE STR_TO_DATE(Return_Time, '%d-%m-%Y') < STR_TO_DATE(?, '%Y-%m-%d')";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, yesterday);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while fetching data: " + e.getMessage(), e);
        }
        return -1;
    }

    /**
     * Updates the "Late" field in the "databydate" table for yesterday's date.
     *
     * @param taskSchedulerConnection A valid database connection.
     * @param latebooks              The number of books that became late yesterday.
     * @throws SQLException If an SQL error occurs.
     */
    public static void updateAmountOflateBooksYesterday(Connection taskSchedulerConnection, int latebooks) throws SQLException {
        String yesterday = EchoServer.clock.convertStringToLocalDate(EchoServer.clock.timeNow()).minusDays(1).toString();
        String query = "UPDATE databydate SET Late = ? WHERE idDataByDate = STR_TO_DATE(?,'%Y-%m-%d')";

        try (PreparedStatement stmt = taskSchedulerConnection.prepareStatement(query)) {
            stmt.setInt(1, latebooks);
            stmt.setString(2, yesterday);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while updating data: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches the number of currently frozen subscribers.
     *
     * @param taskSchedulerConnection A valid database connection.
     * @return The number of subscribers whose status is not "Not Frozen".
     * @throws SQLException If an SQL error occurs.
     */
    public static int FetchAmountFrozen(Connection taskSchedulerConnection) throws SQLException {
        String query = "SELECT COUNT(*) FROM subscriber WHERE status != 'Not Frozen'";
        try (PreparedStatement stmt = taskSchedulerConnection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new SQLException("Error while fetching data: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Updates today's date record in "databydate" with the number of frozen subscribers.
     *
     * @param taskSchedulerConnection A valid database connection.
     * @param amountfrozen           The current number of frozen subscribers.
     * @throws SQLException If an SQL error occurs.
     */
    public static void amountfrozen(Connection taskSchedulerConnection, int amountfrozen) throws SQLException {
        String today = EchoServer.clock.convertStringToLocalDate(EchoServer.clock.timeNow()).toString();
        String query = "UPDATE databydate SET Frozen = ? WHERE idDataByDate = STR_TO_DATE(?,'%Y-%m-%d')";

        try (PreparedStatement stmt = taskSchedulerConnection.prepareStatement(query)) {
            stmt.setInt(1, amountfrozen);
            stmt.setString(2, today);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while updating data: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches the number of subscribers who are currently not frozen.
     *
     * @param taskSchedulerConnection A valid database connection.
     * @return The number of subscribers whose status is exactly "Not Frozen".
     * @throws SQLException If an SQL error occurs.
     */
    public static int FetchAmountNotFrozen(Connection taskSchedulerConnection) throws SQLException {
        String query = "SELECT COUNT(*) FROM subscriber WHERE status = 'Not Frozen'";
        try (PreparedStatement stmt = taskSchedulerConnection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new SQLException("Error while fetching data: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Updates today's date record in "databydate" with the number of not-frozen subscribers.
     *
     * @param taskSchedulerConnection A valid database connection.
     * @param amountNotfrozen         The number of not-frozen subscribers.
     * @throws SQLException If an SQL error occurs.
     */
    public static void amountNotfrozen(Connection taskSchedulerConnection, int amountNotfrozen) throws SQLException {
        String today = EchoServer.clock.convertStringToLocalDate(EchoServer.clock.timeNow()).toString();
        String query = "UPDATE databydate SET NotFrozen = ? WHERE idDataByDate = STR_TO_DATE(?,'%Y-%m-%d')";

        try (PreparedStatement stmt = taskSchedulerConnection.prepareStatement(query)) {
            stmt.setInt(1, amountNotfrozen);
            stmt.setString(2, today);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while updating data: " + e.getMessage(), e);
        }
    }

    /**
     * Converts a date string from "yyyy-MM-dd" to "dd-MM-yyyy" format.
     *
     * @param dateStr The date string in "yyyy-MM-dd" format.
     * @return The reformatted date string in "dd-MM-yyyy" format.
     */
    public static String convertDateFormat(String dateStr) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(dateStr, inputFormatter);
        return date.format(outputFormatter);
    }
    
    /**
     * Checks if a book is already borrowed by a subscriber.
     * 
     * <p>This method queries the database to determine whether a subscriber 
     * has already borrowed a specific book based on the subscriber's ID and the book's ISBN.</p>
     * 
     * @param dbConnection the database connection to execute the query
     * @param subscriberId the ID of the subscriber
     * @param bookId       the ISBN of the book
     * @return {@code true} if the book is already borrowed by the subscriber, {@code false} otherwise
     * @throws SQLException if a database access error occurs
	 */
    public static boolean bookalreadyborrowed(Connection dbConnection, String subscriberId, String bookId) throws SQLException {
        String query = "SELECT COUNT(*) FROM borrowed_books WHERE subscriber_id = ? AND ISBN = ?";

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
            // Set the parameters for the prepared statement
            preparedStatement.setString(1, subscriberId);
            preparedStatement.setString(2, bookId);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Check if count is greater than 0 (the book already reserved)
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("An error occurred while checking the borrowed books: " + e.getMessage());
        }

        // If an exception occurs or no result is found, return false by default
        return false;
    }

    public static void deleterequests(Connection conn) {
        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String todayDate = LocalDate.now().format(formatter);

        // SQL queries
        String fetchISBNQuery = "SELECT ISBN FROM reserved_books";
        String deleteByDateQuery = "DELETE FROM reserved_books WHERE time_left_to_retrieve = ?";
        String checkNumCopiesQuery = "SELECT NumCopies FROM books WHERE ISBN = ?";
        String deleteFromReservedBooksQuery = "DELETE FROM reserved_books WHERE ISBN = ?";
        String deleteFromBooksQuery = "DELETE FROM books WHERE ISBN = ?";

        try (PreparedStatement fetchISBNStmt = conn.prepareStatement(fetchISBNQuery);
             PreparedStatement deleteByDateStmt = conn.prepareStatement(deleteByDateQuery);
             PreparedStatement checkNumCopiesStmt = conn.prepareStatement(checkNumCopiesQuery);
             PreparedStatement deleteReservedStmt = conn.prepareStatement(deleteFromReservedBooksQuery);
             PreparedStatement deleteBooksStmt = conn.prepareStatement(deleteFromBooksQuery)) {

            // Case 1: Delete rows from reserved_books where time_left_to_retrieve equals today's date
            deleteByDateStmt.setString(1, todayDate);
            int rowsDeletedByDate = deleteByDateStmt.executeUpdate();
            System.out.println("Deleted " + rowsDeletedByDate + " rows from reserved_books with today's date.");

            // Case 2: Check NumCopies for each ISBN in reserved_books and delete from both tables if NumCopies = 0
            try (ResultSet rs = fetchISBNStmt.executeQuery()) {
                while (rs.next()) {
                    String isbn = rs.getString("ISBN");

                    // Check NumCopies in books table
                    checkNumCopiesStmt.setString(1, isbn);
                    try (ResultSet copiesRs = checkNumCopiesStmt.executeQuery()) {
                        if (copiesRs.next() && copiesRs.getInt("NumCopies") == 0) {
                            // Delete from reserved_books
                            deleteReservedStmt.setString(1, isbn);
                            int reservedDeleted = deleteReservedStmt.executeUpdate();

                            // Delete from books
                            deleteBooksStmt.setString(1, isbn);
                            int booksDeleted = deleteBooksStmt.executeUpdate();

                            System.out.println("Deleted " + reservedDeleted + " rows from reserved_books and " 
                                + booksDeleted + " rows from books for ISBN: " + isbn);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred: " + e.getMessage());
        }
    }
}