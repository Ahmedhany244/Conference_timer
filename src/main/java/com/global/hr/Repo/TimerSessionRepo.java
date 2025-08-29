package com.global.hr.Repo;

import com.global.hr.Entity.TimerSession;
import com.global.hr.Entity.Event;
import com.global.hr.Entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimerSessionRepo extends JpaRepository<TimerSession, Long> {
    
    // Find timer session by attendance
    Optional<TimerSession> findByAttendance(Attendance attendance);
    
    // Find all active timer sessions for an event
    List<TimerSession> findByEventAndIsActiveTrue(Event event);
    
    // Find all timer sessions for an event
    List<TimerSession> findByEvent(Event event);
    
    // Find all timer sessions currently on break for an event
    List<TimerSession> findByEventAndIsOnBreakTrue(Event event);
    
    // Count active sessions for an event
    @Query("SELECT COUNT(ts) FROM TimerSession ts WHERE ts.event = :event AND ts.isActive = true")
    Long countActiveSessionsByEvent(@Param("event") Event event);
    
    // Count sessions on break for an event
    @Query("SELECT COUNT(ts) FROM TimerSession ts WHERE ts.event = :event AND ts.isOnBreak = true")
    Long countBreakSessionsByEvent(@Param("event") Event event);
    
    // Get total active time for all attendees of an event
    @Query("SELECT SUM(ts.totalActiveTime) FROM TimerSession ts WHERE ts.event = :event")
    Long getTotalActiveTimeByEvent(@Param("event") Event event);
    
    // Find sessions by user ID through attendance
    @Query("SELECT ts FROM TimerSession ts WHERE ts.attendance.user.id = :userId")
    List<TimerSession> findByUserId(@Param("userId") Long userId);
    
    // Find active session for a specific user and event
    @Query("SELECT ts FROM TimerSession ts WHERE ts.attendance.user.id = :userId AND ts.event.id = :eventId AND ts.isActive = true")
    Optional<TimerSession> findActiveSessionByUserAndEvent(@Param("userId") Long userId, @Param("eventId") Long eventId);
    
    // Find timer sessions by user and event IDs (for testing)
    @Query("SELECT ts FROM TimerSession ts WHERE ts.attendance.user.id = :userId AND ts.attendance.event.id = :eventId")
    List<TimerSession> findByAttendance_User_IdAndAttendance_Event_Id(@Param("userId") Long userId, @Param("eventId") Long eventId);
}
