package com.example.htmxdemo.tasksearch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class TaskSearchService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Task> tasks;

    @jakarta.annotation.PostConstruct
    public void loadTasks() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/tasks.json");
            tasks = objectMapper.readValue(inputStream, new TypeReference<List<Task>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load tasks from JSON", e);
        }
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    public List<Task> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllTasks();
        }

        String lowerQuery = query.toLowerCase();
        return tasks.stream()
                .filter(task -> task.name().toLowerCase().contains(lowerQuery))
                .toList();
    }

    public Optional<Task> getTaskById(Long id) {
        return tasks.stream()
                .filter(task -> task.id().equals(id))
                .findFirst();
    }
}
