package com.ris.todoapp.controller;

import com.ris.todoapp.entity.Task;
import com.ris.todoapp.entity.User;
import com.ris.todoapp.entity.TaskType;
import com.ris.todoapp.repository.TaskRepository;
import com.ris.todoapp.repository.UserRepository;
import com.ris.todoapp.repository.TaskTypeRepository;
import com.ris.todoapp.service.GeocodingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")                      // <-- baza je /tasks
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final GeocodingService geocodingService;

    public TaskController(TaskRepository taskRepository,
                          UserRepository userRepository,
                          TaskTypeRepository taskTypeRepository,
                          GeocodingService geocodingService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskTypeRepository = taskTypeRepository;
        this.geocodingService = geocodingService;
    }

    // POST /tasks  (NE /tasks/tasks)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTask(@RequestBody Task task,
                                        @RequestHeader("user-id") Long userId) {
        // 1) validacije minimalne, da ne treščimo 500
        if (task.getTaskName() == null || task.getTaskName().isBlank()) {
            return ResponseEntity.badRequest().body("Task name is required.");
        }
        if (task.getTaskType() == null || task.getTaskType().getId() == null) {
            return ResponseEntity.badRequest().body("Task type id is required.");
        }

        // 2) uporabnik in tip naloge
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user id.");
        }
        Optional<TaskType> typeOpt = taskTypeRepository.findById(task.getTaskType().getId());
        if (typeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid task type id.");
        }

        // 3) sestavi in shrani
        task.setUser(userOpt.get());
        task.setTaskType(typeOpt.get());

        // fallback slika, če je prazno
        if (task.getPicture() == null || task.getPicture().isBlank()) {
            task.setPicture("/uploads/default.jpg");
        }

        Task saved = taskRepository.save(task);
        return ResponseEntity
                .created(URI.create("/tasks/" + saved.getId()))
                .body(saved);
    }

    // Primer: GET /tasks
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listTasks(@RequestHeader("user-id") Long userId) {
        return ResponseEntity.ok(taskRepository.findByUserId(userId));
    }


    @PostMapping("/users/{userId}/tasks/{taskId}/picture")
    public ResponseEntity<?> uploadTaskPicture(@PathVariable Long userId,
                                               @PathVariable Long taskId,
                                               @RequestParam("picture") MultipartFile picture) {
        try {
            if (picture == null || picture.isEmpty()) {
                return ResponseEntity.badRequest().body("Uploaded file is empty.");
            }

            Optional<Task> optTask = taskRepository.findById(taskId);
            if (optTask.isEmpty()) {
                return ResponseEntity.status(404).body("Task not found.");
            }

            Task task = optTask.get();

            // Avtorizacija: naloga mora pripadati userId
            if (task.getUser() == null || task.getUser().getId() == null || !task.getUser().getId().equals(userId)) {
                return ResponseEntity.status(403).body("Unauthorized to upload picture for this task.");
            }

            // (opcijsko) shranjevanje na disk; če ne želiš pisati na disk, lahko to sekcijo izpustiš
            String original = picture.getOriginalFilename() != null ? picture.getOriginalFilename() : "upload.bin";
            String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
            String storedName = UUID.randomUUID() + ext;
            Path uploadDir = Path.of("uploads"); // ali preberi iz properties
            Files.createDirectories(uploadDir);
            Path filePath = uploadDir.resolve(storedName);
            Files.write(filePath, picture.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Shrani pot/ime v entiteti
            task.setPicture("/uploads/" + storedName);
            taskRepository.save(task);

            return ResponseEntity.ok("Picture uploaded successfully.");
        } catch (IOException io) {
            return ResponseEntity.status(500).body("Failed to store picture.");
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Unexpected error while uploading picture.");
        }
    }

    // Primer: PUT /tasks/{id}
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTask(@PathVariable Long id,
                                        @RequestBody Task incoming,
                                        @RequestHeader("user-id") Long userId) {
        return taskRepository.findById(id).map(db -> {
            if (!db.getUser().getId().equals(userId)) {
                return ResponseEntity.status(403).body("Forbidden");
            }
            if (incoming.getTaskName() != null) db.setTaskName(incoming.getTaskName());
            if (incoming.getDescription() != null) db.setDescription(incoming.getDescription());
            if (incoming.getDueDateTime() != null) db.setDueDateTime(incoming.getDueDateTime());
            if (incoming.getPicture() != null) db.setPicture(incoming.getPicture());
            return ResponseEntity.ok(taskRepository.save(db));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@RequestHeader("user-id") Long userId, @PathVariable Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) return ResponseEntity.notFound().build();

        Task task = taskOpt.get();
        if (!task.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized delete attempt.");
        }

        taskRepository.delete(task);
        return ResponseEntity.ok("Task deleted successfully.");
    }
}