package com.expensetracker.controller;

import com.expensetracker.dto.AuthRequest;
import com.expensetracker.dto.AuthResponse;
import com.expensetracker.model.User;
import com.expensetracker.security.JwtUtil;
import com.expensetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        if (userService.existsByMobile(user.getMobile())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Mobile number already exists");
        }
        User savedUser = userService.registerUser(user);
        System.out.println("✅ User registered successfully: " + savedUser.getUsername());
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Optional<User> optionalUser = userService.findByLoginField(request.getLoginField());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        User user = optionalUser.get();

        if (!new BCryptPasswordEncoder().matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        String accessToken = jwtUtil.generateToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Optional: Save refreshToken in DB for user via UserService

        AuthResponse response = new AuthResponse();
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUsername(user.getUsername());

        System.out.println("✅ User logged in successfully: " + user.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("refreshToken");

            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing refresh token");
            }

            String username = jwtUtil.extractUsername(token);

            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            String newAccessToken = jwtUtil.generateToken(username);
            AuthResponse response = new AuthResponse();
            response.setToken(newAccessToken);
            response.setUsername(username);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error refreshing token");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Authorization header");
            }

            String token = authHeader.substring(7); // Remove "Bearer "
            String username = jwtUtil.extractUsername(token);

            Optional<User> optionalUser = userService.findByUsername(username);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            return ResponseEntity.ok(optionalUser.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching profile");
        }
    }

    @PutMapping("/profileUpdate")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser) {
        Optional<User> existingUser = userService.getUserById(updatedUser.getId());
        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Update only the fields that are allowed to be changed
        User user = existingUser.get();
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setMobile(updatedUser.getMobile());

        User savedUser = userService.updateUser(user);
        System.out.println("✅ User profile updated successfully: " + savedUser.getUsername());
        return ResponseEntity.ok(savedUser);
    }
}
