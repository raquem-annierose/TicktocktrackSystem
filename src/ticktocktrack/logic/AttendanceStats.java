package ticktocktrack.logic;

public class AttendanceStats {
    public int present;
    public int absent;
    public int late;
    public int excused;

    public AttendanceStats(int present, int absent, int late, int excused) {
        this.present = present;
        this.absent = absent;
        this.late = late;
        this.excused = excused;
    }
}
