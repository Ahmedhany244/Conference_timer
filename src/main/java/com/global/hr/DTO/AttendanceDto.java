package com.global.hr.DTO;

import java.time.LocalDateTime;

public class AttendanceDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long eventId;
    private String eventName;
    private LocalDateTime checkInTime;
    private String checkedInBy;
    
    // Constructors
    public AttendanceDto() {}
    
    public AttendanceDto(Long id, Long userId, String userName, Long eventId, String eventName, 
                        LocalDateTime checkInTime, String checkedInBy) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.eventId = eventId;
        this.eventName = eventName;
        this.checkInTime = checkInTime;
        this.checkedInBy = checkedInBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
    
    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
    
    public String getCheckedInBy() {
        return checkedInBy;
    }
    
    public void setCheckedInBy(String checkedInBy) {
        this.checkedInBy = checkedInBy;
    }
}
