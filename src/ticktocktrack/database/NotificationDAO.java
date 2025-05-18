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


public class NotificationDAO {

    public static void sendAttendanceNotification(int studentId, String attendanceStatus) {
        int studentUserId = getUserIdByStudentId(studentId);

        System.out.println("DEBUG: Retrieved user_id for student_id " + studentId + " is " + studentUserId);

        if (studentUserId == -1) {
            System.err.println("Error: No user_id found for student_id " + studentId);
            return;
        }

        String message = "Your attendance has been marked as: " + attendanceStatus;
        String type = "Attendance";

        String sql = "INSERT INTO Notifications (recipient_user_id, message, notification_type, date_sent, is_read) " +
                     "VALUES (?, ?, ?, ?, ?)";

        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();

            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                System.out.println("Sending notification to userId: " + studentUserId);

                pstmt.setInt(1, studentUserId);
                pstmt.setString(2, message);
                pstmt.setString(3, type);
                pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                pstmt.setBoolean(5, false); // Unread

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

                    System.out.println("DEBUG: Retrieved Notification #" + count + " — Message: " + message + ", Status: " + status + ", Date: " + dateSent);
                }

                System.out.println("DEBUG: Total notifications retrieved: " + count);
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Failed to retrieve notifications for userId = " + userId + " — " + e.getMessage());
        }

        return notifications;
    }

}
