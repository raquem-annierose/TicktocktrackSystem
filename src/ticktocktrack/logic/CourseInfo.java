package ticktocktrack.logic;

public class CourseInfo {
    public String courseName;
    public String section;
    public String program;

    public CourseInfo(String courseName, String section, String program) {
        this.courseName = courseName;
        this.section = section;
        this.program = program;
    }

    // Optional: You can override toString if needed
    @Override
    public String toString() {
        return courseName + " - " + section + " - " + program;
    }
}
