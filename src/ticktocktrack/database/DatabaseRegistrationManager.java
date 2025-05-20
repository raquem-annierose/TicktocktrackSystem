package ticktocktrack.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import ticktocktrack.logic.Session;

public class DatabaseRegistrationManager {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=AttendanceDB;encrypt=false;trustServerCertificate=true;integratedSecurity=true;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

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
 * Retrieves the real Head Admin's user_id from the database.
 * Assumes that the Head Admin has role 'HeadAdmin' or similar.
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
