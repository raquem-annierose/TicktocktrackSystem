package ticktocktrack.logic;

/**
 * Represents a user in the system, which can be an Admin, Teacher, or Student.
 * Holds user-related information including identifiers, personal details,
 * role, and metadata such as who created the user and when.
 */
public class UsersModel {

    /** Unique user identifier. */
    private int userId;

    /** Username used for login. */
    private String username;

    /** User's email address. */
    private String email;

    /** Role of the user, e.g., Admin, Teacher, Student. */
    private String role;

    /** Name of the user who created this user record. */
    private String createdByName;

    /** Date when the user was created (as a String). */
    private String dateCreated;

    /** Teacher-specific ID (nullable). */
    private Integer teacherId;

    /** Student-specific ID (nullable). */
    private Integer studentId;

    /** Admin-specific ID (nullable). */
    private Integer adminId;

    /** User's first name. */
    private String firstName;

    /** User's last name. */
    private String lastName;

    /** User's middle name. */
    private String middleName;

    /** Academic program of the user (usually for students). */
    private String program;

    /** Section or class section (usually for students). */
    private String section;

    /** Year level (usually for students). */
    private String yearLevel;

    /** Path or URI to the user's profile image. */
    private String profilePath;

    /**
     * Constructs a UsersModel with required user details.
     * 
     * @param userId   unique user ID
     * @param username username for login
     * @param email    user's email
     * @param role     user role (Admin, Teacher, Student)
     */
    public UsersModel(int userId, String username, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // --- Getters and Setters ---

    /**
     * Gets the user ID.
     * 
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     * 
     * @param userId the user ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the username.
     * 
     * @return username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username.
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email.
     * 
     * @return email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the email.
     * 
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user role.
     * 
     * @return role string
     */
    public String getRole() {
        return role;
    }
    
    /**
     * Sets the user role.
     * 
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the teacher ID if this user is a teacher.
     * 
     * @return teacher ID or null
     */
    public Integer getTeacherId() {
        return teacherId;
    }

    /**
     * Sets the teacher ID.
     * 
     * @param teacherId the teacher ID to set
     */
    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    /**
     * Gets the student ID if this user is a student.
     * 
     * @return student ID or null
     */
    public Integer getStudentId() {
        return studentId;
    }

    /**
     * Sets the student ID.
     * 
     * @param studentId the student ID to set
     */
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    /**
     * Gets the admin ID if this user is an admin.
     * 
     * @return admin ID or null
     */
    public Integer getAdminId() {
        return adminId;
    }

    /**
     * Sets the admin ID.
     * 
     * @param adminId the admin ID to set
     */
    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    /**
     * Gets the user's first name.
     * 
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     * 
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name.
     * 
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     * 
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the user's middle name.
     * 
     * @return middle name
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the user's middle name.
     * 
     * @param middleName the middle name to set
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Returns the full name constructed from first, middle, and last names.
     * Skips null components.
     * 
     * @return full name as a string
     */
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + 
               (middleName != null ? middleName + " " : "") +
               (lastName != null ? lastName : "");
    }

    /**
     * Gets the academic program.
     * 
     * @return program
     */
    public String getProgram() {
        return program;
    }

    /**
     * Sets the academic program.
     * 
     * @param program the program to set
     */
    public void setProgram(String program) {
        this.program = program;
    }

    /**
     * Gets the section.
     * 
     * @return section
     */
    public String getSection() {
        return section;
    }

    /**
     * Sets the section.
     * 
     * @param section the section to set
     */
    public void setSection(String section) {
        this.section = section;
    }

    /**
     * Gets the year level.
     * 
     * @return year level
     */
    public String getYearLevel() {
        return yearLevel;
    }

    /**
     * Sets the year level.
     * 
     * @param yearLevel the year level to set
     */
    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    /**
     * Gets the name of the user who created this user.
     * 
     * @return creator's name
     */
    public String getCreatedByName() { 
        return createdByName; 
    }

    /**
     * Sets the name of the user who created this user.
     * 
     * @param createdByName creator's name to set
     */
    public void setCreatedByName(String createdByName) { 
        this.createdByName = createdByName; 
    }
    
    /**
     * Gets the creation date of this user record.
     * 
     * @return date created as a string
     */
    public String getDateCreated() {
        return dateCreated;
    }
    
    /**
     * Sets the creation date of this user record.
     * 
     * @param dateCreated the date to set
     */
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
    
    /**
     * Gets the profile image path or URI.
     * 
     * @return profile image path
     */
    public String getProfilePath() {
        return profilePath;
    }

    /**
     * Sets the profile image path or URI.
     * 
     * @param profilePath the profile path to set
     */
    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }
    
    
    

}
