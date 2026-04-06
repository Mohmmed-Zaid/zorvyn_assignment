package com.finance.demo.exception;

import com.finance.demo.dtos.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse.ErrorResponse> handleNotFound(AppException.ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), null);
    }

    @ExceptionHandler(AppException.DuplicateResourceException.class)
    public ResponseEntity<ApiResponse.ErrorResponse> handleDuplicate(AppException.DuplicateResourceException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), null);
    }

    @ExceptionHandler({AppException.AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ApiResponse.ErrorResponse> handleAccessDenied(RuntimeException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden",
                "You do not have permission to perform this action", null);
    }

    @ExceptionHandler(AppException.InvalidOperationException.class)
    public ResponseEntity<ApiResponse.ErrorResponse> handleInvalidOperation(AppException.InvalidOperationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        // Deliberately vague — don't reveal whether email or password was wrong
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid email or password", null);
    }

    /**
     * Bean Validation failures — returns field-level errors so the frontend
     * can highlight individual form fields.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Failed",
                "Request contains invalid fields", fieldErrors);
    }

    /**
     * Catch-all for unexpected errors.
     * Logs the full stack trace server-side but returns a generic message to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred. Please try again.", null);
    }

    // ---- Helper ----

    private ResponseEntity<Object> buildResponse(
            HttpStatus status, String error, String message, Map<String, String> fieldErrors) {
        ApiResponse.ErrorResponse body = ApiResponse.ErrorResponse.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}