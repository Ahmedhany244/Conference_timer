package com.global.hr.Service;

import com.global.hr.DTO.AttendeeReportDto;
import com.global.hr.DTO.BreakRecordDto;
import com.global.hr.DTO.EventReportDto;
import com.global.hr.Entity.*;
import com.global.hr.Repo.*;
import com.global.hr.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    
    @Autowired
    private EventRepo eventRepo;
    
    @Autowired
    private TimerSessionRepo timerSessionRepo;
    
    @Autowired
    private BreakRecordRepo breakRecordRepo;
    
    @Autowired
    private AttendanceRepo attendanceRepo;
    
    /**
     * Generate comprehensive event report
     */
    public EventReportDto generateEventReport(Long eventId, String reportGeneratedBy) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        // Create base report
        EventReportDto report = new EventReportDto(
            event.getId(),
            event.getEventName(),
            event.getEventStartTime(),
            event.getEventEndTime(),
            LocalDateTime.now(),
            reportGeneratedBy
        );
        
        // Get all timer sessions for this event
        List<TimerSession> timerSessions = timerSessionRepo.findByEvent(event);
        
        // Get all attendances for this event
        List<Attendance> attendances = attendanceRepo.findByEvent(event);
        
        // Calculate basic statistics
        calculateBasicStatistics(report, timerSessions, attendances);
        
        // Calculate break statistics
        calculateBreakStatistics(report, eventId);
        
        // Calculate event duration and participation rates
        calculateParticipationRates(report, event);
        
        // Generate individual attendee reports
        List<AttendeeReportDto> attendeeReports = generateAttendeeReports(timerSessions, event);
        report.setAttendeeReports(attendeeReports);
        
        return report;
    }
    
    /**
     * Generate report for specific attendee
     */
    public AttendeeReportDto generateAttendeeReport(Long userId, Long eventId) {
        TimerSession session = timerSessionRepo.findActiveSessionByUserAndEvent(userId, eventId)
            .orElseThrow(() -> new ResourceNotFoundException("No timer session found for user and event"));
        
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        return createAttendeeReport(session, event);
    }
    
    /**
     * Get summary statistics for multiple events
     */
    public List<EventReportDto> generateMultiEventSummary(List<Long> eventIds, String reportGeneratedBy) {
        return eventIds.stream()
            .map(eventId -> generateEventReport(eventId, reportGeneratedBy))
            .collect(Collectors.toList());
    }
    
    // Helper methods
    
    private void calculateBasicStatistics(EventReportDto report, List<TimerSession> timerSessions, List<Attendance> attendances) {
        // Total registered attendees (those who have attendance records)
        report.setTotalRegisteredAttendees(attendances.size());
        
        // Total checked-in attendees (those who have timer sessions)
        report.setTotalCheckedInAttendees(timerSessions.size());
        
        // Calculate total active and break times
        Long totalActiveTime = timerSessions.stream()
            .mapToLong(session -> session.getTotalActiveTime() != null ? session.getTotalActiveTime() : 0L)
            .sum();
        
        Long totalBreakTime = timerSessions.stream()
            .mapToLong(session -> session.getTotalBreakTime() != null ? session.getTotalBreakTime() : 0L)
            .sum();
        
        report.setTotalActiveTime(totalActiveTime);
        report.setTotalBreakTime(totalBreakTime);
        
        // Calculate averages
        if (timerSessions.size() > 0) {
            report.setAverageActiveTimePerAttendee(totalActiveTime / timerSessions.size());
            report.setAverageBreakTimePerAttendee(totalBreakTime / timerSessions.size());
        } else {
            report.setAverageActiveTimePerAttendee(0L);
            report.setAverageBreakTimePerAttendee(0L);
        }
        
        // Calculate attendance rate
        if (attendances.size() > 0) {
            double attendanceRate = (double) timerSessions.size() / attendances.size() * 100;
            report.setAttendanceRate(attendanceRate);
        } else {
            report.setAttendanceRate(0.0);
        }
    }
    
    private void calculateBreakStatistics(EventReportDto report, Long eventId) {
        List<BreakRecord> allBreaks = breakRecordRepo.findByEventId(eventId);
        
        report.setTotalBreaks(allBreaks.size());
        
        if (!allBreaks.isEmpty()) {
            // Find longest and shortest breaks
            Long longestBreak = allBreaks.stream()
                .filter(br -> br.getBreakDuration() != null)
                .mapToLong(BreakRecord::getBreakDuration)
                .max()
                .orElse(0L);
            
            Long shortestBreak = allBreaks.stream()
                .filter(br -> br.getBreakDuration() != null)
                .mapToLong(BreakRecord::getBreakDuration)
                .min()
                .orElse(0L);
            
            report.setLongestBreakDuration(longestBreak);
            report.setShortestBreakDuration(shortestBreak);
            
            // Find most common break type
            Map<BreakRecord.BreakType, Long> breakTypeCounts = allBreaks.stream()
                .collect(Collectors.groupingBy(
                    BreakRecord::getBreakType,
                    Collectors.counting()
                ));
            
            String mostCommonType = breakTypeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey().toString())
                .orElse("NONE");
            
            report.setMostCommonBreakType(mostCommonType);
        } else {
            report.setLongestBreakDuration(0L);
            report.setShortestBreakDuration(0L);
            report.setMostCommonBreakType("NONE");
        }
    }
    
    private void calculateParticipationRates(EventReportDto report, Event event) {
        // Calculate event duration in minutes
        Long eventDurationMinutes = Duration.between(event.getEventStartTime(), event.getEventEndTime()).toMinutes();
        report.setEventDurationMinutes(eventDurationMinutes);
        
        // Calculate average participation rate
        if (eventDurationMinutes > 0 && report.getTotalCheckedInAttendees() > 0) {
            Long eventDurationSeconds = eventDurationMinutes * 60;
            double avgParticipationRate = (double) report.getAverageActiveTimePerAttendee() / eventDurationSeconds * 100;
            report.setAverageParticipationRate(avgParticipationRate);
        } else {
            report.setAverageParticipationRate(0.0);
        }
    }
    
    private List<AttendeeReportDto> generateAttendeeReports(List<TimerSession> timerSessions, Event event) {
        return timerSessions.stream()
            .map(session -> createAttendeeReport(session, event))
            .collect(Collectors.toList());
    }
    
    private AttendeeReportDto createAttendeeReport(TimerSession session, Event event) {
        User user = session.getAttendance().getUser();
        
        // Calculate participation rate
        Long eventDurationSeconds = Duration.between(event.getEventStartTime(), event.getEventEndTime()).getSeconds();
        Double participationRate = 0.0;
        if (eventDurationSeconds > 0 && session.getTotalActiveTime() != null) {
            participationRate = (double) session.getTotalActiveTime() / eventDurationSeconds * 100;
        }
        
        // Get break records
        List<BreakRecord> breakRecords = breakRecordRepo.findByTimerSessionOrderByBreakStartTimeDesc(session);
        List<BreakRecordDto> breakDtos = breakRecords.stream()
            .map(this::convertBreakToDto)
            .collect(Collectors.toList());
        
        // Determine session completion status
        boolean completedSession = session.getSessionEndTime() != null;
        String sessionEndReason = determineSessionEndReason(session, event);
        
        AttendeeReportDto attendeeReport = new AttendeeReportDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            session.getAttendance().getCheckInTime(),
            session.getSessionStartTime(),
            session.getSessionEndTime(),
            session.getTotalActiveTime(),
            session.getTotalBreakTime(),
            breakRecords.size(),
            participationRate,
            completedSession
        );
        
        attendeeReport.setBreaks(breakDtos);
        attendeeReport.setSessionEndReason(sessionEndReason);
        
        return attendeeReport;
    }
    
    private String determineSessionEndReason(TimerSession session, Event event) {
        if (session.getSessionEndTime() == null) {
            return "ONGOING";
        }
        
        // Check if session ended close to event end time (within 10 minutes)
        LocalDateTime eventEnd = event.getEventEndTime();
        LocalDateTime sessionEnd = session.getSessionEndTime();
        
        if (Math.abs(Duration.between(sessionEnd, eventEnd).toMinutes()) <= 10) {
            return "EVENT_END";
        }
        
        return "MANUAL_STOP";
    }
    
    private BreakRecordDto convertBreakToDto(BreakRecord breakRecord) {
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
}
