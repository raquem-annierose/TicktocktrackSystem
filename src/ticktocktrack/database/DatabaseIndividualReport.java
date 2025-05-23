package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import ticktocktrack.logic.Session;
import ticktocktrack.logic.Student;
import ticktocktrack.logic.UsersModel;

public class DatabaseIndividualReport {
	
	
	public static Student getStudentById(int studentId, int teacherId) {
	    String query = """
	        SELECT 
	            s.student_id,
	            s.last_name,
	            s.first_name,
	            s.middle_name,
	            s.year_level,
	            s.program,
	            s.section,
	            u.email,
	            COUNT(DISTINCT e.class_id) AS total_classes
	        FROM Students s
	        JOIN Users u ON s.user_id = u.user_id
	        JOIN Enrollments e ON s.student_id = e.student_id
	        JOIN Classes c ON e.class_id = c.class_id
	        WHERE s.student_id = ? AND c.teacher_id = ?
	        GROUP BY 
	            s.student_id, s.last_name, s.first_name, s.middle_name, 
	            s.year_level, s.program, s.section, u.email
	    """;

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

	
	public static List<Student> getStudentsForCurrentTeacherClass(String courseName, String section, String program) {
	    List<Student> students = new ArrayList<>();
	    
	    String query = "SELECT s.student_id, u.username, s.first_name, s.middle_name, s.last_name, u.email, s.year_level " +
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
	        return students;  // empty list
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
