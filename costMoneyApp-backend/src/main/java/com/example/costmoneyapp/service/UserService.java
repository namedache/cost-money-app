package com.example.costmoneyapp.service;

import com.example.costmoneyapp.config.JwtUtil;
import com.example.costmoneyapp.dto.AuthResponse;
import com.example.costmoneyapp.dto.LoginRequest;
import com.example.costmoneyapp.dto.RegisterRequest;
import com.example.costmoneyapp.entity.User;
import com.example.costmoneyapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        log.debug("Login attempt for user: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found - {}", request.getUsername());
                    return new RuntimeException("用户不存在");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: wrong password for user - {}", request.getUsername());
            throw new RuntimeException("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        log.info("User logged in successfully: {}, userId: {}", user.getUsername(), user.getId());

        return new AuthResponse(token, user.getId(), user.getUsername());
    }

    public AuthResponse register(RegisterRequest request) {
        log.debug("Register attempt for user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Register failed: username already exists - {}", request.getUsername());
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        log.info("User registered successfully: {}, userId: {}", user.getUsername(), user.getId());

        return new AuthResponse(token, user.getId(), user.getUsername());
    }
}