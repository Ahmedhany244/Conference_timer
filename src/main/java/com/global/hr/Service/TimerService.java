package com.global.hr.Service;

import com.global.hr.DTO.BreakRecordDto;
import com.global.hr.DTO.TimerSessionDto;
import com.global.hr.Entity.*;
import com.global.hr.Repo.*;
import com.global.hr.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TimerService {
    
    @Autowired
    private TimerSessionRepo timerSessionRepo;
    
    @Autowired
    private BreakRecordRepo breakRecordRepo;
    
    @Autowired
    private AttendanceRepo attendanceRepo;
    
    @Autowired
    private EventRepo eventRepo;
    
    @Autowired
    private UserRepo userRepo;
    
    /**
     * Start timer session when user checks in via QR code
     */
    public TimerSessionDto startTimerSession(Long attendanceId, String adminName) {
        Attendance attendance = attendanceRepo.findById(attendanceId)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + attendanceId));
        
        // Check if timer session already exists
        Optional<TimerSession> existingSession = timerSessionRepo.findByAttendance(attendance);
        if (existingSession.isPresent()) {
            TimerSession session = existingSession.get();
            if (session.getIsActive()) {
                throw new IllegalStateException("Timer session is already active for this user");
            }
            // Reactivate existing session
            session.setIsActive(true);
            session.setLastActivityTime(LocalDateTime.now());
            TimerSession saved = timerSessionRepo.save(session);
            return convertToDto(saved);
        }
        
        // Create new timer session
        TimerSession timerSession = new TimerSession(attendance.getEvent(), attendance, adminName);
        TimerSession saved = timerSessionRepo.save(timerSession);
        
        return convertToDto(saved);
    }
    
    /**
     * Stop timer session for specific user
     */
    public TimerSessionDto stopTimerSession(Long userId, Long eventId, String adminName) {
        TimerSession session = timerSessionRepo.findActiveSessionByUserAndEvent(userId, eventId)
            .orElseThrow(() -> new ResourceNotFoundException("No active timer session found for user"));
        
        // End any active break first (safe method)
        safeEndActiveBreak(session, adminName);
        
        // Calculate final active time
        updateActiveTime(session);
        
        // Stop the session
        session.setIsActive(false);
        session.setSessionEndTime(LocalDateTime.now());
        session.setLastActivityTime(LocalDateTime.now());
        
        TimerSession saved = timerSessionRepo.save(session);
        return convertToDto(saved);
    }
    
    /**
     * Stop all timer sessions for an event
     */
    public List<TimerSessionDto> stopAllTimerSessions(Long eventId, String adminName) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        List<TimerSession> activeSessions = timerSessionRepo.findByEventAndIsActiveTrue(event);
        
        return activeSessions.stream()
            .map(session -> {
                // End any active break (safe method)
                safeEndActiveBreak(session, adminName);
                
                // Calculate final active time
                updateActiveTime(session);
                
                // Stop the session
                session.setIsActive(false);
                session.setSessionEndTime(LocalDateTime.now());
                session.setLastActivityTime(LocalDateTime.now());
                
                return timerSessionRepo.save(session);
            })
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Start break for specific user
     */
    public BreakRecordDto startBreak(Long userId, Long eventId, BreakRecord.BreakType breakType, 
                                   String reason, String adminName) {
        TimerSession session = timerSessionRepo.findActiveSessionByUserAndEvent(userId, eventId)
            .orElseThrow(() -> new ResourceNotFoundException("No active timer session found for user"));
        
        // Check if user is already on break
        if (session.getIsOnBreak()) {
            throw new IllegalStateException("User is already on break");
        }
        
        // Update active time before starting break
        updateActiveTime(session);
        
        // Create break record
        BreakRecord breakRecord = new BreakRecord(session, breakType, reason, adminName);
        BreakRecord saved = breakRecordRepo.save(breakRecord);
        
        // Update session status
        session.setIsOnBreak(true);
        session.setLastActivityTime(LocalDateTime.now());
        timerSessionRepo.save(session);
        
        return convertToDto(saved);
    }
    
    /**
     * Start break for all users in an event
     */
    public List<BreakRecordDto> startBreakForAll(Long eventId, BreakRecord.BreakType breakType, 
                                                String reason, String adminName) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        List<TimerSession> activeSessions = timerSessionRepo.findByEventAndIsActiveTrue(event);
        
        return activeSessions.stream()
            .filter(session -> !session.getIsOnBreak()) // Only start break for users not already on break
            .map(session -> {
                // Update active time before starting break
                updateActiveTime(session);
                
                // Create break record
                BreakRecord breakRecord = new BreakRecord(session, breakType, reason, adminName);
                BreakRecord saved = breakRecordRepo.save(breakRecord);
                
                // Update session status
                session.setIsOnBreak(true);
                session.setLastActivityTime(LocalDateTime.now());
                timerSessionRepo.save(session);
                
                return saved;
            })
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * End break for specific user
     */
    public BreakRecordDto endBreak(Long userId, Long eventId, String adminName) {
        TimerSession session = timerSessionRepo.findActiveSessionByUserAndEvent(userId, eventId)
            .orElseThrow(() -> new ResourceNotFoundException("No active timer session found for user"));
        
        return endActiveBreak(session, adminName);
    }
    
    /**
     * End breaks for all users in an event
     */
    public List<BreakRecordDto> endBreakForAll(Long eventId, String adminName) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        List<TimerSession> sessionsOnBreak = timerSessionRepo.findByEventAndIsOnBreakTrue(event);
        
        return sessionsOnBreak.stream()
            .map(session -> endActiveBreak(session, adminName))
            .collect(Collectors.toList());
    }
    
    /**
     * Get timer dashboard data for an event
     */
    public EventTimerDashboard getEventTimerDashboard(Long eventId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        List<TimerSession> allSessions = timerSessionRepo.findByEvent(event);
        List<TimerSession> activeSessions = timerSessionRepo.findByEventAndIsActiveTrue(event);
        List<TimerSession> breakSessions = timerSessionRepo.findByEventAndIsOnBreakTrue(event);
        
        Long totalActiveTime = timerSessionRepo.getTotalActiveTimeByEvent(event);
        
        return new EventTimerDashboard(
            event.getId(),
            event.getEventName(),
            allSessions.size(),
            activeSessions.size(),
            breakSessions.size(),
            totalActiveTime != null ? totalActiveTime : 0L,
            allSessions.stream().map(this::convertToDto).collect(Collectors.toList())
        );
    }
    
    /**
     * Get timer session for specific user and event
     */
    public TimerSessionDto getUserTimerSession(Long userId, Long eventId) {
        Optional<TimerSession> session = timerSessionRepo.findActiveSessionByUserAndEvent(userId, eventId);
        if (session.isPresent()) {
            // Update active time before returning
            updateActiveTime(session.get());
            timerSessionRepo.save(session.get());
            return convertToDto(session.get());
        }
        throw new ResourceNotFoundException("No timer session found for user and event");
    }
    
    /**
     * Search users and their timer status
     */
    public List<TimerSessionDto> searchUserTimerSessions(String searchTerm, Long eventId) {
        // This is a simplified search - you might want to implement more sophisticated search
        List<TimerSession> sessions;
        
        if (eventId != null) {
            Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
            sessions = timerSessionRepo.findByEvent(event);
        } else {
            sessions = timerSessionRepo.findAll();
        }
        
        return sessions.stream()
            .filter(session -> session.getAttendance().getUser().getName().toLowerCase()
                .contains(searchTerm.toLowerCase()) ||
                session.getAttendance().getUser().getEmail().toLowerCase()
                .contains(searchTerm.toLowerCase()))
            .map(session -> {
                updateActiveTime(session);
                timerSessionRepo.save(session);
                return convertToDto(session);
            })
            .collect(Collectors.toList());
    }
    
    // Helper methods
    private BreakRecordDto endActiveBreak(TimerSession session, String adminName) {
        Optional<BreakRecord> activeBreak = breakRecordRepo.findByTimerSessionAndBreakEndTimeIsNull(session);
        
        if (activeBreak.isPresent()) {
            BreakRecord breakRecord = activeBreak.get();
            LocalDateTime now = LocalDateTime.now();
            breakRecord.setBreakEndTime(now);
            breakRecord.setEndedBy(adminName);
            
            // Calculate break duration
            long duration = Duration.between(breakRecord.getBreakStartTime(), now).getSeconds();
            breakRecord.setBreakDuration(duration);
            
            // Update session total break time
            session.setTotalBreakTime(session.getTotalBreakTime() + duration);
            session.setIsOnBreak(false);
            session.setLastActivityTime(now);
            
            BreakRecord saved = breakRecordRepo.save(breakRecord);
            timerSessionRepo.save(session);
            
            return convertToDto(saved);
        }
        
        throw new IllegalStateException("No active break found for user");
    }
    
    /**
     * Safely end active break - doesn't throw exception if no break found
     */
    private BreakRecordDto safeEndActiveBreak(TimerSession session, String adminName) {
        try {
            return endActiveBreak(session, adminName);
        } catch (IllegalStateException e) {
            // No active break found, just ensure session is not marked as on break
            if (session.getIsOnBreak()) {
                session.setIsOnBreak(false);
                session.setLastActivityTime(LocalDateTime.now());
                timerSessionRepo.save(session);
            }
            return null;
        }
    }
    
    private void updateActiveTime(TimerSession session) {
        if (!session.getIsActive() || session.getIsOnBreak()) {
            return;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastActivity = session.getLastActivityTime();
        
        if (lastActivity != null) {
            long additionalTime = Duration.between(lastActivity, now).getSeconds();
            session.setTotalActiveTime(session.getTotalActiveTime() + additionalTime);
        }
        
        session.setLastActivityTime(now);
    }
    
    private TimerSessionDto convertToDto(TimerSession session) {
        List<BreakRecordDto> breakDtos = breakRecordRepo.findByTimerSessionOrderByBreakStartTimeDesc(session)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        TimerSessionDto dto = new TimerSessionDto(
            session.getId(),
            session.getEvent().getId(),
            session.getEvent().getEventName(),
            session.getAttendance().getUser().getId(),
            session.getAttendance().getUser().getName(),
            session.getSessionStartTime(),
            session.getSessionEndTime(),
            session.getTotalActiveTime(),
            session.getTotalBreakTime(),
            session.getIsActive(),
            session.getIsOnBreak(),
            session.getLastActivityTime(),
            session.getCreatedBy()
        );
        
        dto.setBreakRecords(breakDtos);
        return dto;
    }
    
    private BreakRecordDto convertToDto(BreakRecord breakRecord) {
        return new BreakRecordDto(
            breakRecord.getId(),
            breakRecord.getTimerSession().getId(),
            breakRecord.getBreakStartTime(),
            breakRecord.getBreakEndTime(),
            breakRecord.getBreakDuration(),
            breakRecord.getBreakType(),
            breakRecord.getBreakReason(),
            breakRecord.getInitiatedBy(),
            breakRecord.getEndedBy()
        );
    }
    
    // Inner class for dashboard data
    public static class EventTimerDashboard {
        private Long eventId;
        private String eventName;
        private int totalAttendees;
        private int activeAttendees;
        private int attendeesOnBreak;
        private Long totalActiveTime;
        private List<TimerSessionDto> timerSessions;
        
        public EventTimerDashboard(Long eventId, String eventName, int totalAttendees, 
                                 int activeAttendees, int attendeesOnBreak, Long totalActiveTime,
                                 List<TimerSessionDto> timerSessions) {
            this.eventId = eventId;
            this.eventName = eventName;
            this.totalAttendees = totalAttendees;
            this.activeAttendees = activeAttendees;
            this.attendeesOnBreak = attendeesOnBreak;
            this.totalActiveTime = totalActiveTime;
            this.timerSessions = timerSessions;
        }
        
        // Getters and setters
        public Long getEventId() { return eventId; }
        public void setEventId(Long eventId) { this.eventId = eventId; }
        
        public String getEventName() { return eventName; }
        public void setEventName(String eventName) { this.eventName = eventName; }
        
        public int getTotalAttendees() { return totalAttendees; }
        public void setTotalAttendees(int totalAttendees) { this.totalAttendees = totalAttendees; }
        
        public int getActiveAttendees() { return activeAttendees; }
        public void setActiveAttendees(int activeAttendees) { this.activeAttendees = activeAttendees; }
        
        public int getAttendeesOnBreak() { return attendeesOnBreak; }
        public void setAttendeesOnBreak(int attendeesOnBreak) { this.attendeesOnBreak = attendeesOnBreak; }
        
        public Long getTotalActiveTime() { return totalActiveTime; }
        public void setTotalActiveTime(Long totalActiveTime) { this.totalActiveTime = totalActiveTime; }
        
        public List<TimerSessionDto> getTimerSessions() { return timerSessions; }
        public void setTimerSessions(List<TimerSessionDto> timerSessions) { this.timerSessions = timerSessions; }
    }
}
