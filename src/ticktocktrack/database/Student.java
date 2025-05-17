package ticktocktrack.database;

public class Student {
    private int student_id;
    private int user_id;
    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    private String section;
    private String yearLevel;
    private String email;
    private String program;

    public Student() {
        // Default constructor
    }

    public Student(String username, String firstName, String middleName, String lastName, String section, String yearLevel, String email) {
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.section = section;
        this.yearLevel = yearLevel;
        this.email = email;
    }

    public Student(int student_id, int user_id, String firstName, String middleName, String lastName, String section, String yearLevel, String program) {
        this.student_id = student_id;
        this.user_id = user_id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.section = section;
        this.yearLevel = yearLevel;
        this.program = program;
    }

    public int getStudentId() {
        return student_id;
    }

    public void setStudentId(int student_id) {
        this.student_id = student_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    @Override
    public String toString() {
        return firstName + " " + (middleName != null ? middleName + " " : "") + lastName + " (" + section + ")";
    }
    
    public String getFullName() {
        return firstName + " " +
               (middleName != null && !middleName.isBlank() ? middleName + " " : "") +
               lastName;
    }

}
