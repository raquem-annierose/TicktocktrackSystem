package ticktocktrack.database;

import ticktocktrack.gui.AttendanceStatusPanel;
import ticktocktrack.logic.Session;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseStudentViewMyAttendance {

	public static List<String> getStudentClassesWithTeachers() {
	    List<String> classes = new ArrayList<>();
	    int userId = Session.getSenderUserId(); // get logged-in user's user_id

	    if (userId == -1) {
	        System.err.println("No user is currently logged in.");
	        return classes;
	    }

	    String query = """
	        SELECT c.course_name,
	               t.first_name AS teacher_first,
	               t.last_name AS teacher_last,
	               u.profile_path
	        FROM Students s
	        JOIN Enrollments e ON s.student_id = e.student_id
	        JOIN Classes c ON e.class_id = c.class_id
	        JOIN Teachers t ON c.teacher_id = t.teacher_id
	        JOIN Users u ON t.user_id = u.user_id
	        WHERE s.user_id = ?
	    """;

	    DatabaseConnection db = new DatabaseConnection();
	    Connection conn = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    try {
	        db.connectToSQLServer();
	        conn = db.getConnection();

	        stmt = conn.prepareStatement(query);
	        stmt.setInt(1, userId);

	        rs = stmt.executeQuery();
	        while (rs.next()) {
	            String courseName = rs.getString("course_name");
	            String teacherFirst = rs.getString("teacher_first");
	            String teacherLast = rs.getString("teacher_last");
	            String profilePath = rs.getString("profile_path");

	            String teacherName = String.format("Teacher: %s %s", teacherFirst, teacherLast);

	            // Format: courseName | teacherName | profilePath
	            classes.add(courseName + " | " + teacherName + " | " + (profilePath != null ? profilePath : ""));
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching student classes: " + e.getMessage());
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (stmt != null) stmt.close();
	        } catch (SQLException e) {
	            System.err.println("Error closing statement/result set: " + e.getMessage());
	        }

	        db.closeConnection();
	    }

	    return classes;
	}
	
	public static List<AttendanceStatusPanel.AttendanceRecord> getAttendanceHistoryForCourse(String courseName) {
        int userId = Session.getSenderUserId(); // current logged-in user
        List<AttendanceStatusPanel.AttendanceRecord> history = new ArrayList<>();
        if (userId == -1) {
            System.err.println("No user is currently logged in.");
            return history;
        }

        String query = """
            SELECT a.date, a.status
            FROM Attendance a
            JOIN Enrollments e ON a.enrollment_id = e.enrollment_id
            JOIN Students s ON e.student_id = s.student_id
            JOIN Classes c ON e.class_id = c.class_id
            WHERE s.user_id = ?
              AND c.course_name = ?
            ORDER BY a.date DESC
        """;

        DatabaseConnection db = new DatabaseConnection();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            db.connectToSQLServer();
            conn = db.getConnection();

            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, courseName);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Date sqlDate = rs.getDate("date");
                String status = rs.getString("status");
                if (sqlDate != null && status != null) {
                    history.add(new AttendanceStatusPanel.AttendanceRecord(sqlDate.toLocalDate(), status));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching attendance history: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
            db.closeConnection();
        }

        return history;
    }

    /**
     * Optionally, if you want to get just today's attendance status (single record),
     * you can implement this by querying with a date filter for today.
     */
    public static String getTodayAttendanceStatusForCourse(String courseName) {
        int userId = Session.getSenderUserId();
        if (userId == -1) {
            System.err.println("No user is currently logged in.");
            return "No User Logged In";
        }

        String attendanceStatus = "No attendance record for today";

        String query = """
            SELECT a.status
            FROM Attendance a
            JOIN Enrollments e ON a.enrollment_id = e.enrollment_id
            JOIN Students s ON e.student_id = s.student_id
            JOIN Classes c ON e.class_id = c.class_id
            WHERE s.user_id = ?
              AND c.course_name = ?
              AND a.date = CAST(GETDATE() AS DATE)
        """;

        DatabaseConnection db = new DatabaseConnection();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            db.connectToSQLServer();
            conn = db.getConnection();

            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, courseName);

            rs = stmt.executeQuery();

            if (rs.next()) {
                attendanceStatus = rs.getString("status");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching attendance status: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
            db.closeConnection();
        }

        return attendanceStatus;
    }




}
