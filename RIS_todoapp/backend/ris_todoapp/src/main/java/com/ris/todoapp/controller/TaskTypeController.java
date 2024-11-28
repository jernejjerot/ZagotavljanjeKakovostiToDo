package com.ris.todoapp.controller;

import com.ris.todoapp.entity.TaskType;
import com.ris.todoapp.repository.TaskTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-types")
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from the frontend
public class TaskTypeController {

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    // Create a new task type
    @PostMapping
    public TaskType createTaskType(@RequestBody TaskType taskType) {
        return taskTypeRepository.save(taskType);
    }

    // Get all task types
    @GetMapping
public List<TaskType> getAllTaskTypes() {
    List<TaskType> taskTypes = taskTypeRepository.findAll();
    System.out.println("Task Types Retrieved: " + taskTypes); // Debugging
    return taskTypes;
}

    // Delete a task type
    @DeleteMapping("/{id}")
    public String deleteTaskType(@PathVariable Long id) {
        taskTypeRepository.deleteById(id);
        return "Task type deleted successfully.";
    }
}
