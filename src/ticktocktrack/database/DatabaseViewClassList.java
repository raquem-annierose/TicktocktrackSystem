package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ticktocktrack.logic.CourseInfo;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.Student;
import ticktocktrack.logic.UsersModel;
import ticktocktrack.logic.ViewClassList;

/**
 * DatabaseViewClassList handles database operations related to fetching class and course information
 * for teachers. It provides methods to retrieve the classes taught by a specific teacher as well as
 * the courses associated with the currently logged-in teacher, including program details.
 */
public class DatabaseViewClassList {

    /**
     * Retrieves a list of classes taught by a specific teacher.
     *
     * @param teacherId the ID of the teacher whose classes are to be fetched
     * @return a List of CourseInfo objects containing course name, section, and program
     */
    public static List<CourseInfo> getClassesByTeacher(int teacherId) {
        List<CourseInfo> classList = new ArrayList<>();
        String query = "SELECT course_name, section, program FROM Classes WHERE teacher_id = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, teacherId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String courseName = rs.getString("course_name");
                        String section = rs.getString("section");
                        String program = rs.getString("program");
                        classList.add(new CourseInfo(courseName, section, program));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching classes for teacher ID " + teacherId + ": " + e.getMessage());
        }
        return classList;
    }

    /**
     * Retrieves a list of courses taught by the currently logged-in teacher.
     * Each course entry includes course name, section, program, and program abbreviation.
     *
     * @return a List of String arrays where each array contains:
     *         [0] - course name,
     *         [1] - section,
     *         [2] - program,
     *         [3] - program abbreviation
     */
    public static List<String[]> getCoursesByTeacherId() {
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("Error: No user is logged in.");
            return new ArrayList<>();
        }

        Integer teacherId = currentUser.getTeacherId();
        if (teacherId == null) {
            System.err.println("Error: Logged in user is not a teacher.");
            return new ArrayList<>();
        }

        List<String[]> courses = new ArrayList<>();
        String sql = "SELECT c.course_name, cl.section, cl.program " +
                     "FROM Classes cl JOIN Courses c ON cl.course_id = c.course_id " +
                     "WHERE cl.teacher_id = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, teacherId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String courseName = rs.getString("course_name");
                        String section = rs.getString("section");
                        String program = rs.getString("program");
                        if (program == null) program = "N/A";

                        String programAbbreviation = ViewClassList.mapProgramToShortName(program);
                        courses.add(new String[]{courseName, section, program, programAbbreviation});
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching teacher courses: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Fetches a list of all available courses along with their sections.
     * The format of each entry is "course_name - section".
     *
     * @return A list of strings representing courses and their sections.
     */
    public static List<String> fetchAvailableCourses() {
        List<String> courseList = new ArrayList<>();
        String sql = "SELECT course_name, section FROM Courses";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String courseName = rs.getString("course_name");
                    String section = rs.getString("section");
                    courseList.add(courseName + " - " + section);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available courses: " + e.getMessage());
        }
        return courseList;
    }

    /**
     * Deletes a class/course and all associated attendance and enrollment records
     * for the currently logged-in teacher.
     * Performs deletion in a transaction to ensure data integrity.
     *
     * @param courseName The name of the course to delete.
     * @param section The section of the course to delete.
     */
    public static void deleteCourse(String courseName, String section) {
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null || currentUser.getTeacherId() == null) {
            System.err.println("Error: No teacher is logged in.");
            return;
        }

        int teacherId = currentUser.getTeacherId();

        String findClassSql = "SELECT class_id FROM Classes WHERE course_name = ? AND section = ? AND teacher_id = ?";
        String deleteAttendanceSql = "DELETE FROM Attendance WHERE enrollment_id IN (SELECT enrollment_id FROM Enrollments WHERE class_id = ?)";
        String deleteEnrollmentsSql = "DELETE FROM Enrollments WHERE class_id = ?";
        String deleteClassSql = "DELETE FROM Classes WHERE class_id = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement findClassStmt = conn.prepareStatement(findClassSql)) {
                findClassStmt.setString(1, courseName);
                findClassStmt.setString(2, section);
                findClassStmt.setInt(3, teacherId);

                try (ResultSet rs = findClassStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No class found with the given course name and section for this teacher.");
                        conn.rollback();
                        return;
                    }
                    int classId = rs.getInt("class_id");

                    try (PreparedStatement delAttendanceStmt = conn.prepareStatement(deleteAttendanceSql);
                         PreparedStatement delEnrollmentsStmt = conn.prepareStatement(deleteEnrollmentsSql);
                         PreparedStatement delClassStmt = conn.prepareStatement(deleteClassSql)) {

                        delAttendanceStmt.setInt(1, classId);
                        delAttendanceStmt.executeUpdate();

                        delEnrollmentsStmt.setInt(1, classId);
                        delEnrollmentsStmt.executeUpdate();

                        delClassStmt.setInt(1, classId);
                        delClassStmt.executeUpdate();

                        conn.commit();
                        System.out.println("Class successfully deleted.");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting class: " + e.getMessage());
        }
    }

    /**
     * Updates the course name and section for a class identified by the old course name and section.
     *
     * @param oldCourseName The current course name to find.
     * @param oldSection The current section to find.
     * @param newCourseName The new course name to update.
     * @param newSection The new section to update.
     */
    public static void updateCourse(String oldCourseName, String oldSection, String newCourseName, String newSection) {
        String getClassIdSql = "SELECT class_id FROM Classes WHERE course_name = ? AND section = ?";
        String updateClassSql = "UPDATE Classes SET course_name = ?, section = ? WHERE class_id = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement getClassIdStmt = conn.prepareStatement(getClassIdSql)) {
                getClassIdStmt.setString(1, oldCourseName);
                getClassIdStmt.setString(2, oldSection);
                try (ResultSet rs = getClassIdStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No matching class found to update.");
                        return;
                    }
                    int classId = rs.getInt("class_id");

                    try (PreparedStatement updateClassStmt = conn.prepareStatement(updateClassSql)) {
                        updateClassStmt.setString(1, newCourseName);
                        updateClassStmt.setString(2, newSection);
                        updateClassStmt.setInt(3, classId);

                        int rowsUpdated = updateClassStmt.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("Class updated successfully.");
                        } else {
                            System.out.println("Class update failed.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating class: " + e.getMessage());
        }
    }

    /**
     * Retrieves a list of students enrolled in a specific class taught by a given teacher.
     *
     * @param courseName The name of the course.
     * @param section The section of the course.
     * @param program The program associated with the class.
     * @param teacherId The ID of the teacher.
     * @return A list of Student objects enrolled in the specified class.
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
     * Unenrolls a list of students from a specified class taught by a given teacher.
     * Executes the unenrollment in a single transaction using batch deletion.
     *
     * @param students   The list of Student objects to be unenrolled from the class.
     * @param courseName The name of the course.
     * @param section    The section of the course.
     * @param program    The program of the course.
     * @param teacherId  The ID of the teacher who teaches the class.
     */
    public static void unenrollStudentsFromClass(List<Student> students, String courseName, String section, String program, int teacherId) {
        String getClassIdSQL = "SELECT class_id FROM Classes WHERE course_name = ? AND section = ? AND program = ? AND teacher_id = ?";
        String deleteEnrollmentSQL = "DELETE FROM Enrollments WHERE student_id = ? AND class_id = ?";

        DatabaseConnection dbConn = new DatabaseConnection();
        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement getClassStmt = conn.prepareStatement(getClassIdSQL)) {

                conn.setAutoCommit(false); // start transaction

                // Get class_id
                getClassStmt.setString(1, courseName);
                getClassStmt.setString(2, section);
                getClassStmt.setString(3, program);
                getClassStmt.setInt(4, teacherId);

                int classId = -1;
                try (ResultSet rs = getClassStmt.executeQuery()) {
                    if (rs.next()) {
                        classId = rs.getInt("class_id");
                    } else {
                        System.err.println("Class not found for deletion.");
                        return;
                    }
                }

                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteEnrollmentSQL)) {
                    for (Student s : students) {
                        deleteStmt.setInt(1, s.getStudentId());
                        deleteStmt.setInt(2, classId);
                        deleteStmt.addBatch();
                        System.out.println("Queued to unenroll student_id = " + s.getStudentId() + " from class_id = " + classId);
                    }

                    int[] result = deleteStmt.executeBatch();
                    conn.commit(); // commit the transaction

                    System.out.println("Unenrollment completed. Deleted rows: " + result.length);
                } catch (SQLException e) {
                    conn.rollback(); // rollback if there's an error
                    System.err.println("Error during batch delete, transaction rolled back: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during unenrollment: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves a list of students who are not enrolled in the class identified by the given classId.
     *
     * @param classId The ID of the class.
     * @return A List of Student objects representing students who are not currently enrolled in the class.
     */
    public static List<Student> getUnenrolledStudentsWithEnrollmentId(int classId) {
        List<Student> students = new ArrayList<>();
        DatabaseConnection dbConn = new DatabaseConnection();


        String sql = "SELECT s.student_id, s.user_id, u.username, u.email, " +
                "s.first_name, s.middle_name, s.last_name, " +
                "s.year_level, s.section, s.program " +
                "FROM Students s " +
                "JOIN Users u ON s.user_id = u.user_id " +
                "WHERE s.student_id NOT IN ( " +
                "    SELECT student_id FROM Enrollments WHERE class_id = ? " +
                ")";

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, classId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Student s = new Student();
                    s.setStudentId(rs.getInt("student_id"));
                    s.setUserId(rs.getInt("user_id"));
                    s.setUsername(rs.getString("username"));
                    s.setEmail(rs.getString("email"));
                    s.setFirstName(rs.getString("first_name"));
                    s.setMiddleName(rs.getString("middle_name"));
                    s.setLastName(rs.getString("last_name"));
                    s.setYearLevel(rs.getString("year_level"));
                    s.setSection(rs.getString("section"));
                    s.setProgram(rs.getString("program"));


                    students.add(s);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching unenrolled students: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
        return students;
    }
   
    /**
     * Retrieves the class ID for a given course name, section, program, and teacher ID.
     *
     * @param courseName The name of the course.
     * @param section    The section of the course.
     * @param program    The program associated with the class.
     * @param teacherId  The ID of the teacher who teaches the class.
     * @return The class ID if found; otherwise, returns -1.
     */   
    public static int getClassId(String courseName, String section, String program, int teacherId) {
        DatabaseConnection dbConn = new DatabaseConnection();
        int classId = -1; // default invalid


        String sql = "SELECT class_id FROM Classes WHERE course_name = ? AND section = ? AND program = ? AND teacher_id = ?";


        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, courseName);
                pstmt.setString(2, section);
                pstmt.setString(3, program);
                pstmt.setInt(4, teacherId);


                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    classId = rs.getInt("class_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting classId: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }
        return classId;
    }
   
    /**
     * Checks if a username already exists in the Users table.
     *
     * @param username The username to check.
     * @return true if the username exists, false otherwise.
     */
    public static boolean checkUsernameExists(String username) {
        DatabaseConnection dbConn = new DatabaseConnection();
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
        boolean exists = false;


        try {
            dbConn.connectToSQLServer();
            try (Connection conn = dbConn.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {


                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        exists = rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
        } finally {
            dbConn.closeConnection();
        }


        return exists;
    }
   
    /**
     * Retrieves the set of student IDs enrolled in a specific class.
     *
     * @param classId The ID of the class.
     * @return A Set of student IDs enrolled in the class. Returns an empty set if no students are found or on error.
     */
    public static Set<Integer> getEnrolledStudentIds(int classId) {
        Set<Integer> enrolledStudentIds = new HashSet<>();
        DatabaseConnection dbConn = new DatabaseConnection();


        String sql = "SELECT student_id FROM Enrollments WHERE class_id = ?";


        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, classId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    enrolledStudentIds.add(rs.getInt("student_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching enrolled student IDs for classId " + classId + ": " + e.getMessage());
            // Return empty set on error
        } finally {
            dbConn.closeConnection();
        }


        return enrolledStudentIds;
    }
   
    /**
     * Enrolls a list of students into a class.
     *
     * @param classId  The ID of the class.
     * @param students The list of Student objects to enroll.
     * @return true if all students were enrolled successfully; false otherwise or if the input list is null/empty.
     */
    public static boolean enrollStudents(int classId, List<Student> students) {
        if (students == null || students.isEmpty()) {
            return false; // Nothing to enroll
        }
       
        DatabaseConnection dbConn = new DatabaseConnection();
        String insertSql = "INSERT INTO Enrollments (class_id, student_id) VALUES (?, ?)";
       
        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();
            conn.setAutoCommit(false);
           
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                for (Student s : students) {
                    stmt.setInt(1, classId);
                    stmt.setInt(2, s.getStudentId());
                    stmt.addBatch();
                }
                int[] results = stmt.executeBatch();
                conn.commit();
                // Check if all inserts were successful (no executeBatch returns 0)
                for (int res : results) {
                    if (res == 0) {
                        return false;
                    }
                }
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Enrollment failed: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        } finally {
            dbConn.closeConnection();
        }
    }

}
