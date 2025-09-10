package com.global.hr.DTO;

/**
 * DTO for comprehensive analytics overview
 */
public class AnalyticsOverviewResponse {
    private final double averageAttendanceTimeHours;
    private final double medianAttendanceTimeHours;
    private final long totalActiveAttendees;
    private final double overallAttendanceRate;
    private final long totalScanEvents;
    private final double averageSessionDuration;
    
    public AnalyticsOverviewResponse(double averageAttendanceTimeHours, 
                                   double medianAttendanceTimeHours,
                                   long totalActiveAttendees, 
                                   double overallAttendanceRate,
                                   long totalScanEvents,
                                   double averageSessionDuration) {
        this.averageAttendanceTimeHours = averageAttendanceTimeHours;
        this.medianAttendanceTimeHours = medianAttendanceTimeHours;
        this.totalActiveAttendees = totalActiveAttendees;
        this.overallAttendanceRate = overallAttendanceRate;
        this.totalScanEvents = totalScanEvents;
        this.averageSessionDuration = averageSessionDuration;
    }
    
    public double getAverageAttendanceTimeHours() {
        return averageAttendanceTimeHours;
    }
    
    public double getMedianAttendanceTimeHours() {
        return medianAttendanceTimeHours;
    }
    
    public long getTotalActiveAttendees() {
        return totalActiveAttendees;
    }
    
    public double getOverallAttendanceRate() {
        return overallAttendanceRate;
    }
    
    public long getTotalScanEvents() {
        return totalScanEvents;
    }
    
    public double getAverageSessionDuration() {
        return averageSessionDuration;
    }
}
