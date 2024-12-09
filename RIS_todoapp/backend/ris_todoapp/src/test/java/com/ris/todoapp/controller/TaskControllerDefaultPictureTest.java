package com.ris.todoapp.controller;

import com.ris.todoapp.entity.Task;
import com.ris.todoapp.entity.TaskType;
import com.ris.todoapp.entity.User;
import com.ris.todoapp.repository.TaskRepository;
import com.ris.todoapp.repository.TaskTypeRepository;
import com.ris.todoapp.repository.UserRepository;
import com.ris.todoapp.service.GeocodingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskControllerDefaultPictureTest {

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private TaskTypeRepository taskTypeRepository;
    private GeocodingService geocodingService;

    private TaskController taskController;

    @BeforeEach
    void setUp() {
        // Mockiranje odvisnosti
        taskRepository = mock(TaskRepository.class);
        userRepository = mock(UserRepository.class);
        taskTypeRepository = mock(TaskTypeRepository.class);
        geocodingService = mock(GeocodingService.class);

        // Inicializacija kontrolerja
        taskController = new TaskController();
        taskController.setTaskRepository(taskRepository);
        taskController.setUserRepository(userRepository);
        taskController.setTaskTypeRepository(taskTypeRepository);
        taskController.setGeocodingService(geocodingService);
    }

    @Test
    void createTask_NoPicture_DefaultPictureSet() {
        // Priprava podatkov
        User user = new User();
        user.setId(1L);

        TaskType taskType = new TaskType();
        taskType.setId(1L);

        Task task = new Task();
        task.setTaskName("Testna naloga");
        task.setLocationAddress("New York");
        task.setTaskType(taskType);
        task.setPicture(null); // Ni slike

        // Mockiranje vedenja odvisnosti
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskTypeRepository.findById(1L)).thenReturn(Optional.of(taskType));

        // Izvedba metode kontrolerja
        ResponseEntity<?> response = taskController.createTask(task, 1L);

        // Preverjanje rezultatov
        assertEquals(200, response.getStatusCodeValue(), "Pričakovana je statusna koda 200.");
        verify(taskRepository, times(1)).save(argThat(savedTask ->
                "/uploads/default.jpg".equals(savedTask.getPicture())
        ));
    }

    @Test
    void createTask_EmptyPicture_DefaultPictureSet() {
        // Priprava podatkov
        User user = new User();
        user.setId(1L);

        TaskType taskType = new TaskType();
        taskType.setId(1L);

        Task task = new Task();
        task.setTaskName("Testna naloga");
        task.setLocationAddress("New York");
        task.setTaskType(taskType);
        task.setPicture(""); // Prazna vrednost za sliko

        // Mockiranje vedenja odvisnosti
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskTypeRepository.findById(1L)).thenReturn(Optional.of(taskType));

        // Izvedba metode kontrolerja
        ResponseEntity<?> response = taskController.createTask(task, 1L);

        // Preverjanje rezultatov
        assertEquals(200, response.getStatusCodeValue(), "Pričakovana je statusna koda 200.");
        verify(taskRepository, times(1)).save(argThat(savedTask ->
                "/uploads/default.jpg".equals(savedTask.getPicture())
        ));
    }

    @Test
    void createTask_WithPicture_CustomPictureSet() {
        // Priprava podatkov
        User user = new User();
        user.setId(1L);

        TaskType taskType = new TaskType();
        taskType.setId(1L);

        Task task = new Task();
        task.setTaskName("Testna naloga");
        task.setLocationAddress("New York");
        task.setTaskType(taskType);
        task.setPicture("/uploads/custom.jpg"); // Določena slika

        // Mockiranje vedenja odvisnosti
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskTypeRepository.findById(1L)).thenReturn(Optional.of(taskType));

        // Izvedba metode kontrolerja
        ResponseEntity<?> response = taskController.createTask(task, 1L);

        // Preverjanje rezultatov
        assertEquals(200, response.getStatusCodeValue(), "Pričakovana je statusna koda 200.");
        verify(taskRepository, times(1)).save(argThat(savedTask ->
                "/uploads/custom.jpg".equals(savedTask.getPicture())
        ));
    }
}
