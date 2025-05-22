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

    public String getCourseName() {
        return courseName;
    }

    public String getSection() {
        return section;
    }

    public String getProgram() {
        return program;
    }

    @Override
    public String toString() {
        return courseName + " - " + section + " - " + program;
    }
}
