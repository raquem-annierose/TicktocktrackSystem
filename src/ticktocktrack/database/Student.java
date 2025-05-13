package ticktocktrack.database;

public class Student {
    private int student_id;           // NEW: student_id field
    private String username;
    private String firstName;
    private String lastName;
    private String section;
    private String yearLevel;

    // Constructor without student_id (still works for existing code)
    public Student(String username, String firstName, String lastName, String section, String yearLevel) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.section = section;
        this.yearLevel = yearLevel;
    }

    // NEW: Constructor with student_id (for future use)
    public Student(int student_id, String username, String firstName, String lastName, String section, String yearLevel) {
        this.student_id = student_id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.section = section;
        this.yearLevel = yearLevel;
    }

    // NEW: Getter for student_id
    public int getStudentId() {
        return student_id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSection() {
        return section;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + section + ")";
    }
}
