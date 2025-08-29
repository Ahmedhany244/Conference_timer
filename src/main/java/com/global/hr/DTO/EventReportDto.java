package com.global.hr.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class EventReportDto {
    private Long eventId;
    private String eventName;
    private LocalDateTime eventStartTime;
    private LocalDateTime eventEndTime;
    private LocalDateTime reportGeneratedAt;
    private String reportGeneratedBy;
    
    // Summary statistics
    private int totalRegisteredAttendees;
    private int totalCheckedInAttendees;
    private Long totalActiveTime; // in seconds
    private Long totalBreakTime; // in seconds
    private Long averageActiveTimePerAttendee;
    private Long averageBreakTimePerAttendee;
    
    // Detailed attendee reports
    private List<AttendeeReportDto> attendeeReports;
    
    // Break statistics
    private int totalBreaks;
    private Long longestBreakDuration;
    private Long shortestBreakDuration;
    private String mostCommonBreakType;
    
    // Event duration
    private Long eventDurationMinutes;
    private Double attendanceRate; // percentage
    private Double averageParticipationRate; // active time / event duration
    
    // Constructors
    public EventReportDto() {}
    
    public EventReportDto(Long eventId, String eventName, LocalDateTime eventStartTime, 
                         LocalDateTime eventEndTime, LocalDateTime reportGeneratedAt, 
                         String reportGeneratedBy) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.reportGeneratedAt = reportGeneratedAt;
        this.reportGeneratedBy = reportGeneratedBy;
    }
    
    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public String getEventName() {
        return eventName;
    }
    
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    public LocalDateTime getEventStartTime() {
        return eventStartTime;
    }
    
    public void setEventStartTime(LocalDateTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }
    
    public LocalDateTime getEventEndTime() {
        return eventEndTime;
    }
    
    public void setEventEndTime(LocalDateTime eventEndTime) {
        this.eventEndTime = eventEndTime;
    }
    
    public LocalDateTime getReportGeneratedAt() {
        return reportGeneratedAt;
    }
    
    public void setReportGeneratedAt(LocalDateTime reportGeneratedAt) {
        this.reportGeneratedAt = reportGeneratedAt;
    }
    
    public String getReportGeneratedBy() {
        return reportGeneratedBy;
    }
    
    public void setReportGeneratedBy(String reportGeneratedBy) {
        this.reportGeneratedBy = reportGeneratedBy;
    }
    
    public int getTotalRegisteredAttendees() {
        return totalRegisteredAttendees;
    }
    
    public void setTotalRegisteredAttendees(int totalRegisteredAttendees) {
        this.totalRegisteredAttendees = totalRegisteredAttendees;
    }
    
    public int getTotalCheckedInAttendees() {
        return totalCheckedInAttendees;
    }
    
    public void setTotalCheckedInAttendees(int totalCheckedInAttendees) {
        this.totalCheckedInAttendees = totalCheckedInAttendees;
    }
    
    public Long getTotalActiveTime() {
        return totalActiveTime;
    }
    
    public void setTotalActiveTime(Long totalActiveTime) {
        this.totalActiveTime = totalActiveTime;
    }
    
    public Long getTotalBreakTime() {
        return totalBreakTime;
    }
    
    public void setTotalBreakTime(Long totalBreakTime) {
        this.totalBreakTime = totalBreakTime;
    }
    
    public Long getAverageActiveTimePerAttendee() {
        return averageActiveTimePerAttendee;
    }
    
    public void setAverageActiveTimePerAttendee(Long averageActiveTimePerAttendee) {
        this.averageActiveTimePerAttendee = averageActiveTimePerAttendee;
    }
    
    public Long getAverageBreakTimePerAttendee() {
        return averageBreakTimePerAttendee;
    }
    
    public void setAverageBreakTimePerAttendee(Long averageBreakTimePerAttendee) {
        this.averageBreakTimePerAttendee = averageBreakTimePerAttendee;
    }
    
    public List<AttendeeReportDto> getAttendeeReports() {
        return attendeeReports;
    }
    
    public void setAttendeeReports(List<AttendeeReportDto> attendeeReports) {
        this.attendeeReports = attendeeReports;
    }
    
    public int getTotalBreaks() {
        return totalBreaks;
    }
    
    public void setTotalBreaks(int totalBreaks) {
        this.totalBreaks = totalBreaks;
    }
    
    public Long getLongestBreakDuration() {
        return longestBreakDuration;
    }
    
    public void setLongestBreakDuration(Long longestBreakDuration) {
        this.longestBreakDuration = longestBreakDuration;
    }
    
    public Long getShortestBreakDuration() {
        return shortestBreakDuration;
    }
    
    public void setShortestBreakDuration(Long shortestBreakDuration) {
        this.shortestBreakDuration = shortestBreakDuration;
    }
    
    public String getMostCommonBreakType() {
        return mostCommonBreakType;
    }
    
    public void setMostCommonBreakType(String mostCommonBreakType) {
        this.mostCommonBreakType = mostCommonBreakType;
    }
    
    public Long getEventDurationMinutes() {
        return eventDurationMinutes;
    }
    
    public void setEventDurationMinutes(Long eventDurationMinutes) {
        this.eventDurationMinutes = eventDurationMinutes;
    }
    
    public Double getAttendanceRate() {
        return attendanceRate;
    }
    
    public void setAttendanceRate(Double attendanceRate) {
        this.attendanceRate = attendanceRate;
    }
    
    public Double getAverageParticipationRate() {
        return averageParticipationRate;
    }
    
    public void setAverageParticipationRate(Double averageParticipationRate) {
        this.averageParticipationRate = averageParticipationRate;
    }
    
    // Helper methods for formatted display
    public String getFormattedTotalActiveTime() {
        return formatSeconds(totalActiveTime);
    }
    
    public String getFormattedTotalBreakTime() {
        return formatSeconds(totalBreakTime);
    }
    
    public String getFormattedAverageActiveTime() {
        return formatSeconds(averageActiveTimePerAttendee);
    }
    
    public String getFormattedAverageBreakTime() {
        return formatSeconds(averageBreakTimePerAttendee);
    }
    
    private String formatSeconds(Long seconds) {
        if (seconds == null || seconds == 0) return "00:00:00";
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}
