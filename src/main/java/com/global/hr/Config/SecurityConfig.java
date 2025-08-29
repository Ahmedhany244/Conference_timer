package com.global.hr.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.global.hr.Service.CustomUserDetailsService;
import com.global.hr.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager Bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // DAO Authentication Provider
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Security Filter Chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/", "/index.html", "/user-register.html", "/user-login.html", "/admin-register.html", "/admin-login.html", "/qr-test.html", "/admin-dashboard.html", "/timer-display.html", "/reports-dashboard.html", "/test-helper.html", "/static/**").permitAll()
                .requestMatchers("/event/**").hasRole("ADMIN")
                .requestMatchers("/users/**").hasRole("USER")
                .requestMatchers("/api/qr/generate").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/qr/scan").hasRole("ADMIN")
                .requestMatchers("/api/qr/attendance/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/timer/dashboard/**", "/api/timer/stop-*", "/api/timer/break/**", "/api/timer/search", "/api/timer/break-types").hasRole("ADMIN")
                .requestMatchers("/api/timer/session/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/reports/event/**", "/api/reports/multi-event").hasRole("ADMIN")
                .requestMatchers("/api/reports/event/*/summary", "/api/reports/event/*/csv", "/api/reports/event/*/top-performers", "/api/reports/event/*/break-analysis").hasRole("ADMIN")
                .requestMatchers("/api/reports/attendee/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/sse/dashboard/**", "/api/sse/connections").hasRole("ADMIN")
                .requestMatchers("/api/sse/user/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/test/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
