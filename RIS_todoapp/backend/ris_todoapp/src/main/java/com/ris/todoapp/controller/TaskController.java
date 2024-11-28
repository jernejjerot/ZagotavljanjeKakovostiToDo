package com.ris.todoapp.controller;

import com.ris.todoapp.entity.Task;
import com.ris.todoapp.entity.TaskType;
import com.ris.todoapp.entity.User;
import com.ris.todoapp.repository.TaskRepository;
import com.ris.todoapp.repository.TaskTypeRepository;
import com.ris.todoapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from the frontend
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    // Create a new task
    @PostMapping
public ResponseEntity<?> createTask(@RequestBody Task task, @RequestHeader("user-id") Long userId) {
    try {
        // Check if user exists
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user ID.");
        }

        // Validate TaskType
        if (task.getTaskType() == null || task.getTaskType().getId() == null) {
            return ResponseEntity.badRequest().body("TaskType must be provided.");
        }

        Optional<TaskType> taskType = taskTypeRepository.findById(task.getTaskType().getId());
        if (taskType.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid task type ID.");
        }

        // Check for null or blank taskName
        if (task.getTaskName() == null || task.getTaskName().isBlank()) {
            return ResponseEntity.badRequest().body("Task name cannot be blank.");
        }

        // Validate description
        if (task.getDescription() == null || task.getDescription().isBlank()) {
            task.setDescription("No description provided.");
        }

        // Set user and task type
        task.setUser(user.get());
        task.setTaskType(taskType.get());

        Task savedTask = taskRepository.save(task);
        return ResponseEntity.ok(savedTask);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error occurred while creating task.");
    }
}



    // Get all tasks for a user
    @GetMapping
public ResponseEntity<?> getTasks(@RequestHeader("user-id") Long userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
        return ResponseEntity.badRequest().body("Invalid user ID.");
    }
    // Fetch only non-completed tasks
    List<Task> tasks = taskRepository.findByUserId(userId).stream()
            .filter(task -> !task.isCompleted())
            .toList();
    return ResponseEntity.ok(tasks);
}


    // Fetch a single task by ID
    @GetMapping("/{id}")
public ResponseEntity<?> getTaskById(@PathVariable Long id, @RequestHeader("user-id") Long userId) {
    try {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty() || !task.get().getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("Unauthorized access or task not found.");
        }
        return ResponseEntity.ok(task.get());
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error fetching task.");
    }
}

// Update a task
@PutMapping("/{id}")
public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task taskDetails, @RequestHeader("user-id") Long userId) {
    try {
        Optional<Task> taskOptional = taskRepository.findById(id);

        if (taskOptional.isEmpty() || !taskOptional.get().getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("Unauthorized update or task not found.");
        }

        Task taskToUpdate = taskOptional.get();

        // Validate taskName
        if (taskDetails.getTaskName() == null || taskDetails.getTaskName().isBlank()) {
            return ResponseEntity.badRequest().body("Task name cannot be blank.");
        }
        taskToUpdate.setTaskName(taskDetails.getTaskName());

        // Validate description
        if (taskDetails.getDescription() == null || taskDetails.getDescription().isBlank()) {
            taskToUpdate.setDescription("No description provided.");
        } else {
            taskToUpdate.setDescription(taskDetails.getDescription());
        }

        if (taskDetails.getDueDateTime() != null) {
            taskToUpdate.setDueDateTime(taskDetails.getDueDateTime());
        }

        if (taskDetails.getTaskType() != null && taskDetails.getTaskType().getId() != null) {
            Optional<TaskType> taskType = taskTypeRepository.findById(taskDetails.getTaskType().getId());
            if (taskType.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid task type ID.");
            }
            taskToUpdate.setTaskType(taskType.get());
        }

        taskToUpdate.setCompleted(taskDetails.isCompleted());
        taskRepository.save(taskToUpdate);

        return ResponseEntity.ok("Task updated successfully.");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error updating task.");
    }
}





    // Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, @RequestHeader("user-id") Long userId) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty() || !task.get().getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("Unauthorized delete.");
        }
        taskRepository.deleteById(id);
        return ResponseEntity.ok("Task deleted successfully.");
    }

    @GetMapping("/done")
public ResponseEntity<?> getDoneTasks(@RequestHeader("user-id") Long userId) {
    try {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user ID.");
        }

        // Fetch completed tasks
        List<Task> doneTasks = taskRepository.findByUserIdAndIsCompletedTrue(userId);

        return ResponseEntity.ok(doneTasks);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error fetching done tasks.");
    }
}

@PostMapping("/done")
public ResponseEntity<?> moveTaskToDone(@RequestBody Task task, @RequestHeader("user-id") Long userId) {
    System.out.println("Received Task Object:");
    System.out.println("ID: " + task.getId());
    System.out.println("Task Name: " + task.getTaskName());
    System.out.println("Task Type: " + (task.getTaskType() != null ? task.getTaskType().getId() : "null"));
    System.out.println("Description: " + task.getDescription());
    System.out.println("Due DateTime: " + task.getDueDateTime());
    System.out.println("User: " + (task.getUser() != null ? task.getUser().getId() : "null"));
    System.out.println("Received task payload: " + task); // Debugging input
    
    
    if (task.getTaskName() == null || task.getTaskName().isBlank()) {
        return ResponseEntity.badRequest().body("Task name cannot be blank.");
    }
    try {
        // Fetch the task from the database
        Optional<Task> optionalTask = taskRepository.findById(task.getId());
        if (optionalTask.isEmpty()) {
            return ResponseEntity.status(404).body("Task not found.");
        }

        Task existingTask = optionalTask.get();

        // Ensure the task belongs to the user
        if (!existingTask.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("Unauthorized to update this task.");
        }

        // Only update the "isCompleted" flag
        existingTask.setCompleted(true);

        // Save the updated task
        Task updatedTask = taskRepository.save(existingTask);
        System.out.println("Updated Task: " + updatedTask);

        return ResponseEntity.ok("Task moved to done successfully.");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error moving task to done.");
    }
}













}
