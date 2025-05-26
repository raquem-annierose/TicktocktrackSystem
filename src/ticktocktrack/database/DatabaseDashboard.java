package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides utility methods for dashboard-related database operations, 
 * including user and teacher statistics as well as attendance tracking.
 */
public class DatabaseDashboard {

    /**
     * Counts the number of users with the specified role.
     * 
     * @param role the role to filter users by (e.g., "Teacher", "Student").
     * @return the count of users with the specified role.
     */
    public static int countUsersByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, role);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConn.closeConnection();
        }
        return 0;
    }

    /**
     * Counts the number of accounts created by a specific admin.
     * 
     * @param creatorName the full name of the admin who created the accounts.
     * @return the count of accounts created by the specified admin.
     */
    public static int countAccountsCreatedBy(String creatorName) {
        String sql = 
            "SELECT COUNT(*) FROM users u " +
            "JOIN admins a ON u.created_by_admin_id = a.admin_id " +
            "WHERE CONCAT(a.first_name, ' ', a.last_name) = ?";
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, creatorName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConn.closeConnection();
        }
        return 0;
    }

    /**
     * Retrieves the total number of classes assigned to a specific teacher.
     * 
     * @param teacherId the ID of the teacher.
     * @return the total number of classes assigned to the teacher.
     */
    public static int getTotalClassesByTeacher(int teacherId) {
        int total = 0;
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "SELECT COUNT(*) AS total FROM Classes WHERE teacher_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting classes: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
        return total;
    }

    /**
     * Retrieves the total number of unique students taught by a specific teacher.
     * 
     * @param teacherId the ID of the teacher.
     * @return the total number of unique students taught by the teacher.
     */
    public static int getTotalUniqueStudentsByTeacher(int teacherId) {
        int total = 0;
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = 
                "SELECT COUNT(DISTINCT e.student_id) AS total " +
                "FROM Enrollments e " +
                "JOIN Classes c ON e.class_id = c.class_id " +
                "WHERE c.teacher_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting students: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
        return total;
    }

    /**
     * Retrieves the count of attendance records for a specific student 
     * with the specified attendance status.
     * 
     * @param studentId the ID of the student.
     * @param status the attendance status to filter by (e.g., "Present", "Absent").
     * @return the count of attendance records with the specified status.
     */
    public static int getAttendanceCountByStatus(int studentId, String status) {
        int count = 0;
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = 
                "SELECT COUNT(*) " +
                "FROM Attendance a " +
                "JOIN Enrollments e ON a.enrollment_id = e.enrollment_id " +
                "WHERE e.student_id = ? AND a.status = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                pstmt.setString(2, status);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConn.closeConnection();
        }
        return count;
    }
}
