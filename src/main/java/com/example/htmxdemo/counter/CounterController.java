package com.example.htmxdemo.counter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CounterController {

    private final CounterService counterService;

    public CounterController(CounterService counterService) {
        this.counterService = counterService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("count", counterService.getCount());
        return "counter/counter";
    }

    @PostMapping("/counter/increment")
    public String increment(Model model) {
        model.addAttribute("count", counterService.increment());
        return "counter/counter :: count-display";
    }
}
