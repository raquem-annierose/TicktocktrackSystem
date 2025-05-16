package ticktocktrack.database;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import ticktocktrack.gui.TeacherMarkAttendanceCenterPanel.Student;

public class DatabaseAttendance {

    /**
     * Fetches students enrolled in a course and section, including their attendance status for a specific date.
     * If no attendance record exists for that date, status defaults to "Pending" and reason is empty.
     */
    public static List<Student> fetchStudentsWithAttendanceForDate(String courseName, String section, String date) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.student_id, s.last_name, s.first_name, s.middle_name, " +
                     "COALESCE(a.status, 'Pending') AS status, COALESCE(a.reason, '') AS reason " +
                     "FROM Students s " +
                     "JOIN StudentEnrollments se ON s.student_id = se.student_id " +
                     "JOIN Courses c ON se.course_id = c.course_id " +
                     "LEFT JOIN Attendance a ON s.student_id = a.student_id AND a.date = ? " +
                     "WHERE c.course_name = ? AND c.section = ? " +
                     "ORDER BY s.last_name, s.first_name";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date);
            pstmt.setString(2, courseName);
            pstmt.setString(3, section);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String studentId = rs.getString("student_id");
                    String lastName = rs.getString("last_name");
                    String firstName = rs.getString("first_name");
                    String middleName = rs.getString("middle_name");
                    String status = rs.getString("status");
                    String reason = rs.getString("reason");

                    students.add(new Student(studentId, lastName, firstName, middleName, date, status, reason));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching students with attendance: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Saves or updates attendance record for a student on a specific date.
     */
    public static void saveAttendance(String studentId, String date, String status, String reason) {
        String checkSql = "SELECT attendance_id FROM Attendance WHERE student_id = ? AND date = ?";
        String updateSql = "UPDATE Attendance SET status = ?, reason = ? WHERE student_id = ? AND date = ?";
        String insertSql = "INSERT INTO Attendance (student_id, date, status, reason) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, studentId);
            checkStmt.setString(2, date);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Update existing record
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, status);
                        updateStmt.setString(2, reason);
                        updateStmt.setString(3, studentId);
                        updateStmt.setString(4, date);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Insert new record
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, studentId);
                        insertStmt.setString(2, date);
                        insertStmt.setString(3, status);
                        insertStmt.setString(4, reason);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saving attendance: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<String> fetchAvailableCourses() {
        List<String> courses = new ArrayList<>();
        String sql = "SELECT DISTINCT course_name FROM Courses";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                courses.add(rs.getString("course_name"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch courses: " + e.getMessage());
            e.printStackTrace();
        }
        return courses;
    }

    private static Connection getConnection() throws SQLException {
        DatabaseConnection dbConn = new DatabaseConnection();
        dbConn.connectToSQLServer();
        return dbConn.getConnection();
    }
}
