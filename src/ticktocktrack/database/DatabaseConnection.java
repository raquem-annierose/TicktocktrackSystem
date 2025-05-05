package ticktocktrack.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=AttendanceDB;encrypt=false;trustServerCertificate=true;integratedSecurity=true;";
    private Connection conn;

    // Connect to SQL Server
    public void connectToSQLServer() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {
                conn = DriverManager.getConnection(URL);
                System.out.println("Connection successful.");
            } catch (SQLException e) {
                System.err.println("Failed to connect to the database: " + e.getMessage());
                throw e; // Rethrow the exception to be handled by the caller
            }
        }
    }

    // Close the connection to SQL Server
    public void closeConnection() {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("Connection closed successfully.");
                }
            } catch (SQLException e) {
                System.err.println("Failed to close the connection: " + e.getMessage());
            }
        }
    }

    // Getter for the connection
    public Connection getConnection() {
        return conn;
    }
}
