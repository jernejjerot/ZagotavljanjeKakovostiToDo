package com.example.demo.demo1;

import com.example.demo.demo1.repository.TaskRepository;
import com.example.demo.demo1.repository.UserRepository;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new task for the logged-in user
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task, @RequestHeader("user-id") Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid user ID");
        }

        // Assign the task to the user
        task.setUser(user.get());

        // Save task
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.ok(savedTask);
    }

    // Fetch all tasks for a specific user
    @GetMapping
    public ResponseEntity<?> getTasks(@RequestHeader("user-id") Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid user ID");
        }

        return ResponseEntity.ok(taskRepository.findByUserId(userId));
    }

    // Fetch a specific task by ID (ensure it belongs to the logged-in user)
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id, @RequestHeader("user-id") Long userId) {
        Optional<Task> task = taskRepository.findById(id);
        if (!task.isPresent() || !task.get().getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Task not found or access denied");
        }

        return ResponseEntity.ok(task.get());
    }

    // Update a task by ID (ensure it belongs to the logged-in user)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task taskDetails, @RequestHeader("user-id") Long userId) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));

        // Ensure the task belongs to the logged-in user
        if (!task.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot edit this task");
        }

        // Update task details
        task.setTaskName(taskDetails.getTaskName());
        task.setDescription(taskDetails.getDescription());
        task.setDueDate(taskDetails.getDueDate());
        task.setIsCompleted(taskDetails.getIsCompleted());

        Task updatedTask = taskRepository.save(task);
        return ResponseEntity.ok(updatedTask);
    }

    // Delete a task by ID (ensure it belongs to the logged-in user)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, @RequestHeader("user-id") Long userId) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));

        // Ensure the task belongs to the logged-in user
        if (!task.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot delete this task");
        }

        taskRepository.deleteById(id);
        return ResponseEntity.ok("Task deleted successfully");
    }
}