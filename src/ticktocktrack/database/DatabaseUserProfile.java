package ticktocktrack.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUserProfile {

    public static void updateProfilePath(int userId, String imagePath) {
        String sql = "UPDATE Users SET profile_path = ? WHERE user_id = ?";
        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer(); // establish connection
            Connection conn = dbConn.getConnection();
            if (conn == null) {
                System.out.println("Failed to connect to the database.");
                return;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, imagePath);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
                System.out.println("Profile path updated in database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConn.closeConnection();
        }
    }

    // New method to get profile_path by userId
    public static String getProfilePath(int userId) {
        String sql = "SELECT profile_path FROM Users WHERE user_id = ?";
        DatabaseConnection dbConn = new DatabaseConnection();
        String profilePath = null;

        try {
            dbConn.connectToSQLServer();
            Connection conn = dbConn.getConnection();
            if (conn == null) {
                System.out.println("Failed to connect to the database.");
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        profilePath = rs.getString("profile_path");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConn.closeConnection();
        }

        return profilePath;
    }
    
    public static void removeProfilePath(int userId) {
        String sql = "UPDATE Users SET profile_path = NULL WHERE user_id = ?";
        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer(); // establish connection
            Connection conn = dbConn.getConnection();
            if (conn == null) {
                System.out.println("Failed to connect to the database.");
                return;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
                System.out.println("Profile path removed from database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConn.closeConnection();
        }
    }

}
