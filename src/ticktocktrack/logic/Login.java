package ticktocktrack.logic;

import ticktocktrack.database.DatabaseConnection;
import ticktocktrack.database.DatabaseRegistrationManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    private DatabaseConnection dbConnection;

    public Login() {
        dbConnection = new DatabaseConnection();
    }

    /**
     * Authenticates the user and returns their role if successful.
     * @param username The username entered.
     * @param password The password entered.
     * @return The role of the user ("HeadAdmin", "Admin", "Teacher", "Student") if successful; otherwise null.
     */
    public String authenticate(String username, String password) {
        // Hardcoded accounts
        if (username.equals("headadmin") && password.equals("headadmin123")) {
            return "HeadAdmin";
        }
        if (username.equals("teacher") && password.equals("teacher123")) {
            return "Teacher";
        }
        if (username.equals("student") && password.equals("student123")) {
            return "Student";
        }

        try {
            dbConnection.connectToSQLServer();
            Connection conn = dbConnection.getConnection();

            // Step 1: Get the role first
            String getRoleSQL = "SELECT role FROM Users WHERE username = ?";
            PreparedStatement roleStmt = conn.prepareStatement(getRoleSQL);
            roleStmt.setString(1, username);
            ResultSet roleRs = roleStmt.executeQuery();

            if (roleRs.next()) {
                String role = roleRs.getString("role");

                // Step 2: Based on role, check password_hash from respective table
                String tableName = "";
                switch (role) {
                    case "Admin":
                        tableName = "Admins";
                        break;
                    case "Teacher":
                        tableName = "Teachers";
                        break;
                    case "Student":
                        tableName = "Students";
                        break;
                    default:
                        return null; // Unknown role
                }

                String getPasswordHashSQL = "SELECT password_hash FROM " + tableName + " WHERE username = ?";
                PreparedStatement passStmt = conn.prepareStatement(getPasswordHashSQL);
                passStmt.setString(1, username);
                ResultSet passRs = passStmt.executeQuery();

                if (passRs.next()) {
                    String storedHash = passRs.getString("password_hash");

                    // Hash the entered password
                    String enteredHash = DatabaseRegistrationManager.hashPassword(password);

                    // Compare
                    if (storedHash.equals(enteredHash)) {
                        return role; // Successful login
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error during authentication: " + e.getMessage());
        } finally {
            dbConnection.closeConnection();
        }

        return null; // No match
    }

}
