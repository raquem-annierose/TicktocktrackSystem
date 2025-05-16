package ticktocktrack.database;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import ticktocktrack.gui.TeacherMarkAttendanceCenterPanel.Student;

public class DatabaseAttendance {

    public static List<Student> fetchStudents(String courseName, String section) {
        List<Student> students = new ArrayList<>();
        try (Connection conn = getConnection()) {

            String sql = "SELECT s.student_id, s.last_name, s.first_name, s.middle_name, " +
                         "a.status, a.reason " +
                         "FROM Students s " +
                         "JOIN StudentEnrollments se ON s.student_id = se.student_id " +
                         "JOIN Courses c ON se.course_id = c.course_id " +
                         "LEFT JOIN Attendance a ON s.student_id = a.student_id AND a.date = ? " +
                         "WHERE c.course_name = ? AND c.section = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            pstmt.setString(1, currentDate);
            pstmt.setString(2, courseName);
            pstmt.setString(3, section);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String lastName = rs.getString("last_name");
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                String status = rs.getString("status");
                if (status == null) status = "Pending";
                String reason = rs.getString("reason");
                if (reason == null) reason = "";

                students.add(new Student(false, studentId, lastName, firstName, middleName, currentDate, status, reason));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching students: " + e.getMessage());
        }
        return students;
    }

    /**
     * Saves or updates attendance record for a student on a specific date.
     */
    public static void saveAttendance(String studentId, String date, String status, String reason) {
        try (Connection conn = getConnection()) {
            // Check if attendance already exists for this student and date
            String checkSql = "SELECT attendance_id FROM Attendance WHERE student_id = ? AND date = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, studentId);
            checkStmt.setString(2, date);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Update existing attendance record
                String updateSql = "UPDATE Attendance SET status = ?, reason = ? WHERE student_id = ? AND date = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, status);
                updateStmt.setString(2, reason);
                updateStmt.setString(3, studentId);
                updateStmt.setString(4, date);
                updateStmt.executeUpdate();
            } else {
                // Insert new attendance record
                String insertSql = "INSERT INTO Attendance (student_id, date, status, reason) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, studentId);
                insertStmt.setString(2, date);
                insertStmt.setString(3, status);
                insertStmt.setString(4, reason);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error saving attendance: " + e.getMessage());
        }
    }

    public static List<String> fetchAvailableCourses() {
        List<String> courses = new ArrayList<>();
        try {
            Connection conn = new DatabaseConnection().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT course_name FROM Courses");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(rs.getString("course_name"));
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Failed to fetch courses: " + e.getMessage());
        }
        return courses;
    }

    private static Connection getConnection() throws SQLException {
        DatabaseConnection dbConn = new DatabaseConnection();
        dbConn.connectToSQLServer();
        return dbConn.getConnection();
    }
}
