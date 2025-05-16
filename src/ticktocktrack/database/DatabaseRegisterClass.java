package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAddCourse {

    // Method to add a course with a program
    public static boolean addCourse(String courseName, String section, String program) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            // Insert course along with its program
            String sql = "INSERT INTO Courses (course_name, section, program) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseName);
            pstmt.setString(2, section);
            pstmt.setString(3, program);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting course: " + e.getMessage());
            return false;
        } finally {
            dbConn.closeConnection();
        }
    }

    // Method to check if a course exists (updated to include program)
    public static boolean courseExists(String courseName, String section, String program) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "SELECT COUNT(*) FROM Courses WHERE course_name = ? AND section = ? AND program = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseName);
            pstmt.setString(2, section);
            pstmt.setString(3, program);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if course exists: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
        return false;
    }

    // Method to retrieve students (updated to include program and email)
    public static List<Student> getStudents() {
        List<Student> students = new ArrayList<>();
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "SELECT * FROM Students";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("student_id"),
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("last_name"),
                        rs.getString("section"),
                        rs.getString("year_level"),
                        rs.getString("program")  // Include program
                );
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving students: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
        return students;
    }

    // Get course ID by name and section (updated to include program in case it's necessary)
    public static int getCourseId(String courseName, String section, String program) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "SELECT course_id FROM Courses WHERE course_name = ? AND section = ? AND program = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseName);
            pstmt.setString(2, section);
            pstmt.setString(3, program);  // Include program in the query

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("course_id");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving course ID: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
        return -1;  // Return -1 if course is not found
    }

    /// Enroll a student in the course, including their program
    public static boolean enrollStudentInCourse(int studentId, int courseId, String section) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            // Step 1: Get the program of the student
            String getProgramSql = "SELECT program FROM Students WHERE student_id = ?";
            PreparedStatement getProgramStmt = conn.prepareStatement(getProgramSql);
            getProgramStmt.setInt(1, studentId);
            ResultSet programRs = getProgramStmt.executeQuery();

            String program = null;
            if (programRs.next()) {
                program = programRs.getString("program");
            } else {
                System.err.println("No student found with ID: " + studentId);
                return false; // student not found
            }

            // Step 2: Insert enrollment with program
            String sql = "INSERT INTO StudentEnrollments (student_id, course_id, section, program) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, section);
            pstmt.setString(4, program); // Insert program into StudentEnrollments

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error enrolling student in course: " + e.getMessage());
            return false;
        } finally {
            dbConn.closeConnection();
        }
    }

}
