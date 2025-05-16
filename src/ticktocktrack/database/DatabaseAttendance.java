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
	                     "JOIN Enrollments e ON s.student_id = e.student_id " +
	                     "JOIN Classes cl ON e.class_id = cl.class_id " +
	                     "JOIN Courses c ON cl.course_id = c.course_id " +
	                     "LEFT JOIN Attendance a ON e.enrollment_id = a.enrollment_id AND a.date = ? " +
	                     "WHERE c.course_name = ? AND cl.section = ?";

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
	public static void saveAttendance(String studentId, String date, String status, String reason, String courseName, String section) throws SQLException {
	    try (Connection conn = getConnection()) {
	        conn.setAutoCommit(false);

	        // Get enrollment_id for this student in this course/section
	        String getEnrollmentSql = "SELECT e.enrollment_id FROM Enrollments e " +
	                                  "JOIN Classes cl ON e.class_id = cl.class_id " +
	                                  "JOIN Courses c ON cl.course_id = c.course_id " +
	                                  "WHERE e.student_id = ? AND c.course_name = ? AND cl.section = ?";

	        PreparedStatement psGetEnrollment = conn.prepareStatement(getEnrollmentSql);
	        psGetEnrollment.setString(1, studentId);
	        psGetEnrollment.setString(2, courseName);
	        psGetEnrollment.setString(3, section);

	        ResultSet rs = psGetEnrollment.executeQuery();

	        if (!rs.next()) {
	            throw new SQLException("Enrollment not found for student " + studentId);
	        }

	        int enrollmentId = rs.getInt("enrollment_id");

	        // Check if attendance record exists
	        String checkSql = "SELECT attendance_id FROM Attendance WHERE enrollment_id = ? AND date = ?";
	        PreparedStatement psCheck = conn.prepareStatement(checkSql);
	        psCheck.setInt(1, enrollmentId);
	        psCheck.setString(2, date);
	        ResultSet rsCheck = psCheck.executeQuery();

	        if (rsCheck.next()) {
	            // Update existing attendance
	            int attendanceId = rsCheck.getInt("attendance_id");
	            String updateSql = "UPDATE Attendance SET status = ?, reason = ? WHERE attendance_id = ?";
	            PreparedStatement psUpdate = conn.prepareStatement(updateSql);
	            psUpdate.setString(1, status);
	            psUpdate.setString(2, reason);
	            psUpdate.setInt(3, attendanceId);
	            psUpdate.executeUpdate();
	        } else {
	            // Insert new attendance
	            String insertSql = "INSERT INTO Attendance (enrollment_id, date, status, reason) VALUES (?, ?, ?, ?)";
	            PreparedStatement psInsert = conn.prepareStatement(insertSql);
	            psInsert.setInt(1, enrollmentId);
	            psInsert.setString(2, date);
	            psInsert.setString(3, status);
	            psInsert.setString(4, reason);
	            psInsert.executeUpdate();
	        }
	        conn.commit();
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
