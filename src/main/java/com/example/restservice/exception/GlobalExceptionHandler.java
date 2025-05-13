package com.example.restservice.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_KEY = "error";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        logger.warn("⚠️ Ошибка валидации: {}", errors);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String rawPath = violation.getPropertyPath().toString();
            String field = rawPath.replaceAll("^.*arg0\\[(\\d+)]\\.", "$1.");
            String message = violation.getMessage();
            errors.put(field, message);
        }

        logger.warn("⚠️ ConstraintViolationException: {}", errors);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidProductException.class)
    public ResponseEntity<Map<String, String>> handleInvalidProductException(
            InvalidProductException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, ex.getMessage());

        logger.warn("⚠️ InvalidProductException: {}", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<Map<String, String>> handleInvalidOrderException(
            InvalidOrderException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, ex.getMessage());

        logger.warn("⚠️ InvalidOrderException: {}", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllOtherExceptions(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, ex.getMessage());

        logger.error("❌ Неперехваченное исключение: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
