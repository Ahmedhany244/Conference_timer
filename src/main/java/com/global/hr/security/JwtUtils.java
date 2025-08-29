package com.global.hr.security;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

@Component
public class JwtUtils {

    // Use a long secret key (at least 64 characters for HS512)
    private final String jwtSecret = "my-super-strong-and-secure-jwt-secret-key-that-is-long-enough-1234567890";
    private final int jwtExpirationMs = 86400000; // 1 day

    private final Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    public String generateJwtToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities().iterator().next().getAuthority())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getRoleFromJwtToken(String token) {
        return (String) Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("role");
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Generates a JWT token specifically for QR codes with custom claims and expiry
     */
    public String generateQRToken(String subject, Map<String, Object> claims, int expiryHours) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (expiryHours * 60 * 60 * 1000L));
        
        return Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setId(UUID.randomUUID().toString()) // Unique token ID for tracking
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * Extracts all claims from a JWT token
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Gets a specific claim from the token
     */
    public Object getClaimFromToken(String token, String claimName) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get(claimName);
    }
    
    /**
     * Checks if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * Gets the unique token ID (jti claim)
     */
    public String getTokenId(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getId();
    }
}
