package com.example.demo.entity;


import com.example.demo.demo1.enums.TaskStatus;
import com.example.demo.demo1.enums.PriorityLevel;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.example.demo.demo1.enums.TaskStatus;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "responsibility_id", nullable = false)
    private Responsibility responsibility;

    private String taskName;
    private String description;
    private LocalDate dueDate;
    private Boolean isCompleted;

    public Task() {
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Responsibility getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(Responsibility responsibility) {
        this.responsibility = responsibility;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public void setReminder(LocalDate date) {
        System.out.println("Reminder set for: " + date);
    }

    public void updateStatus(TaskStatus status) {
        this.isCompleted = (status == TaskStatus.COMPLETED);
        System.out.println("Task status updated to: " + status);
    }

}