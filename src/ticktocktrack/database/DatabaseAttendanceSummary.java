package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import ticktocktrack.logic.Student;

/**
 * Provides methods to fetch and calculate attendance data for students enrolled in classes.
 */
public class DatabaseAttendanceSummary {

    /**
     * Retrieves a list of students enrolled in a specific class taught by a teacher.
     *
     * @param courseName The name of the course.
     * @param section    The section of the course.
     * @param program    The program the course belongs to.
     * @param teacherId  The ID of the teacher teaching the course.
     * @return A list of students enrolled in the class.
     */
    public static List<Student> getStudentsEnrolledForTeacher(String courseName, String section, String program, int teacherId) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.student_id, u.username, s.first_name, s.middle_name, s.last_name, u.email, s.year_level " +
                       "FROM Students s " +
                       "JOIN Users u ON s.user_id = u.user_id " +
                       "JOIN Enrollments e ON s.student_id = e.student_id " +
                       "JOIN Classes c ON e.class_id = c.class_id " +
                       "WHERE c.course_name = ? AND c.section = ? AND c.program = ? AND c.teacher_id = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, courseName);
                pstmt.setString(2, section);
                pstmt.setString(3, program);
                pstmt.setInt(4, teacherId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Student student = new Student();
                        student.setStudentId(rs.getInt("student_id"));
                        student.setUsername(rs.getString("username"));
                        student.setFirstName(rs.getString("first_name"));
                        student.setMiddleName(rs.getString("middle_name"));
                        student.setLastName(rs.getString("last_name"));
                        student.setEmail(rs.getString("email"));
                        student.setYearLevel(rs.getString("year_level"));
                        students.add(student);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students for teacher's class: " + e.getMessage());
        }
        return students;
    }

    /**
     * Counts the number of absences for a specific student in a class.
     *
     * @param studentId  The ID of the student.
     * @param courseName The name of the course.
     * @param section    The section of the course.
     * @param program    The program the course belongs to.
     * @param teacherId  The ID of the teacher teaching the course.
     * @return The count of absences.
     */
    public static int countAbsences(int studentId, String courseName, String section, String program, int teacherId) {
        int absenceCount = 0;
        String sql = "SELECT COUNT(*) AS absence_count FROM Attendance a " +
                     "JOIN Enrollments e ON a.enrollment_id = e.enrollment_id " +
                     "JOIN Classes c ON e.class_id = c.class_id " +
                     "WHERE e.student_id = ? AND a.status = 'Absent' " +
                     "AND c.course_name = ? AND c.section = ? AND c.program = ? AND c.teacher_id = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, studentId);
                pstmt.setString(2, courseName);
                pstmt.setString(3, section);
                pstmt.setString(4, program);
                pstmt.setInt(5, teacherId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        absenceCount = rs.getInt("absence_count");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting absences for student: " + e.getMessage());
        }
        return absenceCount;
    }

    /**
     * Counts the number of times a student was present in a class.
     *
     * @param studentId  The ID of the student.
     * @param courseName The name of the course.
     * @param section    The section of the course.
     * @param program    The program the course belongs to.
     * @param teacherId  The ID of the teacher teaching the course.
     * @return The count of times the student was present.
     */
    public static int countPresent(int studentId, String courseName, String section, String program, int teacherId) {
        int presentCount = 0;
        String query = "SELECT COUNT(*) AS present_count FROM Attendance a " +
                       "JOIN Enrollments e ON a.enrollment_id = e.enrollment_id " +
                       "JOIN Classes c ON e.class_id = c.class_id " +
                       "WHERE e.student_id = ? AND a.status = 'Present' " +
                       "AND c.course_name = ? AND c.section = ? AND c.program = ? AND c.teacher_id = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, studentId);
                pstmt.setString(2, courseName);
                pstmt.setString(3, section);
                pstmt.setString(4, program);
                pstmt.setInt(5, teacherId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        presentCount = rs.getInt("present_count");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting presents for student: " + e.getMessage());
        }
        return presentCount;
    }

    /**
     * Counts the number of times a student was excused from attendance in a class.
     *
     * @param studentId  The ID of the student.
     * @param courseName The name of the course.
     * @param section    The section of the course.
     * @param program    The program the course belongs to.
     * @param teacherId  The ID of the teacher teaching the course.
     * @return The count of times the student was excused.
     */
    public static int countExcused(int studentId, String courseName, String section, String program, int teacherId) {
        int excusedCount = 0;
        String query = "SELECT COUNT(*) AS excused_count FROM Attendance a " +
                       "JOIN Enrollments e ON a.enrollment_id = e.enrollment_id " +
                       "JOIN Classes c ON e.class_id = c.class_id " +
                       "WHERE e.student_id = ? AND a.status = 'Excused' " +
                       "AND c.course_name = ? AND c.section = ? AND c.program = ? AND c.teacher_id = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, studentId);
                pstmt.setString(2, courseName);
                pstmt.setString(3, section);
                pstmt.setString(4, program);
                pstmt.setInt(5, teacherId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        excusedCount = rs.getInt("excused_count");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting excused for student: " + e.getMessage());
        }
        return excusedCount;
    }

    /**
     * Counts the number of times a student was late for attendance in a class.
     *
     * @param studentId  The ID of the student.
     * @param courseName The name of the course.
     * @param section    The section of the course.
     * @param program    The program the course belongs to.
     * @param teacherId  The ID of the teacher teaching the course.
     * @return The count of times the student was late.
     */
    public static int countLate(int studentId, String courseName, String section, String program, int teacherId) {
        int lateCount = 0;
        String query = "SELECT COUNT(*) AS late_count FROM Attendance a " +
                       "JOIN Enrollments e ON a.enrollment_id = e.enrollment_id " +
                       "JOIN Classes c ON e.class_id = c.class_id " +
                       "WHERE e.student_id = ? AND a.status = 'Late' " +
                       "AND c.course_name = ? AND c.section = ? AND c.program = ? AND c.teacher_id = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, studentId);
                pstmt.setString(2, courseName);
                pstmt.setString(3, section);
                pstmt.setString(4, program);
                pstmt.setInt(5, teacherId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        lateCount = rs.getInt("late_count");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting excused for student: " + e.getMessage());
        }
        return lateCount;
    }
}
