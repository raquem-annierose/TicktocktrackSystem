package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ticktocktrack.logic.CourseInfo;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.Student;
import ticktocktrack.logic.UsersModel;
import ticktocktrack.logic.ViewClassList;

public class DatabaseViewClassList {
	
	
	public static List<CourseInfo> getClassesByTeacher(int teacherId) {
	    DatabaseConnection dbConn = new DatabaseConnection();
	    List<CourseInfo> classList = new ArrayList<>();

	    String query = "SELECT course_name, section, program FROM Classes WHERE teacher_id = ?";

	    try {
	        dbConn.connectToSQLServer(); // Establish connection
	        Connection conn = dbConn.getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
	            stmt.setInt(1, teacherId);
	            ResultSet rs = stmt.executeQuery();

	            while (rs.next()) {
	                String courseName = rs.getString("course_name");
	                String section = rs.getString("section");
	                String program = rs.getString("program");
	                classList.add(new CourseInfo(courseName, section, program));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching classes for teacher ID " + teacherId + ": " + e.getMessage());
	    } finally {
	        dbConn.closeConnection();
	    }

	    return classList;
	}



	
	public static List<String[]> getCoursesByTeacherId() {
	    DatabaseConnection dbConn = new DatabaseConnection();

	    UsersModel currentUser = Session.getCurrentUser();
	    if (currentUser == null) {
	        System.err.println("Error: No user is logged in.");
	        return new ArrayList<>();
	    }

	    Integer teacherId = currentUser.getTeacherId();  // assuming getTeacherId() returns Integer or int wrapper
	    if (teacherId == null) {
	        System.err.println("Error: teacherId is null. The logged in user is not a teacher.");
	        return new ArrayList<>();
	    }

	    List<String[]> courses = new ArrayList<>();

	    try {
	        dbConn.connectToSQLServer();
	        Connection conn = dbConn.getConnection();

	        String sql = """
	            SELECT c.course_name, cl.section, cl.program
	            FROM Classes cl
	            JOIN Courses c ON cl.course_id = c.course_id
	            WHERE cl.teacher_id = ?
	        """;

	        PreparedStatement pstmt = conn.prepareStatement(sql);
	        pstmt.setInt(1, teacherId);

	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            String courseName = rs.getString("course_name");
	            String section = rs.getString("section");
	            String program = rs.getString("program");
	            if (program == null) program = "N/A";

	            String programAbbreviation = ViewClassList.mapProgramToShortName(program);
	            courses.add(new String[]{courseName, section, program, programAbbreviation});
	        }

	    } catch (SQLException e) {
	        System.err.println("Error fetching teacher courses: " + e.getMessage());
	    } finally {
	        dbConn.closeConnection();
	    }

	    return courses;
	}
	

    public static List<String> fetchAvailableCourses() {
        DatabaseConnection dbConn = new DatabaseConnection();
        List<String> courseList = new ArrayList<>();

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = "SELECT course_name, section FROM Courses";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                String section = rs.getString("section");
                courseList.add(courseName + " - " + section);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching available courses: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }

        return courseList;
    }

    public static void deleteCourse(String courseName, String section) {
        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();
            conn.setAutoCommit(false);

            UsersModel currentUser = Session.getCurrentUser();
            if (currentUser == null || currentUser.getTeacherId() == null) {
                System.err.println("Error: No teacher is logged in.");
                return;
            }

            int teacherId = currentUser.getTeacherId();

            // Find the class_id based on courseName, section, and teacherId
            String findClassSql = """
                SELECT class_id FROM Classes
                WHERE course_name = ? AND section = ? AND teacher_id = ?
            """;

            try (PreparedStatement findClassStmt = conn.prepareStatement(findClassSql)) {
                findClassStmt.setString(1, courseName);
                findClassStmt.setString(2, section);
                findClassStmt.setInt(3, teacherId);

                ResultSet rs = findClassStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("No class found with the given course name and section for this teacher.");
                    return;
                }

                int classId = rs.getInt("class_id");

                // Delete attendance entries related to this class
                String deleteAttendanceSql = """
                    DELETE FROM Attendance
                    WHERE enrollment_id IN (
                        SELECT enrollment_id FROM Enrollments WHERE class_id = ?
                    )
                """;
                try (PreparedStatement delAttendanceStmt = conn.prepareStatement(deleteAttendanceSql)) {
                    delAttendanceStmt.setInt(1, classId);
                    delAttendanceStmt.executeUpdate();
                }

                // Delete enrollments tied to this class
                String deleteEnrollmentsSql = "DELETE FROM Enrollments WHERE class_id = ?";
                try (PreparedStatement delEnrollmentsStmt = conn.prepareStatement(deleteEnrollmentsSql)) {
                    delEnrollmentsStmt.setInt(1, classId);
                    delEnrollmentsStmt.executeUpdate();
                }

                // Delete the class itself
                String deleteClassSql = "DELETE FROM Classes WHERE class_id = ?";
                try (PreparedStatement delClassStmt = conn.prepareStatement(deleteClassSql)) {
                    delClassStmt.setInt(1, classId);
                    delClassStmt.executeUpdate();
                }

                conn.commit();
                System.out.println("Class successfully deleted.");

            }

        } catch (SQLException e) {
            System.err.println("Error deleting class: " + e.getMessage());
            try {
                dbConn.getConnection().rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
        } finally {
            dbConn.closeConnection();
        }
    }



    public static void updateCourse(String oldCourseName, String oldSection, String newCourseName, String newSection) {
        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            // Find the class_id of the class to update
            String getClassIdSql = "SELECT class_id FROM Classes WHERE course_name = ? AND section = ?";
            PreparedStatement getClassIdStmt = conn.prepareStatement(getClassIdSql);
            getClassIdStmt.setString(1, oldCourseName);
            getClassIdStmt.setString(2, oldSection);
            ResultSet rs = getClassIdStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("No matching class found to update.");
                return;
            }

            int classId = rs.getInt("class_id");

            // Update course_name and section for the found class_id
            String updateClassSql = "UPDATE Classes SET course_name = ?, section = ? WHERE class_id = ?";
            PreparedStatement updateClassStmt = conn.prepareStatement(updateClassSql);
            updateClassStmt.setString(1, newCourseName);
            updateClassStmt.setString(2, newSection);
            updateClassStmt.setInt(3, classId);

            int rowsUpdated = updateClassStmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Class updated successfully.");
            } else {
                System.out.println("Class update failed.");
            }

            // Close resources
            updateClassStmt.close();
            getClassIdStmt.close();
        } catch (SQLException e) {
            System.err.println("Error updating class: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
    }
    
    public static List<Student> getStudentsEnrolledForTeacher(String courseName, String section, String program, int teacherId) {
        List<Student> students = new ArrayList<>();
        DatabaseConnection dbConn = new DatabaseConnection();

        String query = """
            SELECT s.student_id, u.username, s.first_name, s.middle_name, s.last_name, u.email, s.year_level
            FROM Students s
            JOIN Users u ON s.user_id = u.user_id
            JOIN Enrollments e ON s.student_id = e.student_id
            JOIN Classes c ON e.class_id = c.class_id
            WHERE c.course_name = ? AND c.section = ? AND c.program = ? AND c.teacher_id = ?
        """;

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, courseName);
            pstmt.setString(2, section);
            pstmt.setString(3, program);
            pstmt.setInt(4, teacherId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setEnrollmentId(rs.getInt("student_id"));
                student.setUsername(rs.getString("username"));  // from Users table
                student.setFirstName(rs.getString("first_name"));
                student.setMiddleName(rs.getString("middle_name"));
                student.setLastName(rs.getString("last_name"));
                student.setEmail(rs.getString("email"));        // from Users table
                student.setYearLevel(rs.getString("year_level"));
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students for teacher's class: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }

        return students;
    }
  

    
}