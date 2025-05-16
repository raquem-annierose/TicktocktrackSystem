package ticktocktrack.logic;

import java.util.List;
import ticktocktrack.database.DatabaseAttendance;
import ticktocktrack.gui.TeacherMarkAttendanceCenterPanel.Student;

public class AttendanceHandler {
    public static void markAttendance(List<Student> students) {
        for (Student s : students) {
            DatabaseAttendance.saveAttendance(
                s.getStudentId(),
                s.getDate(),
                s.getStatus(),
                s.getReason()
            );
        }
    }
}
