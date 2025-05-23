package ticktocktrack.logic;

public class AttendanceSummary {
    private int totalClasses;
    private int present;
    private int absent;
    private int late;
    private int excused;

    // Constructor
    public AttendanceSummary(int totalClasses, int present, int absent, int late, int excused) {
        this.totalClasses = totalClasses;
        this.present = present;
        this.absent = absent;
        this.late = late;
        this.excused = excused;
    }

    // Getters
    public int getTotalClasses() { return totalClasses; }
    public int getPresent() { return present; }
    public int getAbsent() { return absent; }
    public int getLate() { return late; }
    public int getExcused() { return excused; }
}
