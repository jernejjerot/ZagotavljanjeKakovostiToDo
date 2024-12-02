package com.ris.todoapp.controller;

import com.ris.todoapp.dto.GeocodingResult;
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

class TaskControllerTest {

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
    void createTask_ValidTask_Success() {
        // Priprava podatkov
        User user = new User();
        user.setId(1L);

        TaskType taskType = new TaskType();
        taskType.setId(1L);

        Task task = new Task();
        task.setTaskName("Testna naloga");
        task.setLocationAddress("New York");
        task.setTaskType(taskType);

        // Uporaba `set` metod za `GeocodingResult`
        GeocodingResult geocodingResult = new GeocodingResult();
        geocodingResult.setLatitude(40.7128);
        geocodingResult.setLongitude(-74.0060);

        // Mockiranje vedenja odvisnosti
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskTypeRepository.findById(1L)).thenReturn(Optional.of(taskType));
        when(geocodingService.geocode("New York")).thenReturn(Optional.of(geocodingResult));

        // Izvedba metode kontrolerja
        ResponseEntity<?> response = taskController.createTask(task, 1L);

        // Preverjanje rezultatov
        assertEquals(200, response.getStatusCodeValue(), "Pričakovana je statusna koda 200.");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_InvalidUser_Failure() {
        // Priprava podatkov
        Task task = new Task();
        task.setTaskName("Testna naloga");

        // Mockiranje vedenja odvisnosti
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Izvedba metode kontrolerja
        ResponseEntity<?> response = taskController.createTask(task, 1L);

        // Preverjanje rezultatov
        assertEquals(400, response.getStatusCodeValue(), "Pričakovana je statusna koda 400 za neveljaven uporabnik.");
        assertEquals("Invalid user ID.", response.getBody(), "Pričakovana je napaka 'Invalid user ID.'");
        verify(taskRepository, never()).save(any(Task.class));
    }
}