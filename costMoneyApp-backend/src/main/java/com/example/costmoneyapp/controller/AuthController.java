package com.example.costmoneyapp.controller;

import com.example.costmoneyapp.dto.AuthResponse;
import com.example.costmoneyapp.dto.LoginRequest;
import com.example.costmoneyapp.dto.RegisterRequest;
import com.example.costmoneyapp.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for user: {}", request.getUsername());
        try {
            AuthResponse response = userService.login(request);
            log.info("Login successful for user: {}, userId: {}", response.getUsername(), response.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Login failed for user: {}, error: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request received for user: {}", request.getUsername());
        try {
            AuthResponse response = userService.register(request);
            log.info("Register successful for user: {}, userId: {}", response.getUsername(), response.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Register failed for user: {}, error: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}