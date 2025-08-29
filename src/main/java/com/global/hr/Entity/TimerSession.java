package com.global.hr.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "timer_sessions")
public class TimerSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendee_id", nullable = false)
    private Attendance attendance;
    
    @Column(name = "session_start_time")
    private LocalDateTime sessionStartTime;
    
    @Column(name = "session_end_time")
    private LocalDateTime sessionEndTime;
    
    @Column(name = "total_active_time") // in seconds
    private Long totalActiveTime = 0L;
    
    @Column(name = "total_break_time") // in seconds
    private Long totalBreakTime = 0L;
    
    @Column(name = "is_active")
    private Boolean isActive = false;
    
    @Column(name = "is_on_break")
    private Boolean isOnBreak = false;
    
    @Column(name = "last_activity_time")
    private LocalDateTime lastActivityTime;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @OneToMany(mappedBy = "timerSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BreakRecord> breakRecords;
    
    // Constructors
    public TimerSession() {}
    
    public TimerSession(Event event, Attendance attendance, String createdBy) {
        this.event = event;
        this.attendance = attendance;
        this.createdBy = createdBy;
        this.sessionStartTime = LocalDateTime.now();
        this.lastActivityTime = LocalDateTime.now();
        this.isActive = true;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public Attendance getAttendance() {
        return attendance;
    }
    
    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
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
    
    public List<BreakRecord> getBreakRecords() {
        return breakRecords;
    }
    
    public void setBreakRecords(List<BreakRecord> breakRecords) {
        this.breakRecords = breakRecords;
    }
}
