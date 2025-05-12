package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseViewClassList {

    public static List<String[]> getCourses() {
        DatabaseConnection dbConn = new DatabaseConnection();
        List<String[]> courses = new ArrayList<>();

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "SELECT course_name, section FROM Courses";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                String section = rs.getString("section");
                courses.add(new String[]{courseName, section});
            }

        } catch (SQLException e) {
            System.err.println("Error fetching courses: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }

        return courses;
    }
    public static void deleteCourse(String courseName, String section) {
        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            // Prepare the SQL query to delete the course
            String sql = "DELETE FROM Courses WHERE course_name = ? AND section = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseName);
            pstmt.setString(2, section);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Course deleted successfully.");
            } else {
                System.out.println("No matching course found to delete.");
            }

        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
    }
    public static void updateCourse(String oldCourseName, String oldSection, String newCourseName, String newSection) {
        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            // Prepare the SQL query to update the course
            String sql = "UPDATE Courses SET course_name = ?, section = ? WHERE course_name = ? AND section = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newCourseName);
            pstmt.setString(2, newSection);
            pstmt.setString(3, oldCourseName);
            pstmt.setString(4, oldSection);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Course updated successfully.");
            } else {
                System.out.println("No matching course found to update.");
            }

        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
    }
}
