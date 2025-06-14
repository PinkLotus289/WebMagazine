package com.example.restservice.controller;

import com.example.restservice.service.VisitCounterService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VisitCounterController {

    private final VisitCounterService visitCounterService;

    public VisitCounterController(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @GetMapping("/visits/products")
    public Map<String, Integer> getProductVisitCount() {
        return Map.of("count", visitCounterService.getValue());
    }
}
