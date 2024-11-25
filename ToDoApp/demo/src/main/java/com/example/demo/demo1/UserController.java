package com.example.demo.demo1;

import com.example.demo.demo1.repository.UserRepository;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Create a new account (public endpoint)
    @PostMapping("/create-account")
    public ResponseEntity<User> createAccount(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // No user entity in the response
        }
        user.setAdmin(false); // Ensure admin status is false by default
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser); // Return the created user
    }

    // Admin creates a new user
    @PostMapping("/admin/create-user")
    public ResponseEntity<?> createUserByAdmin(@RequestBody User newUser, @RequestHeader("user-id") Long adminId) {
        // Check if the requesting user is an admin
        Optional<User> admin = userRepository.findById(adminId);
        if (admin.isPresent() && admin.get().getAdmin()) {
            // Save the new user
            if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use.");
            }
            newUser.setAdmin(false); // Ensure the new user is not an admin by default
            userRepository.save(newUser);
            return ResponseEntity.ok("User created successfully.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to create users.");
    }

    // Get all users (Admin only)
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader("user-id") Long adminId) {
        Optional<User> admin = userRepository.findById(adminId);
        if (admin.isPresent() && admin.get().getAdmin()) {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to view all users.");
    }

    // Get a specific user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok) // Return the found user with 200 OK
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // No body, just 404
    }

    // Update a user by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(userDetails.getName());
            user.setSurname(userDetails.getSurname());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            userRepository.save(user);
            return ResponseEntity.ok("User updated successfully.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }

    // Delete a user by ID (Admin only)
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestHeader("user-id") Long adminId) {
        Optional<User> admin = userRepository.findById(adminId);
        if (admin.isPresent() && admin.get().getAdmin()) {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                return ResponseEntity.ok("User deleted successfully.");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete users.");
    }

    // Handle user login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginDetails) {
        String email = loginDetails.get("email");
        String password = loginDetails.get("password");

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent() && user.get().getPassword().equals(password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.get().getId());
            response.put("isAdmin", user.get().getAdmin());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
    }
}