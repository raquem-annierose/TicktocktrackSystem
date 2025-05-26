package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ticktocktrack.logic.ClassAttendanceSummary;
import ticktocktrack.logic.ClassAttendanceSummary.MonthlyAttendanceSummary;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.Student;
import ticktocktrack.logic.UsersModel;

/**
 * The DatabaseIndividualReport class contains methods for retrieving information about students, courses,
 * attendance summaries, and more from the database. This class focuses on operations involving a specific
 * student or teacher.
 */
public class DatabaseIndividualReport {

    /**
     * Retrieves a list of course names associated with a specific student.
     * 
     * @param studentId The ID of the student.
     * @return A list of course names the student is enrolled in.
     */
    public static List<String> getCourseNamesForStudent(int studentId) {
        List<String> courseNames = new ArrayList<>();

        String query =
                "SELECT DISTINCT c.course_name " +
                "FROM Classes c " +
                "JOIN Enrollments e ON c.class_id = e.class_id " +
                "WHERE e.student_id = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, studentId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        courseNames.add(rs.getString("course_name"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching course names for student: " + e.getMessage());
        }

        return courseNames;
    }

    /**
     * Retrieves a list of course names associated with a specific student and teacher.
     * 
     * @param studentId The ID of the student.
     * @param teacherId The ID of the teacher.
     * @return A list of course names the student is enrolled in under the specified teacher.
     */
    public static List<String> getCourseNamesForStudent(int studentId, int teacherId) {
        List<String> courseNames = new ArrayList<>();

        String query =
                "SELECT DISTINCT c.course_name " +
                "FROM Classes c " +
                "JOIN Enrollments e ON c.class_id = e.class_id " +
                "WHERE e.student_id = ? AND c.teacher_id = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, teacherId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        courseNames.add(rs.getString("course_name"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching course names for student: " + e.getMessage());
        }

        return courseNames;
    }

    /**
     * Retrieves detailed information about a specific student under a specific teacher.
     * 
     * @param studentId The ID of the student.
     * @param teacherId The ID of the teacher.
     * @return A Student object containing detailed information about the student, or null if not found.
     */
    public static Student getStudentById(int studentId, int teacherId) {
        String query =
                "SELECT " +
                "s.student_id, s.last_name, s.first_name, s.middle_name, " +
                "s.year_level, s.program, s.section, u.email, u.profile_path, " +
                "COUNT(DISTINCT e.class_id) AS total_classes " +
                "FROM Students s " +
                "JOIN Users u ON s.user_id = u.user_id " +
                "JOIN Enrollments e ON s.student_id = e.student_id " +
                "JOIN Classes c ON e.class_id = c.class_id " +
                "WHERE s.student_id = ? AND c.teacher_id = ? " +
                "GROUP BY s.student_id, s.last_name, s.first_name, s.middle_name, " +
                "s.year_level, s.program, s.section, u.email, u.profile_path";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, teacherId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Student student = new Student();
                        student.setStudentId(rs.getInt("student_id"));
                        student.setFirstName(rs.getString("first_name"));
                        student.setMiddleName(rs.getString("middle_name"));
                        student.setLastName(rs.getString("last_name"));
                        student.setYearLevel(rs.getString("year_level"));
                        student.setProgram(rs.getString("program"));
                        student.setSection(rs.getString("section"));
                        student.setEmail(rs.getString("email"));
                        student.setProfilePath(rs.getString("profile_path"));
                        student.setTotalClasses(rs.getInt("total_classes"));
                        return student;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Retrieves attendance summary for each class of a specific student under a specific teacher.
     * 
     * @param studentId The ID of the student.
     * @param teacherId The ID of the teacher.
     * @return A list of ClassAttendanceSummary objects.
     */
    public static List<ClassAttendanceSummary> getAttendanceSummaryForStudent(int studentId, int teacherId) {
        List<ClassAttendanceSummary> summaries = new ArrayList<>();

        String query =
                "SELECT c.class_id, c.course_name, " +
                "COUNT(CASE WHEN a.status = 'Present' THEN 1 END) AS present_count, " +
                "COUNT(CASE WHEN a.status = 'Absent' THEN 1 END) AS absent_count, " +
                "COUNT(CASE WHEN a.status = 'Excused' THEN 1 END) AS excused_count, " +
                "COUNT(CASE WHEN a.status = 'Late' THEN 1 END) AS late_count " +
                "FROM Classes c " +
                "JOIN Enrollments e ON c.class_id = e.class_id " +
                "LEFT JOIN Attendance a ON e.enrollment_id = a.enrollment_id " +
                "WHERE e.student_id = ? AND c.teacher_id = ? " +
                "GROUP BY c.class_id, c.course_name";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, teacherId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        ClassAttendanceSummary summary = new ClassAttendanceSummary(
                                rs.getInt("class_id"),
                                rs.getString("course_name"),
                                rs.getInt("present_count"),
                                rs.getInt("absent_count"),
                                rs.getInt("excused_count"),
                                rs.getInt("late_count")
                        );
                        summaries.add(summary);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching attendance summary: " + e.getMessage());
        }

        return summaries;
    }

    /**
     * Retrieves monthly attendance summary for a specific student under a specific teacher.
     * 
     * @param studentId The ID of the student.
     * @param teacherId The ID of the teacher.
     * @return A list of MonthlyAttendanceSummary objects.
     */
    public static List<MonthlyAttendanceSummary> getMonthlyAttendanceSummaryForStudent(int studentId, int teacherId) {
        List<MonthlyAttendanceSummary> summaries = new ArrayList<>();

        String query =
                "SELECT YEAR(a.date) AS year, MONTH(a.date) AS month, " +
                "COUNT(CASE WHEN a.status = 'Present' THEN 1 END) AS present_count, " +
                "COUNT(CASE WHEN a.status = 'Absent' THEN 1 END) AS absent_count, " +
                "COUNT(CASE WHEN a.status = 'Excused' THEN 1 END) AS excused_count, " +
                "COUNT(CASE WHEN a.status = 'Late' THEN 1 END) AS late_count " +
                "FROM Enrollments e " +
                "LEFT JOIN Attendance a ON e.enrollment_id = a.enrollment_id " +
                "JOIN Classes c ON e.class_id = c.class_id " +
                "WHERE e.student_id = ? AND c.teacher_id = ? AND a.date IS NOT NULL " +
                "GROUP BY YEAR(a.date), MONTH(a.date) " +
                "ORDER BY YEAR(a.date), MONTH(a.date)";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, teacherId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        MonthlyAttendanceSummary summary = new MonthlyAttendanceSummary(
                                rs.getInt("year"),
                                rs.getInt("month"),
                                rs.getInt("present_count"),
                                rs.getInt("absent_count"),
                                rs.getInt("excused_count"),
                                rs.getInt("late_count")
                        );
                        summaries.add(summary);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching monthly attendance summary: " + e.getMessage());
        }
        return summaries;
    }

    /**
     * Retrieves a list of students for a teacher's specific class, filtered by course name, section, and program.
     * 
     * @param courseName The name of the course.
     * @param section The section of the class.
     * @param program The program of the class.
     * @return A list of Student objects matching the criteria.
     */
	public static List<Student> getStudentsForCurrentTeacherClass(String courseName, String section, String program) {
	    List<Student> students = new ArrayList<>();

	    String query = "SELECT s.student_id, u.username, s.first_name, s.middle_name, s.last_name, " +
	                   "u.email, s.year_level, u.profile_path " +
	                   "FROM Students s " +
	                   "JOIN Users u ON s.user_id = u.user_id " +
	                   "JOIN Enrollments e ON s.student_id = e.student_id " +
	                   "JOIN Classes c ON e.class_id = c.class_id " +
	                   "WHERE c.course_name = ? AND c.section = ? AND c.program = ? AND c.teacher_id = ?";

	    Integer teacherId = null;
	    UsersModel currentUser = Session.getCurrentUser();
	    if (currentUser != null) {
	        teacherId = currentUser.getTeacherId();
	    }

	    if (teacherId == null) {
	        System.err.println("No logged-in teacher found.");
	        return students;  // Return empty list
	    }

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
	                    student.setProfilePath(rs.getString("profile_path")); // ✅ Fetch profile path
	                    students.add(student);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching students for current teacher's class: " + e.getMessage());
	    }

	    return students;
	}

}
