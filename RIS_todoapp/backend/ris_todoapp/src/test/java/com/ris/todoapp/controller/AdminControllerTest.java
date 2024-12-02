package com.ris.todoapp.controller;

import com.ris.todoapp.entity.Task;
import com.ris.todoapp.entity.TaskType;
import com.ris.todoapp.entity.User;
import com.ris.todoapp.repository.TaskRepository;
import com.ris.todoapp.repository.TaskTypeRepository;
import com.ris.todoapp.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AdminControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    private User adminUser;
    private User regularUser;
    private Task task;

    @BeforeEach
    void setUp() {
    // Create and save an admin user
    adminUser = new User();
    adminUser.setName("Admin");
    adminUser.setSurname("Test");
    adminUser.setEmail("admin@test.com");
    adminUser.setPassword("adminpass");
    adminUser.setAdmin(true);
    adminUser = userRepository.save(adminUser);

    // Create and save a regular user
    regularUser = new User();
    regularUser.setName("User");
    regularUser.setSurname("Test");
    regularUser.setEmail("user@test.com");
    regularUser.setPassword("userpass");
    regularUser.setAdmin(false);
    regularUser = userRepository.save(regularUser);

    // Create and save a task type
    TaskType taskType = new TaskType();
    taskType.setType("Work");
    taskType = taskTypeRepository.save(taskType);

    // Create and save a task for the regular user
    task = new Task();
    task.setTaskName("Test Task");
    task.setDescription("Task description");
    task.setUser(regularUser);
    task.setTaskType(taskType);  // Set the task type here
    task.setCompleted(false);
    task = taskRepository.save(task);
}

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        taskTypeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testDeleteUserAsAdmin() {
        ResponseEntity<?> response = userController.deleteUser(regularUser.getId(), adminUser.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Admin should be able to delete a user.");
        assertFalse(userRepository.existsById(regularUser.getId()), "The user should be deleted.");
    }

    @Test
    public void testDeleteUserAsNonAdmin() {
        ResponseEntity<?> response = userController.deleteUser(adminUser.getId(), regularUser.getId());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Non-admin should not be allowed to delete users.");
    }

    @Test
    public void testGetAllUsersAsAdmin() {
        ResponseEntity<?> response = userController.getAllUsers(adminUser.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Admin should be able to retrieve all users.");
        assertTrue(response.getBody() instanceof List, "Response should contain a list of users.");
        assertTrue(((List<?>) response.getBody()).size() >= 2, "There should be at least two users in the system.");
    }

    @Test
    public void testGetAllUsersAsNonAdmin() {
        ResponseEntity<?> response = userController.getAllUsers(regularUser.getId());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Non-admin should not be able to retrieve all users.");
    }

    @Test
    public void testDeleteTaskAsAdmin() {
        ResponseEntity<?> response = userController.deleteTask(task.getId(), adminUser.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Admin should be able to delete tasks.");
        assertFalse(taskRepository.existsById(task.getId()), "The task should be deleted.");
    }

    @Test
    public void testDeleteTaskAsNonAdmin() {
        ResponseEntity<?> response = userController.deleteTask(task.getId(), regularUser.getId());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Non-admin should not be allowed to delete tasks.");
    }
}
