package com.global.hr.Controller;

import com.global.hr.DTO.AttendeeReportDto;
import com.global.hr.DTO.EventReportDto;
import com.global.hr.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportsController {
    
    @Autowired
    private ReportService reportService;
    
    /**
     * Generate comprehensive event report (Admin only)
     */
    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateEventReport(@PathVariable Long eventId,
                                               @RequestParam(defaultValue = "Admin") String reportedBy) {
        try {
            EventReportDto report = reportService.generateEventReport(eventId, reportedBy);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to generate event report: " + e.getMessage()));
        }
    }
    
    /**
     * Generate report for specific attendee
     */
    @GetMapping("/attendee/{userId}/event/{eventId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> generateAttendeeReport(@PathVariable Long userId, 
                                                   @PathVariable Long eventId) {
        try {
            AttendeeReportDto report = reportService.generateAttendeeReport(userId, eventId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to generate attendee report: " + e.getMessage()));
        }
    }
    
    /**
     * Generate summary reports for multiple events (Admin only)
     */
    @PostMapping("/multi-event")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateMultiEventReport(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> eventIdInts = (List<Integer>) request.get("eventIds");
            List<Long> eventIds = eventIdInts.stream().map(Long::valueOf).toList();
            String reportedBy = (String) request.getOrDefault("reportedBy", "Admin");
            
            List<EventReportDto> reports = reportService.generateMultiEventSummary(eventIds, reportedBy);
            
            return ResponseEntity.ok(Map.of(
                "reports", reports,
                "totalEvents", reports.size(),
                "generatedAt", java.time.LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to generate multi-event report: " + e.getMessage()));
        }
    }
    
    /**
     * Get event report summary (key metrics only)
     */
    @GetMapping("/event/{eventId}/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getEventSummary(@PathVariable Long eventId) {
        try {
            EventReportDto fullReport = reportService.generateEventReport(eventId, "System");
            
            // Return only key metrics for dashboard
            Map<String, Object> summary = Map.of(
                "eventId", fullReport.getEventId(),
                "eventName", fullReport.getEventName(),
                "totalAttendees", fullReport.getTotalCheckedInAttendees(),
                "totalActiveTime", fullReport.getFormattedTotalActiveTime(),
                "totalBreakTime", fullReport.getFormattedTotalBreakTime(),
                "attendanceRate", String.format("%.1f%%", fullReport.getAttendanceRate()),
                "participationRate", String.format("%.1f%%", fullReport.getAverageParticipationRate()),
                "totalBreaks", fullReport.getTotalBreaks()
            );
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get event summary: " + e.getMessage()));
        }
    }
    
    /**
     * Export event report as CSV data (Admin only)
     */
    @GetMapping("/event/{eventId}/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> exportEventReportCSV(@PathVariable Long eventId) {
        try {
            EventReportDto report = reportService.generateEventReport(eventId, "System");
            
            StringBuilder csv = new StringBuilder();
            
            // CSV Header
            csv.append("Event Report - ").append(report.getEventName()).append("\n");
            csv.append("Generated: ").append(report.getReportGeneratedAt()).append("\n\n");
            
            // Summary
            csv.append("SUMMARY\n");
            csv.append("Total Attendees,").append(report.getTotalCheckedInAttendees()).append("\n");
            csv.append("Total Active Time,").append(report.getFormattedTotalActiveTime()).append("\n");
            csv.append("Total Break Time,").append(report.getFormattedTotalBreakTime()).append("\n");
            csv.append("Attendance Rate,").append(String.format("%.1f%%", report.getAttendanceRate())).append("\n");
            csv.append("Participation Rate,").append(String.format("%.1f%%", report.getAverageParticipationRate())).append("\n\n");
            
            // Attendee Details Header
            csv.append("ATTENDEE DETAILS\n");
            csv.append("Name,Email,Check-in Time,Active Time,Break Time,Breaks Count,Participation Rate\n");
            
            // Attendee Data
            for (AttendeeReportDto attendee : report.getAttendeeReports()) {
                csv.append(attendee.getUserName()).append(",")
                   .append(attendee.getUserEmail()).append(",")
                   .append(attendee.getCheckInTime()).append(",")
                   .append(attendee.getFormattedActiveTime()).append(",")
                   .append(attendee.getFormattedBreakTime()).append(",")
                   .append(attendee.getNumberOfBreaks()).append(",")
                   .append(attendee.getFormattedParticipationRate()).append("\n");
            }
            
            return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=event_" + eventId + "_report.csv")
                .body(csv.toString());
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to export CSV: " + e.getMessage()));
        }
    }
    
    /**
     * Get top performers for an event (Admin only)
     */
    @GetMapping("/event/{eventId}/top-performers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTopPerformers(@PathVariable Long eventId,
                                            @RequestParam(defaultValue = "5") int limit) {
        try {
            EventReportDto report = reportService.generateEventReport(eventId, "System");
            
            // Sort attendees by participation rate (descending) and take top N
            List<AttendeeReportDto> topPerformers = report.getAttendeeReports().stream()
                .sorted((a, b) -> Double.compare(
                    b.getParticipationRate() != null ? b.getParticipationRate() : 0.0,
                    a.getParticipationRate() != null ? a.getParticipationRate() : 0.0
                ))
                .limit(limit)
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "eventId", eventId,
                "eventName", report.getEventName(),
                "topPerformers", topPerformers,
                "limit", limit
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get top performers: " + e.getMessage()));
        }
    }
    
    /**
     * Get break analysis for an event (Admin only)
     */
    @GetMapping("/event/{eventId}/break-analysis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBreakAnalysis(@PathVariable Long eventId) {
        try {
            EventReportDto report = reportService.generateEventReport(eventId, "System");
            
            Map<String, Object> analysis = Map.of(
                "eventId", eventId,
                "eventName", report.getEventName(),
                "totalBreaks", report.getTotalBreaks(),
                "longestBreakDuration", formatSeconds(report.getLongestBreakDuration()),
                "shortestBreakDuration", formatSeconds(report.getShortestBreakDuration()),
                "mostCommonBreakType", report.getMostCommonBreakType(),
                "averageBreakTime", report.getFormattedAverageBreakTime(),
                "totalBreakTime", report.getFormattedTotalBreakTime()
            );
            
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get break analysis: " + e.getMessage()));
        }
    }
    
    // Helper method
    private String formatSeconds(Long seconds) {
        if (seconds == null || seconds == 0) return "00:00:00";
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}
