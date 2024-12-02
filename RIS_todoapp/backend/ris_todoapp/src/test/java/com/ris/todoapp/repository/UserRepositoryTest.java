package com.ris.todoapp.repository;

import com.ris.todoapp.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password123");
        testUser.setAdmin(false);

        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testFindByEmail_Success() {
        Optional<User> foundUser = userRepository.findByEmail("testuser@example.com");
        assertTrue(foundUser.isPresent(), "User should be found by email.");
        assertEquals("Test", foundUser.get().getName(), "The user's name should match.");
    }

    @Test
    public void testFindByEmail_NotFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        assertFalse(foundUser.isPresent(), "No user should be found for a non-existent email.");
    }

    @Test
    public void testSaveUser() {
        User newUser = new User();
        newUser.setName("New");
        newUser.setSurname("User");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("newpassword");
        newUser.setAdmin(true);

        User savedUser = userRepository.save(newUser);
        assertNotNull(savedUser.getId(), "Saved user should have a generated ID.");
        assertTrue(userRepository.findByEmail("newuser@example.com").isPresent(), "Saved user should be retrievable.");
    }
}
