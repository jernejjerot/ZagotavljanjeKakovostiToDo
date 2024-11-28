package com.ris.todoapp.repository;

import com.ris.todoapp.entity.TaskType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TaskTypeRepositoryTest {

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    @Test
    public void testFindAll() {
        List<TaskType> taskTypes = taskTypeRepository.findAll();
        System.out.println("Task Types: " + taskTypes);
    }
}
