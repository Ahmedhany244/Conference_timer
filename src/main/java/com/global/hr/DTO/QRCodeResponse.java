package com.global.hr.DTO;

public class QRCodeResponse {
    private String qrCodeBase64;
    private String token;
    private String expiryTime;
    private String eventName;
    private String userName;
    
    // Constructors
    public QRCodeResponse() {}
    
    public QRCodeResponse(String qrCodeBase64, String token, String expiryTime, String eventName, String userName) {
        this.qrCodeBase64 = qrCodeBase64;
        this.token = token;
        this.expiryTime = expiryTime;
        this.eventName = eventName;
        this.userName = userName;
    }
    
    // Getters and Setters
    public String getQrCodeBase64() {
        return qrCodeBase64;
    }
    
    public void setQrCodeBase64(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getExpiryTime() {
        return expiryTime;
    }
    
    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }
    
    public String getEventName() {
        return eventName;
    }
    
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
