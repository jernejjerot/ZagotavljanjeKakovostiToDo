package com.ris.todoapp.controller;

import com.ris.todoapp.entity.Task;
import com.ris.todoapp.entity.User;
import com.ris.todoapp.repository.TaskRepository;
import com.ris.todoapp.repository.UserRepository;
import com.ris.todoapp.service.GeocodingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private GeocodingService geocodingService;

    private TaskController taskController;

    @BeforeEach
    void setUp() {
        // Mockiranje odvisnosti
        taskRepository = mock(TaskRepository.class);
        userRepository = mock(UserRepository.class);
        geocodingService = mock(GeocodingService.class);

        // Inicializacija kontrolerja
        taskController = new TaskController();
        taskController.setTaskRepository(taskRepository);
        taskController.setUserRepository(userRepository);
        taskController.setGeocodingService(geocodingService);

        // Ustvarjanje privzetega uporabnika in naloge
        User user = new User();
        user.setId(1L);

        Task task = new Task();
        task.setId(1L);
        task.setUser(user);

        // Mockiranje vedenja odvisnosti
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
    }

    @Test
    void uploadTaskPicture_ValidPicture_Success() throws Exception {
        // Priprava podatkov
        MockMultipartFile picture = new MockMultipartFile("picture", "test.jpg", "image/jpeg", "sample image".getBytes());

        // Izvedba metode kontrolerja
        ResponseEntity<?> response = taskController.uploadTaskPicture(1L, 1L, picture);

        // Preverjanje rezultatov
        assertEquals(200, response.getStatusCodeValue(), "Pričakovana je statusna koda 200.");
        assertEquals("Picture uploaded successfully.", response.getBody(), "Pričakovano sporočilo o uspešnem nalaganju slike.");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void uploadTaskPicture_FileEmpty_Failure() {
        // Priprava podatkov
        MockMultipartFile emptyFile = new MockMultipartFile("picture", "", "image/jpeg", new byte[0]);

        // Izvedba metode kontrolerja
        ResponseEntity<?> response = taskController.uploadTaskPicture(1L, 1L, emptyFile);

        // Preverjanje rezultatov
        assertEquals(400, response.getStatusCodeValue(), "Pričakovana je statusna koda 400 za prazen file.");
        assertEquals("Uploaded file is empty.", response.getBody(), "Pričakovano sporočilo o praznem file-u.");
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void uploadTaskPicture_UnauthorizedUser_Failure() {
        // Priprava podatkov
        User unauthorizedUser = new User();
        unauthorizedUser.setId(2L);

        Task task = new Task();
        task.setId(1L);
        task.setUser(unauthorizedUser);

        MockMultipartFile picture = new MockMultipartFile("picture", "test.jpg", "image/jpeg", "sample image".getBytes());

        // Mockiranje vedenja odvisnosti
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Izvedba metode kontrolerja
        ResponseEntity<?> response = taskController.uploadTaskPicture(1L, 1L, picture);

        // Preverjanje rezultatov
        assertEquals(403, response.getStatusCodeValue(), "Pričakovana je statusna koda 403 za nepooblaščenega uporabnika.");
        assertEquals("Unauthorized to upload picture for this task.", response.getBody(), "Pričakovano sporočilo o nepooblaščenem dostopu.");
        verify(taskRepository, never()).save(any(Task.class));
    }
}