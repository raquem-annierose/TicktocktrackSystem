package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ticktocktrack.logic.Notification;
import ticktocktrack.logic.Session;

/**
 * Data Access Object for handling notifications related to teachers.
 * Includes methods for sending notifications, retrieving teacher info,
 * and fetching notifications for users.
 */
public class TeacherNotificationDAO {

    /**
     * Retrieves the role of a user based on their user ID.
     * 
     * @param userId the user ID
     * @return the role of the user as a lowercase string; defaults to "user"
     */
	public static String getUserRoleByUserId(int userId) {
	    String role = "user"; // default role if unknown
	    String sql = "SELECT role FROM Users WHERE user_id = ?";  // adjust table/column as needed
	    DatabaseConnection dbConn = new DatabaseConnection();
	    try {
	        dbConn.connectToSQLServer();
	        try (Connection conn = dbConn.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setInt(1, userId);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    role = rs.getString("role").toLowerCase();
	                }
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error retrieving role for user_id " + userId + ": " + e.getMessage());
	    }
	    return role;
	}

    /**
     * Sends a notification to a teacher about an event.
     * 
     * @param teacherId the ID of the teacher to notify
     * @param eventMessage the message describing the event
     * @param eventType the type/category of the notification
     * @param senderUserId the user ID of the sender
     */
	public static void sendTeacherNotification(int teacherId, String eventMessage, String eventType, int senderUserId) {
	    int teacherUserId = getUserIdByTeacherId(teacherId);
	    if (teacherUserId == -1) {
	        System.err.println("Error: No user_id found for teacher_id " + teacherId);
	        return;
	    }

	    String senderRole = getUserRoleByUserId(senderUserId);
	    String senderDisplay = getSenderFullNameAndRole(senderUserId, senderRole);


	    String message = senderDisplay + " " + eventMessage;

	    String sql = "INSERT INTO Notifications (recipient_user_id, sender_user_id, message, notification_type, date_sent, is_read) "
	               + "VALUES (?, ?, ?, ?, ?, ?)";

	    DatabaseConnection dbConn = new DatabaseConnection();
	    try {
	        dbConn.connectToSQLServer();
	        try (Connection conn = dbConn.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setInt(1, teacherUserId);
	            ps.setInt(2, senderUserId);  // This is critical to save sender user id!
	            ps.setString(3, message);
	            ps.setString(4, eventType);
	            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
	            ps.setBoolean(6, false);
	            ps.executeUpdate();
	        }
	    } catch (SQLException e) {
	        System.err.println("Error sending teacher notification: " + e.getMessage());
	    }
	}
	
    /**
     * Sends a notification to a teacher using the current session's user as sender.
     * 
     * @param teacherId the ID of the teacher to notify
     * @param eventMessage the message describing the event
     * @param eventType the type/category of the notification
     */
    public static void sendTeacherNotification(int teacherId, String eventMessage, String eventType) {
        int senderUserId = Session.getCurrentUser().getUserId();
        sendTeacherNotification(teacherId, eventMessage, eventType, senderUserId);
    }

    /**
     * Retrieves all teacher full names for autocomplete or suggestion purposes.
     * 
     * @return a list of teacher full names (first and last concatenated)
     */
    public static List<String> getAllTeacherNames() {
        List<String> teacherNames = new ArrayList<>();
        String query = "SELECT CONCAT(first_name, ' ', last_name) AS name FROM Teachers";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    teacherNames.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching teacher names: " + e.getMessage());
        }
        return teacherNames;
    }

    /**
     * Retrieves the application user ID associated with a teacher ID.
     * 
     * @param teacherId the teacher's ID
     * @return the associated user ID, or -1 if none found
     */
    public static int getUserIdByTeacherId(int teacherId) {
        String sql = "SELECT user_id FROM Teachers WHERE teacher_id = ?";
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, teacherId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user_id for teacher_id " + teacherId + ": " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Retrieves the teacher ID corresponding to a full teacher name.
     * 
     * @param fullName the full name of the teacher (first + last)
     * @return the teacher ID, or -1 if not found
     */
    public static int getTeacherIdByName(String fullName) {
        String sql = "SELECT teacher_id FROM Teachers WHERE CONCAT(first_name, ' ', last_name) = ?";
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, fullName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("teacher_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving teacher_id for name '" + fullName + "': " + e.getMessage());
        }
        return -1;
    }

    /**
     * Constructs a display string combining the sender's full name and role.
     * 
     * @param userId the sender's user ID
     * @param role the sender's role (e.g., "teacher", "student", "admin")
     * @return a string of the format "role FullName"
     */
    private static String getSenderFullNameAndRole(int userId, String role) {
        String table;
        switch (role.toLowerCase()) {
            case "teacher": table = "Teachers"; break;
            case "student": table = "Students"; break;
            case "admin":   table = "Admins";   break;
            default:        table = "Users";    break;
        }
        String sql = table.equals("Users")
                   ? "SELECT username FROM Users WHERE user_id = ?"
                   : String.format("SELECT first_name, last_name FROM %s WHERE user_id = ?", table);

        DatabaseConnection dbConn = new DatabaseConnection();
        String name = "";
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        name = table.equals("Users")
                             ? rs.getString("username")
                             : rs.getString("first_name") + " " + rs.getString("last_name");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sender name: " + e.getMessage());
        }
        return role + " " + name;
    }

    /**
     * Retrieves a paginated list of notifications for a specific user, ordered by the date sent in descending order.
     *
     * @param userId The user ID for whom notifications are retrieved.
     * @param offset The number of rows to skip before starting to fetch the notifications.
     * @param limit The maximum number of notifications to retrieve.
     * @return A list of Notification objects for the user.
     */

    public static List<Notification> getNotificationsForUser(int userId, int offset, int limit) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT notification_id, message, notification_type, date_sent, sender_user_id "
                   + "FROM Notifications WHERE recipient_user_id = ? "
                   + "ORDER BY date_sent DESC "
                   + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();

            try (Connection conn = dbConn.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, userId);
                stmt.setInt(2, offset);
                stmt.setInt(3, limit);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("notification_id");
                        String message = rs.getString("message");
                        String status = rs.getString("notification_type");
                        LocalDateTime dateSent = rs.getTimestamp("date_sent").toLocalDateTime();
                        int senderUserId = rs.getInt("sender_user_id");

                        // Create the Notification object
                        Notification notification = new Notification(message, dateSent, status, senderUserId);

                        // Set the notification ID
                        notification.setNotificationId(id);

                        // Add the notification to the list
                        notifications.add(notification);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to retrieve notifications for userId = " + userId
                              + " � " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }

        return notifications;
    }

    /**
     * Deletes a notification from the database based on its unique identifier.
     *
     * @param notificationId The unique identifier of the notification to be deleted.
     * @return {@code true} if the notification was successfully deleted, {@code false} otherwise.
     */ 
    public static boolean deleteNotificationById(int notificationId) {
        String sql = "DELETE FROM Notifications WHERE notification_id = ?";
        DatabaseConnection dbConn = new DatabaseConnection();
        
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, notificationId);
                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
                
            }
        } catch (SQLException e) {
            System.err.println("Error deleting notification with ID " + notificationId + ": " + e.getMessage());
        }

        return false;
    }

    /**
     * Default constructor for the {@code TeacherNotificationDAO} class.
     * Initializes the DAO for managing teacher notifications.
     */
    public TeacherNotificationDAO() {
        // Default constructor
    }

    
}
