package com.example.restservice.service;

import org.springframework.stereotype.Service;

@Service
public class VisitCounterService {

    private int count = 0;

    public synchronized void increment() {
        count++;
    }

    public synchronized int getValue() {
        return count;
    }
}

