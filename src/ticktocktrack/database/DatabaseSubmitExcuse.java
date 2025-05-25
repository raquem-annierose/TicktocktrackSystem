package ticktocktrack.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations related to submitting excuses,
 * including retrieving teacher names.
 */
public class DatabaseSubmitExcuse {

    /**
     * Retrieves the full names of all teachers in the system.
     * 
     * @return List of teacher names formatted as "FirstName LastName"
     */
    public static List<String> getAllTeacherNames() {
        List<String> teacherNames = new ArrayList<>();
        String query = "SELECT CONCAT(first_name, ' ', last_name) AS name FROM Teachers";

        DatabaseConnection dbConn = new DatabaseConnection();

        try {
            dbConn.connectToSQLServer(); // Connect first

            try (Connection conn = dbConn.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    teacherNames.add(rs.getString("name"));
                }

            }

        } catch (SQLException e) {
            System.err.println("Error fetching teacher names: " + e.getMessage());
        }

        return teacherNames;
    }
}
