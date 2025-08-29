package com.global.hr.Controller;

import com.global.hr.DTO.BreakRecordDto;
import com.global.hr.DTO.TimerSessionDto;
import com.global.hr.Entity.BreakRecord;
import com.global.hr.Service.TimerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/timer")
@CrossOrigin(origins = "*")
public class TimerController {
    
    @Autowired
    private TimerService timerService;
    
    /**
     * Get timer dashboard for an event (Admin only)
     */
    @GetMapping("/dashboard/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getEventTimerDashboard(@PathVariable Long eventId) {
        try {
            TimerService.EventTimerDashboard dashboard = timerService.getEventTimerDashboard(eventId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch dashboard: " + e.getMessage()));
        }
    }
    
    /**
     * Stop timer for specific user (Admin only)
     */
    @PostMapping("/stop-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> stopUserTimer(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long eventId = Long.valueOf(request.get("eventId").toString());
            String adminName = (String) request.get("adminName");
            
            if (adminName == null || adminName.trim().isEmpty()) {
                adminName = "Admin";
            }
            
            TimerSessionDto session = timerService.stopTimerSession(userId, eventId, adminName);
            
            return ResponseEntity.ok(Map.of(
                "message", "Timer stopped successfully for user",
                "session", session
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Stop timer for all users in an event (Admin only)
     */
    @PostMapping("/stop-all/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> stopAllTimers(@PathVariable Long eventId, 
                                          @RequestBody Map<String, String> request) {
        try {
            String adminName = request.get("adminName");
            if (adminName == null || adminName.trim().isEmpty()) {
                adminName = "Admin";
            }
            
            List<TimerSessionDto> sessions = timerService.stopAllTimerSessions(eventId, adminName);
            
            return ResponseEntity.ok(Map.of(
                "message", "All timers stopped successfully",
                "stoppedSessions", sessions.size(),
                "sessions", sessions
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Start break for specific user (Admin only)
     */
    @PostMapping("/break/start-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> startUserBreak(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long eventId = Long.valueOf(request.get("eventId").toString());
            String breakTypeStr = (String) request.get("breakType");
            String reason = (String) request.get("reason");
            String adminName = (String) request.get("adminName");
            
            BreakRecord.BreakType breakType = BreakRecord.BreakType.valueOf(
                breakTypeStr != null ? breakTypeStr : "MANUAL_INDIVIDUAL"
            );
            
            if (adminName == null || adminName.trim().isEmpty()) {
                adminName = "Admin";
            }
            
            BreakRecordDto breakRecord = timerService.startBreak(userId, eventId, breakType, reason, adminName);
            
            return ResponseEntity.ok(Map.of(
                "message", "Break started successfully for user",
                "breakRecord", breakRecord
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Start break for all users in an event (Admin only)
     */
    @PostMapping("/break/start-all/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> startBreakForAll(@PathVariable Long eventId,
                                             @RequestBody Map<String, String> request) {
        try {
            String breakTypeStr = request.get("breakType");
            String reason = request.get("reason");
            String adminName = request.get("adminName");
            
            BreakRecord.BreakType breakType = BreakRecord.BreakType.valueOf(
                breakTypeStr != null ? breakTypeStr : "MANUAL_ALL"
            );
            
            if (adminName == null || adminName.trim().isEmpty()) {
                adminName = "Admin";
            }
            
            List<BreakRecordDto> breakRecords = timerService.startBreakForAll(eventId, breakType, reason, adminName);
            
            return ResponseEntity.ok(Map.of(
                "message", "Break started for all users",
                "affectedUsers", breakRecords.size(),
                "breakRecords", breakRecords
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * End break for specific user (Admin only)
     */
    @PostMapping("/break/end-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> endUserBreak(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long eventId = Long.valueOf(request.get("eventId").toString());
            String adminName = (String) request.get("adminName");
            
            if (adminName == null || adminName.trim().isEmpty()) {
                adminName = "Admin";
            }
            
            BreakRecordDto breakRecord = timerService.endBreak(userId, eventId, adminName);
            
            return ResponseEntity.ok(Map.of(
                "message", "Break ended successfully for user",
                "breakRecord", breakRecord
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * End break for all users in an event (Admin only)
     */
    @PostMapping("/break/end-all/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> endBreakForAll(@PathVariable Long eventId,
                                           @RequestBody Map<String, String> request) {
        try {
            String adminName = request.get("adminName");
            if (adminName == null || adminName.trim().isEmpty()) {
                adminName = "Admin";
            }
            
            List<BreakRecordDto> breakRecords = timerService.endBreakForAll(eventId, adminName);
            
            return ResponseEntity.ok(Map.of(
                "message", "Break ended for all users",
                "affectedUsers", breakRecords.size(),
                "breakRecords", breakRecords
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get timer session for specific user (User can see their own, Admin can see any)
     */
    @GetMapping("/session/{userId}/{eventId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserTimerSession(@PathVariable Long userId, @PathVariable Long eventId) {
        try {
            TimerSessionDto session = timerService.getUserTimerSession(userId, eventId);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Search users and their timer status (Admin only)
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchUserTimerSessions(@RequestParam String searchTerm,
                                                    @RequestParam(required = false) Long eventId) {
        try {
            List<TimerSessionDto> sessions = timerService.searchUserTimerSessions(searchTerm, eventId);
            
            return ResponseEntity.ok(Map.of(
                "searchTerm", searchTerm,
                "eventId", eventId,
                "results", sessions.size(),
                "sessions", sessions
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Search failed: " + e.getMessage()));
        }
    }
    
    /**
     * Get available break types
     */
    @GetMapping("/break-types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBreakTypes() {
        return ResponseEntity.ok(Map.of(
            "breakTypes", BreakRecord.BreakType.values(),
            "descriptions", Map.of(
                "MANUAL_INDIVIDUAL", "Manual break for specific user",
                "MANUAL_ALL", "Manual break for all users",
                "AUTOMATIC", "System automatic break",
                "LUNCH_BREAK", "Scheduled lunch break",
                "COFFEE_BREAK", "Scheduled coffee break",
                "EMERGENCY", "Emergency break"
            )
        ));
    }
}
