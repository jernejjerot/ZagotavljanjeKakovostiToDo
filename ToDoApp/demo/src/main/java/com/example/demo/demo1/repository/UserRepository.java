package com.example.demo.demo1.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query method to find users by email and password for login
    Optional<User> findByEmailAndPassword(String email, String password);
}
