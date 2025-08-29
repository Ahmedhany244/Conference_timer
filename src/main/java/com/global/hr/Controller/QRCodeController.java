package com.global.hr.Controller;

import com.global.hr.DTO.AttendanceDto;
import com.global.hr.DTO.QRCodeRequest;
import com.global.hr.DTO.QRCodeResponse;
import com.global.hr.Service.AttendanceService;
import com.global.hr.Service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qr")
@CrossOrigin(origins = "*")
public class QRCodeController {
    
    @Autowired
    private QRCodeService qrCodeService;
    
    @Autowired
    private AttendanceService attendanceService;
    
    /**
     * Generates a QR code for event attendance
     * Users can generate QR codes for events they want to attend
     */
    @PostMapping("/generate")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> generateQRCode(@RequestBody QRCodeRequest request) {
        try {
            // Validate input
            if (request.getUserId() == null || request.getEventId() == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "User ID and Event ID are required"));
            }
            
            // Check if user can generate QR for this event
            if (!qrCodeService.canGenerateQRCode(request.getUserId(), request.getEventId())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Cannot generate QR code for this event"));
            }
            
            QRCodeResponse response = qrCodeService.generateQRCode(request);
            return ResponseEntity.ok(response);
            
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to generate QR code: " + e.getMessage()));
        }
    }
    
    /**
     * Scans a QR code and marks attendance
     * Staff members use this endpoint to process QR code scans
     */
    @PostMapping("/scan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> scanQRCode(@RequestBody Map<String, String> request) {
        try {
            String qrToken = request.get("qrToken");
            String staffMember = request.get("staffMember");
            
            // Validate input
            if (qrToken == null || qrToken.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "QR token is required"));
            }
            
            if (staffMember == null || staffMember.trim().isEmpty()) {
                staffMember = "Unknown Staff";
            }
            
            AttendanceDto attendance = attendanceService.processQRCodeScan(qrToken, staffMember);
            
            return ResponseEntity.ok(Map.of(
                "message", "Attendance recorded successfully",
                "attendance", attendance
            ));
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to process QR code: " + e.getMessage()));
        }
    }
    
    /**
     * Gets attendance list for a specific event
     * Admins can view who attended an event
     */
    @GetMapping("/attendance/event/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getEventAttendances(@PathVariable Long eventId) {
        try {
            List<AttendanceDto> attendances = attendanceService.getEventAttendances(eventId);
            Long totalCount = attendanceService.getEventAttendanceCount(eventId);
            
            return ResponseEntity.ok(Map.of(
                "attendances", attendances,
                "totalCount", totalCount
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch attendances: " + e.getMessage()));
        }
    }
    
    /**
     * Gets attendance history for a specific user
     * Users can view their own attendance history
     */
    @GetMapping("/attendance/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserAttendances(@PathVariable Long userId) {
        try {
            List<AttendanceDto> attendances = attendanceService.getUserAttendances(userId);
            
            return ResponseEntity.ok(Map.of(
                "attendances", attendances,
                "totalCount", attendances.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch user attendances: " + e.getMessage()));
        }
    }
    
    /**
     * Checks if a user has attended a specific event
     */
    @GetMapping("/attendance/check/{userId}/{eventId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> checkUserAttendance(@PathVariable Long userId, @PathVariable Long eventId) {
        try {
            boolean hasAttended = attendanceService.hasUserAttended(userId, eventId);
            
            return ResponseEntity.ok(Map.of(
                "userId", userId,
                "eventId", eventId,
                "hasAttended", hasAttended
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to check attendance: " + e.getMessage()));
        }
    }
    
    /**
     * Gets attendance count for an event
     */
    @GetMapping("/attendance/count/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getEventAttendanceCount(@PathVariable Long eventId) {
        try {
            Long count = attendanceService.getEventAttendanceCount(eventId);
            
            return ResponseEntity.ok(Map.of(
                "eventId", eventId,
                "attendanceCount", count
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get attendance count: " + e.getMessage()));
        }
    }
}
