package com.global.hr.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "break_records")
public class BreakRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timer_session_id", nullable = false)
    private TimerSession timerSession;
    
    @Column(name = "break_start_time", nullable = false)
    private LocalDateTime breakStartTime;
    
    @Column(name = "break_end_time")
    private LocalDateTime breakEndTime;
    
    @Column(name = "break_duration") // in seconds
    private Long breakDuration;
    
    @Column(name = "break_type")
    @Enumerated(EnumType.STRING)
    private BreakType breakType;
    
    @Column(name = "break_reason")
    private String breakReason;
    
    @Column(name = "initiated_by")
    private String initiatedBy; // admin who started the break
    
    @Column(name = "ended_by")
    private String endedBy; // admin who ended the break
    
    // Constructors
    public BreakRecord() {}
    
    public BreakRecord(TimerSession timerSession, BreakType breakType, String breakReason, String initiatedBy) {
        this.timerSession = timerSession;
        this.breakType = breakType;
        this.breakReason = breakReason;
        this.initiatedBy = initiatedBy;
        this.breakStartTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public TimerSession getTimerSession() {
        return timerSession;
    }
    
    public void setTimerSession(TimerSession timerSession) {
        this.timerSession = timerSession;
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
    
    public BreakType getBreakType() {
        return breakType;
    }
    
    public void setBreakType(BreakType breakType) {
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
    
    // Enum for break types
    public enum BreakType {
        MANUAL_INDIVIDUAL,  // Admin manually put specific user on break
        MANUAL_ALL,         // Admin put all users on break
        AUTOMATIC,          // System automatic break
        LUNCH_BREAK,        // Scheduled lunch break
        COFFEE_BREAK,       // Scheduled coffee break
        EMERGENCY           // Emergency break
    }
}
