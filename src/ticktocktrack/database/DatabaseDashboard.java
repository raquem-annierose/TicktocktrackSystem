package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseDashboard {

	public static int countUsersByRole(String role) {
	    String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
	    DatabaseConnection dbConn = new DatabaseConnection();
	    try {
	        dbConn.connectToSQLServer();
	        Connection conn = dbConn.getConnection();

	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, role);
	            ResultSet rs = stmt.executeQuery();
	            if (rs.next()) {
	                return rs.getInt(1);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        dbConn.closeConnection();
	    }
	    return 0;
	}


	public static int countAccountsCreatedBy(String creatorName) {
	    // Assume creatorName = "John Smith", split it into first and last name
	    String sql = """
	        SELECT COUNT(*) FROM users u
	        JOIN admins a ON u.created_by_admin_id = a.admin_id
	        WHERE CONCAT(a.first_name, ' ', a.last_name) = ?
	    """;
	    DatabaseConnection dbConn = new DatabaseConnection();
	    try {
	        dbConn.connectToSQLServer();
	        Connection conn = dbConn.getConnection();

	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, creatorName);  // example: "John Smith"
	            ResultSet rs = stmt.executeQuery();
	            if (rs.next()) {
	                return rs.getInt(1);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        dbConn.closeConnection();
	    }
	    return 0;
	}
	
	//Teacher
	
	 public static int getTotalClassesByTeacher(int teacherId) {
	        int total = 0;
	        DatabaseConnection dbConn = new DatabaseConnection();
	        try {
	            dbConn.connectToSQLServer();
	            Connection conn = dbConn.getConnection();

	            String sql = "SELECT COUNT(*) AS total FROM Classes WHERE teacher_id = ?";
	            PreparedStatement pstmt = conn.prepareStatement(sql);
	            pstmt.setInt(1, teacherId);
	            ResultSet rs = pstmt.executeQuery();

	            if (rs.next()) {
	                total = rs.getInt("total");
	            }
	        } catch (SQLException e) {
	            System.err.println("Error counting classes: " + e.getMessage());
	        } finally {
	            dbConn.closeConnection();
	        }
	        return total;
	    }
	    
	    public static int getTotalUniqueStudentsByTeacher(int teacherId) {
	        int total = 0;
	        DatabaseConnection dbConn = new DatabaseConnection();
	        try {
	            dbConn.connectToSQLServer();
	            Connection conn = dbConn.getConnection();

	            String sql = 
	            	    "SELECT COUNT(DISTINCT e.student_id) AS total " +
	            	    "FROM Enrollments e " +
	            	    "JOIN Classes c ON e.class_id = c.class_id " +
	            	    "WHERE c.teacher_id = ?";

	            PreparedStatement pstmt = conn.prepareStatement(sql);
	            pstmt.setInt(1, teacherId);
	            ResultSet rs = pstmt.executeQuery();

	            if (rs.next()) {
	                total = rs.getInt("total");
	            }
	        } catch (SQLException e) {
	            System.err.println("Error counting students: " + e.getMessage());
	        } finally {
	            dbConn.closeConnection();
	        }
	        return total;
	    }


}
