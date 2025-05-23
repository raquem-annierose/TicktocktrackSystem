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

public class TeacherNotificationDAO {

    /**
     * Sends a notification to a teacher when an event (e.g., excuse submission) occurs.
     */
    public static void sendTeacherNotification(int teacherId, String eventMessage, String eventType, int senderUserId) {
        int teacherUserId = getUserIdByTeacherId(teacherId);
        if (teacherUserId == -1) {
            System.err.println("Error: No user_id found for teacher_id " + teacherId);
            return;
        }

        String senderRole = Session.getCurrentUser().getRole(); // You may also pass this if needed
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
                ps.setInt(2, senderUserId);
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
     * Overloaded method that sends a teacher notification using the current session user as sender.
     */
    public static void sendTeacherNotification(int teacherId, String eventMessage, String eventType) {
        int senderUserId = Session.getCurrentUser().getUserId();
        sendTeacherNotification(teacherId, eventMessage, eventType, senderUserId);
    }

    /**
     * Retrieves all teacher full names from the Teachers table (for suggestions/autocomplete).
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
     * Helper to resolve application user_id by teacher_id.
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
     * Helper to compose sender's display name including role.
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
     * Fetches notifications for a given teacher user, with debug logging.
     */
    public static List<Notification> getNotificationsForUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT message, notification_type, date_sent "
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

                        notifications.add(new Notification(message, dateSent, status));
                        count++;
                        System.out.println("DEBUG: Retrieved Notification #" + count
                                         + " � Message: " + message
                                         + ", Status: " + status
                                         + ", Date: " + dateSent);
                    }
                    System.out.println("DEBUG: Total notifications retrieved: " + count);
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to retrieve notifications for userId = " + userId
                              + " � " + e.getMessage());
        }
        return notifications;
    }

    
    public static boolean submitExcuse(int studentId,
            String dateSubmitted,
            String reason,
            int teacherUserId,
            String remarks) {
    	
    	return true;
    }
}
