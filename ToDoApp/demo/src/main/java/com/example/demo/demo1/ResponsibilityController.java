package com.example.demo.demo1;

import com.example.demo.demo1.repository.ResponsibilityRepository;
import com.example.demo.entity.Responsibility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/responsibilities")
public class ResponsibilityController {

    @Autowired
    private ResponsibilityRepository responsibilityRepository;

    // Create a new responsibility
    @PostMapping
    public Responsibility createResponsibility(@RequestBody Responsibility responsibility) {
        return responsibilityRepository.save(responsibility);
    }

    // Get all responsibilities
    @GetMapping
    public List<Responsibility> getAllResponsibilities() {
        return responsibilityRepository.findAll();
    }

    // Get a specific responsibility by ID
    @GetMapping("/{id}")
    public Optional<Responsibility> getResponsibilityById(@PathVariable Long id) {
        return responsibilityRepository.findById(id);
    }

    // Update a responsibility by ID
    @PutMapping("/{id}")
    public Responsibility updateResponsibility(@PathVariable Long id, @RequestBody Responsibility responsibilityDetails) {
        Responsibility responsibility = responsibilityRepository.findById(id).orElseThrow();
        responsibility.setType(responsibilityDetails.getType());
        return responsibilityRepository.save(responsibility);
    }

    // Delete a responsibility by ID
    @DeleteMapping("/{id}")
    public void deleteResponsibility(@PathVariable Long id) {
        responsibilityRepository.deleteById(id);
    }
}