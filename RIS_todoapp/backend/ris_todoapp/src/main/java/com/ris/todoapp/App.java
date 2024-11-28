package com.ris.todoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.ris.todoapp.repository") // Adjust if your repository package is different
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
