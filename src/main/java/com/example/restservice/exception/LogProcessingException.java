package com.example.restservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogProcessingException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(LogProcessingException.class);

    public LogProcessingException(String message, Throwable cause) {
        super(message, cause);
        logger.error("❌ Логика выбросила исключение: {}", message, cause);
    }
}

