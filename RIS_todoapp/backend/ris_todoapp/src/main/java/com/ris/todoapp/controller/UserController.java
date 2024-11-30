package com.ris.todoapp.controller;

import com.ris.todoapp.entity.User;
import com.ris.todoapp.repository.UserRepository;
import com.ris.todoapp.entity.Task;
import com.ris.todoapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from the frontend
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            // Check if email already exists
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use.");
            }
            user.setAdmin(false); // Default admin status to false
            User savedUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during registration.");
        }
    }

    // User login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        try {
            Optional<User> foundUser = userRepository.findByEmail(user.getEmail());
            if (foundUser.isPresent() && foundUser.get().getPassword().equals(user.getPassword())) {
                return ResponseEntity.ok(foundUser.get());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during login.");
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
public ResponseEntity<?> getUserById(@PathVariable Long id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isPresent()) {
        return ResponseEntity.ok(user.get());
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }
}


    // Update user profile
    @PutMapping("/{id}")
public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
    try {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(userDetails.getName());
            user.setSurname(userDetails.getSurname());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating user.");
    }
}


    // Delete user (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestHeader("admin-id") Long adminId) {
        try {
            Optional<User> admin = userRepository.findById(adminId);
            if (admin.isPresent() && admin.get().isAdmin()) {
                if (userRepository.existsById(id)) {
                    userRepository.deleteById(id);
                    return ResponseEntity.ok("User deleted successfully.");
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete users.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting user.");
        }
    }

    // Get all users (Admin only)
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader("admin-id") Long adminId) {
        try {
            Optional<User> admin = userRepository.findById(adminId);
            if (admin.isPresent() && admin.get().isAdmin()) {
                List<User> users = userRepository.findAll();
                return ResponseEntity.ok(users);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to view users.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching users.");
        }
    }

    //ADMIN USER----------------------------------
    // Fetch all tasks for a specific user (Admin only)
@GetMapping("/{id}/tasks")
public ResponseEntity<?> getUserTasks(@PathVariable Long id, @RequestHeader("admin-id") Long adminId) {
    Optional<User> admin = userRepository.findById(adminId);
    if (admin.isPresent() && admin.get().isAdmin()) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            List<Task> tasks = taskRepository.findByUserId(user.get().getId());
            return ResponseEntity.ok(tasks);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to view tasks.");
}

// Delete a specific task (Admin only)
@DeleteMapping("/tasks/{taskId}")
public ResponseEntity<?> deleteTask(@PathVariable Long taskId, @RequestHeader("admin-id") Long adminId) {
    Optional<User> admin = userRepository.findById(adminId);
    if (admin.isPresent() && admin.get().isAdmin()) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return ResponseEntity.ok("Task deleted successfully.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete tasks.");
}

// Update a specific task (Admin only)
@PutMapping("/tasks/{taskId}")
public ResponseEntity<?> updateTaskAsAdmin(
    @PathVariable Long taskId,
    @RequestBody Task taskDetails,
    @RequestHeader("admin-id") Long adminId
) {
    Optional<User> admin = userRepository.findById(adminId);
    if (admin.isPresent() && admin.get().isAdmin()) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();

            // Update task fields
            if (taskDetails.getTaskName() != null && !taskDetails.getTaskName().isBlank()) {
                task.setTaskName(taskDetails.getTaskName());
            }
            if (taskDetails.getDescription() != null) {
                task.setDescription(taskDetails.getDescription());
            }
            if (taskDetails.getDueDateTime() != null) {
                task.setDueDateTime(taskDetails.getDueDateTime());
            }
            task.setCompleted(taskDetails.isCompleted());

            taskRepository.save(task);
            return ResponseEntity.ok("Task updated successfully.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update tasks.");
}
}