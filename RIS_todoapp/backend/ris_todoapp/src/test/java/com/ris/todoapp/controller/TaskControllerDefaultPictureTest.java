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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerDefaultPictureTest {

    @Mock TaskRepository taskRepository;
    @Mock UserRepository userRepository;
    @Mock TaskTypeRepository taskTypeRepository;
    @Mock GeocodingService geocodingService;

    // Mockito bo poklical konstruktor TaskController(taskRepository, userRepository, taskTypeRepository, geocodingService)
    @InjectMocks TaskController taskController;

    User user;
    TaskType taskType;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        taskType = new TaskType();
        taskType.setId(1L);

        // stubi odvisnosti
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskTypeRepository.findById(1L)).thenReturn(Optional.of(taskType));

        // če controller geocodira, mu vrnemo veljavne koordinate (ali Optional.empty() če tega ne rabiš)
        when(geocodingService.geocode(anyString()))
                .thenReturn(Optional.of(new GeocodingResult(46.05, 14.51)));
    }

    @Test
    void createTask_noPicture_setsDefaultPicture_andReturns201() {
        Task t = new Task();
        t.setTaskName("Nakup hrane");
        t.setDescription("Mleko, kruh");
        t.setTaskType(taskType);
        t.setDueDateTime(LocalDateTime.now().plusDays(1));
        t.setLocationAddress("Trg republike 3, Ljubljana");

        // pri save vrnemo entiteto z ID-jem, da bo ResponseEntity.created(...) OK
        Task saved = new Task();
        saved.setId(123L);
        saved.setTaskName(t.getTaskName());
        saved.setDescription(t.getDescription());
        saved.setTaskType(taskType);
        saved.setUser(user);
        saved.setLatitude(46.05);
        saved.setLongitude(14.51);
        saved.setPicture("/uploads/default.jpg");  // pričakovana default vrednost

        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        ResponseEntity<?> res = taskController.createTask(t, 1L);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        Task body = (Task) res.getBody();
        assertNotNull(body);
        assertEquals("/uploads/default.jpg", body.getPicture(), "Ko slika ni podana, mora biti nastavljena default slika.");
        assertEquals(46.05, body.getLatitude());
        assertEquals(14.51, body.getLongitude());
    }
}