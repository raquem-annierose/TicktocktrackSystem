package ticktocktrack.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ticktocktrack.logic.UsersModel;

public class UserDAO {

    private static DatabaseConnection dbConnection = new DatabaseConnection();

    public static List<UsersModel> getAdmins() {
        List<UsersModel> list = new ArrayList<>();
        String query = """
            SELECT a.admin_id, u.user_id, u.username, u.email, u.role, a.first_name, a.last_name
            FROM Admins a
            JOIN Users u ON a.user_id = u.user_id
        """;

        try {
            dbConnection.connectToSQLServer();
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsersModel user = new UsersModel(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")
                    );
                    user.setAdminId(rs.getInt("admin_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    list.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConnection.closeConnection();
        }

        return list;
    }

    public static List<UsersModel> getTeachers() {
        List<UsersModel> list = new ArrayList<>();
        String query = """
            SELECT t.teacher_id, u.user_id, u.username, u.email, u.role, t.first_name, t.last_name
            FROM Teachers t
            JOIN Users u ON t.user_id = u.user_id
        """;

        try {
            dbConnection.connectToSQLServer();
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsersModel user = new UsersModel(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")
                    );
                    user.setTeacherId(rs.getInt("teacher_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    list.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConnection.closeConnection();
        }

        return list;
    }

    public static List<UsersModel> getStudents() {
        List<UsersModel> list = new ArrayList<>();
        String query = """
            SELECT s.student_id, u.user_id, u.username, u.email, u.role,
                   s.first_name, s.middle_name, s.last_name,
                   s.year_level, s.program, s.section
            FROM Students s
            JOIN Users u ON s.user_id = u.user_id
        """;

        try {
            dbConnection.connectToSQLServer();
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsersModel user = new UsersModel(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")
                    );
                    user.setStudentId(rs.getInt("student_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setYearLevel(rs.getString("year_level"));
                    user.setProgram(rs.getString("program"));
                    user.setSection(rs.getString("section"));
                    list.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConnection.closeConnection();
        }

        return list;
    }
}
