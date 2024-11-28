package com.ris.todoapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "taskType")
public class TaskType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(nullable = false)
    private String type;

    // No-args constructor (required by JPA)
    public TaskType() {}

    // Constructor for convenience
    public TaskType(Long id, String type) {
        this.id = id;
        this.type = type;
    }

    // Getters and setters
    public Long getId() { 
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
