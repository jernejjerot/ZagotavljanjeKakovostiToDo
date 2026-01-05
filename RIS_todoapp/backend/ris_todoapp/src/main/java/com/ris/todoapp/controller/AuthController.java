package com.ris.todoapp.controller;

import com.ris.todoapp.entity.User;
import com.ris.todoapp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (user == null || isBlank(user.getEmail()) || isBlank(user.getPassword())) {
            return ResponseEntity.badRequest().body("Email and password are required.");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use.");
        }

        user.setAdmin(false);
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        if (user == null || isBlank(user.getEmail()) || isBlank(user.getPassword())) {
            return ResponseEntity.badRequest().body("Email and password are required.");
        }

        Optional<User> found = userRepository.findByEmail(user.getEmail());
        if (found.isPresent() && found.get().getPassword().equals(user.getPassword())) {
            return ResponseEntity.ok(found.get());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    // test za sonarqube
}