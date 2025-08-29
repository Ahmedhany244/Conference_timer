package com.global.hr.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class TimerSessionDto {
    private Long id;
    private Long eventId;
    private String eventName;
    private Long userId;
    private String userName;
    private LocalDateTime sessionStartTime;
    private LocalDateTime sessionEndTime;
    private Long totalActiveTime; // in seconds
    private Long totalBreakTime; // in seconds
    private Boolean isActive;
    private Boolean isOnBreak;
    private LocalDateTime lastActivityTime;
    private String createdBy;
    private List<BreakRecordDto> breakRecords;
    
    // Constructors
    public TimerSessionDto() {}
    
    public TimerSessionDto(Long id, Long eventId, String eventName, Long userId, String userName,
                          LocalDateTime sessionStartTime, LocalDateTime sessionEndTime,
                          Long totalActiveTime, Long totalBreakTime, Boolean isActive, Boolean isOnBreak,
                          LocalDateTime lastActivityTime, String createdBy) {
        this.id = id;
        this.eventId = eventId;
        this.eventName = eventName;
        this.userId = userId;
        this.userName = userName;
        this.sessionStartTime = sessionStartTime;
        this.sessionEndTime = sessionEndTime;
        this.totalActiveTime = totalActiveTime;
        this.totalBreakTime = totalBreakTime;
        this.isActive = isActive;
        this.isOnBreak = isOnBreak;
        this.lastActivityTime = lastActivityTime;
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsOnBreak() {
        return isOnBreak;
    }
    
    public void setIsOnBreak(Boolean isOnBreak) {
        this.isOnBreak = isOnBreak;
    }
    
    public LocalDateTime getLastActivityTime() {
        return lastActivityTime;
    }
    
    public void setLastActivityTime(LocalDateTime lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public List<BreakRecordDto> getBreakRecords() {
        return breakRecords;
    }
    
    public void setBreakRecords(List<BreakRecordDto> breakRecords) {
        this.breakRecords = breakRecords;
    }
    
    // Helper methods for display
    public String getFormattedActiveTime() {
        if (totalActiveTime == null) return "00:00:00";
        long hours = totalActiveTime / 3600;
        long minutes = (totalActiveTime % 3600) / 60;
        long seconds = totalActiveTime % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    public String getFormattedBreakTime() {
        if (totalBreakTime == null) return "00:00:00";
        long hours = totalBreakTime / 3600;
        long minutes = (totalBreakTime % 3600) / 60;
        long seconds = totalBreakTime % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
