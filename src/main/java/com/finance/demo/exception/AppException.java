package com.finance.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AppException {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {

        public ResourceNotFoundException(String message) {
            super(message);
        }

        public static ResourceNotFoundException forUser(Long id) {
            return new ResourceNotFoundException("User not found with id: " + id);
        }

        public static ResourceNotFoundException forRecord(Long id) {
            return new ResourceNotFoundException("Financial record not found with id: " + id);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class DuplicateResourceException extends RuntimeException {

        public DuplicateResourceException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class AccessDeniedException extends RuntimeException {

        public AccessDeniedException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidOperationException extends RuntimeException {

        public InvalidOperationException(String message) {
            super(message);
        }
    }
}
