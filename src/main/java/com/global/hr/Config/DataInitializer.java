package com.global.hr.Config;

import com.global.hr.Entity.Admin;
import com.global.hr.Entity.Event;
import com.global.hr.Entity.User;
import com.global.hr.Repo.AdminRepo;
import com.global.hr.Repo.EventRepo;
import com.global.hr.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private AdminRepo adminRepo;
    
    @Autowired
    private EventRepo eventRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create test user if not exists
        if (userRepo.findByEmail("test@example.com").isEmpty()) {
            User testUser = new User();
            testUser.setName("Test User");
            testUser.setEmail("test@example.com");
            testUser.setPassword(passwordEncoder.encode("password"));
            testUser.setRole(User.Role.USER);
            userRepo.save(testUser);
            System.out.println("✅ Created test user: test@example.com / password");
        }
        
        // Create another test user
        if (userRepo.findByEmail("john@example.com").isEmpty()) {
            User johnUser = new User();
            johnUser.setName("John Doe");
            johnUser.setEmail("john@example.com");
            johnUser.setPassword(passwordEncoder.encode("password123"));
            johnUser.setRole(User.Role.USER);
            userRepo.save(johnUser);
            System.out.println("✅ Created test user: john@example.com / password123");
        }
        
        // Create test admin if not exists
        if (adminRepo.findByEmail("admin@example.com").isEmpty()) {
            Admin testAdmin = new Admin();
            testAdmin.setStaffName("Test Admin");
            testAdmin.setEmail("admin@example.com");
            testAdmin.setPassword(passwordEncoder.encode("admin123"));
            adminRepo.save(testAdmin);
            System.out.println("✅ Created test admin: admin@example.com / admin123");
        }
        
        // Create test events if none exist
        if (eventRepo.count() == 0) {
            // Event 1: Current conference
            Event event1 = new Event();
            event1.setEventName("Spring Boot Conference 2025");
            event1.setEventStartTime(LocalDateTime.now().plusHours(1));
            event1.setEventEndTime(LocalDateTime.now().plusHours(8));
            eventRepo.save(event1);
            
            // Event 2: Future workshop
            Event event2 = new Event();
            event2.setEventName("QR Code Workshop");
            event2.setEventStartTime(LocalDateTime.now().plusDays(1));
            event2.setEventEndTime(LocalDateTime.now().plusDays(1).plusHours(4));
            eventRepo.save(event2);
            
            // Event 3: Past event for testing
            Event event3 = new Event();
            event3.setEventName("Past Tech Meetup");
            event3.setEventStartTime(LocalDateTime.now().minusDays(1));
            event3.setEventEndTime(LocalDateTime.now().minusDays(1).plusHours(3));
            eventRepo.save(event3);
            
            System.out.println("✅ Created 3 test events");
        }
        
        System.out.println("🚀 Data initialization completed!");
        System.out.println("📋 Test Credentials:");
        System.out.println("   User: test@example.com / password");
        System.out.println("   User: john@example.com / password123");
        System.out.println("   Admin: admin@example.com / admin123");
    }
}
