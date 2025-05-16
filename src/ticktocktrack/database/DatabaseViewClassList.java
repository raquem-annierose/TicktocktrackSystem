package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import ticktocktrack.logic.ViewClassList;

public class DatabaseViewClassList {

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
	            courses.add(new String[]{courseName, section, programAbbreviation});
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

            conn.setAutoCommit(false); // Begin transaction

            String getClassIdSql = """
                SELECT cl.class_id 
                FROM Classes cl 
                JOIN Courses c ON cl.course_id = c.course_id 
                WHERE c.course_name = ? AND cl.section = ?
            """;
            PreparedStatement getClassIdStmt = conn.prepareStatement(getClassIdSql);
            getClassIdStmt.setString(1, courseName);
            getClassIdStmt.setString(2, section);
            ResultSet rs = getClassIdStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("No matching class found to delete.");
                return;
            }

            int classId = rs.getInt("class_id");

            // 1. Delete Attendance
            String deleteAttendanceSql = """
                DELETE FROM Attendance 
                WHERE enrollment_id IN (SELECT enrollment_id FROM Enrollments WHERE class_id = ?)
            """;
            PreparedStatement delAttendanceStmt = conn.prepareStatement(deleteAttendanceSql);
            delAttendanceStmt.setInt(1, classId);
            delAttendanceStmt.executeUpdate();

            // 2. Delete Enrollments
            String deleteEnrollmentsSql = "DELETE FROM Enrollments WHERE class_id = ?";
            PreparedStatement delEnrollmentsStmt = conn.prepareStatement(deleteEnrollmentsSql);
            delEnrollmentsStmt.setInt(1, classId);
            delEnrollmentsStmt.executeUpdate();

            // 3. Delete Class
            String deleteClassSql = "DELETE FROM Classes WHERE class_id = ?";
            PreparedStatement delClassStmt = conn.prepareStatement(deleteClassSql);
            delClassStmt.setInt(1, classId);
            int rowsDeleted = delClassStmt.executeUpdate();

            conn.commit(); // Commit transaction
            System.out.println(rowsDeleted > 0 ? "Class deleted successfully." : "No class deleted.");

            // Cleanup
            delAttendanceStmt.close();
            delEnrollmentsStmt.close();
            delClassStmt.close();
            getClassIdStmt.close();

        } catch (SQLException e) {
            System.err.println("Error deleting course/class: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
    }


    public static void updateCourse(String oldCourseName, String oldSection, String newCourseName, String newSection) {
        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            // Check if old course exists
            String getCourseIdSql = "SELECT course_id FROM Courses WHERE course_name = ?";
            PreparedStatement getCourseIdStmt = conn.prepareStatement(getCourseIdSql);
            getCourseIdStmt.setString(1, oldCourseName);
            ResultSet rs = getCourseIdStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Old course name not found in Courses table.");
                return;
            }
            int oldCourseId = rs.getInt("course_id");

            // If the course name is changing, update the Courses table
            if (!oldCourseName.equals(newCourseName)) {
                String updateCourseNameSql = "UPDATE Courses SET course_name = ? WHERE course_id = ?";
                PreparedStatement updateCourseNameStmt = conn.prepareStatement(updateCourseNameSql);
                updateCourseNameStmt.setString(1, newCourseName);
                updateCourseNameStmt.setInt(2, oldCourseId);
                updateCourseNameStmt.executeUpdate();
                updateCourseNameStmt.close();
            }

            // Update Classes table section (and course_id if necessary)
            // In your case, since course_id remains the same (course renamed),
            // you only need to update the section.
            String updateClassesSql = "UPDATE Classes SET section = ? WHERE course_id = ? AND section = ?";
            PreparedStatement updateClassesStmt = conn.prepareStatement(updateClassesSql);
            updateClassesStmt.setString(1, newSection);
            updateClassesStmt.setInt(2, oldCourseId);
            updateClassesStmt.setString(3, oldSection);

            int rowsUpdated = updateClassesStmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Course and classes updated successfully.");
            } else {
                System.out.println("No matching class found to update.");
            }

            updateClassesStmt.close();
            getCourseIdStmt.close();

        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
    }
    
    public static List<Student> getStudentsForCourse(String courseName, String section) {
        List<Student> students = new ArrayList<>();
        DatabaseConnection dbConn = new DatabaseConnection();
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("Error: No user is logged in.");
            return new ArrayList<>();
        }

        Integer teacherId = currentUser.getTeacherId();
        if (teacherId == null) {
            System.err.println("Error: Logged in user is not a teacher.");
            return new ArrayList<>();
        } // Get from session

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = """
                SELECT s.student_id, s.user_id, u.username, s.first_name, s.middle_name, s.last_name, 
                       s.section, s.year_level, u.email, s.program
                FROM Students s
                JOIN Users u ON s.user_id = u.user_id
                JOIN Classes cl ON s.section = cl.section
                JOIN Courses c ON cl.course_id = c.course_id
                WHERE c.course_name = ? AND s.section = ?
            """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseName);
            pstmt.setString(2, section);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("student_id"),
                    rs.getInt("user_id"),
                    rs.getString("first_name"),
                    rs.getString("middle_name"),
                    rs.getString("last_name"),
                    rs.getString("section"),
                    rs.getString("year_level"),
                    rs.getString("program")
                );
                student.setUsername(rs.getString("username"));
                student.setEmail(rs.getString("email"));

                students.add(student);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching students for course: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }

        return students;
    }
    
}