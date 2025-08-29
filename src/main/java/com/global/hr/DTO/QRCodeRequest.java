package com.global.hr.DTO;

public class QRCodeRequest {
    private Long eventId;
    private Long userId;
    
    // Constructors
    public QRCodeRequest() {}
    
    public QRCodeRequest(Long eventId, Long userId) {
        this.eventId = eventId;
        this.userId = userId;
    }
    
    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
