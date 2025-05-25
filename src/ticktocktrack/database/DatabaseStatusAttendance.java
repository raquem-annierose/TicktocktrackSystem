package ticktocktrack.database;

import ticktocktrack.logic.AttendanceStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods to retrieve attendance-related information from the database.
 */
public class DatabaseStatusAttendance {

    /**
     * Retrieves the list of subjects that a student is currently enrolled in.
     *
     * @param studentId The ID of the student.
     * @return A list of course names that the student is enrolled in.
     */
    public static List<String> getEnrolledStudentSubjects(int studentId) {
        List<String> subjects = new ArrayList<>();
        String sql = "SELECT c.course_name FROM Classes c "
                   + "JOIN Enrollments e ON c.class_id = e.class_id "
                   + "WHERE e.student_id = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        subjects.add(rs.getString("course_name"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving enrolled subjects for student_id " + studentId + ": " + e.getMessage());
        }

        return subjects;
    }

    /**
     * Retrieves attendance statistics for a student in a specific subject.
     *
     * @param studentId   The ID of the student.
     * @param subjectName The name of the subject/course.
     * @return An AttendanceStats object containing counts of Present, Absent, Late, and Excused statuses.
     */
    public static AttendanceStats getAttendanceStats(int studentId, String subjectName) {
        AttendanceStats stats = new AttendanceStats(0, 0, 0, 0);

        String sql = "SELECT a.status, COUNT(*) AS count " +
                     "FROM Attendance a " +
                     "JOIN Enrollments e ON a.enrollment_id = e.enrollment_id " +
                     "JOIN Classes c ON e.class_id = c.class_id " +
                     "WHERE e.student_id = ? AND c.course_name = ? " +
                     "GROUP BY a.status";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, studentId);
                stmt.setString(2, subjectName);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("count");

                    switch (status) {
                        case "Present":
                            stats.present = count;
                            break;
                        case "Absent":
                            stats.absent = count;
                            break;
                        case "Late":
                            stats.late = count;
                            break;
                        case "Excused":
                            stats.excused = count;
                            break;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving attendance stats: " + e.getMessage());
        }

        return stats;
    }

    /**
     * Retrieves the full name of the professor teaching a specific subject.
     *
     * @param subjectName The name of the subject/course.
     * @return The full name of the professor, or "Unknown Professor" if not found.
     */
    public static String getProfessorNameBySubject(String subjectName) {
        String professorName = "Unknown Professor";
        String sql = "SELECT t.first_name, t.last_name FROM Classes c "
                   + "JOIN Teachers t ON c.teacher_id = t.teacher_id "
                   + "WHERE c.course_name = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, subjectName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String firstName = rs.getString("first_name");
                        String lastName = rs.getString("last_name");
                        professorName = firstName + " " + lastName;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving professor name for subject " + subjectName + ": " + e.getMessage());
        }

        return professorName;
    }

}