package ticktocktrack.logic;

public class ClassAttendanceSummary {
    private int classId;
    private String courseName;
    private int presentCount;
    private int absentCount;
    private int excusedCount;
    private int lateCount;

    // constructor, getters, setters

    public ClassAttendanceSummary(int classId, String courseName, int presentCount, int absentCount, int excusedCount, int lateCount) {
        this.classId = classId;
        this.courseName = courseName;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.excusedCount = excusedCount;
        this.lateCount = lateCount;
    }
    
    public static class MonthlyAttendanceSummary {
        private int year;
        private int month;
        private int presentCount;
        private int absentCount;
        private int excusedCount;
        private int lateCount;

        public MonthlyAttendanceSummary(int year, int month, int presentCount, int absentCount, int excusedCount, int lateCount) {
            this.year = year;
            this.month = month;
            this.presentCount = presentCount;
            this.absentCount = absentCount;
            this.excusedCount = excusedCount;
            this.lateCount = lateCount;
        }

        public int getYear() { return year; }
        public int getMonth() { return month; }
        public int getPresentCount() { return presentCount; }
        public int getAbsentCount() { return absentCount; }
        public int getExcusedCount() { return excusedCount; }
        public int getLateCount() { return lateCount; }
    }


    public int getClassId() { return classId; }
    public String getCourseName() { return courseName; }
    public int getPresentCount() { return presentCount; }
    public int getAbsentCount() { return absentCount; }
    public int getExcusedCount() { return excusedCount; }
    public int getLateCount() { return lateCount; }
}
