package com.global.hr.Service;

import com.global.hr.DTO.AttendanceDto;
import com.global.hr.Entity.Attendance;
import com.global.hr.Entity.Event;
import com.global.hr.Entity.User;
import com.global.hr.Repo.AttendanceRepo;
import com.global.hr.Repo.EventRepo;
import com.global.hr.Repo.UserRepo;
import com.global.hr.exception.ResourceNotFoundException;
import com.global.hr.security.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceService {
    
    @Autowired
    private AttendanceRepo attendanceRepo;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private EventRepo eventRepo;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private TimerService timerService;
    
    /**
     * Processes QR code scan and marks attendance
     * 
     * @param qrToken The JWT token from the QR code
     * @param staffMember The staff member who scanned the QR
     * @return AttendanceDto with the recorded attendance
     */
    public AttendanceDto processQRCodeScan(String qrToken, String staffMember) {
        // Validate JWT token
        if (!jwtUtils.validateJwtToken(qrToken)) {
            throw new IllegalArgumentException("Invalid or expired QR code");
        }
        
        // Check if token is expired
        if (jwtUtils.isTokenExpired(qrToken)) {
            throw new IllegalArgumentException("QR code has expired");
        }
        
        // Extract claims from token
        Claims claims = jwtUtils.getAllClaimsFromToken(qrToken);
        
        // Validate token purpose
        String purpose = (String) claims.get("purpose");
        if (!"attendance".equals(purpose)) {
            throw new IllegalArgumentException("Invalid QR code purpose");
        }
        
        // Extract user and event information
        Long userId = Long.valueOf(claims.get("userId").toString());
        Long eventId = Long.valueOf(claims.get("eventId").toString());
        String tokenId = jwtUtils.getTokenId(qrToken);
        
        // Check if this QR code was already used
        if (attendanceRepo.existsByQrTokenUsed(tokenId)) {
            throw new IllegalStateException("QR code has already been used");
        }
        
        // Fetch user and event
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        // Check if user already checked in for this event
        if (attendanceRepo.existsByUserAndEvent(user, event)) {
            throw new IllegalStateException("User has already checked in for this event");
        }
        
        // Verify event timing (optional - you might want to allow early/late check-ins)
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(event.getEventEndTime())) {
            throw new IllegalStateException("Event has already ended");
        }
        
        // Create attendance record
        Attendance attendance = new Attendance(
            user,
            event,
            now,
            tokenId,
            staffMember
        );
        
        Attendance savedAttendance = attendanceRepo.save(attendance);
        
        // Automatically start timer session when user checks in
        try {
            timerService.startTimerSession(savedAttendance.getId(), staffMember);
        } catch (Exception e) {
            // Log the error but don't fail the attendance process
            System.err.println("Failed to start timer session: " + e.getMessage());
        }
        
        // Convert to DTO and return
        return convertToDto(savedAttendance);
    }
    
    /**
     * Gets all attendances for a specific event
     */
    public List<AttendanceDto> getEventAttendances(Long eventId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        List<Attendance> attendances = attendanceRepo.findByEvent(event);
        return attendances.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all attendances for a specific user
     */
    public List<AttendanceDto> getUserAttendances(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<Attendance> attendances = attendanceRepo.findByUser(user);
        return attendances.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets attendance count for an event
     */
    public Long getEventAttendanceCount(Long eventId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        return attendanceRepo.countAttendeesByEvent(event);
    }
    
    /**
     * Checks if a user has attended a specific event
     */
    public boolean hasUserAttended(Long userId, Long eventId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        return attendanceRepo.existsByUserAndEvent(user, event);
    }
    
    /**
     * Converts Attendance entity to DTO
     */
    private AttendanceDto convertToDto(Attendance attendance) {
        return new AttendanceDto(
            attendance.getId(),
            attendance.getUser().getId(),
            attendance.getUser().getName(),
            attendance.getEvent().getId(),
            attendance.getEvent().getEventName(),
            attendance.getCheckInTime(),
            attendance.getCheckedInBy()
        );
    }
}
