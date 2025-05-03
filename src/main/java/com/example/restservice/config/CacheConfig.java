package com.example.restservice.config;

import com.example.restservice.model.Order;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public Map<String, List<Order>> ordersByProductNameCache() {
        return new ConcurrentHashMap<>();
    }
}


