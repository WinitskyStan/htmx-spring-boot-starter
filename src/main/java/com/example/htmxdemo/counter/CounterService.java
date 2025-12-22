package com.example.htmxdemo.counter;

import org.springframework.stereotype.Service;

@Service
public class CounterService {

    private int count = 0;

    public int getCount() {
        return count;
    }

    public int increment() {
        return ++count;
    }
}
