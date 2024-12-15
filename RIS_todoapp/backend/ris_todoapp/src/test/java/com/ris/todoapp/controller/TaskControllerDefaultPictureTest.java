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
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
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

        // Privzeti podatki za mockanje
        User user = new User();
        user.setId(1L);

        TaskType taskType = new TaskType();
        taskType.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskTypeRepository.findById(1L)).thenReturn(Optional.of(taskType));

        // Pravilna inicializacija GeocodingResult
        com.ris.todoapp.dto.GeocodingResult geocodingResult = new com.ris.todoapp.dto.GeocodingResult();
        geocodingResult.setLatitude(40.7128);
        geocodingResult.setLongitude(-74.0060);

        // Mockiranje geokodiranja
        when(geocodingService.geocode(anyString())).thenReturn(Optional.of(geocodingResult));
    }

    @Test
    void createTask_NoPicture_DefaultPictureSet() {
        // Priprava podatkov
        Task task = new Task();
        task.setTaskName("Testna naloga");
        task.setLocationAddress("New York");
        task.setTaskType(new TaskType());
        task.getTaskType().setId(1L);
        task.setPicture(null); // Ni slike

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
        Task task = new Task();
        task.setTaskName("Testna naloga");
        task.setLocationAddress("New York");
        task.setTaskType(new TaskType());
        task.getTaskType().setId(1L);
        task.setPicture(""); // Prazna vrednost za sliko

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
        Task task = new Task();
        task.setTaskName("Testna naloga");
        task.setLocationAddress("New York");
        task.setTaskType(new TaskType());
        task.getTaskType().setId(1L);
        task.setPicture("/uploads/custom.jpg"); // Določena slika

        // Izvedba metode kontrolerja
        ResponseEntity<?> response = taskController.createTask(task, 1L);

        // Preverjanje rezultatov
        assertEquals(200, response.getStatusCodeValue(), "Pričakovana je statusna koda 200.");
        verify(taskRepository, times(1)).save(argThat(savedTask ->
                "/uploads/custom.jpg".equals(savedTask.getPicture())
        ));
    }
}