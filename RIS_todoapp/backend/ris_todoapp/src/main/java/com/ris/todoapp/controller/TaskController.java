package com.ris.todoapp.controller;

import com.ris.todoapp.entity.Task;
import com.ris.todoapp.entity.TaskType;
import com.ris.todoapp.entity.User;
import com.ris.todoapp.repository.TaskRepository;
import com.ris.todoapp.repository.TaskTypeRepository;
import com.ris.todoapp.repository.UserRepository;
import com.ris.todoapp.service.GeocodingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {

    private static final String DEFAULT_PICTURE_PATH = "/uploads/default.jpg";
    private static final String UPLOADS_DIR = "uploads";

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskTypeRepository taskTypeRepository;

    /**
     * NOTE: trenutno ni uporabljen v tem controllerju, ampak ga pustim,
     * da se ohrani obstoječa DI konfiguracija (če ga kje pričakuješ).
     */
    @SuppressWarnings("unused")
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTask(@RequestBody Task task,
                                        @RequestHeader("user-id") Long userId) {

        ResponseEntity<String> validation = validateCreateTaskRequest(task);
        if (validation != null) return validation;

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("Invalid user id.");

        Long typeId = task.getTaskType().getId();
        Optional<TaskType> typeOpt = taskTypeRepository.findById(typeId);
        if (typeOpt.isEmpty()) return ResponseEntity.badRequest().body("Invalid task type id.");

        task.setUser(userOpt.get());
        task.setTaskType(typeOpt.get());
        applyDefaultPictureIfMissing(task);

        Task saved = taskRepository.save(task);
        return ResponseEntity.created(URI.create("/tasks/" + saved.getId())).body(saved);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listTasks(@RequestHeader("user-id") Long userId) {
        return ResponseEntity.ok(taskRepository.findByUserId(userId));
    }

    @PostMapping("/users/{userId}/tasks/{taskId}/picture")
    public ResponseEntity<?> uploadTaskPicture(@PathVariable Long userId,
                                               @PathVariable Long taskId,
                                               @RequestParam("picture") MultipartFile picture) {

        ResponseEntity<String> fileValidation = validateUploadedFile(picture);
        if (fileValidation != null) return fileValidation;

        Optional<Task> optTask = taskRepository.findById(taskId);
        if (optTask.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");

        Task task = optTask.get();

        ResponseEntity<String> auth = authorizeTaskOwnership(task, userId);
        if (auth != null) return auth;

        try {
            String storedName = storeUploadedFile(picture);
            task.setPicture("/uploads/" + storedName);
            taskRepository.save(task);
            return ResponseEntity.ok("Picture uploaded successfully.");
        } catch (IOException io) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store picture.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error while uploading picture.");
        }
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTask(@PathVariable Long id,
                                        @RequestBody Task incoming,
                                        @RequestHeader("user-id") Long userId) {

        return taskRepository.findById(id).map(db -> {
            if (db.getUser() == null || db.getUser().getId() == null || !db.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
            }

            applyUpdates(db, incoming);
            return ResponseEntity.ok(taskRepository.save(db));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@RequestHeader("user-id") Long userId, @PathVariable Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) return ResponseEntity.notFound().build();

        Task task = taskOpt.get();
        if (task.getUser() == null || task.getUser().getId() == null || !task.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized delete attempt.");
        }

        taskRepository.delete(task);
        return ResponseEntity.ok("Task deleted successfully.");
    }

    // ---------- helpers (refactoring za nižjo kompleksnost + manj duplikacije) ----------

    private ResponseEntity<String> validateCreateTaskRequest(Task task) {
        if (task == null) return ResponseEntity.badRequest().body("Task payload is required.");
        if (task.getTaskName() == null || task.getTaskName().isBlank()) {
            return ResponseEntity.badRequest().body("Task name is required.");
        }
        if (task.getTaskType() == null || task.getTaskType().getId() == null) {
            return ResponseEntity.badRequest().body("Task type id is required.");
        }
        return null;
    }

    private void applyDefaultPictureIfMissing(Task task) {
        if (task.getPicture() == null || task.getPicture().isBlank()) {
            task.setPicture(DEFAULT_PICTURE_PATH);
        }
    }

    private ResponseEntity<String> validateUploadedFile(MultipartFile picture) {
        if (picture == null || picture.isEmpty()) {
            return ResponseEntity.badRequest().body("Uploaded file is empty.");
        }
        return null;
    }

    private ResponseEntity<String> authorizeTaskOwnership(Task task, Long userId) {
        if (task.getUser() == null || task.getUser().getId() == null || userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to upload picture for this task.");
        }
        if (!task.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to upload picture for this task.");
        }
        return null;
    }

    private String storeUploadedFile(MultipartFile picture) throws IOException {
        String original = picture.getOriginalFilename() != null ? picture.getOriginalFilename() : "upload.bin";
        String ext = extractExtension(original);
        String storedName = UUID.randomUUID() + ext;

        Path uploadDir = Path.of(UPLOADS_DIR);
        Files.createDirectories(uploadDir);

        Path filePath = uploadDir.resolve(storedName);
        Files.write(filePath, picture.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return storedName;
    }

    private String extractExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return (idx >= 0) ? filename.substring(idx) : "";
    }

    private void applyUpdates(Task db, Task incoming) {
        if (incoming == null) return;

        if (incoming.getTaskName() != null) db.setTaskName(incoming.getTaskName());
        if (incoming.getDescription() != null) db.setDescription(incoming.getDescription());
        if (incoming.getDueDateTime() != null) db.setDueDateTime(incoming.getDueDateTime());
        if (incoming.getPicture() != null) db.setPicture(incoming.getPicture());
    }
}