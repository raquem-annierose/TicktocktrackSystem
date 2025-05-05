package ticktocktrack.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseRegistrationManager {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=AttendanceDB;encrypt=false;trustServerCertificate=true;integratedSecurity=true;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Method to hash password
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());
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

    // Method for checking if username already exists
    private static boolean checkUsernameExists(String username) {
        try (Connection connection = getConnection()) {
            String checkUsernameSQL = "SELECT COUNT(*) FROM Users WHERE username = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkUsernameSQL)) {
                checkStatement.setString(1, username);
                ResultSet rs = checkStatement.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                return count > 0;  // Returns true if username exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;  // Default to true if there is an error
        }
    }

    // Method to register a Faculty (Admin or Teacher)
    public static boolean registerFaculty(String username, String role, String email, String passwordHash) {
        if (checkUsernameExists(username)) {
            System.out.println("Username already taken!");
            return false;
        }

        try (Connection connection = getConnection()) {
            // Insert user into 'Users' table
            String sql = "INSERT INTO Users (username, role) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, role);
                statement.executeUpdate();
            }

            // Insert role-specific data for Admin or Teacher
            if (role.equals("Admin")) {
                sql = "INSERT INTO Admins (username, email, password_hash) VALUES (?, ?, ?)";
            } else if (role.equals("Teacher")) {
                sql = "INSERT INTO Teachers (username, email, password_hash) VALUES (?, ?, ?)";
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, email);
                statement.setString(3, passwordHash);
                statement.executeUpdate();
            }

            return true; // Faculty successfully registered
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to register a Student with Year Level and Section
    public static boolean registerStudent(String username, String role, String email, String passwordHash, String firstName, String middleName, String lastName, String yearLevel, String section) {
        if (checkUsernameExists(username)) {
            System.out.println("Username already taken!");
            return false;
        }

        try (Connection connection = getConnection()) {
            // Insert user into 'Users' table
            String sql = "INSERT INTO Users (username, role) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, role);
                statement.executeUpdate();
            }

            // Insert student details into 'Students' table including Year Level and Section
            sql = "INSERT INTO Students (username, email, password_hash, first_name, middle_name, last_name, year_level, section, attendance_status, absent_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, email);
                statement.setString(3, passwordHash);
                statement.setString(4, firstName);
                statement.setString(5, middleName);
                statement.setString(6, lastName);
                statement.setString(7, yearLevel); // Set Year Level
                statement.setString(8, section);  // Set Section
                statement.setString(9, "Pending"); // Default attendance status
                statement.setInt(10, 0);          // Default absent_count
                statement.executeUpdate();
            }

            return true; // Student successfully registered
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Unified method to register a user based on role (with updated student registration)
    public static boolean registerUser(String username, String role, String email, String passwordHash, String firstName, String middleName, String lastName, String yearLevel, String section) {
        if (role.equals("Student")) {
            return registerStudent(username, role, email, passwordHash, firstName, middleName, lastName, yearLevel, section);
        } else if (role.equals("Admin") || role.equals("Teacher")) {
            return registerFaculty(username, role, email, passwordHash);
        }
        return false; // Invalid role
    }
}
