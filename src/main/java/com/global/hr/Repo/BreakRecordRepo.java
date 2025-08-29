package com.global.hr.Repo;

import com.global.hr.Entity.BreakRecord;
import com.global.hr.Entity.TimerSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BreakRecordRepo extends JpaRepository<BreakRecord, Long> {
    
    // Find all break records for a timer session
    List<BreakRecord> findByTimerSessionOrderByBreakStartTimeDesc(TimerSession timerSession);
    
    // Find active break (not ended yet) for a timer session
    Optional<BreakRecord> findByTimerSessionAndBreakEndTimeIsNull(TimerSession timerSession);
    
    // Find all break records for an event
    @Query("SELECT br FROM BreakRecord br WHERE br.timerSession.event.id = :eventId ORDER BY br.breakStartTime DESC")
    List<BreakRecord> findByEventId(@Param("eventId") Long eventId);
    
    // Find all active breaks for an event
    @Query("SELECT br FROM BreakRecord br WHERE br.timerSession.event.id = :eventId AND br.breakEndTime IS NULL")
    List<BreakRecord> findActiveBreaksByEventId(@Param("eventId") Long eventId);
    
    // Get total break time for a timer session
    @Query("SELECT SUM(br.breakDuration) FROM BreakRecord br WHERE br.timerSession = :timerSession AND br.breakDuration IS NOT NULL")
    Long getTotalBreakTimeBySession(@Param("timerSession") TimerSession timerSession);
    
    // Find breaks by type for an event
    @Query("SELECT br FROM BreakRecord br WHERE br.timerSession.event.id = :eventId AND br.breakType = :breakType")
    List<BreakRecord> findByEventIdAndBreakType(@Param("eventId") Long eventId, @Param("breakType") BreakRecord.BreakType breakType);
    
    // Find breaks within time range
    @Query("SELECT br FROM BreakRecord br WHERE br.breakStartTime BETWEEN :startTime AND :endTime")
    List<BreakRecord> findByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // Delete break records by timer session (for testing)
    void deleteByTimerSession(TimerSession timerSession);
}
