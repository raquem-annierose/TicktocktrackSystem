package ticktocktrack.logic;

/**
 * Represents a student with enrollment and attendance details.
 */
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

    /** 
     * Gets the profile image path of the student.
     * @return the profilePath
     */
    public String getProfilePath() { return profilePath; }

    /**
     * Sets the profile image path of the student.
     * @param profilePath the path to set
     */
    public void setProfilePath(String profilePath) { this.profilePath = profilePath; }

    /** Default constructor. */
    public Student() {
        // Default constructor
    }

    /**
     * Constructs a Student with all fields.
     */
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

    /**
     * Constructs a Student object for attendance use.
     */
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

    /** @return the enrollmentId */
    public int getEnrollmentId() {
        return enrollmentId;
    }

    /** @param enrollmentId the enrollmentId to set */
    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    /** @return the studentId */
    public int getStudentId() {
        return studentId;
    }

    /** @param studentId the studentId to set */
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    /** @return the userId */
    public int getUserId() {
        return userId;
    }

    /** @param userId the userId to set */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /** @return the username */
    public String getUsername() {
        return username;
    }

    /** @param username the username to set */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return the firstName or empty string if null */
    public String getFirstName() {
        return firstName != null ? firstName : "";
    }

    /** @param firstName the firstName to set */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** @return the middleName or empty string if null */
    public String getMiddleName() {
        return middleName != null ? middleName : "";
    }

    /** @param middleName the middleName to set */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /** @return the lastName or empty string if null */
    public String getLastName() {
        return lastName != null ? lastName : "";
    }

    /** @param lastName the lastName to set */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** @return the section */
    public String getSection() {
        return section;
    }

    /** @param section the section to set */
    public void setSection(String section) {
        this.section = section;
    }

    /** @return the yearLevel */
    public String getYearLevel() {
        return yearLevel;
    }

    /** @param yearLevel the yearLevel to set */
    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    /** @return the email */
    public String getEmail() {
        return email;
    }

    /** @param email the email to set */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return the program */
    public String getProgram() {
        return program;
    }

    /** @param program the program to set */
    public void setProgram(String program) {
        this.program = program;
    }

    /** @return the status or empty string if null */
    public String getStatus() {
        return status != null ? status : "";
    }

    /** @param status the status to set */
    public void setStatus(String status) {
        this.status = status;
    }

    /** @return the approvalStatus or empty string if null */
    public String getApprovalStatus() {
        return approvalStatus != null ? approvalStatus : "";
    }

    /** @param approvalStatus the approvalStatus to set */
    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    /** @return the reason or empty string if null */
    public String getReason() {
        return reason != null ? reason : "";
    }

    /** @param reason the reason to set */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /** @return the date or empty string if null */
    public String getDate() {
        return date != null ? date : "";
    }

    /** @param date the date to set */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Returns a string representation of the student.
     */
    @Override
    public String toString() {
        return String.format("%s %s %s (%s)", firstName, middleName, lastName, studentId);
    }

    /**
     * Returns the full name of the student.
     * @return full name as "First Middle Last"
     */
    public String getFullName() {
        return String.format("%s %s %s", getFirstName(), getMiddleName(), getLastName())
                .trim().replaceAll(" +", " ");
    }

    /** @param totalClasses the totalClasses to set */
    public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }

    /** @return the totalClasses */
    public int getTotalClasses() { return totalClasses; }

    /** @param present the present count to set */
    public void setPresent(int present) { this.present = present; }

    /** @return the present count */
    public int getPresent() { return present; }

    /** @param absent the absent count to set */
    public void setAbsent(int absent) { this.absent = absent; }

    /** @return the absent count */
    public int getAbsent() { return absent; }

    /** @param late the late count to set */
    public void setLate(int late) { this.late = late; }

    /** @return the late count */
    public int getLate() { return late; }

    /** @param excused the excused count to set */
    public void setExcused(int excused) { this.excused = excused; }

    /** @return the excused count */
    public int getExcused() { return excused; }
}
