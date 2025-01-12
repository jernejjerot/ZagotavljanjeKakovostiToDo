package com.ris.todoapp.controller;

import com.ris.todoapp.dto.GeocodingResult;
import com.ris.todoapp.entity.Task;
import com.ris.todoapp.entity.TaskType;
import com.ris.todoapp.entity.User;
import com.ris.todoapp.google.GoogleCalendarService;
import com.ris.todoapp.repository.TaskRepository;
import com.ris.todoapp.repository.TaskTypeRepository;
import com.ris.todoapp.repository.UserRepository;
import com.ris.todoapp.service.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from the frontend
public class TaskController {

    private static final String CLIENT_ID = "your-client-id"; // Nadomestite z vašim Client ID
    private static final Set<String> SCOPES = Set.of("Calendars.ReadWrite");

    private ResponseEntity<String> handleOutlookSyncError(Exception e, String message) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(message + " Outlook Calendar sync failed. Reason: " + e.getMessage());
    }

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoogleCalendarService googleCalendarService;


    @Autowired
    private TaskTypeRepository taskTypeRepository;


    // Create a new task
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task, @RequestHeader("user-id") Long userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid user ID.");
            }

            if (task.getTaskType() == null || task.getTaskType().getId() == null) {
                return ResponseEntity.badRequest().body("TaskType must be provided.");
            }

            Optional<TaskType> taskType = taskTypeRepository.findById(task.getTaskType().getId());
            if (taskType.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid task type ID.");
            }

            // Geocoding za naslov naloge (če obstaja)
            if (task.getLocationAddress() != null && !task.getLocationAddress().isBlank()) {
                Optional<GeocodingResult> resultOptional = geocodingService.geocode(task.getLocationAddress());
                if (resultOptional.isPresent()) {
                    GeocodingResult result = resultOptional.get();
                    task.setLatitude(result.getLatitude());
                    task.setLongitude(result.getLongitude());
                } else {
                    return ResponseEntity.badRequest().body("Invalid location address.");
                }
            }

            task.setUser(user.get());
            task.setTaskType(taskType.get());

            if (task.getPicture() == null || task.getPicture().isBlank()) {
                task.setPicture("/uploads/default.jpg"); // Default slika, če ni podana
            }

            Task savedTask = taskRepository.save(task);


        // Add task to Google Calendar if due date is provided
           /* if (task.getDueDateTime() != null) {
                googleCalendarService.addTaskToCalendar(
                        task.getTaskName(),
                        task.getDescription(),
                        new Date(), // Current date as start date
                        Date.from(task.getDueDateTime().atZone(ZoneId.systemDefault()).toInstant()) // Due date as end date
                );
            } */

            return ResponseEntity.ok(savedTask);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error occurred while creating task.");
        }
    }

    // Upload picture for a task
    @PostMapping("/{id}/upload")
    public ResponseEntity<?> uploadTaskPicture(
            @PathVariable Long id,
            @RequestHeader("user-id") Long userId,
            @RequestParam("picture") MultipartFile picture) {
        try {
            // Preverite obstoj naloge
            Optional<Task> optionalTask = taskRepository.findById(id);
            if (optionalTask.isEmpty()) {
                return ResponseEntity.status(404).body("Task not found.");
            }

            Task task = optionalTask.get();
            if (!task.getUser().getId().equals(userId)) {
                return ResponseEntity.status(403).body("Unauthorized to upload picture for this task.");
            }

            // Preverite, ali je datoteka prazna
            if (picture.isEmpty()) {
                return ResponseEntity.badRequest().body("Uploaded file is empty.");
            }

            // Prepričajte se, da mapa obstaja
            Path uploadPath = Paths.get(System.getProperty("user.dir") + "/uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Shranite datoteko
            String picturePath = "/uploads/" + picture.getOriginalFilename();
            Path absolutePath = uploadPath.resolve(picture.getOriginalFilename());
            Files.write(absolutePath, picture.getBytes());

            // Posodobite nalogo
            task.setPicture(picturePath);
            taskRepository.save(task);

            return ResponseEntity.ok("Picture uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading picture.");
        }
    }


    // Get all tasks for a user
    @GetMapping
    public ResponseEntity<?> getTasks(@RequestHeader("user-id") Long userId) {
        Optional<User> user = userRepository.findById(userId);
        System.out.println(userId);
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


            if (taskDetails.getPicture() != null && !taskDetails.getPicture().isBlank()) {
                taskToUpdate.setPicture(taskDetails.getPicture()); //pictures added
            }

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

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setTaskTypeRepository(TaskTypeRepository taskTypeRepository) {
        this.taskTypeRepository = taskTypeRepository;
    }

    public void setGeocodingService(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }
}
