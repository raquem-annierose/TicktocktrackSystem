package ticktocktrack.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ticktocktrack.logic.CourseInfo;
import ticktocktrack.logic.Student;

public class DatabaseAttendance {

    /**
     * Fetches list of students with their attendance status for a given teacher (userId),
     * course, section, and date.
     */
    
    public static List<String[]> getTeacherCoursesSections(int teacherId) {
        List<String[]> courses = new ArrayList<>();
        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "SELECT DISTINCT course_name, section, program " +
                         "FROM Classes " +
                         "WHERE teacher_id = ? " +
                         "ORDER BY course_name, section";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                String section = rs.getString("section");
                String program = rs.getString("program");
                if (program == null) program = "N/A";

                courses.add(new String[] { courseName, section, program });
            }

        } catch (SQLException e) {
            System.err.println("Error fetching teacher courses and sections: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }

        return courses;
    }
    
    public static CourseInfo[] getCoursesForTeacher(int teacherId) {
        List<CourseInfo> courseInfoList = new ArrayList<>();
        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "SELECT DISTINCT course_name, section, program " +
                         "FROM Classes " +
                         "WHERE teacher_id = ? " +
                         "ORDER BY course_name, section";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                String section = rs.getString("section");
                String program = rs.getString("program");
                if (program == null) program = "N/A";

                courseInfoList.add(new CourseInfo(courseName, section, program));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching teacher courses and sections: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }

        return courseInfoList.toArray(new CourseInfo[0]);
    }


    public static List<Student> getStudentsByProgramSection(String program, String section) {
        List<Student> students = new ArrayList<>();
        DatabaseConnection dbConn = new DatabaseConnection();

        String sql = "SELECT student_id, last_name, first_name, middle_name FROM Students " +
                     "WHERE program = ? AND section = ? ORDER BY last_name, first_name";

        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, program);
                pstmt.setString(2, section);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int studentId = rs.getInt("student_id");
                        String lastName = rs.getString("last_name");
                        String firstName = rs.getString("first_name");
                        String middleName = rs.getString("middle_name");

                        Student student = new Student(studentId, lastName, firstName, middleName, "", "Pending", "");
                        students.add(student);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            dbConn.closeConnection();
        }

        return students;
    }


    public static List<Student> fetchStudentsWithAttendanceForDate(int userId, String courseName, String section, String date) {
        List<Student> students = new ArrayList<>();
        DatabaseConnection dbConn = new DatabaseConnection();

        String sql = "SELECT s.student_id, s.last_name, s.first_name, s.middle_name, " +
                     "COALESCE(a.status, 'Pending') AS status, COALESCE(a.reason, '') AS reason " +
                     "FROM Users u " +
                     "JOIN Teachers t ON u.user_id = t.user_id " +
                     "JOIN Classes cl ON t.teacher_id = cl.teacher_id " +
                     "JOIN Enrollments e ON cl.class_id = e.class_id " +
                     "JOIN Students s ON e.student_id = s.student_id " +
                     "LEFT JOIN Attendance a ON e.enrollment_id = a.enrollment_id AND a.date = ? " +
                     "WHERE u.user_id = ? " +
                     "AND cl.section = ? " +
                     "AND cl.course_id = (SELECT course_id FROM Courses WHERE course_name = ?) " +
                     "ORDER BY s.last_name, s.first_name";

        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, date);
                pstmt.setInt(2, userId);
                pstmt.setString(3, section);
                pstmt.setString(4, courseName);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int studentId = rs.getInt("student_id");
                        String lastName = rs.getString("last_name");
                        String firstName = rs.getString("first_name");
                        String middleName = rs.getString("middle_name");
                        String status = rs.getString("status");
                        String reason = rs.getString("reason");

                        students.add(new Student(studentId, lastName, firstName, middleName, date, status, reason));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            dbConn.closeConnection();
        }

        return students;
    }

    public static List<Student> getStudentsEnrolled(String courseName, String program, String section) {
        List<Student> students = new ArrayList<>();
        DatabaseConnection dbConn = new DatabaseConnection();

        String sql = "SELECT s.student_id, s.last_name, s.first_name, s.middle_name " +
                     "FROM Students s " +
                     "JOIN Enrollments e ON s.student_id = e.student_id " +
                     "JOIN Classes c ON e.class_id = c.class_id " +
                     "WHERE c.course_name = ? " +
                     "AND c.program = ? " +
                     "AND c.section = ? " +
                     "ORDER BY s.last_name, s.first_name";

        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, courseName);
                pstmt.setString(2, program);
                pstmt.setString(3, section);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int studentId = rs.getInt("student_id");
                        String lastName = rs.getString("last_name");
                        String firstName = rs.getString("first_name");
                        String middleName = rs.getString("middle_name");

                        students.add(new Student(studentId, lastName, firstName, middleName, "", "Pending", ""));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            dbConn.closeConnection();
        }

        return students;
    }

    /**
     * Saves or updates attendance record for a student on a specific date.
     */
    public static void saveAttendance(int studentId, String date, String status, String reason, String program, String courseName, String section) throws SQLException {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();
            conn.setAutoCommit(false);
            // Get enrollment_id
            String enrollmentSql = "SELECT e.enrollment_id " +
                                   "FROM Enrollments e " +
                                   "JOIN Classes c ON e.class_id = c.class_id " +
                                   "WHERE e.student_id = ? AND c.course_name = ? AND c.program = ? AND c.section = ?";
            Integer enrollmentId = null;
            try (PreparedStatement psEnroll = conn.prepareStatement(enrollmentSql)) {
                psEnroll.setInt(1, studentId);
                psEnroll.setString(2, courseName.trim());
                psEnroll.setString(3, program.trim());
                psEnroll.setString(4, section.trim());
                try (ResultSet rs = psEnroll.executeQuery()) {
                    if (rs.next()) {
                        enrollmentId = rs.getInt("enrollment_id");
                    } else {
                        throw new SQLException("Enrollment not found for studentId " + studentId + ", course " + courseName + ", program " + program + ", section " + section);
                    }
                }
            }
            // Check for existing attendance
            String checkSql = "SELECT attendance_id FROM Attendance WHERE enrollment_id = ? AND date = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, enrollmentId);
                psCheck.setString(2, date);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next()) {
                        // Update
                        int attendanceId = rsCheck.getInt("attendance_id");
                        String updateSql = "UPDATE Attendance SET status = ?, reason = ? WHERE attendance_id = ?";
                        try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                            psUpdate.setString(1, status);
                            psUpdate.setString(2, reason);
                            psUpdate.setInt(3, attendanceId);
                            psUpdate.executeUpdate();
                        }
                    } else {
                        // Insert with default approval status
                        String insertSql = "INSERT INTO Attendance (enrollment_id, date, status, reason, approval_status) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                            psInsert.setInt(1, enrollmentId);
                            psInsert.setString(2, date);
                            psInsert.setString(3, status);
                            psInsert.setString(4, reason);
                            psInsert.setString(5, "Pending"); // default approval status
                            psInsert.executeUpdate();
                        }
                    }
                }
            }
            conn.commit(); // commit all changes
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            dbConn.closeConnection();
        }
    }

    public static void updateStudentAttendance(int studentId, String date, String status, String program, String courseName, String section) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();
            conn.setAutoCommit(false);

            // Get enrollment_id
            String enrollmentSql = "SELECT e.enrollment_id " +
                                   "FROM Enrollments e " +
                                   "JOIN Classes c ON e.class_id = c.class_id " +
                                   "WHERE e.student_id = ? AND c.course_name = ? AND c.program = ? AND c.section = ?";
            Integer enrollmentId = null;
            try (PreparedStatement psEnroll = conn.prepareStatement(enrollmentSql)) {
                psEnroll.setInt(1, studentId);
                psEnroll.setString(2, courseName.trim());
                psEnroll.setString(3, program.trim());
                psEnroll.setString(4, section.trim());
                try (ResultSet rs = psEnroll.executeQuery()) {
                    if (rs.next()) {
                        enrollmentId = rs.getInt("enrollment_id");
                    } else {
                        throw new SQLException("Enrollment not found.");
                    }
                }
            }

            // Update attendance
            String updateSql = "UPDATE Attendance SET status = ? WHERE enrollment_id = ? AND date = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setString(1, status);
                psUpdate.setInt(2, enrollmentId);
                psUpdate.setString(3, date);
                int rowsUpdated = psUpdate.executeUpdate();
                if (rowsUpdated == 0) {
                    System.out.println("No attendance record found to update.");
                }
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConn.closeConnection();
        }
    }



    /**
     * Fetches all distinct course names from the Courses table.
     */
    public static List<String> fetchAvailableCourses() {
        List<String> courses = new ArrayList<>();
        DatabaseConnection dbConn = new DatabaseConnection();

        String sql = "SELECT DISTINCT course_name FROM Courses";

        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    courses.add(rs.getString("course_name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch courses: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }

        return courses;
    }
    
}
