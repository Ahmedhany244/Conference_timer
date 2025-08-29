package com.global.hr.Service;

import com.global.hr.DTO.QRCodeRequest;
import com.global.hr.DTO.QRCodeResponse;
import com.global.hr.Entity.Event;
import com.global.hr.Entity.User;
import com.global.hr.Repo.EventRepo;
import com.global.hr.Repo.UserRepo;
import com.global.hr.exception.ResourceNotFoundException;
import com.global.hr.security.JwtUtils;
import com.global.hr.util.QRCodeGenerator;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeService {
    
    @Autowired
    private QRCodeGenerator qrCodeGenerator;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private EventRepo eventRepo;
    
    // QR code expiry time in hours
    private static final int QR_EXPIRY_HOURS = 4;
    
    /**
     * Generates a QR code for event attendance
     * 
     * @param request Contains userId and eventId
     * @return QRCodeResponse with Base64 image and token details
     */
    public QRCodeResponse generateQRCode(QRCodeRequest request) {
        try {
            // Validate user and event exist
            User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
            
            Event event = eventRepo.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + request.getEventId()));
            
            // Check if event is still active/upcoming
            LocalDateTime now = LocalDateTime.now();
            if (event.getEventEndTime().isBefore(now)) {
                throw new IllegalStateException("Cannot generate QR code for past events");
            }
            
            // Create JWT token with custom claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("eventId", event.getId());
            claims.put("userName", user.getName());
            claims.put("eventName", event.getEventName());
            claims.put("purpose", "attendance");
            claims.put("generatedAt", now.toString());
            
            // Generate JWT token with expiry
            String jwtToken = jwtUtils.generateQRToken(user.getEmail(), claims, QR_EXPIRY_HOURS);
            
            // Generate QR code image from JWT
            String qrCodeBase64 = qrCodeGenerator.generateQRCodeBase64(jwtToken);
            
            // Calculate expiry time
            LocalDateTime expiryTime = now.plusHours(QR_EXPIRY_HOURS);
            String formattedExpiry = expiryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            return new QRCodeResponse(
                qrCodeBase64,
                jwtToken,
                formattedExpiry,
                event.getEventName(),
                user.getName()
            );
            
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates if a user can generate QR code for an event
     */
    public boolean canGenerateQRCode(Long userId, Long eventId) {
        // Check if user and event exist
        boolean userExists = userRepo.existsById(userId);
        boolean eventExists = eventRepo.existsById(eventId);
        
        if (!userExists || !eventExists) {
            return false;
        }
        
        // Check if event is still active
        Event event = eventRepo.findById(eventId).get();
        return event.getEventEndTime().isAfter(LocalDateTime.now());
    }
}
