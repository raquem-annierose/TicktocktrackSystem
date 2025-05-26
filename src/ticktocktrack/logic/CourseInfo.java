package ticktocktrack.logic;

/**
 * Represents information about a course, including its name, section, and program.
 */
public class CourseInfo {

    /**
     * The name of the course.
     */
    public String courseName;

    /**
     * The section of the course.
     */
    public String section;

    /**
     * The program to which the course belongs.
     */
    public String program;

    /**
     * Constructs a CourseInfo object with the specified details.
     *
     * @param courseName the name of the course
     * @param section    the section of the course
     * @param program    the program to which the course belongs
     */
    public CourseInfo(String courseName, String section, String program) {
        this.courseName = courseName;
        this.section = section;
        this.program = program;
    }

    /**
     * Gets the name of the course.
     *
     * @return the course name
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Gets the section of the course.
     *
     * @return the course section
     */
    public String getSection() {
        return section;
    }

    /**
     * Gets the program to which the course belongs.
     *
     * @return the course program
     */
    public String getProgram() {
        return program;
    }

    /**
     * Returns a string representation of the CourseInfo object.
     * The format is "courseName - section - program".
     *
     * @return a string representation of the CourseInfo object
     */
    @Override
    public String toString() {
        return courseName + " - " + section + " - " + program;
    }
}

