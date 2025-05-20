package com.example.restservice.exception;

public class ProductInOrderException extends RuntimeException {
    public ProductInOrderException(String message) {
        super(message);
    }
}

