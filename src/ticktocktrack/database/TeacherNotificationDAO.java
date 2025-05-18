package ticktocktrack.database;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TeacherNotificationDAO {

    public static void sendTeacherNotification(int teacherUserId, String message, String type, int senderUserId) {
        String sql = "INSERT INTO Notifications (recipient_user_id, sender_user_id, message, notification_type, date_sent, is_read) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();

            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, teacherUserId);
                pstmt.setInt(2, senderUserId);
                pstmt.setString(3, message);
                pstmt.setString(4, type);
                pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                pstmt.setBoolean(6, false);

                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("Error sending teacher notification: " + e.getMessage());
        }
    }
}
