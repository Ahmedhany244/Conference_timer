package com.global.hr.Service;

import com.global.hr.DTO.*;
import com.global.hr.Entity.*;
import com.global.hr.Repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final EventRepo eventRepo;
    private final RegistrationRepo registrationRepo;
    private final AttendanceEventRepo attendanceEventRepo;
    private final UserRepo userRepo;
    private final AttendanceService attendanceService;

    public AnalyticsService(EventRepo eventRepo, RegistrationRepo registrationRepo,
                          AttendanceEventRepo attendanceEventRepo, UserRepo userRepo,
                          AttendanceService attendanceService) {
        this.eventRepo = eventRepo;
        this.registrationRepo = registrationRepo;
        this.attendanceEventRepo = attendanceEventRepo;
        this.userRepo = userRepo;
        this.attendanceService = attendanceService;
    }

    /**
     * Get comprehensive analytics overview
     */
    @Transactional(readOnly = true)
    public AnalyticsOverviewResponse getAnalyticsOverview() {
        List<Registration> allRegistrations = registrationRepo.findAll();
        
        // Calculate attendance times
        List<Double> attendanceTimes = new ArrayList<>();
        long totalActiveAttendees = 0;
        long totalScanEvents = 0;
        
        for (Registration registration : allRegistrations) {
            Duration totalActive = attendanceService.computeTotalActiveTimeForRegistration(registration);
            double hours = attendanceService.computeCreditHours(totalActive.getSeconds());
            
            if (hours > 0) {
                attendanceTimes.add(hours);
                totalActiveAttendees++;
            }
            
            // Count scan events
            List<AttendanceEvent> events = attendanceEventRepo.findByRegistrationOrderByCreatedAtAsc(registration);
            totalScanEvents += events.size();
        }
        
        double averageAttendanceTime = attendanceTimes.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        
        double medianAttendanceTime = calculateMedian(attendanceTimes);
        
        // Calculate overall attendance rate
        long totalRegistrations = allRegistrations.size();
        double overallAttendanceRate = totalRegistrations > 0 
                ? (double) totalActiveAttendees / totalRegistrations * 100 
                : 0.0;
        
        // Calculate average session duration
        double averageSessionDuration = totalActiveAttendees > 0 
                ? averageAttendanceTime 
                : 0.0;
        
        return new AnalyticsOverviewResponse(
                averageAttendanceTime,
                medianAttendanceTime,
                totalActiveAttendees,
                overallAttendanceRate,
                totalScanEvents,
                averageSessionDuration
        );
    }

    /**
     * Get detailed analytics for all events
     */
    @Transactional(readOnly = true)
    public List<EventAnalyticsResponse> getEventAnalytics() {
        List<Event> events = eventRepo.findAll();
        
        return events.stream().map(event -> {
            List<Registration> registrations = registrationRepo.findByEvent(event);
            long totalRegistrations = registrations.size();
            
            // Calculate attendee statistics
            long totalAttendees = 0;
            double totalAttendanceHours = 0.0;
            long totalCheckIns = 0;
            long totalCheckOuts = 0;
            List<Double> sessionDurations = new ArrayList<>();
            
            for (Registration registration : registrations) {
                Duration totalActive = attendanceService.computeTotalActiveTimeForRegistration(registration);
                double hours = attendanceService.computeCreditHours(totalActive.getSeconds());
                
                if (hours > 0) {
                    totalAttendees++;
                    totalAttendanceHours += hours;
                    sessionDurations.add(hours);
                }
                
                // Count check-ins and check-outs
                List<AttendanceEvent> attendanceEvents = attendanceEventRepo.findByRegistrationOrderByCreatedAtAsc(registration);
                for (AttendanceEvent attendanceEvent : attendanceEvents) {
                    if (attendanceEvent.getEventType() == AttendanceEventType.CHECKIN) {
                        totalCheckIns++;
                    } else if (attendanceEvent.getEventType() == AttendanceEventType.CHECKOUT) {
                        totalCheckOuts++;
                    }
                }
            }
            
            double attendanceRate = totalRegistrations > 0 
                    ? (double) totalAttendees / totalRegistrations * 100 
                    : 0.0;
            
            double averageAttendanceTime = totalAttendees > 0 
                    ? totalAttendanceHours / totalAttendees 
                    : 0.0;
            
            double averageSessionDuration = sessionDurations.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            
            return new EventAnalyticsResponse(
                    event.getId(),
                    event.getEventName(),
                    totalRegistrations,
                    totalAttendees,
                    attendanceRate,
                    averageAttendanceTime,
                    totalAttendanceHours,
                    totalCheckIns,
                    totalCheckOuts,
                    averageSessionDuration,
                    event.getEventStartTime().toString(),
                    event.getEventEndTime().toString()
            );
        }).collect(Collectors.toList());
    }

    /**
     * Get time-based analytics patterns
     */
    @Transactional(readOnly = true)
    public TimeAnalyticsResponse getTimeAnalytics() {
        List<AttendanceEvent> allEvents = attendanceEventRepo.findAll();
        
        // Hourly activity breakdown
        Map<Integer, Map<AttendanceEventType, Long>> hourlyActivity = new HashMap<>();
        Map<String, Set<Long>> dailyUniqueAttendees = new HashMap<>();
        Map<String, Double> dailyHours = new HashMap<>();
        
        List<Double> allSessionLengths = new ArrayList<>();
        
        for (AttendanceEvent event : allEvents) {
            LocalDateTime timestamp = LocalDateTime.ofInstant(event.getCreatedAt(), ZoneId.systemDefault());
            int hour = timestamp.getHour();
            String date = timestamp.toLocalDate().toString();
            
            // Track hourly activity
            hourlyActivity.computeIfAbsent(hour, k -> new HashMap<>())
                    .merge(event.getEventType(), 1L, Long::sum);
            
            // Track daily unique attendees
            dailyUniqueAttendees.computeIfAbsent(date, k -> new HashSet<>())
                    .add(event.getRegistration().getUser().getId());
            
            // Calculate session lengths for this registration
            if (event.getEventType() == AttendanceEventType.CHECKOUT) {
                Duration sessionTime = attendanceService.computeTotalActiveTimeForRegistration(event.getRegistration());
                double hours = attendanceService.computeCreditHours(sessionTime.getSeconds());
                allSessionLengths.add(hours);
                
                dailyHours.merge(date, hours, Double::sum);
            }
        }
        
        // Build hourly activity response
        List<TimeAnalyticsResponse.HourlyActivityResponse> hourlyResponses = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            Map<AttendanceEventType, Long> hourData = hourlyActivity.getOrDefault(hour, new HashMap<>());
            long checkIns = hourData.getOrDefault(AttendanceEventType.CHECKIN, 0L);
            long checkOuts = hourData.getOrDefault(AttendanceEventType.CHECKOUT, 0L);
            long total = checkIns + checkOuts + 
                    hourData.getOrDefault(AttendanceEventType.PAUSE, 0L) + 
                    hourData.getOrDefault(AttendanceEventType.RESUME, 0L);
            
            hourlyResponses.add(new TimeAnalyticsResponse.HourlyActivityResponse(hour, checkIns, checkOuts, total));
        }
        
        // Build daily activity response
        List<TimeAnalyticsResponse.DailyActivityResponse> dailyResponses = dailyUniqueAttendees.entrySet().stream()
                .map(entry -> {
                    String date = entry.getKey();
                    long uniqueAttendees = entry.getValue().size();
                    long totalActivity = allEvents.stream()
                            .mapToLong(e -> LocalDateTime.ofInstant(e.getCreatedAt(), ZoneId.systemDefault())
                                    .toLocalDate().toString().equals(date) ? 1 : 0)
                            .sum();
                    double totalHours = dailyHours.getOrDefault(date, 0.0);
                    
                    return new TimeAnalyticsResponse.DailyActivityResponse(date, totalActivity, uniqueAttendees, totalHours);
                })
                .sorted(Comparator.comparing(TimeAnalyticsResponse.DailyActivityResponse::getDate))
                .collect(Collectors.toList());
        
        // Find peak times
        TimeAnalyticsResponse.PeakTimeResponse peakCheckIn = findPeakTime(hourlyActivity, AttendanceEventType.CHECKIN);
        TimeAnalyticsResponse.PeakTimeResponse peakCheckOut = findPeakTime(hourlyActivity, AttendanceEventType.CHECKOUT);
        
        // Calculate session statistics
        double averageSessionLength = allSessionLengths.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        
        double longestSession = allSessionLengths.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
        
        double shortestSession = allSessionLengths.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);
        
        return new TimeAnalyticsResponse(
                hourlyResponses,
                dailyResponses,
                peakCheckIn,
                peakCheckOut,
                averageSessionLength,
                longestSession,
                shortestSession
        );
    }

    /**
     * Get detailed attendance rate breakdowns
     */
    @Transactional(readOnly = true)
    public AttendanceRateBreakdownResponse getAttendanceRateBreakdown() {
        List<Event> events = eventRepo.findAll();
        
        // Calculate event rates
        List<AttendanceRateBreakdownResponse.EventRateResponse> eventRates = new ArrayList<>();
        long excellent = 0, good = 0, average = 0, poor = 0;
        
        for (Event event : events) {
            List<Registration> registrations = registrationRepo.findByEvent(event);
            long totalRegistrations = registrations.size();
            long attendees = registrations.stream()
                    .mapToLong(reg -> {
                        Duration time = attendanceService.computeTotalActiveTimeForRegistration(reg);
                        return attendanceService.computeCreditHours(time.getSeconds()) > 0 ? 1 : 0;
                    })
                    .sum();
            
            double rate = totalRegistrations > 0 ? (double) attendees / totalRegistrations * 100 : 0.0;
            
            String category;
            if (rate >= 90) {
                category = "Excellent";
                excellent++;
            } else if (rate >= 70) {
                category = "Good";
                good++;
            } else if (rate >= 50) {
                category = "Average";
                average++;
            } else {
                category = "Poor";
                poor++;
            }
            
            eventRates.add(new AttendanceRateBreakdownResponse.EventRateResponse(
                    event.getId(), event.getEventName(), totalRegistrations, attendees, rate, category
            ));
        }
        
        // Calculate overall rate
        long totalRegistrations = registrationRepo.count();
        long totalActiveAttendees = registrationRepo.findAll().stream()
                .mapToLong(reg -> {
                    Duration time = attendanceService.computeTotalActiveTimeForRegistration(reg);
                    return attendanceService.computeCreditHours(time.getSeconds()) > 0 ? 1 : 0;
                })
                .sum();
        
        double overallRate = totalRegistrations > 0 ? (double) totalActiveAttendees / totalRegistrations * 100 : 0.0;
        
        // Get top attendees
        List<AttendanceRateBreakdownResponse.UserAttendanceResponse> topAttendees = getTopAttendees();
        
        // Calculate average registration to attendance time
        double avgRegToAttendanceTime = calculateAverageRegistrationToAttendanceTime();
        
        return new AttendanceRateBreakdownResponse(
                overallRate,
                eventRates,
                new AttendanceRateBreakdownResponse.RateRangeResponse(excellent, good, average, poor),
                topAttendees,
                avgRegToAttendanceTime
        );
    }

    // Helper methods
    
    private double calculateMedian(List<Double> values) {
        if (values.isEmpty()) return 0.0;
        
        Collections.sort(values);
        int size = values.size();
        
        if (size % 2 == 0) {
            return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
        } else {
            return values.get(size / 2);
        }
    }
    
    private TimeAnalyticsResponse.PeakTimeResponse findPeakTime(
            Map<Integer, Map<AttendanceEventType, Long>> hourlyActivity, 
            AttendanceEventType eventType) {
        
        int peakHour = 0;
        long maxCount = 0;
        
        for (Map.Entry<Integer, Map<AttendanceEventType, Long>> entry : hourlyActivity.entrySet()) {
            long count = entry.getValue().getOrDefault(eventType, 0L);
            if (count > maxCount) {
                maxCount = count;
                peakHour = entry.getKey();
            }
        }
        
        String timeLabel = String.format("%02d:00", peakHour);
        return new TimeAnalyticsResponse.PeakTimeResponse(peakHour, maxCount, timeLabel);
    }
    
    private List<AttendanceRateBreakdownResponse.UserAttendanceResponse> getTopAttendees() {
        List<User> users = userRepo.findAll();
        
        return users.stream()
                .map(user -> {
                    List<Registration> registrations = registrationRepo.findByUser(user);
                    long eventsRegistered = registrations.size();
                    
                    long eventsAttended = registrations.stream()
                            .mapToLong(reg -> {
                                Duration time = attendanceService.computeTotalActiveTimeForRegistration(reg);
                                return attendanceService.computeCreditHours(time.getSeconds()) > 0 ? 1 : 0;
                            })
                            .sum();
                    
                    double totalHours = registrations.stream()
                            .mapToDouble(reg -> {
                                Duration time = attendanceService.computeTotalActiveTimeForRegistration(reg);
                                return attendanceService.computeCreditHours(time.getSeconds());
                            })
                            .sum();
                    
                    double attendanceRate = eventsRegistered > 0 ? (double) eventsAttended / eventsRegistered * 100 : 0.0;
                    
                    return new AttendanceRateBreakdownResponse.UserAttendanceResponse(
                            user.getId(), user.getName(), user.getEmail(),
                            eventsRegistered, eventsAttended, attendanceRate, totalHours
                    );
                })
                .filter(response -> response.getEventsRegistered() > 0)
                .sorted(Comparator.comparingDouble(AttendanceRateBreakdownResponse.UserAttendanceResponse::getTotalHours).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
    
    private double calculateAverageRegistrationToAttendanceTime() {
        List<Registration> registrations = registrationRepo.findAll();
        List<Duration> timesToFirstAttendance = new ArrayList<>();
        
        for (Registration registration : registrations) {
            List<AttendanceEvent> events = attendanceEventRepo.findByRegistrationOrderByCreatedAtAsc(registration);
            
            Optional<AttendanceEvent> firstCheckIn = events.stream()
                    .filter(e -> e.getEventType() == AttendanceEventType.CHECKIN)
                    .findFirst();
            
            if (firstCheckIn.isPresent()) {
                Duration timeToAttendance = Duration.between(
                        registration.getCreatedAt(),
                        firstCheckIn.get().getCreatedAt()
                );
                timesToFirstAttendance.add(timeToAttendance);
            }
        }
        
        return timesToFirstAttendance.stream()
                .mapToDouble(duration -> duration.toHours())
                .average()
                .orElse(0.0);
    }
}
