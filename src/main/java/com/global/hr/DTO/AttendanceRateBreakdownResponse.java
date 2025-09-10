package com.global.hr.DTO;

import java.util.List;

/**
 * DTO for detailed attendance rate breakdowns
 */
public class AttendanceRateBreakdownResponse {
    private final double overallAttendanceRate;
    private final List<EventRateResponse> eventRates;
    private final RateRangeResponse rateDistribution;
    private final List<UserAttendanceResponse> topAttendees;
    private final double averageRegistrationToAttendanceTime;
    
    public AttendanceRateBreakdownResponse(double overallAttendanceRate,
                                         List<EventRateResponse> eventRates,
                                         RateRangeResponse rateDistribution,
                                         List<UserAttendanceResponse> topAttendees,
                                         double averageRegistrationToAttendanceTime) {
        this.overallAttendanceRate = overallAttendanceRate;
        this.eventRates = eventRates;
        this.rateDistribution = rateDistribution;
        this.topAttendees = topAttendees;
        this.averageRegistrationToAttendanceTime = averageRegistrationToAttendanceTime;
    }
    
    public double getOverallAttendanceRate() {
        return overallAttendanceRate;
    }
    
    public List<EventRateResponse> getEventRates() {
        return eventRates;
    }
    
    public RateRangeResponse getRateDistribution() {
        return rateDistribution;
    }
    
    public List<UserAttendanceResponse> getTopAttendees() {
        return topAttendees;
    }
    
    public double getAverageRegistrationToAttendanceTime() {
        return averageRegistrationToAttendanceTime;
    }
    
    /**
     * DTO for individual event attendance rates
     */
    public static class EventRateResponse {
        private final Long eventId;
        private final String eventName;
        private final long registrations;
        private final long attendees;
        private final double attendanceRate;
        private final String category;
        
        public EventRateResponse(Long eventId, String eventName, long registrations, 
                               long attendees, double attendanceRate, String category) {
            this.eventId = eventId;
            this.eventName = eventName;
            this.registrations = registrations;
            this.attendees = attendees;
            this.attendanceRate = attendanceRate;
            this.category = category;
        }
        
        public Long getEventId() {
            return eventId;
        }
        
        public String getEventName() {
            return eventName;
        }
        
        public long getRegistrations() {
            return registrations;
        }
        
        public long getAttendees() {
            return attendees;
        }
        
        public double getAttendanceRate() {
            return attendanceRate;
        }
        
        public String getCategory() {
            return category;
        }
    }
    
    /**
     * DTO for attendance rate distribution
     */
    public static class RateRangeResponse {
        private final long excellent; // 90-100%
        private final long good;      // 70-89%
        private final long average;   // 50-69%
        private final long poor;      // 0-49%
        
        public RateRangeResponse(long excellent, long good, long average, long poor) {
            this.excellent = excellent;
            this.good = good;
            this.average = average;
            this.poor = poor;
        }
        
        public long getExcellent() {
            return excellent;
        }
        
        public long getGood() {
            return good;
        }
        
        public long getAverage() {
            return average;
        }
        
        public long getPoor() {
            return poor;
        }
    }
    
    /**
     * DTO for user attendance patterns
     */
    public static class UserAttendanceResponse {
        private final Long userId;
        private final String userName;
        private final String userEmail;
        private final long eventsRegistered;
        private final long eventsAttended;
        private final double attendanceRate;
        private final double totalHours;
        
        public UserAttendanceResponse(Long userId, String userName, String userEmail,
                                    long eventsRegistered, long eventsAttended, 
                                    double attendanceRate, double totalHours) {
            this.userId = userId;
            this.userName = userName;
            this.userEmail = userEmail;
            this.eventsRegistered = eventsRegistered;
            this.eventsAttended = eventsAttended;
            this.attendanceRate = attendanceRate;
            this.totalHours = totalHours;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public String getUserName() {
            return userName;
        }
        
        public String getUserEmail() {
            return userEmail;
        }
        
        public long getEventsRegistered() {
            return eventsRegistered;
        }
        
        public long getEventsAttended() {
            return eventsAttended;
        }
        
        public double getAttendanceRate() {
            return attendanceRate;
        }
        
        public double getTotalHours() {
            return totalHours;
        }
    }
}
