package com.rest.user_service.exception;

public class CustomResourceNotFoundException extends RuntimeException {
    String entity;
    String property;
    String value;

    public CustomResourceNotFoundException(String entity, String property, String value) {
        super(entity + " with " + property + " = " + value + " not found");
    }
}
