package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAddCourse {

    public static boolean addCourse(String courseName, String section) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "INSERT INTO Courses (course_name, section) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseName);
            pstmt.setString(2, section);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0; // true if inserted successfully

        } catch (SQLException e) {
            System.err.println("Error inserting course: " + e.getMessage());
            return false;
        } finally {
            dbConn.closeConnection();
        }
    }

    // ✨ New method to check if course already exists
    public static boolean courseExists(String courseName, String section) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "SELECT COUNT(*) FROM Courses WHERE course_name = ? AND section = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseName);
            pstmt.setString(2, section);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error checking if course exists: " + e.getMessage());
            return false;
        } finally {
            dbConn.closeConnection();
        }
    }
}
