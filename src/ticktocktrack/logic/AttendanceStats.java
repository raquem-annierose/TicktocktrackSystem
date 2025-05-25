package ticktocktrack.logic;

/**
 * The AttendanceStats class represents attendance statistics for an individual.
 * It includes counts for various attendance statuses such as present, absent,
 * late, and excused.
 */
public class AttendanceStats {
	
    /**
     * The number of times an individual was present.
     */
    public int present;
    
    /**
     * The number of times an individual was absent.
     */
    public int absent;
    
    /**
     * The number of times an individual was late.
     */
    public int late;
    
    /**
     * The number of times an individual's absence was excused.
     */
    public int excused;

    /**
     * Constructs an AttendanceStats object with specified attendance counts.
     *
     * @param present the number of times an individual was present
     * @param absent  the number of times an individual was absent
     * @param late    the number of times an individual was late
     * @param excused the number of times an individual's absence was excused
     */
    public AttendanceStats(int present, int absent, int late, int excused) {
        this.present = present;
        this.absent = absent;
        this.late = late;
        this.excused = excused;
    }
}
