package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
 // Method to fetch list of students (you can filter by section if needed)
    public static List<Student> getStudents() {
        DatabaseConnection dbConn = new DatabaseConnection();
        List<Student> students = new ArrayList<>();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            // Add 'username' to the SELECT clause
            String sql = "SELECT student_id, first_name, last_name, username, section, year_level FROM Students";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String username = rs.getString("username");
                String section = rs.getString("section");
                String yearLevel = rs.getString("year_level");

                // Ensure the Student constructor matches these fields
                students.add(new Student(studentId, firstName, lastName, username, section, yearLevel));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
        return students;
    }
 // New method to fetch the course ID for the given course and section
    public static int getCourseId(String courseName, String section) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "SELECT course_id FROM Courses WHERE course_name = ? AND section = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseName);
            pstmt.setString(2, section);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("course_id");
            }
            return -1; // Return -1 if the course ID is not found
        } catch (SQLException e) {
            System.err.println("Error fetching course ID: " + e.getMessage());
            return -1; // Return -1 on error
        } finally {
            dbConn.closeConnection();
        }
    }

    // Method to enroll students into a course
    public static boolean enrollStudentInCourse(int studentId, int courseId, String section) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "INSERT INTO StudentEnrollments (student_id, course_id, section) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, section);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0; // true if enrolled successfully
        } catch (SQLException e) {
            System.err.println("Error enrolling student: " + e.getMessage());
            return false;
        } finally {
            dbConn.closeConnection();
        }
    }
    public static boolean deleteCourse(int courseId) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            // Step 1: Delete from StudentEnrollments first
            String deleteEnrollmentsSql = "DELETE FROM StudentEnrollments WHERE course_id = ?";
            PreparedStatement deleteEnrollmentsStmt = conn.prepareStatement(deleteEnrollmentsSql);
            deleteEnrollmentsStmt.setInt(1, courseId);
            deleteEnrollmentsStmt.executeUpdate();

            // Step 2: Delete from Courses
            String deleteCourseSql = "DELETE FROM Courses WHERE course_id = ?";
            PreparedStatement deleteCourseStmt = conn.prepareStatement(deleteCourseSql);
            deleteCourseStmt.setInt(1, courseId);
            int rowsDeleted = deleteCourseStmt.executeUpdate();

            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
            return false;
        } finally {
            dbConn.closeConnection();
        }
    }

}
