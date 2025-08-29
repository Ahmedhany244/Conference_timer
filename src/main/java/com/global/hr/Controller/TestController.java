package com.global.hr.Controller;

import com.global.hr.Repo.AttendanceRepo;
import com.global.hr.Repo.TimerSessionRepo;
import com.global.hr.Repo.BreakRecordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @Autowired
    private AttendanceRepo attendanceRepo;
    
    @Autowired
    private TimerSessionRepo timerSessionRepo;
    
    @Autowired
    private BreakRecordRepo breakRecordRepo;
    
    /**
     * Clear all attendance records for testing (Admin only)
     * WARNING: This deletes all data - use only for testing!
     */
    @PostMapping("/clear-attendance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> clearAttendanceData() {
        try {
            // Delete in correct order due to foreign key constraints
            breakRecordRepo.deleteAll();
            timerSessionRepo.deleteAll();
            attendanceRepo.deleteAll();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "All attendance and timer data cleared successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Failed to clear data: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Clear attendance for specific user and event (Admin only)
     */
    @PostMapping("/clear-user-attendance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> clearUserAttendance(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long eventId = Long.valueOf(request.get("eventId").toString());
            
            // Find and delete timer sessions first
            timerSessionRepo.findByAttendance_User_IdAndAttendance_Event_Id(userId, eventId)
                .forEach(session -> {
                    breakRecordRepo.deleteByTimerSession(session);
                    timerSessionRepo.delete(session);
                });
            
            // Delete attendance record
            attendanceRepo.deleteByUser_IdAndEvent_Id(userId, eventId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User attendance cleared successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Failed to clear user attendance: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get testing information
     */
    @GetMapping("/info")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTestInfo() {
        long totalAttendances = attendanceRepo.count();
        long totalTimerSessions = timerSessionRepo.count();
        long totalBreakRecords = breakRecordRepo.count();
        
        return ResponseEntity.ok(Map.of(
            "totalAttendances", totalAttendances,
            "totalTimerSessions", totalTimerSessions,
            "totalBreakRecords", totalBreakRecords,
            "testUsers", Map.of(
                "user1", "test@example.com / password (ID: 1)",
                "user2", "john@example.com / password123 (ID: 2)",
                "admin", "admin@example.com / admin123"
            ),
            "testEvents", Map.of(
                "event1", "Spring Boot Conference 2025 (ID: 1)",
                "event2", "QR Code Workshop (ID: 2)",
                "event3", "Past Tech Meetup (ID: 3)"
            )
        ));
    }
}
