package com.global.hr.security;

import com.global.hr.Repo.AttendanceRepo;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class QRTokenValidator {
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private AttendanceRepo attendanceRepo;
    
    // List of suspicious patterns that might indicate QR code abuse
    private static final List<String> SUSPICIOUS_PATTERNS = List.of(
        "automated",
        "bot",
        "script",
        "bulk"
    );
    
    /**
     * Comprehensive QR token validation
     */
    public ValidationResult validateQRToken(String token) {
        ValidationResult result = new ValidationResult();
        
        try {
            // Basic JWT validation
            if (!jwtUtils.validateJwtToken(token)) {
                result.setValid(false);
                result.setErrorMessage("Invalid JWT token");
                return result;
            }
            
            // Check expiration
            if (jwtUtils.isTokenExpired(token)) {
                result.setValid(false);
                result.setErrorMessage("QR code has expired");
                return result;
            }
            
            // Extract and validate claims
            Claims claims = jwtUtils.getAllClaimsFromToken(token);
            
            // Validate required claims
            if (!hasRequiredClaims(claims)) {
                result.setValid(false);
                result.setErrorMessage("Missing required token claims");
                return result;
            }
            
            // Validate token purpose
            String purpose = (String) claims.get("purpose");
            if (!"attendance".equals(purpose)) {
                result.setValid(false);
                result.setErrorMessage("Invalid token purpose");
                return result;
            }
            
            // Check if token was already used
            String tokenId = jwtUtils.getTokenId(token);
            if (attendanceRepo.existsByQrTokenUsed(tokenId)) {
                result.setValid(false);
                result.setErrorMessage("QR code has already been used");
                return result;
            }
            
            // Validate generation time (prevent very old tokens)
            if (!isTokenGeneratedRecently(claims)) {
                result.setValid(false);
                result.setErrorMessage("QR code is too old");
                return result;
            }
            
            // Check for suspicious patterns
            if (containsSuspiciousPatterns(claims)) {
                result.setValid(false);
                result.setErrorMessage("Suspicious token detected");
                return result;
            }
            
            result.setValid(true);
            result.setClaims(claims);
            
        } catch (Exception e) {
            result.setValid(false);
            result.setErrorMessage("Token validation failed: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Checks if all required claims are present
     */
    private boolean hasRequiredClaims(Claims claims) {
        return claims.get("userId") != null &&
               claims.get("eventId") != null &&
               claims.get("purpose") != null &&
               claims.get("generatedAt") != null;
    }
    
    /**
     * Validates that token was generated recently (within reasonable timeframe)
     */
    private boolean isTokenGeneratedRecently(Claims claims) {
        try {
            String generatedAtStr = (String) claims.get("generatedAt");
            LocalDateTime generatedAt = LocalDateTime.parse(generatedAtStr);
            LocalDateTime maxAge = LocalDateTime.now().minusHours(24); // Max 24 hours old
            
            return generatedAt.isAfter(maxAge);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks for suspicious patterns in token claims
     */
    private boolean containsSuspiciousPatterns(Claims claims) {
        String userName = (String) claims.get("userName");
        String eventName = (String) claims.get("eventName");
        
        if (userName != null) {
            String userLower = userName.toLowerCase();
            for (String pattern : SUSPICIOUS_PATTERNS) {
                if (userLower.contains(pattern)) {
                    return true;
                }
            }
        }
        
        if (eventName != null) {
            String eventLower = eventName.toLowerCase();
            for (String pattern : SUSPICIOUS_PATTERNS) {
                if (eventLower.contains(pattern)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Result class for validation
     */
    public static class ValidationResult {
        private boolean valid;
        private String errorMessage;
        private Claims claims;
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        
        public Claims getClaims() {
            return claims;
        }
        
        public void setClaims(Claims claims) {
            this.claims = claims;
        }
    }
}
