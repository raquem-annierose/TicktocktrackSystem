package ticktocktrack.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides a utility for managing database connections to a SQL Server.
 * This class includes methods to connect, close, and retrieve the active connection.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=AttendanceDB;encrypt=false;trustServerCertificate=true;integratedSecurity=true;";
    private Connection conn;

    /**
     * Establishes a connection to the SQL Server database.
     * 
     * @throws SQLException if the connection attempt fails.
     */
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

    /**
     * Closes the active connection to the SQL Server database if it exists.
     * Prints a message indicating success or failure.
     */
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

    /**
     * Retrieves the active database connection.
     * 
     * @return the active {@link Connection} object, or {@code null} if no connection is established.
     */
    public Connection getConnection() {
        return conn;
    }
}
