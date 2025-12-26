package com.example.htmxdemo.tasksearch;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/tasksearch")
public class TaskSearchController {
    private final TaskSearchService service;

    public TaskSearchController(TaskSearchService service) {
        this.service = service;
    }

    @GetMapping
    public String index(Model model) {
        return "tasksearch/tasksearch";
    }

    @PostMapping("/search")
    public String search(@RequestParam(defaultValue = "") String query, Model model) {
        model.addAttribute("query", query);
        model.addAttribute("tasks", service.search(query));
        return "tasksearch/tasksearch :: task-dropdown";
    }

    @GetMapping("/{id}")
    public String taskDetail(@PathVariable Long id, Model model) {
        service.getTaskById(id).ifPresent(task -> model.addAttribute("task", task));
        return "tasksearch/tasksearch :: task-detail";
    }
}
