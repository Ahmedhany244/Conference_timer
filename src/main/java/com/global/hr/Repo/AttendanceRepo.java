package com.global.hr.Repo;

import com.global.hr.Entity.Attendance;
import com.global.hr.Entity.Event;
import com.global.hr.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, Long> {
    
    // Check if user already checked in for this event
    boolean existsByUserAndEvent(User user, Event event);
    
    // Check if QR token was already used (prevent reuse)
    boolean existsByQrTokenUsed(String qrToken);
    
    // Get all attendances for a specific event
    List<Attendance> findByEvent(Event event);
    
    // Get all attendances for a specific user
    List<Attendance> findByUser(User user);
    
    // Get attendance record for specific user and event
    Optional<Attendance> findByUserAndEvent(User user, Event event);
    
    // Count attendees for an event
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.event = :event")
    Long countAttendeesByEvent(@Param("event") Event event);
    
    // Delete by user and event IDs (for testing)
    @Modifying
    @Transactional
    void deleteByUser_IdAndEvent_Id(Long userId, Long eventId);
}
