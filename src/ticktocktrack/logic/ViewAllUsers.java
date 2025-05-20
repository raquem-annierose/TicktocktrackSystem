package ticktocktrack.database;

import java.util.List;

import ticktocktrack.logic.UsersModel;

public class DatabaseViewAllUsers {

    public static void main(String[] args) {

        System.out.println("Admins:");
        List<UsersModel> admins = UserDAO.getAdmins();
        for (UsersModel admin : admins) {
            System.out.println(admin);
        }

        System.out.println("\nFaculty:");
        List<UsersModel> faculty = UserDAO.getTeachers();
        for (UsersModel teacher : faculty) {
            System.out.println(teacher);
        }

        System.out.println("\nStudents:");
        List<UsersModel> students = UserDAO.getStudents();
        for (UsersModel student : students) {
            System.out.println(student);
        }
    }
}
