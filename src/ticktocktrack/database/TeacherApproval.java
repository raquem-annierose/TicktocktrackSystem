package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Provides methods for teachers to approve or reject student excuses,
 * retrieve enrollment and user information related to attendance.
 */
public class TeacherApproval {
	
    /**
     * Retrieves the enrollment ID for a student in a specific course.
     *
     * @param studentId The ID of the student.
     * @param courseName The name of the course.
     * @return The enrollment ID if found, otherwise -1.
     */
	public static int getEnrollmentId(int studentId, String courseName) {
	    DatabaseConnection db = new DatabaseConnection();
	    int enrollmentId = -1;

	    try {
	        db.connectToSQLServer();
	        Connection conn = db.getConnection();

	        String query = "SELECT e.enrollment_id FROM Enrollments e " +
                    "JOIN Classes c ON e.class_id = c.class_id " +
                    "WHERE e.student_id = ? AND c.course_name = ?";

	        PreparedStatement stmt = conn.prepareStatement(query);
	        stmt.setInt(1, studentId);
	        stmt.setString(2, courseName);

	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            enrollmentId = rs.getInt("enrollment_id");
	        }

	        rs.close();
	        stmt.close();

	    } catch (SQLException e) {
	        System.err.println("Error getting enrollment ID: " + e.getMessage());
	    } finally {
	        db.closeConnection();
	    }

	    return enrollmentId;
	}

    /**
     * Approves a student's excuse for an absence on a specific date and course,
     * updating or inserting the attendance record accordingly,
     * and sends a notification to the student.
     *
     * @param studentId The ID of the student.
     * @param courseName The name of the course.
     * @param dateString The date of the absence in ISO format (yyyy-MM-dd).
     * @param reason The reason for the excuse.
     * @param teacherId The ID of the approving teacher.
     * @return true if the operation was successful; false otherwise.
     */
	public static boolean approveExcuse(int studentId, String courseName, String dateString, String reason, int teacherId) {
	    DatabaseConnection db = new DatabaseConnection();
	    try {
	        db.connectToSQLServer();
	        Connection conn = db.getConnection();

	        LocalDate attendanceDate = LocalDate.parse(dateString);

	        // Step 1: Get enrollment ID
	        String enrollmentQuery = "SELECT e.enrollment_id FROM Enrollments e " +
                    "JOIN Classes c ON e.class_id = c.class_id " +
                    "WHERE e.student_id = ? AND c.course_name = ?";
	        
	        PreparedStatement enrollmentStmt = conn.prepareStatement(enrollmentQuery);
	        enrollmentStmt.setInt(1, studentId);
	        enrollmentStmt.setString(2, courseName);
	        ResultSet enrollmentRs = enrollmentStmt.executeQuery();

	        boolean success = false;

	        while (enrollmentRs.next()) {
	            int enrollmentId = enrollmentRs.getInt("enrollment_id");

	            // Step 2: Check for existing attendance
	            String attendanceCheck = "SELECT attendance_id FROM Attendance " +
                        "WHERE enrollment_id = ? AND date = ?";

	            PreparedStatement attendanceStmt = conn.prepareStatement(attendanceCheck);
	            attendanceStmt.setInt(1, enrollmentId);
	            attendanceStmt.setDate(2, java.sql.Date.valueOf(attendanceDate));
	            ResultSet attendanceRs = attendanceStmt.executeQuery();

	            if (attendanceRs.next()) {
	                // Update existing attendance
	                int attendanceId = attendanceRs.getInt("attendance_id");
	                String updateQuery = "UPDATE Attendance " +
                            "SET status = 'Excused', reason = ?, approval_status = 'Approved', " +
                            "approved_by = ?, approval_date = ? " +
                            "WHERE attendance_id = ?";
	                
	                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
	                updateStmt.setString(1, reason);
	                updateStmt.setInt(2, teacherId);
	                updateStmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
	                updateStmt.setInt(4, attendanceId);
	                success = updateStmt.executeUpdate() > 0;
	                updateStmt.close();
	            } else {
	                // Insert new attendance
	            	String insertQuery = "INSERT INTO Attendance " +
                            "(enrollment_id, date, status, reason, approval_status, approved_by, approval_date) " +
                            "VALUES (?, ?, 'Excused', ?, 'Approved', ?, ?)";
	            	
	                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
	                insertStmt.setInt(1, enrollmentId);
	                insertStmt.setDate(2, java.sql.Date.valueOf(attendanceDate));
	                insertStmt.setString(3, reason);
	                insertStmt.setInt(4, teacherId);
	                insertStmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
	                success = insertStmt.executeUpdate() > 0;
	                insertStmt.close();
	            }

	            attendanceStmt.close();
	            attendanceRs.close();

	            // Step 3: Send notification if success
	            if (success) {
	                String getUserIdQuery = "SELECT user_id FROM Students WHERE student_id = ?";
	                PreparedStatement userStmt = conn.prepareStatement(getUserIdQuery);
	                userStmt.setInt(1, studentId);
	                ResultSet userRs = userStmt.executeQuery();
	                if (userRs.next()) {
	                    int recipientUserId = userRs.getInt("user_id");
	                    String message = "Your excuse on " + attendanceDate + " for " + courseName + " has been approved.";
	                    String notifyQuery = "INSERT INTO Notifications " +
                                "(recipient_user_id, sender_user_id, message, notification_type, date_sent, is_read) " +
                                "VALUES (?, ?, ?, 'ExcuseApproval', ?, 0)";
	                    
	                    PreparedStatement notifyStmt = conn.prepareStatement(notifyQuery);
	                    notifyStmt.setInt(1, recipientUserId);
	                    notifyStmt.setInt(2, teacherId);  // <-- Include sender_user_id here
	                    notifyStmt.setString(3, message);
	                    notifyStmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
	                    notifyStmt.executeUpdate();
	                    notifyStmt.close();
	                }
	                userRs.close();
	                userStmt.close();
	            }

	        }

	        enrollmentStmt.close();
	        enrollmentRs.close();
	        return success;

	    } catch (SQLException e) {
	        System.err.println("Error approving excuse: " + e.getMessage());
	        return false;
	    } finally {
	        db.closeConnection();
	    }
	}
	
    /**
     * Rejects a student's excuse for an absence on a specific date and course,
     * updating or inserting the attendance record accordingly,
     * and sends a notification to the student.
     *
     * @param studentId The ID of the student.
     * @param courseName The name of the course.
     * @param dateString The date of the absence in ISO format (yyyy-MM-dd).
     * @param teacherId The ID of the teacher rejecting the excuse.
     * @return true if the operation was successful; false otherwise.
     */
	public static boolean rejectExcuse(int studentId, String courseName, String dateString, int teacherId) {
	    DatabaseConnection db = new DatabaseConnection();
	    boolean success = false;

	    try {
	        db.connectToSQLServer();
	        Connection conn = db.getConnection();

	        LocalDate attendanceDate = LocalDate.parse(dateString);

	        String enrollmentQuery = "SELECT e.enrollment_id FROM Enrollments e " +
                    "JOIN Classes c ON e.class_id = c.class_id " +
                    "WHERE e.student_id = ? AND c.course_name = ?";
	        
	        PreparedStatement enrollmentStmt = conn.prepareStatement(enrollmentQuery);
	        enrollmentStmt.setInt(1, studentId);
	        enrollmentStmt.setString(2, courseName);
	        ResultSet enrollmentRs = enrollmentStmt.executeQuery();

	        if (!enrollmentRs.isBeforeFirst()) {
	            System.out.println("No enrollments found for student " + studentId + " and course " + courseName);
	        }

	        while (enrollmentRs.next()) {
	            int enrollmentId = enrollmentRs.getInt("enrollment_id");

	            String attendanceCheck = "SELECT attendance_id FROM Attendance " +
                        "WHERE enrollment_id = ? AND date = ?";
	            
	            PreparedStatement attendanceStmt = conn.prepareStatement(attendanceCheck);
	            attendanceStmt.setInt(1, enrollmentId);
	            attendanceStmt.setDate(2, java.sql.Date.valueOf(attendanceDate));
	            ResultSet attendanceRs = attendanceStmt.executeQuery();
	            if (!attendanceRs.isBeforeFirst()) {
	                // No attendance record found, so insert one with Absent + Rejected
	                System.out.println("No attendance record found for enrollment " + enrollmentId + " on date " + attendanceDate + ", inserting absent record.");

	                String insertQuery = "INSERT INTO Attendance " +
                            "(enrollment_id, date, status, reason, approval_status, approved_by, approval_date) " +
                            "VALUES (?, ?, 'Absent', NULL, 'Rejected', ?, ?)";
	                
	                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
	                insertStmt.setInt(1, enrollmentId);
	                insertStmt.setDate(2, java.sql.Date.valueOf(attendanceDate));
	                insertStmt.setInt(3, teacherId);
	                insertStmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));

	                int rowsInserted = insertStmt.executeUpdate();
	                System.out.println("Inserted attendance rows: " + rowsInserted);

	                success = rowsInserted > 0;
	                insertStmt.close();

	            } else {
	                // Attendance record(s) exist, update all of them
	                while (attendanceRs.next()) {
	                    int attendanceId = attendanceRs.getInt("attendance_id");

	                    String updateQuery = "UPDATE Attendance " +
                                "SET status = 'Absent', reason = NULL, approval_status = 'Rejected', " +
                                "approved_by = ?, approval_date = ? " +
                                "WHERE attendance_id = ?";
	                    
	                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
	                    updateStmt.setInt(1, teacherId);
	                    updateStmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
	                    updateStmt.setInt(3, attendanceId);

	                    int rowsUpdated = updateStmt.executeUpdate();
	                    System.out.println("Update attendance rows affected: " + rowsUpdated);

	                    success = rowsUpdated > 0;
	                    updateStmt.close();
	                }
	            }

	            if (success) {
	                String getUserIdQuery = "SELECT user_id FROM Students WHERE student_id = ?";
	                PreparedStatement userStmt = conn.prepareStatement(getUserIdQuery);
	                userStmt.setInt(1, studentId);
	                ResultSet userRs = userStmt.executeQuery();

	                if (userRs.next()) {
	                    int recipientUserId = userRs.getInt("user_id");
	                    String message = "Your excuse on " + attendanceDate + " for " + courseName + " has been rejected.";
	                    String notifyQuery = "INSERT INTO Notifications " +
                                "(recipient_user_id, sender_user_id, message, notification_type, date_sent, is_read) " +
                                "VALUES (?, ?, ?, 'ExcuseRejection', ?, 0)";
	                    
	                    PreparedStatement notifyStmt = conn.prepareStatement(notifyQuery);
	                    notifyStmt.setInt(1, recipientUserId);
	                    notifyStmt.setInt(2, teacherId);  // assuming teacherId as sender_user_id
	                    notifyStmt.setString(3, message);
	                    notifyStmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
	                    notifyStmt.executeUpdate();
	                    notifyStmt.close();
	                }
	                userRs.close();
	                userStmt.close();
	            }
	        }

	        enrollmentStmt.close();
	        enrollmentRs.close();
	        return success;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    } finally {
	        db.closeConnection();
	    }
	}

    /**
     * Checks if a user is a student based on their user ID.
     *
     * @param userId The ID of the user.
     * @return true if the user has a student role, false otherwise.
     */
	public static boolean isUserStudent(int userId) {
	    DatabaseConnection db = new DatabaseConnection();
	    boolean isStudent = false;
	    try {
	        db.connectToSQLServer();
	        Connection conn = db.getConnection();

	        String sql = "SELECT role FROM Users WHERE user_id = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, userId);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    String role = rs.getString("role");
	                    isStudent = role.equalsIgnoreCase("student");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        db.closeConnection();
	    }
	    return isStudent;
	}

    /**
     * Retrieves the student ID associated with a given user ID.
     *
     * @param userId The user ID.
     * @return The student ID if found; -1 otherwise.
     */
	public static int getStudentIdByUserId(int userId) {
	    int studentId = -1;
	    DatabaseConnection db = new DatabaseConnection();

	    try {
	        db.connectToSQLServer();
	        Connection conn = db.getConnection();

	        String sql = "SELECT student_id FROM Students WHERE user_id = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, userId);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    studentId = rs.getInt("student_id");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        db.closeConnection();
	    }

	    return studentId;
	}
	
    /**
     * Retrieves the teacher ID associated with a given user ID.
     * 
     * @param userId the user ID
     * @return the teacher ID, or -1 if not found
     */
	public static int getTeacherIdByUserId(int userId) {
	    int teacherId = -1;
	    DatabaseConnection db = new DatabaseConnection();

	    try {
	        db.connectToSQLServer();
	        Connection conn = db.getConnection();

	        String sql = "SELECT teacher_id FROM Teachers WHERE user_id = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, userId);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    teacherId = rs.getInt("teacher_id");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        db.closeConnection();
	    }
	    return teacherId;
	}

}
