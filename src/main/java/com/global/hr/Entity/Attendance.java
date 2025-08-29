package com.global.hr.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;
    
    @Column(name = "qr_token_used")
    private String qrTokenUsed; // Store the JWT ID to prevent reuse
    
    @Column(name = "checked_in_by")
    private String checkedInBy; // Staff member who scanned the QR
    
    // Constructors
    public Attendance() {}
    
    public Attendance(User user, Event event, LocalDateTime checkInTime, String qrTokenUsed, String checkedInBy) {
        this.user = user;
        this.event = event;
        this.checkInTime = checkInTime;
        this.qrTokenUsed = qrTokenUsed;
        this.checkedInBy = checkedInBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
    
    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
    
    public String getQrTokenUsed() {
        return qrTokenUsed;
    }
    
    public void setQrTokenUsed(String qrTokenUsed) {
        this.qrTokenUsed = qrTokenUsed;
    }
    
    public String getCheckedInBy() {
        return checkedInBy;
    }
    
    public void setCheckedInBy(String checkedInBy) {
        this.checkedInBy = checkedInBy;
    }
}
