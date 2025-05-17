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

    public static void deleteCourse(String courseName) {
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

            // Get course_id for this course name and teacher
            String getCourseIdSql = """
                SELECT DISTINCT c.course_id
                FROM Courses c
                JOIN Classes cl ON c.course_id = cl.course_id
                WHERE c.course_name = ? AND cl.teacher_id = ?
            """;
            PreparedStatement getCourseIdStmt = conn.prepareStatement(getCourseIdSql);
            getCourseIdStmt.setString(1, courseName);
            getCourseIdStmt.setInt(2, teacherId);
            ResultSet rsCourse = getCourseIdStmt.executeQuery();

            if (!rsCourse.next()) {
                System.out.println("Course not found for this teacher.");
                return;
            }

            int courseId = rsCourse.getInt("course_id");

            // Get all class_ids under this teacher and course_id
            String getClassIdsSql = """
                SELECT class_id FROM Classes
                WHERE course_id = ? AND teacher_id = ?
            """;
            PreparedStatement getClassIdsStmt = conn.prepareStatement(getClassIdsSql);
            getClassIdsStmt.setInt(1, courseId);
            getClassIdsStmt.setInt(2, teacherId);
            ResultSet rsClasses = getClassIdsStmt.executeQuery();

            List<Integer> classIds = new ArrayList<>();
            while (rsClasses.next()) {
                classIds.add(rsClasses.getInt("class_id"));
            }

            // Delete attendance and enrollments tied to those classes
            for (int classId : classIds) {
                String deleteAttendanceSql = """
                    DELETE FROM Attendance
                    WHERE enrollment_id IN (SELECT enrollment_id FROM Enrollments WHERE class_id = ?)
                """;
                try (PreparedStatement delAttendanceStmt = conn.prepareStatement(deleteAttendanceSql)) {
                    delAttendanceStmt.setInt(1, classId);
                    delAttendanceStmt.executeUpdate();
                }

                String deleteEnrollmentsSql = "DELETE FROM Enrollments WHERE class_id = ?";
                try (PreparedStatement delEnrollmentsStmt = conn.prepareStatement(deleteEnrollmentsSql)) {
                    delEnrollmentsStmt.setInt(1, classId);
                    delEnrollmentsStmt.executeUpdate();
                }
            }

            // Delete classes tied to this teacher and course_id
            try (PreparedStatement delClassesStmt = conn.prepareStatement(
                    "DELETE FROM Classes WHERE course_id = ? AND teacher_id = ?")) {
                delClassesStmt.setInt(1, courseId);
                delClassesStmt.setInt(2, teacherId);
                delClassesStmt.executeUpdate();
            }

            // Check if course is still used in other teachers' classes
            String checkCourseUsageSql = """
                SELECT COUNT(*) AS usage_count
                FROM Classes
                WHERE course_id = ?
            """;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkCourseUsageSql)) {
                checkStmt.setInt(1, courseId);
                ResultSet rsCheck = checkStmt.executeQuery();

                if (rsCheck.next() && rsCheck.getInt("usage_count") == 0) {
                    try (PreparedStatement delCourseStmt = conn.prepareStatement("DELETE FROM Courses WHERE course_id = ?")) {
                        delCourseStmt.setInt(1, courseId);
                        delCourseStmt.executeUpdate();
                        System.out.println("Course also deleted from Courses table (no other usage).");
                    }
                } else {
                    System.out.println("Course not deleted from Courses table (still used elsewhere).");
                }
            }

            conn.commit();

        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
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
    
    public static List<Student> getStudentsEnrolled(String courseName, String section, String program) {
        List<Student> students = new ArrayList<>();
        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();

            String sql = """
                SELECT s.student_id, u.username, s.first_name, s.middle_name, s.last_name, s.year_level, u.email
                FROM Students s
                JOIN Users u ON s.user_id = u.user_id
                JOIN Enrollments e ON s.student_id = e.student_id
                JOIN Classes cl ON e.class_id = cl.class_id
                JOIN Courses c ON cl.course_id = c.course_id
                WHERE c.course_name = ? AND cl.section = ? AND cl.program = ?
                ORDER BY s.last_name, s.first_name
                """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseName);
            pstmt.setString(2, section);
            pstmt.setString(3, program);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setUsername(rs.getString("username"));  // from Users table
                student.setFirstName(rs.getString("first_name"));
                student.setMiddleName(rs.getString("middle_name"));
                student.setLastName(rs.getString("last_name"));
                student.setYearLevel(rs.getString("year_level"));
                student.setEmail(rs.getString("email"));  // from Users table

                students.add(student);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("Error fetching students enrolled: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }

        return students;
    }

    
}