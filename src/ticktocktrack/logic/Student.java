package ticktocktrack.logic;

public class Student {
    private int enrollmentId;
    private int studentId; // Use String for easy filtering in GUI
    private int userId;
    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    private String section;
    private String yearLevel;
    private String email;
    private String program;
    private String status;
    private String approvalStatus;
    private String reason; // For attendance reason dialog
    private String date;
 // In ticktocktrack.logic.Student.java
    private int totalClasses;
    private int present;
    private int absent;
    private int late;
    private int excused;
    private String profilePath;
    public String getProfilePath() { return profilePath; }
    public void setProfilePath(String profilePath) { this.profilePath = profilePath; }


    public Student() {
        // Default constructor
    }

    // Constructor with all fields
    public Student(int enrollmentId, int studentId, int userId, String username,
                   String firstName, String middleName, String lastName,
                   String section, String yearLevel, String email, String program,
                   String status, String approvalStatus, String reason, String date) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.section = section;
        this.yearLevel = yearLevel;
        this.email = email;
        this.program = program;
        this.status = status;
        this.approvalStatus = approvalStatus;
        this.reason = reason;
        this.date = date;
    }

    // Constructor for attendance use
    public Student(int studentId, String lastName, String firstName, String middleName,
                   String date, String status, String reason) {
        this.studentId = studentId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.date = date;
        this.status = status;
        this.reason = reason;
    }

    // Getters and Setters
    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName != null ? firstName : "";
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName != null ? middleName : "";
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName != null ? lastName : "";
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

    public String getStatus() {
        return status != null ? status : "";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovalStatus() {
        return approvalStatus != null ? approvalStatus : "";
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getReason() {
        return reason != null ? reason : "";
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate() {
        return date != null ? date : "";
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s (%s)", firstName, middleName, lastName, studentId);
    }

    public String getFullName() {
        return String.format("%s %s %s", getFirstName(), getMiddleName(), getLastName())
                .trim().replaceAll(" +", " ");
    }
    
    public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }
    public int getTotalClasses() { return totalClasses; }

    public void setPresent(int present) { this.present = present; }
    public int getPresent() { return present; }

    public void setAbsent(int absent) { this.absent = absent; }
    public int getAbsent() { return absent; }

    public void setLate(int late) { this.late = late; }
    public int getLate() { return late; }

    public void setExcused(int excused) { this.excused = excused; }
    public int getExcused() { return excused; }
	
}
