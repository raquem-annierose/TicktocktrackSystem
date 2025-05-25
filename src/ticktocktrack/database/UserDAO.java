package ticktocktrack.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.TableView;
import ticktocktrack.logic.UsersModel;

/**
 * Data Access Object (DAO) class for managing user data from the database.
 * Provides methods to retrieve lists of admins and teachers with their details.
 */
public class UserDAO {

    private static DatabaseConnection dbConnection = new DatabaseConnection();

    /**
     * Retrieves a list of all admins along with their user details.
     * 
     * @return List of UsersModel objects representing admins.
     */
    public static List<UsersModel> getAdmins() {
        List<UsersModel> list = new ArrayList<>();
        String query = 
                "SELECT a.admin_id, u.user_id, u.username, u.email, u.role, a.first_name, a.last_name " +
                "FROM Admins a " +
                "JOIN Users u ON a.user_id = u.user_id";

        try {
            dbConnection.connectToSQLServer();
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsersModel user = new UsersModel(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")
                    );
                    user.setAdminId(rs.getInt("admin_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    list.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConnection.closeConnection();
        }

        return list;
    }

    /**
     * Retrieves a list of all teachers along with their user details.
     * 
     * @return List of UsersModel objects representing teachers.
     */
    public static List<UsersModel> getTeachers() {
        List<UsersModel> list = new ArrayList<>();
        String query = 
                "SELECT t.teacher_id, u.user_id, u.username, u.email, u.role, t.first_name, t.last_name " +
                "FROM Teachers t " +
                "JOIN Users u ON t.user_id = u.user_id";

        try {
            dbConnection.connectToSQLServer();
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsersModel user = new UsersModel(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")
                    );
                    user.setTeacherId(rs.getInt("teacher_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    list.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConnection.closeConnection();
        }

        return list;
    }

    /**
     * Retrieves a list of all students along with their user and academic details.
     * 
     * @return List of UsersModel objects representing students.
     */
    public static List<UsersModel> getStudents() {
        List<UsersModel> list = new ArrayList<>();
        String query = 
                "SELECT s.student_id, u.user_id, u.username, u.email, u.role, " +
                "s.first_name, s.middle_name, s.last_name, " +
                "s.year_level, s.program, s.section " +
                "FROM Students s " +
                "JOIN Users u ON s.user_id = u.user_id";

        try {
            dbConnection.connectToSQLServer();
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsersModel user = new UsersModel(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")
                    );
                    user.setStudentId(rs.getInt("student_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setYearLevel(rs.getString("year_level"));
                    user.setProgram(rs.getString("program"));
                    user.setSection(rs.getString("section"));
                    list.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        }

        return list;
    }
    
    /**
     * Retrieves a list of admins excluding the headadmin, including who created their accounts.
     * Useful for admin management views.
     * 
     * @return List of UsersModel objects representing manageable admins.
     */
    public static List<UsersModel> manageAdmins() {
        List<UsersModel> list = new ArrayList<>();
        String query = 
                "SELECT a.admin_id, u.user_id, u.username, u.email, u.role, " +
                "a.first_name, a.last_name, " +
                "u.created_by_admin_id, u.date_created, " +
                "ca.first_name AS created_by_first_name, " +
                "ca.last_name AS created_by_last_name " +
                "FROM Admins a " +
                "JOIN Users u ON a.user_id = u.user_id " +
                "LEFT JOIN Admins ca ON u.created_by_admin_id = ca.admin_id " +
                "WHERE u.username <> 'headadmin'";


        try {
            dbConnection.connectToSQLServer();
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String createdByName = null;
                    String createdByFirstName = rs.getString("created_by_first_name");
                    String createdByLastName = rs.getString("created_by_last_name");
                    if (createdByFirstName != null || createdByLastName != null) {
                        createdByName = (createdByFirstName != null ? createdByFirstName : "") +
                                        (createdByLastName != null ? " " + createdByLastName : "");
                    }


                    UsersModel user = new UsersModel(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")
                    );

                    user.setAdminId(rs.getInt("admin_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setCreatedByName(createdByName);
                    user.setDateCreated(rs.getString("date_created"));

                    list.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConnection.closeConnection();
        }

        return list;
    }

    /**
     * Retrieves a list of teachers along with their user info and the admin who created their accounts.
     * Useful for managing teacher accounts.
     *
     * @return List of UsersModel objects representing manageable teachers.
     */
    public static List<UsersModel> manageTeachers() {
        List<UsersModel> list = new ArrayList<>();
        String query = 
                "SELECT t.teacher_id, u.user_id, u.username, u.email, u.role, " +
                "t.first_name, t.last_name, " +
                "u.created_by_admin_id, u.date_created, " +
                "ca.first_name AS created_by_first_name, " +
                "ca.last_name AS created_by_last_name " +
                "FROM Teachers t " +
                "JOIN Users u ON t.user_id = u.user_id " +
                "LEFT JOIN Admins ca ON u.created_by_admin_id = ca.admin_id";

        try {
            dbConnection.connectToSQLServer();
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String createdByName = null;
                    String createdByFirstName = rs.getString("created_by_first_name");
                    String createdByLastName = rs.getString("created_by_last_name");
                    if (createdByFirstName != null || createdByLastName != null) {
                        createdByName = (createdByFirstName != null ? createdByFirstName : "") +
                                        (createdByLastName != null ? " " + createdByLastName : "");
                    }


                    UsersModel user = new UsersModel(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")
                    );

                    user.setTeacherId(rs.getInt("teacher_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setCreatedByName(createdByName);
                    user.setDateCreated(rs.getString("date_created"));

                    list.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConnection.closeConnection();
        }

        return list;
    }

    /**
     * Retrieves a list of students along with their user info, academic details, 
     * and the admin who created their accounts.
     * Useful for managing student accounts.
     *
     * @return List of UsersModel objects representing manageable students.
     */
    public static List<UsersModel> manageStudents() {
        List<UsersModel> list = new ArrayList<>();
        String query = 
                "SELECT s.student_id, u.user_id, u.username, u.email, u.role, " +
                "s.first_name, s.middle_name, s.last_name, " +
                "s.year_level, s.program, s.section, " +
                "u.created_by_admin_id, u.date_created, " +
                "ca.first_name AS created_by_first_name, " +
                "ca.last_name AS created_by_last_name " +
                "FROM Students s " +
                "JOIN Users u ON s.user_id = u.user_id " +
                "LEFT JOIN Admins ca ON u.created_by_admin_id = ca.admin_id";

        try {
            dbConnection.connectToSQLServer();
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String createdByName = null;
                    String createdByFirstName = rs.getString("created_by_first_name");
                    String createdByLastName = rs.getString("created_by_last_name");
                    if (createdByFirstName != null || createdByLastName != null) {
                        createdByName = (createdByFirstName != null ? createdByFirstName : "") +
                                        (createdByLastName != null ? " " + createdByLastName : "");
                    }


                    UsersModel user = new UsersModel(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")
                    );

                    user.setStudentId(rs.getInt("student_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setYearLevel(rs.getString("year_level"));
                    user.setProgram(rs.getString("program"));
                    user.setSection(rs.getString("section"));
                    user.setCreatedByName(createdByName);
                    user.setDateCreated(rs.getString("date_created"));

                    list.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConnection.closeConnection();
        }

        return list;
    }

    /**
     * Deletes a user by their user ID, including cleaning up all related data such as:
     * - Nullifying created_by_admin_id references to this user
     * - Deleting enrollments related to this user's classes or student record
     * - Deleting classes taught by this user (if teacher)
     * - Deleting notifications sent by this user
     * - Removing entries from role-specific tables (Admins, Teachers, Students)
     * - Finally deleting the user record itself
     * 
     * This operation is performed within a database transaction to ensure atomicity,
     * rolling back all changes if any step fails.
     * 
     * @param userId The user ID of the user to delete.
     * @return true if the user was successfully deleted, false otherwise.
     */
    public static boolean deleteUserById(int userId) {
        String updateCreatedByAdmin = "UPDATE Users SET created_by_admin_id = NULL WHERE created_by_admin_id = ?";

        String deleteEnrollmentsByClassIds = 
            "DELETE FROM Enrollments WHERE class_id IN " +
            "(SELECT class_id FROM Classes WHERE teacher_id = (SELECT teacher_id FROM Teachers WHERE user_id = ?))";

        String deleteClassesByTeacherUserId = 
            "DELETE FROM Classes WHERE teacher_id = (SELECT teacher_id FROM Teachers WHERE user_id = ?)";

        String deleteEnrollmentsByStudentUserId = 
            "DELETE FROM Enrollments WHERE student_id = (SELECT student_id FROM Students WHERE user_id = ?)";

        String deleteNotificationsBySender = 
            "DELETE FROM Notifications WHERE sender_user_id = ?";

        String deleteAdmin = "DELETE FROM Admins WHERE user_id = ?";
        String deleteTeacher = "DELETE FROM Teachers WHERE user_id = ?";
        String deleteStudent = "DELETE FROM Students WHERE user_id = ?";
        String deleteUser = "DELETE FROM Users WHERE user_id = ?";

        try {
            dbConnection.connectToSQLServer();
            Connection conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            try (
                PreparedStatement psUpdateCreatedBy = conn.prepareStatement(updateCreatedByAdmin);
                PreparedStatement psDeleteEnrollmentsByClass = conn.prepareStatement(deleteEnrollmentsByClassIds);
                PreparedStatement psDeleteClasses = conn.prepareStatement(deleteClassesByTeacherUserId);
                PreparedStatement psDeleteEnrollmentsByStudent = conn.prepareStatement(deleteEnrollmentsByStudentUserId);
                PreparedStatement psDeleteNotificationsBySender = conn.prepareStatement(deleteNotificationsBySender);
                PreparedStatement psDeleteAdmin = conn.prepareStatement(deleteAdmin);
                PreparedStatement psDeleteTeacher = conn.prepareStatement(deleteTeacher);
                PreparedStatement psDeleteStudent = conn.prepareStatement(deleteStudent);
                PreparedStatement psDeleteUser = conn.prepareStatement(deleteUser)
            ) {
                // Break foreign key references for created_by_admin_id
                psUpdateCreatedBy.setInt(1, userId);
                psUpdateCreatedBy.executeUpdate();

                // Delete enrollments for classes taught by teacher with this userId
                psDeleteEnrollmentsByClass.setInt(1, userId);
                psDeleteEnrollmentsByClass.executeUpdate();

                // Delete classes taught by this teacher
                psDeleteClasses.setInt(1, userId);
                psDeleteClasses.executeUpdate();

                // Delete enrollments for this student
                psDeleteEnrollmentsByStudent.setInt(1, userId);
                psDeleteEnrollmentsByStudent.executeUpdate();

                // Delete notifications where user is sender
                psDeleteNotificationsBySender.setInt(1, userId);
                psDeleteNotificationsBySender.executeUpdate();

                // Delete from role-specific tables
                psDeleteAdmin.setInt(1, userId);
                psDeleteAdmin.executeUpdate();

                psDeleteTeacher.setInt(1, userId);
                psDeleteTeacher.executeUpdate();

                psDeleteStudent.setInt(1, userId);
                psDeleteStudent.executeUpdate();

                // Finally delete the user
                psDeleteUser.setInt(1, userId);
                int affected = psDeleteUser.executeUpdate();

                conn.commit(); // Commit transaction
                return affected > 0;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            dbConnection.closeConnection();
        }
    }
    
    /**
     * Updates a user's general and role-specific details in the database.
     * The method updates the Users table and the corresponding role table (Students, Teachers, Admins).
     * The update is performed within a transaction to ensure data integrity.
     * 
     * @param user The UsersModel object containing updated user details.
     * @return true if the update succeeded, false otherwise.
     */
    public static boolean updateUser(UsersModel user) {
        String updateUserSQL = "UPDATE Users SET username = ?, email = ? WHERE user_id = ?";

        String updateStudentSQL = "UPDATE Students SET first_name = ?, last_name = ?, middle_name = ?, program = ?, section = ?, year_level = ? WHERE user_id = ?";
        String updateTeacherSQL = "UPDATE Teachers SET first_name = ?, last_name = ? WHERE user_id = ?";
        String updateAdminSQL = "UPDATE Admins SET first_name = ?, last_name = ? WHERE user_id = ?";

        try {
            dbConnection.connectToSQLServer();
            Connection conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            try (
                PreparedStatement psUser = conn.prepareStatement(updateUserSQL);
            ) {
                // Update Users table
                psUser.setString(1, user.getUsername());
                psUser.setString(2, user.getEmail());
                psUser.setInt(3, user.getUserId());
                psUser.executeUpdate();

                // Update role-specific table
                if (user.getRole().equalsIgnoreCase("Student")) {
                    try (PreparedStatement psStudent = conn.prepareStatement(updateStudentSQL)) {
                        psStudent.setString(1, user.getFirstName());
                        psStudent.setString(2, user.getLastName());
                        psStudent.setString(3, user.getMiddleName());
                        psStudent.setString(4, user.getProgram());
                        psStudent.setString(5, user.getSection());
                        psStudent.setString(6, user.getYearLevel());
                        psStudent.setInt(7, user.getUserId());
                        psStudent.executeUpdate();
                    }
                } else if (user.getRole().equalsIgnoreCase("Teacher")) {
                    try (PreparedStatement psTeacher = conn.prepareStatement(updateTeacherSQL)) {
                        psTeacher.setString(1, user.getFirstName());
                        psTeacher.setString(2, user.getLastName());
                        psTeacher.setInt(3, user.getUserId());
                        psTeacher.executeUpdate();
                    }
                } else if (user.getRole().equalsIgnoreCase("Admin")) {
                    try (PreparedStatement psAdmin = conn.prepareStatement(updateAdminSQL)) {
                        psAdmin.setString(1, user.getFirstName());
                        psAdmin.setString(2, user.getLastName());
                        psAdmin.setInt(3, user.getUserId());
                        psAdmin.executeUpdate();
                    }
                }

                conn.commit(); // Commit all changes
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            dbConnection.closeConnection();
        }
    }

    /**
     * Retrieves a list of users filtered by their role.
     * Supports "Admin", "Teacher", and "Student" roles.
     * Returns an empty list if the role is null, empty, or unrecognized.
     * 
     * @param role The role of users to retrieve (case-insensitive).
     * @return A list of UsersModel objects matching the given role.
     */
    public static List<UsersModel> getUsersByRole(String role) {
        List<UsersModel> users = new ArrayList<>();

        // Null check - return empty list if role is null or empty
        if (role == null || role.trim().isEmpty()) {
            return users;
        }

        String query = "";

        switch (role.toLowerCase()) {
            case "admin":
            	query = 
                "SELECT u.user_id, u.username, u.email, u.date_created, " +
                "a.first_name, a.last_name, " +
                "ca.username AS created_by_username " +
                "FROM Users u " +
                "JOIN Admins a ON u.user_id = a.user_id " +
                "LEFT JOIN Admins ca_admin ON u.created_by_admin_id = ca_admin.admin_id " +
                "LEFT JOIN Users ca ON ca.user_id = ca_admin.user_id " +
                "WHERE u.role = 'Admin'";
                break;
            case "teacher":
            	query = 
                "SELECT u.user_id, u.username, u.email, u.date_created, " +
                "t.first_name, t.last_name, " +
                "ca.username AS created_by_username " +
                "FROM Users u " +
                "JOIN Teachers t ON u.user_id = t.user_id " +
                "LEFT JOIN Admins ca_admin ON u.created_by_admin_id = ca_admin.admin_id " +
                "LEFT JOIN Users ca ON ca.user_id = ca_admin.user_id " +
                "WHERE u.role = 'Teacher'";
                break;
            case "student":
            	query = 
                "SELECT u.user_id, u.username, u.email, u.date_created, " +
                "s.first_name, s.last_name, " +
                "ca.username AS created_by_username " +
                "FROM Users u " +
                "JOIN Students s ON u.user_id = s.user_id " +
                "LEFT JOIN Admins ca_admin ON u.created_by_admin_id = ca_admin.admin_id " +
                "LEFT JOIN Users ca ON ca.user_id = ca_admin.user_id " +
                "WHERE u.role = 'Student'";
                break;
            default:
                return users; // Return empty list if role is unrecognized
        }

        DatabaseConnection db = new DatabaseConnection();
        try {
            db.connectToSQLServer();
            try (Connection conn = db.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    UsersModel user = new UsersModel(0, "", "", "");
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setDateCreated(rs.getString("date_created"));
                    user.setCreatedByName(rs.getString("created_by_username"));
                    user.setRole(role);

                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }

        return users;
    }

    
   
}
