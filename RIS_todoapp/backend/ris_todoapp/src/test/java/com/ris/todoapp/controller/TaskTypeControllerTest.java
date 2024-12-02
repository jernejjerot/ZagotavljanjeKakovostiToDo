package com.ris.todoapp.controller;

import com.ris.todoapp.entity.TaskType;
import com.ris.todoapp.repository.TaskTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskTypeControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    private TaskType testTaskType;

    @BeforeEach
    public void setUp() {
        // Inicializiraj testni objekt TaskType
        testTaskType = new TaskType();
        testTaskType.setId(1L);
        testTaskType.setType("Test Task Type");

        // Shrani testni TaskType v bazo
        testTaskType = taskTypeRepository.save(testTaskType);
    }

    @AfterEach
    public void tearDown() {
        // Počisti vse TaskType iz baze po vsakem testu
        taskTypeRepository.delete(testTaskType);
    }

    @Test
    public void testCreateTaskType() {
        TaskType newTaskType = new TaskType();
        newTaskType.setType("New Task Type");

        // Pošlji zahtevo za ustvarjanje novega TaskType
        ResponseEntity<TaskType> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/task-types",
                newTaskType,
                TaskType.class
        );

        // Preveri odgovor
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getType()).isEqualTo("New Task Type");

        // Preveri, če je bil shranjen v bazo
        Optional<TaskType> savedTaskType = taskTypeRepository.findById(response.getBody().getId());
        assertThat(savedTaskType).isPresent();
    }

    @Test
    public void testDeleteTaskType() {
        // Pošlji zahtevo za brisanje TaskType
        restTemplate.delete("http://localhost:" + port + "/task-types/" + testTaskType.getId());

        // Preveri, ali je bil TaskType odstranjen iz baze
        Optional<TaskType> deletedTaskType = taskTypeRepository.findById(testTaskType.getId());
        assertThat(deletedTaskType).isNotPresent();
    }


}
