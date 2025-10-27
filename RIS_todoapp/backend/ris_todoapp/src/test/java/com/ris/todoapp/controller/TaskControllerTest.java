package com.ris.todoapp.controller;

import com.ris.todoapp.entity.Task;
import com.ris.todoapp.entity.User;
import com.ris.todoapp.repository.TaskRepository;
import com.ris.todoapp.repository.TaskTypeRepository;
import com.ris.todoapp.repository.UserRepository;
import com.ris.todoapp.service.GeocodingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;
    @Mock private TaskTypeRepository taskTypeRepository;   // zahteva ga konstruktor
    @Mock private GeocodingService geocodingService;

    // Mockito bo uporabil konstruktor TaskController(TaskRepository, UserRepository, TaskTypeRepository, GeocodingService)
    @InjectMocks
    private TaskController taskController;

    private User user;
    private Task existingTask;

    @BeforeEach
    void setUp() {
        // Ustvarimo testnega userja in nalogo
        user = new User();
        user.setId(1L);

        existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setUser(user);

        // Privzeti stubi
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        // če metoda med shranjevanjem kliče save, vrnemo objekt z ID-jem
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void uploadTaskPicture_ValidPicture_Success() throws Exception {
        // Priprava podatkov
        MockMultipartFile picture =
                new MockMultipartFile("picture", "test.jpg", "image/jpeg", "sample image".getBytes());

        // Klic metode
        ResponseEntity<?> response = taskController.uploadTaskPicture(1L, 1L, picture);

        // Preverjanje
        assertEquals(200, response.getStatusCodeValue(), "Pričakovana je statusna koda 200.");
        assertEquals("Picture uploaded successfully.", response.getBody(),
                "Pričakovano sporočilo o uspešnem nalaganju slike.");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void uploadTaskPicture_FileEmpty_Failure() {
        MockMultipartFile emptyFile =
                new MockMultipartFile("picture", "", "image/jpeg", new byte[0]);

        ResponseEntity<?> response = taskController.uploadTaskPicture(1L, 1L, emptyFile);

        assertEquals(400, response.getStatusCodeValue(), "Pričakovana je statusna koda 400 za prazen file.");
        assertEquals("Uploaded file is empty.", response.getBody(),
                "Pričakovano sporočilo o praznem file-u.");
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void uploadTaskPicture_UnauthorizedUser_Failure() {
        // naloga pripada drugemu userju
        User unauthorizedUser = new User();
        unauthorizedUser.setId(2L);

        Task taskOwnedByOther = new Task();
        taskOwnedByOther.setId(1L);
        taskOwnedByOther.setUser(unauthorizedUser);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskOwnedByOther));

        MockMultipartFile picture =
                new MockMultipartFile("picture", "test.jpg", "image/jpeg", "sample image".getBytes());

        ResponseEntity<?> response = taskController.uploadTaskPicture(1L, 1L, picture);

        assertEquals(403, response.getStatusCodeValue(),
                "Pričakovana je statusna koda 403 za nepooblaščenega uporabnika.");
        assertEquals("Unauthorized to upload picture for this task.", response.getBody(),
                "Pričakovano sporočilo o nepooblaščenem dostopu.");
        verify(taskRepository, never()).save(any(Task.class));
    }
}