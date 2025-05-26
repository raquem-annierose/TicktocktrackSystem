package ticktocktrack.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import ticktocktrack.logic.Session;

/**
 * Manages user registration for different roles (Admin, Teacher, Student).
 * Handles database connections, password hashing, and insertion into related tables.
 */
public class DatabaseRegistrationManager {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=AttendanceDB;encrypt=false;trustServerCertificate=true;integratedSecurity=true;";

    /**
     * Gets a database connection using the configured DB_URL.
     * 
     * @return a Connection to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Hashes a plain-text password using SHA-256.
     * 
     * @param password the plain-text password to hash
     * @return the hexadecimal string representation of the hashed password
     */
    public static String hashPassword(String password) {
        try {
            password = password.trim();  // optional but recommended

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if a username already exists in the Users table.
     * 
     * @param username the username to check for existence
     * @return true if username exists, false otherwise
     */
    private static boolean checkUsernameExists(String username) {
        try (Connection connection = getConnection()) {
            String checkUsernameSQL = "SELECT COUNT(*) FROM Users WHERE username = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkUsernameSQL)) {
                checkStatement.setString(1, username);
                ResultSet rs = checkStatement.executeQuery();
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
    
    /**
     * Retrieves the admin ID associated with the current user from the database.
     * 
     * @param connection the database connection to use
     * @return the admin_id of the current user
     * @throws SQLException if the current user is not an admin or database error occurs
     */
    public static int getCurrentAdminId(Connection connection) throws SQLException {
        int userId = Session.getSenderUserId();

        // No hardcoded check here — just query the Admins table
        String query = "SELECT admin_id FROM Admins WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("admin_id");
            } else {
                throw new SQLException("Current user is not an admin.");
            }
        }
    }
    
    /**
     * Registers a faculty user (Admin or Teacher).
     * Inserts user data into Users table and the corresponding role table.
     * 
     * @param username the username of the faculty member
     * @param role either "Admin" or "Teacher"
     * @param email the email address of the faculty member
     * @param passwordHash the hashed password
     * @param firstName first name of the faculty member
     * @param lastName last name of the faculty member
     * @param createdByAdminId the ID of the admin who created this user; if -1, uses head admin
     * @return true if registration successful, false otherwise
     */
    public static boolean registerFaculty(String username, String role, String email, String passwordHash,
            String firstName, String lastName, int createdByAdminId) {

        if (checkUsernameExists(username)) {
            System.out.println("Username already taken!");
            return false;
        }

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            // If createdByAdminId is -1, fetch real Head Admin user id
            if (createdByAdminId == -1) {
                createdByAdminId = getRealHeadAdminId(connection);
                if (createdByAdminId == -1) {
                    System.out.println("Head Admin ID not found.");
                    return false;
                }
            }

            // Insert into Users with date_created
            String userSql = "INSERT INTO Users (username, role, email, password_hash, created_by_admin_id, date_created) VALUES (?, ?, ?, ?, ?, ?)";
            int userId;
            try (PreparedStatement userStmt = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, username);
                userStmt.setString(2, role);
                userStmt.setString(3, email);
                userStmt.setString(4, passwordHash);
                userStmt.setInt(5, createdByAdminId);
                userStmt.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));

                userStmt.executeUpdate();

                ResultSet generatedKeys = userStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                } else {
                    connection.rollback();
                    System.out.println("Failed to get user_id.");
                    return false;
                }
            }

            // Insert into Admins or Teachers table
            String roleTableSql = null;
            if (role.equalsIgnoreCase("Admin")) {
                roleTableSql = "INSERT INTO Admins (user_id, first_name, last_name) VALUES (?, ?, ?)";
            } else if (role.equalsIgnoreCase("Teacher")) {
                roleTableSql = "INSERT INTO Teachers (user_id, first_name, last_name) VALUES (?, ?, ?)";
            } else {
                connection.rollback();
                System.out.println("Invalid role for faculty.");
                return false;
            }

            try (PreparedStatement roleStmt = connection.prepareStatement(roleTableSql)) {
                roleStmt.setInt(1, userId);
                roleStmt.setString(2, firstName);
                roleStmt.setString(3, lastName);
                roleStmt.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves the user_id of the real Head Admin from the database.
     * Assumes the Head Admin role is labeled as 'HeadAdmin'.
     * 
     * @param connection the database connection
     * @return user_id of the Head Admin, or -1 if not found
     * @throws SQLException if a database error occurs
     */
    private static int getRealHeadAdminId(Connection connection) throws SQLException {
        String sql = "SELECT TOP 1 user_id FROM Users WHERE role = 'HeadAdmin'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            } else {
                return -1; // Head Admin not found
            }
        }
    }
    
    /**
     * Registers a student user.
     * Inserts data into Users and Students tables with relevant details.
     * 
     * @param username the username of the student
     * @param email the email address of the student
     * @param passwordHash the hashed password
     * @param firstName first name of the student
     * @param middleName middle name of the student
     * @param lastName last name of the student
     * @param yearLevel student's year level
     * @param program student's program or course
     * @param section student's section
     * @return true if registration successful, false otherwise
     */
    public static boolean registerStudent(String username, String email, String passwordHash,
            String firstName, String middleName, String lastName,
            String yearLevel, String program, String section) {
    	if (checkUsernameExists(username)) {
            System.out.println("Username already taken!");
            return false;
        }

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            // Get current admin ID from session
            int currentAdminId = getCurrentAdminId(connection);

            // Insert into Users (with created_by_admin_id)
            String userSql = "INSERT INTO Users (username, role, email, password_hash, created_by_admin_id) VALUES (?, 'Student', ?, ?, ?)";
            int userId;
            try (PreparedStatement userStmt = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, username);
                userStmt.setString(2, email);
                userStmt.setString(3, passwordHash);
                userStmt.setInt(4, currentAdminId);
                userStmt.executeUpdate();

                ResultSet generatedKeys = userStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                } else {
                    connection.rollback();
                    System.out.println("Failed to get user_id.");
                    return false;
                }
            }

            // Insert into Students table
            String studentSql = "INSERT INTO Students (user_id, first_name, middle_name, last_name, year_level, program, section) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement studentStmt = connection.prepareStatement(studentSql)) {
                studentStmt.setInt(1, userId);
                studentStmt.setString(2, firstName);
                studentStmt.setString(3, middleName);
                studentStmt.setString(4, lastName);
                studentStmt.setString(5, yearLevel);
                studentStmt.setString(6, program);
                studentStmt.setString(7, section);
                studentStmt.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Registers a user based on role.
     * Delegates registration to either registerStudent or registerFaculty accordingly.
     * 
     * @param username the username of the user
     * @param role the role of the user ("Student", "Admin", or "Teacher")
     * @param email the user's email address
     * @param passwordHash the hashed password
     * @param firstName first name of the user
     * @param middleName middle name (for students)
     * @param lastName last name of the user
     * @param yearLevel applicable for students
     * @param section applicable for students
     * @param program applicable for students
     * @return true if registration successful, false otherwise
     */
    public static boolean registerUser(String username, String role, String email, String passwordHash,
                                       String firstName, String middleName, String lastName,
                                       String yearLevel, String section, String program) {
    	 if ("Student".equalsIgnoreCase(role)) {
    	        return registerStudent(username, email, passwordHash,
    	                firstName, middleName, lastName,
    	                yearLevel, program, section);
    	    } else if ("Admin".equalsIgnoreCase(role) || "Teacher".equalsIgnoreCase(role)) {
    	        try (Connection connection = ticktocktrack.database.DatabaseRegistrationManager.getConnection()) {
    	            int currentAdminId = ticktocktrack.database.DatabaseRegistrationManager.getCurrentAdminId(connection);
    	            return ticktocktrack.database.DatabaseRegistrationManager.registerFaculty(username, role, email, passwordHash,
    	                    firstName, lastName, currentAdminId);
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	            return false;
    	        }
    	    }
    	    return false;
    	    
    }
}
