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
     * @return The role of the user ("HeadAdmin", "Admin", "Teacher", "Student") if successful; otherwise null.
     */
    public String authenticate(String username, String password) {
        // Check hardcoded accounts first
        if (username.equals("headadmin") && password.equals("headadmin123")) {
            UsersModel user = new UsersModel(-1, "headadmin", "headadmin@example.com", "HeadAdmin");
            user.setAdminId(-1);
            user.setFirstName("Head");
            user.setLastName("Admin");
            Session.setCurrentUser(user);
            return "HeadAdmin";
        } else if (username.equals("headteacher") && password.equals("headteacher123")) {
            UsersModel user = new UsersModel(-2, "headteacher", "headteacher@example.com", "Teacher");
            user.setTeacherId(-2);
            user.setFirstName("Head");
            user.setLastName("Teacher");
            Session.setCurrentUser(user);
            return "Teacher";
        } else if (username.equals("headstudent") && password.equals("headstudent123")) {
            UsersModel user = new UsersModel(-3, "headstudent", "headstudent@example.com", "Student");
            user.setStudentId(-3);
            user.setFirstName("Head");
            user.setLastName("Student");
            user.setMiddleName("Hardcoded");
            user.setYearLevel("4");
            user.setProgram("BSIT");
            user.setSection("A");
            Session.setCurrentUser(user);
            return "Student";
        }

        // If not a hardcoded account, proceed with database lookup
        try {
            dbConnection.connectToSQLServer();
            Connection conn = dbConnection.getConnection();

            String sql = "SELECT user_id, username, email, role, password_hash FROM Users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String role = rs.getString("role");
                String email = rs.getString("email");
                String storedHash = rs.getString("password_hash");

                String enteredHash = DatabaseRegistrationManager.hashPassword(password);

                if (storedHash != null && storedHash.equals(enteredHash)) {
                    UsersModel user = new UsersModel(userId, username, email, role);

                    switch (role.toLowerCase()) {
                        case "teacher":
                            String teacherSql = "SELECT teacher_id, first_name, last_name FROM Teachers WHERE user_id = ?";
                            try (PreparedStatement teacherStmt = conn.prepareStatement(teacherSql)) {
                                teacherStmt.setInt(1, userId);
                                ResultSet tRs = teacherStmt.executeQuery();
                                if (tRs.next()) {
                                    user.setTeacherId(tRs.getInt("teacher_id"));
                                    user.setFirstName(tRs.getString("first_name"));
                                    user.setLastName(tRs.getString("last_name"));
                                }
                            }
                            break;

                        case "student":
                            String studentSql = "SELECT student_id, first_name, last_name, middle_name, year_level, program, section FROM Students WHERE user_id = ?";
                            try (PreparedStatement studentStmt = conn.prepareStatement(studentSql)) {
                                studentStmt.setInt(1, userId);
                                ResultSet sRs = studentStmt.executeQuery();
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
                            break;

                        case "admin":
                        case "headadmin":
                            String adminSql = "SELECT admin_id, first_name, last_name FROM Admins WHERE user_id = ?";
                            try (PreparedStatement adminStmt = conn.prepareStatement(adminSql)) {
                                adminStmt.setInt(1, userId);
                                ResultSet aRs = adminStmt.executeQuery();
                                if (aRs.next()) {
                                    user.setAdminId(aRs.getInt("admin_id"));
                                    user.setFirstName(aRs.getString("first_name"));
                                    user.setLastName(aRs.getString("last_name"));
                                }
                            }
                            break;
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
            dbConnection.closeConnection();
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
