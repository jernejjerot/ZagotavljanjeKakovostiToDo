package com.ris.todoapp.repository;

import com.ris.todoapp.entity.Task;
import com.ris.todoapp.entity.TaskType;
import com.ris.todoapp.entity.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskRepositoryTest {

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
        // Uporabnik
        testUser = new User();
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setEmail("testuser+" + System.nanoTime() + "@example.com"); // unikaten email
        testUser.setPassword("password123");
        testUser.setAdmin(false);
        testUser = userRepository.save(testUser);

        // Tip naloge
        testTaskType = new TaskType();
        testTaskType.setType("Test Task Type " + System.nanoTime());
        testTaskType = taskTypeRepository.save(testTaskType);

        // Naloga 1
        Task task1 = new Task();
        task1.setTaskName("Task 1");
        task1.setDescription("Description 1");
        task1.setUser(testUser);
        task1.setTaskType(testTaskType);
        task1.setDueDateTime(LocalDateTime.now().plusDays(1));
        task1.setCompleted(false);
        task1.setPicture("/uploads/task1.jpg");
        taskRepository.save(task1);

        // Naloga 2
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
        // Pobriši naloge testnega uporabnika (pravilno; ne po userId na Task!)
        List<Task> usersTasks = taskRepository.findByUserId(testUser.getId());
        taskRepository.deleteAll(usersTasks);

        // Nato pobriši tip in uporabnika
        taskTypeRepository.deleteById(testTaskType.getId());
        userRepository.deleteById(testUser.getId());
    }

    // 1) findByUserId(Long userId)

    @Test
    void findByUserId_valid_returnsTwo() {
        List<Task> tasks = taskRepository.findByUserId(testUser.getId());
        assertEquals(2, tasks.size(), "Pričakovano število nalog je 2.");
        assertTrue(tasks.stream().allMatch(t -> t.getUser().getId().equals(testUser.getId())));
    }

    @ParameterizedTest(name = "findByUserId with id={0} returns empty list")
    @ValueSource(longs = {0L, -1L, -2L, Long.MIN_VALUE, Long.MAX_VALUE})
    void findByUserId_edgeIds_returnEmpty(long weirdId) {
        // Ideja: če userId ni pravi (0/negativni/ekstremni), pričakujemo prazen rezultat (ne izjeme)
        List<Task> tasks = taskRepository.findByUserId(weirdId);
        assertNotNull(tasks);
        assertTrue(tasks.isEmpty(), "Za nenavaden userId pričakujemo prazen seznam.");
    }

    // 2) findByUserIdAndIsCompletedTrue(Long userId)

    @Test
    void findByUserIdAndIsCompletedTrue_valid_returnsOneCompleted() {
        List<Task> completedTasks = taskRepository.findByUserIdAndIsCompletedTrue(testUser.getId());
        assertEquals(1, completedTasks.size(), "Pričakovano število dokončanih nalog je 1.");
        assertTrue(completedTasks.get(0).isCompleted());
    }

    @ParameterizedTest(name = "findByUserIdAndIsCompletedTrue with id={0} returns empty list")
    @ValueSource(longs = {0L, -1L, Long.MIN_VALUE, Long.MAX_VALUE})
    void findByUserIdAndIsCompletedTrue_edgeIds_returnEmpty(long weirdId) {
        List<Task> completedTasks = taskRepository.findByUserIdAndIsCompletedTrue(weirdId);
        assertNotNull(completedTasks);
        assertTrue(completedTasks.isEmpty(), "Za nenavaden userId pričakujemo prazen seznam.");
    }

    // 3) findByPicture(String picture)

    @Test
    void findByPicture_exactMatch_returnsOne() {
        List<Task> tasksWithPicture = taskRepository.findByPicture("/uploads/task1.jpg");
        assertEquals(1, tasksWithPicture.size(), "Pričakovano število nalog s podano sliko je 1.");
        assertEquals("Task 1", tasksWithPicture.get(0).getTaskName());
    }

    @Test
    void findByPicture_nonExisting_returnsEmpty() {
        List<Task> none = taskRepository.findByPicture("/uploads/does-not-exist.jpg");
        assertTrue(none.isEmpty(), "Za neobstoječo sliko ne sme biti rezultatov.");
    }

    @ParameterizedTest(name = "findByPicture with variant=\"{0}\" should not throw and typically be empty")
    @NullAndEmptySource
    @ValueSource(strings = {
            "   ",                              // samo presledki
            "/uploads/TASK1.JPG",               // drugačna velikost črk (case)
            "/uploads/task1.jpg   ",            // trailing spaces
            "task1.jpg",                        // brez poti
            "../uploads/task1.jpg",             // path traversal poskus
            "/uploads/%2e%2e/task1.jpg",        // URL-encoded traversal
            "/uploads/task1.jpg?x=1",           // query-string
            "/uploads/task1.jpg#frag",          // fragment
            "/uploads/'; DROP TABLE tasks;--",  // sql-injection style
    })
    void findByPicture_weirdInputs_noException_andUsuallyEmpty(String input) {
        List<Task> result = taskRepository.findByPicture(input);
        assertNotNull(result, "Metoda naj vrne prazen seznam, ne null.");
    }
}
