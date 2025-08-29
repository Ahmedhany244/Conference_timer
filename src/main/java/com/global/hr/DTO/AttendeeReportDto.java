package com.global.hr.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class AttendeeReportDto {
    private Long userId;
    private String userName;
    private String userEmail;
    private LocalDateTime checkInTime;
    private LocalDateTime sessionStartTime;
    private LocalDateTime sessionEndTime;
    private Long totalActiveTime; // in seconds
    private Long totalBreakTime; // in seconds
    private int numberOfBreaks;
    private Double participationRate; // active time / event duration
    private List<BreakRecordDto> breaks;
    
    // Session status
    private boolean completedSession;
    private String sessionEndReason; // "MANUAL_STOP", "EVENT_END", "AUTO_STOP"
    
    // Constructors
    public AttendeeReportDto() {}
    
    public AttendeeReportDto(Long userId, String userName, String userEmail, 
                           LocalDateTime checkInTime, LocalDateTime sessionStartTime,
                           LocalDateTime sessionEndTime, Long totalActiveTime, 
                           Long totalBreakTime, int numberOfBreaks, 
                           Double participationRate, boolean completedSession) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.checkInTime = checkInTime;
        this.sessionStartTime = sessionStartTime;
        this.sessionEndTime = sessionEndTime;
        this.totalActiveTime = totalActiveTime;
        this.totalBreakTime = totalBreakTime;
        this.numberOfBreaks = numberOfBreaks;
        this.participationRate = participationRate;
        this.completedSession = completedSession;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
    
    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
    
    public LocalDateTime getSessionStartTime() {
        return sessionStartTime;
    }
    
    public void setSessionStartTime(LocalDateTime sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }
    
    public LocalDateTime getSessionEndTime() {
        return sessionEndTime;
    }
    
    public void setSessionEndTime(LocalDateTime sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
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
    
    public int getNumberOfBreaks() {
        return numberOfBreaks;
    }
    
    public void setNumberOfBreaks(int numberOfBreaks) {
        this.numberOfBreaks = numberOfBreaks;
    }
    
    public Double getParticipationRate() {
        return participationRate;
    }
    
    public void setParticipationRate(Double participationRate) {
        this.participationRate = participationRate;
    }
    
    public List<BreakRecordDto> getBreaks() {
        return breaks;
    }
    
    public void setBreaks(List<BreakRecordDto> breaks) {
        this.breaks = breaks;
    }
    
    public boolean isCompletedSession() {
        return completedSession;
    }
    
    public void setCompletedSession(boolean completedSession) {
        this.completedSession = completedSession;
    }
    
    public String getSessionEndReason() {
        return sessionEndReason;
    }
    
    public void setSessionEndReason(String sessionEndReason) {
        this.sessionEndReason = sessionEndReason;
    }
    
    // Helper methods for formatted display
    public String getFormattedActiveTime() {
        return formatSeconds(totalActiveTime);
    }
    
    public String getFormattedBreakTime() {
        return formatSeconds(totalBreakTime);
    }
    
    public String getFormattedParticipationRate() {
        if (participationRate == null) return "0%";
        return String.format("%.1f%%", participationRate);
    }
    
    public Long getTotalSessionTime() {
        if (totalActiveTime == null || totalBreakTime == null) return 0L;
        return totalActiveTime + totalBreakTime;
    }
    
    public String getFormattedTotalSessionTime() {
        return formatSeconds(getTotalSessionTime());
    }
    
    private String formatSeconds(Long seconds) {
        if (seconds == null || seconds == 0) return "00:00:00";
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}
