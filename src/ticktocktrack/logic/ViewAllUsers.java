package ticktocktrack.logic;

import java.util.List;

import ticktocktrack.database.UserDAO;

/**
 * A simple test or utility class to display all users categorized by their roles:
 * Admins, Faculty (Teachers), and Students.
 * 
 * This class retrieves users from the UserDAO and prints their details to the console.
 */
public class ViewAllUsers {

    /**
     * The main method that runs the program.
     * It fetches lists of admins, teachers, and students from UserDAO
     * and prints their information to standard output.
     * 
     * @param args command-line arguments (not used)
     */
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