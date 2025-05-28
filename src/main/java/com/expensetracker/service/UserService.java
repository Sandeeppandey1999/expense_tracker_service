package com.expensetracker.service;

import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register a new user
    public User registerUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Find user by username OR email OR mobile
    public Optional<User> findByLoginField(String loginField) {
        return userRepository.findByUsernameOrEmailOrMobile(loginField, loginField, loginField);
    }

    // Check if username already exists
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // Check if email already exists
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Check if mobile number already exists
    public boolean existsByMobile(String mobile) {
        return userRepository.existsByMobile(mobile);
    }

    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Update user profile
    public User updateUser(User updatedUser) {
        return userRepository.save(updatedUser);
    }

    public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
}

}
