package com.global.hr.Controller;

import com.global.hr.Service.TimerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sse")
@CrossOrigin(origins = "*")
public class SSEController {
    
    @Autowired
    private TimerService timerService;
    
    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    /**
     * Subscribe to real-time dashboard updates (Admin only)
     */
    @GetMapping(value = "/dashboard/{eventId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public SseEmitter subscribeToDashboardUpdates(@PathVariable Long eventId) {
        String emitterId = "dashboard_" + eventId + "_" + System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        emitters.put(emitterId, emitter);
        
        emitter.onCompletion(() -> emitters.remove(emitterId));
        emitter.onTimeout(() -> emitters.remove(emitterId));
        emitter.onError((ex) -> emitters.remove(emitterId));
        
        // Send initial data
        try {
            TimerService.EventTimerDashboard dashboard = timerService.getEventTimerDashboard(eventId);
            emitter.send(SseEmitter.event()
                .name("dashboard-update")
                .data(dashboard));
        } catch (Exception e) {
            emitter.completeWithError(e);
            return emitter;
        }
        
        // Schedule periodic updates every 10 seconds
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (emitters.containsKey(emitterId)) {
                    TimerService.EventTimerDashboard dashboard = timerService.getEventTimerDashboard(eventId);
                    emitter.send(SseEmitter.event()
                        .name("dashboard-update")
                        .data(dashboard));
                }
            } catch (IOException e) {
                emitters.remove(emitterId);
                emitter.complete();
            } catch (Exception e) {
                emitters.remove(emitterId);
                emitter.completeWithError(e);
            }
        }, 10, 10, TimeUnit.SECONDS);
        
        return emitter;
    }
    
    /**
     * Subscribe to user timer updates
     */
    @GetMapping(value = "/user/{userId}/event/{eventId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public SseEmitter subscribeToUserTimer(@PathVariable Long userId, @PathVariable Long eventId) {
        String emitterId = "user_" + userId + "_" + eventId + "_" + System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        emitters.put(emitterId, emitter);
        
        emitter.onCompletion(() -> emitters.remove(emitterId));
        emitter.onTimeout(() -> emitters.remove(emitterId));
        emitter.onError((ex) -> emitters.remove(emitterId));
        
        // Send initial data
        try {
            var timerSession = timerService.getUserTimerSession(userId, eventId);
            emitter.send(SseEmitter.event()
                .name("timer-update")
                .data(timerSession));
        } catch (Exception e) {
            System.out.println("Error sending timer update: " + e.getMessage());
        }
        
        // Schedule periodic updates every 5 seconds
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (emitters.containsKey(emitterId)) {
                    var timerSession = timerService.getUserTimerSession(userId, eventId);
                    emitter.send(SseEmitter.event()
                        .name("timer-update")
                        .data(timerSession));
                }
            } catch (IOException e) {
                emitters.remove(emitterId);
                emitter.complete();
            } catch (Exception e) {
                // Timer session might not exist or be inactive
                try {
                    emitter.send(SseEmitter.event()
                        .name("timer-update")
                        .data("{}"));
                } catch (IOException ioException) {
                    emitters.remove(emitterId);
                    emitter.complete();
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
        
        return emitter;
    }
    
    /**
     * Broadcast event to all connected clients for a specific event
     */
    public void broadcastEventUpdate(Long eventId, String eventType, Object data) {
        String prefix = "dashboard_" + eventId + "_";
        emitters.entrySet().removeIf(entry -> {
            if (entry.getKey().startsWith(prefix)) {
                try {
                    entry.getValue().send(SseEmitter.event()
                        .name(eventType)
                        .data(data));
                    return false; // Keep the emitter
                } catch (IOException e) {
                    entry.getValue().complete();
                    return true; // Remove the emitter
                }
            }
            return false;
        });
    }
    
    /**
     * Get current connection count
     */
    @GetMapping("/connections")
    @PreAuthorize("hasRole('ADMIN')")
    public int getConnectionCount() {
        return emitters.size();
    }
}
