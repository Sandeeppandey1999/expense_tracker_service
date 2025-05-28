package com.expensetracker.repository;

import com.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameOrEmailOrMobile(String username, String email, String mobile);
    Optional<User> findByUsername(String username); // ✅ fixed
    boolean existsByUsername(String username); // ✅ fixed
    boolean existsByEmail(String email);       // ✅ fixed
    boolean existsByMobile(String mobile);     // ✅ fixed
}
