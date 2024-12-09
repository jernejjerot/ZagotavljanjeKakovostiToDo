package com.ris.todoapp.repository;

import com.ris.todoapp.entity.Task;
import com.ris.todoapp.entity.TaskType;
import com.ris.todoapp.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    private User testUser;
    private TaskType testTaskType;

    @BeforeEach
    void setUp() {
        // Ustvari testnega uporabnika
        testUser = new User();
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password123");
        testUser.setAdmin(false);
        testUser = userRepository.save(testUser);

        // Ustvari testni tip naloge
        testTaskType = new TaskType();
        testTaskType.setType("Test Task Type");
        testTaskType = taskTypeRepository.save(testTaskType);

        // Ustvari testne naloge
        Task task1 = new Task();
        task1.setTaskName("Task 1");
        task1.setDescription("Description 1");
        task1.setUser(testUser);
        task1.setTaskType(testTaskType);
        task1.setDueDateTime(LocalDateTime.now().plusDays(1));
        task1.setCompleted(false);
        task1.setPicture("/uploads/task1.jpg");
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTaskName("Task 2");
        task2.setDescription("Description 2");
        task2.setUser(testUser);
        task2.setTaskType(testTaskType);
        task2.setDueDateTime(LocalDateTime.now().plusDays(2));
        task2.setCompleted(true);
        task2.setPicture("/uploads/task2.jpg");
        taskRepository.save(task2);
    }

    @AfterEach
    void tearDown() {
        // Izbriši samo naloge, ustvarjene v testih
        taskRepository.deleteById(testUser.getId());

        // Izbriši samo testne tipe nalog
        taskTypeRepository.deleteById(testTaskType.getId());

        // Izbriši samo testne uporabnike
        userRepository.deleteById(testUser.getId());
    }



    @Test
    void testFindByUserId() {
        List<Task> tasks = taskRepository.findByUserId(testUser.getId());
        assertEquals(2, tasks.size(), "Pričakovano število nalog je 2.");
        assertTrue(tasks.stream().allMatch(task -> task.getUser().getId().equals(testUser.getId())),
                "Vse naloge morajo pripadati testnemu uporabniku.");
    }

    @Test
    void testFindByUserIdAndIsCompletedTrue() {
        List<Task> completedTasks = taskRepository.findByUserIdAndIsCompletedTrue(testUser.getId());
        assertEquals(1, completedTasks.size(), "Pričakovano število dokončanih nalog je 1.");
        assertTrue(completedTasks.get(0).isCompleted(), "Naloga mora biti označena kot dokončana.");
    }

    @Test
    void testFindByPicture() {
        List<Task> tasksWithPicture = taskRepository.findByPicture("/uploads/task1.jpg");
        assertEquals(1, tasksWithPicture.size(), "Pričakovano število nalog s podano sliko je 1.");
        assertEquals("Task 1", tasksWithPicture.get(0).getTaskName(), "Ime naloge mora biti 'Task 1'.");
    }
}
