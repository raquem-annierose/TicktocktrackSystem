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
    private String program; // Add this field for program

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
    public Student(int student_id, String username, String firstName, String middleName, String lastName, String section, String yearLevel, String program) {
        this.student_id = student_id;
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;   // Initialize middleName
        this.lastName = lastName;
        this.section = section;
        this.yearLevel = yearLevel;
        this.program = program; // Initialize program
    }
    
    // Getter and setter for program
    public String getProgram() {
        return program;
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
    
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public String toString() {
        return firstName + " " + (middleName != null ? middleName + " " : "") + lastName + " (" + section + ")";
    }
}
