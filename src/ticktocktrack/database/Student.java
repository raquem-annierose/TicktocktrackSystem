package ticktocktrack.database;

public class Student {
    private int student_id;           // student_id field
    private String username;
    private String firstName;
    private String middleName;       // NEW: middleName field
    private String lastName;
    private String section;
    private String yearLevel;
    private String email; // Add this field

 // Constructor without student_id (still works for existing code)
    public Student(String username, String firstName, String middleName, String lastName, String section, String yearLevel, String email) {
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;   // Initialize middleName
        this.lastName = lastName;
        this.section = section;
        this.yearLevel = yearLevel;
        this.email = email;             // Initialize email
    }

    // Constructor with student_id (for future use)
    public Student(int student_id, String username, String firstName, String middleName, String lastName, String section, String yearLevel) {
        this.student_id = student_id;
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;   // Initialize middleName
        this.lastName = lastName;
        this.section = section;
        this.yearLevel = yearLevel;
    }
 // Add getter
    public String getEmail() {
        return email;
    }
    
    // Getter for student_id
    public int getStudentId() {
        return student_id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {     // NEW: Getter for middleName
        return middleName;
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
        return firstName + " " + (middleName != null ? middleName + " " : "") + lastName + " (" + section + ")";
    }
}
