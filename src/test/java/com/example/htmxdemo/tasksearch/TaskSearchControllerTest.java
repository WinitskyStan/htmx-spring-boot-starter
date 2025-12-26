package com.example.htmxdemo.tasksearch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskSearchControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskSearchService service;

    @Test
    void testIndexReturnsFullPage() {
        ResponseEntity<String> response = restTemplate.getForEntity("/tasksearch", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("<!DOCTYPE html>"));
        assertTrue(response.getBody().contains("Task Search"));
        assertTrue(response.getBody().contains("id=\"task-dropdown\""));
        assertTrue(response.getBody().contains("id=\"task-detail\""));
    }

    @Test
    void testSearchWithEmptyQueryShowsAllTasks() {
        ResponseEntity<String> response = restTemplate.postForEntity("/tasksearch/search", "", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Should contain fragment, not full page
        assertTrue(!response.getBody().contains("<!DOCTYPE html>"));
        assertTrue(response.getBody().contains("id=\"task-dropdown\""));
        // Empty query should show all tasks
        assertTrue(response.getBody().contains("Setup development environment"));
        assertTrue(response.getBody().contains("Create user authentication"));
    }

    @Test
    void testSearchFiltersTasksByName() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/tasksearch/search?query=authentication",
                "",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id=\"task-dropdown\""));
        assertTrue(response.getBody().contains("Create user authentication"));
        assertTrue(!response.getBody().contains("Setup development environment"));
    }

    @Test
    void testSearchIsCaseInsensitive() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/tasksearch/search?query=AUTHENTICATION",
                "",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Create user authentication"));
    }

    @Test
    void testSearchWithNoResultsShowsEmptyMessage() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/tasksearch/search?query=nonexistent",
                "",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("No tasks found matching your search"));
    }

    @Test
    void testTaskDetailReturnsFragment() {
        ResponseEntity<String> response = restTemplate.getForEntity("/tasksearch/1", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Should contain fragment, not full page
        assertTrue(!response.getBody().contains("<!DOCTYPE html>"));
        assertTrue(response.getBody().contains("Setup development environment"));
        assertTrue(response.getBody().contains("Install necessary tools"));
    }

    @Test
    void testServiceLoadsAllTasks() {
        assertEquals(25, service.getAllTasks().size());
    }

    @Test
    void testServiceSearchFilters() {
        var results = service.search("database");
        assertEquals(2, results.size());
    }

    @Test
    void testServiceGetTaskById() {
        var task = service.getTaskById(1L);
        assertTrue(task.isPresent());
        assertEquals("Setup development environment", task.get().name());
    }
}
