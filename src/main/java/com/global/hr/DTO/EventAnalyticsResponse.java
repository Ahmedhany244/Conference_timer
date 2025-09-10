package com.global.hr.DTO;

/**
 * DTO for detailed event-specific analytics
 */
public class EventAnalyticsResponse {
    private final Long eventId;
    private final String eventName;
    private final long totalRegistrations;
    private final long totalAttendees;
    private final double attendanceRate;
    private final double averageAttendanceTimeHours;
    private final double totalAttendanceHours;
    private final long totalCheckIns;
    private final long totalCheckOuts;
    private final double averageSessionDuration;
    private final String eventStartTime;
    private final String eventEndTime;
    
    public EventAnalyticsResponse(Long eventId, String eventName, long totalRegistrations,
                                long totalAttendees, double attendanceRate, 
                                double averageAttendanceTimeHours, double totalAttendanceHours,
                                long totalCheckIns, long totalCheckOuts, 
                                double averageSessionDuration, String eventStartTime, String eventEndTime) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.totalRegistrations = totalRegistrations;
        this.totalAttendees = totalAttendees;
        this.attendanceRate = attendanceRate;
        this.averageAttendanceTimeHours = averageAttendanceTimeHours;
        this.totalAttendanceHours = totalAttendanceHours;
        this.totalCheckIns = totalCheckIns;
        this.totalCheckOuts = totalCheckOuts;
        this.averageSessionDuration = averageSessionDuration;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
    }
    
    public Long getEventId() {
        return eventId;
    }
    
    public String getEventName() {
        return eventName;
    }
    
    public long getTotalRegistrations() {
        return totalRegistrations;
    }
    
    public long getTotalAttendees() {
        return totalAttendees;
    }
    
    public double getAttendanceRate() {
        return attendanceRate;
    }
    
    public double getAverageAttendanceTimeHours() {
        return averageAttendanceTimeHours;
    }
    
    public double getTotalAttendanceHours() {
        return totalAttendanceHours;
    }
    
    public long getTotalCheckIns() {
        return totalCheckIns;
    }
    
    public long getTotalCheckOuts() {
        return totalCheckOuts;
    }
    
    public double getAverageSessionDuration() {
        return averageSessionDuration;
    }
    
    public String getEventStartTime() {
        return eventStartTime;
    }
    
    public String getEventEndTime() {
        return eventEndTime;
    }
}
