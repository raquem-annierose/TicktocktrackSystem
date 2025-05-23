package ticktocktrack.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ticktocktrack.logic.Session;
import ticktocktrack.logic.Student;

public class DatabaseRegisterClass {

    public static boolean addClass(int teacherId, String courseName, String section, String program) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();
            String sql = "INSERT INTO Classes (teacher_id, course_name, section, program) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            pstmt.setString(2, courseName);
            pstmt.setString(3, section);
            pstmt.setString(4, program);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting class: " + e.getMessage());
            return false;
        } finally {
            dbConn.closeConnection();
        }
    }

    public static int getClassId(String courseName, int teacherId, String section, String program) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "SELECT class_id FROM Classes WHERE course_name = ? AND teacher_id = ? AND section = ? AND program = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseName);
            pstmt.setInt(2, teacherId);
            pstmt.setString(3, section);
            pstmt.setString(4, program);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("class_id");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving class ID: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
        return -1; // Not found
    }

    public static boolean classExists(int teacherId, String courseName, String section, String program) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            int currentAdminUserId = Session.getSenderUserId();

            // SQL to check for classes created by teachers that were created by the current admin
            String sql = 
            	    "SELECT COUNT(*) AS count " +
            	    "FROM Classes c " +
            	    "JOIN Teachers t ON c.teacher_id = t.teacher_id " +
            	    "JOIN Users u ON t.user_id = u.user_id " +
            	    "WHERE c.teacher_id = ? " +
            	    "AND c.course_name = ? " +
            	    "AND c.section = ? " +
            	    "AND c.program = ? " +
            	    "AND u.created_by_admin_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            pstmt.setString(2, courseName);
            pstmt.setString(3, section);
            pstmt.setString(4, program);
            pstmt.setInt(5, currentAdminUserId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if class exists: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
        return false;
    }

    public static Set<Integer> getEnrolledStudentIds(int classId) {
        Set<Integer> ids = new HashSet<>();
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "SELECT student_id FROM Enrollments WHERE class_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, classId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt("student_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving enrolled student IDs: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
        return ids;
    }

    public static boolean enrollStudentInClass(int studentId, int classId) {
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "INSERT INTO Enrollments (student_id, class_id) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, classId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error enrolling student: " + e.getMessage());
            return false;
        } finally {
            dbConn.closeConnection();
        }
    }

    public static List<Student> getUnenrolledStudents(int classId) {
        List<Student> students = new ArrayList<>();
        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "SELECT s.student_id, s.user_id, u.username, u.email, " +
                         "s.first_name, s.middle_name, s.last_name, " +
                         "s.year_level, s.program, s.section " +
                         "FROM Students s " +
                         "JOIN Users u ON s.user_id = u.user_id " +
                         "WHERE s.student_id NOT IN ( " +
                         "    SELECT e.student_id FROM Enrollments e WHERE e.class_id = ? " +
                         ")";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, classId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setUserId(rs.getInt("user_id"));
                student.setUsername(rs.getString("username"));
                student.setEmail(rs.getString("email"));
                student.setFirstName(rs.getString("first_name"));
                student.setMiddleName(rs.getString("middle_name"));
                student.setLastName(rs.getString("last_name"));
                student.setYearLevel(rs.getString("year_level"));
                student.setProgram(rs.getString("program"));
                student.setSection(rs.getString("section"));
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving unenrolled students: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
        return students;
    }
    
   

}
