package ticktocktrack.logic;

/**
 * Represents a summary of attendance for a class, including counts of present,
 * absent, excused, and late occurrences.
 */
public class ClassAttendanceSummary {
    /**
     * The unique identifier of the class.
     */
    private int classId;

    /**
     * The name of the course associated with the class.
     */
    private String courseName;

    /**
     * The number of students marked as present in the class.
     */
    private int presentCount;

    /**
     * The number of students marked as absent in the class.
     */
    private int absentCount;

    /**
     * The number of students whose absence was excused in the class.
     */
    private int excusedCount;

    /**
     * The number of students marked as late in the class.
     */
    private int lateCount;

    /**
     * Constructs a ClassAttendanceSummary with the specified details.
     *
     * @param classId      the unique identifier of the class
     * @param courseName   the name of the course
     * @param presentCount the number of students marked as present
     * @param absentCount  the number of students marked as absent
     * @param excusedCount the number of excused absences
     * @param lateCount    the number of students marked as late
     */
    public ClassAttendanceSummary(int classId, String courseName, int presentCount, int absentCount, int excusedCount, int lateCount) {
        this.classId = classId;
        this.courseName = courseName;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.excusedCount = excusedCount;
        this.lateCount = lateCount;
    }

    /**
     * Represents a summary of monthly attendance statistics.
     */
    public static class MonthlyAttendanceSummary {
        /**
         * The year of the attendance summary.
         */
        private int year;

        /**
         * The month of the attendance summary.
         */
        private int month;

        /**
         * The number of students marked as present in the month.
         */
        private int presentCount;

        /**
         * The number of students marked as absent in the month.
         */
        private int absentCount;

        /**
         * The number of excused absences in the month.
         */
        private int excusedCount;

        /**
         * The number of students marked as late in the month.
         */
        private int lateCount;

        /**
         * Constructs a MonthlyAttendanceSummary with the specified details.
         *
         * @param year         the year of the summary
         * @param month        the month of the summary
         * @param presentCount the number of students marked as present
         * @param absentCount  the number of students marked as absent
         * @param excusedCount the number of excused absences
         * @param lateCount    the number of students marked as late
         */
        public MonthlyAttendanceSummary(int year, int month, int presentCount, int absentCount, int excusedCount, int lateCount) {
            this.year = year;
            this.month = month;
            this.presentCount = presentCount;
            this.absentCount = absentCount;
            this.excusedCount = excusedCount;
            this.lateCount = lateCount;
        }

        /** @return the year of the summary */
        public int getYear() { return year; }

        /** @return the month of the summary */
        public int getMonth() { return month; }

        /** @return the number of students marked as present */
        public int getPresentCount() { return presentCount; }

        /** @return the number of students marked as absent */
        public int getAbsentCount() { return absentCount; }

        /** @return the number of excused absences */
        public int getExcusedCount() { return excusedCount; }

        /** @return the number of students marked as late */
        public int getLateCount() { return lateCount; }
    }

    /** @return the unique identifier of the class */
    public int getClassId() { return classId; }

    /** @return the name of the course associated with the class */
    public String getCourseName() { return courseName; }

    /** @return the number of students marked as present */
    public int getPresentCount() { return presentCount; }

    /** @return the number of students marked as absent */
    public int getAbsentCount() { return absentCount; }

    /** @return the number of excused absences */
    public int getExcusedCount() { return excusedCount; }

    /** @return the number of students marked as late */
    public int getLateCount() { return lateCount; }
}
