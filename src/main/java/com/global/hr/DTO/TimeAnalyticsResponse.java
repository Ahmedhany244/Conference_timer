package com.global.hr.DTO;

import java.util.List;

/**
 * DTO for time-based analytics patterns
 */
public class TimeAnalyticsResponse {
    private final List<HourlyActivityResponse> hourlyActivity;
    private final List<DailyActivityResponse> dailyActivity;
    private final PeakTimeResponse peakCheckInTime;
    private final PeakTimeResponse peakCheckOutTime;
    private final double averageSessionLength;
    private final double longestSession;
    private final double shortestSession;
    
    public TimeAnalyticsResponse(List<HourlyActivityResponse> hourlyActivity,
                               List<DailyActivityResponse> dailyActivity,
                               PeakTimeResponse peakCheckInTime,
                               PeakTimeResponse peakCheckOutTime,
                               double averageSessionLength,
                               double longestSession,
                               double shortestSession) {
        this.hourlyActivity = hourlyActivity;
        this.dailyActivity = dailyActivity;
        this.peakCheckInTime = peakCheckInTime;
        this.peakCheckOutTime = peakCheckOutTime;
        this.averageSessionLength = averageSessionLength;
        this.longestSession = longestSession;
        this.shortestSession = shortestSession;
    }
    
    public List<HourlyActivityResponse> getHourlyActivity() {
        return hourlyActivity;
    }
    
    public List<DailyActivityResponse> getDailyActivity() {
        return dailyActivity;
    }
    
    public PeakTimeResponse getPeakCheckInTime() {
        return peakCheckInTime;
    }
    
    public PeakTimeResponse getPeakCheckOutTime() {
        return peakCheckOutTime;
    }
    
    public double getAverageSessionLength() {
        return averageSessionLength;
    }
    
    public double getLongestSession() {
        return longestSession;
    }
    
    public double getShortestSession() {
        return shortestSession;
    }
    
    /**
     * DTO for hourly activity breakdown
     */
    public static class HourlyActivityResponse {
        private final int hour;
        private final long checkInCount;
        private final long checkOutCount;
        private final long totalActivity;
        
        public HourlyActivityResponse(int hour, long checkInCount, long checkOutCount, long totalActivity) {
            this.hour = hour;
            this.checkInCount = checkInCount;
            this.checkOutCount = checkOutCount;
            this.totalActivity = totalActivity;
        }
        
        public int getHour() {
            return hour;
        }
        
        public long getCheckInCount() {
            return checkInCount;
        }
        
        public long getCheckOutCount() {
            return checkOutCount;
        }
        
        public long getTotalActivity() {
            return totalActivity;
        }
    }
    
    /**
     * DTO for daily activity breakdown
     */
    public static class DailyActivityResponse {
        private final String date;
        private final long totalActivity;
        private final long uniqueAttendees;
        private final double totalHours;
        
        public DailyActivityResponse(String date, long totalActivity, long uniqueAttendees, double totalHours) {
            this.date = date;
            this.totalActivity = totalActivity;
            this.uniqueAttendees = uniqueAttendees;
            this.totalHours = totalHours;
        }
        
        public String getDate() {
            return date;
        }
        
        public long getTotalActivity() {
            return totalActivity;
        }
        
        public long getUniqueAttendees() {
            return uniqueAttendees;
        }
        
        public double getTotalHours() {
            return totalHours;
        }
    }
    
    /**
     * DTO for peak time information
     */
    public static class PeakTimeResponse {
        private final int hour;
        private final long activityCount;
        private final String timeLabel;
        
        public PeakTimeResponse(int hour, long activityCount, String timeLabel) {
            this.hour = hour;
            this.activityCount = activityCount;
            this.timeLabel = timeLabel;
        }
        
        public int getHour() {
            return hour;
        }
        
        public long getActivityCount() {
            return activityCount;
        }
        
        public String getTimeLabel() {
            return timeLabel;
        }
    }
}
