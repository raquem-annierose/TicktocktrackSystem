package ticktocktrack.logic;

import ticktocktrack.database.DatabaseConnection;
import ticktocktrack.database.DatabaseRegistrationManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
     * @return The role of the user ( "Admin", "Teacher", "Student") if successful; otherwise null.
     */
    public String authenticate(String username, String password) {
        // --- HARD CODED USERS ---
        if ("admin".equals(username) && "adminpass".equals(password)) {
            UsersModel user = new UsersModel(0, "admin", "admin@example.com", "Admin");
            user.setAdminId(0);
            user.setFirstName("Hardcoded");
            user.setLastName("Admin");
            Session.setCurrentUser(user);
            return "Admin";
        }
        if ("teacher".equals(username) && "teacherpass".equals(password)) {
            UsersModel user = new UsersModel(1, "teacher", "teacher@example.com", "Teacher");
            user.setTeacherId(1);
            user.setFirstName("Hardcoded");
            user.setLastName("Teacher");
            Session.setCurrentUser(user);
            return "Teacher";
        }
        if ("student".equals(username) && "studentpass".equals(password)) {
            UsersModel user = new UsersModel(2, "student", "student@example.com", "Student");
            user.setStudentId(2);
            user.setFirstName("Hardcoded");
            user.setLastName("Student");
            user.setMiddleName("H");
            user.setYearLevel("3");
            user.setProgram("CS");
            user.setSection("A");
            Session.setCurrentUser(user);
            return "Student";
        }
        // --- END HARD CODED USERS ---

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            dbConnection.connectToSQLServer();
            conn = dbConnection.getConnection();

            String sql = "SELECT user_id, username, email, role, password_hash, profile_path FROM Users WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String role = rs.getString("role");
                String email = rs.getString("email");
                String storedHash = rs.getString("password_hash");
                String profilePath = rs.getString("profile_path");  // NEW

                String enteredHash = DatabaseRegistrationManager.hashPassword(password);

                System.out.println("Stored Hash: " + storedHash);
                System.out.println("Entered Hash: " + enteredHash);

                if (storedHash != null && storedHash.equals(enteredHash)) {
                    UsersModel user = new UsersModel(userId, username, email, role);
                    user.setProfilePath(profilePath);  // Set profilePath

                    switch (role.toLowerCase()) {
                        case "teacher":
                            String teacherSql = "SELECT teacher_id, first_name, last_name FROM Teachers WHERE user_id = ?";
                            try (PreparedStatement teacherStmt = conn.prepareStatement(teacherSql)) {
                                teacherStmt.setInt(1, userId);
                                try (ResultSet tRs = teacherStmt.executeQuery()) {
                                    if (tRs.next()) {
                                        user.setTeacherId(tRs.getInt("teacher_id"));
                                        user.setFirstName(tRs.getString("first_name"));
                                        user.setLastName(tRs.getString("last_name"));
                                    }
                                }
                            }
                            break;

                        case "student":
                            String studentSql = "SELECT student_id, first_name, last_name, middle_name, year_level, program, section FROM Students WHERE user_id = ?";
                            try (PreparedStatement studentStmt = conn.prepareStatement(studentSql)) {
                                studentStmt.setInt(1, userId);
                                try (ResultSet sRs = studentStmt.executeQuery()) {
                                    if (sRs.next()) {
                                        user.setStudentId(sRs.getInt("student_id"));
                                        user.setFirstName(sRs.getString("first_name"));
                                        user.setLastName(sRs.getString("last_name"));
                                        user.setMiddleName(sRs.getString("middle_name"));
                                        user.setYearLevel(sRs.getString("year_level"));
                                        user.setProgram(sRs.getString("program"));
                                        user.setSection(sRs.getString("section"));
                                    }
                                }
                            }
                            break;

                        case "admin":
                            String adminSql = "SELECT admin_id, first_name, last_name FROM Admins WHERE user_id = ?";
                            try (PreparedStatement adminStmt = conn.prepareStatement(adminSql)) {
                                adminStmt.setInt(1, userId);
                                try (ResultSet aRs = adminStmt.executeQuery()) {
                                    if (aRs.next()) {
                                        user.setAdminId(aRs.getInt("admin_id"));
                                        user.setFirstName(aRs.getString("first_name"));
                                        user.setLastName(aRs.getString("last_name"));
                                    }
                                }
                            }
                            break;

                        // If you have HeadAdmin or other roles, add here similarly
                    }

                    Session.setCurrentUser(user);
                    return role;
                } else {
                    showAlert(AlertType.ERROR, "Login Failed", "Incorrect password.");
                }
            } else {
                showAlert(AlertType.ERROR, "Login Failed", "Username not found.");
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Error", "Error during authentication: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                dbConnection.closeConnection();  // if your class needs this
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }



    private static void showAlert(AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            alert.setOnShown(event -> {
                double screenWidth = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
                double screenHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();

                double alertWidth = alert.getDialogPane().getScene().getWindow().getWidth();
                double alertHeight = alert.getDialogPane().getScene().getWindow().getHeight();

                double centerX = (screenWidth - alertWidth) / 2;
                double centerY = (screenHeight - alertHeight) / 2;

                alert.getDialogPane().getScene().getWindow().setX(600);
                alert.getDialogPane().getScene().getWindow().setY(400);
            });

            alert.showAndWait();
        });
    }
}
