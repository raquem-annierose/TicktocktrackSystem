package ticktocktrack.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ticktocktrack.logic.Notification;
import ticktocktrack.logic.Session;

/**
 * Data Access Object (DAO) class for handling student notification-related database operations.
 */
public class StudentNotificationDAO {
	
    /**
     * Sends a notification to a student when their excuse letter for an absence is accepted.
     *
     * @param studentId      The student ID to whom the notification will be sent.
     * @param courseName     The name of the course for which the excuse was accepted.
     * @param attendanceDate The date of the attendance being excused.
     */
	public static void sendExcuseAcceptedNotification(int studentId, String courseName, LocalDate attendanceDate) {
        int studentUserId = getUserIdByStudentId(studentId);
        if (studentUserId == -1) {
            System.err.println("Error: No user_id found for student_id " + studentId);
            return;
        }

        int senderUserId = Session.getCurrentUser().getUserId();
        String senderRole = Session.getCurrentUser().getRole();
        String senderDisplayName = getSenderFullNameAndRole(senderUserId, senderRole);

        String message = senderDisplayName + " accepted your excuse letter for the course " + courseName + " on " + attendanceDate + ". Your attendance is now marked as Excused.";

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
            System.err.println("Error sending excuse accepted notification: " + e.getMessage());
        }
    }

    /**
     * Sends an attendance notification to a student about their attendance status for a specific date and course.
     *
     * @param studentId      The student ID to notify.
     * @param attendanceStatus The attendance status (e.g., Absent, Present, Excused).
     * @param enrollmentId   The enrollment ID related to the attendance record.
     * @param attendanceDate The date of the attendance.
     * @param course         The course name.
     */
	public static void sendAttendanceNotification(int studentId, String attendanceStatus, int enrollmentId, LocalDate attendanceDate, String course) {
	    int studentUserId = getUserIdByStudentId(studentId);
	    if (studentUserId == -1) {
	        System.err.println("Error: No user_id found for student_id " + studentId);
	        return;
	    }

	    int senderUserId = Session.getCurrentUser().getUserId(); 
	    String senderRole = Session.getCurrentUser().getRole();
	    String senderDisplayName = getSenderFullNameAndRole(senderUserId, senderRole);
	    String message;
	    String formattedDate = attendanceDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")); // e.g., May 25, 2025

	    if (attendanceStatus.equalsIgnoreCase("Absent")) {
	        // Check if this absence is already excused
	        if (isAlreadyExcused(enrollmentId, attendanceDate)) {
	            message = senderDisplayName + " marked you as Excused for the course " + course + " on " + formattedDate + ".";
	        } else {
	            message = senderDisplayName + " marked you as Absent in " + course + " on " + formattedDate + ". Please submit an excuse letter.";
	        }
	    } else {
	        // Default message for other statuses like Present, Late, Excused
	        message = senderDisplayName + " marked you as " + attendanceStatus + " in " + course + " on " + formattedDate + ".";
	    }

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

    /**
     * Checks if an attendance record for a given enrollment and date is already marked as excused.
     *
     * @param enrollmentId   The enrollment ID to check.
     * @param attendanceDate The date of the attendance record.
     * @return True if the attendance status is "Excused"; false otherwise.
     */
	private static boolean isAlreadyExcused(int enrollmentId, LocalDate attendanceDate) {
	    String sql = "SELECT status FROM Attendance WHERE enrollment_id = ? AND date = ?";
	    DatabaseConnection dbConn = new DatabaseConnection();

	    try {
	        dbConn.connectToSQLServer();
	        try (Connection conn = dbConn.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(sql)) {

	            stmt.setInt(1, enrollmentId);
	            stmt.setDate(2, Date.valueOf(attendanceDate));

	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    String status = rs.getString("status");
	                    return "Excused".equalsIgnoreCase(status);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error checking attendance status: " + e.getMessage());
	    }

	    return false;
	}
	
    /**
     * Retrieves the user_id associated with a given student_id.
     *
     * @param studentId The student ID to resolve.
     * @return The corresponding user_id, or -1 if not found.
     */
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
    
    /**
     * Retrieves the sender's full name and role as a formatted string.
     *
     * @param userId The user ID of the sender.
     * @param role   The role of the sender (e.g., "teacher", "admin", "student").
     * @return A string in the format "Role FullName" or "Role Username".
     */
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

    /**
     * Retrieves all notifications for a specific user, ordered by the date sent in descending order.
     *
     * @param userId The user ID for whom notifications are retrieved.
     * @return A list of Notification objects for the user.
     */
    public static List<Notification> getNotificationsForUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT message, notification_type, date_sent, sender_user_id "
                   + "FROM Notifications WHERE recipient_user_id = ? "
                   + "ORDER BY date_sent DESC";

        System.out.println("DEBUG: Starting getNotificationsForUser for userId = " + userId);
        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();
            System.out.println("DEBUG: Connected to SQL Server successfully.");

            try (Connection conn = dbConn.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, userId);
                System.out.println("DEBUG: Executing SQL query with userId = " + userId);

                try (ResultSet rs = stmt.executeQuery()) {
                    int count = 0;
                    while (rs.next()) {
                        String message = rs.getString("message");
                        String status = rs.getString("notification_type");
                        LocalDateTime dateSent = rs.getTimestamp("date_sent").toLocalDateTime();
                        int senderUserId = rs.getInt("sender_user_id");

                        notifications.add(new Notification(message, dateSent, status, senderUserId));
                        count++;

                        System.out.println("DEBUG: Retrieved Notification #" + count
                                         + " • Message: " + message
                                         + ", Status: " + status
                                         + ", Date: " + dateSent
                                         + ", SenderUserId: " + senderUserId);
                    }
                    System.out.println("DEBUG: Total notifications retrieved: " + count);
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to retrieve notifications for userId = " + userId
                              + " • " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }

        return notifications;
    }


}
