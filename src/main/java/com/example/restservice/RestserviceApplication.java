package com.example.restservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RestserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestserviceApplication.class, args);
    }
}
