package com.global.hr.DTO;

import com.global.hr.Entity.BreakRecord;
import java.time.LocalDateTime;

public class BreakRecordDto {
    private Long id;
    private Long timerSessionId;
    private LocalDateTime breakStartTime;
    private LocalDateTime breakEndTime;
    private Long breakDuration; // in seconds
    private BreakRecord.BreakType breakType;
    private String breakReason;
    private String initiatedBy;
    private String endedBy;
    
    // Constructors
    public BreakRecordDto() {}
    
    public BreakRecordDto(Long id, Long timerSessionId, LocalDateTime breakStartTime, 
                         LocalDateTime breakEndTime, Long breakDuration, BreakRecord.BreakType breakType,
                         String breakReason, String initiatedBy, String endedBy) {
        this.id = id;
        this.timerSessionId = timerSessionId;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
        this.breakDuration = breakDuration;
        this.breakType = breakType;
        this.breakReason = breakReason;
        this.initiatedBy = initiatedBy;
        this.endedBy = endedBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTimerSessionId() {
        return timerSessionId;
    }
    
    public void setTimerSessionId(Long timerSessionId) {
        this.timerSessionId = timerSessionId;
    }
    
    public LocalDateTime getBreakStartTime() {
        return breakStartTime;
    }
    
    public void setBreakStartTime(LocalDateTime breakStartTime) {
        this.breakStartTime = breakStartTime;
    }
    
    public LocalDateTime getBreakEndTime() {
        return breakEndTime;
    }
    
    public void setBreakEndTime(LocalDateTime breakEndTime) {
        this.breakEndTime = breakEndTime;
    }
    
    public Long getBreakDuration() {
        return breakDuration;
    }
    
    public void setBreakDuration(Long breakDuration) {
        this.breakDuration = breakDuration;
    }
    
    public BreakRecord.BreakType getBreakType() {
        return breakType;
    }
    
    public void setBreakType(BreakRecord.BreakType breakType) {
        this.breakType = breakType;
    }
    
    public String getBreakReason() {
        return breakReason;
    }
    
    public void setBreakReason(String breakReason) {
        this.breakReason = breakReason;
    }
    
    public String getInitiatedBy() {
        return initiatedBy;
    }
    
    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }
    
    public String getEndedBy() {
        return endedBy;
    }
    
    public void setEndedBy(String endedBy) {
        this.endedBy = endedBy;
    }
    
    // Helper methods
    public boolean isActive() {
        return breakEndTime == null;
    }
    
    public String getFormattedDuration() {
        if (breakDuration == null) return "00:00:00";
        long hours = breakDuration / 3600;
        long minutes = (breakDuration % 3600) / 60;
        long seconds = breakDuration % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
