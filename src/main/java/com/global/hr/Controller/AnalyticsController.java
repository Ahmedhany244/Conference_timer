package com.global.hr.Controller;

import com.global.hr.DTO.*;
import com.global.hr.Service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for advanced analytics endpoints
 */
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * GET /analytics/overview
     * Get comprehensive analytics overview including average attendance time
     */
    @GetMapping("/overview")
    public ResponseEntity<AnalyticsOverviewResponse> getAnalyticsOverview() {
        AnalyticsOverviewResponse overview = analyticsService.getAnalyticsOverview();
        return ResponseEntity.ok(overview);
    }

    /**
     * GET /analytics/events
     * Get detailed analytics for all events
     */
    @GetMapping("/events")
    public ResponseEntity<List<EventAnalyticsResponse>> getEventAnalytics() {
        List<EventAnalyticsResponse> eventAnalytics = analyticsService.getEventAnalytics();
        return ResponseEntity.ok(eventAnalytics);
    }

    /**
     * GET /analytics/time-patterns
     * Get time-based analytics patterns (hourly/daily activity, peak times)
     */
    @GetMapping("/time-patterns")
    public ResponseEntity<TimeAnalyticsResponse> getTimeAnalytics() {
        TimeAnalyticsResponse timeAnalytics = analyticsService.getTimeAnalytics();
        return ResponseEntity.ok(timeAnalytics);
    }

    /**
     * GET /analytics/attendance-rates
     * Get detailed attendance rate breakdowns
     */
    @GetMapping("/attendance-rates")
    public ResponseEntity<AttendanceRateBreakdownResponse> getAttendanceRateBreakdown() {
        AttendanceRateBreakdownResponse rateBreakdown = analyticsService.getAttendanceRateBreakdown();
        return ResponseEntity.ok(rateBreakdown);
    }
}
