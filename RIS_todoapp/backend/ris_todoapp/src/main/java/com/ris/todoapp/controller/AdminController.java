package com.ris.todoapp.controller;

import com.ris.todoapp.entity.Task;
import com.ris.todoapp.entity.User;
import com.ris.todoapp.repository.TaskRepository;
import com.ris.todoapp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public AdminController(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("admin-id") Long adminId) {
        if (!isAdmin(adminId)) return forbidden();
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestHeader("admin-id") Long adminId) {
        if (!isAdmin(adminId)) return forbidden();

        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted.");
    }

    @GetMapping("/users/{id}/tasks")
    public ResponseEntity<?> getUserTasks(@PathVariable Long id, @RequestHeader("admin-id") Long adminId) {
        if (!isAdmin(adminId)) return forbidden();

        return ResponseEntity.ok(taskRepository.findByUserId(id));
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Long taskId,
                                        @RequestBody Task details,
                                        @RequestHeader("admin-id") Long adminId) {
        if (!isAdmin(adminId)) return forbidden();

        Optional<Task> opt = taskRepository.findById(taskId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        }

        Task task = opt.get();
        task.setTaskName(details.getTaskName());
        task.setDescription(details.getDescription());
        task.setDueDateTime(details.getDueDateTime());
        task.setCompleted(details.isCompleted());

        taskRepository.save(task);
        return ResponseEntity.ok("Task updated.");
    }

    private boolean isAdmin(Long adminId) {
        if (adminId == null) return false;
        return userRepository.findById(adminId).map(User::isAdmin).orElse(false);
    }

    private ResponseEntity<String> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required.");
    }

    //test za sonarqube
}