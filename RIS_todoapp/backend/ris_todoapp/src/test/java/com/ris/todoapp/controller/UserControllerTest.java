package com.ris.todoapp.controller;

import com.ris.todoapp.entity.User;
import com.ris.todoapp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        // Create a test user
        testUser = new User();
        testUser.setName("John");
        testUser.setSurname("Doe");
        testUser.setEmail("john.doe22@example.com");
        testUser.setPassword("password123");
        testUser.setAdmin(false);

        // Save the test user in the database
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    public void tearDown() {
        // Clean up the database after each test
        userRepository.delete(testUser);
    }

    @Test
    public void testGetUserById_Success() {
        // Send a GET request to retrieve the user by ID
        ResponseEntity<User> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/users/" + testUser.getId(),
                User.class
        );

        // Assert that the response is successful and contains the expected user
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(testUser.getId());
        assertThat(response.getBody().getName()).isEqualTo(testUser.getName());
        assertThat(response.getBody().getEmail()).isEqualTo(testUser.getEmail());
    }

}
