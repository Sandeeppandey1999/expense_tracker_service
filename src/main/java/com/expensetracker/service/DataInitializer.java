package com.expensetracker.service;

import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminUsername = "admin";

        boolean exists = userRepository.existsByUsername(adminUsername);

        if (!exists) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setEmail("admin@example.com");
            adminUser.setMobile("9999999999");
            adminUser.setPassword(passwordEncoder.encode("admin123"));

            userRepository.save(adminUser);
            System.out.println("✅ Default admin user created successfully.");
        } else {
            System.out.println("ℹ️ Admin user already exists.");
        }
    }
}
