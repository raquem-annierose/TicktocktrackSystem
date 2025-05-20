package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import ticktocktrack.logic.Notification;
import ticktocktrack.logic.Session;


public class StudentNotificationDAO {

	public static void sendAttendanceNotification(int studentId, String attendanceStatus) {
	    int studentUserId = getUserIdByStudentId(studentId);
	    if (studentUserId == -1) {
	        System.err.println("Error: No user_id found for student_id " + studentId);
	        return;
	    }

	    int senderUserId = Session.getCurrentUser().getUserId(); 
	    String senderRole = Session.getCurrentUser().getRole();

	    // Get sender full name + role
	    String senderDisplayName = getSenderFullNameAndRole(senderUserId, senderRole);

	    // Compose customized message
	    String message = senderDisplayName + " marked you as " + attendanceStatus;

	    String type = "Attendance";

	    String sql = "INSERT INTO Notifications (recipient_user_id, sender_user_id, message, notification_type, date_sent, is_read) " +
	                 "VALUES (?, ?, ?, ?, ?, ?)";

	    DatabaseConnection dbConn = new DatabaseConnection();

	    try {
	        dbConn.connectToSQLServer();

	        try (Connection conn = dbConn.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            pstmt.setInt(1, studentUserId);
	            pstmt.setInt(2, senderUserId);
	            pstmt.setString(3, message);
	            pstmt.setString(4, type);
	            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
	            pstmt.setBoolean(6, false);

	            pstmt.executeUpdate();
	        }
	        
	        
	    } catch (SQLException e) {
	        System.err.println("Error sending notification: " + e.getMessage());
	    }
	}




    // Helper method to resolve user_id from student_id
    public static int getUserIdByStudentId(int studentId) {
        String sql = "SELECT user_id FROM Students WHERE student_id = ?";
        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();

            try (Connection conn = dbConn.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, studentId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving user_id: " + e.getMessage());
        }

        return -1;
    }
    
    private static String getSenderFullNameAndRole(int userId, String role) {
        DatabaseConnection dbConn = new DatabaseConnection();
        String fullName = "";
        String table = ""; 
        String firstNameCol = "first_name";
        String lastNameCol = "last_name";

        // Determine which table to query for the sender's name
        switch (role.toLowerCase()) {
            case "teacher":
                table = "Teachers";
                break;
            case "admin":
                table = "Admins";
                break;
            case "student":
                table = "Students";
                break;
            default:
                table = "Users"; // fallback to Users table if no role matched
        }

        String sql = "";

        if (table.equals("Users")) {
            // Just get username from Users table
            sql = "SELECT username FROM Users WHERE user_id = ?";
        } else {
            // Get first and last name from the role-specific table joined with Users
            sql = "SELECT " + firstNameCol + ", " + lastNameCol + " FROM " + table + " WHERE user_id = ?";
        }

        try {
            dbConn.connectToSQLServer();

            try (Connection conn = dbConn.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, userId);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    if (table.equals("Users")) {
                        fullName = rs.getString("username");
                    } else {
                        fullName = rs.getString(firstNameCol) + " " + rs.getString(lastNameCol);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sender name: " + e.getMessage());
        }

        return role + " " + fullName; // e.g. "Teacher John Smith"
    }

    // New method to fetch notifications for a given user
    public static List<Notification> getNotificationsForUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT message, notification_type, date_sent FROM Notifications WHERE recipient_user_id = ? ORDER BY date_sent DESC";

        System.out.println("DEBUG: Starting getNotificationsForUser for userId = " + userId);

        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();
            System.out.println("DEBUG: Connected to SQL Server successfully.");

            try (Connection conn = dbConn.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, userId);
                System.out.println("DEBUG: Executing SQL query with userId = " + userId);

                ResultSet rs = stmt.executeQuery();

                int count = 0;
                while (rs.next()) {
                    String message = rs.getString("message");
                    String status = rs.getString("notification_type");
                    LocalDateTime dateSent = rs.getTimestamp("date_sent").toLocalDateTime();

                    notifications.add(new Notification(message, dateSent, status));
                    count++;

                    System.out.println("DEBUG: Retrieved Notification #" + count + " � Message: " + message + ", Status: " + status + ", Date: " + dateSent);
                }

                System.out.println("DEBUG: Total notifications retrieved: " + count);
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Failed to retrieve notifications for userId = " + userId + " � " + e.getMessage());
        }

        return notifications;
    }

}
